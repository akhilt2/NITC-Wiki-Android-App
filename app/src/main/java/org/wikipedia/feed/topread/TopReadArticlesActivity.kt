package org.akhil.nitcwiki.feed.topread

import android.content.Context
import android.content.Intent
import android.os.Bundle
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.activity.SingleFragmentActivity
import org.akhil.nitcwiki.extensions.parcelableExtra

class TopReadArticlesActivity : SingleFragmentActivity<TopReadFragment>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.top_read_activity_title, intent.parcelableExtra<TopReadListCard>(TOP_READ_CARD)?.subtitle().orEmpty())
    }

    public override fun createFragment(): TopReadFragment {
        return TopReadFragment.newInstance(intent.parcelableExtra(TOP_READ_CARD)!!)
    }

    companion object {
        const val TOP_READ_CARD = "item"
        fun newIntent(context: Context, card: TopReadListCard): Intent {
            return Intent(context, TopReadArticlesActivity::class.java)
                .putExtra(TOP_READ_CARD, card)
        }
    }
}
