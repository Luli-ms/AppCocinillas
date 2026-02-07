package com.utad.appcocinillas.repository

import com.utad.appcocinillas.data.local.MealsDAO
import com.utad.appcocinillas.data.remote.MealApiService
import com.utad.appcocinillas.model.FavoritosUsuario
import com.utad.appcocinillas.model.Meal
import com.utad.appcocinillas.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MealRepository(
    private val mealsDao: MealsDAO,
    private val apiService: MealApiService
) {
    suspend fun registrarUsuario(user: User) {
        withContext(Dispatchers.IO) {
            mealsDao.insertUser(user)
        }
    }

    suspend fun autenticarUsuario(email: String, password: String): User? {
        return withContext(Dispatchers.IO) {
            mealsDao.getUserByEmailAndPassword(email, password)
        }
    }

    suspend fun getMealsByCategory(category: String): List<Meal> {
        return withContext(Dispatchers.IO) {
            apiService.getMealsByCategory(category)?.meals ?: emptyList()
        }
    }

    suspend fun marcarRecetaFavorita(email: String, meal: Meal) {
        withContext(Dispatchers.IO) {
            mealsDao.insertFavoriteMeal(meal)
            mealsDao.insertFavoritosUsuario(FavoritosUsuario(email, meal.idMeal))
        }
    }

    suspend fun desmarcarRecetaFavorita(email: String, meal: Meal) {
        withContext(Dispatchers.IO) {
            mealsDao.deleteFavoriteMealFromUser(email, meal.idMeal)
        }
    }

    suspend fun esFavorito(idMeal: String, email: String): Boolean {
        return withContext(Dispatchers.IO) {
            mealsDao.getMealByIdUsuario(email, idMeal) != null
        }
    }

    suspend fun obtenerRecetasFavoritas(email: String): List<Meal> {
        return withContext(Dispatchers.IO) {
            mealsDao.getFavoriteMealsByUser(email)
        }
    }

    suspend fun getMealById(idMeal: String): Meal? {
        return withContext(Dispatchers.IO) {
            apiService.getMealById(idMeal)?.meals?.firstOrNull()
        }
    }
}