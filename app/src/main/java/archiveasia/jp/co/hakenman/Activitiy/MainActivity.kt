package archiveasia.jp.co.hakenman.Activitiy

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import archiveasia.jp.co.hakenman.R
import archiveasia.jp.co.hakenman.WorkAdapter
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
            "year": "2018",
            "detailWorkList": [
                {
                "monthDay": "monthDay1",
                "beginTime": "beginTime1",
                "endTime": "endTime1",
                "breakTime": "breakTime1",
                "note": "note1"
                },
                {
                "monthDay": "monthDay2",
                "beginTime": "beginTime2",
                "endTime": "endTime2",
                "breakTime": "breakTime2",
                "note": "note2"
                },
                {
                "monthDay": "monthDay3",
                "beginTime": "beginTime3",
                "endTime": "endTime3",
                "breakTime": "breakTime3",
                "note": "note3"
                }
            ]
            },
            {
            "year": "2017",
            "detailWorkList": [
                {
                "monthDay": "monthDay1",
                "beginTime": "beginTime1",
                "endTime": "endTime1",
                "breakTime": "breakTime1",
                "note": "note1"
                },
                {
                "monthDay": "monthDay2",
                "beginTime": "beginTime2",
                "endTime": "endTime2",
                "breakTime": "breakTime2",
                "note": "note2"
                },
                {
                "monthDay": "monthDay3",
                "beginTime": "beginTime3",
                "endTime": "endTime3",
                "breakTime": "breakTime3",
                "note": "note3"
                }
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
