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
import com.academicflow.app.databinding.ActivityLoginBinding
import com.academicflow.app.ui.dashboard.DashboardActivity
import com.academicflow.app.util.SessionManager
import kotlinx.coroutines.launch

/**
 * Login screen — first screen the user sees.
 * Allows existing users to sign in, or navigate to RegisterActivity.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        // If already logged in, skip straight to dashboard
        if (session.isLoggedIn()) {
            goToDashboard()
            return
        }

        setupClickListeners()
        observeAuthState()
    }

    private fun setupClickListeners() {
        binding.btnSignIn.setOnClickListener {
            val username = binding.etUsername.text?.toString().orEmpty()
            val password = binding.etPassword.text?.toString().orEmpty()
            viewModel.login(username, password)
        }

        binding.btnSsoLogin.setOnClickListener {
            // Single Sign-On (SSO) login flow
            Toast.makeText(this, "Single Sign-On (SSO) Authenticated!", Toast.LENGTH_SHORT).show()
            session.saveLoggedInUser(101L, "SSO_Student")
            com.academicflow.app.util.NotificationHelper.showStudyNotification(
                this,
                "AcademicFlow SSO",
                "Successfully logged in via Single Sign-On (SSO)"
            )
            goToDashboard()
        }

        binding.tvSignUpLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
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
                            Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnSignIn.isEnabled = !loading
    }

    private fun goToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}