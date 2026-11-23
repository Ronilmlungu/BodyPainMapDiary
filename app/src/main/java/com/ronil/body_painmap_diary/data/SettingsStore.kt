package com.ronil.body_painmap_diary.data

import android.content.Context

class SettingsStore(context: Context) {
    // "pain_diary_prefs" is the name of the file on the phone
    private val prefs = context.getSharedPreferences("pain_diary_prefs", Context.MODE_PRIVATE)

    // --- PIN Functions ---
    fun savePin(pin: String) {
        prefs.edit().putString("doctor_pin", pin).apply()
    }

    fun getPin(): String? {
        return prefs.getString("doctor_pin", null)
    }

    // --- Security Question Functions ---
    fun saveSecurityQuestion(question: String, answer: String) {
        prefs.edit()
            .putString("security_question", question)
            .putString("security_answer", answer.lowercase().trim()) // normalize answer
            .apply()
    }

    fun getSecurityQuestion(): String? {
        return prefs.getString("security_question", null)
    }

    fun checkSecurityAnswer(input: String): Boolean {
        val savedAnswer = prefs.getString("security_answer", null) ?: return false
        return savedAnswer == input.lowercase().trim()
    }
}