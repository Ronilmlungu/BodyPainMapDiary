package com.ronil.body_painmap_diary.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PainDao {
    @Insert
    suspend fun insertRecord(record: PainRecord)

    // Flow means it automatically updates the UI when new data is added
    @Query("SELECT * FROM pain_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<PainRecord>>

    // NEW: Get only records that have not been uploaded to Firebase yet
    @Query("SELECT * FROM pain_records WHERE synced = 0")
    suspend fun getUnsyncedRecords(): List<PainRecord>

    // NEW: Mark a record as uploaded so we don't upload it again
    @Query("UPDATE pain_records SET synced = 1 WHERE id = :recordId")
    suspend fun markAsSynced(recordId: Int)
}