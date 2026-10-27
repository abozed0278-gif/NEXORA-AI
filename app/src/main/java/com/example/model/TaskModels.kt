package com.example.model

data class TaskPlan(
    val id: String,
    val title: String,
    val objective: String,
    val steps: List<TaskStep>,
    val overallStatus: PlanStatus = PlanStatus.IN_PLANNING,
    val isSimulation: Boolean = false
)

enum class PlanStatus {
    IN_PLANNING,
    READY_FOR_APPROVAL,
    EXECUTING,
    COMPLETED
}

enum class StepStatus {
    PENDING_APPROVAL,
    APPROVED,
    IN_PROGRESS,
    COMPLETED,
    FAILED
}

data class TaskStep(
    val id: String,
    val order: Int,
    val title: String,
    val description: String,
    val requiresApproval: Boolean,
    val isApproved: Boolean = false,
    val status: StepStatus = if (requiresApproval) StepStatus.PENDING_APPROVAL else StepStatus.APPROVED,
    val estimatedDurationSec: Int = 3,
    val executionLog: String = "",
    val outputData: String = ""
)
