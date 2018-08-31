package archiveasia.jp.co.hakenman

import android.util.Log

object CustomLog {

    private const val TAG = "Custom Log"

    fun d(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, msg)
        }
    }
}