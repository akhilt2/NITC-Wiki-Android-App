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
import org.akhil.nitcwiki.database.AppDatabase
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.extensions.parcelable
import org.akhil.nitcwiki.page.PageTitle
import org.akhil.nitcwiki.util.Resource

class RandomViewModel(bundle: Bundle) : ViewModel() {

    private val handler = CoroutineExceptionHandler { _, throwable ->
        _uiState.value = Resource.Error(throwable)
    }
    val wikiSite: WikiSite = bundle.parcelable(Constants.ARG_WIKISITE)!!
    var saveButtonState = false

    private val _uiState = MutableStateFlow(Resource<Boolean>())
    val uiState = _uiState.asStateFlow()

    fun findPageInAnyList(title: PageTitle) {
        viewModelScope.launch(handler) {
            val inAnyList = AppDatabase.instance.readingListPageDao().findPageInAnyList(title) != null
            saveButtonState = inAnyList
            _uiState.value = Resource.Success(inAnyList)
        }
    }

    class Factory(private val bundle: Bundle) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RandomViewModel(bundle) as T
        }
    }
}
