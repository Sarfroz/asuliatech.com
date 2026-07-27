package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.CallSchedule
import com.example.data.models.NotificationAlert
import com.example.data.models.ParentUser
import com.example.data.models.Student
import com.example.data.models.WalletInfo
import com.example.data.repository.AsuliaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class DashboardUiState(
    val greeting: String = "Good Morning",
    val user: ParentUser? = null,
    val students: List<Student> = emptyList(),
    val selectedStudentId: String = "",
    val walletInfo: WalletInfo? = null,
    val callSchedule: CallSchedule = CallSchedule(),
    val isScheduleAvailable: Boolean = true,
    val isRefreshing: Boolean = false,
    val unreadNotificationsCount: Int = 2
)

class DashboardViewModel(
    private val repository: AsuliaRepository
) : ViewModel() {

    val user: StateFlow<ParentUser?> = repository.currentUser
    val students: StateFlow<List<Student>> = repository.students
    val selectedStudentId: StateFlow<String> = repository.selectedStudentId
    val schedule: StateFlow<CallSchedule> = repository.callSchedule

    private val _isRefreshing = MutableStateFlow(false)

    val uiState: StateFlow<DashboardUiState> = combine(
        user,
        students,
        selectedStudentId,
        schedule,
        _isRefreshing,
        repository.notifications
    ) { flowArray ->
        @Suppress("UNCHECKED_CAST")
        val currentUser = flowArray[0] as ParentUser?
        @Suppress("UNCHECKED_CAST")
        val studentList = flowArray[1] as List<Student>
        val currentSelectedId = flowArray[2] as String
        val repoSchedule = flowArray[3] as CallSchedule
        val refreshing = flowArray[4] as Boolean
        @Suppress("UNCHECKED_CAST")
        val notifs = flowArray[5] as List<NotificationAlert>

        val currentStudent = studentList.find { it.id == currentSelectedId } ?: studentList.firstOrNull()
        val currentSchedule = CallSchedule(
            dayText = currentStudent?.callScheduleDay?.ifEmpty { "Everyday" } ?: "Everyday",
            timeText = currentStudent?.callScheduleTime?.ifEmpty { "7am-10pm" } ?: "7am-10pm"
        )

        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greetingText = when (currentHour) {
            in 5..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }

        val wallet = repository.getWalletInfoForStudent(currentSelectedId)
        val available = currentSchedule.isAvailableNow()
        val unreadCount = notifs.count { !it.isRead }

        DashboardUiState(
            greeting = greetingText,
            user = currentUser,
            students = studentList,
            selectedStudentId = currentSelectedId,
            walletInfo = wallet,
            callSchedule = currentSchedule,
            isScheduleAvailable = available,
            isRefreshing = refreshing,
            unreadNotificationsCount = unreadCount
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    fun selectStudent(studentId: String) {
        repository.selectStudent(studentId)
    }

    fun refreshDashboard() {
        viewModelScope.launch {
            _isRefreshing.value = true
            repository.refreshData()
            _isRefreshing.value = false
        }
    }
}
