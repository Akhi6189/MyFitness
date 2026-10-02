package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val goalName: String = UserGoal.VISIBLE_MUSCLES.name,
    val targetCalories: Int = UserGoal.VISIBLE_MUSCLES.defaultCalories,
    val targetProteinG: Int = UserGoal.VISIBLE_MUSCLES.defaultProteinG,
    val targetCarbsG: Int = UserGoal.VISIBLE_MUSCLES.defaultCarbsG,
    val targetFiberG: Int = UserGoal.VISIBLE_MUSCLES.defaultFiberG,
    val targetFatsG: Int = UserGoal.VISIBLE_MUSCLES.defaultFatsG,
    val targetWaterMl: Int = 3000,
    val targetActiveBurn: Int = UserGoal.VISIBLE_MUSCLES.defaultActiveBurnTarget,
    val userWeightKg: Float = 75f,
    val userHeightCm: Float = 175f
) {
    val goal: UserGoal
        get() = UserGoal.fromName(goalName)
}
