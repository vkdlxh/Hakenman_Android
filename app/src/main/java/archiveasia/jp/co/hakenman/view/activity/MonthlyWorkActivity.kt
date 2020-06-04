package archiveasia.jp.co.hakenman.view.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import archiveasia.jp.co.hakenman.CustomLog
import archiveasia.jp.co.hakenman.R
import archiveasia.jp.co.hakenman.databinding.ActivityMonthlyWorkBinding
import archiveasia.jp.co.hakenman.extension.month
import archiveasia.jp.co.hakenman.extension.viewBinding
import archiveasia.jp.co.hakenman.extension.year
import archiveasia.jp.co.hakenman.manager.CSVManager
import archiveasia.jp.co.hakenman.manager.PrefsManager
import archiveasia.jp.co.hakenman.manager.WorksheetManager
import archiveasia.jp.co.hakenman.model.Worksheet
import archiveasia.jp.co.hakenman.view.activity.DailyWorkActivity.Companion.INTENT_WORKSHEET_RETURN_VALUE
import archiveasia.jp.co.hakenman.view.adapter.WorkListPagerAdapter
import archiveasia.jp.co.hakenman.view.fragment.DetailWorkFragment
import com.afollestad.materialdialogs.MaterialDialog

class MonthlyWorkActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMonthlyWorkBinding::inflate)

    lateinit var worksheet: Worksheet
    private var index: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        index = intent.getIntExtra(INTENT_WORKSHEET_INDEX, index)
        worksheet = intent.getParcelableExtra(INTENT_WORKSHEET_VALUE)
        title = getString(R.string.month_work_activity_title).format(worksheet.workDate.year(), worksheet.workDate.month())

        with (binding) {
            bottomNavigation.setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_sheet -> {
                        viewPager.currentItem = 0
                        return@setOnNavigationItemSelectedListener true
                    }
                    R.id.menu_calendar -> {
                        viewPager.currentItem = 1
                        return@setOnNavigationItemSelectedListener true
                    }
                }
                false
            }
            viewPager.adapter = WorkListPagerAdapter(this@MonthlyWorkActivity, supportFragmentManager)
            viewPager.offscreenPageLimit = 2
            viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }

                override fun onPageSelected(position: Int) {
                    bottomNavigation.menu.getItem(position).isChecked = true
                }

            })
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
                // TODO: Sheet, Calendar프래그먼트에 갱신된 리스트 보내기
                for (fragment in supportFragmentManager.fragments) {
                    if (fragment is DetailWorkFragment) {
                        fragment.replaceWorkList(worksheet)
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun showDetailWork(position: Int) {
        val intent = DailyWorkActivity.createIntent(this, position, worksheet)
        startActivityForResult(intent, REQUEST_WORKSHEET)
    }

    private fun sendMail(fileUri: Uri? = null) {
        val to = PrefsManager(this).emailTo

        if (to.isNullOrEmpty()) {
            MaterialDialog(this).show {
                message(R.string.request_set_address_message)
                positiveButton(R.string.positive_button) {
                    startActivity(SettingActivity.createInstance(this@MonthlyWorkActivity))
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

        fun createInstance(context: Context, index: Int, work: Worksheet) =
            Intent(context, MonthlyWorkActivity::class.java).apply {
                putExtra(INTENT_WORKSHEET_INDEX, index)
                putExtra(INTENT_WORKSHEET_VALUE, work)
            }
    }

    interface SheetCalendarItemClickListener {
        fun onClick(position: Int)
    }
}
