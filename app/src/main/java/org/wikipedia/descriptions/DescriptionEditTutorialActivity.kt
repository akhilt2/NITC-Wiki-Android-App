package org.akhil.nitcwiki.descriptions

import android.content.Context
import android.content.Intent
import org.akhil.nitcwiki.activity.SingleFragmentActivity
import org.akhil.nitcwiki.onboarding.OnboardingFragment

class DescriptionEditTutorialActivity : SingleFragmentActivity<DescriptionEditTutorialFragment>(), OnboardingFragment.Callback {
    override fun onSkip() {}

    override fun onComplete() {
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun createFragment(): DescriptionEditTutorialFragment {
        return DescriptionEditTutorialFragment.newInstance(intent.getBooleanExtra(SHOULD_SHOW_AI_ON_BOARDING, false))
    }

    companion object {
        const val SHOULD_SHOW_AI_ON_BOARDING = "showAIOnBoarding"
        fun newIntent(context: Context, showAIOnBoarding: Boolean): Intent {
            return Intent(context, DescriptionEditTutorialActivity::class.java)
                .putExtra(SHOULD_SHOW_AI_ON_BOARDING, showAIOnBoarding)
        }
    }
}
