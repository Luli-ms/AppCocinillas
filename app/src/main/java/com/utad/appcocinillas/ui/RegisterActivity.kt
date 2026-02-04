package com.utad.appcocinillas.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.utad.appcocinillas.data.local.AppDatabase
import com.utad.appcocinillas.data.remote.RetrofitClient
import com.utad.appcocinillas.databinding.ActivityRegisterBinding
import com.utad.appcocinillas.repository.MealRepository
import com.utad.appcocinillas.viewmodel.MealViewModel
import com.utad.appcocinillas.viewmodel.factories.MealViewModelFactory
import kotlin.getValue

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
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
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupUi()
    }

    private fun setupUi() {
        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirm = binding.etConfirm.text.toString()
            if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                showSnackBarMessage("Por favor, rellene todos los campos")
            } else {
                if (password != confirm) {
                    showSnackBarMessage("Las contraseñas no coinciden")
                } else {
                    viewModel.registerUser(email, password)
                    binding.etEmail.text.clear()
                    binding.etPassword.text.clear()
                    binding.etConfirm.text.clear()
                    finish()
                }
            }
        }
    }

    fun showSnackBarMessage(message: String) {
        Snackbar
            .make(binding.root, message, Snackbar.LENGTH_SHORT)
            .show()
    }
}