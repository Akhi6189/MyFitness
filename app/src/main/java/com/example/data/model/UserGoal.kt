package com.example.data.model

enum class UserGoal(
    val title: String,
    val subtitle: String,
    val description: String,
    val defaultCalories: Int,
    val defaultProteinG: Int,
    val defaultCarbsG: Int,
    val defaultFiberG: Int,
    val defaultFatsG: Int,
    val defaultActiveBurnTarget: Int
) {
    WEIGHT_LOSS(
        title = "Weight Reduction",
        subtitle = "Calorie Deficit & Fat Burn",
        description = "Optimized for shedding fat while preserving lean muscle mass with high protein and fiber.",
        defaultCalories = 1850,
        defaultProteinG = 160,
        defaultCarbsG = 160,
        defaultFiberG = 35,
        defaultFatsG = 50,
        defaultActiveBurnTarget = 500
    ),
    GAIN_MUSCLE(
        title = "Gain Muscle",
        subtitle = "Caloric Surplus & Hypertrophy",
        description = "Provides surplus energy and ample carbohydrates to fuel intense gym sessions and pack on muscle.",
        defaultCalories = 2800,
        defaultProteinG = 190,
        defaultCarbsG = 340,
        defaultFiberG = 30,
        defaultFatsG = 75,
        defaultActiveBurnTarget = 400
    ),
    VISIBLE_MUSCLES(
        title = "Visible Muscles",
        subtitle = "Lean Cut & Definition",
        description = "High protein and controlled carbs to strip stubborn body fat and reveal sharp muscle striations.",
        defaultCalories = 2150,
        defaultProteinG = 200,
        defaultCarbsG = 180,
        defaultFiberG = 38,
        defaultFatsG = 55,
        defaultActiveBurnTarget = 600
    ),
    MAINTENANCE(
        title = "Maintenance",
        subtitle = "Balance & Vitality",
        description = "Balanced macronutrient distribution for peak daily stamina, health, and weight stability.",
        defaultCalories = 2300,
        defaultProteinG = 150,
        defaultCarbsG = 250,
        defaultFiberG = 32,
        defaultFatsG = 65,
        defaultActiveBurnTarget = 450
    );

    companion object {
        fun fromName(name: String?): UserGoal {
            return entries.firstOrNull { it.name == name } ?: VISIBLE_MUSCLES
        }
    }
}
