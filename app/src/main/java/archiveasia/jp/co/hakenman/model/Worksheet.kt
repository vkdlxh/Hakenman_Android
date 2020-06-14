package archiveasia.jp.co.hakenman.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

data class Worksheet (
        @SerializedName("workDate")
        var workDate: Date,                         // 勤務日時
        @SerializedName("workTimeSum")
        var workTimeSum: Double,                    // 勤務時間合計
        @SerializedName("workDaySum")
        var workDaySum: Int,                        // 勤務日合計
        @SerializedName("detailWorkList")
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
                parcel.readList(this as List<DetailWork>, DetailWork::class.java.classLoader)
            }
    )

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeLong(workDate.time)
        dest?.writeDouble(workTimeSum)
        dest?.writeInt(workDaySum)
        dest?.writeList(detailWorkList as List<DetailWork>)
    }

}

@Parcelize
data class DetailWork (
        @SerializedName("workDate")
        val workDate: Date,             // 勤務日
        @SerializedName("workFlag")
        var workFlag: Boolean,          // 勤務フラグ
        @SerializedName("beginTime")
        var beginTime: Date? = null,    // 出社時間
        @SerializedName("endTime")
        var endTime: Date? = null,      // 退社時間
        @SerializedName("breakTime")
        var breakTime: Date? = null,    // 休憩時間
        @SerializedName("duration")
        var duration: Double? = null,   // 勤務時間
        @SerializedName("note")
        var note: String? = null        // 備考
): Parcelable