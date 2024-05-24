package org.akhil.nitcwiki.onboarding

import android.content.Context
import android.content.Intent
import org.akhil.nitcwiki.activity.SingleFragmentActivity
import org.akhil.nitcwiki.settings.Prefs

class InitialOnboardingActivity : SingleFragmentActivity<InitialOnboardingFragment>(), OnboardingFragment.Callback {
    override fun onSkip() {}

    override fun onComplete() {
        setResult(RESULT_OK)
        Prefs.isInitialOnboardingEnabled = false
        finish()
    }

    override fun onBackPressed() {
        if (fragment.onBackPressed()) {
            return
        }
        setResult(RESULT_OK)
        finish()
        super.onBackPressed()
    }

    override fun createFragment(): InitialOnboardingFragment {
        return InitialOnboardingFragment.newInstance()
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, InitialOnboardingActivity::class.java)
        }
    }
}
