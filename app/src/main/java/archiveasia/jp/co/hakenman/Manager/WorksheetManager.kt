package archiveasia.jp.co.hakenman.Manager

import android.app.Application
import android.content.Context
import archiveasia.jp.co.hakenman.Activity.MainActivity
import archiveasia.jp.co.hakenman.Extension.*
import archiveasia.jp.co.hakenman.Model.DetailWork
import archiveasia.jp.co.hakenman.Model.Worksheet
import archiveasia.jp.co.hakenman.MyApplication
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.util.*


object WorksheetManager {

    private const val JSON_FILE_NAME = "worksheet.json"
    private var worksheetList = mutableListOf<Worksheet>()

    fun loadLocalWorksheet() {
        if (File(JSON_FILE_NAME).canRead()) {
//            var jsonString = File(JSON_FILE_NAME).readText(Charsets.UTF_8)
            val gson = GsonBuilder().setDateFormat("yyyy/MM/dd HH:mm:ss").create()
//            var worksheetList = gson.fromJson(FileReader(JSON_FILE_NAME), object : TypeToken<MutableList<Worksheet>>() {}.type)
            var worksheetList: MutableList<Worksheet> = gson.fromJson(FileReader(JSON_FILE_NAME), object : TypeToken<MutableList<Worksheet>>() {}.type)
            this.worksheetList = worksheetList
        } else {
            println("No File")
        }
    }

    fun saveLocalWorksheet() {

    }

    fun addWorksheetToJsonFile(worksheet: Worksheet) {
        worksheetList.add(worksheet)
        val gson = GsonBuilder().setPrettyPrinting().create()
        val jsonString = gson.toJson(worksheetList)

        var context = MyApplication.applicationContext()

        context.openFileOutput(JSON_FILE_NAME, Context.MODE_PRIVATE).use {
            it.write(jsonString.toByteArray())
        }
    }

    fun getWorksheetList(): MutableList<Worksheet> {
        return worksheetList
    }

    fun isAlreadyExistWorksheet(yearMonth: String): Boolean {
        // TODO: 他の方法があるか探す(filter, map, reduce)
        var boolean = false
        for (work in worksheetList) {
            if (work.workDate.yearMonth() == yearMonth) {
                boolean = true
                break
            }
        }
        return boolean
    }


    fun convertToJson(work: Worksheet): String {
        val gson = GsonBuilder().setPrettyPrinting().create()
        return gson.toJson(work)
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