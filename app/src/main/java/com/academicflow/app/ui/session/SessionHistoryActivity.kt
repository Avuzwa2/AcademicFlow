package com.academicflow.app.ui.session

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.academicflow.app.AcademicFlowApp
import com.academicflow.app.data.local.entity.StudySession
import com.academicflow.app.data.local.entity.Subject
import com.academicflow.app.databinding.ActivitySessionHistoryBinding
import com.academicflow.app.databinding.ItemSessionHistoryBinding
import com.academicflow.app.util.SessionManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SessionHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySessionHistoryBinding
    private lateinit var session: SessionManager
    private var allSessions: List<StudySession> = emptyList()
    private var allSubjects: List<Subject> = emptyList()
    private var selectedSubjectId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySessionHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)
        binding.rvSessions.layoutManager = LinearLayoutManager(this)

        loadHistory()

        binding.btnFilter.setOnClickListener {
            showFilterDialog()
        }

        binding.btnViewPhoto.setOnClickListener {
            viewLatestPhoto()
        }
    }

    private fun loadHistory() {
        val userId = session.getUserId()
        val app = application as AcademicFlowApp

        lifecycleScope.launch {
            allSubjects = app.subjectRepository.getSubjects(userId)
            val subjectMap = allSubjects.associateBy { it.subjectId }

            app.studySessionRepository.observeAllSessions(userId).collect { sessions ->
                allSessions = sessions
                renderSessions(subjectMap)
            }
        }
    }

    private fun renderSessions(subjectMap: Map<Long, Subject>) {
        val filtered = if (selectedSubjectId != null) {
            allSessions.filter { it.subjectId == selectedSubjectId }
        } else {
            allSessions
        }

        if (allSessions.isEmpty()) {
            val dummySessions = listOf(
                DummySession("04 Aug", "Open Source Coding - Ch.5 Exercises", "1.5 hrs", "#4F46E5"),
                DummySession("04 Aug", "Coding - Coding Review", "2.0 hrs", "#F59E0B"),
                DummySession("03 Aug", "Database - ERD Diagrams", "1.0 hrs", "#10B981"),
                DummySession("03 Aug", "Programming - Array Methods", "2.5 hrs", "#EF4444")
            )
            binding.rvSessions.adapter = DummyAdapter(dummySessions)
        } else {
            binding.rvSessions.adapter = SessionAdapter(filtered, subjectMap) { session ->
                if (!session.photoUri.isNullOrEmpty()) {
                    showPhotoDialog(session.photoUri)
                } else {
                    Toast.makeText(this, "No photo attached to this session", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showFilterDialog() {
        if (allSubjects.isEmpty()) {
            Toast.makeText(this, "No subjects available to filter", Toast.LENGTH_SHORT).show()
            return
        }

        val options = mutableListOf("All Subjects")
        options.addAll(allSubjects.map { it.subjectName })

        AlertDialog.Builder(this)
            .setTitle("Filter by Subject")
            .setItems(options.toTypedArray()) { _, which ->
                selectedSubjectId = if (which == 0) null else allSubjects[which - 1].subjectId
                val subjectMap = allSubjects.associateBy { it.subjectId }
                renderSessions(subjectMap)
                Toast.makeText(this, "Filter: ${options[which]}", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun viewLatestPhoto() {
        val sessionWithPhoto = allSessions.firstOrNull { !it.photoUri.isNullOrEmpty() }
        if (sessionWithPhoto != null && sessionWithPhoto.photoUri != null) {
            showPhotoDialog(sessionWithPhoto.photoUri)
        } else {
            Toast.makeText(this, "No attached photos found in recent sessions", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showPhotoDialog(photoUriStr: String) {
        val imageView = ImageView(this).apply {
            setPadding(32, 32, 32, 32)
            setImageURI(Uri.parse(photoUriStr))
        }

        AlertDialog.Builder(this)
            .setTitle("Attached Photo")
            .setView(imageView)
            .setPositiveButton("Close", null)
            .show()
    }

    data class DummySession(val date: String, val details: String, val duration: String, val color: String)

    class DummyAdapter(private val list: List<DummySession>) : RecyclerView.Adapter<DummyAdapter.VH>() {
        class VH(val b: ItemSessionHistoryBinding) : RecyclerView.ViewHolder(b.root)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(ItemSessionHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        override fun getItemCount() = list.size
        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = list[position]
            holder.b.tvDate.text = "📅 " + item.date
            holder.b.tvDetails.text = item.details
            holder.b.tvDuration.text = item.duration
            holder.b.vLeftBar.setBackgroundColor(Color.parseColor(item.color))
        }
    }

    class SessionAdapter(
        private val list: List<StudySession>,
        private val subjectMap: Map<Long, Subject>,
        private val onItemClick: (StudySession) -> Unit
    ) : RecyclerView.Adapter<SessionAdapter.VH>() {
        class VH(val b: ItemSessionHistoryBinding) : RecyclerView.ViewHolder(b.root)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(ItemSessionHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        override fun getItemCount() = list.size
        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = list[position]
            val subject = subjectMap[item.subjectId]
            val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
            holder.b.tvDate.text = "📅 " + sdf.format(Date(item.sessionDate))
            val photoIndicator = if (!item.photoUri.isNullOrEmpty()) " 📷" else ""
            holder.b.tvDetails.text = "${subject?.subjectName ?: "Subject"} - ${item.description}$photoIndicator"
            holder.b.tvDuration.text = "${item.durationHours} hrs"
            holder.b.vLeftBar.setBackgroundColor(Color.parseColor(subject?.colorCode ?: "#4F46E5"))

            holder.itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}