package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.NotificationAlert
import com.example.data.repository.AsuliaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class AlertsUiState(
    val notifications: List<NotificationAlert> = emptyList(),
    val unreadCount: Int = 0
)

class AlertsViewModel(
    private val repository: AsuliaRepository
) : ViewModel() {

    val uiState: StateFlow<AlertsUiState> = repository.notifications.map { list ->
        AlertsUiState(
            notifications = list,
            unreadCount = list.count { !it.isRead }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AlertsUiState()
    )

    fun markAsRead(id: String) {
        repository.markNotificationAsRead(id)
    }

    fun markAllRead() {
        repository.markAllNotificationsRead()
    }
}
