package ps.petugas.salam.wakatobi.services


import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.orhanobut.logger.Logger

import org.json.JSONException
import org.json.JSONObject

import ps.petugas.salam.wakatobi.R
import ps.petugas.salam.wakatobi.ui.detaillaporan.VDetailReport
import ps.petugas.salam.wakatobi.ui.mainscreen.VMainScreen

/**
 * ----------------------------------------------
 * Created by ukieTux on 12/31/16 with ♥ .
 * @email : ukie.tux@gmail.com
 * @github : https://www.github.com/tuxkids
 * * ----------------------------------------------
 * * © 2016 | All Rights Reserved
 */

class MyFirabaseMessagingService : FirebaseMessagingService() {


    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        Logger.d("Lacak 1")
        /**
         * Receive from console firebase
         */
        if (remoteMessage!!.data.isNotEmpty()) {
            try {
                val jsonObject = JSONObject(remoteMessage.data)
                sendNotification(jsonObject)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun sendNotification(jsonObject: JSONObject) {
        try {
            //get root json
            Logger.d(jsonObject.toString())
            val messageTitle = jsonObject.getString("title")
            val messageBody = jsonObject.getString("text")
            val idReport = jsonObject.getString("id_laporan")

            val detailReport = Intent(applicationContext, VMainScreen::class.java)
            detailReport.putExtra("id_report", idReport)
            detailReport.putExtra("notif", true)
            detailReport.putExtra("type", "report")
            detailReport.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            val pendingIntent = PendingIntent.getActivity(applicationContext, 0, detailReport, PendingIntent.FLAG_ONE_SHOT)
            Logger.d("Lacak _Detail Report")

            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notifyBuilder = NotificationCompat.Builder(applicationContext)
            val inboxStyle = NotificationCompat.InboxStyle()
            inboxStyle.addLine(messageBody)
            val notification = notifyBuilder
                    .setSmallIcon(R.drawable.logo_icon)
                    .setTicker(messageTitle)
                    .setAutoCancel(true)
                    .setContentTitle(messageTitle)
                    .setContentIntent(pendingIntent)
                    .setSound(defaultSoundUri)
                    .setStyle(inboxStyle)
                    .setWhen(System.currentTimeMillis())
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.logo_icon))
                    .setContentText(messageBody)
                    .build()
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(1, notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
