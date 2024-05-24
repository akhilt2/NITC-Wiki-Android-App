package org.akhil.nitcwiki.edit.insertmedia

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import org.akhil.nitcwiki.Constants
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.WikipediaApp
import org.akhil.nitcwiki.analytics.eventplatform.ImageRecommendationsEvent
import org.akhil.nitcwiki.commons.ImagePreviewDialog
import org.akhil.nitcwiki.databinding.FragmentInsertMediaSettingsBinding
import org.akhil.nitcwiki.page.ExclusiveBottomSheetPresenter
import org.akhil.nitcwiki.page.LinkMovementMethodExt
import org.akhil.nitcwiki.richtext.RichTextUtil
import org.akhil.nitcwiki.suggestededits.PageSummaryForEdit
import org.akhil.nitcwiki.util.CustomTabsUtil
import org.akhil.nitcwiki.util.DeviceUtil
import org.akhil.nitcwiki.util.FeedbackUtil
import org.akhil.nitcwiki.util.StringUtil
import org.akhil.nitcwiki.views.AppTextViewWithImages
import org.akhil.nitcwiki.views.ViewUtil

class InsertMediaSettingsFragment : Fragment() {

    private lateinit var activity: InsertMediaActivity
    private var _binding: FragmentInsertMediaSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel get() = activity.viewModel
    private var currentVoiceInputParentLayout: View? = null

    val isActive get() = binding.root.visibility == View.VISIBLE
    val alternativeText get() = binding.mediaAlternativeText.text.toString().trim()
    val captionText get() = binding.mediaCaptionText.text.toString().trim()

    private val voiceSearchLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val voiceSearchResult = it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        if (it.resultCode == Activity.RESULT_OK && voiceSearchResult != null) {
            val text = voiceSearchResult.first()
            if (currentVoiceInputParentLayout == binding.mediaCaptionLayout) {
                binding.mediaCaptionText.setText(text)
            } else if (currentVoiceInputParentLayout == binding.mediaAlternativeTextLayout) {
                binding.mediaAlternativeText.setText(text)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInsertMediaSettingsBinding.inflate(layoutInflater, container, false)
        activity = (requireActivity() as InsertMediaActivity)

        binding.mediaCaptionLayout.setEndIconOnClickListener {
            currentVoiceInputParentLayout = binding.mediaCaptionLayout
            sendInsertMediaEvent("tts_open")
            launchVoiceInput()
        }
        binding.mediaAlternativeTextLayout.setEndIconOnClickListener {
            currentVoiceInputParentLayout = binding.mediaAlternativeTextLayout
            sendInsertMediaEvent("tts_open")
            launchVoiceInput()
        }
        binding.advancedSettings.setOnClickListener {
            sendInsertMediaEvent("advanced_setting_open")
            activity.showMediaAdvancedSettingsFragment()
        }
        binding.imageInfoContainer.setOnClickListener {
            sendInsertMediaEvent("image_detail_view")
            viewModel.selectedImage?.let {
                val summary = PageSummaryForEdit(it.prefixedText, WikipediaApp.instance.appOrSystemLanguageCode, it,
                    it.displayText, RichTextUtil.stripHtml(it.description), it.thumbUrl)
                ExclusiveBottomSheetPresenter.show(childFragmentManager,
                    ImagePreviewDialog.newInstance(summary))
            }
        }
        binding.mediaCaptionText.addTextChangedListener {
            if (!activity.isDestroyed) {
                activity.invalidateOptionsMenu()
            }
        }
        binding.mediaAlternativeText.addTextChangedListener {
            if (!activity.isDestroyed) {
                activity.invalidateOptionsMenu()
            }
        }

        binding.mediaCaptionText.setText(activity.intent.getStringExtra(InsertMediaActivity.RESULT_IMAGE_CAPTION))
        binding.mediaAlternativeText.setText(activity.intent.getStringExtra(InsertMediaActivity.RESULT_IMAGE_ALT))

        val movementMethod = LinkMovementMethodExt { url ->
            sendInsertMediaEvent("view_help")
            CustomTabsUtil.openInCustomTab(requireActivity(), url)
        }
        var textView = binding.mediaCaptionLayout.findViewById<AppCompatTextView>(com.google.android.material.R.id.textinput_helper_text)
        textView.setLinkTextColor(textView.currentTextColor)
        var url = getString(R.string.image_captions_style_url)
        var text = StringUtil.fromHtml("<a href=\"" + url + "\">" + getString(R.string.insert_media_settings_caption_description) + " ^1</a>")
        AppTextViewWithImages.setTextWithDrawables(textView, text, R.drawable.ic_open_in_new_black_24px)
        textView.movementMethod = movementMethod

        textView = binding.mediaAlternativeTextLayout.findViewById(com.google.android.material.R.id.textinput_helper_text)
        textView.setLinkTextColor(textView.currentTextColor)
        url = getString(R.string.image_alt_text_style_url)
        text = StringUtil.fromHtml("<a href=\"" + url + "\">" + getString(R.string.insert_media_settings_alternative_text_description) + " ^1</a>")
        AppTextViewWithImages.setTextWithDrawables(textView, text, R.drawable.ic_open_in_new_black_24px)
        textView.movementMethod = movementMethod
        if (viewModel.invokeSource == Constants.InvokeSource.EDIT_ADD_IMAGE && viewModel.selectedImage != null) {
            ImageRecommendationsEvent.logImpression("caption_entry", ImageRecommendationsEvent.getActionDataString(
                filename = viewModel.selectedImage?.prefixedText!!, recommendationSource = viewModel.selectedImageSource,
                recommendationSourceProjects = viewModel.selectedImageSourceProjects, acceptanceState = "accepted"), viewModel.selectedImage?.wikiSite?.languageCode!!)
        }
        return binding.root
    }

    private fun sendInsertMediaEvent(action: String) {
        if (viewModel.invokeSource == Constants.InvokeSource.EDIT_ADD_IMAGE && viewModel.selectedImage != null) {
            ImageRecommendationsEvent.logAction(action, "caption_entry", ImageRecommendationsEvent.getActionDataString(
                filename = viewModel.selectedImage?.prefixedText!!, recommendationSource = viewModel.selectedImageSource,
                recommendationSourceProjects = viewModel.selectedImageSourceProjects, acceptanceState = "accepted"), viewModel.selectedImage?.wikiSite?.languageCode!!)
        }
    }

    private fun launchVoiceInput() {
        try {
            voiceSearchLauncher.launch(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM))
        } catch (a: ActivityNotFoundException) {
            FeedbackUtil.showMessage(requireActivity(), R.string.error_voice_search_not_available)
        }
    }

    fun show() {
        binding.root.isVisible = true
        activity.invalidateOptionsMenu()
        activity.supportActionBar?.title = getString(R.string.insert_media_settings)
        viewModel.selectedImage?.let {
            ViewUtil.loadImageWithRoundedCorners(binding.imageView, it.thumbUrl, true)
            binding.mediaTitle.text = it.text
            binding.mediaDescription.text = StringUtil.removeHTMLTags(it.description.orEmpty().ifEmpty { it.displayText }).trim()
        }
        binding.mediaCaptionLayout.requestFocus()
        DeviceUtil.showSoftKeyboard(binding.mediaCaptionText)
    }

    fun hide(reset: Boolean = true) {
        binding.root.isVisible = false
        activity.invalidateOptionsMenu()
        if (reset) {
            ViewUtil.loadImageWithRoundedCorners(binding.imageView, null)
            binding.mediaDescription.text = null
        }
    }

    fun handleBackPressed(): Boolean {
        if (isActive) {
            sendInsertMediaEvent("back")
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
