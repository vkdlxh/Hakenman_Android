package archiveasia.jp.co.hakenman.Extension

import java.text.SimpleDateFormat
import java.util.*

fun String.createDate(): Date = SimpleDateFormat("yyyyMM").parse(this)

fun String.hourMinuteToDate(): Date = SimpleDateFormat("hh:mm").parse(this)