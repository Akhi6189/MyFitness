package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.CarbsColor
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.FatsColor
import com.example.ui.theme.FiberColor
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.ProteinColor
import com.example.ui.viewmodel.NutritionTotals

@Composable
fun DietWidget(
    profile: UserProfile,
    nutrition: NutritionTotals,
    onOpenGoalSettings: () -> Unit,
    onOpenAddFood: () -> Unit,
    onNavigateToDietDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    val remainingCalories = (profile.targetCalories - nutrition.calories).coerceAtLeast(0)

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("diet_widget")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Widget Title & Goal Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalDining,
                            contentDescription = "Diet Tracker",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Diet & Nutrition",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        )
                        // Clickable Goal Pill
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onOpenGoalSettings() }
                                .padding(vertical = 2.dp)
                                .testTag("change_goal_pill")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = profile.goal.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Goal",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(10.dp)
                                    .padding(start = 2.dp)
                            )
                        }
                    }
                }

                // Navigate to deep breakdown
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onNavigateToDietDetails() }
                        .padding(6.dp)
                        .testTag("view_diet_details_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "View Details",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Main Circular Progress Bar + Energy Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Circular Progress Bar with remaining needed
                CircularProgressWidget(
                    currentValue = nutrition.calories,
                    targetValue = profile.targetCalories,
                    unit = "kcal",
                    subtitle = "remaining",
                    progressColors = listOf(EmeraldPrimary, FlameOrange, EmeraldDark),
                    sizeDp = 135.dp,
                    strokeWidth = 12.dp,
                    testTag = "diet_circular_progress"
                )

                // Intake summary column
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = "Consumed",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${nutrition.calories} kcal",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Daily Target",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${profile.targetCalories} kcal",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (remainingCalories > 0) "$remainingCalories kcal remaining needed" else "Daily calorie target met!",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = if (remainingCalories > 0) MaterialTheme.colorScheme.onSurfaceVariant else EmeraldPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(14.dp))

            // Macronutrient Progress Bars (Proteins, Carbs, Fiber, Fats)
            Text(
                text = "Macro Targets & Progress:",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            MacroBar(
                name = "Protein",
                currentG = nutrition.proteinG,
                targetG = profile.targetProteinG,
                color = ProteinColor,
                testTag = "macro_bar_protein"
            )

            MacroBar(
                name = "Carbs",
                currentG = nutrition.carbsG,
                targetG = profile.targetCarbsG,
                color = CarbsColor,
                testTag = "macro_bar_carbs"
            )

            MacroBar(
                name = "Fiber",
                currentG = nutrition.fiberG,
                targetG = profile.targetFiberG,
                color = FiberColor,
                testTag = "macro_bar_fiber"
            )

            MacroBar(
                name = "Fats",
                currentG = nutrition.fatsG,
                targetG = profile.targetFatsG,
                color = FatsColor,
                testTag = "macro_bar_fats"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Add Food Action Button
            Button(
                onClick = onOpenAddFood,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("widget_add_food_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Log Food Intake", fontWeight = FontWeight.Bold)
            }
        }
    }
}
