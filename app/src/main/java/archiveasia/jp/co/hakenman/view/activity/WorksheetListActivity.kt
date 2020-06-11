package archiveasia.jp.co.hakenman.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import archiveasia.jp.co.hakenman.CustomLog
import archiveasia.jp.co.hakenman.R
import archiveasia.jp.co.hakenman.databinding.ActivityWorksheetListBinding
import archiveasia.jp.co.hakenman.model.Worksheet
import archiveasia.jp.co.hakenman.view.adapter.MonthlyWorkAdapter
import archiveasia.jp.co.hakenman.viewmodel.WorksheetListViewModel
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import kotlinx.android.synthetic.main.dialog_datepicker.view.*

class WorksheetListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorksheetListBinding
    private lateinit var viewModel: WorksheetListViewModel

    private lateinit var adapter: MonthlyWorkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorksheetListBinding.inflate(layoutInflater)
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
                        viewModel.removeWorksheet(index)
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

        viewModel = ViewModelProvider(this).get(WorksheetListViewModel::class.java)
        viewModel.worksheetList.observe(this, Observer {
            adapter.replaceWorksheetList(it)
        })

        // TODO: Event処理勉強する
        viewModel.showUpdateDialog.observe(this, Observer { worksheet ->
            MaterialDialog(this).show {
                message(R.string.update_worksheet_title)
                positiveButton(R.string.positive_button) {
                    viewModel.updateWorksheet(worksheet)
                }
                negativeButton(R.string.negative_button)
            }
        })

        CustomLog.d("勤務表一覧画面")
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
            val month = (datePicker.month + 1).toString()
            viewModel.createWorksheet(year, month)
        }
        dialog.negativeButton(R.string.negative_button)
        dialog.show()
    }

    companion object {

        fun createInstance(context: Context) = Intent(context, WorksheetListActivity::class.java)

    }
}
