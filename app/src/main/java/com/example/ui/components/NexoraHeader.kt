package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Shield
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
import com.example.model.Language
import com.example.model.LocalizationManager
import com.example.ui.theme.*

@Composable
fun NexoraHeader(
    currentLanguage: Language,
    isGeminiActive: Boolean,
    onToggleLanguage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalizationManager.getStrings(currentLanguage)
    var showEngineInfoDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("nexora_header"),
        color = NexoraBlack,
        tonalElevation = 6.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand Title with Glowing Accent
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.testTag("brand_logo_title")
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(NexoraCyanGlow)
                            .border(1.dp, NexoraCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "NX",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexoraCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column {
                        Text(
                            text = strings.appName,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "WORKSPACE 3.0",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexoraSilverMuted,
                            fontSize = 9.sp
                        )
                    }
                }

                // Controls: Engine Status & Language Switcher
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Engine Status Pill
                    Surface(
                        modifier = Modifier
                            .clickable { showEngineInfoDialog = true }
                            .testTag("engine_status_pill"),
                        shape = RoundedCornerShape(16.dp),
                        color = if (isGeminiActive) NexoraCyanGlow else Color(0x22F59E0B),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isGeminiActive) NexoraCyan else NexoraAmber
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isGeminiActive) NexoraCyan else NexoraAmber)
                            )
                            Text(
                                text = if (isGeminiActive) strings.statusGeminiCloud else strings.statusOfflineSimulation,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isGeminiActive) NexoraCyan else NexoraAmber,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Language Toggle
                    Surface(
                        modifier = Modifier
                            .clickable { onToggleLanguage() }
                            .testTag("language_toggle_btn"),
                        shape = RoundedCornerShape(16.dp),
                        color = NexoraCardElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraBorderGlow)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Language",
                                tint = NexoraViolet,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = strings.languageToggle,
                                style = MaterialTheme.typography.labelSmall,
                                color = NexoraSilverLight,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = NexoraBorder, thickness = 1.dp)
        }
    }

    if (showEngineInfoDialog) {
        AlertDialog(
            onDismissRequest = { showEngineInfoDialog = false },
            containerColor = NexoraCardElevated,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (isGeminiActive) Icons.Default.Shield else Icons.Default.Info,
                        contentDescription = null,
                        tint = if (isGeminiActive) NexoraCyan else NexoraAmber
                    )
                    Text(
                        text = if (isGeminiActive) "Gemini 2.5 Cloud Integration" else "NEXORA Neural Engine (Offline Mode)",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (isGeminiActive) {
                        Text(
                            text = "NEXORA AI is securely connected to the Gemini 2.5 Flash API via BuildConfig. Responses, wireframe synthesis, and tool adaptations are powered live by Google AI.",
                            color = NexoraSilver,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Text(
                            text = "NEXORA AI is currently running in local offline simulation mode with zero network latency. All tool prototypes, calculators, and task workflows execute through pre-validated declarative components.",
                            color = NexoraSilver,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Setup note: To activate live Gemini 2.5 Cloud intelligence, set your GEMINI_API_KEY in the AI Studio Secrets panel.",
                            color = NexoraAmber,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showEngineInfoDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = NexoraCyan)
                ) {
                    Text("OK")
                }
            }
        )
    }
}
