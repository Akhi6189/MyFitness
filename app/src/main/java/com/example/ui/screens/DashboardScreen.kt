package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MealLog
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutSession
import com.example.ui.components.DietWidget
import com.example.ui.components.GymWidget
import com.example.ui.components.WaterTrackerSection
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.WaterColor
import com.example.ui.viewmodel.ActiveWorkoutState
import com.example.ui.viewmodel.NutritionTotals

@Composable
fun DashboardScreen(
    profile: UserProfile,
    nutrition: NutritionTotals,
    totalCaloriesBurned: Int,
    totalWaterMl: Int,
    todayMealLogs: List<MealLog>,
    todaySessions: List<WorkoutSession>,
    activeWorkout: ActiveWorkoutState,
    onOpenGoalSettings: () -> Unit,
    onOpenAddFood: () -> Unit,
    onOpenLogExercise: () -> Unit,
    onStartWorkout: () -> Unit,
    onAddWater: (Int) -> Unit,
    onClearWater: () -> Unit,
    onDeleteMealLog: (Long) -> Unit,
    onNavigateToDiet: () -> Unit,
    onNavigateToGym: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Daily Overview",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp
                        )
                    )
                    Text(
                        text = "Goal: ${profile.goal.title}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                // Goal settings badge button
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onOpenGoalSettings() }
                        .testTag("dashboard_change_goal_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = "Goals",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Goals",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }

        // Active Workout Banner (if active session running)
        if (activeWorkout.isActive) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = FlameOrange.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToGym() }
                        .testTag("active_workout_banner")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(FlameOrange),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = activeWorkout.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "${activeWorkout.elapsedSeconds / 60}m ${activeWorkout.elapsedSeconds % 60}s elapsed • ${activeWorkout.sets.size} sets logged",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = "Resume",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = FlameOrange
                            )
                        )
                    }
                }
            }
        }

        // WIDGET 1: Diet Tracking Widget (Daily Intake Target, Remaining Calories, Circular Progress Bar)
        item {
            DietWidget(
                profile = profile,
                nutrition = nutrition,
                onOpenGoalSettings = onOpenGoalSettings,
                onOpenAddFood = onOpenAddFood,
                onNavigateToDietDetails = onNavigateToDiet
            )
        }

        // WIDGET 2: Gym Training Tracking Widget (Calories Burned, Sets, Reps, Circular Progress Bar)
        item {
            GymWidget(
                profile = profile,
                todaySessions = todaySessions,
                totalCaloriesBurned = totalCaloriesBurned,
                activeWorkout = activeWorkout,
                onStartWorkout = onStartWorkout,
                onOpenLogExercise = onOpenLogExercise,
                onNavigateToGymDetails = onNavigateToGym
            )
        }

        // TAB / SECTION: Water Intake Tracker
        item {
            WaterTrackerSection(
                currentWaterMl = totalWaterMl,
                targetWaterMl = profile.targetWaterMl,
                onAddWater = onAddWater,
                onClearWater = onClearWater
            )
        }

        // Today's Meal Activity List
        if (todayMealLogs.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Meals Logged",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${todayMealLogs.size} items",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(todayMealLogs, key = { it.id }) { log ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = log.foodName,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${log.mealType} • ${log.servings}x serving • ${log.calories} kcal",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "P: ${log.proteinG.toInt()}g | C: ${log.carbsG.toInt()}g | F: ${log.fatsG.toInt()}g | Fib: ${log.fiberG.toInt()}g",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(
                            onClick = { onDeleteMealLog(log.id) },
                            modifier = Modifier.testTag("delete_meal_log_${log.id}")
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
