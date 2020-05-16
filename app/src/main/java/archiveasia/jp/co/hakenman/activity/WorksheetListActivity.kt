package archiveasia.jp.co.hakenman.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import archiveasia.jp.co.hakenman.adapter.WorksheetListAdapter
import archiveasia.jp.co.hakenman.CreateWorksheetDialog
import archiveasia.jp.co.hakenman.CustomLog
import archiveasia.jp.co.hakenman.manager.WorksheetManager
import archiveasia.jp.co.hakenman.R
import kotlinx.android.synthetic.main.activity_main.*

class WorksheetListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adaptListView()

        // FloatingActionButton リスナー設定
        fab.setOnClickListener { view ->
            showCreateWorksheetDialog()
        }
        title = getString(R.string.main_activity_title)

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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
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
        val dialog = CreateWorksheetDialog()
        dialog.show(supportFragmentManager, "tag")
        supportFragmentManager.executePendingTransactions()
        dialog.dialog?.setOnDismissListener {
            reloadListView()
        }
    }

    private fun reloadListView() {
        adaptListView()
        work_listView.invalidateViews()
    }

    private fun adaptListView() {
        val worksheetList = WorksheetManager.getWorksheetList()

        // 勤務表がない場合、中央にメッセージを表示する
        if (worksheetList.isEmpty()) {
            worksheet_info_textView.visibility = View.VISIBLE
        } else {
            worksheet_info_textView.visibility = View.INVISIBLE
        }

        val adapter = WorksheetListAdapter(this, worksheetList)

        work_listView.adapter = adapter
        work_listView.setOnItemClickListener { parent, view, position, id ->
            val intent = MonthWorkActivity.newIntent(this, position, worksheetList[position])
            startActivity(intent)
        }

        work_listView.setOnItemLongClickListener { parent, view, position, id ->
            showAlertDialog(getString(R.string.delete_worksheet_title), getString(R.string.delete_button)) {
                adapter.remove(position)
                reloadListView()
            }
            true
        }
    }
}
