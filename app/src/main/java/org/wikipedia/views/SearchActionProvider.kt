package org.akhil.nitcwiki.views

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.SearchView
import androidx.core.view.ActionProvider
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.databinding.GroupSearchBinding
import org.akhil.nitcwiki.util.DeviceUtil.showSoftKeyboard
import org.akhil.nitcwiki.util.ResourceUtil.getThemedColor

class SearchActionProvider(context: Context,
                           private val searchHintString: String,
                           private val callback: Callback) : ActionProvider(context) {
    fun interface Callback {
        fun onQueryTextChange(s: String)
        fun onQueryTextFocusChange() {}
    }

    private val binding = GroupSearchBinding.inflate(LayoutInflater.from(context))

    override fun onCreateActionView(): View {
        binding.searchInput.isFocusable = true
        binding.searchInput.isIconified = false
        binding.searchInput.maxWidth = Int.MAX_VALUE
        binding.searchInput.inputType = EditorInfo.TYPE_CLASS_TEXT
        binding.searchInput.isSubmitButtonEnabled = false
        binding.searchInput.queryHint = searchHintString
        binding.searchInput.setSearchHintTextColor(getThemedColor(context, R.attr.secondary_color))
        binding.searchInput.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                binding.searchInput.setCloseButtonVisibility(s)
                callback.onQueryTextChange(s)
                return true
            }
        })
        binding.searchInput.setOnQueryTextFocusChangeListener { _: View?, isFocus: Boolean ->
            if (!isFocus) {
                callback.onQueryTextFocusChange()
            }
        }

        // remove focus line from search plate
        val searchEditPlate = binding.searchInput.findViewById<View>(androidx.appcompat.R.id.search_plate)
        searchEditPlate.setBackgroundColor(Color.TRANSPARENT)
        showSoftKeyboard(binding.searchInput)
        return binding.root
    }

    override fun overridesItemVisibility(): Boolean {
        return true
    }

    fun selectAllQueryTexts() {
        binding.searchInput.selectAllQueryTexts()
    }

    fun setQueryText(text: String?) {
        binding.searchInput.setQuery(text, false)
    }
}
