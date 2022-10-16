package com.chuckerteam.chucker.internal.support

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.chuckerteam.chucker.R
import com.chuckerteam.chucker.api.Chucker
import com.chuckerteam.chucker.api.datamodel.HttpRequest
import com.chuckerteam.chucker.internal.ui.BaseChuckerActivity

internal class NotificationHelper(val context: Context) {

    companion object {
        private const val TRANSACTIONS_CHANNEL_ID = "chucker_transactions"

        private const val TRANSACTION_NOTIFICATION_ID = 1138

        private const val BUFFER_SIZE = 10
        private const val INTENT_REQUEST_CODE = 11

        private val requestBuffer = mutableListOf<HttpRequest>()

        fun clearBuffer() {
            synchronized(requestBuffer) {
                requestBuffer.clear()
            }
        }
    }

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val transactionsScreenIntent by lazy {
        PendingIntent.getActivity(
            context,
            TRANSACTION_NOTIFICATION_ID,
            Chucker.getLaunchIntent(context),
            PendingIntent.FLAG_UPDATE_CURRENT or immutableFlag()
        )
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val transactionsChannel = NotificationChannel(
                TRANSACTIONS_CHANNEL_ID,
                context.getString(R.string.chucker_network_notification_category),
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannels(listOf(transactionsChannel))
        }
    }

    private fun addToBuffer(request: HttpRequest) {
//        if (transaction.id == 0L) {
//            // Don't store Transactions with an invalid ID (0).
//            // Transaction with an Invalid ID will be shown twice in the notification
//            // with both the invalid and the valid ID and we want to avoid this.
//            return
//        }
        synchronized(requestBuffer) {
            requestBuffer.add(request)
            if (requestBuffer.size > BUFFER_SIZE) {
                requestBuffer.removeAt(0)
            }
        }
    }

    private fun canShowNotifications(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notificationManager.areNotificationsEnabled()
        } else {
            true
        }
    }

    fun show(request: HttpRequest) {
        addToBuffer(request)
        if (!BaseChuckerActivity.isInForeground && canShowNotifications()) {
            val builder =
                NotificationCompat.Builder(context, TRANSACTIONS_CHANNEL_ID)
                    .setContentIntent(transactionsScreenIntent)
                    .setLocalOnly(true)
                    .setSmallIcon(R.drawable.chucker_ic_transaction_notification)
                    .setColor(ContextCompat.getColor(context, R.color.chucker_color_primary))
                    .setContentTitle(context.getString(R.string.chucker_http_notification_title))
                    .setAutoCancel(true)
                    .addAction(createClearAction())
            val inboxStyle = NotificationCompat.InboxStyle()
            synchronized(requestBuffer) {
                var count = 0
                (requestBuffer.size - 1 downTo 0).forEach { i ->
                    val bufferedRequest = requestBuffer[i]
                    if (count < BUFFER_SIZE) {
                        if (count == 0) {
                            // TODO Change to text
                            builder.setContentText(bufferedRequest.path)
                        }
                        inboxStyle.addLine(bufferedRequest.path)
                    }
                    count++
                }
                builder.setStyle(inboxStyle)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    builder.setSubText(requestBuffer.size.toString())
                } else {
                    builder.setNumber(requestBuffer.size)
                }
            }
            notificationManager.notify(TRANSACTION_NOTIFICATION_ID, builder.build())
        }
    }

    private fun createClearAction():
        NotificationCompat.Action {
        val clearTitle = context.getString(R.string.chucker_clear)
        val clearRequestsBroadcastIntent =
            Intent(context, ClearDatabaseJobIntentServiceReceiver::class.java)
        val pendingBroadcastIntent = PendingIntent.getBroadcast(
            context,
            INTENT_REQUEST_CODE,
            clearRequestsBroadcastIntent,
            PendingIntent.FLAG_ONE_SHOT or immutableFlag()
        )
        return NotificationCompat.Action(
            R.drawable.chucker_ic_delete_white,
            clearTitle,
            pendingBroadcastIntent
        )
    }

    fun dismissNotifications() {
        notificationManager.cancel(TRANSACTION_NOTIFICATION_ID)
    }

    private fun immutableFlag() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        PendingIntent.FLAG_IMMUTABLE
    } else {
        0
    }

//    private fun notificationMessage(httpRequest: HttpRequest): String {
//        return when (httpRequest.status) {
//            HttpTransaction.Status.Failed -> " ! ! !  $method $path"
//            HttpTransaction.Status.Requested -> " . . .  $method $path"
//            else -> "$responseCode $method $path"
//        }
//    }
}
