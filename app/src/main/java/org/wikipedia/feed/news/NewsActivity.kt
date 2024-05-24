package org.akhil.nitcwiki.feed.news

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import org.akhil.nitcwiki.Constants
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.activity.SingleFragmentActivity
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.extensions.parcelableExtra
import org.akhil.nitcwiki.feed.news.NewsFragment.Companion.newInstance
import org.akhil.nitcwiki.util.ResourceUtil

class NewsActivity : SingleFragmentActivity<NewsFragment>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.TRANSPARENT
    }

    public override fun createFragment(): NewsFragment {
        return newInstance(intent.parcelableExtra(EXTRA_NEWS_ITEM)!!,
            intent.parcelableExtra(Constants.ARG_WIKISITE)!!)
    }

    fun updateNavigationBarColor() {
        setNavigationBarColor(ResourceUtil.getThemedColor(this, R.attr.paper_color))
    }

    companion object {
        const val EXTRA_NEWS_ITEM = "item"

        fun newIntent(context: Context, item: NewsItem, wiki: WikiSite): Intent {
            return Intent(context, NewsActivity::class.java)
                .putExtra(EXTRA_NEWS_ITEM, item)
                .putExtra(Constants.ARG_WIKISITE, wiki)
        }
    }
}
