package archiveasia.jp.co.hakenman.Activity

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import archiveasia.jp.co.hakenman.Adapter.WorksheetAdapter
import archiveasia.jp.co.hakenman.Model.Worksheet
import archiveasia.jp.co.hakenman.R
import kotlinx.android.synthetic.main.activity_month_work.*

const val INTENT_WORK_YEAR = "year"

class MonthWorkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_month_work)

        val item = intent.getParcelableExtra<Worksheet>(INTENT_WORK_YEAR)
        val adapter = WorksheetAdapter(this, item.detailWorkList)
        worksheet_listView.adapter = adapter
    }

    companion object {

        fun newIntent(context: Context, work: Worksheet): Intent {
            val intent = Intent(context, MonthWorkActivity::class.java)
            intent.putExtra(INTENT_WORK_YEAR, work)
            return intent
        }
    }
}
