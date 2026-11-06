package com.academicflow.app.ui.session

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
    private var selectedDateMillis: Long = System.currentTimeMillis()
    private var selectedPhotoUri: Uri? = null

    private val selectPhotoLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedPhotoUri = it
            binding.tvPhotoStatus.text = "📷 Photo Attached"
            Toast.makeText(this, "Photo selected!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        binding.etDate.setText(sdf.format(Date(selectedDateMillis)))

        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnUpload.setOnClickListener {
            selectPhotoLauncher.launch("image/*")
        }

        loadSubjects()

        binding.btnSave.setOnClickListener {
            saveStudySession()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance().apply { timeInMillis = selectedDateMillis }
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                selectedDateMillis = selectedCalendar.timeInMillis
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                binding.etDate.setText(sdf.format(Date(selectedDateMillis)))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
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
                sessionDate = selectedDateMillis,
                description = description,
                photoUri = selectedPhotoUri?.toString()
            )
            app.studySessionRepository.addSession(studySession)
            Toast.makeText(this@AddSessionActivity, "Session saved successfully", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}