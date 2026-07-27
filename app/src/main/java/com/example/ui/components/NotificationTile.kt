package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.models.NotificationAlert
import com.example.data.models.NotificationType
import com.example.ui.theme.AsuliaPrimary

@Composable
fun NotificationTile(
    notification: NotificationAlert,
    onTileClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, iconBg, iconTint) = when (notification.type) {
        NotificationType.RECHARGE_REMINDER -> Triple(
            Icons.Default.AccountBalanceWallet,
            Color(0xFFF3E8FF),
            AsuliaPrimary
        )
        NotificationType.CALL_REMINDER -> Triple(
            Icons.Default.Schedule,
            Color(0xFFFEF3C7),
            Color(0xFFD97706)
        )
        NotificationType.PLAN_EXPIRY -> Triple(
            Icons.Default.Info,
            Color(0xFFFEE2E2),
            Color(0xFFDC2626)
        )
        NotificationType.SYSTEM_ALERT -> Triple(
            Icons.Default.Notifications,
            Color(0xFFE0F2FE),
            Color(0xFF0284C7)
        )
    }

    val containerBg = if (!notification.isRead) Color(0xFFFAF5FF) else Color.White

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerBg),
        elevation = CardDefaults.cardElevation(defaultElevation = if (!notification.isRead) 2.dp else 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onTileClicked() }
            .testTag("notification_tile_${notification.id}")
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = CircleShape,
                color = iconBg,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = notification.timestamp,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (!notification.isRead) {
                Surface(
                    shape = CircleShape,
                    color = AsuliaPrimary,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(8.dp)
                ) {}
            }
        }
    }
}
