package org.akhil.nitcwiki.onboarding

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.WikipediaApp
import org.akhil.nitcwiki.databinding.ViewOnboardingPageBinding
import org.akhil.nitcwiki.onboarding.OnboardingPageView.LanguageListAdapter.OptionsViewHolder
import org.akhil.nitcwiki.page.LinkMovementMethodExt
import org.akhil.nitcwiki.util.StringUtil
import java.util.Locale

class OnboardingPageView constructor(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {
    interface Callback {
        fun onLinkClick(view: OnboardingPageView, url: String)
        fun onListActionButtonClicked(view: OnboardingPageView)
    }

    class DefaultCallback : Callback {
        override fun onLinkClick(view: OnboardingPageView, url: String) {}
        override fun onListActionButtonClicked(view: OnboardingPageView) {}
    }

    var callback: Callback? = null
    private val binding = ViewOnboardingPageBinding.inflate(LayoutInflater.from(context), this)
    private var listDataType: String? = null
    private var viewHeightDetected: Boolean = false

    init {
        attrs?.let { attrSet ->
            context.withStyledAttributes(attrSet, R.styleable.OnboardingPageView) {
                val imageResource = getResourceId(R.styleable.OnboardingPageView_centeredImage, -1)
                val primaryText = getString(R.styleable.OnboardingPageView_primaryText)
                val secondaryText = getString(R.styleable.OnboardingPageView_secondaryText)
                val tertiaryText = getString(R.styleable.OnboardingPageView_tertiaryText)
                listDataType = getString(R.styleable.OnboardingPageView_dataType)
                val showListView = getBoolean(R.styleable.OnboardingPageView_showListView, false)
                val background = getDrawable(R.styleable.OnboardingPageView_background)
                val imageSize = getDimension(R.styleable.OnboardingPageView_imageSize, 0f)
                val showPatrollerTasksButtons = getBoolean(R.styleable.OnboardingPageView_patrollerTasksButtons, false)
                background?.let { setBackground(it) }
                binding.imageViewCentered.isVisible = imageResource != -1
                if (imageSize > 0 && imageResource != -1) {
                    val centeredImage = AppCompatResources.getDrawable(context, imageResource)
                    if (centeredImage != null && centeredImage.intrinsicHeight > 0) {
                        binding.imageViewCentered.setImageDrawable(centeredImage)
                        val aspect =
                            centeredImage.intrinsicWidth.toFloat() / centeredImage.intrinsicHeight
                        binding.imageViewCentered.updateLayoutParams {
                            width = imageSize.toInt()
                            height = (imageSize / aspect).toInt()
                        }
                    }
                }
                binding.primaryTextView.visibility = if (primaryText.isNullOrEmpty()) GONE else VISIBLE
                binding.primaryTextView.text = primaryText
                binding.secondaryTextView.visibility = if (secondaryText.isNullOrEmpty()) GONE else VISIBLE
                binding.secondaryTextView.text = StringUtil.fromHtml(secondaryText)
                binding.tertiaryTextView.visibility = if (tertiaryText.isNullOrEmpty()) GONE else VISIBLE
                binding.tertiaryTextView.text = tertiaryText
                setUpLanguageListContainer(showListView, listDataType)
                binding.secondaryTextView.movementMethod = LinkMovementMethodExt { url: String ->
                    callback?.onLinkClick(this@OnboardingPageView, url)
                }
                binding.languageListContainer.addLanguageButton.setOnClickListener {
                    callback?.onListActionButtonClicked(this@OnboardingPageView)
                }

                binding.patrollerTasksButtonsContainer?.root?.isVisible = showPatrollerTasksButtons
            }
        }
    }

    fun setSecondaryText(text: CharSequence?) {
        binding.secondaryTextView.text = text
    }

    fun setTertiaryTextViewVisible(isVisible: Boolean) {
        binding.tertiaryTextView.isVisible = isVisible
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (!viewHeightDetected) {
            if (binding.scrollView != null && binding.scrollViewContainer != null &&
                    binding.scrollView.height <= binding.scrollViewContainer.height) {
                // Remove layout gravity of the text below on small screens to make centered image visible
                removeScrollViewContainerGravity()
            }
            viewHeightDetected = true
        }
    }

    private fun removeScrollViewContainerGravity() {
        val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        )
        params.gravity = Gravity.NO_GRAVITY
        binding.scrollViewContainer?.layoutParams = params
    }

    private fun setUpLanguageListContainer(showListView: Boolean, dataType: String?) {
        if (!showListView) {
            return
        }
        binding.tertiaryTextView.visibility = View.GONE
        binding.languageListContainer.root.visibility = View.VISIBLE
        binding.languageListContainer.languagesList.layoutManager = LinearLayoutManager(context)
        binding.languageListContainer.languagesList.adapter = LanguageListAdapter(getListData(dataType))
    }

    private fun getListData(dataType: String?): List<String> {
        return if (dataType == context.getString(R.string.language_data)) {
            val language = WikipediaApp.instance.languageState
            language.appLanguageCodes.map { language.getAppLanguageLocalizedName(it) }
                .mapNotNull { localizedName ->
                    localizedName?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                }
        } else {
            emptyList()
        }
    }

    inner class LanguageListAdapter internal constructor(private val items: List<String>) : RecyclerView.Adapter<OptionsViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionsViewHolder {
            return OptionsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_onboarding_options_recycler, parent, false))
        }

        override fun onBindViewHolder(holder: OptionsViewHolder, position: Int) {
            holder.optionLabelTextView.textDirection = binding.primaryTextView.layoutDirection
            holder.optionLabelTextView.text = context.getString(R.string.onboarding_option_string, (position + 1).toString(), items[position])
        }

        override fun getItemCount() = items.size

        inner class OptionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var optionLabelTextView = itemView.findViewById<TextView>(R.id.option_label)!!
        }
    }

    fun refreshLanguageList() {
        binding.languageListContainer.languagesList.adapter = LanguageListAdapter(getListData(listDataType))
        binding.languageListContainer.languagesList.adapter?.notifyDataSetChanged()
    }
}
