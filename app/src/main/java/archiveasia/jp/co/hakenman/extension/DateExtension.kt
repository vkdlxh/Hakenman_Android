package archiveasia.jp.co.hakenman.extension

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Date.year(): String = SimpleDateFormat("yyyy", Locale.getDefault()).format(this)

fun Date.month(): String = SimpleDateFormat("MM", Locale.getDefault()).format(this)

fun Date.yearMonth(): String = SimpleDateFormat("yyyyMM", Locale.getDefault()).format(this)

fun Date.week(): String = SimpleDateFormat("E", Locale.getDefault()).format(this)

fun Date.dayOfWeek(): Int {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar.get(Calendar.DAY_OF_WEEK)
}

fun Date.hourMinute(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(this)

fun Date.day(): String = SimpleDateFormat("dd", Locale.getDefault()).format(this)

fun Date.isHoliday(): Boolean {
    val calendar = Calendar.getInstance()
    calendar.time = this

    return when (calendar.get(Calendar.DAY_OF_WEEK)) {
        1, 7 -> {
            true
        }
        else -> {
            false
        }
    }

}

fun Date.hourMinuteToDouble(): Double {
    val calendar = Calendar.getInstance()
    calendar.time = this
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    return hour.toDouble() + (minute.toDouble() / 60)
}