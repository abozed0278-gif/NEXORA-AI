package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ProjectEntity
import com.example.model.Language
import com.example.model.LocalizationManager
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProjectsScreen(
    language: Language,
    projects: List<ProjectEntity>,
    onOpenProject: (ProjectEntity) -> Unit,
    onDeleteProject: (Long) -> Unit,
    onNavigateToBuilder: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalizationManager.getStrings(language)
    val isArabic = language == Language.ARABIC

    var searchQuery by remember { mutableStateOf("") }
    var projectToExport by remember { mutableStateOf<ProjectEntity?>(null) }

    val filteredProjects = remember(projects, searchQuery) {
        if (searchQuery.isBlank()) projects
        else projects.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.category.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("projects_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = strings.projectsTitle,
                        style = MaterialTheme.typography.displaySmall,
                        color = Color.White
                    )
                    Text(
                        text = "${projects.size} ${if (isArabic) "مشاريع محفوظة محلياً" else "Prototypes Saved Locally"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NexoraSilverMuted
                    )
                }

                Button(
                    onClick = onNavigateToBuilder,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NexoraCyan, contentColor = NexoraBlack),
                    modifier = Modifier.testTag("create_new_project_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(strings.newProject, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Search Input
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        if (isArabic) "بحث في النماذج المحفوظة..." else "Search saved workspace projects...",
                        color = NexoraSilverDark
                    )
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NexoraCyan) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("projects_search_input"),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NexoraCyan,
                    unfocusedBorderColor = NexoraBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = NexoraSilver
                ),
                singleLine = true
            )
        }

        // Empty State
        if (filteredProjects.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("projects_empty_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NexoraCardSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NexoraBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(NexoraCyanGlow, RoundedCornerShape(16.dp))
                                .border(1.dp, NexoraCyan, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.FolderOpen, contentDescription = null, tint = NexoraCyan, modifier = Modifier.size(28.dp))
                        }
                        Text(
                            text = strings.noProjectsYet,
                            style = MaterialTheme.typography.bodyMedium,
                            color = NexoraSilver,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Button(
                            onClick = onNavigateToBuilder,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NexoraCyan, contentColor = NexoraBlack)
                        ) {
                            Text(if (isArabic) "ابدأ بإنشاء أداة جديدة" else "Create First Prototype", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            items(filteredProjects, key = { it.id }) { project ->
                val dateStr = remember(project.createdAt) {
                    SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(project.createdAt))
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("project_item_${project.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = NexoraCardSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NexoraBorder)
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = project.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White
                                )
                                Text(
                                    text = "${project.category} • $dateStr",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NexoraSilverDark
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = { projectToExport = project },
                                    modifier = Modifier.testTag("export_project_${project.id}")
                                ) {
                                    Icon(Icons.Default.Code, contentDescription = "Export Spec", tint = NexoraCyan)
                                }
                                IconButton(
                                    onClick = { onDeleteProject(project.id) },
                                    modifier = Modifier.testTag("delete_project_${project.id}")
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = NexoraRose)
                                }
                            }
                        }

                        if (project.description.isNotBlank()) {
                            Text(
                                text = project.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = NexoraSilverMuted,
                                maxLines = 2
                            )
                        }

                        Button(
                            onClick = { onOpenProject(project) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("open_project_${project.id}"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NexoraCardElevated, contentColor = NexoraCyan)
                        ) {
                            Icon(Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.openProject, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Export JSON Spec Dialog
    projectToExport?.let { proj ->
        AlertDialog(
            onDismissRequest = { projectToExport = null },
            containerColor = NexoraCardElevated,
            title = {
                Text(
                    text = "${strings.exportSpec}: ${proj.title}",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isArabic) "مواصفة المكونات الآمنة بصيغة JSON:" else "Declarative Safe Component Specification (JSON):",
                        color = NexoraSilverMuted,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        color = NexoraBlack,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraBorder)
                    ) {
                        LazyColumn(modifier = Modifier.padding(10.dp)) {
                            item {
                                Text(
                                    text = proj.toolSpecJson.ifBlank { "{\n  \"error\": \"No spec stored\"\n}" },
                                    color = NexoraCyan,
                                    fontSize = 11.sp,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { projectToExport = null },
                    colors = ButtonDefaults.textButtonColors(contentColor = NexoraCyan)
                ) {
                    Text("Close")
                }
            }
        )
    }
}
