package org.akhil.nitcwiki.login

import org.akhil.nitcwiki.dataclient.WikiSite

internal class LoginResetPasswordResult(site: WikiSite, status: String, userName: String?,
    password: String?, message: String?) : LoginResult(site, status, userName, password, message)
