package org.akhil.nitcwiki.feed.announcement

import android.location.Location

class GeoIPCookie(
    val country: String,
    val region: String,
    val city: String,
    val location: Location?
)
