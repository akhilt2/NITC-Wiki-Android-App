package org.akhil.nitcwiki.edit.wikitext

import org.junit.Test
import org.akhil.nitcwiki.dataclient.mwapi.MwException
import org.akhil.nitcwiki.test.MockRetrofitTest

class WikitextClientTest : MockRetrofitTest() {
    @Test
    @Throws(Throwable::class)
    fun testRequestSuccessHasResults() {
        enqueueFromFile("wikitext.json")
        observable.test().await()
            .assertComplete().assertNoErrors()
            .assertValue { response -> response.query!!.firstPage()!!.revisions[0].contentMain == "\\o/\n\ntest12\n\n3" && response.query!!.firstPage()!!.revisions[0].timeStamp == "2018-03-18T18:10:54Z" }
    }

    @Test
    @Throws(Throwable::class)
    fun testRequestResponseApiError() {
        enqueueFromFile("api_error.json")
        observable.test().await()
            .assertError(MwException::class.java)
    }

    @Test
    @Throws(Throwable::class)
    fun testRequestResponseMalformed() {
        enqueueMalformed()
        observable.test().await()
            .assertError(Exception::class.java)
    }

    private val observable
        get() = apiService.getWikiTextForSectionWithInfo("User:Mhollo/sandbox", 0)
}
