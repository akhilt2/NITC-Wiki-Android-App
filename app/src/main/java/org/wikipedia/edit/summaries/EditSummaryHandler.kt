package org.akhil.nitcwiki.edit.summaries

import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.akhil.nitcwiki.database.AppDatabase
import org.akhil.nitcwiki.edit.db.EditSummary
import org.akhil.nitcwiki.page.PageTitle
import org.akhil.nitcwiki.util.L10nUtil.setConditionalTextDirection

class EditSummaryHandler(private val coroutineScope: CoroutineScope,
                         private val container: View,
                         private val summaryEdit: AutoCompleteTextView,
                         title: PageTitle) {

    init {
        container.setOnClickListener { summaryEdit.requestFocus() }
        setConditionalTextDirection(summaryEdit, title.wikiSite.languageCode)

        coroutineScope.launch {
            val summaries = AppDatabase.instance.editSummaryDao().getEditSummaries()
            updateAutoCompleteList(summaries)
        }
    }

    private fun updateAutoCompleteList(editSummaries: List<EditSummary>) {
        val adapter = ArrayAdapter(container.context, android.R.layout.simple_list_item_1, editSummaries)
        summaryEdit.setAdapter(adapter)
    }

    fun show() {
        container.visibility = View.VISIBLE
    }

    fun persistSummary() {
        coroutineScope.launch {
            AppDatabase.instance.editSummaryDao().insertEditSummary(EditSummary(summary = summaryEdit.text.toString()))
        }
    }

    fun handleBackPressed(): Boolean {
        if (container.visibility == View.VISIBLE) {
            container.visibility = View.GONE
            return true
        }
        return false
    }
}
