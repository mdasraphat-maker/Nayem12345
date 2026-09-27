package com.example

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.net.Uri
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class MayaAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "MayaAccessibility"
        @Volatile
        var instance: MayaAccessibilityService? = null
            private set

        fun isRunning(): Boolean = instance != null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.d(TAG, "MayaAccessibilityService connected successfully")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        instance = null
        Log.d(TAG, "MayaAccessibilityService unbound")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Track interactive screen events if needed
    }

    override fun onInterrupt() {
        Log.w(TAG, "MayaAccessibilityService interrupted")
    }

    /**
     * Launch any app by package name
     */
    fun openApp(packageName: String): Boolean {
        return try {
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(launchIntent)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error opening package $packageName: ${e.message}")
            false
        }
    }

    /**
     * Direct call through system telephony
     */
    fun makeDirectCall(phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$phoneNumber")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Log.w(TAG, "ACTION_CALL failed, falling back to ACTION_DIAL: ${e.message}")
            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(dialIntent)
        }
    }

    /**
     * Auto scroll down gesture (useful for Facebook, TikTok, YouTube feeds)
     */
    fun performAutoScrollDown(): Boolean {
        return try {
            val metrics = resources.displayMetrics
            val width = metrics.widthPixels.toFloat()
            val height = metrics.heightPixels.toFloat()

            val startX = width / 2f
            val startY = height * 0.75f
            val endY = height * 0.25f

            val path = Path().apply {
                moveTo(startX, startY)
                lineTo(startX, endY)
            }
            val stroke = GestureDescription.StrokeDescription(path, 0, 450)
            val gesture = GestureDescription.Builder().addStroke(stroke).build()
            dispatchGesture(gesture, null, null)
        } catch (e: Exception) {
            Log.e(TAG, "Error dispatching scroll down gesture: ${e.message}")
            false
        }
    }

    /**
     * Auto scroll up gesture
     */
    fun performAutoScrollUp(): Boolean {
        return try {
            val metrics = resources.displayMetrics
            val width = metrics.widthPixels.toFloat()
            val height = metrics.heightPixels.toFloat()

            val startX = width / 2f
            val startY = height * 0.25f
            val endY = height * 0.75f

            val path = Path().apply {
                moveTo(startX, startY)
                lineTo(startX, endY)
            }
            val stroke = GestureDescription.StrokeDescription(path, 0, 450)
            val gesture = GestureDescription.Builder().addStroke(stroke).build()
            dispatchGesture(gesture, null, null)
        } catch (e: Exception) {
            Log.e(TAG, "Error dispatching scroll up gesture: ${e.message}")
            false
        }
    }

    fun triggerBack(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_BACK)
    }

    fun triggerHome(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_HOME)
    }
}
