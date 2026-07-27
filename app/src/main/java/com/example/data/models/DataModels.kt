package com.example.data.models

data class ParentUser(
    val id: String,
    val name: String,
    val mobileNumber: String,
    val email: String,
    val currentPlan: String,
    val walletBalanceMinutes: Int,
    val linkedStudentsCount: Int
)

data class Student(
    val id: String,
    val name: String,
    val initial: String,
    val className: String,
    val schoolName: String,
    val walletBalanceMinutes: Int,
    val dailyLimitMinutes: Int = 10,
    val dayMinutesLeft: Int = 10,
    val usedTodayMinutes: Int = 0,
    val planName: String = "Active Plan",
    val expiry: String = "Unlimited",
    val cardId: String = "A3210024",
    val registeredMobile: String = "9709995999",
    val mobile1: String = "",
    val mobile2: String = "",
    val mobile3: String = "",
    val callScheduleDay: String = "Everyday",
    val callScheduleTime: String = "7am-10pm",
    val status: String = "Active"
)

data class WalletInfo(
    val balanceMinutes: Int,
    val status: String, // "Active", "Low Balance", "Expired"
    val dailyLimitMinutes: Int,
    val dayMinutesLeft: Int = 10,
    val usedTodayMinutes: Int,
    val remainingMinutes: Int,
    val planName: String,
    val expiry: String,
    val callScheduleDay: String = "Everyday",
    val callScheduleTime: String = "7am-10pm"
)

data class CallSchedule(
    val title: String = "CALL SCHEDULE",
    val dayText: String = "Everyday",
    val timeText: String = "7am-10pm"
) {
    val timing: String
        get() = "${dayText.uppercase()} • ${timeText.uppercase()}"

    fun isAvailableNow(): Boolean {
        return isDayMatching() && isTimeMatching()
    }

    fun isDayMatching(): Boolean {
        if (dayText.isBlank() || dayText.equals("Everyday", ignoreCase = true) || dayText.equals("Daily", ignoreCase = true)) {
            return true
        }

        val calendar = java.util.Calendar.getInstance()
        val dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
        val dayNamesFull = listOf("", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        val dayNamesShort = listOf("", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

        val currentFull = dayNamesFull.getOrElse(dayOfWeek) { "" }
        val currentShort = dayNamesShort.getOrElse(dayOfWeek) { "" }

        val lowerDay = dayText.lowercase()

        if (lowerDay.contains("-") || lowerDay.contains("–")) {
            val parts = lowerDay.replace("–", "-").split("-").map { it.trim() }
            if (parts.size == 2) {
                val startDayIndex = getDayIndex(parts[0])
                val endDayIndex = getDayIndex(parts[1])
                val currentDayIndex = getDayIndex(currentShort)
                if (startDayIndex > 0 && endDayIndex > 0 && currentDayIndex > 0) {
                    if (startDayIndex <= endDayIndex) {
                        return currentDayIndex in startDayIndex..endDayIndex
                    } else {
                        return currentDayIndex >= startDayIndex || currentDayIndex <= endDayIndex
                    }
                }
            }
        }

        return lowerDay.contains(currentFull.lowercase()) || lowerDay.contains(currentShort.lowercase())
    }

    private fun getDayIndex(str: String): Int {
        val s = str.lowercase()
        return when {
            s.startsWith("mon") -> 1
            s.startsWith("tue") -> 2
            s.startsWith("wed") -> 3
            s.startsWith("thu") -> 4
            s.startsWith("fri") -> 5
            s.startsWith("sat") -> 6
            s.startsWith("sun") -> 7
            else -> 0
        }
    }

    fun isTimeMatching(): Boolean {
        if (timeText.isBlank()) return true

        val (startMin, endMin) = parseTimeRange(timeText) ?: return true
        val calendar = java.util.Calendar.getInstance()
        val currentMin = calendar.get(java.util.Calendar.HOUR_OF_DAY) * 60 + calendar.get(java.util.Calendar.MINUTE)

        return if (startMin <= endMin) {
            currentMin in startMin..endMin
        } else {
            currentMin >= startMin || currentMin <= endMin
        }
    }

    private fun parseTimeRange(timeStr: String): Pair<Int, Int>? {
        try {
            val cleanStr = timeStr.lowercase().replace("–", "-").replace("—", "-")
            val parts = cleanStr.split("-").map { it.trim() }
            if (parts.size == 2) {
                val startMins = parseSingleTimeMinutes(parts[0])
                val endMins = parseSingleTimeMinutes(parts[1])
                if (startMins != null && endMins != null) {
                    return Pair(startMins, endMins)
                }
            }
        } catch (_: Exception) {}
        return null
    }

    private fun parseSingleTimeMinutes(str: String): Int? {
        val regex = Regex("(\\d{1,2})(?::(\\d{2}))?\\s*(am|pm)?")
        val match = regex.find(str.lowercase()) ?: return null
        var hour = match.groupValues[1].toIntOrNull() ?: return null
        val min = match.groupValues[2].toIntOrNull() ?: 0
        val ampm = match.groupValues[3]

        if (ampm == "pm" && hour < 12) hour += 12
        if (ampm == "am" && hour == 12) hour = 0

        return hour * 60 + min
    }
}

enum class CallStatus {
    COMPLETED, MISSED, IN_PROGRESS
}

data class CallLog(
    val id: String,
    val studentId: String,
    val studentName: String,
    val dateTime: String,
    val durationMinutes: Int,
    val durationFormatted: String,
    val status: CallStatus,
    val remainingBalanceAfterCall: Int
)

enum class NotificationType {
    RECHARGE_REMINDER, CALL_REMINDER, PLAN_EXPIRY, SYSTEM_ALERT
}

data class NotificationAlert(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val timestamp: String,
    val isRead: Boolean = false,
    val actionUrl: String? = null
)

data class RechargePlan(
    val id: String,
    val minutes: Int,
    val title: String,
    val priceInr: Int,
    val description: String,
    val tag: String? = null, // e.g. "Popular", "Best Value"
    val benefits: List<String> = emptyList(),
    val category: String = "Monthly", // "Monthly" or "Wallet"
    val dailyQuota: String? = null, // e.g. "5 Min/Day - 30 Days Validity"
    val totalQuotaBadge: String? = null, // e.g. "150 total mins"
    val isPopular: Boolean = false
)

data class PaymentTransaction(
    val id: String,
    val studentName: String,
    val planTitle: String,
    val amountInr: Int,
    val date: String,
    val status: String = "SUCCESS", // "SUCCESS", "PENDING", "FAILED"
    val paymentMethod: String = "UPI / Cashfree",
    val txnId: String,
    val minutesAdded: Int,
    val cardId: String = ""
)

data class HistorySummary(
    val successfulCount: Int = 0,
    val totalSpent: Int = 0,
    val pendingCount: Int = 0
)

