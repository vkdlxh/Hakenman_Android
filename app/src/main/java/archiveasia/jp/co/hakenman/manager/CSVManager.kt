package archiveasia.jp.co.hakenman.manager

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import archiveasia.jp.co.hakenman.BuildConfig
import archiveasia.jp.co.hakenman.extension.hourMinuteToDouble
import archiveasia.jp.co.hakenman.extension.yearMonth
import archiveasia.jp.co.hakenman.model.Worksheet
import archiveasia.jp.co.hakenman.MyApplication
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat

class CSVManager(private val context: Context,
                 private val worksheet: Worksheet) {
    private val CSV_HEADER = "日,曜日,作業日,開始時間,終了時間,休憩,勤務時間,備考"
    private val filepath = MyApplication.applicationContext().filesDir.path
    private val fileName = worksheet.workDate.yearMonth() + ".csv"

    fun createCSVFile() {
        var fileWriter: FileWriter? = null

        try {
            fileWriter = FileWriter(File(filepath, fileName))

            fileWriter.append(CSV_HEADER)
            fileWriter.append('\n')

            for (detailWork in worksheet.detailWorkList) {

                fileWriter.append(detailWork.workDay.toString())
                fileWriter.append(',')
                fileWriter.append(detailWork.workWeek)
                fileWriter.append(',')
                val workFlagString = if (detailWork.workFlag) "O" else "X"
                fileWriter.append(workFlagString)
                fileWriter.append(',')


                if (detailWork.beginTime != null) {
                    fileWriter.append(SimpleDateFormat("HH:mm").format(detailWork.beginTime))
                }
                fileWriter.append(',')

                if (detailWork.endTime != null) {
                    fileWriter.append(SimpleDateFormat("HH:mm").format(detailWork.endTime))
                }
                fileWriter.append(',')

                if (detailWork.breakTime != null) {
                    fileWriter.append(detailWork.breakTime!!.hourMinuteToDouble().toString())
                }
                fileWriter.append(',')

                if (detailWork.duration != null) {
                    fileWriter.append(detailWork.duration.toString())
                }
                fileWriter.append(',')

                if (detailWork.note != null) {
                    fileWriter.append(detailWork.note)
                }
                fileWriter.append('\n')
            }
            println("CSV生成成功")
        } catch (e: Exception) {
            println("CSV生成 エラー")
            e.printStackTrace()
        } finally {
            try {
                fileWriter!!.flush()
                fileWriter.close()
            } catch (e: IOException) {
                println("Flushing/closing エラー")
                e.printStackTrace()
            }
        }
    }

    fun getFileUri(): Uri {
        val file = File(filepath, fileName)
        return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file)
    }
}