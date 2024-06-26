package org.akhil.nitcwiki.navtab

import androidx.fragment.app.Fragment
import org.akhil.nitcwiki.views.PositionAwareFragmentStateAdapter

class NavTabFragmentPagerAdapter(fragment: Fragment) : PositionAwareFragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return NavTab.of(position).newInstance()
    }

    override fun getItemCount(): Int {
        return NavTab.entries.size
    }
}
