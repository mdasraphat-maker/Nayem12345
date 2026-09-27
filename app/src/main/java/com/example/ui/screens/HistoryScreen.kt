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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ConversationMessage
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: MayaViewModel,
    messages: List<ConversationMessage>,
    modifier: Modifier = Modifier
) {
    var showClearDialog by remember { mutableStateOf(false) }
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MayaDarkBackground)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "কথোপকথন হিস্টোরি",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MayaTextPrimary
                )
                Text(
                    text = "${messages.size} টি বার্তা সংরক্ষিত",
                    fontSize = 13.sp,
                    color = MayaTextSecondary
                )
            }

            if (messages.isNotEmpty()) {
                IconButton(
                    onClick = { showClearDialog = true },
                    modifier = Modifier.testTag("clear_history_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "হিস্টোরি মুছুন",
                        tint = MayaPink
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (messages.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = null,
                        tint = MayaTextSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "এখনো কোনো কথোপকথন হয়নি",
                        fontSize = 15.sp,
                        color = MayaTextSecondary
                    )
                    Text(
                        text = "মাইক্রোফোন চেপে মায়ার সাথে কথা বলুন!",
                        fontSize = 13.sp,
                        color = MayaPink,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    HistoryItemCard(
                        msg = msg,
                        timeStr = timeFormat.format(Date(msg.timestamp)),
                        onReplaySpeech = {
                            viewModel.speakResponse(msg.text)
                        }
                    )
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("সব হিস্টোরি মুছে ফেলবেন?") },
            text = { Text("আপনার সংরক্ষিত কথোপকথন তালিকা চিরতরে মুছে যাবে।") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllHistory()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MayaPink)
                ) {
                    Text("মুছে ফেলুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("বাতিল")
                }
            },
            containerColor = MayaCardBg,
            titleContentColor = MayaTextPrimary,
            textContentColor = MayaTextSecondary
        )
    }
}

@Composable
fun HistoryItemCard(
    msg: ConversationMessage,
    timeStr: String,
    onReplaySpeech: () -> Unit
) {
    val isUser = msg.sender == "USER"
    val accentColor = when {
        isUser -> MayaPink
        msg.isProactive -> MayaGreen
        else -> MayaViolet
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MayaCardBg)
            .border(1.dp, accentColor.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        isUser -> Icons.Default.Person
                        msg.isProactive -> Icons.Default.NotificationsActive
                        else -> Icons.Default.SmartToy
                    },
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when {
                            isUser -> "আপনি"
                            msg.isProactive -> "মায়া (প্রোঅ্যাক্টিভ)"
                            else -> "মায়া"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                    Text(
                        text = timeStr,
                        fontSize = 11.sp,
                        color = MayaTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = msg.text,
                    fontSize = 14.sp,
                    color = MayaTextPrimary,
                    lineHeight = 20.sp
                )

                if (msg.actionExecuted != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "⚡ সম্পন্ন অ্যাকশন: ${msg.actionExecuted}",
                        fontSize = 11.sp,
                        color = MayaCyan
                    )
                }
            }

            IconButton(
                onClick = onReplaySpeech,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "শুনুন",
                    tint = MayaTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
