package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_sessions")
data class WorkoutSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val dateString: String, // YYYY-MM-DD
    val timestamp: Long = System.currentTimeMillis(),
    val durationMinutes: Int = 45,
    val totalCaloriesBurned: Int = 0,
    val totalVolumeKg: Double = 0.0,
    val totalSets: Int = 0,
    val notes: String = ""
)
