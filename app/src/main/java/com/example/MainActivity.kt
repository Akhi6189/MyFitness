package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.FoodItem
import com.example.ui.components.AddFoodSearchDialog
import com.example.ui.components.FoodDetailDialog
import com.example.ui.components.GoalSettingsDialog
import com.example.ui.components.LogExerciseDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DietScreen
import com.example.ui.screens.GymScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.FitPulseViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                FitPulseApp()
            }
        }
    }
}

@Composable
fun FitPulseApp(viewModel: FitPulseViewModel = viewModel()) {
    var currentNavTab by remember { mutableIntStateOf(0) }

    // Dialog States
    var showGoalSettings by remember { mutableStateOf(false) }
    var showAddFoodDialog by remember { mutableStateOf(false) }
    var targetMealType by remember { mutableStateOf("Lunch") }
    var selectedFoodForDetail by remember { mutableStateOf<FoodItem?>(null) }
    var showLogExerciseDialog by remember { mutableStateOf(false) }

    // State Collection
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val nutrition by viewModel.nutritionTotals.collectAsStateWithLifecycle()
    val totalCaloriesBurned by viewModel.totalCaloriesBurnedToday.collectAsStateWithLifecycle()
    val totalWaterMl by viewModel.totalWaterMl.collectAsStateWithLifecycle()
    val todayMealLogs by viewModel.todayMealLogs.collectAsStateWithLifecycle()
    val todaySessions by viewModel.todaySessions.collectAsStateWithLifecycle()
    val allSessions by viewModel.allSessions.collectAsStateWithLifecycle()
    val allFoodItems by viewModel.allFoodItems.collectAsStateWithLifecycle()
    val allExercises by viewModel.allExercises.collectAsStateWithLifecycle()
    val activeWorkout by viewModel.activeWorkout.collectAsStateWithLifecycle()
    val restSeconds by viewModel.restSecondsRemaining.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(modifier = Modifier.testTag("main_bottom_nav")) {
                NavigationBarItem(
                    selected = currentNavTab == 0,
                    onClick = { currentNavTab = 0 },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Dashboard") },
                    modifier = Modifier.testTag("nav_tab_dashboard")
                )
                NavigationBarItem(
                    selected = currentNavTab == 1,
                    onClick = { currentNavTab = 1 },
                    icon = { Icon(Icons.Default.LocalDining, contentDescription = "Diet") },
                    label = { Text("Diet") },
                    modifier = Modifier.testTag("nav_tab_diet")
                )
                NavigationBarItem(
                    selected = currentNavTab == 2,
                    onClick = { currentNavTab = 2 },
                    icon = { Icon(Icons.Default.FitnessCenter, contentDescription = "Gym") },
                    label = { Text("Gym") },
                    modifier = Modifier.testTag("nav_tab_gym")
                )
            }
        }
    ) { innerPadding ->
        when (currentNavTab) {
            0 -> {
                DashboardScreen(
                    profile = profile,
                    nutrition = nutrition,
                    totalCaloriesBurned = totalCaloriesBurned,
                    totalWaterMl = totalWaterMl,
                    todayMealLogs = todayMealLogs,
                    todaySessions = todaySessions,
                    activeWorkout = activeWorkout,
                    onOpenGoalSettings = { showGoalSettings = true },
                    onOpenAddFood = {
                        targetMealType = "Lunch"
                        showAddFoodDialog = true
                    },
                    onOpenLogExercise = { showLogExerciseDialog = true },
                    onStartWorkout = {
                        if (!activeWorkout.isActive) {
                            viewModel.startActiveWorkout("Gym Training Session")
                        }
                        currentNavTab = 2
                    },
                    onAddWater = { amount -> viewModel.logWater(amount) },
                    onClearWater = { viewModel.clearWater() },
                    onDeleteMealLog = { id -> viewModel.deleteMealLog(id) },
                    onNavigateToDiet = { currentNavTab = 1 },
                    onNavigateToGym = { currentNavTab = 2 },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            1 -> {
                DietScreen(
                    profile = profile,
                    nutrition = nutrition,
                    mealLogs = todayMealLogs,
                    totalWaterMl = totalWaterMl,
                    onBack = { currentNavTab = 0 },
                    onOpenAddFoodForMeal = { mealType ->
                        targetMealType = mealType
                        showAddFoodDialog = true
                    },
                    onDeleteMealLog = { id -> viewModel.deleteMealLog(id) },
                    onAddWater = { amount -> viewModel.logWater(amount) },
                    onClearWater = { viewModel.clearWater() },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            2 -> {
                GymScreen(
                    profile = profile,
                    allExercises = allExercises,
                    allWorkoutHistory = allSessions,
                    activeWorkout = activeWorkout,
                    restSeconds = restSeconds,
                    totalCaloriesBurnedToday = totalCaloriesBurned,
                    onBack = { currentNavTab = 0 },
                    onStartWorkout = { title -> viewModel.startActiveWorkout(title) },
                    onOpenLogExercise = { showLogExerciseDialog = true },
                    onRemoveActiveSet = { index -> viewModel.removeSetFromActiveWorkout(index) },
                    onFinishWorkout = { notes -> viewModel.finishActiveWorkout(notes) },
                    onCancelWorkout = { viewModel.cancelActiveWorkout() },
                    onStartRestTimer = { secs -> viewModel.startRestTimer(secs) },
                    onStopRestTimer = { viewModel.stopRestTimer() },
                    onDeleteSession = { id -> viewModel.deleteSession(id) },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }

    // Modal Dialogs
    if (showGoalSettings) {
        GoalSettingsDialog(
            currentProfile = profile,
            onDismiss = { showGoalSettings = false },
            onSave = { goal, cal, p, c, fib, fat, water, burn, weight, height ->
                viewModel.updateProfile(goal, cal, p, c, fib, fat, water, burn, weight, height)
            }
        )
    }

    if (showAddFoodDialog) {
        AddFoodSearchDialog(
            allFoods = allFoodItems,
            defaultMealType = targetMealType,
            onDismiss = { showAddFoodDialog = false },
            onSelectFood = { food, mealType ->
                targetMealType = mealType
                selectedFoodForDetail = food
                showAddFoodDialog = false
            },
            onCreateCustomFood = { customFood ->
                viewModel.addCustomFood(customFood)
            }
        )
    }

    selectedFoodForDetail?.let { food ->
        FoodDetailDialog(
            food = food,
            defaultMealType = targetMealType,
            onDismiss = { selectedFoodForDetail = null },
            onLogFood = { loggedFood, mealType, servings ->
                viewModel.logMeal(loggedFood, mealType, servings)
                selectedFoodForDetail = null
            }
        )
    }

    if (showLogExerciseDialog) {
        LogExerciseDialog(
            allExercises = allExercises,
            userWeightKg = profile.userWeightKg,
            onDismiss = { showLogExerciseDialog = false },
            onLogSet = { exercise, weightKg, reps ->
                if (activeWorkout.isActive) {
                    viewModel.addSetToActiveWorkout(exercise, weightKg, reps)
                } else {
                    // Start workout and add set
                    viewModel.startActiveWorkout("${exercise.muscleGroup} Workout")
                    viewModel.addSetToActiveWorkout(exercise, weightKg, reps)
                    currentNavTab = 2
                }
            }
        )
    }
}
