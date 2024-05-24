package org.akhil.nitcwiki.feed.becauseyouread

import android.net.Uri
import org.akhil.nitcwiki.feed.model.Card
import org.akhil.nitcwiki.feed.model.CardType
import org.akhil.nitcwiki.page.PageTitle

class BecauseYouReadItemCard(private val title: PageTitle) : Card() {

    fun pageTitle(): PageTitle {
        return title
    }

    override fun title(): String {
        return title.displayText
    }

    override fun subtitle(): String? {
        return title.description
    }

    override fun image(): Uri? {
        return if (title.thumbUrl.isNullOrEmpty()) null else Uri.parse(title.thumbUrl)
    }

    override fun type(): CardType {
        return CardType.BECAUSE_YOU_READ_ITEM
    }
}
