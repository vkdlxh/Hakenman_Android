package archiveasia.jp.co.hakenman.Extension

import java.text.SimpleDateFormat
import java.util.*

fun String.createDate(): Date {
    return SimpleDateFormat("yyyyMM").parse(this)
}