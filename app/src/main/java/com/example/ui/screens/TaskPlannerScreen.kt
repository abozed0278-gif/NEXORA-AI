package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun TaskPlannerScreen(
    language: Language,
    currentPlan: TaskPlan?,
    isGeneratingPlan: Boolean,
    isExecutingPipeline: Boolean,
    onGeneratePlan: (String) -> Unit,
    onToggleApproval: (String) -> Unit,
    onExecuteSingleStep: (String) -> Unit,
    onRunFullPipeline: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalizationManager.getStrings(language)
    val isArabic = language == Language.ARABIC

    var planGoalInput by remember { mutableStateOf("") }
    var selectedStepForApprovalModal by remember { mutableStateOf<TaskStep?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("task_planner_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = strings.plannerTitle,
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White
                )
                Text(
                    text = strings.plannerSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NexoraSilverMuted
                )
            }
        }

        // Mission / Goal Input Box
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("planner_input_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NexoraCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, NexoraBorderGlow)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isArabic) "حدد مهمة تدفق العمل المطلوب تخطيطها" else "SPECIFY WORKFLOW OBJECTIVE TO PLAN",
                        style = MaterialTheme.typography.labelSmall,
                        color = NexoraEmerald,
                        letterSpacing = 1.sp
                    )

                    OutlinedTextField(
                        value = planGoalInput,
                        onValueChange = { planGoalInput = it },
                        placeholder = {
                            Text(
                                strings.plannerInputPlaceholder,
                                color = NexoraSilverDark,
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("planner_goal_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NexoraEmerald,
                            unfocusedBorderColor = NexoraBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = NexoraSilver
                        ),
                        minLines = 2
                    )

                    Button(
                        onClick = {
                            if (planGoalInput.isNotBlank()) {
                                onGeneratePlan(planGoalInput)
                            }
                        },
                        enabled = !isGeneratingPlan && planGoalInput.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("generate_plan_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NexoraEmerald, contentColor = Color.White)
                    ) {
                        if (isGeneratingPlan) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isArabic) "جاري توليد خطوات التدفق..." else "Compiling Workflow Steps...")
                        } else {
                            Icon(Icons.Default.AccountTree, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                strings.generatePlanBtn,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Active Plan Display
        if (currentPlan != null) {
            // Plan Overview Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("plan_overview_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NexoraDarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NexoraBorderGlow)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = currentPlan.title,
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = NexoraEmerald.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NexoraEmerald)
                            ) {
                                Text(
                                    text = "${currentPlan.steps.count { it.status == StepStatus.COMPLETED }}/${currentPlan.steps.size} Done",
                                    color = NexoraEmerald,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Text(
                            text = currentPlan.objective,
                            style = MaterialTheme.typography.bodyMedium,
                            color = NexoraSilver
                        )

                        // Progress Bar
                        val completedCount = currentPlan.steps.count { it.status == StepStatus.COMPLETED }
                        val progress = if (currentPlan.steps.isNotEmpty()) completedCount.toFloat() / currentPlan.steps.size else 0f

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isArabic) "اكتمال التدفق" else "Pipeline Completion",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NexoraSilverMuted
                                )
                                Text(
                                    text = "${(progress * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NexoraCyan
                                )
                            }
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = NexoraCyan,
                                trackColor = NexoraBorder
                            )
                        }

                        // Run Full Pipeline Button
                        Button(
                            onClick = onRunFullPipeline,
                            enabled = !isExecutingPipeline && currentPlan.steps.any { it.status != StepStatus.COMPLETED },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("run_approved_pipeline_btn"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NexoraCyan, contentColor = NexoraBlack)
                        ) {
                            if (isExecutingPipeline) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = NexoraBlack, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isArabic) "جاري تنفيذ الخطوات المعتمدة..." else "Executing Approved Steps...")
                            } else {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(strings.executeAllSteps, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Steps Title
            item {
                Text(
                    text = if (isArabic) "خطوات تدفق العمل ونقاط الاعتماد" else "ORDERED WORKFLOW STEPS & APPROVAL GATES",
                    style = MaterialTheme.typography.labelLarge,
                    color = NexoraSilverMuted,
                    letterSpacing = 1.sp
                )
            }

            // Individual Steps
            items(currentPlan.steps) { step ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("step_card_${step.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = NexoraCardSurface),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        when (step.status) {
                            StepStatus.COMPLETED -> NexoraEmerald
                            StepStatus.IN_PROGRESS -> NexoraCyan
                            StepStatus.PENDING_APPROVAL -> NexoraAmber
                            StepStatus.APPROVED -> NexoraViolet
                            else -> NexoraBorder
                        }
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (step.status) {
                                                StepStatus.COMPLETED -> NexoraEmerald.copy(alpha = 0.2f)
                                                StepStatus.PENDING_APPROVAL -> NexoraAmber.copy(alpha = 0.2f)
                                                else -> NexoraCyanGlow
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${step.order}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (step.status == StepStatus.COMPLETED) NexoraEmerald else NexoraCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = step.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White
                                )
                            }

                            // Status Tag
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when (step.status) {
                                    StepStatus.COMPLETED -> Color(0x2210B981)
                                    StepStatus.IN_PROGRESS -> Color(0x2200F0FF)
                                    StepStatus.PENDING_APPROVAL -> Color(0x22F59E0B)
                                    else -> Color(0x22A855F7)
                                }
                            ) {
                                Text(
                                    text = when (step.status) {
                                        StepStatus.COMPLETED -> if (isArabic) "مكتملة" else "Completed"
                                        StepStatus.IN_PROGRESS -> if (isArabic) "قيد التنفيذ..." else "In Progress..."
                                        StepStatus.PENDING_APPROVAL -> if (isArabic) "بانتظار الموافقة" else "Awaiting Approval"
                                        StepStatus.APPROVED -> if (isArabic) "معتمدة" else "Approved"
                                        else -> "Pending"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = when (step.status) {
                                        StepStatus.COMPLETED -> NexoraEmerald
                                        StepStatus.IN_PROGRESS -> NexoraCyan
                                        StepStatus.PENDING_APPROVAL -> NexoraAmber
                                        else -> NexoraViolet
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = step.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = NexoraSilver
                        )

                        if (step.executionLog.isNotBlank()) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(6.dp),
                                color = NexoraDarkSurface
                            ) {
                                Text(
                                    text = "LOG: ${step.executionLog}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NexoraSilverMuted,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }

                        // Approval & Execution Controls
                        if (step.status != StepStatus.COMPLETED) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (step.requiresApproval) {
                                    OutlinedButton(
                                        onClick = { onToggleApproval(step.id) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("toggle_approval_${step.id}"),
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (step.isApproved) NexoraEmerald else NexoraAmber
                                        ),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = if (step.isApproved) NexoraEmerald else NexoraAmber
                                        )
                                    ) {
                                        Icon(
                                            imageVector = if (step.isApproved) Icons.Default.CheckCircle else Icons.Default.VerifiedUser,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (step.isApproved) strings.stepApproved else strings.approveStep,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                Button(
                                    onClick = { onExecuteSingleStep(step.id) },
                                    enabled = !step.requiresApproval || step.isApproved,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("execute_step_${step.id}"),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = NexoraCyan,
                                        contentColor = NexoraBlack,
                                        disabledContainerColor = NexoraCardElevated,
                                        disabledContentColor = NexoraSilverDark
                                    )
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(strings.executeStep, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
