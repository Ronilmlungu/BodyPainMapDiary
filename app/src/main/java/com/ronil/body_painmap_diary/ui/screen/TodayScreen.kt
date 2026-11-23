package com.ronil.body_painmap_diary.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ronil.body_painmap_diary.PainViewModel
import com.ronil.body_painmap_diary.ui.components.AppCard
import com.ronil.body_painmap_diary.ui.components.BodyMap
import com.ronil.body_painmap_diary.ui.components.GradientButton
import com.ronil.body_painmap_diary.ui.theme.BodyPainMapDiaryTheme
import com.ronil.body_painmap_diary.ui.theme.PainMild
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    viewModel: PainViewModel,
    onNext: (String) -> Unit
) {
    var frontSelected by remember { mutableStateOf(true) }
    var selectedPart by remember { mutableStateOf<String?>(null) }

    // Snackbar setup
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val records by viewModel.records.collectAsState()

    val today = java.time.LocalDate.now()
    val alreadyLoggedNoPain = records.any {
        val recordDate = java.time.Instant.ofEpochMilli(it.timestamp)
            .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
        recordDate == today && it.bodyPart == "None"
    }

    // Wrap in Scaffold to host the Snackbar
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                // Custom Green Success Snackbar
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF2E7D32), // Success Green
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    actionColor = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TodayTopBar()

            Column(
                Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                WelcomeCard()

                ViewToggle(
                    frontSelected = frontSelected,
                    onFrontSelected = { frontSelected = true },
                    onBackSelected = { frontSelected = false }
                )

                NoPainButton(onClick = {
                    if (alreadyLoggedNoPain) {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "You have already logged 'No Pain' for today.",
                                duration = SnackbarDuration.Short
                            )
                        }
                    } else {
                        viewModel.logNoPain()
                        // Show Success Snackbar with Checkmark Emoji
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "✅ Success: No Pain logged for today!",
                                withDismissAction = true,
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                })

                BodyMapCard(
                    selectedPart = selectedPart,
                    onPartSelected = { part ->
                        val alreadyLoggedPart = records.any {
                            val recordDate = java.time.Instant.ofEpochMilli(it.timestamp)
                                .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                            recordDate == today && it.bodyPart == part
                        }

                        if (alreadyLoggedPart) {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "You have already logged '$part' for today.",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        } else {
                            selectedPart = part
                        }
                    },
                    isFront = frontSelected
                )

                NextButton(
                    enabled = selectedPart != null,
                    onClick = { selectedPart?.let { onNext(it) } }
                )
            }
        }
    }
}

@Composable
fun TodayTopBar() {
    Row(
        Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(40.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.MedicalServices,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Text(
                "Pain Diary",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Daily Health Log",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun WelcomeCard() {
    AppCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Welcome, Jeny",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Where does it hurt today?",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Tap a spot on the body map below.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.width(12.dp))
            MedicalBadge()
        }
    }
}

@Composable
fun MedicalBadge() {
    Box(
        modifier = Modifier
            .size(56.dp)
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(14.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Filled.FavoriteBorder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                "Medical",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ViewToggle(
    frontSelected: Boolean,
    onFrontSelected: () -> Unit,
    onBackSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ToggleOption(
            label = "Front View",
            selected = frontSelected,
            modifier = Modifier.weight(1f),
            onClick = onFrontSelected
        )
        ToggleOption(
            label = "Back View",
            selected = !frontSelected,
            modifier = Modifier.weight(1f),
            onClick = onBackSelected
        )
    }
}

@Composable
private fun ToggleOption(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(
                color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun NoPainButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(2.dp, PainMild),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = PainMild.copy(alpha = 0.08f),
            contentColor = PainMild
        )
    ) {
        Icon(
            Icons.Filled.SentimentSatisfied,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = PainMild
        )
        Spacer(Modifier.width(10.dp))
        Text(
            "I have No Pain today",
            style = MaterialTheme.typography.titleMedium,
            color = PainMild
        )
    }
}

@Composable
fun BodyMapCard(
    selectedPart: String?,
    onPartSelected: (String) -> Unit,
    isFront: Boolean
) {
    AppCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.TouchApp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    "Touch circles to\nlocate pain",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                SelectedPill(selectedPart)
            }

            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(16.dp))

            BodyMap(
                selectedPart = selectedPart,
                onPartSelected = onPartSelected,
                isFront = isFront,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(12.dp))

            Text(
                "Need help? You can tap anywhere near the affected joint or limb.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SelectedPill(selectedPart: String?) {
    Box(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = if (selectedPart == null) "Selected: —" else "Selected: $selectedPart",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun NextButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    GradientButton(
        text = "Next: Select Pain Level",
        onClick = onClick,
        enabled = enabled
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TodayScreenPreview() {
    BodyPainMapDiaryTheme {
        TodayScreen(
            viewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
            onNext = {}
        )
    }
}