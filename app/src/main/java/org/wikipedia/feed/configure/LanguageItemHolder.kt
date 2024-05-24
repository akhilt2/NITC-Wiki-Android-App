package org.akhil.nitcwiki.feed.configure

import android.content.Context
import android.view.View
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.util.ResourceUtil
import org.akhil.nitcwiki.views.DefaultViewHolder
import org.akhil.nitcwiki.views.LangCodeView

class LanguageItemHolder internal constructor(private val context: Context, private val langCodeView: LangCodeView) : DefaultViewHolder<View>(langCodeView) {
    fun bindItem(langCode: String, enabled: Boolean) {
        langCodeView.setLangCode(langCode)
        val color = ResourceUtil.getThemedColorStateList(context, R.attr.secondary_color)
        langCodeView.setTextColor(if (enabled) ResourceUtil.getThemedColorStateList(context, R.attr.paper_color) else color)
        langCodeView.setBackgroundTint(if (enabled) ResourceUtil.getThemedColorStateList(context, R.attr.placeholder_color) else color)
        langCodeView.fillBackground(enabled)
    }
}
