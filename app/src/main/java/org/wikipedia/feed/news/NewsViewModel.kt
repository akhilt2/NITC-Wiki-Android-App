package org.akhil.nitcwiki.feed.news

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.akhil.nitcwiki.Constants
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.extensions.parcelable

class NewsViewModel(bundle: Bundle) : ViewModel() {
    val item = bundle.parcelable<NewsItem>(NewsActivity.EXTRA_NEWS_ITEM)!!
    val wiki = bundle.parcelable<WikiSite>(Constants.ARG_WIKISITE)!!

    class Factory(private val bundle: Bundle) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NewsViewModel(bundle) as T
        }
    }
}
