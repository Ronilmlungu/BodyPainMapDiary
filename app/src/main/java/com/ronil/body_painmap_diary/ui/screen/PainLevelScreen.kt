package com.ronil.body_painmap_diary.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ronil.body_painmap_diary.model.PainLevel
import com.ronil.body_painmap_diary.ui.components.GradientButton
import com.ronil.body_painmap_diary.ui.theme.BodyPainMapDiaryTheme
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import com.ronil.body_painmap_diary.PainViewModel

@Composable
fun PainLevelScreen(
    bodyLocation: String,
    viewModel: PainViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit

) {
    var selectedLevel by remember { mutableStateOf<PainLevel?>(null) }
    val speak = rememberSpeaker()
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ---- Top bar ----
        Row(
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.width(4.dp))
            Text(
                "Select Pain Level",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // ---- Main content ----
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Location chip
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(
                    "Location: $bodyLocation",
                    Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(4.dp))

            Text(
                "How strong is the pain?",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(4.dp))

            // 5 pain rows — loop over the enum
            PainLevel.entries.forEach { level ->
                PainLevelRow(
                    level = level,
                    selected = level == selectedLevel,
                    onClick = { selectedLevel = level },
                    onSpeak = { speak(level.label) }
                )
            }
        }

        // ---- Save button ----
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            GradientButton(
                text = "Save Pain Record",
                onClick ={
                    // Check if a level is selected
                    val currentLevel = selectedLevel
                    if (currentLevel != null) {
                        viewModel.savePainRecord(bodyLocation, currentLevel.value)
                        onSaved()
                    }

                },
                enabled = selectedLevel != null,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

        }
    }
}

@Composable
private fun PainLevelRow(
    level: PainLevel,
    selected: Boolean,
    onClick: () -> Unit,
    onSpeak: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Colored number circle
            Box(
                Modifier
                    .size(40.dp)
                    .background(level.fill, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    level.value.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = level.onFill
                )
            }

            Spacer(Modifier.width(14.dp))

            // Label
            Text(
                level.label,
                Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Speaker button
            IconButton(onClick = onSpeak) {
                Icon(
                    Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Hear ${level.label}",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// TTS Helper Function
@Composable
fun rememberSpeaker(): (String) -> Unit {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        // 1. Initialize the TTS engine
        val engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {

            }
        }
        tts = engine

        // 2. Destroy the engine when the screen closes
        onDispose {
            engine.stop()
            engine.shutdown()
        }
    }

    // 3. Return a function that takes a String and speaks it
    return { text ->
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "PainLevel")
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PainLevelScreenPreview() {
    BodyPainMapDiaryTheme {
        PainLevelScreen(
            bodyLocation = "Right Knee",
            viewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
            onBack = {},
            onSaved = {}
        )
    }
}
