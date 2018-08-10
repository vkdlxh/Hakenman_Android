package archiveasia.jp.co.hakenman.Model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

data class Worksheet (
        var workDate: Date,                         // 勤務日時
        var workTimeSum: Double,                    // 勤務時間合計
        var workDaySum: Int,                        // 勤務日合計
        var detailWorkList: MutableList<DetailWork> // 詳細勤務情報
): Parcelable {

    // 以下の処理もっと勉強する
    companion object {
        @JvmField @Suppress("unused")
        val CREATOR = createParcel { Worksheet(it) }

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
            mutableListOf<DetailWork>().apply {
            parcel.readList(this, DetailWork::class.java.classLoader)
            }
    )

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeLong(workDate.time)
        dest?.writeDouble(workTimeSum)
        dest?.writeInt(workDaySum)
        dest?.writeList(detailWorkList)
    }

}

@Parcelize
data class DetailWork (
        val workYear: Int,              // 年
        val workMonth: Int,             // 月
        val workDay: Int,               // 日
        val workWeek: String,           // 週
        val workFlag: Boolean,          // 勤務フラグ
        val beginTime: Date? = null,    // 出社時間
        val endTime: Date? = null,      // 退社時間
        val breakTime: Double? = null,  // 休憩時間
        val note: String? = null        // 参考
): Parcelable