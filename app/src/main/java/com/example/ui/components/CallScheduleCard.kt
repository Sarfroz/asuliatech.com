package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.sp
import com.example.data.models.CallSchedule
import com.example.ui.theme.AsuliaGreenActive
import com.example.ui.theme.AsuliaGreenActiveBg
import com.example.ui.theme.AsuliaYellowBorder
import com.example.ui.theme.AsuliaYellowCard
import com.example.ui.theme.AsuliaYellowHeader

@Composable
fun CallScheduleCard(
    schedule: CallSchedule,
    isAvailable: Boolean,
    modifier: Modifier = Modifier
) {
    val cardBg = if (isAvailable) Color(0xFFF3E8FF) else AsuliaYellowCard
    val cardBorder = if (isAvailable) Color(0xFFD8B4FE) else AsuliaYellowBorder
    val headerColor = if (isAvailable) Color(0xFF7C3AED) else AsuliaYellowHeader
    val timingColor = if (isAvailable) Color(0xFF4C1D95) else Color(0xFF78350F)
    val iconBg = if (isAvailable) Color(0xFFEDE9FE) else AsuliaYellowHeader.copy(alpha = 0.15f)

    val badgeBg = if (isAvailable) AsuliaGreenActiveBg else Color(0xFFFEF3C7)
    val badgeDot = if (isAvailable) AsuliaGreenActive else Color(0xFFD97706)
    val badgeText = if (isAvailable) AsuliaGreenActive else Color(0xFF92400E)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, cardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("call_schedule_card")
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = iconBg,
                    modifier = Modifier.padding(end = 10.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Schedule Icon",
                            tint = headerColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = schedule.title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            fontSize = 11.sp
                        ),
                        color = headerColor,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = schedule.timing,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        color = timingColor,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Dynamic Badge: Available / Not Time
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = badgeBg
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = badgeDot,
                        modifier = Modifier.padding(end = 5.dp)
                    ) {
                        Box(modifier = Modifier.size(6.dp))
                    }
                    Text(
                        text = if (isAvailable) "Available" else "Not Time",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        softWrap = false,
                        maxLines = 1,
                        color = badgeText
                    )
                }
            }
        }
    }
}
