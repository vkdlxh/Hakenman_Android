package archiveasia.jp.co.hakenman.activity

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import archiveasia.jp.co.hakenman.CreateWorksheetDialog
import archiveasia.jp.co.hakenman.CustomLog
import archiveasia.jp.co.hakenman.R
import archiveasia.jp.co.hakenman.adapter.WorksheetListAdapter
import archiveasia.jp.co.hakenman.manager.WorksheetManager
import archiveasia.jp.co.hakenman.model.Worksheet
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.Locale

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
                showAlertDialog(getString(R.string.delete_worksheet_title), getString(R.string.delete_button)) {
                    adapter.replaceWorksheetList(WorksheetManager.removeWorksheet(index))
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

    private fun showAlertDialog(title: String, btn: String, completion: () -> Unit) {
        val alertDialog = AlertDialog.Builder(this)
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

    private fun showCreateWorksheetDialog() {
        val dialog = CreateWorksheetDialog(object : CreateWorksheetDialog.WorksheetDialogListener{
            override fun createdWorksheet() {
                CustomLog.d("createdWorksheet")
                reloadListView()
            }
        })
        dialog.show(supportFragmentManager, "tag")
        supportFragmentManager.executePendingTransactions()
    }

    private fun reloadListView() {
        val worksheetList = WorksheetManager.getWorksheetList()
        adapter.replaceWorksheetList(worksheetList)
    }
}
