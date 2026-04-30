package com.smartbudget.app.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationHelper {

    private const val CHANNEL_ID   = "budget_alerts"
    private const val CHANNEL_NAME = "Alertes Budget"
    private const val TAG          = "NotificationHelper"

    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications de depassement de budget mensuel"
            enableLights(true)
            enableVibration(true)
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        manager.createNotificationChannel(channel)
        Log.d(TAG, "Canal de notification créé")
    }

    fun sendGlobalBudgetAlert(
        context: Context,
        totalSpent: Double,
        limit: Double = 4000.0
    ) {
        val manager = NotificationManagerCompat.from(context)

        // Log pour déboguer
        Log.d(TAG, "Tentative notification — enabled: ${manager.areNotificationsEnabled()}")

        val title   = "\uD83D\uDEA8 Budget mensuel dépassé !"
        val message = "Total ce mois : ${String.format("%.0f", totalSpent)} MAD " +
                "(limite : ${String.format("%.0f", limit)} MAD)"

        val notif = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_MAX)   // MAX au lieu de HIGH
            .setDefaults(NotificationCompat.DEFAULT_ALL)    // son + vibration
            .setAutoCancel(true)
            .build()

        try {
            manager.notify(99999, notif)
            Log.d(TAG, "Notification envoyée avec succès")
        } catch (e: SecurityException) {
            Log.e(TAG, "Permission refusée : ${e.message}")
        }
    }

    fun sendBudgetAlert(
        context: Context,
        categoryName: String,
        spent: Double,
        limit: Double
    ) {
        val manager = NotificationManagerCompat.from(context)
        val percent = ((spent / limit) * 100).toInt()
        val title   = "Dépassement — $categoryName"
        val message = "Dépensé : ${String.format("%.0f", spent)} MAD " +
                "/ ${String.format("%.0f", limit)} MAD ($percent%)"

        val notif = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .build()

        try {
            manager.notify(categoryName.hashCode(), notif)
        } catch (e: SecurityException) {
            Log.e(TAG, "Permission refusée : ${e.message}")
        }
    }
}