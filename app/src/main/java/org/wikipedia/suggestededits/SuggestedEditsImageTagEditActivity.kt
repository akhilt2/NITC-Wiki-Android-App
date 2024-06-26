package org.akhil.nitcwiki.suggestededits

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import org.akhil.nitcwiki.Constants
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.WikipediaApp
import org.akhil.nitcwiki.activity.BaseActivity
import org.akhil.nitcwiki.databinding.ActivitySuggestedEditsFeedCardImageTagsBinding
import org.akhil.nitcwiki.dataclient.mwapi.MwQueryPage
import org.akhil.nitcwiki.descriptions.DescriptionEditActivity
import org.akhil.nitcwiki.json.JsonUtil
import org.akhil.nitcwiki.settings.Prefs
import org.akhil.nitcwiki.util.DimenUtil
import org.akhil.nitcwiki.util.ResourceUtil

class SuggestedEditsImageTagEditActivity : BaseActivity(), SuggestedEditsItemFragment.Callback {

    private lateinit var binding: ActivitySuggestedEditsFeedCardImageTagsBinding
    private lateinit var suggestedEditsImageTagsFragment: SuggestedEditsImageTagsFragment
    var page: MwQueryPage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuggestedEditsFeedCardImageTagsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        page = JsonUtil.decodeFromString(intent.getStringExtra(ARG_PAGE))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = getString(R.string.suggested_edits_tag_images)
        setImageZoomHelper()

        suggestedEditsImageTagsFragment = supportFragmentManager.findFragmentById(R.id.imageTagFragment) as SuggestedEditsImageTagsFragment
        suggestedEditsImageTagsFragment.invokeSource = intent.getSerializableExtra(Constants.INTENT_EXTRA_INVOKE_SOURCE) as Constants.InvokeSource

        binding.addContributionButton.setOnClickListener { suggestedEditsImageTagsFragment.publish() }
        binding.addContributionLandscapeImage.setOnClickListener { suggestedEditsImageTagsFragment.publish() }
        maybeShowOnboarding()
    }

    override fun getLangCode(): String {
        return WikipediaApp.instance.languageState.appLanguageCode
    }

    override fun getSinglePage(): MwQueryPage? {
        return page
    }

    override fun updateActionButton() {
        binding.addContributionLandscapeImage.setBackgroundColor(ResourceUtil.getThemedColor(this, R.attr.progressive_color))
        binding.addContributionButton.isEnabled = suggestedEditsImageTagsFragment.publishEnabled()
        binding.addContributionLandscapeImage.isEnabled = suggestedEditsImageTagsFragment.publishEnabled()
        binding.addContributionButton.alpha = if (suggestedEditsImageTagsFragment.publishEnabled()) 1f else 0.5f
        binding.addContributionLandscapeImage.alpha = if (suggestedEditsImageTagsFragment.publishEnabled()) 1f else 0.5f

        if (DimenUtil.isLandscape(this)) {
            binding.addContributionButton.visibility = GONE
            binding.addContributionLandscapeImage.visibility = VISIBLE
        } else {
            binding.addContributionButton.visibility = VISIBLE
            binding.addContributionLandscapeImage.visibility = GONE
            binding.addContributionText.text = getString(R.string.description_edit_save)
        }
    }

    override fun nextPage(sourceFragment: Fragment?) {
        setResult(RESULT_OK, Intent().putExtra(Constants.INTENT_EXTRA_ACTION, DescriptionEditActivity.Action.ADD_IMAGE_TAGS))
        finish()
    }

    override fun logSuccess() {
    }

    private fun maybeShowOnboarding() {
        if (Prefs.showImageTagsOnboarding) {
            Prefs.showImageTagsOnboarding = false
            startActivity(SuggestedEditsImageTagsOnboardingActivity.newIntent(this))
        }
    }

    override fun onBackPressed() {
        if (!suggestedEditsImageTagsFragment.onBackPressed()) {
            return
        }
        super.onBackPressed()
    }

    companion object {
        private const val ARG_PAGE = "imageTagPage"

        fun newIntent(context: Context, page: MwQueryPage, invokeSource: Constants.InvokeSource): Intent {
            return Intent(context, SuggestedEditsImageTagEditActivity::class.java)
                    .putExtra(ARG_PAGE, JsonUtil.encodeToString(page))
                    .putExtra(Constants.INTENT_EXTRA_INVOKE_SOURCE, invokeSource)
        }
    }
}
