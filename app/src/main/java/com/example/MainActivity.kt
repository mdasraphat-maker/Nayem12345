package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccessibilityNew
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.ui.MayaScreen
import com.example.ui.MayaViewModel
import com.example.ui.screens.ActionsScreen
import com.example.ui.screens.AssistantScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MayaCyan
import com.example.ui.theme.MayaDarkBackground
import com.example.ui.theme.MayaPink
import com.example.ui.theme.MayaSurface
import com.example.ui.theme.MayaTextPrimary
import com.example.ui.theme.MayaTextSecondary
import com.example.ui.theme.MayaViolet
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MayaViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val currentScreen by viewModel.currentScreen.collectAsState()
                val userSpeech by viewModel.userSpeech.collectAsState()
                val mayaResponse by viewModel.mayaResponse.collectAsState()
                val isThinking by viewModel.isThinking.collectAsState()
                val isListening by viewModel.voiceManager.isListening.collectAsState()
                val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsState()
                val soundLevel by viewModel.voiceManager.soundLevel.collectAsState()
                val isProactiveEnabled by viewModel.isProactiveEnabled.collectAsState()
                val proactiveInterval by viewModel.proactiveInterval.collectAsState()
                val ttsPitch by viewModel.ttsPitch.collectAsState()
                val ttsRate by viewModel.ttsRate.collectAsState()
                val selectedLanguage by viewModel.selectedLanguage.collectAsState()
                val customApiKey by viewModel.customApiKey.collectAsState()
                val historyList by viewModel.conversationHistory.collectAsState()
                val statusNotice by viewModel.statusNotice.collectAsState()

                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(statusNotice) {
                    statusNotice?.let {
                        snackbarHostState.showSnackbar(it)
                        viewModel.clearStatusNotice()
                    }
                }

                // Handle back button on sub-screens
                BackHandler(enabled = currentScreen != MayaScreen.ASSISTANT) {
                    viewModel.setScreen(MayaScreen.ASSISTANT)
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MayaDarkBackground,
                    contentWindowInsets = WindowInsets.safeDrawing,
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.maya_avatar),
                                        contentDescription = "Maya App Icon",
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .border(1.5.dp, MayaPink, CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Maya AI • মায়া",
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MayaTextPrimary
                                    )
                                }
                            },
                            actions = {
                                IconButton(
                                    onClick = { viewModel.setScreen(MayaScreen.SETTINGS) },
                                    modifier = Modifier.testTag("top_bar_key_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Key,
                                        contentDescription = "Gemini API Key",
                                        tint = if (customApiKey.isNotBlank()) MayaCyan else MayaPink
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MayaDarkBackground,
                                titleContentColor = MayaTextPrimary
                            )
                        )
                    },
                    bottomBar = {
                        MayaBottomNav(
                            currentScreen = currentScreen,
                            onTabSelected = { screen ->
                                viewModel.setScreen(screen)
                            }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentScreen) {
                            MayaScreen.ASSISTANT -> AssistantScreen(
                                viewModel = viewModel,
                                userSpeech = userSpeech,
                                mayaResponse = mayaResponse,
                                isListening = isListening,
                                isSpeaking = isSpeaking,
                                isThinking = isThinking,
                                soundLevel = soundLevel,
                                isProactiveEnabled = isProactiveEnabled,
                                proactiveInterval = proactiveInterval
                            )
                            MayaScreen.ACTIONS -> ActionsScreen(
                                viewModel = viewModel
                            )
                            MayaScreen.HISTORY -> HistoryScreen(
                                viewModel = viewModel,
                                messages = historyList
                            )
                            MayaScreen.SETTINGS -> SettingsScreen(
                                viewModel = viewModel,
                                isProactiveEnabled = isProactiveEnabled,
                                proactiveInterval = proactiveInterval,
                                ttsPitch = ttsPitch,
                                ttsRate = ttsRate,
                                selectedLanguage = selectedLanguage,
                                customApiKey = customApiKey
                            )
                        }
                    }
                }
            }
        }
    }
}

data class NavItem(
    val screen: MayaScreen,
    val label: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector
)

@Composable
fun MayaBottomNav(
    currentScreen: MayaScreen,
    onTabSelected: (MayaScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem(MayaScreen.ASSISTANT, "মায়া", Icons.Filled.Psychology, Icons.Outlined.Psychology),
        NavItem(MayaScreen.ACTIONS, "অ্যাকশন", Icons.Filled.AccessibilityNew, Icons.Outlined.AccessibilityNew),
        NavItem(MayaScreen.HISTORY, "হিস্টোরি", Icons.Filled.History, Icons.Outlined.History),
        NavItem(MayaScreen.SETTINGS, "সেটিংস", Icons.Filled.Settings, Icons.Outlined.Settings)
    )

    NavigationBar(
        modifier = modifier.testTag("maya_bottom_nav"),
        containerColor = MayaSurface,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentScreen == item.screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(item.screen) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.activeIcon else item.inactiveIcon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MayaPink,
                    selectedTextColor = MayaPink,
                    indicatorColor = MayaPink.copy(alpha = 0.18f),
                    unselectedIconColor = MayaTextSecondary,
                    unselectedTextColor = MayaTextSecondary
                ),
                modifier = Modifier.testTag("nav_tab_${item.screen.name.lowercase()}")
            )
        }
    }
}
