package archiveasia.jp.co.hakenman.extension

import java.math.BigDecimal

fun Double.twoDecimalPlaces() =
        BigDecimal(this).setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()