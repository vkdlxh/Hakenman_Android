package archiveasia.jp.co.hakenman.view.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import archiveasia.jp.co.hakenman.R
import archiveasia.jp.co.hakenman.model.Worksheet
import archiveasia.jp.co.hakenman.view.activity.MonthlyWorkActivity
import archiveasia.jp.co.hakenman.view.adapter.DailyWorkAdapter
import kotlinx.android.synthetic.main.fragment_work_sheet.*

class WorksheetFragment : DetailWorkFragment(R.layout.fragment_work_sheet) {

    private lateinit var dailyWorkAdapter: DailyWorkAdapter

    override fun replaceWorkList(worksheet: Worksheet) {
        dailyWorkAdapter.replaceDailyWorkList(worksheet.detailWorkList)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        daily_work_recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            // TODO: worksheet를 액티비티에서 받아서 보여주도록
            val worksheet = (activity as MonthlyWorkActivity).worksheet
            dailyWorkAdapter = DailyWorkAdapter(worksheet.detailWorkList, object : MonthlyWorkActivity.SheetCalendarItemClickListener {
                override fun onClick(position: Int) {
                    // TODO: activity 를 호출해서 액티비티에서 열도록
                    (activity as MonthlyWorkActivity).showDetailWork(position)
                }
            })
            adapter = dailyWorkAdapter
        }
    }

}