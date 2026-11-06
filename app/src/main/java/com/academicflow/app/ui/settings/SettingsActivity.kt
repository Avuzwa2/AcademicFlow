package com.academicflow.app.ui.settings

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.academicflow.app.databinding.ActivitySettingsBinding
import com.academicflow.app.ui.auth.LoginActivity
import com.academicflow.app.util.SessionManager
import java.util.Locale

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        val prefs = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val currentLang = prefs.getString("selected_language", "en") ?: "en"

        when (currentLang) {
            "af" -> binding.rbAfrikaans.isChecked = true
            "zu" -> binding.rbZulu.isChecked = true
            else -> binding.rbEnglish.isChecked = true
        }

        binding.rgLanguage.setOnCheckedChangeListener { _, checkedId ->
            val langCode = when (checkedId) {
                binding.rbAfrikaans.id -> "af"
                binding.rbZulu.id -> "zu"
                else -> "en"
            }
            if (langCode != currentLang) {
                prefs.edit().putString("selected_language", langCode).apply()
                setAppLocale(langCode)
                Toast.makeText(this, "Language updated to $langCode", Toast.LENGTH_SHORT).show()
                recreate()
            }
        }

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("notifications_enabled", isChecked).apply()
            val msg = if (isChecked) "Push Notifications Enabled" else "Push Notifications Disabled"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        binding.switchOfflineSync.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("offline_sync_enabled", isChecked).apply()
            val msg = if (isChecked) "Offline Sync Enabled" else "Offline Sync Disabled"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        binding.btnLogout.setOnClickListener {
            session.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setAppLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}