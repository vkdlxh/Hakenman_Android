package archiveasia.jp.co.hakenman.model

import java.util.*

data class OldWorksheet (
        var workDate: Date,                             // 勤務日時
        var workTimeSum: Double,                        // 勤務時間合計
        var workDaySum: Int,                            // 勤務日合計
        var detailWorkList: MutableList<OldDetailWork>  // 詳細勤務情報
)

data class OldDetailWork (
        val workYear: Int,              // 年
        val workMonth: Int,             // 月
        val workDay: Int,               // 日
        val workWeek: String,           // 週
        var workFlag: Boolean,          // 勤務フラグ
        var beginTime: Date? = null,    // 出社時間
        var endTime: Date? = null,      // 退社時間
        var breakTime: Date? = null,    // 休憩時間
        var duration: Double? = null,   // 勤務時間
        var note: String? = null        // 備考
)