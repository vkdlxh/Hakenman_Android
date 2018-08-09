package archiveasia.jp.co.hakenman.Model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

data class Work (
        val workDate: Date,                         // 勤務日時
        val workTimeSum: Double,                    // 勤務時間合計
        val workDaySum: Int,                        // 勤務日合計
        val detailWorkList: ArrayList<DetailWork>   // 詳細勤務情報
): Parcelable {

    // 以下の処理もっと勉強する
    companion object {
        @JvmField @Suppress("unused")
        val CREATOR = createParcel { Work(it) }

        inline fun <reified T : Parcelable> createParcel(
                crossinline createFromParcel: (Parcel) -> T?): Parcelable.Creator<T> =
                object : Parcelable.Creator<T> {
                    override fun createFromParcel(source: Parcel): T? = createFromParcel(source)
                    override fun newArray(size: Int): Array<out T?> = arrayOfNulls(size)
                }

    }

    override fun describeContents(): Int = 0

    constructor(parcel: Parcel): this(
            Date(parcel.readLong()),
            parcel.readDouble(),
            parcel.readInt(),
            arrayListOf<DetailWork>().apply {
            parcel.readList(this, DetailWork::class.java.classLoader)
            }
    )

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeLong(workDate.time)
        dest?.writeDouble(workTimeSum)
        dest?.writeInt(workDaySum)
        dest?.writeList(detailWorkList)
    }

    fun Parcel.writeDate(date: Date?) {
        writeLong(date?.time ?: -1)
    }

    fun Parcel.readDate(): Date? {
        val long = readLong()
        return if (long != -1L) Date(long) else null
    }

}

@Parcelize
data class DetailWork (
        val workYear: Int,      // 年
        val workMonth: Int,     // 月
        val workDay: Int,       // 日
        val workWeek: String,   // 週
        val workFlag: Boolean,  // 勤務フラグ
        val beginTime: Date,    // 出社時間
        val endTime: Date,      // 退社時間
        val breakTime: Double,  // 休憩時間
        val note: String        // 参考
): Parcelable