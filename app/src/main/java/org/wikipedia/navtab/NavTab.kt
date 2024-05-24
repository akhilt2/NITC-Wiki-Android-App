package org.akhil.nitcwiki.navtab

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.feed.FeedFragment
import org.akhil.nitcwiki.history.HistoryFragment
import org.akhil.nitcwiki.model.EnumCode
import org.akhil.nitcwiki.readinglist.ReadingListsFragment
import org.akhil.nitcwiki.suggestededits.SuggestedEditsTasksFragment

enum class NavTab constructor(
    @StringRes val text: Int,
    val id: Int,
    @DrawableRes val icon: Int,
    ) : EnumCode {

    EXPLORE(R.string.feed, R.id.nav_tab_explore, R.drawable.selector_nav_explore) {
        override fun newInstance(): Fragment {
            return FeedFragment.newInstance()
        }
    },
    READING_LISTS(R.string.nav_item_saved, R.id.nav_tab_reading_lists, R.drawable.selector_nav_saved) {
        override fun newInstance(): Fragment {
            return ReadingListsFragment.newInstance()
        }
    },
    SEARCH(R.string.nav_item_search, R.id.nav_tab_search, R.drawable.selector_nav_search) {
        override fun newInstance(): Fragment {
            return HistoryFragment.newInstance()
        }
    },
    EDITS(R.string.nav_item_suggested_edits, R.id.nav_tab_edits, R.drawable.selector_nav_edits) {
        override fun newInstance(): Fragment {
            return SuggestedEditsTasksFragment.newInstance()
        }
    };

    abstract fun newInstance(): Fragment

    override fun code(): Int {
        // This enumeration is not marshalled so tying declaration order to presentation order is
        // convenient and consistent.
        return ordinal
    }

    companion object {
        fun of(code: Int): NavTab {
            return entries[code]
        }
    }
}
