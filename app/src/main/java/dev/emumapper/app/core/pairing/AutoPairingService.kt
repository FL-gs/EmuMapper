package dev.emumapper.app.core.pairing

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dev.emumapper.app.R
import dev.emumapper.app.core.app.appGraph
import dev.emumapper.app.core.utils.AppLogger
import dev.emumapper.app.core.utils.LogTags

class AutoPairingService : Service() {
    override fun onCreate() {
        super.onCreate()
        AppLogger.d(LogTags.SERVICE, "service | onCreate")

        createNotificationChannel()

        applicationContext.appGraph.pairingEngine.attachHost(HOST_ID)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        AppLogger.d(
            LogTags.SERVICE,
            "service | onStartCommand | startId=$startId | flags=$flags"
        )

        startForeground(NOTIFICATION_ID, buildNotification())

        return START_STICKY
    }

    override fun onDestroy() {
        AppLogger.d(LogTags.SERVICE, "service | onDestroy")

        applicationContext.appGraph.pairingEngine.detachHost(HOST_ID)

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.auto_pairing_service_title))
            .setContentText(getString(R.string.auto_pairing_service_text))
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.auto_pairing_service_channel_name),
            NotificationManager.IMPORTANCE_LOW
        )
        manager.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "auto_pairing_runtime"
        private const val NOTIFICATION_ID = 1001
        private const val HOST_ID = "service"

        fun start(context: Context) {
            val intent = Intent(context, AutoPairingService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, AutoPairingService::class.java))
        }
    }
}
