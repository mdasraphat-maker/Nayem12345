package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.BatteryStd
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.actions.SystemActionDispatcher
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ActionsScreen(
    viewModel: MayaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var callNumber by remember { mutableStateOf("") }
    val isAccessibilityActive = viewModel.isAccessibilityActive()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MayaDarkBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "সিস্টেম ও অটোমেশন কন্ট্রোল",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MayaTextPrimary
        )
        Text(
            text = "ভয়েস কমান্ড অথবা টাচ দিয়ে যেকোনো অ্যাপ ও ডিভাইস কন্ট্রোল করুন",
            fontSize = 13.sp,
            color = MayaTextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // 1. Accessibility Service Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MayaCardBg)
                .border(
                    1.dp,
                    if (isAccessibilityActive) MayaGreen.copy(alpha = 0.5f) else MayaPink.copy(alpha = 0.4f),
                    RoundedCornerShape(20.dp)
                )
                .padding(16.dp)
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
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isAccessibilityActive) MayaGreen.copy(alpha = 0.2f) else MayaPink.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessibilityNew,
                                contentDescription = null,
                                tint = if (isAccessibilityActive) MayaGreen else MayaPink,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "মায়া অ্যাক্সেসিবিলিটি সার্ভিস",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MayaTextPrimary
                            )
                            Text(
                                text = if (isAccessibilityActive) "সক্রিয় রয়েছে (হ্যান্ডস-ফ্রি কন্ট্রোল প্রস্তুত)" else "নিষ্ক্রিয় (সক্রিয় করতে সেটিংস ওপেন করুন)",
                                fontSize = 12.sp,
                                color = if (isAccessibilityActive) MayaGreen else MayaTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { SystemActionDispatcher.openAccessibilitySettings(context) },
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("open_accessibility_settings_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAccessibilityActive) MayaSurfaceVariant else MayaPink
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isAccessibilityActive) "সার্ভিস পেজ" else "সার্ভিস অন করুন", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = { viewModel.triggerAutoScrollDown() },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("scroll_down_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MayaCyan)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_action_scroll_down),
                            contentDescription = null,
                            tint = MayaCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("স্ক্রোল ডাউন", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = { viewModel.triggerAutoScrollUp() },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("scroll_up_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MayaViolet)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_action_scroll_up),
                            contentDescription = null,
                            tint = MayaViolet,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("স্ক্রোল আপ", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2. Direct Phone Call Section
        Text(
            text = "সরাসরি ফোন কল",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MayaTextPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MayaCardBg)
                .padding(14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = callNumber,
                        onValueChange = { callNumber = it },
                        placeholder = { Text("ফোন নম্বর লিখুন...", color = MayaTextSecondary, fontSize = 13.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MayaSurfaceVariant,
                            unfocusedContainerColor = MayaSurfaceVariant,
                            focusedTextColor = MayaTextPrimary,
                            unfocusedTextColor = MayaTextPrimary,
                            focusedBorderColor = MayaPink
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (callNumber.isNotBlank()) {
                                viewModel.processCommand("কল $callNumber")
                            } else {
                                viewModel.processCommand("ফোন ডায়ালার")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MayaGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("call_action_btn")
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "কল দিন", tint = Color.Black)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("কল", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3. App Launchers Grid
        Text(
            text = "জনপ্রিয় অ্যাপস ওপেন করুন",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MayaTextPrimary,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            maxItemsInEachRow = 3
        ) {
            AppShortcutCard(
                name = "ইউটিউব",
                drawableRes = R.drawable.ic_app_youtube,
                accentColor = Color(0xFFFF0000)
            ) {
                viewModel.processCommand("ইউটিউব ওপেন করো")
            }
            AppShortcutCard(
                name = "ফেসবুক",
                drawableRes = R.drawable.ic_app_facebook,
                accentColor = Color(0xFF1877F2)
            ) {
                viewModel.processCommand("ফেসবুক ওপেন করো")
            }
            AppShortcutCard(
                name = "ইমো (IMO)",
                drawableRes = R.drawable.ic_app_imo,
                accentColor = Color(0xFF00A2FF)
            ) {
                viewModel.processCommand("ইমো অ্যাপ চালু করো")
            }
            AppShortcutCard(
                name = "টিকটক",
                drawableRes = R.drawable.ic_app_tiktok,
                accentColor = MayaPink
            ) {
                viewModel.processCommand("টিকটক ওপেন করো")
            }
            AppShortcutCard(
                name = "হোয়াটসঅ্যাপ",
                drawableRes = R.drawable.ic_app_whatsapp,
                accentColor = Color(0xFF25D366)
            ) {
                viewModel.processCommand("হোয়াটসঅ্যাপ চালু করো")
            }
            AppShortcutCard(
                name = "মেসেঞ্জার",
                drawableRes = R.drawable.ic_app_messenger,
                accentColor = Color(0xFF0084FF)
            ) {
                viewModel.processCommand("মেসেঞ্জার ওপেন করো")
            }
            AppShortcutCard(
                name = "ক্যামেরা",
                drawableRes = R.drawable.ic_app_camera,
                accentColor = MayaViolet
            ) {
                viewModel.processCommand("ক্যামেরা চালু করো")
            }
            AppShortcutCard(
                name = "গুগল ম্যাপস",
                drawableRes = R.drawable.ic_app_maps,
                accentColor = MayaCyan
            ) {
                viewModel.processCommand("গুগল ম্যাপ ওপেন করো")
            }
            AppShortcutCard(
                name = "ক্যালকুলেটর",
                drawableRes = R.drawable.ic_app_calculator,
                accentColor = Color(0xFFFFB300)
            ) {
                viewModel.processCommand("ক্যালকুলেটর চালু করো")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Device Settings Shortcuts
        Text(
            text = "ডিভাইস সেটিংস শর্টকাট",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MayaTextPrimary,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            maxItemsInEachRow = 3
        ) {
            SettingShortcutCard("ওয়াইফাই", Icons.Default.Wifi, MayaCyan) {
                viewModel.processCommand("ওয়াইফাই সেটিংস")
            }
            SettingShortcutCard("ব্লুটুথ", Icons.Default.Bluetooth, MayaViolet) {
                viewModel.processCommand("ব্লুটুথ সেটিংস")
            }
            SettingShortcutCard("অ্যালার্ম", Icons.Default.Alarm, MayaPink) {
                viewModel.processCommand("অ্যালার্ম দেখাও")
            }
            SettingShortcutCard("শব্দ ও ভলিউম", Icons.Default.VolumeUp, MayaGreen) {
                viewModel.processCommand("সাউন্ড সেটিংস")
            }
            SettingShortcutCard("ব্যাটারি পেজ", Icons.Default.BatteryStd, Color(0xFFFFCA28)) {
                SystemActionDispatcher.tryParseAndExecute(context, "ব্যাটারি")
            }
            SettingShortcutCard("মূল সেটিংস", Icons.Default.Settings, Color.White) {
                SystemActionDispatcher.tryParseAndExecute(context, "সেটিংস")
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun AppShortcutCard(
    name: String,
    accentColor: Color,
    icon: ImageVector? = null,
    drawableRes: Int? = null,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(105.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MayaCardBg)
            .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 8.dp)
            .testTag("app_shortcut_$name")
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            if (drawableRes != null) {
                Icon(
                    painter = painterResource(id = drawableRes),
                    contentDescription = name,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(28.dp)
                )
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = name,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            color = MayaTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}

@Composable
fun SettingShortcutCard(
    name: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .width(160.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MayaCardBg)
            .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag("setting_shortcut_$name")
    ) {
        Icon(
            imageVector = icon,
            contentDescription = name,
            tint = accentColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = name,
            color = MayaTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
