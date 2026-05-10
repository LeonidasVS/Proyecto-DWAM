package com.example.shopnova

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.shopnova.UI.DashboardActivity
import com.example.shopnova.UI.RegisterActivity
import com.example.shopnova.Utils.UiState
import com.example.shopnova.Utils.ValidationUtils
import com.example.shopnova.Utils.gone
import com.example.shopnova.Utils.showSnackbarError
import com.example.shopnova.Utils.showToast
import com.example.shopnova.Utils.visible
import com.example.shopnova.Viewmodel.AuthViewModel
import com.example.shopnova.databinding.ActivityMainBinding
import android.widget.Toast
import es.dmoral.toasty.Toasty

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        binding= ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if(viewModel.isLoggedIn()){
            irDashboard()
            return
        }

        setupObservers()

        binding.btnLogin.setOnClickListener {
            if(validarCampos()){
                val email=binding.etEmail.text.toString().trim()
                val password=binding.etPassword.text.toString()
                viewModel.login(email, password)
            }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }

    private fun setupObservers(){
        viewModel.loginState.observe(this){state ->
            when (state){
                is UiState.Loading ->{
                   binding.progressBar.visible()
                    binding.btnLogin.isEnabled=false
                }
                is UiState.Success -> {
                    binding.progressBar.gone()
                    binding.btnLogin.isEnabled = true
                    Toasty.success(
                        this,
                        getString(R.string.success_login),
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                    viewModel.resetLoginState()
                    irDashboard()
                }

                is UiState.Error -> {
                    binding.progressBar.gone()
                    binding.btnLogin.isEnabled = true
                    Toasty.error(
                        this,
                        getString(R.string.login_failure),
                        Toast.LENGTH_LONG,
                        true
                    ).show()
                    viewModel.resetLoginState()
                }
                else ->{
                    binding.progressBar.gone()
                    binding.btnLogin.isEnabled = true
                }
            }
        }
    }

    private fun validarCampos(): Boolean{

        var isValid=true
        val email=binding.etEmail.text.toString().trim()
        val password=binding.etPassword.text.toString()

        binding.tilEmail.error=null
        binding.tilPassword.error=null

        if (!ValidationUtils.isNotEmpty(email)) {
            binding.tilEmail.error = getString(R.string.field_required)
            isValid = false
        } else if (!ValidationUtils.isValidEmail(email)) {
            binding.tilEmail.error = getString(R.string.invalid_email)
            isValid = false
        }

        if (!ValidationUtils.isNotEmpty(password)) {
            binding.tilPassword.error = getString(R.string.field_required)
            isValid = false
        } else if (!ValidationUtils.isValidPassword(password)) {
            binding.tilPassword.error = getString(R.string.password_short)
            isValid = false
        }

        return isValid
    }

    private fun irDashboard(){
        val intent= Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}