package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_items")
data class FoodItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String,
    val servingSize: Double = 100.0,
    val servingUnit: String = "g",
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
    val sodiumMg: Double = 0.0,
    val isCustom: Boolean = false
)
