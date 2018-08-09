package archiveasia.jp.co.hakenman.Activitiy

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import archiveasia.jp.co.hakenman.R
import archiveasia.jp.co.hakenman.Adapter.WorkAdapter
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
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }
}
