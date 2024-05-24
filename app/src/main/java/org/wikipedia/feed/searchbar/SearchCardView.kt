package org.akhil.nitcwiki.feed.searchbar

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.WikipediaApp
import org.akhil.nitcwiki.databinding.ViewSearchBarBinding
import org.akhil.nitcwiki.feed.view.DefaultFeedCardView
import org.akhil.nitcwiki.util.FeedbackUtil
import org.akhil.nitcwiki.util.ResourceUtil

class SearchCardView(context: Context) : DefaultFeedCardView<SearchCard>(context) {
    interface Callback {
        fun onSearchRequested(view: View)
        fun onVoiceSearchRequested()
    }

    init {
        val binding = ViewSearchBarBinding.inflate(LayoutInflater.from(context), this, true)
        binding.searchContainer.setCardBackgroundColor(ResourceUtil.getThemedColor(context, R.attr.background_color))
        FeedbackUtil.setButtonTooltip(binding.voiceSearchButton)

        binding.searchContainer.setOnClickListener { callback?.onSearchRequested(it) }
        binding.voiceSearchButton.setOnClickListener { callback?.onVoiceSearchRequested() }
        binding.voiceSearchButton.isVisible = WikipediaApp.instance.voiceRecognitionAvailable
    }
}
