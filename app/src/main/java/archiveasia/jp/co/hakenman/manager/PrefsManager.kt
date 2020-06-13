package archiveasia.jp.co.hakenman.manager

import android.content.Context
import android.content.SharedPreferences

class PrefsManager(context: Context) {
    companion object {
        private const val PREFS_FILENAME = "jp.co.archiveasia.prefs"
        private const val TIMEPICKER_INTERVAL = "timepicker_interval"
        private const val EMAIL_TO = "email_to"
        private const val DEFAULT_BEGIN_TIME = "default_begin_time"
        private const val DEFAULT_END_TIME = "default_end_time"
        private const val THEME = "theme"
        private const val IS_NEED_TUTORIAL = "IS_NEED_TUTORIAL"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    var interval: Int
        get() = prefs.getInt(TIMEPICKER_INTERVAL, 1)
        set(value) = prefs.edit().putInt(TIMEPICKER_INTERVAL, value).apply()

    var emailTo: String?
        get() = prefs.getString(EMAIL_TO, null)
        set(value) = prefs.edit().putString(EMAIL_TO, value).apply()

    var defaultBeginTime: String
        get() = prefs.getString(DEFAULT_BEGIN_TIME, "09:30")!!
        set(value) = prefs.edit().putString(DEFAULT_BEGIN_TIME, value).apply()

    var defaultEndTime: String
        get() = prefs.getString(DEFAULT_END_TIME, "18:30")!!
        set(value) = prefs.edit().putString(DEFAULT_END_TIME, value).apply()

    var theme: String
        get() = prefs.getString(THEME, ThemeUtil.DEFAULT_MODE)!!
        set(value) = prefs.edit().putString(THEME, value).apply()

    var isNeedTutorial: Boolean
        get() = prefs.getBoolean(IS_NEED_TUTORIAL, true)
        set(value) = prefs.edit().putBoolean(IS_NEED_TUTORIAL, value).apply()
}