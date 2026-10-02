package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MealLog
import com.example.data.model.UserProfile
import com.example.ui.components.CircularProgressWidget
import com.example.ui.components.MacroBar
import com.example.ui.components.MicroNutrientRow
import com.example.ui.components.WaterTrackerSection
import com.example.ui.theme.CarbsColor
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.FatsColor
import com.example.ui.theme.FiberColor
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.ProteinColor
import com.example.ui.viewmodel.NutritionTotals

@Composable
fun DietScreen(
    profile: UserProfile,
    nutrition: NutritionTotals,
    mealLogs: List<MealLog>,
    totalWaterMl: Int,
    onBack: () -> Unit,
    onOpenAddFoodForMeal: (mealType: String) -> Unit,
    onDeleteMealLog: (Long) -> Unit,
    onAddWater: (Int) -> Unit,
    onClearWater: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Meals & Macros", "Vitamins & Minerals", "Water Tracker")

    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snacks")

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("diet_screen")
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("diet_back_btn")
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Diet & Nutrition Hub",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        // Sub-tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontSize = 12.sp, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("diet_subtab_$index")
                )
            }
        }

        when (selectedTab) {
            0 -> {
                // Tab 0: Meals & Macros
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Summary Card
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    CircularProgressWidget(
                                        currentValue = nutrition.calories,
                                        targetValue = profile.targetCalories,
                                        unit = "kcal",
                                        subtitle = "left",
                                        progressColors = listOf(EmeraldPrimary, FlameOrange, EmeraldDark),
                                        sizeDp = 110.dp,
                                        strokeWidth = 10.dp,
                                        testTag = "diet_screen_circular_progress"
                                    )

                                    Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                                        Text(
                                            text = "Today's Energy",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "${nutrition.calories} / ${profile.targetCalories} kcal",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Goal: ${profile.goal.title}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                Spacer(modifier = Modifier.height(10.dp))

                                MacroBar(name = "Protein", currentG = nutrition.proteinG, targetG = profile.targetProteinG, color = ProteinColor)
                                MacroBar(name = "Carbs", currentG = nutrition.carbsG, targetG = profile.targetCarbsG, color = CarbsColor)
                                MacroBar(name = "Fiber", currentG = nutrition.fiberG, targetG = profile.targetFiberG, color = FiberColor)
                                MacroBar(name = "Fats", currentG = nutrition.fatsG, targetG = profile.targetFatsG, color = FatsColor)
                            }
                        }
                    }

                    // Categorized Meals: Breakfast, Lunch, Dinner, Snacks
                    items(mealTypes) { mealType ->
                        val logsForMeal = mealLogs.filter { it.mealType.equals(mealType, ignoreCase = true) }
                        val mealCalories = logsForMeal.sumOf { it.calories }

                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("meal_section_${mealType.lowercase()}")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = mealType,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "$mealCalories kcal total",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    IconButton(
                                        onClick = { onOpenAddFoodForMeal(mealType) },
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                            .testTag("add_food_btn_${mealType.lowercase()}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Add Food to $mealType",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                if (logsForMeal.isEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "No items logged yet for $mealType",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    logsForMeal.forEach { log ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = log.foodName,
                                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                                                )
                                                Text(
                                                    text = "${log.servings}x portion • ${log.calories} kcal (P: ${log.proteinG.toInt()}g, C: ${log.carbsG.toInt()}g, F: ${log.fatsG.toInt()}g)",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            IconButton(
                                                onClick = { onDeleteMealLog(log.id) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Delete",
                                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(30.dp)) }
                }
            }
            1 -> {
                // Tab 1: Detailed Vitamins & Minerals Breakdown
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "Daily Micronutrient Totals",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Calculated across all logged meals in the database",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("vitamins_card")
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Vitamins",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                MicroNutrientRow("Vitamin A (Retinol)", "${String.format("%.1f", nutrition.vitAMcg)} mcg (RDA: 900 mcg)")
                                MicroNutrientRow("Vitamin C (Ascorbic Acid)", "${String.format("%.1f", nutrition.vitCMg)} mg (RDA: 90 mg)")
                                MicroNutrientRow("Vitamin D", "${String.format("%.1f", nutrition.vitDIu)} IU (RDA: 600-800 IU)")
                                MicroNutrientRow("Vitamin B12 (Cobalamin)", "${String.format("%.2f", nutrition.vitB12Mcg)} mcg (RDA: 2.4 mcg)")
                            }
                        }
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("minerals_card")
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Essential Minerals & Electrolytes",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                MicroNutrientRow("Iron (Fe)", "${String.format("%.1f", nutrition.ironMg)} mg (RDA: 8-18 mg)")
                                MicroNutrientRow("Calcium (Ca)", "${String.format("%.1f", nutrition.calciumMg)} mg (RDA: 1000 mg)")
                                MicroNutrientRow("Potassium (K)", "${String.format("%.1f", nutrition.potassiumMg)} mg (RDA: 3400 mg)")
                                MicroNutrientRow("Magnesium (Mg)", "${String.format("%.1f", nutrition.magnesiumMg)} mg (RDA: 400 mg)")
                                MicroNutrientRow("Zinc (Zn)", "${String.format("%.1f", nutrition.zincMg)} mg (RDA: 11 mg)")
                                MicroNutrientRow("Sodium (Na)", "${String.format("%.1f", nutrition.sodiumMg)} mg (Max: 2300 mg)")
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(30.dp)) }
                }
            }
            2 -> {
                // Tab 2: Water Intake Tab
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        WaterTrackerSection(
                            currentWaterMl = totalWaterMl,
                            targetWaterMl = profile.targetWaterMl,
                            onAddWater = onAddWater,
                            onClearWater = onClearWater
                        )
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Hydration & Muscle Performance",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Maintaining adequate water intake is critical for gym performance, protein synthesis, creatine uptake, and nutrient transport to recovering muscle fibers.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(30.dp)) }
                }
            }
        }
    }
}
