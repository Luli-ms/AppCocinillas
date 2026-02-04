package com.utad.appcocinillas.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.utad.appcocinillas.model.FavoritosUsuario
import com.utad.appcocinillas.model.Meal
import com.utad.appcocinillas.model.User

@Dao
interface MealsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM usuarios WHERE email = :email AND password = :password")
    suspend fun getUserByEmailAndPassword(email: String, password: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteMeal(favoriteMeal: Meal)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoritosUsuario(favoritosUsuario: FavoritosUsuario)

    @Query("""
        SELECT *
        FROM meals_favoritos
        INNER JOIN favoritos_usuario 
        ON meals_favoritos.idMeal = favoritos_usuario.idMeal
        WHERE favoritos_usuario.email = :email
    """)
    suspend fun getFavoriteMealsByUser(email: String): List<Meal>

    @Query("DELETE FROM favoritos_usuario WHERE email = :email AND idMeal = :idMeal")
    suspend fun deleteFavoriteMealFromUser(email: String, idMeal: String)

    @Query("""
        SELECT *
        FROM favoritos_usuario
        WHERE favoritos_usuario.email = :email AND favoritos_usuario.idMeal = :idMeal
        LIMIT 1
    """)
    suspend fun getMealByIdUsuario(email: String, idMeal: String): FavoritosUsuario?
}
