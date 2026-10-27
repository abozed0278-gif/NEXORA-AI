package com.example.model

data class ToolSpec(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val components: List<ToolComponent>,
    val sourcePrompt: String = "",
    val isSimulation: Boolean = false
)

sealed interface ToolComponent {
    val id: String
    val title: String
}

data class MetricGroupComponent(
    override val id: String,
    override val title: String,
    val metrics: List<MetricItem>
) : ToolComponent

data class MetricItem(
    val label: String,
    val value: String,
    val delta: String,
    val isPositive: Boolean = true,
    val icon: String = "bolt"
)

data class CalculatorComponent(
    override val id: String,
    override val title: String,
    val formulaType: String, // "ROI", "CLOUD_COST", "AI_TOKEN_BUDGET", "CYBER_SECURITY_RISK", "CUSTOM_MULTIPLIER"
    val inputs: List<CalcInputField>,
    val resultLabel: String,
    val resultUnit: String
) : ToolComponent

data class CalcInputField(
    val id: String,
    val label: String,
    val defaultValue: Double,
    val min: Double,
    val max: Double,
    val step: Double = 1.0,
    val unit: String = ""
)

data class FormComponent(
    override val id: String,
    override val title: String,
    val fields: List<FormFieldItem>
) : ToolComponent

data class FormFieldItem(
    val id: String,
    val label: String,
    val type: String, // "TEXT", "NUMBER", "TOGGLE", "DROPDOWN"
    val defaultValue: String,
    val options: List<String> = emptyList()
)

data class TableComponent(
    override val id: String,
    override val title: String,
    val columns: List<String>,
    val rows: List<List<String>>
) : ToolComponent

data class ChartComponent(
    override val id: String,
    override val title: String,
    val bars: List<ChartBarItem>,
    val maxValue: Float
) : ToolComponent

data class ChartBarItem(
    val label: String,
    val value: Float,
    val displayValue: String,
    val colorHex: String
)

data class ActionButtonsComponent(
    override val id: String,
    override val title: String,
    val actions: List<ActionItem>
) : ToolComponent

data class ActionItem(
    val id: String,
    val label: String,
    val iconName: String,
    val outcomeMessage: String,
    val isPrimary: Boolean = false
)
