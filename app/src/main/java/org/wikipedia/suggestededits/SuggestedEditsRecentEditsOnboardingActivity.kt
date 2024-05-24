package org.akhil.nitcwiki.suggestededits

import android.content.Context
import android.content.Intent
import org.akhil.nitcwiki.activity.SingleFragmentActivity
import org.akhil.nitcwiki.analytics.eventplatform.PatrollerExperienceEvent
import org.akhil.nitcwiki.onboarding.OnboardingFragment

class SuggestedEditsRecentEditsOnboardingActivity : SingleFragmentActivity<SuggestedEditsRecentEditsOnboardingFragment>(), OnboardingFragment.Callback {
    override fun onSkip() {
        PatrollerExperienceEvent.logAction("onboarding_skip", "pt_onboarding")
    }

    override fun onComplete() {
        PatrollerExperienceEvent.logAction("get_started", "pt_onboarding")
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        PatrollerExperienceEvent.logAction("back", "pt_onboarding")
    }

    override fun createFragment(): SuggestedEditsRecentEditsOnboardingFragment {
        return SuggestedEditsRecentEditsOnboardingFragment.newInstance()
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, SuggestedEditsRecentEditsOnboardingActivity::class.java)
        }
    }
}
