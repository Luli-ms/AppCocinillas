package com.utad.appcocinillas.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.utad.appcocinillas.R
import com.utad.appcocinillas.data.local.AppDatabase
import com.utad.appcocinillas.data.remote.RetrofitClient
import com.utad.appcocinillas.databinding.ActivityMealsBinding
import com.utad.appcocinillas.model.Meal
import com.utad.appcocinillas.repository.MealRepository
import com.utad.appcocinillas.ui.adapters.MealAdapter
import com.utad.appcocinillas.viewmodel.MealViewModel
import com.utad.appcocinillas.viewmodel.factories.MealViewModelFactory
import kotlin.getValue

class MealsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMealsBinding
    private val viewModel: MealViewModel by viewModels {
        MealViewModelFactory(
            repository = MealRepository(
                mealsDao = AppDatabase.getDatabase(this).mealsDao(),
                apiService = RetrofitClient.apiService
            )
        )
    }
    private val mealAdapter = MealAdapter { navigateToDetail(idMeal = it)}
    private var selectedCategory: String = "Seafood"
    private var email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMealsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        email = intent.getStringExtra("email") ?: ""
        setupUi()
        setupObservers()
    }

    override fun onRestart() {
        super.onRestart()
        if (selectedCategory == "Favoritos") {
            viewModel.getUserFavMeals(email)
        } else {
            viewModel.getMealsByCategory(selectedCategory)
        }
    }

    private fun setupUi() {
        viewModel.getMealsByCategory(selectedCategory)
        binding.rvMeals.adapter = mealAdapter
        binding.rvMeals.layoutManager = LinearLayoutManager(this)
    }

    private fun setupObservers() {
        viewModel.state.observe(this) { state ->
            handleEmptyView(state.meals)
            mealAdapter.submitList(state.meals)
            if (state.message.isNotEmpty()) showSnackBarMessage(state.message)
            this.title = selectedCategory
        }
    }

    private fun handleEmptyView(meals: List<Meal>) {
        if (meals.isEmpty()) {
            binding.rvMeals.visibility = View.GONE
            binding.tvEmpty.visibility = View.VISIBLE
        } else {
            binding.rvMeals.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE
        }
    }

    private fun navigateToDetail(idMeal: String) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("idMeal", idMeal)
        intent.putExtra("email", email)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.seafood -> {
                selectedCategory = "Seafood"
            }
            R.id.chicken -> {
                selectedCategory = "Chicken"
            }
            R.id.pasta -> {
                selectedCategory = "Pasta"
            }
            R.id.vegetarian -> {
                selectedCategory = "Vegetarian"
            }
            R.id.miscellaneous -> {
                selectedCategory = "Miscellaneous"
            }
            R.id.favoritos -> {
                selectedCategory = "Favoritos"
            }
            else -> return false
        }
        if (selectedCategory == "Favoritos") {
            val email = intent.getStringExtra("email")
            if (email != null) {
                viewModel.getUserFavMeals(email)
            } else {
                showSnackBarMessage("No hay usuario")
            }
        } else {
            viewModel.getMealsByCategory(selectedCategory)
        }
        return true
    }

    private fun showSnackBarMessage(message: String) {
        Snackbar
            .make(binding.root, message, Snackbar.LENGTH_SHORT)
            .show()
    }
}