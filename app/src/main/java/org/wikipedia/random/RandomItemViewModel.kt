package org.akhil.nitcwiki.random

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.akhil.nitcwiki.Constants
import org.akhil.nitcwiki.dataclient.ServiceFactory
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.dataclient.page.PageSummary
import org.akhil.nitcwiki.extensions.parcelable
import org.akhil.nitcwiki.util.Resource

class RandomItemViewModel(bundle: Bundle) : ViewModel() {

    private val handler = CoroutineExceptionHandler { _, throwable ->
        _uiState.value = Resource.Error(throwable)
    }
    val wikiSite: WikiSite = bundle.parcelable(Constants.ARG_WIKISITE)!!
    var summary: PageSummary? = null

    private val _uiState = MutableStateFlow(Resource<PageSummary?>())
    val uiState = _uiState.asStateFlow()

    init {
        getRandomPage()
    }

    fun getRandomPage() {
        _uiState.value = Resource.Loading()
        viewModelScope.launch(handler) {
            summary = ServiceFactory.getRest(wikiSite).getRandomSummary()
            _uiState.value = Resource.Success(summary)
        }
    }

    class Factory(private val bundle: Bundle) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RandomItemViewModel(bundle) as T
        }
    }
}
