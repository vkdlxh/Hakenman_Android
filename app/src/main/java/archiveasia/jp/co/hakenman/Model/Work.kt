package archiveasia.jp.co.hakenman.Model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class Work (
     val year: String,                          // 年
     val detailWorkList: ArrayList<DetailWork>  // 詳細勤務情報
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
        parcel.readString(),
        arrayListOf<DetailWork>().apply {
            parcel.readList(this, DetailWork::class.java.classLoader)
        }
    )

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(year)
        dest?.writeList(detailWorkList)
    }

}

@Parcelize
data class DetailWork (
    val monthDay: String,   // 月日
    val beginTime: String,  // 出社時間
    val endTime: String,    // 退社時間
    val breakTime: String,  // 休憩時間
    val note: String        //  参考
): Parcelable