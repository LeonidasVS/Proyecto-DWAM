package com.example.shopnova.UI

import android.content.Intent
import android.os.Bundle

import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.shopnova.R
import com.example.shopnova.Utils.UiState
import com.example.shopnova.Utils.ValidationUtils
import com.example.shopnova.Utils.gone
import com.example.shopnova.Utils.showSnackbarError
import com.example.shopnova.Utils.visible
import com.example.shopnova.Viewmodel.AuthViewModel
import com.example.shopnova.databinding.ActivityRegisterBinding
import es.dmoral.toasty.Toasty

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityRegisterBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupObservers()

        binding.btnRegister.setOnClickListener {
            // Solo procesa si los campos son validos
            if (validarCampos()) {
                procesarRegistro()
            }
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }
    }

    // Procesa el registro segun el rol
    private fun procesarRegistro(){
            val nombre   = binding.etName.text.toString().trim()
            val email    = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()

            // Siempre será cliente automáticamente
            viewModel.register(nombre, email, password)
    }


    private fun setupObservers() {
        viewModel.registerState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visible()
                    binding.btnRegister.isEnabled = false
                }
                is UiState.Success -> {
                    binding.progressBar.gone()
                    binding.btnRegister.isEnabled = true
                    Toasty.success(
                        this,
                        getString(R.string.success_register),
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                    viewModel.resetRegisterState()
                    irDashboard()
                }
                is UiState.Error -> {
                    binding.progressBar.gone()
                    binding.btnRegister.isEnabled = true
                    binding.root.showSnackbarError(state.message)
                    viewModel.resetRegisterState()
                }
                else -> {
                    binding.progressBar.gone()
                    binding.btnRegister.isEnabled = true
                }
            }
        }
    }

    private fun validarCampos(): Boolean {
        var isValid = true
        val name            = binding.etName.text.toString().trim()
        val email           = binding.etEmail.text.toString().trim()
        val password        = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        // Limpiar errores previos
        binding.tilName.error            = null
        binding.tilEmail.error           = null
        binding.tilPassword.error        = null
        binding.tilConfirmPassword.error = null

        if (!ValidationUtils.isNotEmpty(name)) {
            binding.tilName.error = getString(R.string.field_required)
            isValid = false
        }
        if(!ValidationUtils.isValidName(name)){
            binding.tilName.error = getString(R.string.field_name_caracter)
            isValid = false
        }
        if (!ValidationUtils.isNotEmpty(email)) {
            binding.tilEmail.error = getString(R.string.field_required)
            isValid = false
        } else if (!ValidationUtils.isValidEmail(email)) {
            binding.tilEmail.error = getString(R.string.invalid_email)
            isValid = false
        }
        if (!ValidationUtils.isValidPassword(password)) {
            binding.tilPassword.error = getString(R.string.password_short)
            isValid = false
        }
        if (password != confirmPassword) {
            binding.tilConfirmPassword.error = getString(R.string.passwords_not_match)
            isValid = false
        }

        return isValid
    }

    private fun irDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}