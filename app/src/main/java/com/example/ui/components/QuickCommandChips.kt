package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.MayaCardBg
import com.example.ui.theme.MayaPink
import com.example.ui.theme.MayaTextPrimary

data class QuickAction(
    val title: String,
    val command: String,
    val icon: ImageVector? = null,
    val drawableRes: Int? = null,
    val tintColor: Color? = null
)

@Composable
fun QuickCommandChips(
    onCommandClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        QuickAction("ইউটিউব", "ইউটিউব ওপেন করো", drawableRes = R.drawable.ic_app_youtube),
        QuickAction("ফেসবুক", "ফেসবুক ওপেন করো", drawableRes = R.drawable.ic_app_facebook),
        QuickAction("টিকটক", "টিকটক ওপেন করো", drawableRes = R.drawable.ic_app_tiktok),
        QuickAction("ইমো", "ইমো অ্যাপ চালু করো", drawableRes = R.drawable.ic_app_imo),
        QuickAction("হোয়াটসঅ্যাপ", "হোয়াটসঅ্যাপ চালু করো", drawableRes = R.drawable.ic_app_whatsapp),
        QuickAction("মেসেঞ্জার", "মেসেঞ্জার ওপেন করো", drawableRes = R.drawable.ic_app_messenger),
        QuickAction("ক্যামেরা", "ক্যামেরা চালু করো", drawableRes = R.drawable.ic_app_camera),
        QuickAction("স্ক্রোল ডাউন", "স্ক্রোল ডাউন", drawableRes = R.drawable.ic_action_scroll_down),
        QuickAction("স্ক্রোল আপ", "স্ক্রোল আপ", drawableRes = R.drawable.ic_action_scroll_up),
        QuickAction("ওয়াইফাই", "ওয়াইফাই সেটিংস", icon = Icons.Default.Wifi, tintColor = MayaPink),
        QuickAction("অ্যালার্ম", "অ্যালার্ম সেট করো", icon = Icons.Default.Alarm, tintColor = MayaPink),
        QuickAction("কল দিন", "ফোন কল দিন", icon = Icons.Default.Call, tintColor = MayaPink),
        QuickAction("কেমন আছো?", "কেমন আছো মায়া?", icon = Icons.Default.Favorite, tintColor = MayaPink),
        QuickAction("টিপস দাও", "আমাকে স্বাস্থ্যকর থাকার কিছু টিপস দাও", icon = Icons.Default.TipsAndUpdates, tintColor = MayaPink)
    )

    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MayaCardBg)
                    .border(1.dp, MayaPink.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                    .clickable { onCommandClick(item.command) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .testTag("quick_chip_${item.title}")
            ) {
                if (item.drawableRes != null) {
                    Icon(
                        painter = painterResource(id = item.drawableRes),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(16.dp)
                    )
                } else if (item.icon != null) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = item.tintColor ?: MayaPink,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = item.title,
                    color = MayaTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }
    }
}
