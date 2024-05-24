package org.akhil.nitcwiki.feed.suggestededits

import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.WikipediaApp
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.dataclient.mwapi.MwQueryPage
import org.akhil.nitcwiki.feed.model.CardType
import org.akhil.nitcwiki.feed.model.WikiSiteCard
import org.akhil.nitcwiki.util.DateUtil

class SuggestedEditsCard(val summaryList: List<SuggestedEditsFeedClient.SuggestedEditsSummary>?,
                         val imageTagsPage: MwQueryPage?,
                         wiki: WikiSite,
                         val age: Int) : WikiSiteCard(wiki) {

    override fun type(): CardType {
        return CardType.SUGGESTED_EDITS
    }

    override fun title(): String {
        return WikipediaApp.instance.getString(R.string.suggested_edits_feed_card_title)
    }

    override fun subtitle(): String {
        return DateUtil.getFeedCardDateString(age)
    }

    fun footerActionText(): String {
        return WikipediaApp.instance.getString(R.string.suggested_card_more_edits)
    }
}
