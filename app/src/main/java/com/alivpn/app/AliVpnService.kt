package com.alivpn.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat
import java.io.IOException

class AliVpnService : VpnService() {
    companion object {
        @Volatile var isRunning: Boolean = false
        private const val CHANNEL_ID = "alivpn_vpn"
        private const val NOTIFICATION_ID = 42
    }

    private var tun: ParcelFileDescriptor? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
        establishVpn()
        return START_STICKY
    }

    private fun buildNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(android.R.drawable.stat_sys_warning)
        .setContentTitle("AliVPN")
        .setContentText("VPN-интерфейс активен")
        .setOngoing(true)
        .build()

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "AliVPN VPN", NotificationManager.IMPORTANCE_LOW)
            )
        }
    }

    private fun establishVpn() {
        if (tun != null) return
        tun = Builder()
            .setSession("AliVPN")
            .addAddress("10.8.0.2", 24)
            .addDnsServer("1.1.1.1")
            .addDnsServer("8.8.8.8")
            .establish()
        isRunning = tun != null
    }

    override fun onDestroy() {
        isRunning = false
        try { tun?.close() } catch (_: IOException) {}
        tun = null
        super.onDestroy()
    }
}
