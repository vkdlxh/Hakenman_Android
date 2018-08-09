package archiveasia.jp.co.hakenman.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import archiveasia.jp.co.hakenman.R
import archiveasia.jp.co.hakenman.Adapter.WorkAdapter
import archiveasia.jp.co.hakenman.Manager.WorksheetManager
import archiveasia.jp.co.hakenman.Model.Work
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val testJson = """
            [
            {
            "workDate": "2018/05/24 19:31:02",
            "workTimeSum": 150.25,
            "workDaySum": 20,
            "detailWorkList": [
                {
                "workYear": 2018,
                "workMonth": 08,
                "workDay": 25,
                "workWeek": "月",
                "workFlag": true,
                "beginTime": "2018/05/24 09:00:02",
                "endTime": "2018/05/24 18:31:02",
                "breakTime": 1,
                "note": "note1"
                },
                {
                "workYear": 2018,
                "workMonth": 08,
                "workDay": 25,
                "workWeek": "月",
                "workFlag": true,
                "beginTime": "2018/05/24 09:00:02",
                "endTime": "2018/05/24 18:31:02",
                "breakTime": 1,
                "note": "note1"
                },
                {
                "workYear": 2018,
                "workMonth": 08,
                "workDay": 25,
                "workWeek": "月",
                "workFlag": true,
                "beginTime": "2018/05/24 09:00:02",
                "endTime": "2018/05/24 18:31:02",
                "breakTime": 1,
                "note": "note1"
                }
            ]
            },
            {
            "workDate": "2018/05/24 19:31:02",
            "workTimeSum": 150.25,
            "workDaySum": 20,
            "detailWorkList": [
                {
                "workYear": 2018,
                "workMonth": 08,
                "workDay": 25,
                "workWeek": "月",
                "workFlag": true,
                "beginTime": "2018/05/24 09:00:02",
                "endTime": "2018/05/24 18:31:02",
                "breakTime": 1,
                "note": "note1"
                },
                {
                "workYear": 2018,
                "workMonth": 08,
                "workDay": 25,
                "workWeek": "月",
                "workFlag": true,
                "beginTime": "2018/05/24 09:00:02",
                "endTime": "2018/05/24 18:31:02",
                "breakTime": 1,
                "note": "note1"
                },
                {
                "workYear": 2018,
                "workMonth": 08,
                "workDay": 25,
                "workWeek": "月",
                "workFlag": true,
                "beginTime": "2018/05/24 09:00:02",
                "endTime": "2018/05/24 18:31:02",
                "breakTime": 1,
                "note": "note1"
                }
            ]
            }
            ]
            """

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // テストデータをリストビューに設定
        val gson = GsonBuilder().setDateFormat("yyyy/MM/dd HH:mm:ss").create()
        val workList = gson.fromJson(testJson, Array<Work>::class.java)
        val adapter = WorkAdapter(this, workList)

        work_listView.adapter = adapter
        work_listView.setOnItemClickListener { parent, view, position, id ->
            val intent = MonthWorkActivity.newIntent(this, workList[position])
            startActivity(intent)
        }

        // FloatingActionButton リスナー設定
        fab.setOnClickListener { view ->
            showCreateCategoryDialog()
        }
    }

    private fun showCreateCategoryDialog() {
        val alertDialog = AlertDialog.Builder(this)
        var editTextAge: EditText? = null

        with (alertDialog) {
            setTitle("勤務表生成")

            editTextAge = EditText(context)
            editTextAge!!.hint="201808(yyyyDD)"
            editTextAge!!.inputType = InputType.TYPE_CLASS_NUMBER

            setPositiveButton("確認") {
                dialog, whichButton ->
                val editTextValue = editTextAge!!.text
                // TODO: 공란, 형식 체크
                if (editTextValue.isNullOrBlank()) {
                    Toast.makeText(this@MainActivity, "空欄なく入力してください。", Toast.LENGTH_SHORT).show()
                    showCreateCategoryDialog()
                } else if (editTextValue.trim().length != 6) {
                    Toast.makeText(this@MainActivity, "正しい値を入力してください。", Toast.LENGTH_SHORT).show()
                    showCreateCategoryDialog()
                } else {
                    // TODO: 근무표 추가
                    // 1. create work
                    var yearMonth = editTextAge!!.text.toString()
                    WorksheetManager().createWorksheet(yearMonth)
                    // 3. 워크데이(일한 날만 필터해서) 넣기
                    dialog.dismiss()
                }


            }

            setNegativeButton("キャンセル") {
                dialog, whichButton ->
                dialog.dismiss()
            }
        }

        val dialog = alertDialog.create()
        dialog.setView(editTextAge)
        dialog.show()
    }
}
