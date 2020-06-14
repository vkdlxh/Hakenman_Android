package archiveasia.jp.co.hakenman.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class OldWorksheet (
        @SerializedName("b")
        var workDate: Date,                             // 勤務日時
        @SerializedName("c")
        var workTimeSum: Double,                        // 勤務時間合計
        @SerializedName("d")
        var workDaySum: Int,                            // 勤務日合計
        @SerializedName("e")
        var detailWorkList: MutableList<OldDetailWork>  // 詳細勤務情報
)

data class OldDetailWork (
        @SerializedName("a")
        val workYear: Int,              // 年
        @SerializedName("b")
        val workMonth: Int,             // 月
        @SerializedName("c")
        val workDay: Int,               // 日
        @SerializedName("d")
        val workWeek: String,           // 週
        @SerializedName("e")
        var workFlag: Boolean,          // 勤務フラグ
        @SerializedName("f")
        var beginTime: Date? = null,    // 出社時間
        @SerializedName("g")
        var endTime: Date? = null,      // 退社時間
        @SerializedName("h")
        var breakTime: Date? = null,    // 休憩時間
        @SerializedName("i")
        var duration: Double? = null,   // 勤務時間
        @SerializedName("j")
        var note: String? = null        // 備考
)