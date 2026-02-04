package com.utad.appcocinillas.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.utad.appcocinillas.model.Meal
import com.utad.appcocinillas.model.User
import com.utad.appcocinillas.repository.MealRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class MealUiState(
    val meals: List<Meal> = emptyList(),
    val message: String = "",
)
class MealViewModel(
    val repository: MealRepository
): ViewModel() {
    private val _state = MutableLiveData<MealUiState>()
    private val _user = MutableLiveData<User?>()
    private val _selectedMeal = MutableLiveData<Meal?>()
    private val _esFavorito = MutableLiveData<Boolean>(false)
    val state: LiveData<MealUiState> get() = _state
    val user: LiveData<User?> get() = _user
    val selectedMeal: LiveData<Meal?> get() = _selectedMeal
    val esFavorito: LiveData<Boolean> get() = _esFavorito

    fun getMealsByCategory(category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val meals = repository.getMealsByCategory(category)
                setMealUiState(meals = meals)
            } catch (e: Exception) {
                setMealUiState(message = e.message ?: "Error desconocido")
                Log.i("Excepción", e.message ?: "Error desconocido")
            }
        }
    }

    fun registerUser(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.registrarUsuario(User(email, password))
        }
    }

    fun autenticarUsuario(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val usuario = repository.autenticarUsuario(email, password)
            _user.postValue(usuario)
        }
    }

    fun getUserFavMeals(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val meals = repository.obtenerRecetasFavoritas(email)
                setMealUiState(meals = meals)
                Log.i("Email", email)
            } catch (e: Exception) {
                setMealUiState(message = e.message ?: "Error desconocido")
            }
        }
    }

    fun getMealById(idMeal: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val meal = repository.getMealById(idMeal)
                _selectedMeal.postValue(meal)
            } catch (e: Exception) {
                setMealUiState(message = e.message ?: "Error desconocido")
            }
        }
    }

    fun checkIfIsFavorite(idMeal: String, email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val isFavorite = repository.esFavorito(idMeal, email)
            _esFavorito.postValue(isFavorite)
        }
    }

    fun toggleFavorite(email: String, meal: Meal) {
        viewModelScope.launch(Dispatchers.IO) {
            if (repository.esFavorito(meal.idMeal, email)) {
                repository.desmarcarRecetaFavorita(email, meal)
                _esFavorito.postValue(false)
            } else {
                repository.marcarRecetaFavorita(email, meal)
                _esFavorito.postValue(true)
            }
        }
    }

    private fun setMealUiState(meals: List<Meal> = emptyList(), message: String = "") {
        _state.postValue(MealUiState(meals, message))
    }
}