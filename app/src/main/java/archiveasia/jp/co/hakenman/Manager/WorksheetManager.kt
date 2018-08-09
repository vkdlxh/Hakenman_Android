package archiveasia.jp.co.hakenman.Manager

import archiveasia.jp.co.hakenman.Extension.*
import archiveasia.jp.co.hakenman.Model.DetailWork
import archiveasia.jp.co.hakenman.Model.Work
import java.util.*


class WorksheetManager {
    fun createWorksheet(yyyymm: String): Work {
        val year = yyyymm.substring(0, 4).toInt()
        val month = yyyymm.substring(4, 6).toInt()

        val date = yyyymm.createDate()

        var calendar = Calendar.getInstance()
        calendar.time = date
        var lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        var detailWorks = mutableListOf<DetailWork>()
        for (day in 1..lastDay) {
            val newDate = createDate(year, month, day)
            val week = newDate.getWeek()
            val isHoliday = newDate.isHoliday()

            var detailWork = DetailWork(year, month, day, week, isHoliday)
            detailWorks.add(detailWork)
        }

        return Work(date, 0.0, 0, detailWorks)
    }

    private fun createDate(year: Int, month: Int, day: Int): Date {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, day)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        return cal.time
    }
}