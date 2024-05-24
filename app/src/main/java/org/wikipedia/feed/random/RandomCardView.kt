package org.akhil.nitcwiki.feed.random

import android.content.Context
import org.akhil.nitcwiki.feed.featured.FeaturedArticleCardView
import org.akhil.nitcwiki.feed.view.CardFooterView

class RandomCardView(context: Context) : FeaturedArticleCardView(context) {
    interface Callback {
        fun onRandomClick(view: RandomCardView)
    }

    override val footerCallback: CardFooterView.Callback
        get() = CardFooterView.Callback {
            card?.let {
                callback?.onRandomClick(this@RandomCardView)
            }
        }
}
