package archiveasia.jp.co.hakenman.Extension

import java.text.SimpleDateFormat
import java.util.*

fun Date.year(): String {
    return SimpleDateFormat("yyyy").format(this)
}

fun Date.month(): String {
    return SimpleDateFormat("MM").format(this)
}

fun Date.yearMonth(): String {
    return SimpleDateFormat("yyyyMM").format(this)
}
fun Date.week(): String {
    return SimpleDateFormat("E").format(this)
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