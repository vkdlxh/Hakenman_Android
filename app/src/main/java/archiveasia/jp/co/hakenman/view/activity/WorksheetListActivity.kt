package archiveasia.jp.co.hakenman.view.activity

import android.content.Context
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
import archiveasia.jp.co.hakenman.databinding.ActivityWorksheetListBinding
import archiveasia.jp.co.hakenman.extension.viewBinding
import archiveasia.jp.co.hakenman.manager.WorksheetManager
import archiveasia.jp.co.hakenman.model.Worksheet
import archiveasia.jp.co.hakenman.view.adapter.MonthlyWorkAdapter
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import kotlinx.android.synthetic.main.dialog_datepicker.view.*

class WorksheetListActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityWorksheetListBinding::inflate)

    private lateinit var adapter: MonthlyWorkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        title = getString(R.string.main_activity_title)

        adapter = MonthlyWorkAdapter(listener = object : MonthlyWorkAdapter.MonthlyWorkListener {
            override fun onClickItem(index: Int, worksheet: Worksheet) {
                val intent = MonthlyWorkActivity.createInstance(this@WorksheetListActivity, index, worksheet)
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
        binding.workRecyclerView.adapter = adapter
        binding.workRecyclerView.layoutManager = LinearLayoutManager(this)

        // FloatingActionButton リスナー設定
        binding.fab.setOnClickListener {
            showCreateWorksheetDialog()
        }
        CustomLog.d("勤務表一覧画面")
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
                startActivity(SettingActivity.createInstance(this))
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
        val dialog = MaterialDialog(this).customView(R.layout.dialog_datepicker)
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

    companion object {

        fun createInstance(context: Context) = Intent(context, WorksheetListActivity::class.java)

    }
}
