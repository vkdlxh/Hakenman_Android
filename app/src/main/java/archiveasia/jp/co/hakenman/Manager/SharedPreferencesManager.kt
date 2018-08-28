package archiveasia.jp.co.hakenman.Manager

import android.content.Context
import android.content.SharedPreferences

class PrefsManager (context: Context) {
    val PREFS_FILENAME = "jp.co.archiveasia.prefs"
    val TIMEPICKER_INTERVAL = "timepicker_interval"
    val EMAIL_FROM = "email_from"
    val EMAIL_TO = "email_to"
    val emptyMessage = "登録してください。"


    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    var interval: Int
        get() = prefs.getInt(TIMEPICKER_INTERVAL, 15)
        set(value) = prefs.edit().putInt(TIMEPICKER_INTERVAL, value).apply()

    var emailFrom: String?
        get() = prefs.getString(EMAIL_FROM, null)
        set(value) = prefs.edit().putString(EMAIL_FROM, value).apply()

    var emailTo: String?
        get() = prefs.getString(EMAIL_TO, null)
        set(value) = prefs.edit().putString(EMAIL_TO, value).apply()
}