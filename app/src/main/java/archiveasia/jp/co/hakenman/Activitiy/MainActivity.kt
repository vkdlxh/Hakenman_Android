package archiveasia.jp.co.hakenman.Activitiy

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import archiveasia.jp.co.hakenman.R
import archiveasia.jp.co.hakenman.Adapter.WorkAdapter
import archiveasia.jp.co.hakenman.Model.Work
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById<ListView>(R.id.work_listView)

        val testJson = """
            [
            {
            "workDate": "201808",
            "workTimeSum": 150.25,
            "workDaySum": 20,
            "detailWorkList": [
                {
                "workYear": 2018,
                "workMonth": 08,
                "workDay": 25,
                "workWeek": "月",
                "workFlag": true,
                "beginTime": "09:00",
                "endTime": "18:00",
                "breakTime": "1",
                "note": "note1"
                },
                {
                "workYear": 2018,
                "workMonth": 08,
                "workDay": 25,
                "workWeek": "月",
                "workFlag": true,
                "beginTime": "09:00",
                "endTime": "18:00",
                "breakTime": "1",
                "note": "note1"
                },
                {
                "workYear": 2018,
                "workMonth": 08,
                "workDay": 25,
                "workWeek": "月",
                "workFlag": true,
                "beginTime": "09:00",
                "endTime": "18:00",
                "breakTime": "1",
                "note": "note1"
                },
            ]
            },
            {
            "workDate": "201808",
            "workTimeSum": 150.25,
            "workDaySum": 20,
            "detailWorkList": [
                {
                "workYear": 2018,
                "workMonth": 08,
                "workDay": 25,
                "workWeek": "月",
                "workFlag": true,
                "beginTime": "09:00",
                "endTime": "18:00",
                "breakTime": "1",
                "note": "note1"
                },
                {
                "workYear": 2018,
                "workMonth": 08,
                "workDay": 25,
                "workWeek": "月",
                "workFlag": true,
                "beginTime": "09:00",
                "endTime": "18:00",
                "breakTime": "1",
                "note": "note1"
                },
                {
                "workYear": 2018,
                "workMonth": 08,
                "workDay": 25,
                "workWeek": "月",
                "workFlag": true,
                "beginTime": "09:00",
                "endTime": "18:00",
                "breakTime": "1",
                "note": "note1"
                },
            ]
            }
            ]
            """
        val workList = Gson().fromJson(testJson, Array<Work>::class.java)
        val adapter = WorkAdapter(this, workList)
        listView.adapter = adapter
        
        listView.setOnItemClickListener { parent, view, position, id ->
            val intent = MonthWorkActivity.newIntent(this, workList[position])
            startActivity(intent)
        }
    }
}
