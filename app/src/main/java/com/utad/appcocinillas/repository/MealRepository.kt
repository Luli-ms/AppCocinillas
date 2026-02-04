package com.utad.appcocinillas.repository

import com.utad.appcocinillas.data.local.MealsDAO
import com.utad.appcocinillas.data.remote.MealApiService
import com.utad.appcocinillas.model.FavoritosUsuario
import com.utad.appcocinillas.model.Meal
import com.utad.appcocinillas.model.User

class MealRepository(
    private val mealsDao: MealsDAO,
    private val apiService: MealApiService
) {
    suspend fun registrarUsuario(user: User) {
        mealsDao.insertUser(user)
    }

    suspend fun autenticarUsuario(email: String, password: String): User? {
        return mealsDao.getUserByEmailAndPassword(email, password)
    }

    suspend fun getMealsByCategory(category: String): List<Meal> {
        val mealsResponse = apiService.getMealsByCategory(category)
        val meals = mealsResponse?.meals ?: emptyList()
        return meals
    }

    suspend fun marcarRecetaFavorita(email: String, meal: Meal) {
        mealsDao.insertFavoriteMeal(meal)
        mealsDao.insertFavoritosUsuario(FavoritosUsuario(email, meal.idMeal))
    }

    suspend fun desmarcarRecetaFavorita(email: String, meal: Meal) {
        mealsDao.deleteFavoriteMealFromUser(email, meal.idMeal)
    }

    suspend fun esFavorito(idMeal: String, email: String): Boolean {
        return mealsDao.getMealByIdUsuario(email, idMeal) != null
    }

    suspend fun obtenerRecetasFavoritas(email: String): List<Meal> {
        return mealsDao.getFavoriteMealsByUser(email)
    }

    suspend fun getMealById(idMeal: String): Meal? {
        val mealsResponse = apiService.getMealById(idMeal)
        val meals = mealsResponse?.meals ?: emptyList()
        if (meals.isEmpty()) {
            return null
        }
        return meals.first()
    }
}