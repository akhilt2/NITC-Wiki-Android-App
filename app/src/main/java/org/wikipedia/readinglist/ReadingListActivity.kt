package org.akhil.nitcwiki.readinglist

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.activity.SingleFragmentActivity
import org.akhil.nitcwiki.analytics.eventplatform.ReadingListsAnalyticsHelper
import org.akhil.nitcwiki.readinglist.database.ReadingList
import org.akhil.nitcwiki.util.ResourceUtil.getThemedColor

class ReadingListActivity : SingleFragmentActivity<ReadingListFragment>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.TRANSPARENT
        title = getString(R.string.reading_list_activity_title, intent.getStringExtra(EXTRA_READING_LIST_TITLE))
    }

    public override fun createFragment(): ReadingListFragment {
        val isPreview = intent.getBooleanExtra(EXTRA_READING_LIST_PREVIEW, false)
        return if (isPreview) {
            ReadingListFragment.newInstance(true)
        } else {
            ReadingListFragment.newInstance(intent.getLongExtra(EXTRA_READING_LIST_ID, 0))
        }
    }

    fun updateNavigationBarColor() {
        setNavigationBarColor(getThemedColor(this, R.attr.paper_color))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (intent.getBooleanExtra(EXTRA_READING_LIST_PREVIEW, false)) {
            ReadingListsAnalyticsHelper.logReceiveCancel(this, fragment.readingList)
        }
    }

    companion object {
        private const val EXTRA_READING_LIST_TITLE = "readingListTitle"
        const val EXTRA_READING_LIST_ID = "readingListId"
        const val EXTRA_READING_LIST_PREVIEW = "previewReadingList"

        fun newIntent(context: Context, list: ReadingList): Intent {
            return Intent(context, ReadingListActivity::class.java)
                    .putExtra(EXTRA_READING_LIST_TITLE, list.title)
                    .putExtra(EXTRA_READING_LIST_ID, list.id)
        }

        fun newIntent(context: Context, preview: Boolean): Intent {
            return Intent(context, ReadingListActivity::class.java)
                .putExtra(EXTRA_READING_LIST_PREVIEW, preview)
        }
    }
}
