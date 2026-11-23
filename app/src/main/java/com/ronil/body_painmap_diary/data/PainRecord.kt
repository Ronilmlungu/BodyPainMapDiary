package com.ronil.body_painmap_diary.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pain_records")
data class PainRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bodyPart: String,      // e.g., "Right Knee"
    val painLevel: Int,        // 0 to 4
    val timestamp: Long ,      // The exact time they saved it (System.currentTimeMillis())
    val synced: Boolean = false
)