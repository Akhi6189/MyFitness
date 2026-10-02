package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val muscleGroup: String, // Chest, Back, Legs, Shoulders, Arms, Core, Cardio
    val equipment: String, // Barbell, Dumbbell, Machine, Bodyweight, Cable, Cardio
    val instructions: String,
    val metValue: Double = 5.5,
    val defaultCaloriesPerRep: Double = 0.35,
    val isCustom: Boolean = false
)
