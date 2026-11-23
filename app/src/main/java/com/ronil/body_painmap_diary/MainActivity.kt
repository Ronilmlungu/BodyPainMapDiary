
package com.ronil.body_painmap_diary

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.firebase.auth.FirebaseAuth
import com.ronil.body_painmap_diary.ui.AppRoot
import com.ronil.body_painmap_diary.ui.theme.BodyPainMapDiaryTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sign in anonymously if the user is not already authenticated
        val auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            auth.signInAnonymously()
                .addOnFailureListener { exception ->
                    Log.e("FirebaseAuth", "Anonymous sign-in failed", exception)
                }
        }

        enableEdgeToEdge()

        setContent {
            BodyPainMapDiaryTheme {
                AppRoot()
            }
        }
    }
}
