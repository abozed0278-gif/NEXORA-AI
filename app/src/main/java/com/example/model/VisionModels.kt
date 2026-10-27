package com.example.model

data class VisionPreset(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val iconName: String,
    val sampleImageRes: Int? = null,
    val analysis: VisionAnalysis
)

data class VisionAnalysis(
    val title: String,
    val summary: String,
    val detectedElements: List<DetectedElement>,
    val wireframeNodes: List<WireframeNode>,
    val extractedTelemetry: Map<String, String>,
    val colorTokens: List<ColorToken>,
    val suggestedTools: List<SuggestedToolAction>,
    val isSimulation: Boolean = false
)

data class DetectedElement(
    val label: String,
    val category: String,
    val confidence: Float,
    val boundingArea: String
)

data class WireframeNode(
    val level: Int,
    val nodeType: String, // "Container", "Header", "MetricGroup", "InteractiveGrid", "ControlPanel"
    val description: String
)

data class ColorToken(
    val name: String,
    val hex: String,
    val usageRole: String
)

data class SuggestedToolAction(
    val id: String,
    val title: String,
    val description: String,
    val targetToolType: String,
    val prompt: String
)

enum class ChatSender {
    USER,
    NEXORA_AI
}

data class ChatMessage(
    val id: String,
    val sender: ChatSender,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val appliedChangeSummary: String? = null
)
