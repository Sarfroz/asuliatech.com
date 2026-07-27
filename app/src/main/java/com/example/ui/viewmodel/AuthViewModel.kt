package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val mobileNumber: String = "",
    val password: String = "",
    val otpSent: Boolean = false,
    val otpCode: String = "",
    val rememberMe: Boolean = true,
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val errorMessage: String? = null,
    val isBiometricAvailable: Boolean = true
)

class AuthViewModel(
    private val repository: com.example.data.repository.AsuliaRepository = com.example.data.repository.AsuliaRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun onMobileChanged(value: String) {
        val filtered = value.filter { it.isDigit() }.take(10)
        _uiState.value = _uiState.value.copy(mobileNumber = filtered, errorMessage = null)
    }

    fun onPasswordChanged(value: String) {
        _uiState.value = _uiState.value.copy(password = value, errorMessage = null)
    }

    fun onOtpCodeChanged(value: String) {
        val filtered = value.filter { it.isDigit() }.take(6)
        _uiState.value = _uiState.value.copy(otpCode = filtered, errorMessage = null)
    }

    fun resetOtp() {
        _uiState.value = _uiState.value.copy(otpSent = false, otpCode = "", errorMessage = null)
    }

    fun onRememberMeToggled(value: Boolean) {
        _uiState.value = _uiState.value.copy(rememberMe = value)
    }

    fun sendOtp(onOtpSent: (String) -> Unit) {
        val mobile = _uiState.value.mobileNumber
        if (mobile.length != 10) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid 10-digit mobile number")
            return
        }
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val result = repository.sendLoginOtp(mobile)
            if (result.isSuccess) {
                val otpMsg = result.getOrNull() ?: "OTP Sent"
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    otpSent = true,
                    otpCode = ""
                )
                onOtpSent(otpMsg)
            } else {
                val err = result.exceptionOrNull()?.message ?: "Failed to send OTP"
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = err
                )
            }
        }
    }

    fun verifyOtpAndLogin(onSuccess: () -> Unit) {
        val mobile = _uiState.value.mobileNumber
        val otp = _uiState.value.otpCode

        if (mobile.length != 10 || otp.length != 6) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid 10-digit mobile and 6-digit OTP")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val result = repository.verifyLoginOtp(mobile, otp)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = true
                )
                onSuccess()
            } else {
                val err = result.exceptionOrNull()?.message ?: "Invalid OTP or Mobile number"
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = err
                )
            }
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.logout()
            _uiState.value = AuthUiState()
            onLoggedOut()
        }
    }
}
