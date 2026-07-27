package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CallLogTile
import com.example.ui.components.PaymentTile
import com.example.ui.theme.AsuliaBackground
import com.example.ui.viewmodel.HistoryViewModel

private val PurpleHeaderBg = Color(0xFF7C3AED)
private val LightPurpleText = Color(0xFFDDD6FE)
private val DarkTitle = Color(0xFF1E293B)
private val GraySubtext = Color(0xFF64748B)

@Composable
fun HistoryScreen(
    historyViewModel: HistoryViewModel
) {
    val uiState by historyViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        historyViewModel.refreshHistory()
    }

    val isPaymentsTab = uiState.selectedTab == "PAYMENTS"

    // Calculate Summary Stats from API response summary or fallback to list
    val successfulCount = if (uiState.summary.successfulCount > 0) uiState.summary.successfulCount else uiState.paymentTransactions.count { it.status == "SUCCESS" }
    val totalSpent = if (uiState.summary.totalSpent > 0) uiState.summary.totalSpent else uiState.paymentTransactions.filter { it.status == "SUCCESS" }.sumOf { it.amountInr }
    val pendingCount = if (uiState.summary.pendingCount > 0) uiState.summary.pendingCount else uiState.paymentTransactions.count { it.status == "PENDING" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AsuliaBackground)
            .testTag("history_screen")
    ) {
        // Top Purple Banner Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PurpleHeaderBg)
                .padding(top = 14.dp, bottom = 14.dp, start = 14.dp, end = 14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isPaymentsTab) "Recharge History" else "Call History",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "WAHAJ KHAN",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp,
                            color = LightPurpleText
                        )
                    }

                    // Tab Switcher Pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.clickable {
                            historyViewModel.onTabChanged(if (isPaymentsTab) "CALLS" else "PAYMENTS")
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isPaymentsTab) Icons.Default.Call else Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isPaymentsTab) "Call Logs" else "Payments",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Floating Stats Card Inside Header
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Stat 1: Success Count
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$successfulCount",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Success",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(20.dp)
                                .background(Color.White.copy(alpha = 0.3f))
                        )

                        // Stat 2: Spent
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "₹$totalSpent",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Spent",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(20.dp)
                                .background(Color.White.copy(alpha = 0.3f))
                        )

                        // Stat 3: Pending
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$pendingCount",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Pending",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Pills Row (Only shown for Payments Tab)
        if (isPaymentsTab) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isAll = uiState.filterStatus == "ALL"
                val isSuccess = uiState.filterStatus == "SUCCESS"
                val isPending = uiState.filterStatus == "PENDING"
                val isFailed = uiState.filterStatus == "FAILED"

                // ALL Chip
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isAll) PurpleHeaderBg else Color.White,
                    border = if (!isAll) BorderStroke(1.dp, Color(0xFFE2E8F0)) else null,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { historyViewModel.onFilterChanged("ALL") }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatListBulleted,
                            contentDescription = null,
                            tint = if (isAll) Color.White else GraySubtext,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "All",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAll) Color.White else DarkTitle
                        )
                    }
                }

                // SUCCESS Chip
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSuccess) Color(0xFFDCFCE7) else Color.White,
                    border = BorderStroke(1.dp, if (isSuccess) Color(0xFF86EFAC) else Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { historyViewModel.onFilterChanged("SUCCESS") }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Success",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D)
                        )
                    }
                }

                // PENDING Chip
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isPending) Color(0xFFFEF3C7) else Color.White,
                    border = BorderStroke(1.dp, if (isPending) Color(0xFDF0A5) else Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { historyViewModel.onFilterChanged("PENDING") }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Pending",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB45309)
                        )
                    }
                }

                // FAILED Chip
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isFailed) Color(0xFFFEE2E2) else Color.White,
                    border = BorderStroke(1.dp, if (isFailed) Color(0xFCA5A5) else Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { historyViewModel.onFilterChanged("FAILED") }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Failed",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB91C1C)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        // List View Content
        if (isPaymentsTab) {
            if (uiState.paymentTransactions.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.ReceiptLong,
                    title = "No Payment History Found",
                    subtitle = "Your recharge receipts and transaction history will appear here."
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 2.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.paymentTransactions, key = { it.id }) { txn ->
                        PaymentTile(transaction = txn)
                    }
                }
            }
        } else {
            if (uiState.callLogs.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Call,
                    title = "No Call Logs Found",
                    subtitle = "Your recent call logs will appear here."
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 2.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.callLogs, key = { it.id }) { log ->
                        CallLogTile(callLog = log)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyStateView(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFCBD5E1),
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = DarkTitle
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            fontSize = 11.sp,
            color = GraySubtext
        )
    }
}

