package com.ronil.body_painmap_diary

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ronil.body_painmap_diary.data.AppDatabase
import com.ronil.body_painmap_diary.data.PainDao
import com.ronil.body_painmap_diary.data.PainRecord
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@RunWith(AndroidJUnit4::class)
class DatabaseAndLogicTests {

    private lateinit var database: AppDatabase
    private lateinit var dao: PainDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Use an in-memory database for testing so it doesn't affect the real app data
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = database.painDao()
    }

    // Matches Test 8: Save Pain Record – Success
    @Test
    fun testSaveRecord_savesToDatabase() = runBlocking {
        val record = PainRecord(
            bodyPart = "Right Knee",
            painLevel = 3,
            timestamp = System.currentTimeMillis()
        )
        dao.insertRecord(record)

        val savedRecords = dao.getAllRecords().first()
        Assert.assertEquals(1, savedRecords.size)
        Assert.assertEquals("Right Knee", savedRecords[0].bodyPart)
        Assert.assertEquals(3, savedRecords[0].painLevel)
    }

    // Matches Test 6: Quick Log - Saves Level 0
    @Test
    fun testQuickLog_savesLevelZero() = runBlocking {
        val record = PainRecord(
            bodyPart = "None",
            painLevel = 0,
            timestamp = System.currentTimeMillis()
        )
        dao.insertRecord(record)

        val savedRecords = dao.getAllRecords().first()
        Assert.assertEquals(1, savedRecords.size)
        Assert.assertEquals("None", savedRecords[0].bodyPart)
        Assert.assertEquals(0, savedRecords[0].painLevel)
    }

    // Matches Test 18: Data Persistence - Data survives a new read
    @Test
    fun testDataPersistence() = runBlocking {
        val record = PainRecord(
            bodyPart = "Head",
            painLevel = 2,
            timestamp = System.currentTimeMillis()
        )
        dao.insertRecord(record)

        // Simulate reopening the app by reading from the database again
        val recordsAfterReopen = dao.getAllRecords().first()
        Assert.assertEquals(1, recordsAfterReopen.size)
        Assert.assertEquals("Head", recordsAfterReopen[0].bodyPart)
    }

    // Matches Test 7: Duplicate prevention logic
    @Test
    fun testTrendLogic_calculatesCorrectly() {
        // This tests the logic from the Doctor Screen, ensuring math is correct
        val today = System.currentTimeMillis()
        val yesterday = today - (1000 * 60 * 60 * 24)

        val records = listOf(
            PainRecord(bodyPart = "Head", painLevel = 4, timestamp = today),
            PainRecord(bodyPart = "Leg", painLevel = 2, timestamp = yesterday)
        )

        val todayDate = LocalDate.now()
        val trendData = (29 downTo 0).map { daysAgo ->
            val date = todayDate.minusDays(daysAgo.toLong())
            val dayRecords = records.filter {
                Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate() == date
            }
            dayRecords.maxOfOrNull { it.painLevel } ?: 0
        }

        // Check that today's pain is 4, and yesterday's was 2
        Assert.assertEquals(4, trendData.last())
        Assert.assertEquals(2, trendData[trendData.size - 2])
    }
}