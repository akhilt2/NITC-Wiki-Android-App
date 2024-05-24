package org.akhil.nitcwiki.captcha

import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.edit.EditResult

// Handles only Image Captchas
class CaptchaResult(val captchaId: String) : EditResult("Failure") {
    fun getCaptchaUrl(wiki: WikiSite): String {
        return wiki.url("index.php") + "?title=Special:Captcha/image&wpCaptchaId=" + captchaId
    }
}
