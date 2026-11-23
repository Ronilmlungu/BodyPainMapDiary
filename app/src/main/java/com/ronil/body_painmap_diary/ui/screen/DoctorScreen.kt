package com.ronil.body_painmap_diary.ui.screen

import android.content.ContentValues
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ronil.body_painmap_diary.PainViewModel
import com.ronil.body_painmap_diary.data.PainRecord
import com.ronil.body_painmap_diary.data.SettingsStore
import com.ronil.body_painmap_diary.ui.components.AppCard
import com.ronil.body_painmap_diary.ui.components.GradientButton
import com.ronil.body_painmap_diary.ui.components.TrendChart
import com.ronil.body_painmap_diary.ui.theme.BodyPainMapDiaryTheme
import com.ronil.body_painmap_diary.ui.theme.PainMild
import java.io.File
import java.io.FileOutputStream

@Composable
fun DoctorScreen(viewModel: PainViewModel) {
    var isUnlocked by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val savedPin = remember { SettingsStore(context).getPin() }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Doctor View",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.weight(1f))
            if (isUnlocked) {
                IconButton(onClick = { isUnlocked = false }) {
                    Icon(Icons.Filled.LockOpen, contentDescription = "Lock", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }

        if (!isUnlocked) {
            LockedView(savedPin = savedPin, onUnlock = { isUnlocked = true })
        } else {
            UnlockedView(viewModel = viewModel)
        }
    }
}

@Composable
fun LockedView(savedPin: String?, onUnlock: () -> Unit) {
    var pin by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var showForgotDialog by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))

        Text(
            "Enter your PIN code",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            repeat(4) { index ->
                val filled = index < pin.length
                Box(
                    Modifier
                        .size(14.dp)
                        .background(
                            color = if (filled) MaterialTheme.colorScheme.primary else Color(0xFFE0E0E0),
                            shape = CircleShape
                        )
                )
            }
        }

        if (showError) {
            Spacer(Modifier.height(12.dp))
            Text(
                "Incorrect PIN. Please try again.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(Modifier.height(32.dp))

        NumPad(
            onNumberClick = {
                if (pin.length < 4) {
                    pin += it
                    showError = false
                }
            },
            onDeleteClick = { if (pin.isNotEmpty()) pin = pin.dropLast(1) },
            onClearClick = { pin = "" }
        )

        Spacer(Modifier.weight(1f))
        Spacer(Modifier.height(16.dp))

        GradientButton(
            text = "Unlock",
            onClick = {
                if (pin == savedPin) {
                    onUnlock()
                } else {
                    showError = true
                    pin = ""
                }
            },
            enabled = pin.length == 4
        )

        Spacer(Modifier.height(24.dp))

        Text(
            "Forgot PIN code?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { showForgotDialog = true }
        )
    }

    // The Recovery Dialog
    if (showForgotDialog) {
        val context = LocalContext.current
        val settingsStore = remember { SettingsStore(context) }
        val question = settingsStore.getSecurityQuestion() ?: "No question set"

        var answer by remember { mutableStateOf("") }
        var answerError by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            title = { Text("Recover PIN") },
            text = {
                Column {
                    Text("Question: $question", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = answer,
                        onValueChange = { answer = it; answerError = false },
                        label = { Text("Your Answer") },
                        isError = answerError,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (answerError) {
                        Text("Incorrect answer", color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (settingsStore.checkSecurityAnswer(answer)) {
                        // Answer is correct. Show PIN to user.
                        showForgotDialog = false
                        Toast.makeText(context, "Your PIN is: $savedPin", Toast.LENGTH_LONG).show()
                        pin = savedPin ?: ""
                    } else {
                        answerError = true
                    }
                }) {
                    Text("Recover")
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun NumPad(
    onNumberClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onClearClick: () -> Unit
) {
    val row1 = listOf("1", "2", "3")
    val row2 = listOf("4", "5", "6")
    val row3 = listOf("7", "8", "9")
    val row4 = listOf("", "0", "DEL")

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        @Composable
        fun NumpadRow(items: List<String>) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { label ->
                    if (label.isEmpty()) {
                        Spacer(Modifier.weight(1f))
                    } else {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1.2f)
                                .clip(CircleShape)
                                .background(Color(0xFFF3F6F8))
                                .clickable {
                                    when (label) {
                                        "DEL" -> onDeleteClick()
                                        else -> onNumberClick(label)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (label == "DEL") {
                                Icon(
                                    Icons.AutoMirrored.Filled.Backspace,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                Text(
                                    label,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }
            }
        }

        NumpadRow(row1)
        NumpadRow(row2)
        NumpadRow(row3)
        NumpadRow(row4)
    }
}

@Composable
fun UnlockedView(viewModel: PainViewModel) {
    val records by viewModel.records.collectAsState()
    val context = LocalContext.current

    val totalRecords = records.size
    val avgPain = if (records.isNotEmpty()) records.map { it.painLevel }.average() else 0.0
    val topLocations = records.groupingBy { it.bodyPart }.eachCount().entries.sortedByDescending { it.value }.take(3)
    val trendData = remember(records) { generateTrendData(records) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AppCard {
            Column(Modifier.padding(16.dp)) {
                Text("Patient Overview", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("TOTAL LOGS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$totalRecords", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("AVG PAIN LEVEL", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(String.format("%.1f / 4", avgPain), style = MaterialTheme.typography.titleLarge, color = PainMild)
                    }
                }
            }
        }

        PainTrendCard(data = trendData)

        AppCard {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Most Affected Locations", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

                if (topLocations.isEmpty()) {
                    Text("No pain data recorded yet.", style = MaterialTheme.typography.bodyMedium)
                } else {
                    topLocations.forEachIndexed { index, (location, count) ->
                        val percentage = if (totalRecords > 0) (count.toFloat() / totalRecords * 100).toInt() else 0
                        LocationBar(rank = "${index + 1}", name = location, percentage = "$percentage%", logs = count)
                    }
                }
            }
        }

        ExportCard(onDownloadClick = { generatePdfReport(context, records) })
    }
}

@Composable
fun LocationBar(rank: String, name: String, percentage: String, logs: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(24.dp).background(MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(rank, color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.width(10.dp))

        Column(Modifier.weight(1f)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { percentage.replace("%", "").toFloat() / 100f },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("$logs recorded logs", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(percentage, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun PainTrendCard(data: List<Int>) {
    AppCard {
        Column(Modifier.padding(16.dp)) {
            Text("30-Day Pain Severity Trend", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(16.dp))

            Box(
                Modifier.fillMaxWidth().background(Color(0xFFF0F6FC), RoundedCornerShape(12.dp)).padding(12.dp)
            ) {
                Column {
                    Row(Modifier.fillMaxWidth()) {
                        Column(
                            Modifier.height(110.dp).padding(end = 8.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.End
                        ) {
                            Text("Lvl 4", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Lvl 2", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Lvl 0", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        TrendChart(data = data, modifier = Modifier.height(110.dp).weight(1f))
                    }
                }
            }
        }
    }
}

fun generateTrendData(records: List<PainRecord>): List<Int> {
    val today = java.time.LocalDate.now()
    return (29 downTo 0).map { daysAgo ->
        val date = today.minusDays(daysAgo.toLong())
        val dayRecords = records.filter {
            java.time.Instant.ofEpochMilli(it.timestamp).atZone(java.time.ZoneId.systemDefault()).toLocalDate() == date
        }
        dayRecords.maxOfOrNull { it.painLevel } ?: 0
    }
}

@Composable
fun ExportCard(onDownloadClick: () -> Unit) {
    AppCard {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Box(Modifier.size(36.dp).background(MaterialTheme.colorScheme.secondaryContainer, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("Certified Medical", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                    Text("Record Export", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("Export complete longitudinal logs, pain intensity notes, and trigger annotations.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            GradientButton(text = "Download Clinical PDF Report", onClick = onDownloadClick)
        }
    }
}

fun generatePdfReport(context: android.content.Context, records: List<PainRecord>) {
    try {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = android.graphics.Paint()

        paint.textSize = 24f
        paint.isFakeBoldText = true
        canvas.drawText("Pain Diary - Clinical Report", 50f, 60f, paint)

        paint.textSize = 14f
        paint.isFakeBoldText = false
        canvas.drawText("Total Logged Records: ${records.size}", 50f, 100f, paint)

        val avgPain = if (records.isNotEmpty()) records.map { it.painLevel }.average() else 0.0
        canvas.drawText("Average Pain Level: ${String.format("%.1f", avgPain)} / 4", 50f, 120f, paint)

        canvas.drawText("Top Locations:", 50f, 140f, paint)
        val topLocations = records.groupingBy { it.bodyPart }.eachCount().entries.sortedByDescending { it.value }.take(3)
        var yPos = 160f
        topLocations.forEach { (location, count) ->
            canvas.drawText("- $location ($count logs)", 60f, yPos, paint)
            yPos += 20f
        }

        yPos += 20f
        canvas.drawLine(50f, yPos, 545f, yPos, paint)
        yPos += 20f

        canvas.drawText("Recent Entries:", 50f, yPos, paint)
        yPos += 20f

        records.take(15).forEach { record ->
            if (yPos > 800f) return@forEach
            val date = java.time.Instant.ofEpochMilli(record.timestamp)
                .atZone(java.time.ZoneId.systemDefault())
                .format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mm a"))
            canvas.drawText("$date | ${record.bodyPart} | Level ${record.painLevel}", 50f, yPos, paint)
            yPos += 18f
        }

        pdfDocument.finishPage(page)

        val fileName = "Pain_Report_${System.currentTimeMillis()}.pdf"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                }
            }
        } else {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            outputStream.close()
        }

        pdfDocument.close()
        Toast.makeText(context, "PDF Saved to Downloads!", Toast.LENGTH_LONG).show()

    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Failed to generate PDF: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Doctor Screen - Locked")
@Composable
fun DoctorScreenLockedPreview() {
    BodyPainMapDiaryTheme {
        DoctorScreen(viewModel = androidx.lifecycle.viewmodel.compose.viewModel())
    }
}