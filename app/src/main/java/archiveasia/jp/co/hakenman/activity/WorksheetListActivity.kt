package archiveasia.jp.co.hakenman.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import archiveasia.jp.co.hakenman.CustomLog
import archiveasia.jp.co.hakenman.R
import archiveasia.jp.co.hakenman.adapter.WorksheetListAdapter
import archiveasia.jp.co.hakenman.manager.WorksheetManager
import archiveasia.jp.co.hakenman.model.Worksheet
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.datepicker_dialog.view.*

class WorksheetListActivity : AppCompatActivity() {

    private lateinit var adapter: WorksheetListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = getString(R.string.main_activity_title)
        CustomLog.d("勤務表一覧画面")

        adapter = WorksheetListAdapter(listener = object : WorksheetListAdapter.WorksheetListener {
            override fun onClickItem(index: Int, worksheet: Worksheet) {
                val intent = MonthWorkActivity.newIntent(this@WorksheetListActivity, index, worksheet)
                startActivity(intent)
            }

            override fun onLongClickItem(index: Int) {
                MaterialDialog(this@WorksheetListActivity).show {
                    message(R.string.delete_worksheet_title)
                    positiveButton(R.string.delete_button) {
                        adapter.replaceWorksheetList(WorksheetManager.removeWorksheet(index))
                    }
                    negativeButton(R.string.negative_button)
                }
            }

        })
        work_recycler_view.adapter = adapter
        work_recycler_view.layoutManager = LinearLayoutManager(this)

        // FloatingActionButton リスナー設定
        fab.setOnClickListener {
            showCreateWorksheetDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        reloadListView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.setting_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_setting -> {
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun reloadListView() {
        val worksheetList = WorksheetManager.getWorksheetList()
        adapter.replaceWorksheetList(worksheetList)
    }

    private fun showCreateWorksheetDialog() {
        val dialog = MaterialDialog(this).customView(R.layout.datepicker_dialog)
        dialog.title(R.string.create_worksheet_title)
        val customView = dialog.getCustomView()
        val datePicker = customView.date_picker.apply {
            // 日のSpinnerを非表示する
            findViewById<NumberPicker>(resources.getIdentifier("day", "id", "android")).visibility = View.GONE
            // 当月まで選択できるように
            maxDate = System.currentTimeMillis()
        }
        dialog.positiveButton(R.string.positive_button) {
            val year = datePicker.year.toString()
            var month = (datePicker.month + 1).toString()
            if (month.length < 2) {
                month = "0$month"
            }
            val yearMonth = year + month
            val worksheet = WorksheetManager.createWorksheet(yearMonth)
            if (WorksheetManager.isAlreadyExistWorksheet(yearMonth)) {
                MaterialDialog(this).show {
                    message(R.string.update_worksheet_title)
                    positiveButton(R.string.positive_button) {
                        WorksheetManager.updateWorksheet(worksheet)
                        CustomLog.d("勤務表生成 : $yearMonth")
                        reloadListView()
                    }
                    negativeButton(R.string.negative_button)
                }
            } else {
                WorksheetManager.addWorksheetToJsonFile(worksheet)
                CustomLog.d("勤務表生成 : $yearMonth")
                reloadListView()
            }
        }
        dialog.negativeButton(R.string.negative_button)
        dialog.show()
    }
}
