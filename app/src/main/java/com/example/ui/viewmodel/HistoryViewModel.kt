package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.CallLog
import com.example.data.models.CallStatus
import com.example.data.models.HistorySummary
import com.example.data.models.PaymentTransaction
import com.example.data.repository.AsuliaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HistoryUiState(
    val selectedTab: String = "PAYMENTS", // "PAYMENTS" or "CALLS"
    val searchQuery: String = "",
    val filterStatus: String = "ALL", // "ALL", "SUCCESS", "PENDING", "FAILED"
    val paymentTransactions: List<PaymentTransaction> = emptyList(),
    val callLogs: List<CallLog> = emptyList(),
    val summary: HistorySummary = HistorySummary(),
    val isRefreshing: Boolean = false
)

class HistoryViewModel(
    private val repository: AsuliaRepository
) : ViewModel() {

    private val _selectedTab = MutableStateFlow("PAYMENTS")
    private val _searchQuery = MutableStateFlow("")
    private val _filterStatus = MutableStateFlow("ALL")
    private val _isRefreshing = MutableStateFlow(false)

    val uiState: StateFlow<HistoryUiState> = combine(
        combine(repository.paymentHistory, repository.callHistory, repository.historySummary) { txns, calls, summary ->
            Triple(txns, calls, summary)
        },
        _selectedTab,
        _searchQuery,
        _filterStatus,
        _isRefreshing
    ) { (txns, callLogs, summary), tab, query, filter, refreshing ->

        val filteredTxns = txns.filter { txn ->
            val matchesQuery = query.isBlank() ||
                    txn.studentName.contains(query, ignoreCase = true) ||
                    txn.planTitle.contains(query, ignoreCase = true) ||
                    txn.txnId.contains(query, ignoreCase = true) ||
                    txn.date.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                "SUCCESS" -> txn.status == "SUCCESS"
                "PENDING" -> txn.status == "PENDING"
                "FAILED" -> txn.status == "FAILED"
                else -> true
            }

            matchesQuery && matchesFilter
        }

        val filteredCalls = callLogs.filter { log ->
            val matchesQuery = query.isBlank() ||
                    log.studentName.contains(query, ignoreCase = true) ||
                    log.dateTime.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                "COMPLETED" -> log.status == CallStatus.COMPLETED
                "MISSED" -> log.status == CallStatus.MISSED
                else -> true
            }

            matchesQuery && matchesFilter
        }

        HistoryUiState(
            selectedTab = tab,
            searchQuery = query,
            filterStatus = filter,
            paymentTransactions = filteredTxns,
            callLogs = filteredCalls,
            summary = summary,
            isRefreshing = refreshing
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistoryUiState()
    )

    fun onTabChanged(tab: String) {
        _selectedTab.value = tab
        _filterStatus.value = "ALL" // Reset filter when switching tabs
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onFilterChanged(filter: String) {
        _filterStatus.value = filter
    }

    fun refreshHistory() {
        viewModelScope.launch {
            _isRefreshing.value = true
            repository.refreshData()
            _isRefreshing.value = false
        }
    }
}

