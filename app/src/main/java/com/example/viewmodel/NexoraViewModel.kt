package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.JsonHelper
import com.example.data.PresetData
import com.example.data.api.GeminiApiService
import com.example.data.local.NexoraDatabase
import com.example.data.local.ProjectEntity
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

enum class AppDestination {
    WORKSPACE,
    VISION,
    BUILDER,
    PLANNER,
    PROJECTS
}

class NexoraViewModel(application: Application) : AndroidViewModel(application) {

    private val db = NexoraDatabase.getDatabase(application)
    private val projectDao = db.projectDao()
    private val geminiService = GeminiApiService()

    // Navigation & App Settings
    private val _currentDestination = MutableStateFlow(AppDestination.WORKSPACE)
    val currentDestination: StateFlow<AppDestination> = _currentDestination.asStateFlow()

    private val _currentLanguage = MutableStateFlow(Language.ENGLISH)
    val currentLanguage: StateFlow<Language> = _currentLanguage.asStateFlow()

    private val _isGeminiActive = MutableStateFlow(geminiService.isGeminiConfigured())
    val isGeminiActive: StateFlow<Boolean> = _isGeminiActive.asStateFlow()

    // User Feedback Alert Banner
    private val _userFeedbackMessage = MutableStateFlow<String?>(null)
    val userFeedbackMessage: StateFlow<String?> = _userFeedbackMessage.asStateFlow()

    // Vision Analysis State
    private val _currentAnalysis = MutableStateFlow<VisionAnalysis?>(PresetData.samplePresets.first().analysis)
    val currentAnalysis: StateFlow<VisionAnalysis?> = _currentAnalysis.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    // Tool Builder State
    private val _activeTool = MutableStateFlow<ToolSpec?>(PresetData.getStarterProjects().first())
    val activeTool: StateFlow<ToolSpec?> = _activeTool.asStateFlow()

    private val _isGeneratingTool = MutableStateFlow(false)
    val isGeneratingTool: StateFlow<Boolean> = _isGeneratingTool.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "chat_init",
                sender = ChatSender.NEXORA_AI,
                content = "NEXORA Neural Engine active. Describe modifications or additional controls to tweak your active prototype."
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // Task Planner State
    private val _currentPlan = MutableStateFlow<TaskPlan?>(PresetData.getStarterTaskPlans().first())
    val currentPlan: StateFlow<TaskPlan?> = _currentPlan.asStateFlow()

    private val _isGeneratingPlan = MutableStateFlow(false)
    val isGeneratingPlan: StateFlow<Boolean> = _isGeneratingPlan.asStateFlow()

    private val _isExecutingPipeline = MutableStateFlow(false)
    val isExecutingPipeline: StateFlow<Boolean> = _isExecutingPipeline.asStateFlow()

    // Saved Projects List
    private val _savedProjects = MutableStateFlow<List<ProjectEntity>>(emptyList())
    val savedProjects: StateFlow<List<ProjectEntity>> = _savedProjects.asStateFlow()

    private val _recentTools = MutableStateFlow<List<ToolSpec>>(PresetData.getStarterProjects())
    val recentTools: StateFlow<List<ToolSpec>> = _recentTools.asStateFlow()

    init {
        // Collect saved projects from Room
        viewModelScope.launch {
            projectDao.getAllProjects().collectLatest { entities ->
                if (entities.isEmpty()) {
                    // Seed initial projects so the workspace is immediately rich and functional
                    PresetData.getStarterProjects().forEach { starter ->
                        projectDao.insertProject(
                            ProjectEntity(
                                title = starter.name,
                                description = starter.description,
                                category = starter.category,
                                toolSpecJson = JsonHelper.toolSpecToJson(starter),
                                taskPlanJson = JsonHelper.taskPlanToJson(PresetData.getStarterTaskPlans().first())
                            )
                        )
                    }
                } else {
                    _savedProjects.value = entities
                    val parsed = entities.mapNotNull { JsonHelper.jsonToToolSpec(it.toolSpecJson) }
                    if (parsed.isNotEmpty()) {
                        _recentTools.value = parsed
                    }
                }
            }
        }
    }

    fun navigateTo(dest: AppDestination) {
        _currentDestination.value = dest
    }

    fun toggleLanguage() {
        _currentLanguage.value = if (_currentLanguage.value == Language.ENGLISH) Language.ARABIC else Language.ENGLISH
    }

    fun clearFeedbackMessage() {
        _userFeedbackMessage.value = null
    }

    // Vision Actions
    fun analyzePreset(preset: VisionPreset) {
        _currentAnalysis.value = preset.analysis
    }

    fun analyzeCustom(concept: String) {
        viewModelScope.launch {
            _isAnalyzing.value = true
            try {
                val analysis = geminiService.analyzeVisionImage(concept, isCustomUpload = true)
                _currentAnalysis.value = analysis
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    fun convertSuggestionToInteractiveTool(suggestion: SuggestedToolAction) {
        viewModelScope.launch {
            _isGeneratingTool.value = true
            _currentDestination.value = AppDestination.BUILDER
            try {
                val tool = geminiService.generateToolSpec(suggestion.prompt)
                _activeTool.value = tool
            } finally {
                _isGeneratingTool.value = false
            }
        }
    }

    // Tool Builder Actions
    fun generateToolFromPrompt(prompt: String) {
        viewModelScope.launch {
            _isGeneratingTool.value = true
            try {
                val tool = geminiService.generateToolSpec(prompt)
                _activeTool.value = tool
            } finally {
                _isGeneratingTool.value = false
            }
        }
    }

    fun sendChatInstruction(instruction: String) {
        val current = _activeTool.value ?: return
        viewModelScope.launch {
            val userMsg = ChatMessage(
                id = "chat_u_${System.currentTimeMillis()}",
                sender = ChatSender.USER,
                content = instruction
            )
            _chatMessages.value = _chatMessages.value + userMsg

            val (updatedTool, summary) = geminiService.refineToolWithChat(current, instruction)
            _activeTool.value = updatedTool

            val aiMsg = ChatMessage(
                id = "chat_ai_${System.currentTimeMillis()}",
                sender = ChatSender.NEXORA_AI,
                content = summary,
                appliedChangeSummary = summary
            )
            _chatMessages.value = _chatMessages.value + aiMsg
        }
    }

    fun saveActiveToolToProjects() {
        val tool = _activeTool.value ?: return
        viewModelScope.launch {
            val json = JsonHelper.toolSpecToJson(tool)
            val planJson = _currentPlan.value?.let { JsonHelper.taskPlanToJson(it) } ?: ""
            projectDao.insertProject(
                ProjectEntity(
                    title = tool.name,
                    description = tool.description,
                    category = tool.category,
                    toolSpecJson = json,
                    taskPlanJson = planJson
                )
            )
            val isAr = _currentLanguage.value == Language.ARABIC
            _userFeedbackMessage.value = if (isAr) "تم حفظ الأداة التفاعلية في مساحة العمل بنجاح!" else "Interactive prototype saved to workspace!"
        }
    }

    fun openProject(project: ProjectEntity) {
        val tool = JsonHelper.jsonToToolSpec(project.toolSpecJson)
        if (tool != null) {
            _activeTool.value = tool
            if (project.taskPlanJson.isNotBlank()) {
                val plan = JsonHelper.jsonToTaskPlan(project.taskPlanJson)
                if (plan != null) _currentPlan.value = plan
            }
            _currentDestination.value = AppDestination.BUILDER
        }
    }

    fun deleteProject(id: Long) {
        viewModelScope.launch {
            projectDao.deleteById(id)
            val isAr = _currentLanguage.value == Language.ARABIC
            _userFeedbackMessage.value = if (isAr) "تم حذف المشروع" else "Project deleted"
        }
    }

    fun selectToolDirectly(tool: ToolSpec) {
        _activeTool.value = tool
        _currentDestination.value = AppDestination.BUILDER
    }

    // Task Planner Actions
    fun generatePlan(goal: String) {
        viewModelScope.launch {
            _isGeneratingPlan.value = true
            try {
                val plan = geminiService.generateTaskPlan(goal)
                _currentPlan.value = plan
            } finally {
                _isGeneratingPlan.value = false
            }
        }
    }

    fun toggleApproval(stepId: String) {
        val plan = _currentPlan.value ?: return
        val updatedSteps = plan.steps.map { step ->
            if (step.id == stepId) {
                val newApproved = !step.isApproved
                step.copy(
                    isApproved = newApproved,
                    status = if (newApproved) StepStatus.APPROVED else StepStatus.PENDING_APPROVAL
                )
            } else step
        }
        _currentPlan.value = plan.copy(steps = updatedSteps)
    }

    fun executeSingleStep(stepId: String) {
        val plan = _currentPlan.value ?: return
        viewModelScope.launch {
            // Mark step in progress
            _currentPlan.value = plan.copy(
                steps = plan.steps.map { if (it.id == stepId) it.copy(status = StepStatus.IN_PROGRESS) else it }
            )
            delay(1200) // Realistic execution simulation
            _currentPlan.value = plan.copy(
                steps = plan.steps.map {
                    if (it.id == stepId) {
                        it.copy(
                            status = StepStatus.COMPLETED,
                            executionLog = "Executed successfully at ${System.currentTimeMillis() % 10000}. Telemetry asserted."
                        )
                    } else it
                }
            )
        }
    }

    fun runFullPipeline() {
        val plan = _currentPlan.value ?: return
        viewModelScope.launch {
            _isExecutingPipeline.value = true
            try {
                val steps = plan.steps.toMutableList()
                for (i in steps.indices) {
                    val s = steps[i]
                    if (s.status != StepStatus.COMPLETED) {
                        if (s.requiresApproval && !s.isApproved) {
                            // Step requires operator approval, stop here
                            break
                        }
                        steps[i] = s.copy(status = StepStatus.IN_PROGRESS)
                        _currentPlan.value = plan.copy(steps = steps.toList())
                        delay(1000)
                        steps[i] = s.copy(
                            status = StepStatus.COMPLETED,
                            executionLog = "Step #${s.order} pipeline verification completed."
                        )
                        _currentPlan.value = plan.copy(steps = steps.toList())
                    }
                }
            } finally {
                _isExecutingPipeline.value = false
            }
        }
    }

    fun triggerToolAction(actionId: String, outcome: String) {
        _userFeedbackMessage.value = outcome
    }
}
