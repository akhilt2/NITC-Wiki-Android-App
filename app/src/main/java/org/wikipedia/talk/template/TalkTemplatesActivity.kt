package org.akhil.nitcwiki.talk.template

import android.content.Context
import android.content.Intent
import org.akhil.nitcwiki.Constants
import org.akhil.nitcwiki.activity.SingleFragmentActivity
import org.akhil.nitcwiki.extensions.parcelableExtra
import org.akhil.nitcwiki.page.PageTitle
import org.akhil.nitcwiki.talk.TalkReplyActivity
import org.akhil.nitcwiki.talk.TalkReplyActivity.Companion.EXTRA_TEMPLATE_MANAGEMENT

class TalkTemplatesActivity : SingleFragmentActivity<TalkTemplatesFragment>() {
    public override fun createFragment(): TalkTemplatesFragment {
        return TalkTemplatesFragment.newInstance(intent.parcelableExtra(Constants.ARG_TITLE),
            intent.getBooleanExtra(EXTRA_TEMPLATE_MANAGEMENT, false),
            intent.getLongExtra(TalkReplyActivity.FROM_REVISION_ID, -1),
            intent.getLongExtra(TalkReplyActivity.TO_REVISION_ID, -1))
    }

    companion object {
        fun newIntent(context: Context, pageTitle: PageTitle?, templateManagement: Boolean = false, fromRevisionId: Long = -1, toRevisionId: Long = -1): Intent {
            return Intent(context, TalkTemplatesActivity::class.java)
                .putExtra(Constants.ARG_TITLE, pageTitle)
                .putExtra(EXTRA_TEMPLATE_MANAGEMENT, templateManagement)
                .putExtra(TalkReplyActivity.FROM_REVISION_ID, fromRevisionId)
                .putExtra(TalkReplyActivity.TO_REVISION_ID, toRevisionId)
        }
    }
}
