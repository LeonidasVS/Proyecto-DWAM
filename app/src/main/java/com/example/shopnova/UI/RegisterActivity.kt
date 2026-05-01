package com.example.shopnova.UI

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.shopnova.R
import com.example.shopnova.Utils.RolUtils
import com.example.shopnova.Utils.UiState
import com.example.shopnova.Utils.ValidationUtils
import com.example.shopnova.Utils.gone
import com.example.shopnova.Utils.showSnackbarError
import com.example.shopnova.Utils.showToast
import com.example.shopnova.Utils.visible
import com.example.shopnova.Viewmodel.AuthViewModel
import com.example.shopnova.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels()

    private val roles = listOf(
        "Cliente",
        "Administrador"
    )

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
        setupRolesDropdown()

        binding.btnRegister.setOnClickListener {
            // ✅ Solo procesa si los campos son válidos
            if (validarCampos()) {
                procesarRegistro()
            }
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }
    }

    // ── Procesa el registro según el rol ──────────────────────────────────────
    private fun procesarRegistro() {
        val nombre   = binding.etName.text.toString().trim()
        val email    = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val role     = binding.actvRole.text.toString().trim().lowercase()

        if (role == RolUtils.ROL_ADMIN) {
            // Deshabilitar botón mientras verifica
            binding.progressBar.visible()
            binding.btnRegister.isEnabled = false

            RolUtils.verificarLimiteAdmins(
                onPermitido = {
                    // ✅ Hay espacio, proceder con el registro
                    viewModel.register(nombre, email, role, password)
                },
                onLimitAlcanzado = {
                    // ❌ Ya hay 3 admins
                    binding.progressBar.gone()
                    binding.btnRegister.isEnabled = true
                    binding.tiltRole.error =
                        "Límite alcanzado: solo se permiten ${RolUtils.MAX_ADMINS} administradores"
                }
            )
        } else {
            // Es cliente, registrar directo
            viewModel.register(nombre, email, role, password)
        }
    }

    private fun setupRolesDropdown() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            roles
        )
        binding.actvRole.setAdapter(adapter)
        binding.actvRole.setOnClickListener {
            binding.actvRole.showDropDown()
        }
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
                    showToast(getString(R.string.success_register))
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
        val role            = binding.actvRole.text.toString().trim()

        // Limpiar errores previos
        binding.tilName.error            = null
        binding.tilEmail.error           = null
        binding.tilPassword.error        = null
        binding.tilConfirmPassword.error = null
        binding.tiltRole.error           = null

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
        // ✅ Validar que se haya seleccionado un rol
        if (!ValidationUtils.isNotEmpty(role)) {
            binding.tiltRole.error = getString(R.string.field_required)
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