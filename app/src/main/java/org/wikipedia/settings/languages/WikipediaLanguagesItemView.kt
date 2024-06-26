package org.akhil.nitcwiki.settings.languages

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.databinding.ItemWikipediaLanguageBinding
import org.akhil.nitcwiki.util.DeviceUtil
import org.akhil.nitcwiki.util.ResourceUtil
import org.akhil.nitcwiki.util.StringUtil
import java.util.*

class WikipediaLanguagesItemView : LinearLayout {
    interface Callback {
        fun onCheckedChanged(position: Int)
        fun onLongPress(position: Int)
    }

    private var binding = ItemWikipediaLanguageBinding.inflate(LayoutInflater.from(context), this)
    private var position = 0
    var callback: Callback? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        setBackgroundColor(ResourceUtil.getThemedColor(context, R.attr.paper_color))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            foreground = AppCompatResources.getDrawable(context,
                ResourceUtil.getThemedAttributeId(context, androidx.appcompat.R.attr.selectableItemBackground))
        }
        binding.wikiLanguageCheckbox.setOnCheckedChangeListener { _, _ ->
            callback?.onCheckedChanged(position)
            updateBackgroundColor()
        }
        setOnLongClickListener {
            callback?.onLongPress(position)
            true
        }
        DeviceUtil.setContextClickAsLongClick(this)
    }

    private fun updateBackgroundColor() {
        setBackgroundColor(if (binding.wikiLanguageCheckbox.isChecked) ResourceUtil.getThemedColor(context, R.attr.background_color)
        else ResourceUtil.getThemedColor(context, R.attr.paper_color))
    }

    fun setContents(langCode: String, languageLocalizedName: String?, position: Int) {
        this.position = position
        binding.wikiLanguageOrder.text = (position + 1).toString()
        binding.wikiLanguageTitle.text = StringUtil.capitalize(languageLocalizedName.orEmpty())
        binding.wikiLanguageCode.setLangCode(langCode)
    }

    fun setCheckBoxEnabled(enabled: Boolean) {
        binding.wikiLanguageOrder.visibility = if (enabled) GONE else VISIBLE
        binding.wikiLanguageCheckbox.visibility = if (enabled) VISIBLE else GONE
        if (!enabled) {
            binding.wikiLanguageCheckbox.isChecked = false
            setBackgroundColor(ResourceUtil.getThemedColor(context, R.attr.paper_color))
        }
    }

    fun setCheckBoxChecked(checked: Boolean) {
        binding.wikiLanguageCheckbox.isChecked = checked
        updateBackgroundColor()
    }

    fun setDragHandleEnabled(enabled: Boolean) {
        binding.wikiLanguageDragHandle.visibility = if (enabled) VISIBLE else GONE
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setDragHandleTouchListener(listener: OnTouchListener?) {
        binding.wikiLanguageDragHandle.setOnTouchListener(listener)
    }
}
