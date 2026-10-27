package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun InteractiveToolRenderer(
    tool: ToolSpec,
    language: Language,
    modifier: Modifier = Modifier,
    onActionTriggered: (String, String) -> Unit = { _, _ -> }
) {
    val isArabic = language == Language.ARABIC

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("interactive_tool_container"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tool Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("tool_header_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = NexoraCardSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, NexoraBorderGlow)
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tool.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = NexoraVioletGlow,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraViolet)
                    ) {
                        Text(
                            text = tool.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = NexoraViolet,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                if (tool.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = tool.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = NexoraSilverMuted
                    )
                }

                if (tool.isSimulation) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Simulation Notice",
                            tint = NexoraAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isArabic) "نموذج محاكاة محلي (المكونات آمنة وتفاعلية)" else "Local Simulation Prototype (Safe, Reactive Predefined Components)",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexoraAmber
                        )
                    }
                }
            }
        }

        // Render each component
        for (component in tool.components) {
            when (component) {
                is MetricGroupComponent -> {
                    MetricGroupRenderer(component)
                }
                is CalculatorComponent -> {
                    InteractiveCalculatorRenderer(component, isArabic)
                }
                is FormComponent -> {
                    InteractiveFormRenderer(component, isArabic, onActionTriggered)
                }
                is TableComponent -> {
                    InteractiveTableRenderer(component, isArabic)
                }
                is ChartComponent -> {
                    InteractiveChartRenderer(component)
                }
                is ActionButtonsComponent -> {
                    InteractiveActionsRenderer(component, onActionTriggered)
                }
            }
        }
    }
}

@Composable
fun MetricGroupRenderer(component: MetricGroupComponent) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = component.title,
            style = MaterialTheme.typography.titleMedium,
            color = NexoraSilverLight
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            component.metrics.take(4).forEach { metric ->
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("metric_card_${metric.label}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = NexoraDarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NexoraBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = metric.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = NexoraSilverMuted,
                            maxLines = 1
                        )
                        Text(
                            text = metric.value,
                            style = MaterialTheme.typography.titleMedium,
                            color = NexoraCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (metric.isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = if (metric.isPositive) NexoraEmerald else NexoraRose,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = metric.delta,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (metric.isPositive) NexoraEmerald else NexoraRose,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InteractiveCalculatorRenderer(
    component: CalculatorComponent,
    isArabic: Boolean
) {
    // Reactive local state for inputs
    val inputValues = remember(component.inputs) {
        mutableStateMapOf<String, Double>().apply {
            component.inputs.forEach { put(it.id, it.defaultValue) }
        }
    }

    // Dynamic computed total
    val computedResult = remember(inputValues.toMap(), component.formulaType) {
        when (component.formulaType) {
            "CLOUD_COST" -> {
                val nodes = inputValues["nodes"] ?: 8.0
                val cpu = inputValues["cpu_per_node"] ?: 8.0
                val ram = inputValues["ram_per_node"] ?: 32.0
                val storage = inputValues["storage_tb"] ?: 4.0
                (nodes * cpu * 16.5) + (nodes * ram * 2.2) + (storage * 85.0)
            }
            "AI_TOKEN_BUDGET" -> {
                val dau = inputValues["dau"] ?: 5000.0
                val prompts = inputValues["prompts_per_user"] ?: 6.0
                val inTok = inputValues["input_tokens"] ?: 800.0
                val outTok = inputValues["output_tokens"] ?: 400.0
                val monthlyQueries = dau * prompts * 30.0
                // Gemini 2.5 Flash pricing: $0.10 / 1M input, $0.40 / 1M output
                (monthlyQueries * ((inTok * 0.00000010) + (outTok * 0.00000040)))
            }
            "ROI" -> {
                val principal = inputValues["principal"] ?: 10000.0
                val rate = inputValues["annual_rate"] ?: 12.0
                val months = inputValues["duration_months"] ?: 12.0
                principal * ((rate / 100.0) * (months / 12.0))
            }
            else -> {
                // Multiplier fallback
                var product = 1.0
                inputValues.values.forEach { product *= (it / 10.0).coerceAtLeast(1.0) }
                product * 250.0
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("calculator_${component.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NexoraCardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraBorderGlow)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = component.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = NexoraSilverLight
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NexoraCyanGlow
                ) {
                    Text(
                        text = if (isArabic) "حساب فوري مباشر" else "LIVE COMPUTATION",
                        style = MaterialTheme.typography.labelSmall,
                        color = NexoraCyan,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            // Input Sliders
            component.inputs.forEach { inputField ->
                val currentValue = inputValues[inputField.id] ?: inputField.defaultValue
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = inputField.label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = NexoraSilver
                        )
                        Text(
                            text = "${if (currentValue % 1.0 == 0.0) currentValue.toInt().toString() else "%.1f".format(currentValue)} ${inputField.unit}",
                            style = MaterialTheme.typography.labelLarge,
                            color = NexoraCyan
                        )
                    }
                    Slider(
                        value = currentValue.toFloat(),
                        onValueChange = { newVal ->
                            inputValues[inputField.id] = newVal.toDouble()
                        },
                        valueRange = inputField.min.toFloat()..inputField.max.toFloat(),
                        colors = SliderDefaults.colors(
                            thumbColor = NexoraCyan,
                            activeTrackColor = NexoraCyan,
                            inactiveTrackColor = NexoraBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("slider_${inputField.id}")
                    )
                }
            }

            // Dynamic Result Output Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = NexoraDarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, NexoraCyan)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = component.resultLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = NexoraSilverMuted
                        )
                        Text(
                            text = "${NumberFormat.getNumberInstance(Locale.US).format(computedResult.toLong())} ${component.resultUnit}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = NexoraCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = "Calculator Output",
                        tint = NexoraCyan,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun InteractiveFormRenderer(
    component: FormComponent,
    isArabic: Boolean,
    onActionTriggered: (String, String) -> Unit
) {
    val formState = remember(component.fields) {
        mutableStateMapOf<String, String>().apply {
            component.fields.forEach { put(it.id, it.defaultValue) }
        }
    }
    var submittedNotice by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("form_${component.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NexoraCardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = component.title,
                style = MaterialTheme.typography.titleMedium,
                color = NexoraSilverLight
            )

            component.fields.forEach { field ->
                when (field.type) {
                    "TOGGLE" -> {
                        val isChecked = formState[field.id]?.toBooleanStrictOrNull() ?: false
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = field.label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = NexoraSilver
                            )
                            Switch(
                                checked = isChecked,
                                onCheckedChange = { formState[field.id] = it.toString() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NexoraBlack,
                                    checkedTrackColor = NexoraCyan,
                                    uncheckedThumbColor = NexoraSilverMuted,
                                    uncheckedTrackColor = NexoraBorder
                                )
                            )
                        }
                    }
                    "DROPDOWN" -> {
                        var expanded by remember { mutableStateOf(false) }
                        val selected = formState[field.id] ?: field.defaultValue
                        Column {
                            Text(
                                text = field.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = NexoraSilverMuted
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expanded = true },
                                shape = RoundedCornerShape(8.dp),
                                color = NexoraDarkSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, NexoraBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = selected, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = NexoraCyan)
                                }
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(NexoraCardElevated)
                            ) {
                                field.options.forEach { opt ->
                                    DropdownMenuItem(
                                        text = { Text(opt, color = Color.White) },
                                        onClick = {
                                            formState[field.id] = opt
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    else -> {
                        val txt = formState[field.id] ?: field.defaultValue
                        OutlinedTextField(
                            value = txt,
                            onValueChange = { formState[field.id] = it },
                            label = { Text(field.label, color = NexoraSilverMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NexoraCyan,
                                unfocusedBorderColor = NexoraBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = NexoraSilver
                            )
                        )
                    }
                }
            }

            Button(
                onClick = {
                    val summary = if (isArabic) "تم حفظ وتطبيق معاملات النموذج بنجاح" else "Form parameters applied successfully to active session."
                    submittedNotice = summary
                    onActionTriggered("form_submit", summary)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("form_submit_btn"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NexoraCyan, contentColor = NexoraBlack)
            ) {
                Text(
                    text = if (isArabic) "تطبيق المعاملات" else "Apply Parameters",
                    fontWeight = FontWeight.Bold
                )
            }

            AnimatedVisibility(visible = submittedNotice != null) {
                submittedNotice?.let { notice ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0x2210B981),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraEmerald)
                    ) {
                        Text(
                            text = notice,
                            style = MaterialTheme.typography.bodySmall,
                            color = NexoraEmerald,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InteractiveTableRenderer(
    component: TableComponent,
    isArabic: Boolean
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredRows = remember(component.rows, searchQuery) {
        if (searchQuery.isBlank()) component.rows
        else component.rows.filter { row -> row.any { it.contains(searchQuery, ignoreCase = true) } }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("table_${component.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NexoraCardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraBorder)
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
                    text = component.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = NexoraSilverLight
                )
                Text(
                    text = "${filteredRows.size} ${if (isArabic) "سجلات" else "records"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = NexoraCyan
                )
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(if (isArabic) "بحث في السجلات..." else "Filter table rows...", color = NexoraSilverDark) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NexoraCyan) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NexoraCyan,
                    unfocusedBorderColor = NexoraBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = NexoraSilver
                ),
                singleLine = true
            )

            // Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NexoraCardElevated, RoundedCornerShape(6.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                component.columns.forEach { col ->
                    Text(
                        text = col,
                        style = MaterialTheme.typography.labelSmall,
                        color = NexoraSilverMuted,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Table Rows
            filteredRows.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    row.forEachIndexed { idx, cell ->
                        val isStatus = idx == row.size - 1
                        if (isStatus && (cell == "RUNNING" || cell == "OPTIMAL")) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0x2210B981),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = cell,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NexoraEmerald,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        } else {
                            Text(
                                text = cell,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                HorizontalDivider(color = NexoraBorder.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
fun InteractiveChartRenderer(component: ChartComponent) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("chart_${component.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NexoraCardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = component.title,
                style = MaterialTheme.typography.titleMedium,
                color = NexoraSilverLight
            )

            component.bars.forEach { bar ->
                val progress = (bar.value / component.maxValue).coerceIn(0f, 1f)
                val animatedProgress by animateFloatAsState(
                    targetValue = progress,
                    animationSpec = tween(durationMillis = 600),
                    label = "bar_progress"
                )

                val barColor = try {
                    Color(android.graphics.Color.parseColor(bar.colorHex))
                } catch (e: Exception) {
                    NexoraCyan
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = bar.label, style = MaterialTheme.typography.bodyMedium, color = NexoraSilver)
                        Text(text = bar.displayValue, style = MaterialTheme.typography.labelLarge, color = barColor)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(NexoraDarkSurface)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedProgress)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(5.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(barColor.copy(alpha = 0.7f), barColor)
                                    )
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InteractiveActionsRenderer(
    component: ActionButtonsComponent,
    onActionTriggered: (String, String) -> Unit
) {
    var lastExecutedAction by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("actions_${component.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NexoraCardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraBorderGlow)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = component.title,
                style = MaterialTheme.typography.titleMedium,
                color = NexoraSilverLight
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                component.actions.forEach { action ->
                    if (action.isPrimary) {
                        Button(
                            onClick = {
                                lastExecutedAction = action.outcomeMessage
                                onActionTriggered(action.id, action.outcomeMessage)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("action_btn_${action.id}"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NexoraCyan, contentColor = NexoraBlack)
                        ) {
                            Text(action.label, fontWeight = FontWeight.Bold, maxLines = 1, fontSize = 13.sp)
                        }
                    } else {
                        OutlinedButton(
                            onClick = {
                                lastExecutedAction = action.outcomeMessage
                                onActionTriggered(action.id, action.outcomeMessage)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("action_btn_${action.id}"),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NexoraBorderGlow),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NexoraSilverLight)
                        ) {
                            Text(action.label, maxLines = 1, fontSize = 13.sp)
                        }
                    }
                }
            }

            AnimatedVisibility(visible = lastExecutedAction != null) {
                lastExecutedAction?.let { msg ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0x2200F0FF),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraCyan)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NexoraCyan, modifier = Modifier.size(16.dp))
                            Text(text = msg, style = MaterialTheme.typography.bodySmall, color = NexoraCyan)
                        }
                    }
                }
            }
        }
    }
}
