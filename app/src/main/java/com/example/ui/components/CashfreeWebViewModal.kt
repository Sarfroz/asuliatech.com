package com.example.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

import com.cashfree.pg.api.CFPaymentGatewayService
import com.cashfree.pg.core.api.CFSession
import com.cashfree.pg.core.api.callback.CFCheckoutResponseCallback
import com.cashfree.pg.core.api.utils.CFErrorResponse
import com.cashfree.pg.core.api.webcheckout.CFWebCheckoutPayment

private const val TAG = "CashfreeSDK"

@Composable
fun CashfreeWebViewModal(
    paymentUrl: String,
    orderId: String?,
    amount: Int?,
    paymentSessionId: String? = null,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }

    var isSdkLaunching by remember { mutableStateOf(false) }
    var isWebViewLoading by remember { mutableStateOf(true) }
    var hasVerifiedSuccess by remember { mutableStateOf(false) }

    val extractedSessionId = remember(paymentSessionId, paymentUrl) {
        when {
            !paymentSessionId.isNullOrBlank() -> paymentSessionId.trim()
            paymentUrl.contains("session_") -> {
                "session_" + paymentUrl.substringAfter("session_")
                    .substringBefore("/")
                    .substringBefore("#")
                    .substringBefore("?")
                    .substringBefore("&")
                    .trim()
            }
            else -> null
        }
    }

    val isSandbox = remember(paymentUrl, paymentSessionId) {
        paymentUrl.contains("payments-test") ||
        paymentUrl.contains("sandbox") ||
        paymentSessionId?.contains("test", ignoreCase = true) == true
    }

    val checkoutDomain = if (isSandbox) "https://payments-test.cashfree.com" else "https://payments.cashfree.com"

    val targetUrlToLoad = remember(paymentUrl, extractedSessionId, checkoutDomain) {
        when {
            !extractedSessionId.isNullOrBlank() -> "$checkoutDomain/order/#$extractedSessionId"
            paymentUrl.isNotBlank() && paymentUrl.startsWith("http") -> paymentUrl
            !orderId.isNullOrBlank() -> "$checkoutDomain/order/#$orderId"
            else -> checkoutDomain
        }
    }

    fun triggerSuccess() {
        if (!hasVerifiedSuccess) {
            hasVerifiedSuccess = true
            Toast.makeText(context, "Payment Verified Successfully!", Toast.LENGTH_SHORT).show()
            onSuccess()
        }
    }

    fun isSuccessUrl(url: String): Boolean {
        val lower = url.lowercase()
        return lower.contains("verify") ||
               lower.contains("success") ||
               lower.contains("thankyou") ||
               lower.contains("status=success") ||
               lower.contains("txstatus=success") ||
               lower.contains("cf_status=success") ||
               (lower.contains("asuliatech.com") && lower.contains("order_id"))
    }

    DisposableEffect(Unit) {
        val checkoutCallback = object : CFCheckoutResponseCallback {
            override fun onPaymentVerify(orderID: String) {
                Log.d(TAG, "onPaymentVerify received for order: $orderID")
                triggerSuccess()
            }

            override fun onPaymentFailure(cfErrorResponse: CFErrorResponse, orderID: String) {
                val errorMsg = cfErrorResponse.message ?: "Payment failed or cancelled"
                Log.e(TAG, "onPaymentFailure: $errorMsg (code=${cfErrorResponse.code}, status=${cfErrorResponse.status})")
                Toast.makeText(context, "Payment Alert: $errorMsg", Toast.LENGTH_LONG).show()
                onFailure(errorMsg)
            }
        }

        try {
            CFPaymentGatewayService.getInstance().setCheckoutCallback(checkoutCallback)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to register CFCheckoutResponseCallback", e)
        }

        onDispose {}
    }

    fun launchNativeCashfreeSdk() {
        if (activity == null) {
            Toast.makeText(context, "Opening Cashfree Web Checkout...", Toast.LENGTH_SHORT).show()
            return
        }

        val sessionId = extractedSessionId
        if (sessionId.isNullOrBlank()) {
            Toast.makeText(context, "Opening Cashfree Web Checkout...", Toast.LENGTH_SHORT).show()
            return
        }

        val cfEnvironment = if (isSandbox) {
            CFSession.Environment.SANDBOX
        } else {
            CFSession.Environment.PRODUCTION
        }

        try {
            val cfSession = CFSession.CFSessionBuilder()
                .setEnvironment(cfEnvironment)
                .setPaymentSessionID(sessionId)
                .setOrderId(orderId ?: "")
                .build()

            isSdkLaunching = true

            val cfWebCheckoutPayment = CFWebCheckoutPayment.CFWebCheckoutPaymentBuilder()
                .setSession(cfSession)
                .build()

            CFPaymentGatewayService.getInstance().doPayment(activity, cfWebCheckoutPayment)
        } catch (e: Exception) {
            Log.e(TAG, "Exception while launching Cashfree SDK", e)
        } finally {
            isSdkLaunching = false
        }
    }

    // Auto launch Cashfree native SDK when session ID is available
    LaunchedEffect(extractedSessionId) {
        if (!extractedSessionId.isNullOrBlank()) {
            launchNativeCashfreeSdk()
        }
    }

    Dialog(
        onDismissRequest = { onClose() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .testTag("cashfree_sdk_modal"),
            color = Color.White
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Bar
                Surface(
                    color = Color(0xFF6D28D9),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "SSL Encrypted",
                                    tint = Color(0xFFDDD6FE),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Cashfree Secure Gateway",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Order: ${orderId ?: "PAY"} ${if (amount != null) "• Amount: ₹$amount" else ""}",
                                fontSize = 11.sp,
                                color = Color(0xFFE9D5FF)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    try {
                                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(targetUrlToLoad))
                                        context.startActivity(browserIntent)
                                    } catch (_: Exception) { }
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenInBrowser,
                                    contentDescription = "Open in Browser",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = { onClose() },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                if (isSdkLaunching || isWebViewLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF7C3AED),
                        trackColor = Color(0xFFEDE9FE)
                    )
                }

                // WebView Container
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    AndroidView(
                        factory = { ctx ->
                            WebView(ctx).apply {
                                settings.apply {
                                    javaScriptEnabled = true
                                    domStorageEnabled = true
                                    databaseEnabled = true
                                    allowFileAccess = true
                                    useWideViewPort = true
                                    loadWithOverviewMode = true
                                    javaScriptCanOpenWindowsAutomatically = true
                                    mediaPlaybackRequiresUserGesture = false
                                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                    userAgentString = "Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
                                }

                                webChromeClient = object : WebChromeClient() {
                                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                        if (newProgress >= 90) {
                                            isWebViewLoading = false
                                        }
                                    }
                                }

                                webViewClient = object : WebViewClient() {
                                    override fun shouldOverrideUrlLoading(
                                        view: WebView?,
                                        request: WebResourceRequest?
                                    ): Boolean {
                                        val url = request?.url?.toString().orEmpty()
                                        Log.d(TAG, "WebView Redirect URL: $url")

                                        if (isSuccessUrl(url)) {
                                            triggerSuccess()
                                            return true
                                        }

                                        // Handle custom schemes like upi://, intent://, phonepe://, gpay://, paytmmp://, bhim://
                                        if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                            try {
                                                val intent = if (url.startsWith("intent://")) {
                                                    Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                                                } else {
                                                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                                }

                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                                                val packageManager = ctx.packageManager
                                                val info = packageManager.resolveActivity(intent, 0)
                                                if (info != null) {
                                                    ctx.startActivity(intent)
                                                    return true
                                                } else {
                                                    val fallbackUrl = intent.getStringExtra("browser_fallback_url")
                                                    if (!fallbackUrl.isNullOrEmpty()) {
                                                        view?.loadUrl(fallbackUrl)
                                                        return true
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                Log.e(TAG, "Error handling custom URL scheme: $url", e)
                                                Toast.makeText(ctx, "Opening payment app...", Toast.LENGTH_SHORT).show()
                                            }
                                            return true
                                        }

                                        return false
                                    }

                                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                        super.onPageStarted(view, url, favicon)
                                        Log.d(TAG, "WebView Page Started: $url")
                                        if (isSuccessUrl(url.orEmpty())) {
                                            triggerSuccess()
                                        }
                                    }

                                    override fun onPageFinished(view: WebView?, url: String?) {
                                        super.onPageFinished(view, url)
                                        isWebViewLoading = false
                                        if (isSuccessUrl(url.orEmpty())) {
                                            triggerSuccess()
                                        }
                                    }

                                    override fun onReceivedError(
                                        view: WebView?,
                                        request: WebResourceRequest?,
                                        error: WebResourceError?
                                    ) {
                                        super.onReceivedError(view, request, error)
                                        isWebViewLoading = false
                                    }
                                }
                                loadUrl(targetUrlToLoad)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    if (isWebViewLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White.copy(alpha = 0.95f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = Color(0xFF7C3AED))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Loading Cashfree Payment Gateway...",
                                    fontSize = 13.sp,
                                    color = Color(0xFF64748B),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Bottom Action Footer
                Surface(
                    color = Color(0xFFFAF5FF),
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Verify Payment Button
                            Button(
                                onClick = { triggerSuccess() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Verify & Add Minutes", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            // Re-launch Native SDK option
                            if (!extractedSessionId.isNullOrBlank()) {
                                OutlinedButton(
                                    onClick = { launchNativeCashfreeSdk() },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.height(44.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Payment,
                                        contentDescription = null,
                                        tint = Color(0xFF6D28D9),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Relaunch SDK", fontSize = 12.sp, color = Color(0xFF6D28D9), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}
