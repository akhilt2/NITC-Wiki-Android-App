package org.akhil.nitcwiki.feed.topread

import android.content.Context
import org.akhil.nitcwiki.feed.view.CardFooterView
import org.akhil.nitcwiki.feed.view.ListCardItemView
import org.akhil.nitcwiki.feed.view.ListCardRecyclerAdapter
import org.akhil.nitcwiki.feed.view.ListCardView
import org.akhil.nitcwiki.history.HistoryEntry
import org.akhil.nitcwiki.views.DefaultViewHolder

class TopReadCardView(context: Context) : ListCardView<TopReadListCard>(context) {
    override var card: TopReadListCard? = null
        set(value) {
            field = value
            value?.let {
                header(it)
                footer(it)
                set(RecyclerAdapter(it.items().subList(0, it.items().size.coerceAtMost(EVENTS_SHOWN))))
                setLayoutDirectionByWikiSite(it.wikiSite(), layoutDirectionView)
            }
        }

    private fun footer(card: TopReadListCard) {
        footerView.visibility = VISIBLE
        footerView.callback = getFooterCallback(card)
        footerView.setFooterActionText(card.footerActionText(), card.wikiSite().languageCode)
    }

    private fun header(card: TopReadListCard) {
        headerView.setTitle(card.title())
            .setLangCode(card.wikiSite().languageCode)
            .setCard(card)
            .setCallback(callback)
    }

    fun getFooterCallback(card: TopReadListCard): CardFooterView.Callback {
        return CardFooterView.Callback {
            callback?.onFooterClick(card)
        }
    }

    private inner class RecyclerAdapter constructor(items: List<TopReadItemCard>) :
        ListCardRecyclerAdapter<TopReadItemCard>(items) {

        override fun callback(): ListCardItemView.Callback? {
            return callback
        }

        override fun onBindViewHolder(holder: DefaultViewHolder<ListCardItemView>, position: Int) {
            val item = item(position)
            holder.view.setCard(card).setHistoryEntry(HistoryEntry(item.pageTitle, HistoryEntry.SOURCE_FEED_MOST_READ))
            holder.view.setNumber(position + 1)
            holder.view.setPageViews(item.pageViews)
            holder.view.setGraphView(item.viewHistory)
        }
    }

    companion object {
        private const val EVENTS_SHOWN = 5
    }
}
