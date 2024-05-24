package org.akhil.nitcwiki.analytics.eventplatform

import kotlinx.coroutines.runBlocking
import org.akhil.nitcwiki.WikipediaApp
import org.akhil.nitcwiki.auth.AccountUtil
import org.akhil.nitcwiki.settings.Prefs
import org.akhil.nitcwiki.util.log.L

class MachineGeneratedArticleDescriptionABCTest : ABTest("mBART25", GROUP_SIZE_2) {

    override fun assignGroup() {
        super.assignGroup()
        if (AccountUtil.isLoggedIn) {
            runBlocking {
                try {
                    MachineGeneratedArticleDescriptionsAnalyticsHelper.setUserExperienced()
                    if (testGroup == GROUP_2 && Prefs.suggestedEditsMachineGeneratedDescriptionsIsExperienced) {
                        testGroup = GROUP_3
                    }
                } catch (e: Exception) {
                    L.e(e)
                }
            }
        }
        MachineGeneratedArticleDescriptionsAnalyticsHelper().logGroupAssigned(WikipediaApp.instance, testGroup)
    }
}
