package archiveasia.jp.co.hakenman.Extension

import java.text.SimpleDateFormat
import java.util.*

fun Date.getWeek(): String {
    var week = SimpleDateFormat("E").format(this)
    return week
}

fun Date.isHoliday(): Boolean {
    var calendar = Calendar.getInstance()
    calendar.time = this
    val weekOfYear = calendar.get(Calendar.DAY_OF_WEEK)

    when (weekOfYear) {
        1, 7 -> {
            return true
        }
        else -> {
            return false
        }
    }

}