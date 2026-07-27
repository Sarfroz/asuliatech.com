package com.example.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AsuliaPrimary
import com.example.ui.viewmodel.AlertsViewModel
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.DashboardViewModel
import com.example.ui.viewmodel.HistoryViewModel
import com.example.ui.viewmodel.ProfileViewModel

enum class NavigationTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home),
    HISTORY("History", Icons.Filled.History, Icons.Outlined.History),
    ALERTS("Alerts", Icons.Filled.Notifications, Icons.Outlined.Notifications),
    PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun MainScreen(
    dashboardViewModel: DashboardViewModel,
    historyViewModel: HistoryViewModel,
    alertsViewModel: AlertsViewModel,
    profileViewModel: ProfileViewModel,
    authViewModel: AuthViewModel,
    onNavigateToRecharge: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(NavigationTab.HOME) }
    val alertsUiState by alertsViewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("bottom_navigation_bar")
            ) {
                NavigationTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        icon = {
                            if (tab == NavigationTab.ALERTS && alertsUiState.unreadCount > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge(containerColor = Color.Red) {
                                            Text("${alertsUiState.unreadCount}")
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.title
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.title
                                )
                            }
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AsuliaPrimary,
                            selectedTextColor = AsuliaPrimary,
                            indicatorColor = Color(0xFFF3E8FF),
                            unselectedIconColor = Color(0xFF64748B),
                            unselectedTextColor = Color(0xFF64748B)
                        ),
                        modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = selectedTab,
                label = "tab_crossfade"
            ) { currentTab ->
                when (currentTab) {
                    NavigationTab.HOME -> HomeScreen(
                        dashboardViewModel = dashboardViewModel,
                        onNavigateToRecharge = onNavigateToRecharge,
                        onNavigateToAlerts = { selectedTab = NavigationTab.ALERTS }
                    )
                    NavigationTab.HISTORY -> HistoryScreen(
                        historyViewModel = historyViewModel
                    )
                    NavigationTab.ALERTS -> AlertsScreen(
                        alertsViewModel = alertsViewModel
                    )
                    NavigationTab.PROFILE -> ProfileScreen(
                        profileViewModel = profileViewModel,
                        authViewModel = authViewModel,
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}
