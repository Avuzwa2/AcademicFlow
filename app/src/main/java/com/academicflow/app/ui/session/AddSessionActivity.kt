package com.academicflow.app.ui.session

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.academicflow.app.AcademicFlowApp
import com.academicflow.app.data.local.entity.StudySession
import com.academicflow.app.data.local.entity.Subject
import com.academicflow.app.databinding.ActivityAddSessionBinding
import com.academicflow.app.util.SessionManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddSessionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddSessionBinding
    private lateinit var session: SessionManager
    private var subjectsList: List<Subject> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        // Set current date as default
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        binding.etDate.setText(sdf.format(Date()))

        loadSubjects()

        binding.btnSave.setOnClickListener {
            saveStudySession()
        }
    }

    private fun loadSubjects() {
        val userId = session.getUserId()
        val app = application as AcademicFlowApp

        lifecycleScope.launch {
            subjectsList = app.subjectRepository.getSubjects(userId)
            val names = subjectsList.map { it.subjectName }
            val adapter = ArrayAdapter(this@AddSessionActivity, android.R.layout.simple_dropdown_item_1line, names)
            binding.spinnerSubject.setAdapter(adapter)
            if (names.isNotEmpty()) {
                binding.spinnerSubject.setText(names[0], false)
            }
        }
    }

    private fun saveStudySession() {
        val durationStr = binding.etDuration.text?.toString().orEmpty()
        val subjectName = binding.spinnerSubject.text?.toString().orEmpty()
        val description = binding.etDescription.text?.toString().orEmpty()

        val duration = durationStr.toFloatOrNull()
        if (duration == null || duration <= 0f) {
            Toast.makeText(this, "Please enter a valid duration", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedSubject = subjectsList.find { it.subjectName == subjectName }
        if (selectedSubject == null) {
            Toast.makeText(this, "Please select a subject", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = session.getUserId()
        val app = application as AcademicFlowApp

        lifecycleScope.launch {
            val studySession = StudySession(
                userId = userId,
                subjectId = selectedSubject.subjectId,
                durationHours = duration,
                sessionDate = System.currentTimeMillis(),
                description = description
            )
            app.studySessionRepository.addSession(studySession)
            Toast.makeText(this@AddSessionActivity, "Session saved successfully", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}