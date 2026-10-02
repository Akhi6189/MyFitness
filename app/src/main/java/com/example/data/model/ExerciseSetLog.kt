package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_set_logs")
data class ExerciseSetLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val exerciseId: Long,
    val exerciseName: String,
    val muscleGroup: String,
    val setNumber: Int,
    val weightKg: Double,
    val reps: Int,
    val isCompleted: Boolean = true,
    val rpe: Double = 8.0,
    val caloriesBurned: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val dateString: String // YYYY-MM-DD
)
