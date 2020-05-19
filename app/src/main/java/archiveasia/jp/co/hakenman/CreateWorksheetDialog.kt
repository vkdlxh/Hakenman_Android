package archiveasia.jp.co.hakenman

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import archiveasia.jp.co.hakenman.manager.WorksheetManager
import kotlinx.android.synthetic.main.datepicker_dialog.*
import kotlinx.android.synthetic.main.datepicker_dialog.view.*

class CreateWorksheetDialog(private val listener: WorksheetDialogListener): DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.datepicker_dialog, container,
                false)
        // 日のSpinnerを非表示する
        rootView.date_picker.findViewById<NumberPicker>(resources.getIdentifier("day", "id", "android")).visibility = View.GONE
        // 当月まで選択できるように
        rootView.date_picker.maxDate = System.currentTimeMillis()
        val positiveButton = rootView.findViewById<Button>(R.id.button_positive)
        positiveButton.setOnClickListener { createWorksheet() }
        val negativeButton = rootView.findViewById<Button>(R.id.button_negative)
        negativeButton.setOnClickListener { dismiss() }

        return rootView
    }

    private fun createWorksheet() {
        val year = date_picker.year.toString()
        var month = (date_picker.month + 1).toString()
        if (month.length < 2) {
            month = "0$month"
        }
        val yearMonth = year + month
        val worksheet = WorksheetManager.createWorksheet(yearMonth)
        if (WorksheetManager.isAlreadyExistWorksheet(yearMonth)) {
            showAlertDialog(getString(R.string.update_worksheet_title), getString(R.string.positive_button)) {
                WorksheetManager.updateWorksheet(worksheet)
                CustomLog.d("勤務表生成 : $yearMonth")
                dismiss()
                listener.createdWorksheet()
            }
        } else {
            WorksheetManager.addWorksheetToJsonFile(worksheet)
            CustomLog.d("勤務表生成 : $yearMonth")
            dismiss()
            listener.createdWorksheet()
        }
    }

    private fun showAlertDialog(title: String, btn: String, completion: () -> Unit) {
        val alertDialog = AlertDialog.Builder(context!!)
        with (alertDialog) {

            val titleView = TextView(context)
            titleView.text = title
            titleView.gravity = Gravity.CENTER_HORIZONTAL
            titleView.textSize = 20F
            titleView.setTextColor(resources.getColor(R.color.colorBlack))
            setView(titleView)

            setPositiveButton(btn) {
                dialog, whichButton ->
                completion()
            }

            setNegativeButton(getString(R.string.negative_button)) {
                dialog, whichButton ->
                dialog.dismiss()
            }
        }
        val dialog = alertDialog.create()
        dialog.show()
    }

    interface WorksheetDialogListener {
        fun createdWorksheet()
    }
}