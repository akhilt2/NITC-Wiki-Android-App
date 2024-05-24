package org.akhil.nitcwiki.feed.view

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.feed.model.Card
import org.akhil.nitcwiki.util.L10nUtil

abstract class DefaultFeedCardView<T : Card?>(context: Context?) : LinearLayout(context), FeedCardView<T> {
    override var card: T? = null
    override var callback: FeedAdapter.Callback? = null

    protected fun setLayoutDirectionByWikiSite(wiki: WikiSite, rootView: View) {
        L10nUtil.setConditionalLayoutDirection(rootView, wiki.languageCode)
    }
}
