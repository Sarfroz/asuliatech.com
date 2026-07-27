package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.bounceClick
import com.example.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

private val HeaderPurple = Color(0xFF6C26F2)
private val PurplePrimary = Color(0xFF7038E8)
private val LightPurpleBg = Color(0xFFF3EEFF)
private val PageBackground = Color(0xFFF8FAFC)
private val TextDark = Color(0xFF0F172A)
private val TextSubtle = Color(0xFF64748B)

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
            .testTag("login_screen")
    ) {
        if (uiState.otpSent) {
            // STEP 2: OTP Verification Screen
            OtpVerificationView(
                mobileNumber = uiState.mobileNumber,
                otpCode = uiState.otpCode,
                isLoading = uiState.isLoading,
                errorMessage = uiState.errorMessage,
                onOtpChanged = { authViewModel.onOtpCodeChanged(it) },
                onBackClicked = { authViewModel.resetOtp() },
                onResendOtp = { authViewModel.sendOtp { } },
                onVerifyOtp = { authViewModel.verifyOtpAndLogin(onLoginSuccess) }
            )
        } else {
            // STEP 1: Mobile Entry Screen (Matching uploaded mockup)
            MobileEntryView(
                mobileNumber = uiState.mobileNumber,
                isLoading = uiState.isLoading,
                errorMessage = uiState.errorMessage,
                onMobileChanged = { authViewModel.onMobileChanged(it) },
                onSendOtp = { authViewModel.sendOtp { } }
            )
        }
    }
}

/**
 * Top AsuliaTech Brand Banner matching the uploaded mockup image.
 */
@Composable
private fun AsuliaTechHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(HeaderPurple)
            .statusBarsPadding()
            .padding(vertical = 20.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // "AT" Badge Box
            Box(
                modifier = Modifier
                    .size(width = 46.dp, height = 46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.22f))
                    .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AT",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Thin vertical divider line
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(32.dp)
                    .background(Color.White.copy(alpha = 0.35f))
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Brand Text & Subtitle
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "ASULIA",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "TECH",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.85f),
                        letterSpacing = 0.5.sp
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "— SMART COMMUNICATION SOLUTIONS —",
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.85f),
                    letterSpacing = 0.6.sp
                )
            }
        }
    }
}

/**
 * Step 1: Mobile Entry View matching the user mockup.
 */
@Composable
private fun MobileEntryView(
    mobileNumber: String,
    isLoading: Boolean,
    errorMessage: String?,
    onMobileChanged: (String) -> Unit,
    onSendOtp: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Purple Header Bar
        AsuliaTechHeader()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 2. Family Illustration Graphic
            Image(
                painter = painterResource(id = R.drawable.ic_family_illustration),
                contentDescription = "Parent and Students Illustration",
                contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(200.dp)
                    .padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Floating White Welcome Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Welcome!",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Enter your registered mobile number to continue",
                        fontSize = 13.sp,
                        color = TextSubtle,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Mobile Number Label
                    Text(
                        text = "Mobile Number",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Mobile Field Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF8FAFC))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "IN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSubtle
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "+91",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.width(12.dp))

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(22.dp)
                                .background(Color(0xFFCBD5E1))
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        BasicTextField(
                            value = mobileNumber,
                            onValueChange = {
                                if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                                    onMobileChanged(it)
                                }
                            },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark,
                                letterSpacing = 1.sp
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("mobile_input_field"),
                            decorationBox = { innerTextField ->
                                Box(contentAlignment = Alignment.CenterStart) {
                                    if (mobileNumber.isEmpty()) {
                                        Text(
                                            text = "Enter 10-digit number",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF94A3B8)
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )
                    }

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Send OTP Button
                    Button(
                        onClick = onSendOtp,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                        enabled = !isLoading && mobileNumber.length == 10,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("send_otp_button")
                            .bounceClick(enabled = !isLoading && mobileNumber.length == 10, onClick = onSendOtp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Send OTP",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // SMS Info Alert Box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = LightPurpleBg
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = PurplePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "A 6-digit OTP will be sent to your mobile via SMS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PurplePrimary,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Terms and Privacy Footer
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "By continuing, you agree to our ",
                    fontSize = 12.sp,
                    color = TextSubtle,
                    textAlign = TextAlign.Center
                )
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Terms & Conditions",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PurplePrimary,
                    modifier = Modifier.clickable { }
                )
                Text(
                    text = " and ",
                    fontSize = 12.sp,
                    color = TextSubtle
                )
                Text(
                    text = "Privacy Policy",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PurplePrimary,
                    modifier = Modifier.clickable { }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Step 2: OTP Verification View matching the theme and flow.
 */
@Composable
private fun OtpVerificationView(
    mobileNumber: String,
    otpCode: String,
    isLoading: Boolean,
    errorMessage: String?,
    onOtpChanged: (String) -> Unit,
    onBackClicked: () -> Unit,
    onResendOtp: () -> Unit,
    onVerifyOtp: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    var timerSeconds by remember { mutableIntStateOf(30) }

    LaunchedEffect(key1 = timerSeconds) {
        if (timerSeconds > 0) {
            delay(1000)
            timerSeconds -= 1
        }
    }

    LaunchedEffect(Unit) {
        delay(300)
        focusRequester.requestFocus()
    }

    LaunchedEffect(otpCode) {
        if (otpCode.length == 6 && !isLoading) {
            onVerifyOtp()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        AsuliaTechHeader()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Back Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .size(42.dp)
                        .clickable { onBackClicked() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PurplePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // White Card for OTP Form
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Security Badge
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(LightPurpleBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Shield,
                            contentDescription = null,
                            tint = PurplePrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Verify OTP",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Enter the 6-digit OTP code sent to",
                        fontSize = 14.sp,
                        color = TextSubtle
                    )
                    Text(
                        text = "+91 $mobileNumber",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurplePrimary
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // 6 Digit OTP Boxes Row
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        BasicTextField(
                            value = otpCode,
                            onValueChange = {
                                if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                                    onOtpChanged(it)
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .focusRequester(focusRequester)
                                .fillMaxWidth()
                                .testTag("otp_input_field")
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                repeat(6) { index ->
                                    val char = otpCode.getOrNull(index)?.toString() ?: ""
                                    val isFocused = otpCode.length == index || (otpCode.length == 6 && index == 5)

                                    Box(
                                        modifier = Modifier
                                            .size(width = 44.dp, height = 52.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFFF8FAFC))
                                            .border(
                                                width = if (isFocused || char.isNotEmpty()) 2.dp else 1.dp,
                                                color = if (char.isNotEmpty()) PurplePrimary
                                                else if (isFocused) PurplePrimary.copy(alpha = 0.5f)
                                                else Color(0xFFE2E8F0),
                                                shape = RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = char,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextDark,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (isLoading) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = PurplePrimary,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Verifying OTP...",
                                color = PurplePrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = errorMessage,
                            color = Color(0xFFDC2626),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Resend Timer
                    if (timerSeconds > 0) {
                        val formattedSeconds = if (timerSeconds < 10) "0$timerSeconds" else "$timerSeconds"
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Resend OTP in ",
                                fontSize = 13.sp,
                                color = TextSubtle
                            )
                            Text(
                                text = "00:$formattedSeconds",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PurplePrimary
                            )
                        }
                    } else {
                        TextButton(
                            onClick = {
                                timerSeconds = 30
                                onResendOtp()
                            }
                        ) {
                            Text(
                                text = "Resend OTP Now",
                                fontWeight = FontWeight.Bold,
                                color = PurplePrimary,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Trust Badge: Secure login with OTP
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFF1F5F9)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Secure login with OTP",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF475569)
                            )
                        }
                    }
                }
            }
        }
    }
}
