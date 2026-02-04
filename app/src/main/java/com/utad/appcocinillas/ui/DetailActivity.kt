package com.utad.appcocinillas.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.utad.appcocinillas.R
import com.utad.appcocinillas.data.local.AppDatabase
import com.utad.appcocinillas.data.remote.RetrofitClient
import com.utad.appcocinillas.databinding.ActivityDetailBinding
import com.utad.appcocinillas.model.Meal
import com.utad.appcocinillas.repository.MealRepository
import com.utad.appcocinillas.viewmodel.MealViewModel
import com.utad.appcocinillas.viewmodel.factories.MealViewModelFactory

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel: MealViewModel by viewModels {
        MealViewModelFactory(
            repository = MealRepository(
                mealsDao = AppDatabase.getDatabase(this).mealsDao(),
                apiService = RetrofitClient.apiService
            )
        )
    }
    private lateinit var selectedMeal: Meal
    private var email: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        email = intent.getStringExtra("email") ?: ""
        val idMeal = intent.getStringExtra("idMeal") ?: ""
        viewModel.getMealById(idMeal)
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.selectedMeal.observe(this) { meal ->
            if (meal != null) {
                selectedMeal = meal
                setupUi()
                viewModel.checkIfIsFavorite(selectedMeal.idMeal, email)
            }
        }
        viewModel.esFavorito.observe(this) {
            binding.btnFavorito.text = if (viewModel.esFavorito.value == true) {
                getString(R.string.btn_favorito_desmarcar)
            } else {
                getString(R.string.btn_favorito_marcar)
            }
        }
    }

    private fun setupUi() {
        Picasso.get()
            .load(selectedMeal.strMealThumb)
            .into(binding.ivPoster)
        binding.tvTitle.text = selectedMeal.strMeal
        binding.tvInstructions.text = selectedMeal.strInstructions
        binding.tvDescription.text = buildIngredientsString(selectedMeal)
        binding.btnFavorito.setOnClickListener {
            viewModel.toggleFavorite(email, selectedMeal)
        }
    }

    private fun buildIngredientsString(meal: Meal): String {
        val ingredients = StringBuilder()
        val ingredientsList = listOf(
            meal.strIngredient1 to meal.strMeasure1,
            meal.strIngredient2 to meal.strMeasure2,
            meal.strIngredient3 to meal.strMeasure3,
            meal.strIngredient4 to meal.strMeasure4,
            meal.strIngredient5 to meal.strMeasure5,
            meal.strIngredient6 to meal.strMeasure6,
            meal.strIngredient7 to meal.strMeasure7,
            meal.strIngredient8 to meal.strMeasure8,
            meal.strIngredient9 to meal.strMeasure9,
            meal.strIngredient10 to meal.strMeasure10,
            meal.strIngredient11 to meal.strMeasure11,
            meal.strIngredient12 to meal.strMeasure12,
            meal.strIngredient13 to meal.strMeasure13,
            meal.strIngredient14 to meal.strMeasure14,
            meal.strIngredient15 to meal.strMeasure15,
            meal.strIngredient16 to meal.strMeasure16,
            meal.strIngredient17 to meal.strMeasure17,
            meal.strIngredient18 to meal.strMeasure18,
            meal.strIngredient19 to meal.strMeasure19,
            meal.strIngredient20 to meal.strMeasure20
        )

        ingredientsList.forEach { (ingredient, measure) ->
            val ingredientStr = ingredient?.trim()
            val measureStr = measure?.trim()

            if (ingredientStr?.isNotEmpty() == true) {
                if (measureStr?.isNotEmpty() == true) {
                    ingredients.append("• $measureStr $ingredientStr\n")
                } else {
                    ingredients.append("• $ingredientStr\n")
                }
            }
        }
        return ingredients.toString().trimEnd()
    }

    private fun showSnackBarMessage(message: String) {
        Snackbar
            .make(binding.root, message, Snackbar.LENGTH_SHORT)
            .show()
    }

}