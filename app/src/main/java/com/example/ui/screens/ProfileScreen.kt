package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Student
import com.example.ui.components.bounceClick
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.ProfileViewModel

private val PurpleHeaderStart = Color(0xFF7C3AED)
private val PurpleHeaderEnd = Color(0xFF6D28D9)
private val OffWhiteBg = Color(0xFFF8FAFC)
private val DarkTitle = Color(0xFF1E293B)
private val SlateSubtext = Color(0xFF64748B)
private val MutedGrey = Color(0xFF94A3B8)
private val ActiveGreenBg = Color(0xFFDCFCE7)
private val ActiveGreenText = Color(0xFF15803D)
private val ActiveGreenBorder = Color(0xFF22C55E)
private val PrimaryPurpleBg = Color(0xFFF3E8FF)
private val PrimaryPurpleText = Color(0xFF7C3AED)

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    val user by profileViewModel.user.collectAsState()
    val students by profileViewModel.students.collectAsState()

    var showEditProfile by remember { mutableStateOf(false) }
    var showSupportModal by remember { mutableStateOf(false) }
    var showTermsModal by remember { mutableStateOf(false) }
    var showPrivacyModal by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }

    if (showEditProfile) {
        EditProfileModal(
            user = user,
            onDismiss = { showEditProfile = false },
            onSave = { n, e, m -> profileViewModel.updateProfile(n, e, m) }
        )
    }

    if (showSupportModal) {
        ContactSupportModal(onDismiss = { showSupportModal = false })
    }

    if (showTermsModal) {
        TermsPrivacyModal(title = "Terms & Conditions", onDismiss = { showTermsModal = false })
    }

    if (showPrivacyModal) {
        TermsPrivacyModal(title = "Privacy Policy", onDismiss = { showPrivacyModal = false })
    }

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("Confirm Logout", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = { Text("Are you sure you want to log out of AsuliaTech Parent?", fontSize = 13.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirm = false
                        authViewModel.logout(onLogout)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Logout", color = Color.White, fontSize = 13.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text("Cancel", fontSize = 13.sp)
                }
            },
            shape = RoundedCornerShape(18.dp)
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(OffWhiteBg)
            .testTag("profile_screen"),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Top Header Block (Gradient + Avatar + Stats)
        item {
            ProfileHeaderBlock(
                parentName = user?.name ?: "Parent",
                childrenCount = students.size,
                activeCount = students.count { it.status.equals("Active", ignoreCase = true) },
                totalMins = students.sumOf { it.walletBalanceMinutes }
            )
        }

        // Section 1: MY CHILDREN
        item {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                Text(
                    text = "MY CHILDREN",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = SlateSubtext
                )
            }
        }

        items(students, key = { it.id }) { student ->
            Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)) {
                ChildProfileCard(student = student)
            }
        }

        // Warning / School Administration Banner
        item {
            Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                SchoolAdminNoticeBanner()
            }
        }

        // Section 2: SUPPORT & LEGAL
        item {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                Text(
                    text = "SUPPORT & LEGAL",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = SlateSubtext
                )
            }
        }

        item {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SupportMenuItemCard(
                    icon = Icons.Outlined.HelpOutline,
                    title = "Help & Support",
                    subtitle = "WhatsApp & Email support",
                    onClick = { showSupportModal = true },
                    testTag = "help_support_menu"
                )

                SupportMenuItemCard(
                    icon = Icons.Outlined.Description,
                    title = "Terms & Conditions",
                    subtitle = "Usage policies",
                    onClick = { showTermsModal = true },
                    testTag = "terms_menu"
                )

                SupportMenuItemCard(
                    icon = Icons.Default.Lock,
                    title = "Privacy Policy",
                    subtitle = "Data & privacy",
                    onClick = { showPrivacyModal = true },
                    testTag = "privacy_menu"
                )
            }
        }

        // Section 3: SESSION
        item {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                Text(
                    text = "SESSION",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = SlateSubtext
                )
            }
        }

        item {
            Box(modifier = Modifier.padding(horizontal = 14.dp)) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .bounceClick { showLogoutConfirm = true }
                        .testTag("logout_button")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFFEE2E2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Logout",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "Logout",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444)
                            )
                            Spacer(modifier = Modifier.height(1.dp))
                            Text(
                                text = "Sign out from this device",
                                fontSize = 10.sp,
                                color = MutedGrey
                            )
                        }
                    }
                }
            }
        }

        // Bottom Version Pill
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF1F5F9)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .border(1.dp, SlateSubtext, RoundedCornerShape(7.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "i",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateSubtext
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AsuliaTech v1.0.0",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = SlateSubtext
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeaderBlock(
    parentName: String,
    childrenCount: Int,
    activeCount: Int,
    totalMins: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(PurpleHeaderStart, PurpleHeaderEnd)
                )
            )
    ) {
        // Decorative background circles
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.08f),
                radius = size.width * 0.45f,
                center = Offset(size.width * 0.9f, size.height * 0.1f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.08f),
                radius = size.width * 0.35f,
                center = Offset(size.width * 0.05f, size.height * 0.8f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 14.dp, start = 14.dp, end = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Squircle Avatar Container
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.22f))
                    .border(1.2.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "P",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Title "Parent"
            Text(
                text = parentName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Floating Stats Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Children Stat
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$childrenCount",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = "Children",
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

                    // Active Stat
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$activeCount",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = "Active",
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

                    // Total Mins Stat
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$totalMins",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = "Mins Balance",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChildProfileCard(student: Student) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("child_card_${student.id}")
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Initial Box
                val isWk = student.initial == "WK"
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isWk) Color(0xFFEFF6FF) else Color(0xFFF3E8FF))
                        .border(1.2.dp, ActiveGreenBorder, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = student.initial,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isWk) Color(0xFF2563EB) else Color(0xFF7C3AED)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Name & Class
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = student.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkTitle
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = student.className,
                        fontSize = 11.sp,
                        color = SlateSubtext
                    )
                }

                // Active Badge & Mins
                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ActiveGreenBg
                    ) {
                        Text(
                            text = "Active",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = ActiveGreenText,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${student.walletBalanceMinutes} Min",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkTitle
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Card ID Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .border(1.dp, PrimaryPurpleText, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "i",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryPurpleText
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Card ID ",
                    fontSize = 11.sp,
                    color = SlateSubtext
                )
                Text(
                    text = student.cardId,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkTitle
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            HorizontalDivider(color = Color(0xFFF1F5F9))

            Spacer(modifier = Modifier.height(6.dp))

            // Registered Mobile Numbers Section
            Text(
                text = "REGISTERED MOBILE NUMBERS",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = SlateSubtext
            )

            Spacer(modifier = Modifier.height(6.dp))

            val mob1 = student.mobile1.ifEmpty { student.registeredMobile }
            val mob2 = student.mobile2
            val mob3 = student.mobile3

            val mobileList = mutableListOf<Pair<String, Boolean>>()
            if (mob1.isNotBlank()) mobileList.add(Pair(mob1, true))
            if (mob2.isNotBlank()) mobileList.add(Pair(mob2, false))
            if (mob3.isNotBlank()) mobileList.add(Pair(mob3, false))

            if (mobileList.isEmpty() && student.registeredMobile.isNotBlank()) {
                mobileList.add(Pair(student.registeredMobile, true))
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                mobileList.forEach { (number, isPrimary) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = PrimaryPurpleText,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = number,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkTitle
                        )

                        if (isPrimary) {
                            Spacer(modifier = Modifier.weight(1f))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = PrimaryPurpleBg
                            ) {
                                Text(
                                    text = "Primary",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryPurpleText,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SchoolAdminNoticeBanner() {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .border(1.dp, Color(0xFFD97706), RoundedCornerShape(7.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "i",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD97706)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            val noticeText = buildAnnotatedString {
                append("If any registered mobile number appears incorrect, please contact your ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF92400E))) {
                    append("school administration")
                }
                append(" immediately to get it updated.")
            }

            Text(
                text = noticeText,
                fontSize = 10.sp,
                lineHeight = 14.sp,
                color = Color(0xFFB45309)
            )
        }
    }
}

@Composable
private fun SupportMenuItemCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(PrimaryPurpleBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = PrimaryPurpleText,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkTitle
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = MutedGrey
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MutedGrey,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

