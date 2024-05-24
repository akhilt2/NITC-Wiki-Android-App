package org.akhil.nitcwiki.edit.templates

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.databinding.FragmentInsertTemplateBinding
import org.akhil.nitcwiki.databinding.ItemInsertTemplateBinding
import org.akhil.nitcwiki.dataclient.mwapi.TemplateDataResponse
import org.akhil.nitcwiki.page.LinkMovementMethodExt
import org.akhil.nitcwiki.page.PageTitle
import org.akhil.nitcwiki.util.StringUtil
import org.akhil.nitcwiki.util.UriUtil
import org.akhil.nitcwiki.views.PlainPasteEditText

class InsertTemplateFragment : Fragment() {

    private lateinit var activity: TemplatesSearchActivity
    private var _binding: FragmentInsertTemplateBinding? = null
    private val binding get() = _binding!!
    val isActive get() = binding.root.visibility == View.VISIBLE

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInsertTemplateBinding.inflate(layoutInflater, container, false)
        activity = (requireActivity() as TemplatesSearchActivity)
        activity.supportActionBar?.title = null
        return binding.root
    }

    private fun buildParamsInputFields(templateData: TemplateDataResponse.TemplateData) {
        activity.updateInsertButton(true)
        binding.templateDataParamsContainer.removeAllViews()
        templateData.getParams?.filter {
            !it.value.isDeprecated
        }?.forEach {
            val itemBinding = ItemInsertTemplateBinding.inflate(layoutInflater)
            val labelText = it.value.label.orEmpty().ifEmpty { StringUtil.capitalize(it.key) }
            itemBinding.root.tag = false
            if (it.value.required) {
                itemBinding.textInputLayout.hint = labelText
                itemBinding.editText.addTextChangedListener {
                    if (!activity.isDestroyed) {
                        checkRequiredParams()
                    }
                }
                itemBinding.root.tag = true
                // Make the insert button disable when require param shows up.
                activity.updateInsertButton(false)
            } else if (it.value.suggested) {
                itemBinding.textInputLayout.hint = getString(R.string.templates_param_suggested_hint, labelText)
            } else {
                itemBinding.textInputLayout.hint = getString(R.string.templates_param_optional_hint, labelText)
            }
            itemBinding.textInputLayout.tag = it.key
            val hintText = it.value.suggestedValues.firstOrNull()
            if (!hintText.isNullOrEmpty()) {
                itemBinding.textInputLayout.placeholderText = getString(R.string.templates_param_suggested_value, hintText)
            }
            itemBinding.textInputLayout.helperText = it.value.description
            binding.templateDataParamsContainer.addView(itemBinding.root)
        }
    }

    private fun checkRequiredParams() {
        val allRequiredParamsFilled = !binding.templateDataParamsContainer.children
            .any { it.tag == true && it.findViewById<PlainPasteEditText>(R.id.editText).text.toString().trim().isEmpty() }
        activity.updateInsertButton(allRequiredParamsFilled)
    }

    fun show(pageTitle: PageTitle, templateData: TemplateDataResponse.TemplateData) {
        activity.sendPatrollerExperienceEvent("search_success", "pt_templates")
        binding.root.isVisible = true
        binding.templateDataTitle.text = StringUtil.removeNamespace(pageTitle.displayText)
        binding.templateDataDescription.text = StringUtil.fromHtml(getTemplateDescription(templateData))
        binding.templateDataDescription.isVisible = !binding.templateDataDescription.text.isNullOrEmpty()
        binding.templateDataMissing.isVisible = templateData.noTemplateData
        binding.templateDataMissingText.text = StringUtil.fromHtml(getString(R.string.templates_description_missing_data,
            getString(R.string.template_parameters_url), getString(R.string.autogenerated_parameters_url)))
        binding.templateDataMissingText.movementMethod = LinkMovementMethodExt.getExternalLinkMovementMethod()
        binding.templateDataLearnMoreButton.setOnClickListener {
            activity.sendPatrollerExperienceEvent("learn_click", "pt_templates")
            UriUtil.visitInExternalBrowser(requireContext(), Uri.parse(pageTitle.uri))
        }
        buildParamsInputFields(templateData)
    }

    private fun getTemplateDescription(templateData: TemplateDataResponse.TemplateData): String {
        return if (templateData.description.isNullOrEmpty()) {
            getString(R.string.templates_description_empty, templateData.title)
        } else {
            templateData.description + "<br><i>" + getString(R.string.templates_description_incomplete) + "</i>"
        }
    }

    fun hide() {
        binding.root.isVisible = false
        activity.invalidateOptionsMenu()
    }

    fun collectParamsInfoAndBuildWikiText(): String {
        var wikiText = "{{"
        wikiText += binding.templateDataTitle.text
        binding.templateDataParamsContainer.children.iterator().forEach {
            var label = it.findViewById<TextInputLayout>(R.id.textInputLayout).tag as String
            label = if (label.toIntOrNull() != null) "" else "$label="
            val editText = it.findViewById<PlainPasteEditText>(R.id.editText).text.toString().trim()
            if (editText.isNotEmpty()) {
                wikiText += "|$label$editText"
            }
        }
        wikiText += "}}"
        return wikiText
    }

    fun handleBackPressed(): Boolean {
        if (isActive) {
            hide()
            return true
        }
        return false
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
