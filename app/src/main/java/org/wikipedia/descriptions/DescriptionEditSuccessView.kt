package org.akhil.nitcwiki.descriptions

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.databinding.ViewDescriptionEditSuccessBinding
import org.akhil.nitcwiki.views.AppTextViewWithImages

class DescriptionEditSuccessView : FrameLayout {
    interface Callback {
        fun onDismissClick()
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val binding = ViewDescriptionEditSuccessBinding.inflate(LayoutInflater.from(context), this, true)
    var callback: Callback? = null

    init {
        val editHint = resources.getString(R.string.description_edit_success_article_edit_hint)
        AppTextViewWithImages.setTextWithDrawables(binding.viewDescriptionEditSuccessHintText, editHint, R.drawable.ic_mode_edit_white_24dp)
        binding.viewDescriptionEditSuccessDoneButton.setOnClickListener {
            callback?.onDismissClick()
        }
    }
}
