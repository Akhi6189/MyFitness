package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Exercise
import com.example.data.model.ExerciseSetLog
import com.example.data.model.FoodItem
import com.example.data.model.MealLog
import com.example.data.model.UserProfile
import com.example.data.model.WaterLog
import com.example.data.model.WorkoutSession
import kotlinx.coroutines.flow.Flow

@Dao
interface FitPulseDao {

    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile)

    // Food Database
    @Query("SELECT * FROM food_items ORDER BY name ASC")
    fun getAllFoodItems(): Flow<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE name LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchFoodItems(query: String): Flow<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE id = :id LIMIT 1")
    fun getFoodItemById(id: Long): Flow<FoodItem?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodItem(item: FoodItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFoodItems(items: List<FoodItem>)

    @Query("SELECT COUNT(*) FROM food_items")
    suspend fun getFoodItemCount(): Int

    // Meal Logs
    @Query("SELECT * FROM meal_logs WHERE dateString = :date ORDER BY timestamp DESC")
    fun getMealLogsForDate(date: String): Flow<List<MealLog>>

    @Query("SELECT * FROM meal_logs ORDER BY timestamp DESC")
    fun getAllMealLogs(): Flow<List<MealLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealLog(mealLog: MealLog): Long

    @Delete
    suspend fun deleteMealLog(mealLog: MealLog)

    @Query("DELETE FROM meal_logs WHERE id = :id")
    suspend fun deleteMealLogById(id: Long)

    // Water Logs
    @Query("SELECT * FROM water_logs WHERE dateString = :date ORDER BY timestamp DESC")
    fun getWaterLogsForDate(date: String): Flow<List<WaterLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterLog(waterLog: WaterLog): Long

    @Query("DELETE FROM water_logs WHERE id = :id")
    suspend fun deleteWaterLogById(id: Long)

    @Query("DELETE FROM water_logs WHERE dateString = :date")
    suspend fun clearWaterLogsForDate(date: String)

    // Exercise Library
    @Query("SELECT * FROM exercises ORDER BY name ASC")
    fun getAllExercises(): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE muscleGroup = :group ORDER BY name ASC")
    fun getExercisesByMuscleGroup(group: String): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE name LIKE '%' || :query || '%' OR muscleGroup LIKE '%' || :query || '%' OR equipment LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchExercises(query: String): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE id = :id LIMIT 1")
    fun getExerciseById(id: Long): Flow<Exercise?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: Exercise): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllExercises(exercises: List<Exercise>)

    @Query("SELECT COUNT(*) FROM exercises")
    suspend fun getExerciseCount(): Int

    // Workout Sessions
    @Query("SELECT * FROM workout_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workout_sessions WHERE dateString = :date ORDER BY timestamp DESC")
    fun getSessionsForDate(date: String): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workout_sessions WHERE id = :id LIMIT 1")
    fun getSessionById(id: Long): Flow<WorkoutSession?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WorkoutSession): Long

    @Update
    suspend fun updateSession(session: WorkoutSession)

    @Query("DELETE FROM workout_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: Long)

    // Exercise Set Logs
    @Query("SELECT * FROM exercise_set_logs WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getSetLogsForSession(sessionId: Long): Flow<List<ExerciseSetLog>>

    @Query("SELECT * FROM exercise_set_logs WHERE dateString = :date ORDER BY timestamp DESC")
    fun getSetLogsForDate(date: String): Flow<List<ExerciseSetLog>>

    @Query("SELECT * FROM exercise_set_logs WHERE exerciseId = :exerciseId ORDER BY timestamp DESC")
    fun getSetLogsForExercise(exerciseId: Long): Flow<List<ExerciseSetLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetLog(setLog: ExerciseSetLog): Long

    @Query("DELETE FROM exercise_set_logs WHERE id = :id")
    suspend fun deleteSetLogById(id: Long)

    @Query("DELETE FROM exercise_set_logs WHERE sessionId = :sessionId")
    suspend fun deleteSetLogsForSession(sessionId: Long)

    @Query("SELECT * FROM exercise_set_logs ORDER BY timestamp DESC")
    fun getAllSetLogs(): Flow<List<ExerciseSetLog>>
}
