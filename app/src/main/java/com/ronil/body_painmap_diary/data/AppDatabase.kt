package com.ronil.body_painmap_diary.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [PainRecord::class], version = 2, exportSchema = false) // Version bumped to 2
abstract class AppDatabase : RoomDatabase() {
    abstract fun painDao(): PainDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // NEW: This tells Room how to handle the new "synced" column without deleting user data
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE pain_records ADD COLUMN synced INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pain_diary_database"
                )
                    .addMigrations(MIGRATION_1_2) // NEW: Add the migration here
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}