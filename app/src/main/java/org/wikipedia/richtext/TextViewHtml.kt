package org.akhil.nitcwiki.richtext

import android.widget.TextView

fun TextView.setHtml(source: String?) {
    this.text = CustomHtmlParser.fromHtml(source, this).trim()
}
