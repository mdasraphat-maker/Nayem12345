package com.example.actions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.AlarmClock
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.MayaAccessibilityService

sealed class ActionResult {
    data class Success(val messageBn: String, val actionTag: String) : ActionResult()
    data class Failure(val reasonBn: String) : ActionResult()
    object NotAnAction : ActionResult()
}

object SystemActionDispatcher {
    private const val TAG = "SystemActionDispatcher"

    // Supported App Packages
    val APP_PACKAGES = mapOf(
        "tiktok" to "com.zhiliaoapp.musically",
        "imo" to "com.imo.android.imoim",
        "youtube" to "com.google.android.youtube",
        "facebook" to "com.facebook.katana",
        "whatsapp" to "com.whatsapp",
        "messenger" to "com.facebook.orca",
        "chrome" to "com.android.chrome",
        "maps" to "com.google.android.apps.maps",
        "calculator" to "com.google.android.calculator"
    )

    fun tryParseAndExecute(context: Context, command: String): ActionResult {
        val lower = command.lowercase().trim()

        // 1. Accessibility gestures
        if (lower.contains("স্ক্রোল ডাউন") || lower.contains("নিচে স্ক্রোল") || lower.contains("scroll down")) {
            val service = MayaAccessibilityService.instance
            return if (service != null) {
                service.performAutoScrollDown()
                ActionResult.Success("স্ক্রিনে নিচের দিকে স্ক্রোল করছি...", "GESTURE_SCROLL_DOWN")
            } else {
                ActionResult.Failure("অ্যাক্সেসিবিলিটি সার্ভিস সক্রিয় করা নেই। দয়া করে সেটিংস থেকে চালু করুন।")
            }
        }

        if (lower.contains("স্ক্রোল আপ") || lower.contains("উপরে স্ক্রোল") || lower.contains("scroll up")) {
            val service = MayaAccessibilityService.instance
            return if (service != null) {
                service.performAutoScrollUp()
                ActionResult.Success("স্ক্রিনে উপরের দিকে স্ক্রোল করছি...", "GESTURE_SCROLL_UP")
            } else {
                ActionResult.Failure("অ্যাক্সেসিবিলিটি সার্ভিস সক্রিয় করা নেই।")
            }
        }

        // 2. Apps Launching
        if (lower.contains("টিকটক") || lower.contains("tiktok")) {
            return launchApp(context, APP_PACKAGES["tiktok"]!!, "টিকটক ওপেন করছি...", "https://www.tiktok.com", "APP:TIKTOK")
        }

        if (lower.contains("ইমো") || lower.contains("imo")) {
            return launchApp(context, APP_PACKAGES["imo"]!!, "ইমো অ্যাপ চালু করা হচ্ছে...", "https://imo.im", "APP:IMO")
        }

        if (lower.contains("ইউটিউব") || lower.contains("youtube")) {
            return launchApp(context, APP_PACKAGES["youtube"]!!, "ইউটিউব ওপেন করছি...", "https://youtube.com", "APP:YOUTUBE")
        }

        if (lower.contains("ফেসবুক") || lower.contains("facebook")) {
            return launchApp(context, APP_PACKAGES["facebook"]!!, "ফেসবুক ওপেন করা হচ্ছে...", "https://facebook.com", "APP:FACEBOOK")
        }

        if (lower.contains("হোয়াটসঅ্যাপ") || lower.contains("হোয়াটসঅ্যপ") || lower.contains("whatsapp")) {
            return launchApp(context, APP_PACKAGES["whatsapp"]!!, "হোয়াটসঅ্যাপ চালু করা হচ্ছে...", "https://web.whatsapp.com", "APP:WHATSAPP")
        }

        if (lower.contains("মেসেঞ্জার") || lower.contains("messenger")) {
            return launchApp(context, APP_PACKAGES["messenger"]!!, "মেসেঞ্জার ওপেন করছি...", "https://messenger.com", "APP:MESSENGER")
        }

        if (lower.contains("ক্যামেরা") || lower.contains("ছবি তুল") || lower.contains("camera")) {
            return try {
                val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                ActionResult.Success("ক্যামেরা চালু করছি...", "APP:CAMERA")
            } catch (e: Exception) {
                ActionResult.Failure("ক্যামেরা চালু করতে সমস্যা হয়েছে")
            }
        }

        if (lower.contains("ক্যালকুলেটর") || lower.contains("calculator")) {
            return launchApp(context, APP_PACKAGES["calculator"]!!, "ক্যালকুলেটর চালু করা হচ্ছে...", null, "APP:CALCULATOR")
        }

        if (lower.contains("ম্যাপ") || lower.contains("maps")) {
            return launchApp(context, APP_PACKAGES["maps"]!!, "গুগল ম্যাপ ওপেন করছি...", "https://maps.google.com", "APP:MAPS")
        }

        // 3. System Settings
        if (lower.contains("ওয়াইফাই") || lower.contains("ওয়াইফাই") || lower.contains("wifi")) {
            return try {
                val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                ActionResult.Success("ওয়াইফাই সেটিংস ওপেন করা হয়েছে", "SETTINGS:WIFI")
            } catch (e: Exception) {
                ActionResult.Failure("ওয়াইফাই সেটিংস ওপেন করা যায়নি")
            }
        }

        if (lower.contains("ব্লুটুথ") || lower.contains("bluetooth")) {
            return try {
                val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                ActionResult.Success("ব্লুটুথ সেটিংস পেজে নিয়ে যাচ্ছি", "SETTINGS:BLUETOOTH")
            } catch (e: Exception) {
                ActionResult.Failure("ব্লুটুথ সেটিংস খুলতে সমস্যা হয়েছে")
            }
        }

        if (lower.contains("অ্যালার্ম") || lower.contains("alarm")) {
            return try {
                val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                ActionResult.Success("অ্যালার্ম পেজ ওপেন করা হয়েছে", "SETTINGS:ALARM")
            } catch (e: Exception) {
                try {
                    val fallback = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(fallback)
                    ActionResult.Success("অ্যালার্ম সেট করার পেজে নিয়ে যাচ্ছি", "SETTINGS:ALARM")
                } catch (e2: Exception) {
                    ActionResult.Failure("অ্যালার্ম অ্যাপ পাওয়া যায়নি")
                }
            }
        }

        if (lower.contains("ভলিউম") || lower.contains("শব্দ") || lower.contains("sound")) {
            return try {
                val intent = Intent(Settings.ACTION_SOUND_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                ActionResult.Success("সাউন্ড সেটিংস ওপেন করা হয়েছে", "SETTINGS:SOUND")
            } catch (e: Exception) {
                ActionResult.Failure("সাউন্ড সেটিংস খুলতে সমস্যা হয়েছে")
            }
        }

        // 4. Phone Calling
        if (lower.contains("কল") || lower.contains("ফোন") || lower.contains("call") || lower.contains("dial")) {
            val digits = extractPhoneNumber(lower)
            if (digits.isNotEmpty()) {
                return makeCall(context, digits)
            } else {
                // Open Dial pad
                return try {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    ActionResult.Success("ফোন ডায়ালার ওপেন করা হয়েছে", "CALL:DIALER")
                } catch (e: Exception) {
                    ActionResult.Failure("ডায়ালার ওপেন করা যায়নি")
                }
            }
        }

        return ActionResult.NotAnAction
    }

    fun launchApp(context: Context, packageName: String, successMsg: String, webFallback: String?, tag: String): ActionResult {
        val service = MayaAccessibilityService.instance
        if (service != null) {
            val opened = service.openApp(packageName)
            if (opened) return ActionResult.Success(successMsg, tag)
        }

        val pm = context.packageManager
        val launchIntent = pm.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
            return ActionResult.Success(successMsg, tag)
        }

        // If package not found and webFallback available, open in browser
        if (webFallback != null) {
            return try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webFallback)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(browserIntent)
                ActionResult.Success("$successMsg (ব্রাউজারে চালু হয়েছে)", tag)
            } catch (e: Exception) {
                ActionResult.Failure("অ্যাপটি ডিভাইসে ইনস্টল করা নেই")
            }
        }

        return ActionResult.Failure("অ্যাপটি পাওয়া যায়নি")
    }

    fun makeCall(context: Context, phoneNumber: String): ActionResult {
        val hasCallPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED

        val service = MayaAccessibilityService.instance
        if (hasCallPermission) {
            if (service != null) {
                service.makeDirectCall(phoneNumber)
                return ActionResult.Success("$phoneNumber নম্বরে কল করা হচ্ছে...", "CALL:$phoneNumber")
            }
            return try {
                val intent = Intent(Intent.ACTION_CALL).apply {
                    data = Uri.parse("tel:$phoneNumber")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                ActionResult.Success("$phoneNumber নম্বরে সরাসরি কল দেওয়া হচ্ছে...", "CALL:$phoneNumber")
            } catch (e: Exception) {
                openDialer(context, phoneNumber)
            }
        } else {
            return openDialer(context, phoneNumber)
        }
    }

    private fun openDialer(context: Context, phoneNumber: String): ActionResult {
        return try {
            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(dialIntent)
            ActionResult.Success("$phoneNumber নম্বরের ডায়ালার চালু করা হয়েছে", "CALL:DIALER")
        } catch (e: Exception) {
            ActionResult.Failure("কল সার্ভিস চালু করতে সমস্যা হয়েছে")
        }
    }

    fun openAccessibilitySettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Cannot open accessibility settings: ${e.message}")
        }
    }

    private fun extractPhoneNumber(text: String): String {
        val sb = StringBuilder()
        var recording = false
        for (ch in text) {
            if (ch.isDigit() || ch == '+') {
                sb.append(ch)
                recording = true
            } else if (recording && ch.isWhitespace()) {
                // allow spaces in phone numbers like +880 1711...
                continue
            }
        }
        val candidate = sb.toString().trim()
        return if (candidate.length >= 3) candidate else ""
    }
}
