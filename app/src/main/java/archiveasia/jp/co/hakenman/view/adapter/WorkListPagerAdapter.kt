package archiveasia.jp.co.hakenman.view.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import archiveasia.jp.co.hakenman.R
import archiveasia.jp.co.hakenman.view.fragment.WorkCalendarFragment
import archiveasia.jp.co.hakenman.view.fragment.WorksheetFragment

class WorkListPagerAdapter(val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> WorksheetFragment()
            else -> WorkCalendarFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> context.getString(R.string.sheet)
            else -> context.getString(R.string.calendar)
        }
    }

    override fun getCount(): Int = 2

}