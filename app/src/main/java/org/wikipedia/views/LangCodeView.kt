package org.akhil.nitcwiki.views

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.core.content.res.use
import androidx.core.view.ViewCompat
import androidx.core.widget.TextViewCompat
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.databinding.ViewLangCodeBinding
import org.akhil.nitcwiki.language.LanguageUtil
import org.akhil.nitcwiki.util.DimenUtil
import org.akhil.nitcwiki.util.ResourceUtil

class LangCodeView(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    private val binding = ViewLangCodeBinding.inflate(LayoutInflater.from(context), this)
    private val primaryColor = ResourceUtil.getThemedColor(context, R.attr.primary_color)
    init {
        val (textColor, backgroundTint, fillBackground) = context
            .obtainStyledAttributes(attrs, R.styleable.LangCodeView)
            .use {
                Triple(
                    it.getColor(R.styleable.LangCodeView_textColor, primaryColor),
                    it.getColor(R.styleable.LangCodeView_backgroundTint, primaryColor),
                    it.getBoolean(R.styleable.LangCodeView_fillBackground, false)
                )
            }

        layoutParams = ViewGroup.LayoutParams(DimenUtil.roundedDpToPx(48.0f), ViewGroup.LayoutParams.MATCH_PARENT)

        setTextColor(textColor)
        setBackgroundTint(backgroundTint)
        fillBackground(fillBackground)
        isFocusable = true
    }

    fun setLangCode(langCode: String) {
        binding.langCodeText.text = LanguageUtil.formatLangCodeForButton(langCode.uppercase())
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(binding.langCodeText, 1, 10, 1, TypedValue.COMPLEX_UNIT_SP)
    }

    fun setTextColor(@ColorInt textColor: Int) {
        binding.langCodeText.setTextColor(textColor)
    }

    fun setTextColor(colors: ColorStateList) {
        binding.langCodeText.setTextColor(colors)
    }

    fun fillBackground(fillBackground: Boolean) {
        binding.langCodeText.setBackgroundResource(if (fillBackground) R.drawable.square_shape_border_filled else R.drawable.square_shape_border)
    }

    fun setBackgroundTint(@ColorInt tintColor: Int) {
        ViewCompat.setBackgroundTintList(binding.langCodeText, ColorStateList.valueOf(tintColor))
    }

    fun setBackgroundTint(colors: ColorStateList) {
        ViewCompat.setBackgroundTintList(binding.langCodeText, colors)
    }
}
