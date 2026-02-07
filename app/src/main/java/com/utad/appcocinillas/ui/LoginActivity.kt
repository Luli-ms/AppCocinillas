package com.utad.appcocinillas.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.utad.appcocinillas.data.local.AppDatabase
import com.utad.appcocinillas.data.remote.RetrofitClient
import com.utad.appcocinillas.databinding.ActivityLoginBinding
import com.utad.appcocinillas.repository.MealRepository
import com.utad.appcocinillas.viewmodel.MealViewModel
import com.utad.appcocinillas.viewmodel.factories.MealViewModelFactory
import kotlin.getValue

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: MealViewModel by viewModels {
        MealViewModelFactory(
            repository = MealRepository(
                mealsDao = AppDatabase.getDatabase(this).mealsDao(),
                apiService = RetrofitClient.apiService
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupUi()
        setupObservers()
    }

    private fun setupUi() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                showSnackBarMessage("Por favor, rellene todos los campos")
            } else {
                viewModel.autenticarUsuario(email, password)
            }
        }
        binding.tvRegister.setOnClickListener {
            navigateToRegisterActivity()
        }
    }

    private fun setupObservers() {
        viewModel.user.observe(this) { user ->
            if (user != null) {
                binding.etEmail.text.clear()
                binding.etPassword.text.clear()
                navigateToMealsActivity(user.email)
            } else {
                showSnackBarMessage("Credenciales incorrectas")
            }
        }
    }

    private fun navigateToRegisterActivity() {
        binding.etEmail.text.clear()
        binding.etPassword.text.clear()
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToMealsActivity(email: String) {
        val intent = Intent(this, MealsActivity::class.java)
        intent.putExtra("email", email)
        startActivity(intent)
    }

    private fun showSnackBarMessage(message: String) {
        Snackbar
            .make(binding.root, message, Snackbar.LENGTH_SHORT)
            .show()
    }
}