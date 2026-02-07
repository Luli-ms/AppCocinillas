package com.utad.appcocinillas.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utad.appcocinillas.model.Meal
import com.utad.appcocinillas.model.User
import com.utad.appcocinillas.repository.MealRepository
import kotlinx.coroutines.launch

data class MealUiState(
    val meals: List<Meal> = emptyList(),
    val message: String = "",
    val isLoading: Boolean = false
)
class MealViewModel(
    val repository: MealRepository
): ViewModel() {
    private val _state = MutableLiveData<MealUiState>()
    private val _user = MutableLiveData<User?>()
    private val _selectedMeal = MutableLiveData<Meal?>()
    private val _esFavorito = MutableLiveData(false)
    val state: LiveData<MealUiState> get() = _state
    val user: LiveData<User?> get() = _user
    val selectedMeal: LiveData<Meal?> get() = _selectedMeal
    val esFavorito: LiveData<Boolean> get() = _esFavorito

    fun getMealsByCategory(category: String) {
        viewModelScope.launch {
            try {
                setMealUiState(isLoading = true)
                val meals = repository.getMealsByCategory(category)
                setMealUiState(meals = meals, isLoading = false)
            } catch (e: Exception) {
                setMealUiState(
                    message = e.message ?: "Ha ocurrido un error desconocido",
                    isLoading = false
                )
            }
        }
    }

    fun registerUser(email: String, password: String) {
        viewModelScope.launch {
            repository.registrarUsuario(User(email, password))
        }
    }

    fun autenticarUsuario(email: String, password: String) {
        viewModelScope.launch {
            val usuario = repository.autenticarUsuario(email, password)
            _user.postValue(usuario)
        }
    }

    fun getUserFavMeals(email: String) {
        viewModelScope.launch {
            try {
                setMealUiState(isLoading = true)
                val meals = repository.obtenerRecetasFavoritas(email)
                setMealUiState(meals = meals, isLoading = false)
                Log.i("Email", email)
            } catch (e: Exception) {
                setMealUiState(
                    message = e.message ?: "Ha ocurrido un error desconocido",
                    isLoading = true)
            }
        }
    }

    fun getMealById(idMeal: String) {
        viewModelScope.launch {
            try {
                setMealUiState(isLoading = true)
                val meal = repository.getMealById(idMeal)
                _selectedMeal.postValue(meal)
                setMealUiState(isLoading = false)
            } catch (e: Exception) {
                setMealUiState(
                    message = e.message ?: "Error desconocido",
                    isLoading = false
                )
            }
        }
    }

    fun checkIfIsFavorite(idMeal: String, email: String) {
        viewModelScope.launch {
            val isFavorite = repository.esFavorito(idMeal, email)
            _esFavorito.postValue(isFavorite)
        }
    }

    fun toggleFavorite(email: String, meal: Meal) {
        viewModelScope.launch {
            if (repository.esFavorito(meal.idMeal, email)) {
                repository.desmarcarRecetaFavorita(email, meal)
                _esFavorito.postValue(false)
                setMealUiState(message = "${meal.strMeal} se ha eliminado de favoritos")
            } else {
                repository.marcarRecetaFavorita(email, meal)
                _esFavorito.postValue(true)
                setMealUiState(message = "${meal.strMeal} se ha añadido a favoritos")
            }
        }
    }

    private fun setMealUiState(meals: List<Meal> = emptyList(), message: String = "", isLoading: Boolean = false) {
        _state.postValue(MealUiState(meals, message, isLoading))
    }
}