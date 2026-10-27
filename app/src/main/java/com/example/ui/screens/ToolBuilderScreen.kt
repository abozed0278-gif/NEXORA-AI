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
import com.example.model.*
import com.example.ui.components.InteractiveToolRenderer
import com.example.ui.theme.*

@Composable
fun ToolBuilderScreen(
    language: Language,
    activeTool: ToolSpec?,
    isGenerating: Boolean,
    chatMessages: List<ChatMessage>,
    onGenerateFromPrompt: (String) -> Unit,
    onSendChatInstruction: (String) -> Unit,
    onSaveToolToProjects: () -> Unit,
    onActionTriggered: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalizationManager.getStrings(language)
    val isArabic = language == Language.ARABIC

    var promptInput by remember { mutableStateOf("") }
    var chatInstructionInput by remember { mutableStateOf("") }
    var showChatPanel by remember { mutableStateOf(false) }

    val quickPrompts = listOf(
        "Cloud Infrastructure Cost Calculator with node slider and monthly burn" to "Cloud Cost",
        "SaaS LTV & CAC Scenario Forecaster with churn and marketing budget" to "SaaS LTV",
        "Cybersecurity Threat Risk Matrix with severity table and mitigation actions" to "Security Risk",
        "Industrial Solar Micro-Grid Peak Shaving Forecaster" to "Energy Grid"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("tool_builder_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Title
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = strings.builderTitle,
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White
                )
                Text(
                    text = strings.builderSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NexoraSilverMuted
                )
            }
        }

        // Natural-language Idea Input Box
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("builder_input_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NexoraCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, NexoraBorderGlow)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isArabic) "اكتب فكرة أداتك التفاعلية" else "DESCRIBE YOUR INTERACTIVE TOOL",
                        style = MaterialTheme.typography.labelSmall,
                        color = NexoraCyan,
                        letterSpacing = 1.sp
                    )

                    OutlinedTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        placeholder = {
                            Text(
                                strings.ideaInputPlaceholder,
                                color = NexoraSilverDark,
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("builder_prompt_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NexoraCyan,
                            unfocusedBorderColor = NexoraBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = NexoraSilver
                        ),
                        minLines = 3
                    )

                    // Quick Neural Templates
                    Text(
                        text = strings.quickPresets,
                        style = MaterialTheme.typography.labelSmall,
                        color = NexoraSilverMuted
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(quickPrompts) { (fullPrompt, tag) ->
                            Surface(
                                modifier = Modifier
                                    .clickable {
                                        promptInput = fullPrompt
                                        onGenerateFromPrompt(fullPrompt)
                                    }
                                    .testTag("quick_prompt_$tag"),
                                shape = RoundedCornerShape(16.dp),
                                color = NexoraCardElevated,
                                border = androidx.compose.foundation.BorderStroke(1.dp, NexoraBorder)
                            ) {
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NexoraSilverLight,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    // Synthesize Button
                    Button(
                        onClick = {
                            if (promptInput.isNotBlank()) {
                                onGenerateFromPrompt(promptInput)
                            }
                        },
                        enabled = !isGenerating && promptInput.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("synthesize_tool_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NexoraCyan, contentColor = NexoraBlack)
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = NexoraBlack, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(strings.generatingTool)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                strings.generateToolBtn,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Action Toolbar: Save to Workspace & Toggle AI Chat
        if (activeTool != null) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onSaveToolToProjects,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_project_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NexoraEmerald, contentColor = Color.White)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.saveProject, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { showChatPanel = !showChatPanel },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("toggle_chat_refine_btn"),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraViolet),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NexoraViolet)
                    ) {
                        Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            if (showChatPanel) (if (isArabic) "إغلاق المحادثة" else "Close AI Chat") else strings.navChat,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // AI Chat Refinement Panel
            item {
                AnimatedVisibility(visible = showChatPanel) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("chat_refine_panel"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = NexoraDarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraVioletGlow)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Psychology, contentDescription = null, tint = NexoraViolet)
                                    Text(
                                        text = if (isArabic) "تطوير النموذج بالذكاء الاصطناعي" else "AI Natural-Language Refinement",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White
                                    )
                                }
                            }

                            // Chat History
                            chatMessages.takeLast(4).forEach { msg ->
                                val isUser = msg.sender == ChatSender.USER
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isUser) NexoraVioletGlow else NexoraCardElevated,
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isUser) NexoraViolet else NexoraBorder
                                        ),
                                        modifier = Modifier.widthIn(max = 280.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text(
                                                text = if (isUser) (if (isArabic) "أنت" else "You") else "NEXORA AI",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (isUser) NexoraViolet else NexoraCyan,
                                                fontSize = 9.sp
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = msg.content,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }

                            // Chat Input
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = chatInstructionInput,
                                    onValueChange = { chatInstructionInput = it },
                                    placeholder = {
                                        Text(
                                            if (isArabic) "اطلب تعديل: 'أضف منزلق لنسبة الضريبة'..." else "e.g., 'Add a tax rate slider'...",
                                            color = NexoraSilverDark,
                                            fontSize = 12.sp
                                        )
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("chat_instruction_input"),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NexoraViolet,
                                        unfocusedBorderColor = NexoraBorder,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = NexoraSilver
                                    ),
                                    singleLine = true
                                )

                                IconButton(
                                    onClick = {
                                        if (chatInstructionInput.isNotBlank()) {
                                            onSendChatInstruction(chatInstructionInput)
                                            chatInstructionInput = ""
                                        }
                                    },
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(NexoraViolet, RoundedCornerShape(8.dp))
                                        .testTag("send_chat_instruction_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = "Send",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Interactive Tool Preview Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.interactivePreview,
                        style = MaterialTheme.typography.labelLarge,
                        color = NexoraCyan,
                        letterSpacing = 1.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NexoraCyanGlow
                    ) {
                        Text(
                            text = if (isArabic) "جاهز للتفاعل الفوري" else "ACTIVE SANDBOX",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexoraCyan,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Render the live reactive tool
            item {
                InteractiveToolRenderer(
                    tool = activeTool,
                    language = language,
                    onActionTriggered = onActionTriggered
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
