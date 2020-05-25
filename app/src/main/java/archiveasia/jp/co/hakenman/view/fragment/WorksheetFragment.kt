package archiveasia.jp.co.hakenman.view.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import archiveasia.jp.co.hakenman.R
import archiveasia.jp.co.hakenman.view.activity.DailyWorkActivity
import archiveasia.jp.co.hakenman.view.activity.MonthlyWorkActivity
import archiveasia.jp.co.hakenman.view.adapter.DailyWorkAdapter
import kotlinx.android.synthetic.main.fragment_work_sheet.*

class WorksheetFragment : Fragment(R.layout.fragment_work_sheet) {

    private lateinit var dailyWorkAdapter: DailyWorkAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        daily_work_recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            // TODO: worksheet를 액티비티에서 받아서 보여주도록
            val worksheet = (activity as MonthlyWorkActivity).worksheet
            dailyWorkAdapter = DailyWorkAdapter(worksheet.detailWorkList, listener = object : DailyWorkAdapter.DailyWorkListener {
                override fun onClick(position: Int) {
//                    // TODO: activity 를 호출해서 액티비티에서 열도록
//                    val intent = DailyWorkActivity.newIntent(context, position, worksheet)
//                    startActivityForResult(intent, MonthlyWorkActivity.REQUEST_WORKSHEET)
                }
            })
            adapter = dailyWorkAdapter
        }
    }

}