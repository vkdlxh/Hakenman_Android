package archiveasia.jp.co.hakenman.view.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import archiveasia.jp.co.hakenman.CustomLog
import archiveasia.jp.co.hakenman.R
import archiveasia.jp.co.hakenman.databinding.ActivityWorksheetListBinding
import archiveasia.jp.co.hakenman.databinding.DialogDatepickerBinding
import archiveasia.jp.co.hakenman.manager.WorksheetManager
import archiveasia.jp.co.hakenman.model.Worksheet
import archiveasia.jp.co.hakenman.view.adapter.MonthlyWorkAdapter
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class WorksheetListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorksheetListBinding
    private lateinit var adapter: MonthlyWorkAdapter
    private lateinit var analytics: FirebaseAnalytics

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorksheetListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = getString(R.string.main_activity_title)
        analytics = Firebase.analytics
        analytics.setCurrentScreen(this, "勤務表リスト画面", null)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

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
        val binding = DialogDatepickerBinding.inflate(LayoutInflater.from(this))
        val dialog = MaterialDialog(this).customView(null, binding.root)
        dialog.title(R.string.create_worksheet_title)
        val datePicker = binding.datePicker.apply {
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
