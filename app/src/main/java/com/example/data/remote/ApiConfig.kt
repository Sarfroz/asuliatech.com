package com.example.data.remote

/**
 * Configuration manager for AsuliaTech Parent REST API Integration.
 */
object ApiConfig {
    // Live Backend BASE_URL
    var baseUrl: String = "https://asuliatech.com/api/parent/"
        set(value) {
            field = if (value.endsWith("/")) value else "$value/"
            ApiClient.resetClient()
        }

    // Dynamic Auth Token for Bearer Authentication
    var authToken: String? = null

    // Strictly connect with LIVE BACKEND SERVER and disable mock data
    var isLiveApiEnabled: Boolean = true
    var useMock: Boolean = false
}

