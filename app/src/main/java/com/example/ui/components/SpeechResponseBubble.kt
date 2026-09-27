package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MayaCardBg
import com.example.ui.theme.MayaCyan
import com.example.ui.theme.MayaPink
import com.example.ui.theme.MayaSurfaceVariant
import com.example.ui.theme.MayaTextPrimary
import com.example.ui.theme.MayaTextSecondary
import com.example.ui.theme.MayaViolet

@Composable
fun SpeechResponseBubble(
    userSpeech: String,
    mayaResponse: String,
    isSpeaking: Boolean,
    onSpeak: () -> Unit,
    onStopSpeak: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // User speech box
        if (userSpeech.isNotBlank() && userSpeech != "বলুন, আমি শুনছি...") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp, 4.dp, 18.dp, 18.dp))
                        .background(MayaSurfaceVariant.copy(alpha = 0.85f))
                        .border(1.dp, MayaPink.copy(alpha = 0.3f), RoundedCornerShape(18.dp, 4.dp, 18.dp, 18.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = userSpeech,
                        color = MayaTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MayaPink.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "আপনি",
                        tint = MayaPink,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Maya response box
        AnimatedVisibility(
            visible = mayaResponse.isNotBlank(),
            enter = fadeIn() + slideInVertically(initialOffsetY = { 20 })
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MayaCardBg,
                                MayaSurfaceVariant.copy(alpha = 0.7f)
                            )
                        )
                    )
                    .border(
                        1.dp,
                        Brush.horizontalGradient(listOf(MayaPink.copy(alpha = 0.5f), MayaViolet.copy(alpha = 0.4f))),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp)
                    .testTag("maya_response_card")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isSpeaking) MayaPink else MayaCyan)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "মায়ার উত্তর",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MayaPink
                            )
                        }

                        Row {
                            // Copy button
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Maya AI", mayaResponse))
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "কপি করুন",
                                    tint = MayaTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // TTS Listen / Stop button
                            IconButton(
                                onClick = {
                                    if (isSpeaking) onStopSpeak() else onSpeak()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSpeaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = if (isSpeaking) "বন্ধ করুন" else "শুনুন",
                                    tint = if (isSpeaking) MayaPink else MayaViolet,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = mayaResponse,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MayaTextPrimary,
                            lineHeight = 22.sp,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }
            }
        }
    }
}
