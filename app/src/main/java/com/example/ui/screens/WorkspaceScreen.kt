package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Language
import com.example.model.LocalizationManager
import com.example.model.ToolSpec
import com.example.ui.theme.*

@Composable
fun WorkspaceScreen(
    language: Language,
    recentTools: List<ToolSpec>,
    onNavigateToVision: () -> Unit,
    onNavigateToBuilder: () -> Unit,
    onNavigateToPlanner: () -> Unit,
    onNavigateToProjects: () -> Unit,
    onSelectTool: (ToolSpec) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalizationManager.getStrings(language)
    val isArabic = language == Language.ARABIC

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("workspace_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Hero Card with Cybernetic Graphic
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("workspace_hero_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NexoraCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, NexoraCyanGlow)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Try to display the generated hero banner if available
                    Image(
                        painter = painterResource(id = R.drawable.nexora_hero_banner_1793056637622),
                        contentDescription = "NEXORA AI Interface",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )

                    // Gradient overlay to guarantee perfect text contrast
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0x7708090C),
                                        Color(0xDD08090C),
                                        NexoraCardSurface
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = NexoraCyanGlow,
                            border = androidx.compose.foundation.BorderStroke(1.dp, NexoraCyan)
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
                                        .background(NexoraCyan)
                                )
                                Text(
                                    text = if (isArabic) "نظام الذكاء السيبراني v3.0" else "NEXORA NEURAL KERNEL 3.0",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NexoraCyan,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Text(
                            text = strings.appName,
                            style = MaterialTheme.typography.displaySmall,
                            color = Color.White
                        )

                        Text(
                            text = strings.appSubtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = NexoraSilver
                        )
                    }
                }
            }
        }

        // Section Title: Core Capabilities
        item {
            Text(
                text = if (isArabic) "الوحدات السيبرانية الأساسية" else "CORE ARCHITECTURAL MODULES",
                style = MaterialTheme.typography.labelLarge,
                color = NexoraSilverMuted,
                letterSpacing = 1.sp
            )
        }

        // 2x2 Feature Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FeatureLaunchCard(
                        title = strings.navVision,
                        subtitle = if (isArabic) "تحليل المخططات والصور إلى مكونات" else "Decompose blueprints & images into UI nodes",
                        icon = Icons.Default.Visibility,
                        accentColor = NexoraCyan,
                        onClick = onNavigateToVision,
                        modifier = Modifier.weight(1f),
                        testTag = "launch_vision_card"
                    )

                    FeatureLaunchCard(
                        title = strings.navBuilder,
                        subtitle = if (isArabic) "توليد أدوات وحاسبات تفاعلية بأمان" else "Synthesize interactive tools & calculators",
                        icon = Icons.Default.Build,
                        accentColor = NexoraViolet,
                        onClick = onNavigateToBuilder,
                        modifier = Modifier.weight(1f),
                        testTag = "launch_builder_card"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FeatureLaunchCard(
                        title = strings.navPlanner,
                        subtitle = if (isArabic) "خطط مهام مع نقاط اعتماد المشرف" else "Guided workflows with approval gates",
                        icon = Icons.Default.Rule,
                        accentColor = NexoraEmerald,
                        onClick = onNavigateToPlanner,
                        modifier = Modifier.weight(1f),
                        testTag = "launch_planner_card"
                    )

                    FeatureLaunchCard(
                        title = strings.navProjects,
                        subtitle = if (isArabic) "إدارة وتعديل النماذج الأولية المحفوظة" else "Review, load & export saved prototypes",
                        icon = Icons.Default.Folder,
                        accentColor = NexoraAmber,
                        onClick = onNavigateToProjects,
                        modifier = Modifier.weight(1f),
                        testTag = "launch_projects_card"
                    )
                }
            }
        }

        // Quick Prototypes Spotlight
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isArabic) "النماذج الأولية الجاهزة للتجربة" else "ACTIVE INTERACTIVE PROTOTYPES",
                    style = MaterialTheme.typography.labelLarge,
                    color = NexoraSilverMuted,
                    letterSpacing = 1.sp
                )
                TextButton(onClick = onNavigateToProjects) {
                    Text(
                        text = if (isArabic) "عرض الكل" else "View All",
                        color = NexoraCyan,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        items(recentTools) { tool ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectTool(tool) }
                    .testTag("recent_tool_${tool.id}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = NexoraCardSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, NexoraBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(NexoraDarkSurface)
                            .border(1.dp, NexoraCyanGlow, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = NexoraCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tool.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tool.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = NexoraSilverMuted,
                            maxLines = 2
                        )
                    }

                    Icon(
                        imageVector = if (isArabic) Icons.Default.ArrowBackIos else Icons.Default.ArrowForwardIos,
                        contentDescription = "Open",
                        tint = NexoraSilverDark,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun FeatureLaunchCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NexoraCardSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.15f))
                    .border(1.dp, accentColor, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = NexoraSilverMuted,
                minLines = 2,
                maxLines = 2
            )
        }
    }
}
