package archiveasia.jp.co.hakenman.Activity

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import archiveasia.jp.co.hakenman.Adapter.WorksheetAdapter
import archiveasia.jp.co.hakenman.CustomLog
import archiveasia.jp.co.hakenman.Extension.month
import archiveasia.jp.co.hakenman.Extension.year
import archiveasia.jp.co.hakenman.Manager.WorksheetManager
import archiveasia.jp.co.hakenman.Model.Worksheet
import archiveasia.jp.co.hakenman.R
import kotlinx.android.synthetic.main.activity_month_work.*

const val INTENT_WORKSHEET_INDEX = "worksheet_index"
const val INTENT_WORKSHEET_VALUE = "worksheet_value"

class MonthWorkActivity : AppCompatActivity() {

    private var index: Int = -1
    private lateinit var worksheet: Worksheet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_month_work)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        index = intent.getIntExtra(INTENT_WORKSHEET_INDEX, index)
        worksheet = intent.getParcelableExtra(INTENT_WORKSHEET_VALUE)
        adaptListView()
        title = getString(R.string.month_work_activity_title).format(worksheet.workDate.year(), worksheet.workDate.month())

        CustomLog.d("月勤務表画面")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_send_mail -> {
                // TODO: To, Fromが登録されているか確認した後、登録されているとメール送付
                return true
            }
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                worksheet = data!!.getParcelableExtra(INTENT_WORKSHEET_RETURN_VALUE)
                WorksheetManager.updateWorksheetWithIndex(index, worksheet)
                adaptListView()
                worksheet_listView.invalidateViews()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun adaptListView() {
        val adapter = WorksheetAdapter(this, worksheet.detailWorkList)
        worksheet_listView.adapter = adapter
        worksheet_listView.setOnItemClickListener { parent, view, position, id ->
            val intent = DayWorksheetActivity.newIntent(this, position, worksheet)
            startActivityForResult(intent, 100) // １００は臨時値
        }
    }

    companion object {

        fun newIntent(context: Context, index: Int, work: Worksheet): Intent {
            val intent = Intent(context, MonthWorkActivity::class.java)
            intent.putExtra(INTENT_WORKSHEET_INDEX, index)
            intent.putExtra(INTENT_WORKSHEET_VALUE, work)
            return intent
        }
    }
}
