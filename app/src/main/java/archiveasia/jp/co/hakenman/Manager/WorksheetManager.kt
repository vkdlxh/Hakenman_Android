package archiveasia.jp.co.hakenman.Manager

import archiveasia.jp.co.hakenman.Extension.*
import archiveasia.jp.co.hakenman.Model.DetailWork
import archiveasia.jp.co.hakenman.Model.Worksheet
import archiveasia.jp.co.hakenman.MyApplication
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.PrintWriter
import java.util.*

object WorksheetManager {

    private const val JSON_FILE_NAME = "/worksheet.json"

    private var worksheetList = mutableListOf<Worksheet>()

    fun loadLocalWorksheet() {
        var filepath = MyApplication.applicationContext().filesDir.path + JSON_FILE_NAME
        if (File(filepath).exists()) {
            val gson = GsonBuilder().setDateFormat("MMM dd, yyyy hh:mm:ss a").create()
            var worksheetList: MutableList<Worksheet> = gson.fromJson(FileReader(File(filepath)), object : TypeToken<MutableList<Worksheet>>() {}.type)
            this.worksheetList = worksheetList
        } else {
            println("No File")
        }
    }

    fun addWorksheetToJsonFile(worksheet: Worksheet) {
        worksheetList.add(worksheet)
        val gson = GsonBuilder().setPrettyPrinting().create()
        val jsonString = gson.toJson(worksheetList)

        var filepath = MyApplication.applicationContext().filesDir.path + JSON_FILE_NAME

        val writer = PrintWriter(filepath)
        writer.append(jsonString)
        writer.close()
    }

    fun updateWorksheet(newValue: Worksheet) {
        var oldValue = this.worksheetList.find {
            it.workDate.yearMonth() == newValue.workDate.yearMonth()
        }

        this.worksheetList.remove(oldValue)
        this.worksheetList.add(newValue)

        val gson = GsonBuilder().setPrettyPrinting().create()
        val jsonString = gson.toJson(worksheetList)

        var filepath = MyApplication.applicationContext().filesDir.path + JSON_FILE_NAME

        val writer = PrintWriter(filepath)
        writer.append(jsonString)
        writer.close()
    }

    fun updateWorksheetWithIndex(index: Int, worksheet: Worksheet) {
        worksheetList[index] = worksheet
        val gson = GsonBuilder().setPrettyPrinting().create()
        val jsonString = gson.toJson(worksheetList)

        var filepath = MyApplication.applicationContext().filesDir.path + JSON_FILE_NAME

        val writer = PrintWriter(filepath)
        writer.append(jsonString)
        writer.close()
    }

    fun updateAllWorksheet(worksheetList: List<Worksheet>) {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val jsonString = gson.toJson(worksheetList)

        var filepath = MyApplication.applicationContext().filesDir.path + JSON_FILE_NAME

        val writer = PrintWriter(filepath)
        writer.append(jsonString)
        writer.close()
    }

    fun getWorksheetList(): List<Worksheet> {
        return worksheetList.sortedByDescending { it.workDate.yearMonth() }
    }

    fun isAlreadyExistWorksheet(yearMonth: String): Boolean {
        return worksheetList.filter { it.workDate.yearMonth() == yearMonth}.isNotEmpty()
    }

    fun calculateDuration(detailWork: DetailWork): Double? {
        if (detailWork.beginTime != null && detailWork.endTime != null && detailWork.breakTime != null) {
            var beginTime = detailWork.beginTime!!.time
            var endTime = detailWork.endTime!!.time
            var breakTime = detailWork.breakTime!!.hourMinuteToDouble()
            val workTime = (endTime - beginTime) / (60 * 60 * 1000)
            return workTime.toDouble() - breakTime
        } else {
            return null
        }
    }

    fun createWorksheet(yyyymm: String): Worksheet {
        val year = yyyymm.substring(0, 4).toInt()
        val month = yyyymm.substring(4, 6).toInt()

        val date = yyyymm.createDate()

        var calendar = Calendar.getInstance()
        calendar.time = date
        var lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        var detailWorks = mutableListOf<DetailWork>()
        for (day in 1..lastDay) {
            val newDate = createDate(year, month, day)
            val week = newDate.week()
            val isHoliday = !newDate.isHoliday()

            var detailWork = DetailWork(year, month, day, week, isHoliday)
            detailWorks.add(detailWork)
        }
        var worksheet = Worksheet(date, 0.0, 0, detailWorks)
        worksheet.workDaySum = worksheet.detailWorkList
                .filter { it.workFlag == true }
                .count()
        return worksheet
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