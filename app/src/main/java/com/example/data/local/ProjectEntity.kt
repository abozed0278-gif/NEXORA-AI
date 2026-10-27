package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val category: String,
    val createdAt: Long = System.currentTimeMillis(),
    val toolSpecJson: String,
    val taskPlanJson: String = "",
    val visionJson: String = ""
)
