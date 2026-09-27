package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import com.example.actions.SystemActionDispatcher
import com.example.api.GeminiApiClient
import com.example.ui.ApiKeyTestStatus
import com.example.ui.MayaViewModel
import com.example.ui.theme.MayaCardBg
import com.example.ui.theme.MayaCyan
import com.example.ui.theme.MayaDarkBackground
import com.example.ui.theme.MayaGreen
import com.example.ui.theme.MayaPink
import com.example.ui.theme.MayaSurfaceVariant
import com.example.ui.theme.MayaTextPrimary
import com.example.ui.theme.MayaTextSecondary
import com.example.ui.theme.MayaViolet

@Composable
fun SettingsScreen(
    viewModel: MayaViewModel,
    isProactiveEnabled: Boolean,
    proactiveInterval: Int,
    ttsPitch: Float,
    ttsRate: Float,
    selectedLanguage: String,
    customApiKey: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var keyInput by remember(customApiKey) { mutableStateOf(customApiKey) }
    var pitchSlider by remember(ttsPitch) { mutableFloatStateOf(ttsPitch) }
    var rateSlider by remember(ttsRate) { mutableFloatStateOf(ttsRate) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MayaDarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "সেটিংস ও টিউনিং",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MayaTextPrimary
        )
        Text(
            text = "মায়ার কণ্ঠস্বর, প্রোঅ্যাক্টিভ আচরণ ও এআই কনফিগারেশন",
            fontSize = 13.sp,
            color = MayaTextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // 1. Proactive Loop Settings Card
        SettingsCard(title = "প্রোঅ্যাক্টিভ চেক-ইন লুপ", icon = Icons.Default.NotificationsActive, iconColor = MayaGreen) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "স্বয়ংক্রিয় মিষ্টি চেক-ইন",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MayaTextPrimary
                    )
                    Text(
                        text = "মায়া নিজে থেকে মাঝে মাঝে মিষ্টি সুরে খোঁজখবর নিবে এবং সাহায্য অফার করবে।",
                        fontSize = 12.sp,
                        color = MayaTextSecondary
                    )
                }
                Switch(
                    checked = isProactiveEnabled,
                    onCheckedChange = { viewModel.toggleProactive(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MayaGreen
                    ),
                    modifier = Modifier.testTag("proactive_switch")
                )
            }

            if (isProactiveEnabled) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "চেক-ইন বিরতি: $proactiveInterval সেকেন্ড পর পর",
                    fontSize = 13.sp,
                    color = MayaCyan,
                    fontWeight = FontWeight.Medium
                )
                Slider(
                    value = proactiveInterval.toFloat(),
                    onValueChange = { viewModel.setProactiveInterval(it.toInt()) },
                    valueRange = 10f..120f,
                    steps = 10,
                    colors = SliderDefaults.colors(
                        thumbColor = MayaPink,
                        activeTrackColor = MayaPink,
                        inactiveTrackColor = MayaSurfaceVariant
                    ),
                    modifier = Modifier.testTag("proactive_interval_slider")
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(15, 30, 60).forEach { sec ->
                        OutlinedButton(
                            onClick = { viewModel.setProactiveInterval(sec) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (proactiveInterval == sec) MayaPink else MayaTextSecondary
                            )
                        ) {
                            Text("${sec}s")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. TTS Voice Customization Card
        SettingsCard(title = "নারী কণ্ঠস্বর টিউনিং (TTS)", icon = Icons.Default.RecordVoiceOver, iconColor = MayaPink) {
            // Pitch slider
            Text(
                text = "ভয়েস পিচ (উঁচু মিষ্টি কণ্ঠ): ${"%.2f".format(pitchSlider)}x",
                fontSize = 13.sp,
                color = MayaTextPrimary
            )
            Slider(
                value = pitchSlider,
                onValueChange = {
                    pitchSlider = it
                    viewModel.updatePitch(it)
                },
                valueRange = 0.8f..1.8f,
                colors = SliderDefaults.colors(
                    thumbColor = MayaPink,
                    activeTrackColor = MayaPink,
                    inactiveTrackColor = MayaSurfaceVariant
                )
            )

            // Speed slider
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "কথা বলার গতি: ${"%.2f".format(rateSlider)}x",
                fontSize = 13.sp,
                color = MayaTextPrimary
            )
            Slider(
                value = rateSlider,
                onValueChange = {
                    rateSlider = it
                    viewModel.updateRate(it)
                },
                valueRange = 0.6f..1.4f,
                colors = SliderDefaults.colors(
                    thumbColor = MayaViolet,
                    activeTrackColor = MayaViolet,
                    inactiveTrackColor = MayaSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Language selector
            Text(
                text = "প্রাথমিক ভাষা",
                fontSize = 13.sp,
                color = MayaTextPrimary,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilterChip(
                    selected = selectedLanguage.startsWith("bn"),
                    onClick = { viewModel.updateLanguage("bn-BD") },
                    label = { Text("বাংলা (Bengali)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MayaPink.copy(alpha = 0.25f),
                        selectedLabelColor = MayaPink
                    )
                )
                FilterChip(
                    selected = selectedLanguage.startsWith("en"),
                    onClick = { viewModel.updateLanguage("en-US") },
                    label = { Text("English") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MayaViolet.copy(alpha = 0.25f),
                        selectedLabelColor = MayaViolet
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Test Voice button
            Button(
                onClick = {
                    viewModel.speakResponse("হ্যালো! আমি মায়া। আমি সবসময় আপনার সাহায্যে প্রস্তুত।")
                },
                colors = ButtonDefaults.buttonColors(containerColor = MayaPink),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("test_voice_btn")
            ) {
                Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("ভয়েস পরীক্ষা করুন", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val apiKeyStatus by viewModel.apiKeyTestStatus.collectAsState()
        val selectedModel by viewModel.selectedModel.collectAsState()

        // 3. Gemini API Key Configuration & Run Options
        SettingsCard(title = "Gemini AI এপিআই কি রান ও কনফিগারেশন", icon = Icons.Default.Key, iconColor = MayaCyan) {
            Text(
                text = "গুগল এআই স্টুডিও (Google AI Studio) থেকে আপনার জেমিনি এপিআই কি প্রবেশ করিয়ে সরাসরি রান ও টেস্ট করুন।",
                fontSize = 12.sp,
                color = MayaTextSecondary,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            // Model Selection
            Text(
                text = "এআই মডেল নির্বাচন করুন:",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MayaTextPrimary,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedModel == GeminiApiClient.DEFAULT_MODEL,
                    onClick = { viewModel.setSelectedModel(GeminiApiClient.DEFAULT_MODEL) },
                    label = { Text("3.5 Flash (ডিফল্ট)", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MayaCyan.copy(alpha = 0.25f),
                        selectedLabelColor = MayaCyan
                    )
                )
                FilterChip(
                    selected = selectedModel == GeminiApiClient.MODEL_PRO,
                    onClick = { viewModel.setSelectedModel(GeminiApiClient.MODEL_PRO) },
                    label = { Text("3.1 Pro (স্মার্ট)", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MayaPink.copy(alpha = 0.25f),
                        selectedLabelColor = MayaPink
                    )
                )
                FilterChip(
                    selected = selectedModel == GeminiApiClient.MODEL_LITE,
                    onClick = { viewModel.setSelectedModel(GeminiApiClient.MODEL_LITE) },
                    label = { Text("Flash Lite (দ্রুত)", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MayaViolet.copy(alpha = 0.25f),
                        selectedLabelColor = MayaViolet
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Key Input
            OutlinedTextField(
                value = keyInput,
                onValueChange = {
                    keyInput = it
                    if (apiKeyStatus !is ApiKeyTestStatus.Idle) {
                        viewModel.resetApiKeyStatus()
                    }
                },
                placeholder = { Text("AIzaSy... আপনার এপিআই কি দিন", color = MayaTextSecondary, fontSize = 12.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("gemini_key_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MayaSurfaceVariant,
                    unfocusedContainerColor = MayaSurfaceVariant,
                    focusedTextColor = MayaTextPrimary,
                    unfocusedTextColor = MayaTextPrimary,
                    focusedBorderColor = MayaCyan
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons: Run & Test Key, Save, and Clear
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // RUN & TEST BUTTON (NEW)
                Button(
                    onClick = { viewModel.testAndRunApiKey(keyInput) },
                    enabled = keyInput.isNotBlank() && apiKeyStatus !is ApiKeyTestStatus.Testing,
                    colors = ButtonDefaults.buttonColors(containerColor = MayaCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .testTag("run_gemini_key_btn")
                ) {
                    if (apiKeyStatus is ApiKeyTestStatus.Testing) {
                        CircularProgressIndicator(
                            color = Color.Black,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("রান হচ্ছে...", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    } else {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("এপিআই কি রান করুন", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                // Clear / Delete Button
                if (customApiKey.isNotBlank() || keyInput.isNotBlank()) {
                    OutlinedButton(
                        onClick = {
                            keyInput = ""
                            viewModel.clearCustomApiKey()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red.copy(alpha = 0.8f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("clear_gemini_key_btn")
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "মুছুন", modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Status Display Card
            when (val st = apiKeyStatus) {
                is ApiKeyTestStatus.Success -> {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MayaGreen.copy(alpha = 0.15f))
                            .border(1.dp, MayaGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MayaGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "সফলভাবে রান হয়েছে! (${st.model})",
                                    color = MayaGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "মায়ার উত্তর: \"${st.reply}\"",
                                color = MayaTextPrimary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                is ApiKeyTestStatus.Error -> {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Red.copy(alpha = 0.12f))
                            .border(1.dp, Color.Red.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color.Red, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = st.errorMessage,
                                color = Color(0xFFFF8A80),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                else -> {
                    if (customApiKey.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "বর্তমান স্ট্যাটাস: এপিআই কি সংরক্ষিত আছে। রান বোতাম চেপে যাচাই করুন।",
                            fontSize = 11.sp,
                            color = MayaCyan.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Accessibility Service Link
        SettingsCard(title = "অ্যাক্সেসিবিলিটি সার্ভিস সেটিংস", icon = Icons.Default.AccessibilityNew, iconColor = MayaViolet) {
            Text(
                text = "স্ক্রিন পড়া, অ্যাপ খোলা ও জেসচার স্ক্রোল করার জন্য ডিভাইসের Accessibility মেনু থেকে Maya AI অন করুন।",
                fontSize = 12.sp,
                color = MayaTextSecondary,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            OutlinedButton(
                onClick = { SystemActionDispatcher.openAccessibilitySettings(context) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MayaViolet)
            ) {
                Text("সিস্টেম অ্যাক্সেসিবিলিটি পেজ খুলুন")
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun SettingsCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MayaCardBg)
            .border(1.dp, MayaSurfaceVariant, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MayaTextPrimary
                )
            }
            content()
        }
    }
}
