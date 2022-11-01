package archiveasia.jp.co.hakenman.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Worksheet (
        @SerializedName("workDate")
        var workDate: Date,                         // 勤務日時
        @SerializedName("workTimeSum")
        var workTimeSum: Double,                    // 勤務時間合計
        @SerializedName("workDaySum")
        var workDaySum: Int,                        // 勤務日合計
        @SerializedName("detailWorkList")
        var detailWorkList: MutableList<DetailWork> // 詳細勤務情報
): Parcelable

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