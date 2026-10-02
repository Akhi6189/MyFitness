package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Exercise
import com.example.data.model.ExerciseSetLog
import com.example.data.model.FoodItem
import com.example.data.model.MealLog
import com.example.data.model.UserProfile
import com.example.data.model.WaterLog
import com.example.data.model.WorkoutSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfile::class,
        FoodItem::class,
        MealLog::class,
        WaterLog::class,
        Exercise::class,
        WorkoutSession::class,
        ExerciseSetLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun fitPulseDao(): FitPulseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fitpulse_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.fitPulseDao())
                    }
                }
            }

            suspend fun populateInitialData(dao: FitPulseDao) {
                // Initialize default user profile
                dao.insertOrUpdateProfile(UserProfile())

                // Prepopulate foods
                dao.insertAllFoodItems(DefaultData.FOOD_ITEMS)

                // Prepopulate exercises
                dao.insertAllExercises(DefaultData.EXERCISES)
            }
        }
    }
}
