package com.example.data.repository

import com.example.data.local.DefaultData
import com.example.data.local.FitPulseDao
import com.example.data.model.Exercise
import com.example.data.model.ExerciseSetLog
import com.example.data.model.FoodItem
import com.example.data.model.MealLog
import com.example.data.model.UserGoal
import com.example.data.model.UserProfile
import com.example.data.model.WaterLog
import com.example.data.model.WorkoutSession
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FitPulseRepository(private val dao: FitPulseDao) {

    val userProfile: Flow<UserProfile?> = dao.getUserProfile()
    val allFoodItems: Flow<List<FoodItem>> = dao.getAllFoodItems()
    val allExercises: Flow<List<Exercise>> = dao.getAllExercises()
    val allSessions: Flow<List<WorkoutSession>> = dao.getAllSessions()

    fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    suspend fun ensureInitialized() {
        val foodCount = dao.getFoodItemCount()
        if (foodCount == 0) {
            dao.insertAllFoodItems(DefaultData.FOOD_ITEMS)
        }
        val exerciseCount = dao.getExerciseCount()
        if (exerciseCount == 0) {
            dao.insertAllExercises(DefaultData.EXERCISES)
        }
    }

    // Profile & Goal
    suspend fun updateProfile(profile: UserProfile) {
        dao.insertOrUpdateProfile(profile)
    }

    suspend fun setGoal(goal: UserGoal, currentProfile: UserProfile) {
        val updated = currentProfile.copy(
            goalName = goal.name,
            targetCalories = goal.defaultCalories,
            targetProteinG = goal.defaultProteinG,
            targetCarbsG = goal.defaultCarbsG,
            targetFiberG = goal.defaultFiberG,
            targetFatsG = goal.defaultFatsG,
            targetActiveBurn = goal.defaultActiveBurnTarget
        )
        dao.insertOrUpdateProfile(updated)
    }

    // Diet & Meals
    fun getMealLogsForDate(date: String): Flow<List<MealLog>> {
        return dao.getMealLogsForDate(date)
    }

    fun searchFoods(query: String): Flow<List<FoodItem>> {
        return if (query.isBlank()) {
            dao.getAllFoodItems()
        } else {
            dao.searchFoodItems(query)
        }
    }

    suspend fun logMeal(
        food: FoodItem,
        mealType: String,
        servings: Double,
        dateString: String = getTodayDateString()
    ) {
        val mealLog = MealLog(
            foodItemId = food.id,
            foodName = food.name,
            mealType = mealType,
            dateString = dateString,
            servings = servings,
            calories = (food.calories * servings).toInt(),
            proteinG = food.proteinG * servings,
            carbsG = food.carbsG * servings,
            fiberG = food.fiberG * servings,
            fatsG = food.fatsG * servings,
            vitAMcg = food.vitAMcg * servings,
            vitCMg = food.vitCMg * servings,
            vitDIu = food.vitDIu * servings,
            vitB12Mcg = food.vitB12Mcg * servings,
            ironMg = food.ironMg * servings,
            calciumMg = food.calciumMg * servings,
            potassiumMg = food.potassiumMg * servings,
            magnesiumMg = food.magnesiumMg * servings,
            zincMg = food.zincMg * servings,
            sodiumMg = food.sodiumMg * servings
        )
        dao.insertMealLog(mealLog)
    }

    suspend fun deleteMealLog(id: Long) {
        dao.deleteMealLogById(id)
    }

    suspend fun addCustomFood(foodItem: FoodItem): Long {
        return dao.insertFoodItem(foodItem.copy(isCustom = true))
    }

    // Water Tracker
    fun getWaterLogsForDate(date: String): Flow<List<WaterLog>> {
        return dao.getWaterLogsForDate(date)
    }

    suspend fun addWater(amountMl: Int, dateString: String = getTodayDateString()) {
        dao.insertWaterLog(
            WaterLog(
                dateString = dateString,
                amountMl = amountMl
            )
        )
    }

    suspend fun clearWaterForDate(date: String = getTodayDateString()) {
        dao.clearWaterLogsForDate(date)
    }

    // Gym & Workouts
    fun getSessionsForDate(date: String): Flow<List<WorkoutSession>> {
        return dao.getSessionsForDate(date)
    }

    fun getSetLogsForSession(sessionId: Long): Flow<List<ExerciseSetLog>> {
        return dao.getSetLogsForSession(sessionId)
    }

    fun getSetLogsForDate(date: String): Flow<List<ExerciseSetLog>> {
        return dao.getSetLogsForDate(date)
    }

    fun getSetLogsForExercise(exerciseId: Long): Flow<List<ExerciseSetLog>> {
        return dao.getSetLogsForExercise(exerciseId)
    }

    fun searchExercises(query: String): Flow<List<Exercise>> {
        return if (query.isBlank()) {
            dao.getAllExercises()
        } else {
            dao.searchExercises(query)
        }
    }

    fun getExercisesByMuscleGroup(group: String): Flow<List<Exercise>> {
        return dao.getExercisesByMuscleGroup(group)
    }

    suspend fun saveWorkoutSession(
        title: String,
        durationMinutes: Int,
        sets: List<ExerciseSetLog>,
        userWeightKg: Float,
        notes: String = ""
    ): Long {
        val dateString = getTodayDateString()
        var totalVolume = 0.0
        var totalCaloriesBurned = 0.0

        sets.forEach { set ->
            totalVolume += (set.weightKg * set.reps)
            // Weight lifted + set duration MET formula
            val calorieForSet = set.caloriesBurned
            totalCaloriesBurned += calorieForSet
        }

        // Base MET burn for the active workout duration:
        // Calories = (MET * 3.5 * weightKg / 200) * durationMinutes
        val activeWorkoutBurn = (6.0 * 3.5 * userWeightKg / 200.0) * durationMinutes
        val finalCaloriesBurned = (activeWorkoutBurn + (totalCaloriesBurned * 0.4)).toInt().coerceAtLeast(30)

        val session = WorkoutSession(
            title = title,
            dateString = dateString,
            durationMinutes = durationMinutes,
            totalCaloriesBurned = finalCaloriesBurned,
            totalVolumeKg = totalVolume,
            totalSets = sets.size,
            notes = notes
        )

        val sessionId = dao.insertSession(session)
        sets.forEach { set ->
            dao.insertSetLog(
                set.copy(
                    sessionId = sessionId,
                    dateString = dateString
                )
            )
        }
        return sessionId
    }

    suspend fun deleteSession(sessionId: Long) {
        dao.deleteSetLogsForSession(sessionId)
        dao.deleteSessionById(sessionId)
    }

    suspend fun addCustomExercise(exercise: Exercise): Long {
        return dao.insertExercise(exercise.copy(isCustom = true))
    }
}
