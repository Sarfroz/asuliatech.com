package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CallScheduleCard
import com.example.ui.components.StudentSelector
import com.example.ui.components.UsageProgressBar
import com.example.ui.components.WalletBalanceCard
import com.example.ui.components.bounceClick
import com.example.ui.theme.AsuliaBackground
import com.example.ui.theme.AsuliaPrimary
import com.example.ui.theme.AsuliaSecondary
import com.example.ui.viewmodel.DashboardViewModel

@Composable
fun HomeScreen(
    dashboardViewModel: DashboardViewModel,
    onNavigateToRecharge: () -> Unit,
    onNavigateToAlerts: () -> Unit
) {
    val uiState by dashboardViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        dashboardViewModel.refreshDashboard()
    }

    val refreshRotation by animateFloatAsState(
        targetValue = if (uiState.isRefreshing) 360f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "refreshRotation"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AsuliaBackground)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 100.dp, top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = uiState.greeting,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        ),
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = "Parent 👋",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        ),
                        color = Color(0xFF1E1B4B)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { dashboardViewModel.refreshDashboard() },
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("refresh_icon_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = AsuliaPrimary,
                            modifier = Modifier
                                .size(24.dp)
                                .rotate(refreshRotation)
                        )
                    }

                    Box {
                        IconButton(
                            onClick = onNavigateToAlerts,
                            modifier = Modifier
                                .size(42.dp)
                                .testTag("notifications_icon_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = AsuliaPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        if (uiState.unreadNotificationsCount > 0) {
                            Surface(
                                shape = CircleShape,
                                color = Color.Red,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 4.dp, end = 4.dp)
                                    .size(16.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${uiState.unreadNotificationsCount}",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Student Cards Selector Row
        item {
            StudentSelector(
                students = uiState.students,
                selectedStudentId = uiState.selectedStudentId,
                onStudentSelected = { dashboardViewModel.selectStudent(it) }
            )
        }

        // Wallet Balance Card
        item {
            WalletBalanceCard(
                walletInfo = uiState.walletInfo,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        // Progress Section & Small Info Cards
        item {
            UsageProgressBar(
                walletInfo = uiState.walletInfo,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        // Yellow Call Schedule Card
        item {
            CallScheduleCard(
                schedule = uiState.callSchedule,
                isAvailable = uiState.isScheduleAvailable,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        // Large Purple Gradient Recharge Button
        item {
            val gradientBrush = Brush.horizontalGradient(
                colors = listOf(AsuliaPrimary, AsuliaSecondary)
            )

            Button(
                onClick = onNavigateToRecharge,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .padding(horizontal = 20.dp)
                    .bounceClick(onClick = onNavigateToRecharge)
                    .background(brush = gradientBrush, shape = RoundedCornerShape(20.dp))
                    .testTag("recharge_now_large_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Recharge Now",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = Color.White
                    )
                }
            }
        }
    }
}
