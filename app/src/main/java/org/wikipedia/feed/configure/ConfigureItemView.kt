package org.akhil.nitcwiki.feed.configure

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.WikipediaApp
import org.akhil.nitcwiki.databinding.ItemFeedContentTypeBinding
import org.akhil.nitcwiki.feed.FeedContentType

class ConfigureItemView(context: Context) : FrameLayout(context) {

    interface Callback {
        fun onCheckedChanged(contentType: FeedContentType, checked: Boolean)
        fun onLanguagesChanged(contentType: FeedContentType)
    }

    private val binding = ItemFeedContentTypeBinding.inflate(LayoutInflater.from(context), this, true)

    private lateinit var contentType: FeedContentType
    private lateinit var adapter: LanguageItemAdapter
    var callback: Callback? = null

    init {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        binding.feedContentTypeLangList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        binding.feedContentTypeCheckbox.setOnCheckedChangeListener { _, checked ->
            callback?.onCheckedChanged(contentType, checked)
        }

        binding.feedContentTypeLangListClickTarget.setOnClickListener {
            showLangSelectDialog()
        }
    }

    fun setContents(contentType: FeedContentType) {
        this.contentType = contentType
        binding.feedContentTypeTitle.setText(contentType.titleId)
        binding.feedContentTypeSubtitle.setText(contentType.subtitleId)
        binding.feedContentTypeCheckbox.isChecked = contentType.isEnabled
        if (contentType.isPerLanguage && WikipediaApp.instance.languageState.appLanguageCodes.size > 1) {
            binding.feedContentTypeLangListContainer.visibility = VISIBLE
            adapter = LanguageItemAdapter(context, contentType)
            binding.feedContentTypeLangList.adapter = adapter
        } else {
            binding.feedContentTypeLangListContainer.visibility = GONE
        }
    }

    private fun showLangSelectDialog() {
        val view = ConfigureItemLanguageDialogView(context)
        val tempDisabledList = contentType.langCodesDisabled.toMutableList()
        view.setContentType(adapter.langList, tempDisabledList)
        MaterialAlertDialogBuilder(context)
            .setView(view)
            .setTitle(contentType.titleId)
            .setPositiveButton(R.string.customize_lang_selection_dialog_ok_button_text) { _, _ ->
                contentType.langCodesDisabled.clear()
                contentType.langCodesDisabled.addAll(tempDisabledList)
                adapter.notifyDataSetChanged()
                callback?.onLanguagesChanged(contentType)
                val atLeastOneEnabled = adapter.langList.any { !tempDisabledList.contains(it) }
                binding.feedContentTypeCheckbox.isChecked = atLeastOneEnabled
            }
            .setNegativeButton(R.string.customize_lang_selection_dialog_cancel_button_text, null)
            .show()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setDragHandleTouchListener(listener: OnTouchListener?) {
        binding.feedContentTypeDragHandle.setOnTouchListener(listener)
    }
}
