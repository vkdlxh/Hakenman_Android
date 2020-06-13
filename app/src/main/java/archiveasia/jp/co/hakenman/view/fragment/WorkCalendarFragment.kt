package archiveasia.jp.co.hakenman.view.fragment

import android.os.Bundle
import android.view.View
import archiveasia.jp.co.hakenman.R
import archiveasia.jp.co.hakenman.databinding.FragmentWorkCalendarBinding
import archiveasia.jp.co.hakenman.model.Worksheet
import archiveasia.jp.co.hakenman.view.activity.MonthlyWorkActivity
import archiveasia.jp.co.hakenman.view.adapter.CalendarAdapter
import java.util.Calendar
import java.util.Date

class WorkCalendarFragment : DetailWorkFragment(R.layout.fragment_work_calendar) {

    private var _binding: FragmentWorkCalendarBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CalendarAdapter

    override fun replaceWorkList(worksheet: Worksheet) {
        adapter.replaceWorksheet(worksheet)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWorkCalendarBinding.bind(view)

        val worksheet = (activity as MonthlyWorkActivity).worksheet

        val calendar = Calendar.getInstance()
        calendar.time = worksheet.workDate

        val count = calendar.getActualMaximum(Calendar.WEEK_OF_MONTH) * 7

        calendar.set(Calendar.DATE, 1)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
        calendar.add(Calendar.DATE, -dayOfWeek)


        val days = ArrayList<Date>()
        for (i in 0 until count) {
            days.add(calendar.time)
            calendar.add(Calendar.DATE, 1)
        }

        // カレンダーを復元
        calendar.time = worksheet.workDate

        adapter = CalendarAdapter(context!!, days, worksheet, object : MonthlyWorkActivity.SheetCalendarItemClickListener {
            override fun onClick(position: Int) {
                // TODO: activity 를 호출해서 액티비티에서 열도록
                (activity as MonthlyWorkActivity).showDetailWork(position)
            }
        })
        binding.gridView.adapter = adapter
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}