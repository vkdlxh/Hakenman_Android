package archiveasia.jp.co.hakenman

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.view.View
import android.widget.NumberPicker
import android.widget.TimePicker
import androidx.annotation.StringRes
import archiveasia.jp.co.hakenman.extension.hourMinute
import archiveasia.jp.co.hakenman.extension.hourMinuteToDate
import archiveasia.jp.co.hakenman.manager.PrefsManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import kotlinx.android.synthetic.main.timepicker_dialog.view.*
import java.util.*

class TimePickerDialog(val context: Context) {

    enum class WorkTimeType {
        BEGIN_TIME, END_TIME, BREAK_TIME
    }

    private val dialog: MaterialDialog = MaterialDialog(context).customView(R.layout.timepicker_dialog)
    private val prefsManager = PrefsManager(context)
    private var title: Int? = null
    private var positive: Int = R.string.positive_button
    private var negative: Int = R.string.negative_button
    private var interval = BREAK_TIME_INTERVAL

    fun title(@StringRes res: Int): TimePickerDialog = apply {
        title = res
        return this
    }

    fun positiveButton(@StringRes res: Int): TimePickerDialog = apply {
        positive = res
        return this
    }

    fun negativeButton(@StringRes res: Int): TimePickerDialog = apply {
        negative = res
        return this
    }

    fun show(time: String, workTimeType: WorkTimeType, callback: (String) -> Unit) {
        val view = dialog.getCustomView()
        view.time_picker.apply {
            setIs24HourView(true)

            val calendar = Calendar.getInstance()
            var value: Date
            var hour: Int
            var minute: Int

            when (workTimeType) {
                WorkTimeType.BEGIN_TIME -> {
                    value = prefsManager.defaultBeginTime.hourMinuteToDate()
                    calendar.time = value
                    hour = calendar.get(Calendar.HOUR_OF_DAY)
                    minute = calendar.get(Calendar.MINUTE)
                }
                WorkTimeType.END_TIME -> {
                    value = prefsManager.defaultEndTime.hourMinuteToDate()
                    calendar.time = value
                    hour = calendar.get(Calendar.HOUR_OF_DAY)
                    minute = calendar.get(Calendar.MINUTE)
                }
                WorkTimeType.BREAK_TIME -> {
                    hour = 1
                    minute = 0
                }
            }

            // 値が存在している場合
            if (time.isNotEmpty()) {
                value = time.hourMinuteToDate()
                calendar.time = value
                hour = calendar.get(Calendar.HOUR_OF_DAY)
                minute = calendar.get(Calendar.MINUTE)
            }

            interval = if (workTimeType == WorkTimeType.BREAK_TIME) {
                BREAK_TIME_INTERVAL
            } else {
                prefsManager.interval
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.hour = hour
                this.minute = minute / interval
            } else {
                this.currentHour = hour
                this.currentMinute = minute / interval
            }
            setTimePickerInterval(this)
        }
        dialog.title(title)
        dialog.positiveButton(positive) {
            callback(getPickerTime(view).hourMinute())
        }
        dialog.negativeButton(negative)
        dialog.show()
    }

    private fun setTimePickerInterval(timePicker: TimePicker) {
        val minuteID = Resources.getSystem().getIdentifier("minute", "id", "android")
        val minutePicker = timePicker.findViewById<NumberPicker>(minuteID)

        val numValue = 60 / interval
        val displayedValue = arrayListOf<String>()
        for (i in 0 until numValue) {
            val value = i * interval
            displayedValue.add(i, value.toString())
        }
        minutePicker.minValue = 0
        minutePicker.maxValue = numValue - 1
        minutePicker.displayedValues = displayedValue.toTypedArray()
    }

    private fun getPickerTime(view: View): Date {
        val hour = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.time_picker.hour
        } else {
            view.time_picker.currentHour
        }
        val minute = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.time_picker.minute * interval
        } else {
            view.time_picker.currentMinute * interval
        }

        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        return cal.time
    }

    companion object {
        private const val BREAK_TIME_INTERVAL = 5
    }
}