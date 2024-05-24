package org.akhil.nitcwiki.feed.mainpage

import android.content.Context
import android.view.LayoutInflater
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.databinding.ViewStaticCardBinding
import org.akhil.nitcwiki.feed.view.CardFooterView
import org.akhil.nitcwiki.feed.view.DefaultFeedCardView
import org.akhil.nitcwiki.feed.view.FeedAdapter
import org.akhil.nitcwiki.history.HistoryEntry
import org.akhil.nitcwiki.page.PageTitle
import org.akhil.nitcwiki.staticdata.MainPageNameData
import org.akhil.nitcwiki.util.L10nUtil.getStringForArticleLanguage

class MainPageCardView(context: Context) : DefaultFeedCardView<MainPageCard>(context) {

    private val binding = ViewStaticCardBinding.inflate(LayoutInflater.from(context), this, true)

    override var callback: FeedAdapter.Callback? = null
        set(value) {
            field = value
            binding.cardHeader.setCallback(value)
        }

    override var card: MainPageCard? = null
        set(value) {
            field = value
            value?.let {
                binding.cardHeader.setTitle(getStringForArticleLanguage(it.wikiSite().languageCode, R.string.view_main_page_card_title))
                    .setLangCode(it.wikiSite().languageCode)
                    .setCard(it)
                    .setCallback(callback)
                binding.cardFooter.callback = CardFooterView.Callback { goToMainPage() }
                binding.cardFooter.setFooterActionText(getStringForArticleLanguage(it.wikiSite().languageCode,
                    R.string.view_main_page_card_action), it.wikiSite().languageCode)
            }
        }

    private fun goToMainPage() {
        card?.let {
            callback?.onSelectPage(it, HistoryEntry(PageTitle(MainPageNameData.valueFor(it.wikiSite().languageCode), it.wikiSite()),
                HistoryEntry.SOURCE_FEED_MAIN_PAGE), false)
        }
    }
}
