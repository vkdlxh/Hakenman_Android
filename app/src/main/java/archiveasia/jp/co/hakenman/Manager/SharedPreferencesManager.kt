package archiveasia.jp.co.hakenman.Manager

import android.content.Context
import android.content.SharedPreferences

class PrefsManager (context: Context) {
    companion object {
        const val PREFS_FILENAME = "jp.co.archiveasia.prefs"
        const val TIMEPICKER_INTERVAL = "timepicker_interval"
        const val EMAIL_TO = "email_to"
        const val DEFAULT_BEGIN_TIME = "defalut_begin_time"
        const val DEFAULT_END_TIME = "defalut_end_time"
    }

    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    var interval: Int
        get() = prefs.getInt(TIMEPICKER_INTERVAL, 15)
        set(value) = prefs.edit().putInt(TIMEPICKER_INTERVAL, value).apply()

    var emailTo: String?
        get() = prefs.getString(EMAIL_TO, null)
        set(value) = prefs.edit().putString(EMAIL_TO, value).apply()

    var defaultBeginTime: String
        get() = prefs.getString(DEFAULT_BEGIN_TIME, "09:30")
        set(value) = prefs.edit().putString(DEFAULT_BEGIN_TIME, value).apply()

    var defaultEndTime: String
        get() = prefs.getString(DEFAULT_END_TIME, "18:30")
        set(value) = prefs.edit().putString(DEFAULT_END_TIME, value).apply()
}