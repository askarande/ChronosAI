package com.chronos.app.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import com.chronos.app.ChronosApplication.Companion.CHANNEL_FLOATING
import com.chronos.app.ui.theme.OrbitronFamily
import java.text.SimpleDateFormat
import java.util.*

class FloatingClockService : Service() {

    private lateinit var windowManager: WindowManager
    private var floatView: ComposeView? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        startForeground(300, buildNotification())
        showFloatingClock()
    }

    private fun showFloatingClock() {
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 16; y = 80
        }

        floatView = ComposeView(this).apply {
            setContent {
                val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                Box(
                    modifier = Modifier
                        .background(Color(0xCC0A0A0F), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = time,
                        fontFamily = OrbitronFamily,
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color(0xFF00F5D4)
                    )
                }
            }
        }
        windowManager.addView(floatView, params)
    }

    private fun buildNotification() = NotificationCompat.Builder(this, CHANNEL_FLOATING)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("Chronos Floating Clock")
        .setContentText("Tap to manage")
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .build()

    override fun onDestroy() {
        floatView?.let { windowManager.removeView(it) }
        super.onDestroy()
    }
}
