package org.akhil.nitcwiki.notifications

import android.content.Context
import android.net.Uri
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.diff.ArticleEditDetailsActivity
import org.akhil.nitcwiki.history.HistoryEntry
import org.akhil.nitcwiki.page.LinkHandler
import org.akhil.nitcwiki.page.PageActivity
import org.akhil.nitcwiki.page.PageTitle
import org.akhil.nitcwiki.util.CustomTabsUtil
import org.akhil.nitcwiki.util.log.L

class NotificationLinkHandler constructor(context: Context) : LinkHandler(context) {

    override fun onPageLinkClicked(anchor: String, linkText: String) {
        // ignore
    }

    override fun onMediaLinkClicked(title: PageTitle) {
        // ignore
    }

    override fun onDiffLinkClicked(title: PageTitle, revisionId: Long) {
        context.startActivity(ArticleEditDetailsActivity.newIntent(context, title, revisionId))
    }

    override lateinit var wikiSite: WikiSite

    override fun onInternalLinkClicked(title: PageTitle) {
        context.startActivity(PageActivity.newIntentForCurrentTab(context,
            HistoryEntry(title, HistoryEntry.SOURCE_NOTIFICATION), title))
    }

    override fun onExternalLinkClicked(uri: Uri) {
        try {
            CustomTabsUtil.openInCustomTab(context, uri.toString())
        } catch (e: Exception) {
            L.e(e)
        }
    }
}
