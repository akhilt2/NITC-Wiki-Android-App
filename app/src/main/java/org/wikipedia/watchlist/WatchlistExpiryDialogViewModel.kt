package org.akhil.nitcwiki.watchlist

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.akhil.nitcwiki.analytics.eventplatform.WatchlistAnalyticsHelper
import org.akhil.nitcwiki.dataclient.ServiceFactory
import org.akhil.nitcwiki.extensions.parcelable
import org.akhil.nitcwiki.page.PageTitle
import org.akhil.nitcwiki.util.Resource

class WatchlistExpiryDialogViewModel(bundle: Bundle) : ViewModel() {
    private val handler = CoroutineExceptionHandler { _, throwable ->
        _uiState.value = Resource.Error(throwable)
    }

    var pageTitle = bundle.parcelable<PageTitle>(WatchlistExpiryDialog.ARG_PAGE_TITLE)!!
    var expiry = bundle.getSerializable(WatchlistExpiryDialog.ARG_EXPIRY) as WatchlistExpiry

    private val _uiState = MutableStateFlow(Resource<WatchlistExpiry>())
    val uiState = _uiState.asStateFlow()

    fun changeExpiry(expiry: WatchlistExpiry) {
        WatchlistAnalyticsHelper.logAddedToWatchlist(pageTitle)
        viewModelScope.launch(handler) {
            val token = ServiceFactory.get(pageTitle.wikiSite).getWatchToken().query?.watchToken()
            val response = ServiceFactory.get(pageTitle.wikiSite)
                .watch(null, null, pageTitle.prefixedText, expiry.expiry, token!!)
            response.getFirst()?.let {
                WatchlistAnalyticsHelper.logAddedToWatchlistSuccess(pageTitle)
                _uiState.value = Resource.Success(expiry)
            }
        }
    }

    class Factory(private val bunble: Bundle) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WatchlistExpiryDialogViewModel(bunble) as T
        }
    }
}
