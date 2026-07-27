package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.RechargePlan
import com.example.data.repository.AsuliaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RechargeUiState(
    val selectedPlan: RechargePlan? = null,
    val isInitiatingPayment: Boolean = false,
    val isVerifyingPayment: Boolean = false,
    val isProcessingPayment: Boolean = false,
    val isWebViewModalOpen: Boolean = false,
    val paymentUrl: String? = null,
    val paymentSessionId: String? = null,
    val orderId: String? = null,
    val transactionId: String? = null,
    val paymentSuccess: Boolean = false,
    val paymentFailedMessage: String? = null,
    val selectedPaymentMethod: String = "UPI",
    val errorMessage: String? = null
)

class RechargeViewModel(
    private val repository: AsuliaRepository
) : ViewModel() {

    val plans = repository.rechargePlans
    val students = repository.students
    val selectedStudentId = repository.selectedStudentId

    private val _uiState = MutableStateFlow(RechargeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadPlans()
    }

    fun loadPlans() {
        viewModelScope.launch {
            repository.fetchPlans()
            if (_uiState.value.selectedPlan == null) {
                _uiState.value = _uiState.value.copy(selectedPlan = plans.value.firstOrNull())
            }
        }
    }

    fun selectPlan(plan: RechargePlan) {
        _uiState.value = _uiState.value.copy(
            selectedPlan = plan,
            paymentSuccess = false,
            errorMessage = null,
            paymentFailedMessage = null
        )
    }

    fun selectPaymentMethod(method: String) {
        _uiState.value = _uiState.value.copy(selectedPaymentMethod = method)
    }

    fun initiatePaymentFlow(cardIdParam: String? = null) {
        val plan = _uiState.value.selectedPlan ?: plans.value.firstOrNull() ?: return
        val currentStudent = students.value.find { it.id == selectedStudentId.value || it.cardId == selectedStudentId.value }
            ?: students.value.firstOrNull()

        val cardId = cardIdParam
            ?.ifEmpty { null }
            ?: currentStudent?.cardId
            ?: currentStudent?.id
            ?: "A3210083"

        _uiState.value = _uiState.value.copy(
            isInitiatingPayment = true,
            errorMessage = null,
            paymentFailedMessage = null,
            paymentSuccess = false
        )

        viewModelScope.launch {
            val result = repository.initiateRecharge(
                cardId = cardId,
                planCode = plan.id,
                amount = plan.priceInr.toDouble()
            )

            if (result.isSuccess) {
                val data = result.getOrNull()
                val ordId = data?.orderId ?: "ORD_${System.currentTimeMillis()}"
                val txnId = data?.transactionId ?: "TXN_${System.currentTimeMillis()}"
                val rawUrl = data?.paymentUrl

                val webUrl = rawUrl.orEmpty()

                _uiState.value = _uiState.value.copy(
                    isInitiatingPayment = false,
                    orderId = ordId,
                    transactionId = txnId,
                    paymentUrl = webUrl,
                    paymentSessionId = data?.paymentSessionId,
                    isWebViewModalOpen = true
                )
            } else {
                val err = result.exceptionOrNull()?.message ?: "Failed to initiate payment"
                _uiState.value = _uiState.value.copy(
                    isInitiatingPayment = false,
                    errorMessage = err
                )
            }
        }
    }

    fun onPaymentSuccessCallback() {
        val ordId = _uiState.value.orderId ?: "ORD_${System.currentTimeMillis()}"
        val txnId = _uiState.value.transactionId ?: "TXN_${System.currentTimeMillis()}"

        _uiState.value = _uiState.value.copy(
            isWebViewModalOpen = false,
            isVerifyingPayment = true,
            errorMessage = null,
            paymentFailedMessage = null
        )

        viewModelScope.launch {
            val verifyResult = repository.verifyRecharge(orderId = ordId, transactionId = txnId)
            // Automatically refresh student profile balance and transaction history
            repository.refreshData()

            _uiState.value = _uiState.value.copy(
                isVerifyingPayment = false,
                paymentSuccess = true
            )
        }
    }

    fun onPaymentFailedCallback(message: String = "Payment Failed or Cancelled") {
        _uiState.value = _uiState.value.copy(
            isWebViewModalOpen = false,
            isVerifyingPayment = false,
            isInitiatingPayment = false,
            paymentFailedMessage = message
        )
    }

    fun closeWebViewModal() {
        onPaymentFailedCallback("Payment Failed or Cancelled")
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            paymentFailedMessage = null
        )
    }

    fun processRecharge(onSuccess: () -> Unit) {
        val currentStudent = students.value.find { it.id == selectedStudentId.value || it.cardId == selectedStudentId.value }
            ?: students.value.firstOrNull()
        initiatePaymentFlow(currentStudent?.cardId)
    }

    fun resetState() {
        _uiState.value = RechargeUiState(selectedPlan = plans.value.firstOrNull())
    }
}

