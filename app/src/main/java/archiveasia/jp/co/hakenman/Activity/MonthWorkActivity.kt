package archiveasia.jp.co.hakenman.Activity

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import archiveasia.jp.co.hakenman.Adapter.WorksheetAdapter
import archiveasia.jp.co.hakenman.Extension.month
import archiveasia.jp.co.hakenman.Extension.year
import archiveasia.jp.co.hakenman.Extension.yearMonth
import archiveasia.jp.co.hakenman.Model.Worksheet
import archiveasia.jp.co.hakenman.R
import kotlinx.android.synthetic.main.activity_month_work.*

const val INTENT_WORK_YEAR = "year"

class MonthWorkActivity : AppCompatActivity() {

    private lateinit var worksheet: Worksheet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_month_work)

        worksheet = intent.getParcelableExtra(INTENT_WORK_YEAR)
        val adapter = WorksheetAdapter(this, worksheet.detailWorkList)
        worksheet_listView.adapter = adapter

        title = getString(R.string.month_work_activity_title).format(worksheet.workDate.year(), worksheet.workDate.month())
    }

    companion object {

        fun newIntent(context: Context, work: Worksheet): Intent {
            val intent = Intent(context, MonthWorkActivity::class.java)
            intent.putExtra(INTENT_WORK_YEAR, work)
            return intent
        }
    }
}
