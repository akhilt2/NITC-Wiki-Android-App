package org.akhil.nitcwiki.settings

import android.content.Context
import android.content.Intent
import org.akhil.nitcwiki.activity.SingleFragmentActivity

class NotificationSettingsActivity : SingleFragmentActivity<NotificationSettingsFragment>() {
    public override fun createFragment(): NotificationSettingsFragment {
        setResult(RESULT_OK)
        return NotificationSettingsFragment.newInstance()
    }

    companion object {
        fun newIntent(ctx: Context): Intent {
            return Intent(ctx, NotificationSettingsActivity::class.java)
        }
    }
}
