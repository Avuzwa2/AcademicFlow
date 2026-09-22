package com.academicflow.app.ui.achievements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.academicflow.app.AcademicFlowApp
import com.academicflow.app.data.local.entity.Achievement
import com.academicflow.app.databinding.ActivityAchievementsBinding
import com.academicflow.app.databinding.ItemAchievementBinding
import com.academicflow.app.util.SessionManager
import kotlinx.coroutines.launch

class AchievementsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAchievementsBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAchievementsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)
        binding.rvAchievements.layoutManager = LinearLayoutManager(this)

        loadAchievements()
    }

    private fun loadAchievements() {
        val userId = session.getUserId()
        val app = application as AcademicFlowApp

        lifecycleScope.launch {
            app.achievementRepository.observeAchievements(userId).collect { list ->
                if (list.isEmpty()) {
                    // Pre-populate matches screenshot exactly
                    val dummy = listOf(
                        DummyAchievement("🔥", "7-Day Streak!", "Studied 7 days in a row", 100),
                        DummyAchievement("📘", "Subject Master", "10+ hours in Programming", 100),
                        DummyAchievement("🌅", "Early Riser", "3 sessions before 8 AM", 80),
                        DummyAchievement("💪", "14-Day Streak", "Consistency champion", 50)
                    )
                    binding.rvAchievements.adapter = DummyAdapter(dummy)
                } else {
                    binding.rvAchievements.adapter = AchievementAdapter(list)
                }
            }
        }
    }

    data class DummyAchievement(val emoji: String, val name: String, val desc: String, val progress: Int)

    class DummyAdapter(private val list: List<DummyAchievement>) : RecyclerView.Adapter<DummyAdapter.VH>() {
        class VH(val b: ItemAchievementBinding) : RecyclerView.ViewHolder(b.root)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(ItemAchievementBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        override fun getItemCount() = list.size
        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = list[position]
            holder.b.tvBadgeEmoji.text = item.emoji
            holder.b.tvBadgeName.text = item.name
            holder.b.tvBadgeDescription.text = item.desc
            if (item.progress >= 100) {
                holder.b.tvCheckmark.visibility = View.VISIBLE
                holder.b.tvProgressPill.visibility = View.GONE
            } else {
                holder.b.tvCheckmark.visibility = View.GONE
                holder.b.tvProgressPill.visibility = View.VISIBLE
                holder.b.tvProgressPill.text = "${item.progress}%"
            }
        }
    }

    class AchievementAdapter(private val list: List<Achievement>) : RecyclerView.Adapter<AchievementAdapter.VH>() {
        class VH(val b: ItemAchievementBinding) : RecyclerView.ViewHolder(b.root)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(ItemAchievementBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        override fun getItemCount() = list.size
        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = list[position]
            holder.b.tvBadgeName.text = item.badgeName
            holder.b.tvBadgeDescription.text = item.badgeDescription
            
            // Map names to emojis
            holder.b.tvBadgeEmoji.text = when(item.badgeName) {
                "7-Day Streak!" -> "🔥"
                "Subject Master" -> "📘"
                "Early Riser" -> "🌅"
                "14-Day Streak" -> "💪"
                else -> "🏆"
            }

            if (item.progressPercentage >= 100) {
                holder.b.tvCheckmark.visibility = View.VISIBLE
                holder.b.tvProgressPill.visibility = View.GONE
            } else {
                holder.b.tvCheckmark.visibility = View.GONE
                holder.b.tvProgressPill.visibility = View.VISIBLE
                holder.b.tvProgressPill.text = "${item.progressPercentage}%"
            }
        }
    }
}