package org.akhil.nitcwiki.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.databinding.ViewEditHistoryStatsBinding
import org.akhil.nitcwiki.history.HistoryEntry
import org.akhil.nitcwiki.page.LinkMovementMethodExt
import org.akhil.nitcwiki.page.PageActivity
import org.akhil.nitcwiki.page.PageTitle
import org.akhil.nitcwiki.page.edithistory.EditHistoryListViewModel
import org.akhil.nitcwiki.util.DateUtil
import org.akhil.nitcwiki.util.DimenUtil
import org.akhil.nitcwiki.util.FeedbackUtil
import org.akhil.nitcwiki.util.StringUtil
import java.time.LocalDateTime

class EditHistoryStatsView constructor(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {

    val binding = ViewEditHistoryStatsBinding.inflate(LayoutInflater.from(context), this)

    init {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val padding = DimenUtil.roundedDpToPx(16f)
        setPadding(padding, 0, padding, 0)
    }

    fun setup(pageTitle: PageTitle, editHistoryStats: EditHistoryListViewModel.EditHistoryStats?) {
        binding.articleTitleView.text = StringUtil.fromHtml(context.getString(R.string.page_edit_history_activity_title,
                "<a href=\"#\">${pageTitle.displayText}</a>"))
        editHistoryStats?.let { stats ->
            val timestamp = stats.revision.timeStamp
            if (timestamp.isNotBlank()) {
                val createdYear = DateUtil.getYearOnlyDateString(DateUtil.iso8601DateParse(timestamp))
                val localDateTime = LocalDateTime.now()
                val today = DateUtil.getShortDateString(localDateTime.toLocalDate())
                val lastYear = DateUtil.getShortDateString(localDateTime.minusYears(1).toLocalDate())
                binding.editCountsView.text = context.resources.getQuantityString(R.plurals.page_edit_history_article_edits_since_year,
                    stats.allEdits.count, stats.allEdits.count, createdYear)
                binding.statsGraphView.setData(stats.metrics.map { it.edits.toFloat() })
                binding.statsGraphView.contentDescription = context.getString(R.string.page_edit_history_metrics_content_description, lastYear, today)
                FeedbackUtil.setButtonTooltip(binding.statsGraphView)
            }
        }
        binding.articleTitleView.movementMethod = LinkMovementMethodExt { _ ->
            context.startActivity(PageActivity.newIntentForNewTab(context, HistoryEntry(pageTitle, HistoryEntry.SOURCE_EDIT_HISTORY), pageTitle))
        }
    }
}
