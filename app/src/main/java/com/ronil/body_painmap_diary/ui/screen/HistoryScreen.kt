package com.ronil.body_painmap_diary.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.ronil.body_painmap_diary.data.PainRecord
import com.ronil.body_painmap_diary.model.PainLevel
import com.ronil.body_painmap_diary.ui.components.AppCard
import com.ronil.body_painmap_diary.ui.theme.BodyPainMapDiaryTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HistoryScreen(viewModel: PainViewModel) {
    val records by viewModel.records.collectAsState()

    val today = LocalDate.now()
    // Selected Day state now starts at TODAY
    var selectedDay by remember { mutableStateOf(today.dayOfMonth) }

    val currentMonthYear = today.format(DateTimeFormatter.ofPattern("MMMM yyyy"))

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Pain History",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Column(
            Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SummaryCard(records)

            CalendarCard(
                currentMonthYear = currentMonthYear,
                today = today,
                selectedDay = selectedDay,
                onDaySelected = { selectedDay = it },
                records = records
            )

            // We pass the month and year explicitly so it doesn't get confused
            DayDetailsCard(
                day = selectedDay,
                records = records,
                monthName = today.format(DateTimeFormatter.ofPattern("MMMM")),
                year = today.year
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun SummaryCard(records: List<PainRecord>) {
    val totalLogged = records.size
    val topLocation = records.groupingBy { it.bodyPart }.eachCount().maxByOrNull { it.value }?.key ?: "None"
    val avgPain = if (records.isNotEmpty()) records.map { it.painLevel }.average() else 0.0

    AppCard {
        Column(Modifier.padding(16.dp)) {
            Text("Past 30 Days Summary", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha=0.3f), RoundedCornerShape(12.dp)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.EventAvailable, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Total Logged Days", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
                Text("$totalLogged", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }

            Spacer(Modifier.height(8.dp))

            Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha=0.3f), RoundedCornerShape(12.dp)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Accessibility, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Most Frequent Location", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Text(topLocation, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha=0.3f), RoundedCornerShape(12.dp)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.SentimentSatisfied, contentDescription = null, tint = PainLevel.Some.fill)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Average Pain Level", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
                Surface(color = PainLevel.Some.fill.copy(alpha=0.2f), shape = RoundedCornerShape(8.dp)) {
                    Text(String.format("%.1f", avgPain), style = MaterialTheme.typography.titleMedium, color = Color.Black, modifier = Modifier.padding(6.dp))
                }
            }
        }
    }
}

@Composable
fun CalendarCard(
    currentMonthYear: String,
    today: LocalDate,
    selectedDay: Int,
    onDaySelected: (Int) -> Unit,
    records: List<PainRecord>
) {
    AppCard {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.ChevronLeft, contentDescription = "Prev Month", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(currentMonthYear, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Icon(Icons.Filled.ChevronRight, contentDescription = "Next Month", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa").forEach {
                    Text(it, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(36.dp), textAlign = TextAlign.Center)
                }
            }
            Spacer(Modifier.height(8.dp))

            // Dynamic Calendar Math
            val daysInMonth = today.lengthOfMonth()
            val firstDayOfMonth = today.withDayOfMonth(1).dayOfWeek.value % 7

            var currentDay = 1
            for (week in 0 until 6) {
                if (currentDay > daysInMonth) break

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    for (dayOfWeek in 0 until 7) {
                        if (week == 0 && dayOfWeek < firstDayOfMonth) {
                            Spacer(Modifier.size(36.dp))
                        } else if (currentDay <= daysInMonth) {
                            val dayToDraw = currentDay // Capture the value for the lambda

                            // Check if there is a real record for this exact day
                            val hasRecord = records.any {
                                java.time.Instant.ofEpochMilli(it.timestamp)
                                    .atZone(java.time.ZoneId.systemDefault()).dayOfMonth == dayToDraw
                            }

                            CalendarDayCell(
                                day = dayToDraw,
                                isSelected = dayToDraw == selectedDay,
                                onClick = { onDaySelected(dayToDraw) },
                                hasRecord = hasRecord
                            )
                            currentDay++
                        } else {
                            Spacer(Modifier.size(36.dp))
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun CalendarDayCell(day: Int, isSelected: Boolean, onClick: () -> Unit, hasRecord: Boolean) {
    Column(
        modifier = Modifier
            .width(36.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                day.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(if (hasRecord) MaterialTheme.colorScheme.primary else Color.Transparent, CircleShape)
        )
    }
}

@Composable
fun DayDetailsCard(day: Int, records: List<PainRecord>, monthName: String, year: Int) {
    // Find records matching the exact selected day
    val dailyRecords = records.filter {
        java.time.Instant.ofEpochMilli(it.timestamp)
            .atZone(java.time.ZoneId.systemDefault()).dayOfMonth == day
    }

    AppCard {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Selected Date", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    // Dynamically show the selected date (e.g., November 3, 2026)
                    Text("$monthName $day, $year", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(16.dp))

            if (dailyRecords.isEmpty()) {
                Text("No records found for this day.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                dailyRecords.forEach { record ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                        Box(Modifier.size(48.dp).background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.AccessibilityNew, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Affected Area", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(record.bodyPart, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }

                        val levelData = PainLevel.entries.first { it.value == record.painLevel }
                        Surface(color = levelData.fill.copy(alpha=0.2f), shape = RoundedCornerShape(8.dp)) {
                            Text(
                                "${levelData.value} - ${levelData.label}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = levelData.fill,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}