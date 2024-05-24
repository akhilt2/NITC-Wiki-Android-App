package org.akhil.nitcwiki.talk

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.akhil.nitcwiki.Constants
import org.akhil.nitcwiki.R
import org.akhil.nitcwiki.auth.AccountUtil
import org.akhil.nitcwiki.csrf.CsrfTokenClient
import org.akhil.nitcwiki.dataclient.ServiceFactory
import org.akhil.nitcwiki.dataclient.WikiSite
import org.akhil.nitcwiki.dataclient.okhttp.HttpStatusException
import org.akhil.nitcwiki.edit.Edit
import org.akhil.nitcwiki.page.PageTitle
import org.akhil.nitcwiki.util.StringUtil
import org.akhil.nitcwiki.util.log.L
import java.util.concurrent.TimeUnit

object NotificationDirectReplyHelper {
    const val DIRECT_REPLY_EDIT_COMMENT = "#directreply-1.0"

    // TODO: update this to use DiscussionTools API, and enable.
    fun handleReply(context: Context, wiki: WikiSite, title: PageTitle, replyText: String,
                    replyTo: String, notificationId: Int) {
        Toast.makeText(context, context.getString(R.string.notifications_direct_reply_progress, replyTo), Toast.LENGTH_SHORT).show()

        Observable.zip(CsrfTokenClient.getToken(wiki).subscribeOn(Schedulers.io()),
            ServiceFactory.getRest(wiki).getTalkPage(title.prefixedText).subscribeOn(Schedulers.io())) { token, response ->
            Pair(token, response)
        }.subscribeOn(Schedulers.io())
            .flatMap { pair ->
                val topic = pair.second.topics!!.find {
                    it.id > 0 && it.html?.trim().orEmpty() == StringUtil.removeUnderscores(title.fragment)
                }
                if (topic == null || title.fragment.isNullOrEmpty()) {
                    Observable.just(Edit())
                } else {
                    ServiceFactory.get(wiki).postEditSubmit(
                        title.prefixedText, topic.id.toString(), null,
                        DIRECT_REPLY_EDIT_COMMENT, if (AccountUtil.isLoggedIn) "user" else null, null, replyText,
                        pair.second.revision, pair.first, null, null
                    )
                }
            }
            .subscribe({
                if (it.edit?.editSucceeded == true) {
                    waitForUpdatedRevision(context, wiki, title, it.edit.newRevId, notificationId)
                } else {
                    fallBackToTalkPage(context, title)
                }
            }, {
                L.e(it)
                fallBackToTalkPage(context, title)
            })
    }

    private fun waitForUpdatedRevision(context: Context, wiki: WikiSite, title: PageTitle,
                                       newRevision: Long, notificationId: Int) {
        ServiceFactory.getRest(wiki).getTalkPage(title.prefixedText)
            .delay(2, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .map {
                if (it.revision < newRevision) {
                    throw IllegalStateException()
                }
                it.revision
            }
            .retry(20) {
                (it is IllegalStateException) || (it is HttpStatusException && it.code == 404)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnTerminate {
                cancelNotification(context, notificationId)
            }
            .subscribe({
                // revisionForUndo = it
                Toast.makeText(context, R.string.notifications_direct_reply_success, Toast.LENGTH_LONG).show()
            }, {
                L.e(it)
                fallBackToTalkPage(context, title)
            })
    }

    private fun fallBackToTalkPage(context: Context, title: PageTitle) {
        Toast.makeText(context, R.string.notifications_direct_reply_error, Toast.LENGTH_LONG).show()
        context.startActivity(TalkTopicsActivity.newIntent(context, title, Constants.InvokeSource.NOTIFICATION))
    }

    private fun cancelNotification(context: Context, notificationId: Int) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
            return
        }
        context.getSystemService<NotificationManager>()?.activeNotifications?.find {
            it.id == notificationId
        }?.run {
            val n = NotificationCompat.Builder(context, this.notification)
                    .setRemoteInputHistory(null)
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .setVibrate(null)
                    .setTimeoutAfter(1)
                    .build()
            NotificationManagerCompat.from(context).notify(notificationId, n)
        }
    }
}
