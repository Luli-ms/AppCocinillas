package com.utad.appcocinillas.model

import androidx.room.Entity

@Entity(
    tableName = "favoritos_usuario",
    primaryKeys = ["email", "idMeal"]
)
data class FavoritosUsuario(
    val email: String,
    val idMeal: String
)
