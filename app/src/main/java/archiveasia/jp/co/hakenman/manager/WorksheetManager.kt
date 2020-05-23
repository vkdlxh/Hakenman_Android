package archiveasia.jp.co.hakenman.manager

import archiveasia.jp.co.hakenman.MyApplication
import archiveasia.jp.co.hakenman.R
import archiveasia.jp.co.hakenman.extension.*
import archiveasia.jp.co.hakenman.model.*
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * 勤務表関連マネージャークラス
 *
 * 勤務表作成、修正などを担当するマネージャー
 *
 * @author Jeon SangJun
 */
object WorksheetManager {

    private const val JSON_FILE_NAME = "/worksheet.json"
    private const val DOC_SIGN_CREATE_BY = "<!-- generated from Hakenman. -->"
    private const val HEADER_LINE = "|:--:|:---:|:-----:|:------:|:------:|:---:|:------:|:----:|"

    private var worksheetList = mutableListOf<Worksheet>()

    /**
     * JSONファイルをロードしてMutableList<Worksheet>に変更する
     *
     * @return MutableList<Worksheet>
     */
    fun loadLocalWorksheet() {
        val filepath = MyApplication.applicationContext().filesDir.path + JSON_FILE_NAME
        if (File(filepath).exists()) {
            val gson = GsonBuilder().setDateFormat("MMM dd, yyyy hh:mm:ss a").create()
            var worksheetList: MutableList<Worksheet> = gson.fromJson(FileReader(File(filepath)), object : TypeToken<MutableList<Worksheet>>() {}.type)

            // TODO: もっといい方法
            val worksheet = worksheetList.first()
            val detailWork = worksheet.detailWorkList.first()
            if (detailWork.workDate == null) {
                // Old -> New Worksheet Model 変更処理
                val oldWorksheetList: MutableList<OldWorksheet> = gson.fromJson(FileReader(File(filepath)), object : TypeToken<MutableList<OldWorksheet>>() {}.type)
                worksheetList = migrateNewWorksheetModel(oldWorksheetList)
                updateAllWorksheet(worksheetList)
            } else {
                this.worksheetList = worksheetList
            }

            println("No File")
        } else {
            println("No File")
        }
    }

    /**
     * 勤務表（Worksheet）を勤務表リストに追加した後、JSONファイルとして保持
     *
     * @param worksheet 追加する勤務表
     * @return JSONファイルで保持
     */
    fun addWorksheetToJsonFile(worksheet: Worksheet) {
        worksheetList.add(worksheet)
        writeJsonFile()
    }

    /**
     * 既存の勤務表を上書きした後、JSONファイルとして保持
     *
     * @param newValue 修正する勤務表
     * @return JSONファイルで保持
     */
    fun updateWorksheet(newValue: Worksheet) {
        val oldValue = this.worksheetList.find {
            it.workDate.yearMonth() == newValue.workDate.yearMonth()
        }

        this.worksheetList.remove(oldValue)
        this.worksheetList.add(newValue)

        writeJsonFile()
    }

    /**
     * 勤務表リストからIndexで新しい勤務表を上書きした後、JSONファイルとして保持
     *
     * @param index 勤務表位置
     * @param worksheet 修正する勤務表
     * @return JSONファイルで保持
     */
    fun updateWorksheetWithIndex(index: Int, worksheet: Worksheet) {
        worksheetList[index] = worksheet
        writeJsonFile()
    }

    /**
     * 勤務表リスト全体を修正し、JSONファイルとして保持
     *
     * @param worksheetList 修正する勤務表リスト
     * @return JSONファイルで保持
     */
    fun updateAllWorksheet(worksheetList: List<Worksheet>) {
        this.worksheetList = worksheetList.toMutableList()
        writeJsonFile()
    }

    /**
     * 勤務表を削除する
     *
     * @param index 削除する勤務表Index
     * @return 勤務表リスト
     */
    fun removeWorksheet(index: Int) : List<Worksheet> {
        worksheetList.removeAt(index)
        // TODO: 今月勤務表を削除する場合新しく作成する
        if (index == 0) {
            val currentYearMonth = Date().yearMonth()
            val worksheet = createWorksheet(currentYearMonth)
            addWorksheetToJsonFile(worksheet)
        }

        writeJsonFile()
        return worksheetList
    }

    /**
     * 勤務表リストをとる
     *
     * @return 勤務表リスト
     */
    fun getWorksheetList(): List<Worksheet> {
        return worksheetList
    }

    /**
     * 存在する勤務表かを確認
     *
     * @param yearMonth 追加する年月の値（yearmonth）
     * @return 存在すればtrue、でなければfalse
     */
    fun isAlreadyExistWorksheet(yearMonth: String): Boolean {
        return worksheetList.filter { it.workDate.yearMonth() == yearMonth}.isNotEmpty()
    }

    /**
     * 勤務時間を求める
     *
     * @param detailWork 1日勤務情報（DetailWork）
     * @return 開始、終了、休憩の３つの値が全部あれば、勤務時間を求める
     */
    fun calculateDuration(detailWork: DetailWork): Double? {
        return if (detailWork.beginTime != null && detailWork.endTime != null && detailWork.breakTime != null) {
//            val beginTime = detailWork.beginTime!!.time
//            val endTime = detailWork.endTime!!.time
//            val breakTime = detailWork.breakTime!!.hourMinuteToDouble()
//            val workTime = (endTime - beginTime) / (60 * 60 * 1000)
//            workTime - breakTime
            calculateDuration(detailWork.beginTime!!, detailWork.endTime!!, detailWork.breakTime!!)
        } else {
            null
        }
    }

    /**
     *
     */
    fun calculateDuration(startTime: Date, endTime: Date, breakTime: Date): Double {
        val defaultBeginTime = startTime.time
        val defaultEndTime = endTime.time
        val defaultBreakTime = breakTime.hourMinuteToDouble()
        val workTime = (defaultEndTime - defaultBeginTime) / (60 * 60 * 1000)
        return workTime - defaultBreakTime
    }

    /**
     * 新しい勤務表を作成する
     *
     * @param yyyymm 追加する年月の値
     * @return 新しい勤務表をリターン
     */
    fun createWorksheet(yyyymm: String): Worksheet {
        val year = yyyymm.substring(0, 4).toInt()
        val month = yyyymm.substring(4, 6).toInt()

        val date = yyyymm.createDate()

        val calendar = Calendar.getInstance()
        calendar.time = date
        val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val detailWorks = mutableListOf<DetailWork>()
        for (day in 1..lastDay) {
            val newDate = createDate(year, month, day)
            val isHoliday = !newDate.isHoliday()

            val detailWork = DetailWork(newDate, isHoliday)
            detailWorks.add(detailWork)
        }
        val worksheet = Worksheet(date, 0.0, 0, detailWorks)
        worksheet.workDaySum = worksheet.detailWorkList
                .filter { it.workFlag }
                .count()
        return worksheet
    }

    fun generateWorksheetToMarkdown(worksheet: Worksheet): String {
        val detailWorkList = worksheet.detailWorkList
        val headerColumn = MyApplication.applicationContext().getString(R.string.mardown_header_column)
        var markdownString = DOC_SIGN_CREATE_BY + "\n" +
            headerColumn + "\n" +
            HEADER_LINE + "\n"

        for (detailWork in detailWorkList) {
            val workDate = detailWork.workDate
            markdownString += "| "

            markdownString += workDate.day() + "| "

            markdownString += SimpleDateFormat("E", Locale.getDefault()).format(detailWork.workDate) + "| "

            val workFlagString = if (detailWork.workFlag) "O" else "X"
            markdownString += workFlagString + "| "

            if (detailWork.beginTime != null) {
                markdownString += SimpleDateFormat("HH:mm", Locale.getDefault()).format(detailWork.beginTime)
            }
            markdownString += "| "

            if (detailWork.endTime != null) {
                markdownString += SimpleDateFormat("HH:mm", Locale.getDefault()).format(detailWork.endTime)
            }
            markdownString += "| "

            if (detailWork.breakTime != null) {
                markdownString += detailWork.breakTime!!.hourMinuteToDouble().toString()
            }
            markdownString += "| "

            if (detailWork.duration != null) {
                markdownString += detailWork.duration.toString()
            }
            markdownString += "| "

            if (detailWork.note != null) {
                markdownString += detailWork.note
            }
            markdownString += "| \n"
        }

        return markdownString
    }

    private fun writeJsonFile() {
        worksheetList = worksheetList.sortedByDescending { it.workDate.yearMonth() }.toMutableList()
        val gson = GsonBuilder().setPrettyPrinting().create()
        val jsonString = gson.toJson(worksheetList)

        val filepath = MyApplication.applicationContext().filesDir.path + JSON_FILE_NAME

        val writer = PrintWriter(filepath)
        writer.append(jsonString)
        writer.close()
    }

    /**
     * 年、月、日のIntを持ってDateを作る
     *
     * @param year 年
     * @param month 月
     * @param day 日
     * @return Date値をリターンする
     */
    private fun createDate(year: Int, month: Int, day: Int): Date {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month - 1)
        cal.set(Calendar.DAY_OF_MONTH, day)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        return cal.time
    }

    // 旧バージョンユーザーのマイグレーションメソッド
    private fun migrateNewWorksheetModel(oldList: MutableList<OldWorksheet>): ArrayList<Worksheet>{
        // Old -> New Worksheet Model 변경처리
        val newList = ArrayList<Worksheet>()
        oldList.forEach { oldWorksheet ->
            val newDetailWorksheetList = ArrayList<DetailWork>()
            oldWorksheet.detailWorkList.forEach {
                val workDate = createDate(it.workYear, it.workMonth, it.workDay)
                val newDetailWork = DetailWork(workDate, it.workFlag, it.beginTime, it.endTime, it.breakTime, it.duration, it.note)
                newDetailWorksheetList.add(newDetailWork)
            }
            val newWorksheet = Worksheet(oldWorksheet.workDate, oldWorksheet.workTimeSum, oldWorksheet.workDaySum, newDetailWorksheetList)
            newList.add(newWorksheet)
        }
        writeJsonFile()
        return newList
    }
}