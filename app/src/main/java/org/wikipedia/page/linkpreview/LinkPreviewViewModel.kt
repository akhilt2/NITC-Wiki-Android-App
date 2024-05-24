package org.akhil.nitcwiki.page.linkpreview

import android.location.Location
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.akhil.nitcwiki.analytics.eventplatform.WatchlistAnalyticsHelper
import org.akhil.nitcwiki.auth.AccountUtil
import org.akhil.nitcwiki.database.AppDatabase
import org.akhil.nitcwiki.dataclient.ServiceFactory
import org.akhil.nitcwiki.extensions.parcelable
import org.akhil.nitcwiki.history.HistoryEntry
import org.akhil.nitcwiki.page.PageTitle
import org.akhil.nitcwiki.settings.Prefs
import org.akhil.nitcwiki.util.log.L
import org.akhil.nitcwiki.watchlist.WatchlistExpiry

class LinkPreviewViewModel(bundle: Bundle) : ViewModel() {
    private val _uiState = MutableStateFlow<LinkPreviewViewState>(LinkPreviewViewState.Loading)
    val uiState = _uiState.asStateFlow()
    val historyEntry = bundle.parcelable<HistoryEntry>(LinkPreviewDialog.ARG_ENTRY)!!
    var pageTitle = historyEntry.title
    var location = bundle.parcelable<Location>(LinkPreviewDialog.ARG_LOCATION)
    val fromPlaces = historyEntry.source == HistoryEntry.SOURCE_PLACES
    val lastKnownLocation = bundle.parcelable<Location>(LinkPreviewDialog.ARG_LAST_KNOWN_LOCATION)
    var isInReadingList = false

    var isWatched = false
    var hasWatchlistExpiry = false

    init {
        loadContent()
    }

    private fun loadContent() {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            _uiState.value = LinkPreviewViewState.Error(throwable)
        }) {
            val summaryCall = async { ServiceFactory.getRest(pageTitle.wikiSite)
                .getSummaryResponseSuspend(pageTitle.prefixedText, null, null, null, null, null) }

            val watchedCall = async { if (fromPlaces && AccountUtil.isLoggedIn) ServiceFactory.get(pageTitle.wikiSite).getWatchedStatus(pageTitle.prefixedText) else null }

            val response = summaryCall.await()
            val summary = response.body()!!
            // Rebuild our PageTitle, since it may have been redirected or normalized.
            val oldFragment = pageTitle.fragment
            pageTitle = PageTitle(
                    summary.apiTitle, pageTitle.wikiSite, summary.thumbnailUrl,
                    summary.description, summary.displayTitle
            )

            // check if our URL was redirected, which might include a URL fragment that leads
            // to a specific section in the target article.
            if (!response.raw().request.url.fragment.isNullOrEmpty()) {
                pageTitle.fragment = response.raw().request.url.fragment
            } else if (!oldFragment.isNullOrEmpty()) {
                pageTitle.fragment = oldFragment
            }

            if (fromPlaces) {
                isWatched = watchedCall.await()?.query?.firstPage()?.watched ?: false
                val readingList = AppDatabase.instance.readingListPageDao().findPageInAnyList(pageTitle)
                isInReadingList = readingList != null
            }

            if (location == null) {
                location = summary.coordinates
            }

            _uiState.value = LinkPreviewViewState.Content(summary)
        }
    }

    fun loadGallery(revision: Long) {
        if (Prefs.isImageDownloadEnabled) {
            viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
                L.w("Failed to fetch gallery collection.", throwable)
            }) {
                val mediaList = ServiceFactory.getRest(pageTitle.wikiSite)
                    .getMediaListSuspend(pageTitle.prefixedText, revision)
                val maxImages = 10
                val items = mediaList.getItems("image", "video").asReversed()
                val titleList =
                    items.filter { it.showInGallery }.map { it.title }.take(maxImages)
                if (titleList.isEmpty()) _uiState.value = LinkPreviewViewState.Completed
                else {
                    val response = ServiceFactory.get(
                        pageTitle.wikiSite
                    ).getImageInfoSuspend(
                        titleList.joinToString("|"),
                        pageTitle.wikiSite.languageCode
                    )
                    val pageList =
                        response.query?.pages?.filter { it.imageInfo() != null }.orEmpty()
                    _uiState.value = LinkPreviewViewState.Gallery(pageList)
                }
            }
        } else {
            _uiState.value = LinkPreviewViewState.Completed
        }
    }

    fun watchOrUnwatch(unwatch: Boolean) {
        if (isWatched) {
            WatchlistAnalyticsHelper.logRemovedFromWatchlist(pageTitle)
        } else {
            WatchlistAnalyticsHelper.logAddedToWatchlist(pageTitle)
        }
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            L.w("Failed to fetch watch status.", throwable)
        }) {
            val token = ServiceFactory.get(pageTitle.wikiSite).getWatchToken().query?.watchToken()
            val response = ServiceFactory.get(pageTitle.wikiSite)
                .watch(if (unwatch) 1 else null, null, pageTitle.prefixedText, WatchlistExpiry.NEVER.expiry, token!!)

            if (unwatch) {
                WatchlistAnalyticsHelper.logRemovedFromWatchlistSuccess(pageTitle)
            } else {
                WatchlistAnalyticsHelper.logAddedToWatchlistSuccess(pageTitle)
            }
            response.getFirst()?.let {
                isWatched = it.watched
                _uiState.value = LinkPreviewViewState.Watch(isWatched)
            }
        }
    }

    class Factory(private val bunble: Bundle) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LinkPreviewViewModel(bunble) as T
        }
    }
}
