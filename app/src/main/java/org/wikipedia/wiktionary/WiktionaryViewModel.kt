package org.akhil.nitcwiki.wiktionary

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
import org.akhil.nitcwiki.dataclient.restbase.RbDefinition
import org.akhil.nitcwiki.extensions.parcelable
import org.akhil.nitcwiki.page.PageTitle
import org.akhil.nitcwiki.util.Resource
import org.akhil.nitcwiki.util.StringUtil
import org.akhil.nitcwiki.util.log.L
import java.util.Locale

class WiktionaryViewModel(bundle: Bundle) : ViewModel() {

    private val handler = CoroutineExceptionHandler { _, throwable ->
        _uiState.value = Resource.Error(throwable)
    }

    val pageTitle = bundle.parcelable<PageTitle>(Constants.ARG_TITLE)!!
    var selectedText = bundle.getString(Constants.ARG_TEXT)

    private val _uiState = MutableStateFlow(Resource<List<RbDefinition.Usage>>())
    val uiState = _uiState.asStateFlow()

    init {
        loadDefinitions()
    }

    private fun loadDefinitions() {
        _uiState.value = Resource.Loading()
        viewModelScope.launch(handler) {
            if (selectedText.isNullOrEmpty()) {
                definitionsNotFound()
                return@launch
            }
            val query = StringUtil.addUnderscores(selectedText)
            val response = try {
                ServiceFactory.getRest(WikiSite(pageTitle.wikiSite.subdomain() + WiktionaryDialog.WIKTIONARY_DOMAIN))
                    .getDefinition(query)
            } catch (e: Exception) {
                L.w("Cannot find the definition. Try to use lowercase text.")
                ServiceFactory.getRest(WikiSite(pageTitle.wikiSite.subdomain() + WiktionaryDialog.WIKTIONARY_DOMAIN))
                    .getDefinition(query.lowercase(Locale.getDefault()))
            }

            response[pageTitle.wikiSite.languageCode]?.let { usageList ->
                if (usageList.isEmpty()) {
                    definitionsNotFound()
                } else {
                    _uiState.value = Resource.Success(usageList)
                }
            } ?: run {
                definitionsNotFound()
            }
        }
    }

    private fun definitionsNotFound() {
        _uiState.value = Resource.Error(Throwable("Definitions not found."))
    }

    class Factory(private val bundle: Bundle) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WiktionaryViewModel(bundle) as T
        }
    }
}
