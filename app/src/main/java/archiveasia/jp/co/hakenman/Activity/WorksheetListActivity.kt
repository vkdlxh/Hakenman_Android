package archiveasia.jp.co.hakenman.Activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import archiveasia.jp.co.hakenman.R
import archiveasia.jp.co.hakenman.Adapter.WorksheetListAdapter
import archiveasia.jp.co.hakenman.CustomLog
import archiveasia.jp.co.hakenman.Manager.WorksheetManager
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
        val alertDialog = AlertDialog.Builder(this)
        var editTextAge: EditText? = null

        with (alertDialog) {
            setTitle(getString(R.string.create_worksheet_title))

            editTextAge = EditText(context)
            editTextAge!!.hint="201808(yyyyDD)"
            editTextAge!!.inputType = InputType.TYPE_CLASS_NUMBER

            setPositiveButton(getString(R.string.positive_button)) {
                dialog, whichButton ->
                val editTextValue = editTextAge!!.text
                // TODO: validateする他の方法考えてみる
                if (editTextValue.isNullOrBlank()) {
                    Toast.makeText(this@WorksheetListActivity, getString(R.string.empty_error_message), Toast.LENGTH_SHORT).show()
                    showCreateWorksheetDialog()
                } else if (editTextValue.trim().length != 6) {
                    Toast.makeText(this@WorksheetListActivity, getString(R.string.invalidate_error_message), Toast.LENGTH_SHORT).show()
                    showCreateWorksheetDialog()
                } else {
                    var yearMonth = editTextAge!!.text.toString()
                    var worksheet = WorksheetManager.createWorksheet(yearMonth)

                    if (WorksheetManager.isAlreadyExistWorksheet(yearMonth)) {
                        showAlertDialog(getString(R.string.update_worksheet_title), getString(R.string.positive_button)) {
                            WorksheetManager.updateWorksheet(worksheet)
                            CustomLog.d("勤務表生成 : " + yearMonth)
                            reloadListView()
                        }
                    } else {
                        WorksheetManager.addWorksheetToJsonFile(worksheet)
                        CustomLog.d("勤務表生成 : " + yearMonth)
                        reloadListView()
                    }
                    dialog.dismiss()
                }

            }

            setNegativeButton(R.string.negative_button) {
                dialog, whichButton ->
                dialog.dismiss()
            }
        }

        val dialog = alertDialog.create()
        dialog.setView(editTextAge)
        dialog.show()
    }

    private fun reloadListView() {
        adaptListView()
        work_listView.invalidateViews()
    }

    private fun adaptListView() {
        WorksheetManager.loadLocalWorksheet()

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
