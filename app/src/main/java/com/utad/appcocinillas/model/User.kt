package com.utad.appcocinillas.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class User(
    @PrimaryKey
    val email: String,
    val password: String
)
