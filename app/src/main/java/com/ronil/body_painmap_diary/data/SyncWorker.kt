
package com.ronil.body_painmap_diary.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {

        val db = AppDatabase.getDatabase(applicationContext)
        val dao = db.painDao()

        return try {

            // 1. Get unsynced records from Room
            val unsyncedRecords = dao.getUnsyncedRecords()

            if (unsyncedRecords.isEmpty()) {
                return Result.success()
            }

            // 2. Get the authenticated Firebase user
            val auth = FirebaseAuth.getInstance()

            val user = auth.currentUser
                ?: auth.signInAnonymously().await().user
                ?: return Result.retry()

            val userId = user.uid

            val firestore = FirebaseFirestore.getInstance()

            // 3. Upload records into this user's own collection
            for (record in unsyncedRecords) {

                val recordData = hashMapOf(
                    "bodyPart" to record.bodyPart,
                    "painLevel" to record.painLevel,
                    "timestamp" to record.timestamp
                )

                firestore.collection("users")
                    .document(userId)
                    .collection("pain_records")
                    .document(record.id.toString())
                    .set(recordData)
                    .await()

                // 4. Mark record as synced after successful upload
                dao.markAsSynced(record.id)
            }

            Result.success()

        } catch (e: Exception) {

            e.printStackTrace()

            // Retry if authentication or network access fails
            Result.retry()
        }
    }
}
