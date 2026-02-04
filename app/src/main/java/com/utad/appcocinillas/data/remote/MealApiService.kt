package com.utad.appcocinillas.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface MealApiService {
    @GET("filter.php?")
    suspend fun getMealsByCategory(@Query("c") category: String): MealsResponse?

    @GET("lookup.php?")
    suspend fun getMealById(@Query("i") id: String): MealsResponse?
}