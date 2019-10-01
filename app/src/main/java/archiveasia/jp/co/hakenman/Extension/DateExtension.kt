package archiveasia.jp.co.hakenman.Extension

import java.text.SimpleDateFormat
import java.util.*

fun Date.year(): String = SimpleDateFormat("yyyy").format(this)

fun Date.month(): String = SimpleDateFormat("MM").format(this)

fun Date.yearMonth(): String = SimpleDateFormat("yyyyMM").format(this)

fun Date.week(): String = SimpleDateFormat("E").format(this)

fun Date.hourMinute(): String = SimpleDateFormat("HH:mm").format(this)

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