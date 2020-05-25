package archiveasia.jp.co.hakenman.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import archiveasia.jp.co.hakenman.CustomLog
import archiveasia.jp.co.hakenman.R
import archiveasia.jp.co.hakenman.activity.DailyWorkActivity.Companion.INTENT_WORKSHEET_RETURN_VALUE
import archiveasia.jp.co.hakenman.adapter.DailyWorkAdapter
import archiveasia.jp.co.hakenman.extension.month
import archiveasia.jp.co.hakenman.extension.year
import archiveasia.jp.co.hakenman.manager.CSVManager
import archiveasia.jp.co.hakenman.manager.PrefsManager
import archiveasia.jp.co.hakenman.manager.WorksheetManager
import archiveasia.jp.co.hakenman.model.Worksheet
import com.afollestad.materialdialogs.MaterialDialog
import kotlinx.android.synthetic.main.activity_monthly_work.*

class MonthlyWorkActivity : AppCompatActivity() {

    private var index: Int = -1
    private lateinit var worksheet: Worksheet

    private lateinit var dailyWorkAdapter: DailyWorkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monthly_work)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        index = intent.getIntExtra(INTENT_WORKSHEET_INDEX, index)
        worksheet = intent.getParcelableExtra(INTENT_WORKSHEET_VALUE)
        title = getString(R.string.month_work_activity_title).format(worksheet.workDate.year(), worksheet.workDate.month())

        daily_work_recycler_view.apply {
            layoutManager = LinearLayoutManager(this@MonthlyWorkActivity)
            dailyWorkAdapter = DailyWorkAdapter(worksheet.detailWorkList, object : DailyWorkAdapter.DailyWorkListener {
                override fun onClick(position: Int) {
                    val intent = DailyWorkActivity.newIntent(this@MonthlyWorkActivity, position, worksheet)
                    startActivityForResult(intent, REQUEST_WORKSHEET)
                }
            })
            adapter = dailyWorkAdapter
        }

        CustomLog.d("月勤務表画面")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.send_email_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.send_csv -> {
                val csvManager = CSVManager(this, worksheet)
                csvManager.createCSVFile()
                sendMail(csvManager.getFileUri())

                return true
            }
            R.id.send_markdown -> {
                sendMail()
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
        // TODO: 見直し
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_WORKSHEET) {
                worksheet = data!!.getParcelableExtra(INTENT_WORKSHEET_RETURN_VALUE)
                WorksheetManager.updateWorksheetWithIndex(index, worksheet)
                dailyWorkAdapter.replaceDailyWorkList(worksheet.detailWorkList)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun sendMail(fileUri: Uri? = null) {
        val to = PrefsManager(this).emailTo

        if (to.isNullOrEmpty()) {
            MaterialDialog(this).show {
                message(R.string.request_set_address_message)
                positiveButton(R.string.positive_button) {
                    val intent = Intent(this@MonthlyWorkActivity, SettingActivity::class.java)
                    startActivity(intent)
                }
                negativeButton(R.string.negative_button)
            }
        } else {
            val addresses = arrayOf(to)
            val subject = getString(R.string.month_work_activity_title).format(worksheet.workDate.year(), worksheet.workDate.month())

            val emailIntent = Intent(Intent.ACTION_SEND)

            if (fileUri == null) {
                val body = WorksheetManager.generateWorksheetToMarkdown(worksheet)
                emailIntent.putExtra(Intent.EXTRA_TEXT, body)
            }

            emailIntent.putExtra(Intent.EXTRA_EMAIL, addresses)
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
            emailIntent.type = "message/rfc822"
            startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email_type)))
        }
    }

    companion object {
        private const val REQUEST_WORKSHEET = 100
        private const val INTENT_WORKSHEET_INDEX = "worksheet_index"
        private const val INTENT_WORKSHEET_VALUE = "worksheet_value"

        fun newIntent(context: Context, index: Int, work: Worksheet): Intent {
            val intent = Intent(context, MonthlyWorkActivity::class.java)
            intent.putExtra(INTENT_WORKSHEET_INDEX, index)
            intent.putExtra(INTENT_WORKSHEET_VALUE, work)
            return intent
        }
    }
}
