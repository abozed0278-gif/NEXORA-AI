package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.PresetData
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun VisionScreen(
    language: Language,
    currentAnalysis: VisionAnalysis?,
    isAnalyzing: Boolean,
    onAnalyzePreset: (VisionPreset) -> Unit,
    onAnalyzeCustom: (String) -> Unit,
    onConvertToInteractiveTool: (SuggestedToolAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalizationManager.getStrings(language)
    val isArabic = language == Language.ARABIC

    var customConceptText by remember { mutableStateOf("") }
    var selectedPresetId by remember { mutableStateOf(PresetData.samplePresets.first().id) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("vision_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Section Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = strings.visionTitle,
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White
                )
                Text(
                    text = strings.visionSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NexoraSilverMuted
                )
            }
        }

        // Preset Cybernetic Blueprints Selector
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("preset_blueprints_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NexoraCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, NexoraBorderGlow)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isArabic) "اختر نموذج مخطط سيبراني لتحليله" else "SELECT CYBERNETIC BLUEPRINT TO DECOMPOSE",
                        style = MaterialTheme.typography.labelSmall,
                        color = NexoraCyan,
                        letterSpacing = 1.sp
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(PresetData.samplePresets) { preset ->
                            val isSelected = preset.id == selectedPresetId
                            Surface(
                                modifier = Modifier
                                    .width(220.dp)
                                    .clickable {
                                        selectedPresetId = preset.id
                                        onAnalyzePreset(preset)
                                    }
                                    .testTag("preset_${preset.id}"),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) NexoraDarkSurface else NexoraCardElevated,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) NexoraCyan else NexoraBorder
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = preset.category,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = NexoraViolet,
                                            fontSize = 9.sp
                                        )
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = NexoraCyan,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = preset.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = preset.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = NexoraSilverMuted,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Custom Blueprint / Wireframe Input
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("custom_vision_input_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NexoraCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, NexoraBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (isArabic) "أو أدخل وصف واجهة / لقطة مخصصة للمسح" else "OR ENTER CUSTOM UI / WIREFRAME CONCEPT TO SCAN",
                        style = MaterialTheme.typography.labelSmall,
                        color = NexoraSilverMuted
                    )

                    OutlinedTextField(
                        value = customConceptText,
                        onValueChange = { customConceptText = it },
                        placeholder = {
                            Text(
                                if (isArabic) "مثال: مخطط شاشة استهلاك الطاقة الشمسية مع بطاريات تخزين وجدول ساعات الذروة..."
                                else "e.g., Solar micro-grid telemetry dashboard with battery storage indicators and peak tariff matrix...",
                                color = NexoraSilverDark,
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_concept_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NexoraCyan,
                            unfocusedBorderColor = NexoraBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = NexoraSilver
                        ),
                        minLines = 2
                    )

                    Button(
                        onClick = {
                            if (customConceptText.isNotBlank()) {
                                onAnalyzeCustom(customConceptText)
                            }
                        },
                        enabled = !isAnalyzing && customConceptText.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("scan_custom_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NexoraCyan, contentColor = NexoraBlack)
                    ) {
                        if (isAnalyzing) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = NexoraBlack, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(strings.analyzing)
                        } else {
                            Icon(Icons.Default.DocumentScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (isArabic) "مسح وتحليل المخطط المخصص" else "Scan & Decompose Custom Concept",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Analysis Results Breakdown
        if (currentAnalysis != null) {
            item {
                Text(
                    text = strings.analysisResults,
                    style = MaterialTheme.typography.labelLarge,
                    color = NexoraSilverMuted,
                    letterSpacing = 1.sp
                )
            }

            // Summary Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("vision_summary_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NexoraDarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NexoraCyan)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NexoraCyan, modifier = Modifier.size(20.dp))
                            Text(
                                text = currentAnalysis.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                        }
                        Text(
                            text = currentAnalysis.summary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = NexoraSilver
                        )
                    }
                }
            }

            // Detected Objects & Elements
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = NexoraCardSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NexoraBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = strings.objectsDetected,
                            style = MaterialTheme.typography.titleMedium,
                            color = NexoraSilverLight
                        )
                        currentAnalysis.detectedElements.forEach { element ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(NexoraDarkSurface, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = element.label, color = Color.White, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                    Text(text = "${element.category} • ${element.boundingArea}", color = NexoraSilverMuted, style = MaterialTheme.typography.labelSmall)
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = NexoraCyanGlow
                                ) {
                                    Text(
                                        text = "${(element.confidence * 100).toInt()}% match",
                                        color = NexoraCyan,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Wireframe Hierarchy
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = NexoraCardSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NexoraBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = strings.wireframeHierarchy,
                            style = MaterialTheme.typography.titleMedium,
                            color = NexoraSilverLight
                        )
                        currentAnalysis.wireframeNodes.forEach { node ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(NexoraVioletGlow),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "${node.level}", color = NexoraViolet, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                                Text(
                                    text = "[${node.nodeType}] ${node.description}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NexoraSilver
                                )
                            }
                        }
                    }
                }
            }

            // Extracted Telemetry
            if (currentAnalysis.extractedTelemetry.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = NexoraCardSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = strings.extractedData,
                                style = MaterialTheme.typography.titleMedium,
                                color = NexoraSilverLight
                            )
                            currentAnalysis.extractedTelemetry.forEach { (key, value) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = key, style = MaterialTheme.typography.bodySmall, color = NexoraSilverMuted)
                                    Text(text = value, style = MaterialTheme.typography.labelLarge, color = NexoraCyan)
                                }
                                HorizontalDivider(color = NexoraBorder.copy(alpha = 0.4f))
                            }
                        }
                    }
                }
            }

            // Color Palette Tokens
            if (currentAnalysis.colorTokens.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = NexoraCardSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = strings.colorPalette,
                                style = MaterialTheme.typography.titleMedium,
                                color = NexoraSilverLight
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                currentAnalysis.colorTokens.forEach { token ->
                                    val tokenColor = try {
                                        Color(android.graphics.Color.parseColor(token.hex))
                                    } catch (e: Exception) {
                                        NexoraCyan
                                    }
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(tokenColor)
                                                .border(1.dp, NexoraBorderGlow, RoundedCornerShape(8.dp))
                                        )
                                        Text(text = token.name, style = MaterialTheme.typography.labelSmall, color = Color.White, fontSize = 9.sp, maxLines = 1)
                                        Text(text = token.hex, style = MaterialTheme.typography.labelSmall, color = NexoraSilverDark, fontSize = 8.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Suggested Interactive Tools
            item {
                Text(
                    text = strings.suggestedTools,
                    style = MaterialTheme.typography.labelLarge,
                    color = NexoraSilverMuted,
                    letterSpacing = 1.sp
                )
            }

            items(currentAnalysis.suggestedTools) { toolSuggestion ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("suggestion_${toolSuggestion.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NexoraCardSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NexoraVioletGlow)
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
                            Text(
                                text = toolSuggestion.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = NexoraVioletGlow
                            ) {
                                Text(
                                    text = toolSuggestion.targetToolType,
                                    color = NexoraViolet,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = toolSuggestion.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = NexoraSilverMuted
                        )

                        Button(
                            onClick = { onConvertToInteractiveTool(toolSuggestion) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("convert_btn_${toolSuggestion.id}"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NexoraViolet, contentColor = Color.White)
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.convertToTool,
                                fontWeight = FontWeight.Bold
                            )
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
