package com.academicflow.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.academicflow.app.databinding.ActivityRegisterBinding
import com.academicflow.app.ui.dashboard.DashboardActivity
import com.academicflow.app.util.SessionManager
import kotlinx.coroutines.launch

/**
 * Registration screen.
 * Allows new users to sign up, then seeds defaults and navigates to the dashboard.
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        setupClickListeners()
        observeAuthState()
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text?.toString().orEmpty()
            val password = binding.etPassword.text?.toString().orEmpty()
            val confirmPassword = binding.etConfirmPassword.text?.toString().orEmpty()
            viewModel.register(username, password, confirmPassword)
        }

        binding.tvSignInLink.setOnClickListener {
            finish() // Go back to LoginActivity
        }
    }

    private fun observeAuthState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.authState.collect { state ->
                    when (state) {
                        is AuthViewModel.AuthState.Idle -> showLoading(false)

                        is AuthViewModel.AuthState.Loading -> showLoading(true)

                        is AuthViewModel.AuthState.LoginSuccess -> {
                            showLoading(false)
                            session.saveLoggedInUser(state.userId, state.username)
                            goToDashboard()
                        }

                        is AuthViewModel.AuthState.Error -> {
                            showLoading(false)
                            Toast.makeText(this@RegisterActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !loading
    }

    private fun goToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}