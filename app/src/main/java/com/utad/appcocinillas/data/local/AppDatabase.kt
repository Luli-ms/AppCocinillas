package com.utad.appcocinillas.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.utad.appcocinillas.model.FavoritosUsuario
import com.utad.appcocinillas.model.Meal
import com.utad.appcocinillas.model.User

@Database(
    entities = [Meal::class, User::class, FavoritosUsuario::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun mealsDao(): MealsDAO

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) return tempInstance

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fav_meals_db"
                ).build()

                INSTANCE = instance
                return instance
            }
        }
    }
}
