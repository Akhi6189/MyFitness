package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Exercise
import com.example.data.model.ExerciseSetLog
import com.example.data.model.FoodItem
import com.example.data.model.MealLog
import com.example.data.model.UserGoal
import com.example.data.model.UserProfile
import com.example.data.model.WaterLog
import com.example.data.model.WorkoutSession
import com.example.data.repository.FitPulseRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ActiveWorkoutSet(
    val exerciseId: Long,
    val exerciseName: String,
    val muscleGroup: String,
    val setNumber: Int,
    val weightKg: Double,
    val reps: Int,
    val caloriesBurned: Double
)

data class ActiveWorkoutState(
    val isActive: Boolean = false,
    val title: String = "Gym Session",
    val startTimestamp: Long = 0L,
    val elapsedSeconds: Int = 0,
    val sets: List<ActiveWorkoutSet> = emptyList()
)

data class NutritionTotals(
    val calories: Int = 0,
    val proteinG: Double = 0.0,
    val carbsG: Double = 0.0,
    val fiberG: Double = 0.0,
    val fatsG: Double = 0.0,
    val vitAMcg: Double = 0.0,
    val vitCMg: Double = 0.0,
    val vitDIu: Double = 0.0,
    val vitB12Mcg: Double = 0.0,
    val ironMg: Double = 0.0,
    val calciumMg: Double = 0.0,
    val potassiumMg: Double = 0.0,
    val magnesiumMg: Double = 0.0,
    val zincMg: Double = 0.0,
    val sodiumMg: Double = 0.0
)

class FitPulseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FitPulseRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = FitPulseRepository(database.fitPulseDao())
        viewModelScope.launch {
            repository.ensureInitialized()
        }
    }

    val todayDateString: String = repository.getTodayDateString()

    val userProfile: StateFlow<UserProfile> = repository.userProfile
        .combine(MutableStateFlow(UserProfile())) { dbProfile, defaultProfile ->
            dbProfile ?: defaultProfile
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProfile()
        )

    val todayMealLogs: StateFlow<List<MealLog>> = repository.getMealLogsForDate(todayDateString)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val todayWaterLogs: StateFlow<List<WaterLog>> = repository.getWaterLogsForDate(todayDateString)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val todaySessions: StateFlow<List<WorkoutSession>> = repository.getSessionsForDate(todayDateString)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allSessions: StateFlow<List<WorkoutSession>> = repository.allSessions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allFoodItems: StateFlow<List<FoodItem>> = repository.allFoodItems
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allExercises: StateFlow<List<Exercise>> = repository.allExercises
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Calculated Daily Nutrition Totals
    val nutritionTotals: StateFlow<NutritionTotals> = todayMealLogs.combine(todayMealLogs) { logs, _ ->
        var cal = 0
        var p = 0.0
        var c = 0.0
        var fib = 0.0
        var fat = 0.0
        var vA = 0.0
        var vC = 0.0
        var vD = 0.0
        var vB12 = 0.0
        var fe = 0.0
        var ca = 0.0
        var k = 0.0
        var mg = 0.0
        var zn = 0.0
        var na = 0.0

        logs.forEach { log ->
            cal += log.calories
            p += log.proteinG
            c += log.carbsG
            fib += log.fiberG
            fat += log.fatsG
            vA += log.vitAMcg
            vC += log.vitCMg
            vD += log.vitDIu
            vB12 += log.vitB12Mcg
            fe += log.ironMg
            ca += log.calciumMg
            k += log.potassiumMg
            mg += log.magnesiumMg
            zn += log.zincMg
            na += log.sodiumMg
        }

        NutritionTotals(
            calories = cal,
            proteinG = p,
            carbsG = c,
            fiberG = fib,
            fatsG = fat,
            vitAMcg = vA,
            vitCMg = vC,
            vitDIu = vD,
            vitB12Mcg = vB12,
            ironMg = fe,
            calciumMg = ca,
            potassiumMg = k,
            magnesiumMg = mg,
            zincMg = zn,
            sodiumMg = na
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NutritionTotals()
    )

    // Total Water Logged Today
    val totalWaterMl: StateFlow<Int> = todayWaterLogs.combine(todayWaterLogs) { logs, _ ->
        logs.sumOf { it.amountMl }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    // Total Calories Burned Today from Workouts
    val totalCaloriesBurnedToday: StateFlow<Int> = todaySessions.combine(todaySessions) { sessions, _ ->
        sessions.sumOf { it.totalCaloriesBurned }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    // Active Workout State
    private val _activeWorkout = MutableStateFlow(ActiveWorkoutState())
    val activeWorkout: StateFlow<ActiveWorkoutState> = _activeWorkout.asStateFlow()

    private var workoutTimerJob: Job? = null

    // Rest Timer
    private val _restSecondsRemaining = MutableStateFlow(0)
    val restSecondsRemaining: StateFlow<Int> = _restSecondsRemaining.asStateFlow()
    private var restTimerJob: Job? = null

    fun selectGoal(goal: UserGoal) {
        viewModelScope.launch {
            val current = userProfile.value
            repository.setGoal(goal, current)
        }
    }

    fun updateProfile(
        goal: UserGoal,
        targetCal: Int,
        targetP: Int,
        targetC: Int,
        targetFib: Int,
        targetFat: Int,
        targetWater: Int,
        targetBurn: Int,
        weightKg: Float,
        heightCm: Float
    ) {
        viewModelScope.launch {
            val updated = userProfile.value.copy(
                goalName = goal.name,
                targetCalories = targetCal,
                targetProteinG = targetP,
                targetCarbsG = targetC,
                targetFiberG = targetFib,
                targetFatsG = targetFat,
                targetWaterMl = targetWater,
                targetActiveBurn = targetBurn,
                userWeightKg = weightKg,
                userHeightCm = heightCm
            )
            repository.updateProfile(updated)
        }
    }

    // Food & Meal Actions
    fun logMeal(food: FoodItem, mealType: String, servings: Double) {
        viewModelScope.launch {
            repository.logMeal(food, mealType, servings, todayDateString)
        }
    }

    fun deleteMealLog(mealLogId: Long) {
        viewModelScope.launch {
            repository.deleteMealLog(mealLogId)
        }
    }

    fun addCustomFood(foodItem: FoodItem) {
        viewModelScope.launch {
            repository.addCustomFood(foodItem)
        }
    }

    // Water Actions
    fun logWater(amountMl: Int) {
        viewModelScope.launch {
            repository.addWater(amountMl, todayDateString)
        }
    }

    fun clearWater() {
        viewModelScope.launch {
            repository.clearWaterForDate(todayDateString)
        }
    }

    // Workout Tracker Actions
    fun startActiveWorkout(title: String = "Gym Session") {
        _activeWorkout.value = ActiveWorkoutState(
            isActive = true,
            title = title,
            startTimestamp = System.currentTimeMillis(),
            elapsedSeconds = 0,
            sets = emptyList()
        )
        startWorkoutTimer()
    }

    private fun startWorkoutTimer() {
        workoutTimerJob?.cancel()
        workoutTimerJob = viewModelScope.launch {
            while (_activeWorkout.value.isActive) {
                delay(1000)
                _activeWorkout.value = _activeWorkout.value.copy(
                    elapsedSeconds = _activeWorkout.value.elapsedSeconds + 1
                )
            }
        }
    }

    fun addSetToActiveWorkout(
        exercise: Exercise,
        weightKg: Double,
        reps: Int
    ) {
        val currentSets = _activeWorkout.value.sets
        val nextSetNumber = currentSets.count { it.exerciseId == exercise.id } + 1

        // Calculate calories burned for this set
        // Weight factor + rep MET
        val userWeight = userProfile.value.userWeightKg
        val loadFactor = 1.0 + (weightKg / (userWeight * 2.0)).coerceIn(0.0, 1.5)
        val calculatedBurn = (exercise.defaultCaloriesPerRep * reps * loadFactor).coerceAtLeast(1.0)

        val newSet = ActiveWorkoutSet(
            exerciseId = exercise.id,
            exerciseName = exercise.name,
            muscleGroup = exercise.muscleGroup,
            setNumber = nextSetNumber,
            weightKg = weightKg,
            reps = reps,
            caloriesBurned = calculatedBurn
        )

        _activeWorkout.value = _activeWorkout.value.copy(
            sets = currentSets + newSet
        )

        // Start standard 60-second rest timer
        startRestTimer(60)
    }

    fun removeSetFromActiveWorkout(index: Int) {
        val currentSets = _activeWorkout.value.sets.toMutableList()
        if (index in currentSets.indices) {
            currentSets.removeAt(index)
            _activeWorkout.value = _activeWorkout.value.copy(sets = currentSets)
        }
    }

    fun finishActiveWorkout(notes: String = "") {
        val current = _activeWorkout.value
        val durationMins = (current.elapsedSeconds / 60).coerceAtLeast(1)

        val entitySets = current.sets.map { s ->
            ExerciseSetLog(
                sessionId = 0L,
                exerciseId = s.exerciseId,
                exerciseName = s.exerciseName,
                muscleGroup = s.muscleGroup,
                setNumber = s.setNumber,
                weightKg = s.weightKg,
                reps = s.reps,
                isCompleted = true,
                caloriesBurned = s.caloriesBurned,
                dateString = todayDateString
            )
        }

        viewModelScope.launch {
            repository.saveWorkoutSession(
                title = current.title,
                durationMinutes = durationMins,
                sets = entitySets,
                userWeightKg = userProfile.value.userWeightKg,
                notes = notes
            )
            cancelActiveWorkout()
        }
    }

    fun cancelActiveWorkout() {
        workoutTimerJob?.cancel()
        workoutTimerJob = null
        _activeWorkout.value = ActiveWorkoutState(isActive = false)
        stopRestTimer()
    }

    fun startRestTimer(seconds: Int = 60) {
        restTimerJob?.cancel()
        _restSecondsRemaining.value = seconds
        restTimerJob = viewModelScope.launch {
            while (_restSecondsRemaining.value > 0) {
                delay(1000)
                _restSecondsRemaining.value -= 1
            }
        }
    }

    fun stopRestTimer() {
        restTimerJob?.cancel()
        restTimerJob = null
        _restSecondsRemaining.value = 0
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            repository.deleteSession(sessionId)
        }
    }

    fun addCustomExercise(exercise: Exercise) {
        viewModelScope.launch {
            repository.addCustomExercise(exercise)
        }
    }
}
