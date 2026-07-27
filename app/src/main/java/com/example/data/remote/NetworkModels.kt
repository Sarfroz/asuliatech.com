package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * REST API Data Transfer Objects (DTOs) for AsuliaTech Parent API
 */

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    @Json(name = "status") val status: Boolean? = true,
    @Json(name = "success") val success: Boolean? = true,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: T? = null
)

@JsonClass(generateAdapter = true)
data class LogoutResponseDto(
    @Json(name = "message") val message: String? = "Logged out successfully"
)

@JsonClass(generateAdapter = true)
data class HistorySummaryDto(
    @Json(name = "successful_count") val successfulCount: Int? = 0,
    @Json(name = "total_spent") val totalSpent: Int? = 0,
    @Json(name = "pending_count") val pendingCount: Int? = 0
)

@JsonClass(generateAdapter = true)
data class HistoryItemDto(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "student_name") val studentName: String? = null,
    @Json(name = "card_id") val cardId: String? = null,
    @Json(name = "plan_name") val planName: String? = null,
    @Json(name = "payment_amt") val paymentAmt: Int? = null,
    @Json(name = "payment_status") val paymentStatus: String? = null,
    @Json(name = "recharge_date") val rechargeDate: String? = null
)

@JsonClass(generateAdapter = true)
data class HistoryResponseDto(
    @Json(name = "success") val success: Boolean? = true,
    @Json(name = "summary") val summary: HistorySummaryDto? = null,
    @Json(name = "history") val history: List<HistoryItemDto>? = emptyList(),
    @Json(name = "status") val status: Boolean? = null,
    @Json(name = "data") val data: HistoryDataDto? = null,
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class HistoryDataDto(
    @Json(name = "payments") val payments: List<PaymentTransactionDto>? = emptyList(),
    @Json(name = "call_logs") val callLogs: List<CallLogDto>? = emptyList()
)

@JsonClass(generateAdapter = true)
data class PaymentTransactionDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "student_name") val studentName: String? = null,
    @Json(name = "plan_title") val planTitle: String? = null,
    @Json(name = "amount") val amount: Double? = null,
    @Json(name = "amount_inr") val amountInr: Int? = null,
    @Json(name = "date") val date: String? = null,
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "payment_method") val paymentMethod: String? = null,
    @Json(name = "txn_id") val txnId: String? = null,
    @Json(name = "transaction_id") val transactionId: String? = null,
    @Json(name = "minutes_added") val minutesAdded: Int? = null
)

@JsonClass(generateAdapter = true)
data class SendOtpRequestDto(
    @Json(name = "parent_mobile") val parentMobile: String
)

@JsonClass(generateAdapter = true)
data class SendOtpResponseDto(
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "status") val status: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "otp") val otp: String? = null
)

@JsonClass(generateAdapter = true)
data class VerifyOtpRequestDto(
    @Json(name = "parent_mobile") val parentMobile: String,
    @Json(name = "otp") val otp: String
)

@JsonClass(generateAdapter = true)
data class VerifyOtpResponseDto(
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "status") val status: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "token") val token: String? = null,
    @Json(name = "primary_card_id") val primaryCardId: String? = null,
    @Json(name = "user") val user: ParentUserDto? = null,
    @Json(name = "students") val students: List<StudentDto>? = null
)

@JsonClass(generateAdapter = true)
data class StudentProfileResponseDto(
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "status") val status: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "student") val student: StudentProfileDto? = null,
    @Json(name = "data") val data: StudentProfileDto? = null
)

@JsonClass(generateAdapter = true)
data class StudentProfileDto(
    @Json(name = "card_id") val cardId: String? = null,
    @Json(name = "student_name") val studentName: String? = null,
    @Json(name = "school_name") val schoolName: String? = null,
    @Json(name = "section") val section: String? = null,
    @Json(name = "class_name") val className: String? = null,
    @Json(name = "credits_left") val creditsLeft: Int? = null,
    @Json(name = "student_credits") val studentCredits: Int? = null,
    @Json(name = "expiry_status") val expiryStatus: String? = null,
    @Json(name = "expiry_text") val expiryText: String? = null,
    @Json(name = "daily_limit") val dailyLimit: Int? = null,
    @Json(name = "day_minutes_max") val dayMinutesMax: Int? = null,
    @Json(name = "day_minutes_left") val dayMinutesLeft: Int? = null,
    @Json(name = "call_schedule_day") val callScheduleDay: String? = null,
    @Json(name = "call_schedule_time") val callScheduleTime: String? = null,
    @Json(name = "registered_mobile") val registeredMobile: String? = null,
    @Json(name = "mobile1") val mobile1: String? = null,
    @Json(name = "mobile2") val mobile2: String? = null,
    @Json(name = "mobile3") val mobile3: String? = null,
    @Json(name = "status") val status: String? = null
)

@JsonClass(generateAdapter = true)
data class PlansResponseDto(
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "status") val status: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "plans") val plans: List<RechargePlanDto>? = null,
    @Json(name = "data") val data: List<RechargePlanDto>? = null
)

@JsonClass(generateAdapter = true)
data class RechargePlanDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "plan_code") val planCode: String? = null,
    @Json(name = "plan_name") val planName: String? = null,
    @Json(name = "title") val title: String? = null,
    @Json(name = "amount") val amount: Double? = null,
    @Json(name = "price_inr") val priceInr: Double? = null,
    @Json(name = "credit_cost") val creditCost: Double? = null,
    @Json(name = "daily_minutes") val dailyMinutes: Int? = null,
    @Json(name = "minutes") val minutes: Int? = null,
    @Json(name = "validity_days") val validityDays: Int? = null,
    @Json(name = "validity_text") val validityText: String? = null,
    @Json(name = "plan_type") val planType: String? = null,
    @Json(name = "wallet_minutes") val walletMinutes: Int? = null,
    @Json(name = "is_unlimited") val isUnlimited: Boolean? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "tag") val tag: String? = null,
    @Json(name = "category") val category: String? = null
)

@JsonClass(generateAdapter = true)
data class InitiateRechargeRequestDto(
    @Json(name = "card_id") val cardId: String,
    @Json(name = "plan_code") val planCode: String? = null,
    @Json(name = "amount") val amount: Double? = null
)

@JsonClass(generateAdapter = true)
data class InitiateRechargeResponseDto(
    @Json(name = "success") val success: Boolean? = true,
    @Json(name = "status") val status: Boolean? = true,
    @Json(name = "message") val message: String? = null,
    @Json(name = "transaction_id") val transactionId: String? = null,
    @Json(name = "order_id") val orderId: String? = null,
    @Json(name = "payment_url") val paymentUrl: String? = null,
    @Json(name = "payment_link") val paymentLink: String? = null,
    @Json(name = "payment_session_id") val paymentSessionId: String? = null,
    @Json(name = "session_id") val sessionId: String? = null,
    @Json(name = "data") val data: InitiateRechargeResponseDto? = null
)

@JsonClass(generateAdapter = true)
data class VerifyRechargeRequestDto(
    @Json(name = "order_id") val orderId: String,
    @Json(name = "transaction_id") val transactionId: String? = null
)

@JsonClass(generateAdapter = true)
data class VerifyRechargeResponseDto(
    @Json(name = "success") val success: Boolean? = true,
    @Json(name = "status") val status: String? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "payment_status") val paymentStatus: String? = null,
    @Json(name = "credits_added") val creditsAdded: Int? = null,
    @Json(name = "total_credits") val totalCredits: Int? = null,
    @Json(name = "data") val data: VerifyRechargeResponseDto? = null
)


@JsonClass(generateAdapter = true)
data class LoginRequestDto(
    @Json(name = "mobile_number") val mobileNumber: String,
    @Json(name = "password") val password: String? = null,
    @Json(name = "otp") val otp: String? = null
)

@JsonClass(generateAdapter = true)
data class LoginResponseDto(
    @Json(name = "token") val token: String?,
    @Json(name = "user") val user: ParentUserDto?
)

@JsonClass(generateAdapter = true)
data class ForgotPasswordRequestDto(
    @Json(name = "mobile_number") val mobileNumber: String
)

@JsonClass(generateAdapter = true)
data class ResetPasswordRequestDto(
    @Json(name = "mobile_number") val mobileNumber: String,
    @Json(name = "otp") val otp: String,
    @Json(name = "new_password") val newPassword: String
)

// --- Data Models DTOs ---

@JsonClass(generateAdapter = true)
data class ParentUserDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "mobile_number") val mobileNumber: String,
    @Json(name = "email") val email: String,
    @Json(name = "current_plan") val currentPlan: String? = null,
    @Json(name = "wallet_balance_minutes") val walletBalanceMinutes: Int = 0,
    @Json(name = "linked_students_count") val linkedStudentsCount: Int = 0
)

@JsonClass(generateAdapter = true)
data class StudentDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "card_id") val cardId: String? = null,
    @Json(name = "student_name") val studentName: String? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "initial") val initial: String? = null,
    @Json(name = "section") val section: String? = null,
    @Json(name = "class_name") val className: String? = null,
    @Json(name = "school_name") val schoolName: String? = null,
    @Json(name = "student_credits") val studentCredits: Int? = null,
    @Json(name = "wallet_balance_minutes") val walletBalanceMinutes: Int? = null,
    @Json(name = "day_minutes_max") val dayMinutesMax: Int? = null,
    @Json(name = "daily_limit_minutes") val dailyLimitMinutes: Int? = null,
    @Json(name = "day_minutes_left") val dayMinutesLeft: Int? = null,
    @Json(name = "used_today_minutes") val usedTodayMinutes: Int? = null,
    @Json(name = "call_schedule_day") val callScheduleDay: String? = null,
    @Json(name = "call_schedule_time") val callScheduleTime: String? = null,
    @Json(name = "plan_name") val planName: String? = null,
    @Json(name = "expiry") val expiry: String? = null,
    @Json(name = "expiry_text") val expiryText: String? = null,
    @Json(name = "registered_mobile") val registeredMobile: String? = null,
    @Json(name = "mobile1") val mobile1: String? = null,
    @Json(name = "mobile2") val mobile2: String? = null,
    @Json(name = "mobile3") val mobile3: String? = null,
    @Json(name = "status") val status: String? = null
)

@JsonClass(generateAdapter = true)
data class WalletInfoDto(
    @Json(name = "balance_minutes") val balanceMinutes: Int,
    @Json(name = "status") val status: String,
    @Json(name = "daily_limit_minutes") val dailyLimitMinutes: Int,
    @Json(name = "used_today_minutes") val usedTodayMinutes: Int,
    @Json(name = "remaining_minutes") val remainingMinutes: Int,
    @Json(name = "plan_name") val planName: String,
    @Json(name = "expiry") val expiry: String
)

@JsonClass(generateAdapter = true)
data class CallLogDto(
    @Json(name = "id") val id: String,
    @Json(name = "student_id") val studentId: String,
    @Json(name = "student_name") val studentName: String,
    @Json(name = "date_time") val dateTime: String,
    @Json(name = "duration_minutes") val durationMinutes: Int,
    @Json(name = "duration_formatted") val durationFormatted: String,
    @Json(name = "status") val status: String,
    @Json(name = "remaining_balance") val remainingBalanceAfterCall: Int
)

@JsonClass(generateAdapter = true)
data class NotificationSummaryDto(
    @Json(name = "recharge_count") val rechargeCount: Int? = null,
    @Json(name = "expiry_count") val expiryCount: Int? = null,
    @Json(name = "total_unread") val totalUnread: Int? = null
)

@JsonClass(generateAdapter = true)
data class NotificationsResponseDto(
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "status") val status: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "summary") val summary: NotificationSummaryDto? = null,
    @Json(name = "notifications") val notifications: List<NotificationAlertDto>? = null,
    @Json(name = "data") val data: List<NotificationAlertDto>? = null
)

@JsonClass(generateAdapter = true)
data class NotificationAlertDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "type") val type: String? = null,
    @Json(name = "title") val title: String? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "date") val date: String? = null,
    @Json(name = "timestamp") val timestamp: String? = null,
    @Json(name = "raw_date") val rawDate: String? = null,
    @Json(name = "is_read") val isRead: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class RechargeRequestDto(
    @Json(name = "student_id") val studentId: String,
    @Json(name = "plan_id") val planId: String,
    @Json(name = "amount_inr") val amountInr: Double,
    @Json(name = "payment_method") val paymentMethod: String = "UPI"
)

@JsonClass(generateAdapter = true)
data class RechargeResponseDto(
    @Json(name = "transaction_id") val transactionId: String,
    @Json(name = "status") val status: String,
    @Json(name = "updated_balance") val updatedBalanceMinutes: Int
)
