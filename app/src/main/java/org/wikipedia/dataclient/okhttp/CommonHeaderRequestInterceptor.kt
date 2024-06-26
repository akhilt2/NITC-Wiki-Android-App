package org.akhil.nitcwiki.dataclient.okhttp

import okhttp3.Interceptor
import okhttp3.Response
import org.akhil.nitcwiki.WikipediaApp
import org.akhil.nitcwiki.dataclient.RestService
import java.io.IOException

internal class CommonHeaderRequestInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val app = WikipediaApp.instance
        val builder = chain.request().newBuilder()
                .header("User-Agent", app.userAgent)
                .header("X-WMF-UUID", app.appInstallID)
        if (chain.request().url.encodedPath.contains(RestService.PAGE_HTML_ENDPOINT)) {
            builder.header("Accept", RestService.ACCEPT_HEADER_MOBILE_HTML)
        } else if (chain.request().url.host.contains("maps.wikimedia.org")) {
            builder.header("Referer", "https://maps.wikimedia.org/")
        }
        return chain.proceed(builder.build())
    }
}
