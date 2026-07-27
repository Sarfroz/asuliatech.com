package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.RechargePlan
import com.example.ui.components.CashfreeWebViewModal
import com.example.ui.components.bounceClick
import com.example.ui.theme.AsuliaBackground
import com.example.ui.theme.AsuliaPrimary
import com.example.ui.viewmodel.RechargeViewModel

private val PurpleHeaderBg = Color(0xFF7C3AED)
private val LightPurpleBadge = Color(0xFFEDE9FE)
private val DarkText = Color(0xFF1E293B)
private val GraySubtext = Color(0xFF64748B)

@Composable
fun RechargeScreen(
    rechargeViewModel: RechargeViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by rechargeViewModel.uiState.collectAsState()
    val allPlans by rechargeViewModel.plans.collectAsState()
    val students by rechargeViewModel.students.collectAsState()
    val selectedStudentId by rechargeViewModel.selectedStudentId.collectAsState()

    val currentStudent = remember(students, selectedStudentId) {
        students.find { it.id == selectedStudentId || it.cardId == selectedStudentId } ?: students.firstOrNull()
    }

    var selectedCategory by remember { mutableStateOf("Monthly") }

    val filteredPlans = remember(selectedCategory, allPlans) {
        allPlans.filter { it.category.equals(selectedCategory, ignoreCase = true) }
            .ifEmpty { allPlans }
    }

    // 1. In-App Cashfree WebView Modal
    if (uiState.isWebViewModalOpen && !uiState.paymentUrl.isNullOrEmpty()) {
        CashfreeWebViewModal(
            paymentUrl = uiState.paymentUrl!!,
            orderId = uiState.orderId,
            amount = uiState.selectedPlan?.priceInr ?: filteredPlans.firstOrNull()?.priceInr,
            paymentSessionId = uiState.paymentSessionId,
            onSuccess = {
                rechargeViewModel.onPaymentSuccessCallback()
            },
            onFailure = { msg ->
                rechargeViewModel.onPaymentFailedCallback(msg)
            },
            onClose = {
                rechargeViewModel.closeWebViewModal()
            }
        )
    }

    // 2. Loading State Modal (Initiating or Verifying)
    if (uiState.isInitiatingPayment || uiState.isVerifyingPayment) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Text(
                    text = if (uiState.isVerifyingPayment) "Verifying Payment..." else "Initiating Cashfree Payment...",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    CircularProgressIndicator(color = PurpleHeaderBg)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (uiState.isVerifyingPayment)
                            "Updating student calling wallet and transaction history..."
                        else
                            "Connecting to Cashfree secure payment gateway...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GraySubtext
                    )
                }
            },
            confirmButton = {},
            shape = RoundedCornerShape(20.dp)
        )
    }

    // 3. Payment Success Dialog
    if (uiState.paymentSuccess) {
        AlertDialog(
            onDismissRequest = {
                rechargeViewModel.resetState()
                onNavigateBack()
            },
            title = {
                Text(
                    text = "Recharge Successful! 🎉",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = Color(0xFF16A34A),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Payment verified successfully!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = DarkText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${uiState.selectedPlan?.minutes ?: "Calling"} minutes added to ${currentStudent?.name ?: "student"}'s wallet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GraySubtext
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        rechargeViewModel.resetState()
                        onNavigateBack()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleHeaderBg)
                ) {
                    Text("Done", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    // 4. Payment Failed / Error Alert
    val failMessage = uiState.paymentFailedMessage ?: uiState.errorMessage
    if (failMessage != null) {
        AlertDialog(
            onDismissRequest = { rechargeViewModel.clearMessages() },
            title = {
                Text(
                    text = "Payment Alert",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error",
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = failMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkText
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { rechargeViewModel.clearMessages() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleHeaderBg)
                ) {
                    Text("OK", color = Color.White)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AsuliaBackground)
            .testTag("recharge_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Purple Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .background(PurpleHeaderBg)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Circular Back Button
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { onNavigateBack() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Centered Titles
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Recharge Plans",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Choose a plan for ${currentStudent?.name ?: "Student"}",
                            fontSize = 11.sp,
                            color = Color(0xFFDDD6FE)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.size(36.dp))
                }
            }

            // Main Content Area
            LazyColumn(
                contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // 2. Student Info Card
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar Box with initials
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFEFF6FF))
                                    .border(1.dp, Color(0xFF3B82F6), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentStudent?.initial ?: "S",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D4ED8)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            // Student Name and Card ID
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentStudent?.name ?: "Student Name",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText
                                )
                                Spacer(modifier = Modifier.height(1.dp))
                                Text(
                                    text = "Card ID: ${currentStudent?.cardId ?: "A3210024"}",
                                    fontSize = 11.sp,
                                    color = GraySubtext
                                )
                            }

                            // Right Balance Pill Badge
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = LightPurpleBadge
                            ) {
                                Text(
                                    text = "${currentStudent?.walletBalanceMinutes ?: 0} Min",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PurpleHeaderBg,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // 3. Tab Selector Row (Monthly Plans vs Wallet Plans)
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Monthly Plans Tab
                            val isMonthlySelected = selectedCategory == "Monthly"
                            Button(
                                onClick = { selectedCategory = "Monthly" },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isMonthlySelected) PurpleHeaderBg else Color.Transparent,
                                    contentColor = if (isMonthlySelected) Color.White else GraySubtext
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(vertical = 8.dp),
                                elevation = if (isMonthlySelected) ButtonDefaults.buttonElevation(defaultElevation = 1.dp) else null,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Monthly Plans", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            // Wallet Plans Tab
                            val isWalletSelected = selectedCategory == "Wallet"
                            Button(
                                onClick = { selectedCategory = "Wallet" },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isWalletSelected) PurpleHeaderBg else Color.Transparent,
                                    contentColor = if (isWalletSelected) Color.White else GraySubtext
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(vertical = 8.dp),
                                elevation = if (isWalletSelected) ButtonDefaults.buttonElevation(defaultElevation = 1.dp) else null,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Wallet Plans", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }

                // 4. Plan Cards
                items(filteredPlans, key = { it.id }) { plan ->
                    val isSelected = uiState.selectedPlan?.id == plan.id

                    RechargePlanCard(
                        plan = plan,
                        isSelected = isSelected,
                        onSelect = { rechargeViewModel.selectPlan(plan) }
                    )
                }

                // 5. Info Note Box
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF5F3FF),
                        border = BorderStroke(1.dp, Color(0xFFDDD6FE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info",
                                tint = PurpleHeaderBg,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Daily minutes reset every day at 12:00 AM midnight.",
                                fontSize = 11.sp,
                                color = Color(0xFF6B21A8),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // 6. Bottom Sticky CTA Button (Pay Now)
        val selectedPlan = uiState.selectedPlan ?: filteredPlans.firstOrNull()
        val currentPrice = selectedPlan?.priceInr ?: 70

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.95f))
                .navigationBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Button(
                onClick = {
                    if (selectedPlan != null) {
                        rechargeViewModel.selectPlan(selectedPlan)
                    }
                    rechargeViewModel.initiatePaymentFlow(currentStudent?.cardId)
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PurpleHeaderBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .bounceClick {
                        if (selectedPlan != null) {
                            rechargeViewModel.selectPlan(selectedPlan)
                        }
                        rechargeViewModel.initiatePaymentFlow(currentStudent?.cardId)
                    }
                    .testTag("pay_now_button")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(modifier = Modifier.width(20.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Secure Lock",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Pay Now ₹$currentPrice",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.25f),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Forward",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Custom Plan Card.
 */
@Composable
private fun RechargePlanCard(
    plan: RechargePlan,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val shakeAnim = remember { Animatable(0f) }

    LaunchedEffect(isSelected) {
        if (isSelected) {
            shakeAnim.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 380
                    0.0f at 0
                    -1.0f at 75
                    1.0f at 150
                    -0.6f at 225
                    0.4f at 300
                    0.0f at 380
                }
            )
        } else {
            shakeAnim.snapTo(0f)
        }
    }

    val selectScale by animateFloatAsState(
        targetValue = if (isSelected) 1.03f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "selectScale"
    )

    val cardElevation by animateDpAsState(
        targetValue = if (isSelected) 8.dp else 1.dp,
        animationSpec = spring(),
        label = "elevation"
    )

    val cardColor by animateColorAsState(
        targetValue = if (isSelected) PurpleHeaderBg else Color.White,
        animationSpec = spring(),
        label = "cardColor"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        border = if (!isSelected) BorderStroke(1.dp, Color(0xFFE2E8F0)) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = selectScale
                scaleY = selectScale
                rotationZ = shakeAnim.value * 3.5f
                translationX = shakeAnim.value * 14.dp.toPx()
            }
            .bounceClick { onSelect() }
            .testTag("plan_card_${plan.id}")
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Popular Tag on Top Right if applicable
            if (plan.isPopular && !isSelected) {
                Surface(
                    shape = RoundedCornerShape(bottomStart = 10.dp, topEnd = 16.dp),
                    color = Color(0xFFF59E0B),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "Popular",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Column Details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = plan.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else DarkText
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = plan.dailyQuota ?: plan.description,
                        fontSize = 11.sp,
                        color = if (isSelected) Color(0xFFE9D5FF) else GraySubtext
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Total Minutes Badge Pill
                    plan.totalQuotaBadge?.let { badgeText ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color.White.copy(alpha = 0.22f) else Color(0xFFDCFCE7)
                        ) {
                            Text(
                                text = badgeText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color(0xFF166534),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Right Price
                Text(
                    text = "₹${plan.priceInr}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else PurpleHeaderBg
                )
            }
        }
    }
}

