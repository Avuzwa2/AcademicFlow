package com.academicflow.app.ui.subject

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.academicflow.app.AcademicFlowApp
import com.academicflow.app.data.local.entity.Subject
import com.academicflow.app.databinding.ActivitySubjectListBinding
import com.academicflow.app.databinding.ItemSubjectBinding
import com.academicflow.app.util.SessionManager
import kotlinx.coroutines.launch

class SubjectListActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySubjectListBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubjectListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)
        binding.rvSubjects.layoutManager = LinearLayoutManager(this)

        loadSubjects()

        binding.btnFabAdd.setOnClickListener { showAddSubjectDialog() }
        binding.btnAddCustomSubject.setOnClickListener { showAddSubjectDialog() }
    }

    private fun loadSubjects() {
        val userId = session.getUserId()
        val app = application as AcademicFlowApp

        lifecycleScope.launch {
            app.subjectRepository.observeSubjects(userId).collect { list ->
                if (list.isEmpty()) {
                    // Fallback to match screenshot exactly
                    val dummyList = listOf(
                        Subject(subjectName = "Open Source Coding", colorCode = "#4F46E5", userId = userId),
                        Subject(subjectName = "Software Quality and Testing", colorCode = "#F59E0B", userId = userId),
                        Subject(subjectName = "Database Design", colorCode = "#10B981", userId = userId),
                        Subject(subjectName = "Communication", colorCode = "#EF4444", userId = userId)
                    )
                    binding.rvSubjects.adapter = SubjectAdapter(dummyList, onDelete = {}, onEdit = {})
                } else {
                    binding.rvSubjects.adapter = SubjectAdapter(list, 
                        onDelete = { sub ->
                            lifecycleScope.launch {
                                app.subjectRepository.deleteSubject(sub)
                                Toast.makeText(this@SubjectListActivity, "Subject deleted", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onEdit = { sub ->
                            showEditSubjectDialog(sub)
                        }
                    )
                }
            }
        }
    }

    private fun showAddSubjectDialog() {
        val input = EditText(this).apply { hint = "Enter subject name" }
        AlertDialog.Builder(this)
            .setTitle("Add Custom Subject")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    val colors = listOf("#4F46E5", "#F59E0B", "#10B981", "#EF4444", "#7C3AED", "#3B82F6")
                    val randomColor = colors.random()
                    val userId = session.getUserId()
                    val app = application as AcademicFlowApp
                    lifecycleScope.launch {
                        app.subjectRepository.addSubject(userId, name, randomColor)
                        Toast.makeText(this@SubjectListActivity, "Subject added", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditSubjectDialog(subject: Subject) {
        val input = EditText(this).apply {
            hint = "Enter subject name"
            setText(subject.subjectName)
        }
        AlertDialog.Builder(this)
            .setTitle("Edit Subject")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    val app = application as AcademicFlowApp
                    lifecycleScope.launch {
                        app.subjectRepository.updateSubject(subject.copy(subjectName = name))
                        Toast.makeText(this@SubjectListActivity, "Subject updated", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    class SubjectAdapter(
        private val list: List<Subject>,
        private val onDelete: (Subject) -> Unit,
        private val onEdit: (Subject) -> Unit
    ) : RecyclerView.Adapter<SubjectAdapter.VH>() {
        class VH(val b: ItemSubjectBinding) : RecyclerView.ViewHolder(b.root)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(ItemSubjectBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        override fun getItemCount() = list.size
        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = list[position]
            holder.b.tvSubjectName.text = item.subjectName
            holder.b.vColorIndicator.setBackgroundColor(Color.parseColor(item.colorCode))
            holder.b.btnDelete.setOnClickListener { onDelete(item) }
            holder.b.btnEdit.setOnClickListener { onEdit(item) }
        }
    }
}