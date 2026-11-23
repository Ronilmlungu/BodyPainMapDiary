package com.ronil.body_painmap_diary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.ronil.body_painmap_diary.data.AppDatabase
import com.ronil.body_painmap_diary.data.PainRecord
import com.ronil.body_painmap_diary.data.SyncWorker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class PainViewModel(application: Application) : AndroidViewModel(application) {

    // 1. Connect to the Database
    private val dao = AppDatabase.getDatabase(application).painDao()

    // 2. Live stream of records. The UI will watch this.
    val records = dao.getAllRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Helper function to check if a SPECIFIC body part was already saved today
    private fun hasRecordForToday(bodyPart: String): Boolean {
        val today = LocalDate.now()
        return records.value.any { record ->
            val recordDate = java.time.Instant.ofEpochMilli(record.timestamp)
                .atZone(ZoneId.systemDefault()).toLocalDate()

            // It only returns true if the date is today AND the body part matches
            recordDate == today && record.bodyPart == bodyPart
        }
    }

    // NEW: Function to trigger the background sync to Firebase
    private fun triggerSync() {
        // We only want to sync if the phone has an internet connection
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(getApplication()).enqueue(syncRequest)
    }

    // 3. Save a new record (Prevents duplicates for the SAME body part on the same day)
    fun savePainRecord(bodyPart: String, level: Int) {
        viewModelScope.launch {
            if (!hasRecordForToday(bodyPart)) {
                val record = PainRecord(
                    bodyPart = bodyPart,
                    painLevel = level,
                    timestamp = System.currentTimeMillis()
                )
                dao.insertRecord(record)

                // NEW: Trigger WorkManager right after saving to Room
                triggerSync()
            }
        }
    }

    // 4. Quick Log (No Pain) - Prevents duplicates for "None" on the same day
    fun logNoPain() {
        viewModelScope.launch {
            if (!hasRecordForToday("None")) {
                val record = PainRecord(
                    bodyPart = "None",
                    painLevel = 0,
                    timestamp = System.currentTimeMillis()
                )
                dao.insertRecord(record)

                // NEW: Trigger WorkManager right after saving to Room
                triggerSync()
            }
        }
    }
}