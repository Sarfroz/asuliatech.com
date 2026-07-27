package com.example.data.remote

import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit REST API interface for AsuliaTech Parent Backend
 * Base URL: https://asuliatech.com/api/parent/
 */
interface AsuliaApiService {

    /** 1. POST login/send-otp */
    @POST("login/send-otp")
    suspend fun sendLoginOtp(@Body request: SendOtpRequestDto): Response<SendOtpResponseDto>

    /** 2. POST login/verify-otp */
    @POST("login/verify-otp")
    suspend fun verifyLoginOtp(@Body request: VerifyOtpRequestDto): Response<VerifyOtpResponseDto>

    /** 3. GET profile?card_id=XYZ */
    @GET("profile")
    suspend fun getStudentProfileByCardId(@Query("card_id") cardId: String? = null): Response<StudentProfileResponseDto>

    /** 4. GET history?card_id=XYZ */
    @GET("history")
    suspend fun getHistory(@Query("card_id") cardId: String? = null): Response<HistoryResponseDto>

    /** 5. GET plans */
    @GET("plans")
    suspend fun getRechargePlans(): Response<PlansResponseDto>

    /** 6. POST recharge/initiate */
    @POST("recharge/initiate")
    suspend fun initiateRecharge(@Body request: InitiateRechargeRequestDto): Response<InitiateRechargeResponseDto>

    /** 7. POST recharge/verify */
    @POST("recharge/verify")
    suspend fun verifyRecharge(@Body request: VerifyRechargeRequestDto): Response<VerifyRechargeResponseDto>

    /** 8. GET notifications */
    @GET("notifications")
    suspend fun getNotifications(): Response<NotificationsResponseDto>

    /** 9. POST logout */
    @POST("logout")
    suspend fun logout(): Response<ApiResponse<LogoutResponseDto>>
}

