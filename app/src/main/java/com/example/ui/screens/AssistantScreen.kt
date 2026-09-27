package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MayaViewModel
import com.example.ui.components.MayaAvatarView
import com.example.ui.components.QuickCommandChips
import com.example.ui.components.SpeechResponseBubble
import com.example.ui.components.VoiceInputBar
import com.example.ui.theme.MayaCardBg
import com.example.ui.theme.MayaDarkBackground
import com.example.ui.theme.MayaGreen
import com.example.ui.theme.MayaPink
import com.example.ui.theme.MayaTextPrimary
import com.example.ui.theme.MayaTextSecondary
import com.example.ui.theme.MayaViolet

@Composable
fun AssistantScreen(
    viewModel: MayaViewModel,
    userSpeech: String,
    mayaResponse: String,
    isListening: Boolean,
    isSpeaking: Boolean,
    isThinking: Boolean,
    soundLevel: Float,
    isProactiveEnabled: Boolean,
    proactiveInterval: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MayaDarkBackground)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Top Status Bar: Proactive Indicator & TTS Mute
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Proactive Toggle Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MayaCardBg)
                    .border(
                        1.dp,
                        if (isProactiveEnabled) MayaGreen.copy(alpha = 0.5f) else Color.Gray.copy(alpha = 0.3f),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { viewModel.toggleProactive(!isProactiveEnabled) }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("proactive_toggle_chip")
            ) {
                Icon(
                    imageVector = if (isProactiveEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                    contentDescription = null,
                    tint = if (isProactiveEnabled) MayaGreen else MayaTextSecondary,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isProactiveEnabled) "প্রোঅ্যাক্টিভ চেক-ইন (${proactiveInterval}s)" else "প্রোঅ্যাক্টিভ বন্ধ",
                    fontSize = 12.sp,
                    color = if (isProactiveEnabled) MayaGreen else MayaTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }

            // Quick Mute button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MayaCardBg)
                    .clickable {
                        if (isSpeaking) viewModel.stopSpeaking() else viewModel.speakResponse()
                    }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("tts_quick_toggle")
            ) {
                Icon(
                    imageVector = if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                    contentDescription = null,
                    tint = if (isSpeaking) MayaPink else MayaTextSecondary,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isSpeaking) "কথা বলছে" else "ভয়েস অন",
                    fontSize = 12.sp,
                    color = MayaTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Center Maya Animated Avatar
        MayaAvatarView(
            isListening = isListening,
            isSpeaking = isSpeaking,
            isThinking = isThinking,
            soundLevel = soundLevel,
            onClickAvatar = {
                viewModel.startListening()
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // User speech & Maya Response Bubble
        SpeechResponseBubble(
            userSpeech = userSpeech,
            mayaResponse = mayaResponse,
            isSpeaking = isSpeaking,
            onSpeak = { viewModel.speakResponse() },
            onStopSpeak = { viewModel.stopSpeaking() }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Bengali Voice Actions row
        Text(
            text = "দ্রুত কমান্ড বা প্রশ্ন করুন",
            fontSize = 13.sp,
            color = MayaTextSecondary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 4.dp, bottom = 8.dp)
        )

        QuickCommandChips(
            onCommandClick = { cmd ->
                viewModel.processCommand(cmd)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(22.dp))

        // Voice Recording Mic and Text input
        VoiceInputBar(
            isListening = isListening,
            onMicClick = {
                viewModel.startListening()
            },
            onSendMessage = { text ->
                viewModel.processCommand(text)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}
