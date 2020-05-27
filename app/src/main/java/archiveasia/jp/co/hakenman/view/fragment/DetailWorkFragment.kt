package archiveasia.jp.co.hakenman.view.fragment

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import archiveasia.jp.co.hakenman.model.Worksheet

abstract class DetailWorkFragment(@LayoutRes layoutId: Int) : Fragment(layoutId) {

    abstract fun replaceWorkList(worksheet: Worksheet)

}