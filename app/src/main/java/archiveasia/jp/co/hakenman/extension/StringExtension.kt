package archiveasia.jp.co.hakenman.extension

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun String.createDate(): Date {
    return SimpleDateFormat("yyyyMM", Locale.getDefault()).parse(this)
}

fun String.hourMinuteToDate(): Date = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(this)