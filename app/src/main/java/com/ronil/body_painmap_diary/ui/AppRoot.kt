package com.ronil.body_painmap_diary.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ronil.body_painmap_diary.PainViewModel
import com.ronil.body_painmap_diary.data.SettingsStore
import com.ronil.body_painmap_diary.ui.screen.*

@Composable
fun AppRoot() {
    val viewModel: PainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val context = LocalContext.current
    val settingsStore = remember { SettingsStore(context) }
    val nav = rememberNavController()
    val currentRoute = nav.currentBackStackEntryAsState().value?.destination?.route

    // Show the bottom bar only on main tab screens
    val mainTabs = listOf("today", "history", "doctor")
    val showBottomBar = currentRoute in mainTabs

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(Modifier.fillMaxSize()) {

            // NavHost takes the space above the bottom bar
            Box(Modifier.fillMaxWidth().weight(1f)) {

                NavHost(nav, startDestination = "splash") {

                    composable("splash") {
                        SplashScreen(
                            onSplashFinished = {
                                // Check if PIN exists to decide the next screen
                                val nextScreen = if (settingsStore.getPin() == null) "setup" else "today"

                                nav.navigate(nextScreen) {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("setup") {
                        // We now receive 3 parameters: pin, question, answer
                        SetupScreen { newPin, question, answer ->
                            // Save the PIN and Security Q&A to storage
                            settingsStore.savePin(newPin)
                            settingsStore.saveSecurityQuestion(question, answer)

                            // Navigate to today and remove setup from the backstack
                            nav.navigate("today") {
                                popUpTo("setup") { inclusive = true }
                            }
                        }
                    }

                    composable("today") {
                        TodayScreen(
                            viewModel = viewModel,
                            onNext = { location ->
                                nav.navigate("painlevel/${Uri.encode(location)}")
                            }
                        )
                    }

                    composable("painlevel/{location}") { entry ->
                        val loc = entry.arguments?.getString("location").orEmpty()
                        PainLevelScreen(
                            bodyLocation = loc,
                            viewModel = viewModel,
                            onBack = { nav.popBackStack() },
                            onSaved = { nav.popBackStack() }
                        )
                    }

                    composable("history") { HistoryScreen(viewModel = viewModel) }

                    composable("doctor")  { DoctorScreen(viewModel = viewModel) }
                }
            }

            // The Bottom Bar (Only shows on main tabs)
            if (showBottomBar) {
                BottomNav(
                    currentTab = currentRoute.orEmpty(),
                    onTabSelected = { tab ->
                        nav.navigate(tab) {
                            popUpTo(nav.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

// ==========================================
// BOTTOM NAVIGATION COMPONENTS
// ==========================================

@Composable
fun BottomNav(
    currentTab: String,
    onTabSelected: (String) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavTab(
                label = "Today's Diary",
                icon = Icons.Filled.EditNote,
                selected = currentTab == "today",
                onClick = { onTabSelected("today") }
            )
            NavTab(
                label = "History",
                icon = Icons.Filled.CalendarMonth,
                selected = currentTab == "history",
                onClick = { onTabSelected("history") }
            )
            NavTab(
                label = "Doctor View",
                icon = Icons.Filled.Lock,
                selected = currentTab == "doctor",
                onClick = { onTabSelected("doctor") }
            )
        }
    }
}

@Composable
private fun NavTab(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        Modifier
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    icon, contentDescription = label,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    icon, contentDescription = label,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}