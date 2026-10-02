package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_logs")
data class MealLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val foodItemId: Long,
    val foodName: String,
    val mealType: String, // Breakfast, Lunch, Dinner, Snack
    val dateString: String, // YYYY-MM-DD
    val timestamp: Long = System.currentTimeMillis(),
    val servings: Double = 1.0,
    val calories: Int,
    val proteinG: Double,
    val carbsG: Double,
    val fiberG: Double,
    val fatsG: Double,
    // Vitamins
    val vitAMcg: Double = 0.0,
    val vitCMg: Double = 0.0,
    val vitDIu: Double = 0.0,
    val vitB12Mcg: Double = 0.0,
    // Minerals
    val ironMg: Double = 0.0,
    val calciumMg: Double = 0.0,
    val potassiumMg: Double = 0.0,
    val magnesiumMg: Double = 0.0,
    val zincMg: Double = 0.0,
    val sodiumMg: Double = 0.0
)
