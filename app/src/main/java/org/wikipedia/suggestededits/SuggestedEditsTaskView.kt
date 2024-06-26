package org.akhil.nitcwiki.suggestededits

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import org.akhil.nitcwiki.Constants.MIN_LANGUAGES_TO_UNLOCK_TRANSLATION
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.WikipediaApp
import org.akhil.nitcwiki.databinding.ViewSuggestedEditsTaskItemBinding
import org.akhil.nitcwiki.util.DimenUtil
import org.akhil.nitcwiki.util.ResourceUtil
import org.akhil.nitcwiki.views.WikiCardView

internal class SuggestedEditsTaskView constructor(context: Context, attrs: AttributeSet? = null) : WikiCardView(context, attrs) {
    private val binding = ViewSuggestedEditsTaskItemBinding.inflate(LayoutInflater.from(context), this)

    init {
        val params = MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        val marginX = resources.getDimension(R.dimen.activity_horizontal_margin).toInt()
        val marginY = DimenUtil.roundedDpToPx(8f)
        params.setMargins(marginX, marginY, marginX, marginY)
        layoutParams = params
    }

    private fun updateTranslateActionUI() {
        val color = ResourceUtil.getThemedColor(context, if (WikipediaApp.instance.languageState.appLanguageCodes.size >= MIN_LANGUAGES_TO_UNLOCK_TRANSLATION)
            R.attr.progressive_color else R.attr.placeholder_color)
        binding.secondaryButton.iconTint = ColorStateList.valueOf(color)
        binding.secondaryButton.setTextColor(color)
    }

    fun setUpViews(task: SuggestedEditsTask, callback: Callback?) {
        updateTranslateActionUI()
        binding.taskTitle.text = task.title
        binding.taskDescription.text = task.description
        binding.primaryButton.text = task.primaryAction
        if (task.primaryActionIcon != 0) {
            binding.primaryButton.setIconResource(task.primaryActionIcon)
        }
        binding.primaryButton.contentDescription = task.primaryAction + " " + task.title
        binding.taskIcon.setImageResource(task.imageDrawable)
        binding.taskTitleNewLabel.visibility = if (task.new) VISIBLE else GONE

        setOnClickListener {
            if (!task.disabled) {
                callback?.onViewClick(task, false)
            }
        }
        binding.primaryButton.setOnClickListener {
            if (!task.disabled) {
                callback?.onViewClick(task, false)
            }
        }
        binding.secondaryButton.setOnClickListener {
            if (!task.disabled) {
                callback?.onViewClick(task, true)
            }
        }
        binding.secondaryButton.visibility = if (task.secondaryAction.isNullOrEmpty()) View.GONE else VISIBLE
        binding.secondaryButton.text = task.secondaryAction
        binding.secondaryButton.contentDescription = task.secondaryAction + " " + task.title
    }

    interface Callback {
        fun onViewClick(task: SuggestedEditsTask, secondary: Boolean)
    }
}
