package org.akhil.nitcwiki.page

import android.net.Uri
import android.text.Selection
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.method.Touch
import android.text.style.URLSpan
import android.view.MotionEvent
import android.widget.TextView
import androidx.core.text.getSpans
import org.akhil.nitcwiki.WikipediaApp
import org.akhil.nitcwiki.analytics.eventplatform.BreadCrumbLogEvent
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.util.UriUtil
import org.akhil.nitcwiki.util.log.L

class LinkMovementMethodExt : LinkMovementMethod {

    fun interface UrlHandler {
        fun onUrlClick(url: String)
    }

    fun interface UrlHandlerWithText {
        fun onUrlClick(url: String, titleString: String?, linkText: String)
    }

    fun interface UrlHandlerWithTextAndCoords {
        fun onUrlClick(url: String, titleString: String?, linkText: String, x: Int, y: Int)
    }

    private var handler: UrlHandler? = null
    private var handlerWithText: UrlHandlerWithText? = null
    private var handlerWithTextAndCoords: UrlHandlerWithTextAndCoords? = null

    constructor(handler: UrlHandler?) {
        this.handler = handler
    }

    constructor(handler: UrlHandlerWithText?) {
        handlerWithText = handler
    }

    constructor(handler: UrlHandlerWithTextAndCoords?) {
        handlerWithTextAndCoords = handler
    }

    override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {
        val action = event.action
        if (action == MotionEvent.ACTION_UP) {
            val x = event.x.toInt() - widget.totalPaddingLeft + widget.scrollX
            val y = event.y.toInt() - widget.totalPaddingTop + widget.scrollY
            val layout = widget.layout
            val line = layout.getLineForVertical(y)

            // Avoid links being activated by touches outside the line bounds.
            // Implementation taken from LinkMovementMethodCompat
            if (y !in 0..layout.height ||
                x.toFloat() !in layout.getLineLeft(line)..layout.getLineRight(line)
            ) {
                Selection.removeSelection(buffer)
                return Touch.onTouchEvent(widget, buffer, event)
            }

            val off = layout.getOffsetForHorizontal(line, x.toFloat())
            val links = buffer.getSpans<URLSpan>(off, off)
            if (links.isNotEmpty()) {
                val linkText = try {
                    buffer.subSequence(buffer.getSpanStart(links[0]), buffer.getSpanEnd(links[0])).toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                    ""
                }
                L.d(linkText)
                val url = UriUtil.decodeURL(links[0].url)

                BreadCrumbLogEvent.logClick(widget.context, widget)

                handler?.onUrlClick(url)

                handlerWithText?.onUrlClick(url, UriUtil.getTitleFromUrl(url), linkText)

                handlerWithTextAndCoords?.onUrlClick(url, UriUtil.getTitleFromUrl(url), linkText,
                    event.rawX.toInt(), event.rawY.toInt())

                return true
            }
        }
        return super.onTouchEvent(widget, buffer, event)
    }

    internal class ErrorLinkHandler internal constructor(override var wikiSite: WikiSite) : LinkHandler(WikipediaApp.instance) {
        override fun onMediaLinkClicked(title: PageTitle) {}
        override fun onDiffLinkClicked(title: PageTitle, revisionId: Long) {}
        override fun onPageLinkClicked(anchor: String, linkText: String) {}
        override fun onInternalLinkClicked(title: PageTitle) {
            // Explicitly send everything to an external browser, since the error might be shown in
            // a child activity of PageActivity, and we don't want to lose our place.
            UriUtil.visitInExternalBrowser(WikipediaApp.instance,
                    Uri.parse(UriUtil.resolveProtocolRelativeUrl(title.wikiSite, title.mobileUri)))
        }
    }

    companion object {
        fun getExternalLinkMovementMethod(wikiSite: WikiSite = WikipediaApp.instance.wikiSite): LinkMovementMethodExt {
            return LinkMovementMethodExt(ErrorLinkHandler(wikiSite))
        }
    }
}
