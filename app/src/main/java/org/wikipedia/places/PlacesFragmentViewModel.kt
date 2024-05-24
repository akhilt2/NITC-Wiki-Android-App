package org.akhil.nitcwiki.places

import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mapbox.mapboxsdk.plugins.annotation.Symbol
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.akhil.nitcwiki.Constants
import org.akhil.nitcwiki.dataclient.ServiceFactory
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.extensions.parcelable
import org.akhil.nitcwiki.page.PageTitle
import org.akhil.nitcwiki.settings.Prefs
import org.akhil.nitcwiki.util.ImageUrlUtil
import org.akhil.nitcwiki.util.Resource

class PlacesFragmentViewModel(bundle: Bundle) : ViewModel() {

    val wikiSite: WikiSite get() = WikiSite.forLanguageCode(Prefs.placesWikiCode)
    var location: Location? = bundle.parcelable(PlacesActivity.EXTRA_LOCATION)
    var highlightedPageTitle: PageTitle? = bundle.parcelable(Constants.ARG_TITLE)

    var lastKnownLocation: Location? = null
    val nearbyPagesLiveData = MutableLiveData<Resource<List<NearbyPage>>>()

    fun fetchNearbyPages(latitude: Double, longitude: Double, radius: Int, maxResults: Int) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            nearbyPagesLiveData.postValue(Resource.Error(throwable))
        }) {
            val response = ServiceFactory.get(wikiSite).getGeoSearch("$latitude|$longitude", radius, maxResults, maxResults)
            val pages = response.query?.pages.orEmpty()
                .filter { it.coordinates != null }
                .map {
                    NearbyPage(it.pageId, PageTitle(it.title, wikiSite,
                        if (it.thumbUrl().isNullOrEmpty()) null else ImageUrlUtil.getUrlForPreferredSize(it.thumbUrl()!!, PlacesFragment.THUMB_SIZE),
                        it.description, it.displayTitle(wikiSite.languageCode)), it.coordinates!![0].lat, it.coordinates[0].lon)
                }
                .sortedBy {
                    lastKnownLocation?.run {
                        it.location.distanceTo(this)
                    }
                }
            nearbyPagesLiveData.postValue(Resource.Success(pages))
        }
    }

    class NearbyPage(
        val pageId: Int,
        val pageTitle: PageTitle,
        val latitude: Double,
        val longitude: Double,
        var annotation: Symbol? = null,
        var bitmap: Bitmap? = null
    ) {

        private val lat = latitude
        private val lng = longitude
        val location get() = Location("").apply {
            latitude = lat
            longitude = lng
        }
    }

    class Factory(private val bundle: Bundle) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PlacesFragmentViewModel(bundle) as T
        }
    }
}
