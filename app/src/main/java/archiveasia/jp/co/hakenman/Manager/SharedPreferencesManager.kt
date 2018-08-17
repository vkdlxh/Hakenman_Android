package archiveasia.jp.co.hakenman.Manager

import android.content.Context
import android.content.SharedPreferences

class PrefsManager (context: Context) {
    val PREFS_FILENAME = "jp.co.archiveasia.prefs"
    val TIMEPICKER_INTERVAL = "timepicker_interval"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    var interval: Int
        get() = prefs.getInt(TIMEPICKER_INTERVAL, 15)
        set(value) = prefs.edit().putInt(TIMEPICKER_INTERVAL, value).apply()
}