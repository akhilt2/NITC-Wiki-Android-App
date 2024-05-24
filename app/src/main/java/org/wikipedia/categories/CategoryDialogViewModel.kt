package org.akhil.nitcwiki.categories

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.akhil.nitcwiki.Constants
import org.akhil.nitcwiki.dataclient.ServiceFactory
import org.akhil.nitcwiki.extensions.parcelable
import org.akhil.nitcwiki.page.PageTitle
import org.akhil.nitcwiki.util.Resource

class CategoryDialogViewModel(bundle: Bundle) : ViewModel() {
    val pageTitle = bundle.parcelable<PageTitle>(Constants.ARG_TITLE)!!
    val categoriesData = MutableLiveData<Resource<List<PageTitle>>>()

    init {
        fetchCategories()
    }

    private fun fetchCategories() {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            categoriesData.postValue(Resource.Error(throwable))
        }) {
            val response = ServiceFactory.get(pageTitle.wikiSite).getCategories(pageTitle.prefixedText)
            val titles = response.query?.pages?.map { page ->
                PageTitle(page.title, pageTitle.wikiSite).also {
                    it.displayText = page.displayTitle(pageTitle.wikiSite.languageCode)
                }
            }.orEmpty()
            categoriesData.postValue(Resource.Success(titles))
        }
    }

    class Factory(private val bundle: Bundle) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CategoryDialogViewModel(bundle) as T
        }
    }
}
