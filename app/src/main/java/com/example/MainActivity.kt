package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.Language
import com.example.model.LocalizationManager
import com.example.ui.components.NexoraHeader
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.AppDestination
import com.example.viewmodel.NexoraViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                NexoraApp()
            }
        }
    }
}

@Composable
fun NexoraApp(viewModel: NexoraViewModel = viewModel()) {
    val currentDestination by viewModel.currentDestination.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val isGeminiActive by viewModel.isGeminiActive.collectAsStateWithLifecycle()
    val feedbackMessage by viewModel.userFeedbackMessage.collectAsStateWithLifecycle()

    val activeTool by viewModel.activeTool.collectAsStateWithLifecycle()
    val isGeneratingTool by viewModel.isGeneratingTool.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()

    val currentAnalysis by viewModel.currentAnalysis.collectAsStateWithLifecycle()
    val isAnalyzing by viewModel.isAnalyzing.collectAsStateWithLifecycle()

    val currentPlan by viewModel.currentPlan.collectAsStateWithLifecycle()
    val isGeneratingPlan by viewModel.isGeneratingPlan.collectAsStateWithLifecycle()
    val isExecutingPipeline by viewModel.isExecutingPipeline.collectAsStateWithLifecycle()

    val savedProjects by viewModel.savedProjects.collectAsStateWithLifecycle()
    val recentTools by viewModel.recentTools.collectAsStateWithLifecycle()

    val strings = LocalizationManager.getStrings(currentLanguage)
    val layoutDirection = if (currentLanguage == Language.ARABIC) LayoutDirection.Rtl else LayoutDirection.Ltr

    // Custom back navigation support
    BackHandler(enabled = currentDestination != AppDestination.WORKSPACE) {
        viewModel.navigateTo(AppDestination.WORKSPACE)
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(NexoraBlack)
                .testTag("nexora_root_scaffold"),
            topBar = {
                NexoraHeader(
                    currentLanguage = currentLanguage,
                    isGeminiActive = isGeminiActive,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    modifier = Modifier.statusBarsPadding()
                )
            },
            bottomBar = {
                NavigationBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("nexora_bottom_navigation"),
                    containerColor = NexoraDarkSurface,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentDestination == AppDestination.WORKSPACE,
                        onClick = { viewModel.navigateTo(AppDestination.WORKSPACE) },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = strings.navWorkspace) },
                        label = { Text(strings.navWorkspace, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NexoraCyan,
                            selectedTextColor = NexoraCyan,
                            indicatorColor = NexoraCyanGlow,
                            unselectedIconColor = NexoraSilverMuted,
                            unselectedTextColor = NexoraSilverDark
                        ),
                        modifier = Modifier.testTag("nav_workspace")
                    )

                    NavigationBarItem(
                        selected = currentDestination == AppDestination.VISION,
                        onClick = { viewModel.navigateTo(AppDestination.VISION) },
                        icon = { Icon(Icons.Default.Visibility, contentDescription = strings.navVision) },
                        label = { Text(strings.navVision, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NexoraCyan,
                            selectedTextColor = NexoraCyan,
                            indicatorColor = NexoraCyanGlow,
                            unselectedIconColor = NexoraSilverMuted,
                            unselectedTextColor = NexoraSilverDark
                        ),
                        modifier = Modifier.testTag("nav_vision")
                    )

                    NavigationBarItem(
                        selected = currentDestination == AppDestination.BUILDER,
                        onClick = { viewModel.navigateTo(AppDestination.BUILDER) },
                        icon = { Icon(Icons.Default.Build, contentDescription = strings.navBuilder) },
                        label = { Text(strings.navBuilder, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NexoraViolet,
                            selectedTextColor = NexoraViolet,
                            indicatorColor = NexoraVioletGlow,
                            unselectedIconColor = NexoraSilverMuted,
                            unselectedTextColor = NexoraSilverDark
                        ),
                        modifier = Modifier.testTag("nav_builder")
                    )

                    NavigationBarItem(
                        selected = currentDestination == AppDestination.PLANNER,
                        onClick = { viewModel.navigateTo(AppDestination.PLANNER) },
                        icon = { Icon(Icons.Default.Rule, contentDescription = strings.navPlanner) },
                        label = { Text(strings.navPlanner, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NexoraEmerald,
                            selectedTextColor = NexoraEmerald,
                            indicatorColor = NexoraEmerald.copy(alpha = 0.2f),
                            unselectedIconColor = NexoraSilverMuted,
                            unselectedTextColor = NexoraSilverDark
                        ),
                        modifier = Modifier.testTag("nav_planner")
                    )

                    NavigationBarItem(
                        selected = currentDestination == AppDestination.PROJECTS,
                        onClick = { viewModel.navigateTo(AppDestination.PROJECTS) },
                        icon = { Icon(Icons.Default.Folder, contentDescription = strings.navProjects) },
                        label = { Text(strings.navProjects, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NexoraAmber,
                            selectedTextColor = NexoraAmber,
                            indicatorColor = Color(0x22F59E0B),
                            unselectedIconColor = NexoraSilverMuted,
                            unselectedTextColor = NexoraSilverDark
                        ),
                        modifier = Modifier.testTag("nav_projects")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(NexoraBlack)
            ) {
                // Active Screen routing
                when (currentDestination) {
                    AppDestination.WORKSPACE -> {
                        WorkspaceScreen(
                            language = currentLanguage,
                            recentTools = recentTools,
                            onNavigateToVision = { viewModel.navigateTo(AppDestination.VISION) },
                            onNavigateToBuilder = { viewModel.navigateTo(AppDestination.BUILDER) },
                            onNavigateToPlanner = { viewModel.navigateTo(AppDestination.PLANNER) },
                            onNavigateToProjects = { viewModel.navigateTo(AppDestination.PROJECTS) },
                            onSelectTool = { tool -> viewModel.selectToolDirectly(tool) }
                        )
                    }
                    AppDestination.VISION -> {
                        VisionScreen(
                            language = currentLanguage,
                            currentAnalysis = currentAnalysis,
                            isAnalyzing = isAnalyzing,
                            onAnalyzePreset = { preset -> viewModel.analyzePreset(preset) },
                            onAnalyzeCustom = { concept -> viewModel.analyzeCustom(concept) },
                            onConvertToInteractiveTool = { suggestion ->
                                viewModel.convertSuggestionToInteractiveTool(suggestion)
                            }
                        )
                    }
                    AppDestination.BUILDER -> {
                        ToolBuilderScreen(
                            language = currentLanguage,
                            activeTool = activeTool,
                            isGenerating = isGeneratingTool,
                            chatMessages = chatMessages,
                            onGenerateFromPrompt = { prompt -> viewModel.generateToolFromPrompt(prompt) },
                            onSendChatInstruction = { instruction -> viewModel.sendChatInstruction(instruction) },
                            onSaveToolToProjects = { viewModel.saveActiveToolToProjects() },
                            onActionTriggered = { id, outcome -> viewModel.triggerToolAction(id, outcome) }
                        )
                    }
                    AppDestination.PLANNER -> {
                        TaskPlannerScreen(
                            language = currentLanguage,
                            currentPlan = currentPlan,
                            isGeneratingPlan = isGeneratingPlan,
                            isExecutingPipeline = isExecutingPipeline,
                            onGeneratePlan = { goal -> viewModel.generatePlan(goal) },
                            onToggleApproval = { stepId -> viewModel.toggleApproval(stepId) },
                            onExecuteSingleStep = { stepId -> viewModel.executeSingleStep(stepId) },
                            onRunFullPipeline = { viewModel.runFullPipeline() }
                        )
                    }
                    AppDestination.PROJECTS -> {
                        ProjectsScreen(
                            language = currentLanguage,
                            projects = savedProjects,
                            onOpenProject = { project -> viewModel.openProject(project) },
                            onDeleteProject = { id -> viewModel.deleteProject(id) },
                            onNavigateToBuilder = { viewModel.navigateTo(AppDestination.BUILDER) }
                        )
                    }
                }

                // Floating Feedback Notification Banner
                AnimatedVisibility(
                    visible = feedbackMessage != null,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                ) {
                    feedbackMessage?.let { msg ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = NexoraCardElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, NexoraCyan),
                            tonalElevation = 8.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("feedback_banner")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = NexoraCyan)
                                    Text(text = msg, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                                }
                                IconButton(
                                    onClick = { viewModel.clearFeedbackMessage() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = NexoraSilverMuted, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
