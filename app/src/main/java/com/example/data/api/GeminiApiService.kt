package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import com.example.data.JsonHelper
import com.example.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiApiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .build()

    fun isGeminiConfigured(): Boolean {
        val key = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }
        return key.isNotBlank() && key != "MY_GEMINI_API_KEY" && !key.startsWith("YOUR_")
    }

    suspend fun analyzeVisionImage(
        imageDescription: String,
        isCustomUpload: Boolean = false
    ): VisionAnalysis = withContext(Dispatchers.IO) {
        if (!isGeminiConfigured()) {
            return@withContext generateOfflineVisionAnalysis(imageDescription, isCustomUpload)
        }

        try {
            val key = BuildConfig.GEMINI_API_KEY
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$key"

            val prompt = """
                Analyze this technical blueprint/wireframe concept: "$imageDescription".
                Return ONLY valid JSON matching this exact structure:
                {
                   "title": "Short title",
                   "summary": "Technical summary of what was detected",
                   "detectedElements": [
                      {"label": "Element name", "category": "Category", "confidence": 0.95, "boundingArea": "Location"}
                   ],
                   "wireframeNodes": [
                      {"level": 1, "nodeType": "Container", "description": "Description of layout node"}
                   ],
                   "extractedTelemetry": {
                      "Key Metric": "Value"
                   },
                   "colorTokens": [
                      {"name": "Color Name", "hex": "#00F0FF", "usageRole": "Usage"}
                   ],
                   "suggestedTools": [
                      {"id": "t1", "title": "Tool title", "description": "Description", "targetToolType": "CALCULATOR", "prompt": "Prompt to generate it"}
                   ]
                }
            """.trimIndent()

            val requestJson = JSONObject().apply {
                val contents = JSONArray().apply {
                    val partObj = JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        })
                    }
                    put(partObj)
                }
                put("contents", contents)
            }

            val body = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder().url(url).post(body).build()

            val response = client.newCall(request).execute()
            val respStr = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val candidateText = parseGeminiResponse(respStr)
                parseVisionAnalysisFromJson(candidateText, isSimulation = false)
                    ?: generateOfflineVisionAnalysis(imageDescription, isCustomUpload)
            } else {
                Log.w("GeminiApiService", "Gemini HTTP ${response.code}: $respStr")
                generateOfflineVisionAnalysis(imageDescription, isCustomUpload)
            }
        } catch (e: Exception) {
            Log.e("GeminiApiService", "Gemini call error", e)
            generateOfflineVisionAnalysis(imageDescription, isCustomUpload)
        }
    }

    suspend fun generateToolSpec(prompt: String): ToolSpec = withContext(Dispatchers.IO) {
        if (!isGeminiConfigured()) {
            return@withContext generateOfflineToolSpec(prompt)
        }

        try {
            val key = BuildConfig.GEMINI_API_KEY
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$key"

            val systemInstruction = """
                You are NEXORA AI tool synthesis compiler.
                The user wants an interactive tool for: "$prompt".
                You must compose a declarative prototype using ONLY these safe predefined component types:
                - METRIC_GROUP (metrics: [{label, value, delta, isPositive, icon}])
                - CALCULATOR (formulaType: "CUSTOM_MULTIPLIER" or "ROI" or "CLOUD_COST", inputs: [{id, label, defaultValue, min, max, step, unit}], resultLabel, resultUnit)
                - FORM (fields: [{id, label, type: "TEXT"|"NUMBER"|"TOGGLE"|"DROPDOWN", defaultValue, options: []}])
                - TABLE (columns: [], rows: [[]])
                - CHART (bars: [{label, value, displayValue, colorHex}], maxValue)
                - ACTIONS (actions: [{id, label, iconName, outcomeMessage, isPrimary}])

                Return ONLY valid JSON matching this format:
                {
                   "id": "tool_${System.currentTimeMillis()}",
                   "name": "Concise Tool Name",
                   "description": "Tool description",
                   "category": "Category",
                   "sourcePrompt": "$prompt",
                   "isSimulation": false,
                   "components": [ ... ]
                }
            """.trimIndent()

            val requestJson = JSONObject().apply {
                val contents = JSONArray().apply {
                    val partObj = JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", systemInstruction) })
                        })
                    }
                    put(partObj)
                }
                put("contents", contents)
            }

            val body = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder().url(url).post(body).build()

            val response = client.newCall(request).execute()
            val respStr = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val candidateText = parseGeminiResponse(respStr)
                JsonHelper.jsonToToolSpec(cleanJsonMarkdown(candidateText))
                    ?: generateOfflineToolSpec(prompt)
            } else {
                generateOfflineToolSpec(prompt)
            }
        } catch (e: Exception) {
            generateOfflineToolSpec(prompt)
        }
    }

    suspend fun generateTaskPlan(prompt: String): TaskPlan = withContext(Dispatchers.IO) {
        if (!isGeminiConfigured()) {
            return@withContext generateOfflineTaskPlan(prompt)
        }

        try {
            val key = BuildConfig.GEMINI_API_KEY
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$key"

            val systemInstruction = """
                Generate a guided multi-step task execution plan for: "$prompt".
                Include approval checkpoints (requiresApproval: true) for risky or high-impact actions.
                Return ONLY valid JSON matching:
                {
                   "id": "plan_${System.currentTimeMillis()}",
                   "title": "Action Plan Title",
                   "objective": "High-level goal statement",
                   "overallStatus": "READY_FOR_APPROVAL",
                   "isSimulation": false,
                   "steps": [
                      {
                         "id": "s_1",
                         "order": 1,
                         "title": "Step 1 title",
                         "description": "Step 1 description",
                         "requiresApproval": false,
                         "isApproved": true,
                         "status": "APPROVED",
                         "estimatedDurationSec": 2,
                         "executionLog": "Ready to execute"
                      },
                      {
                         "id": "s_2",
                         "order": 2,
                         "title": "Step 2 title",
                         "description": "Step 2 description",
                         "requiresApproval": true,
                         "isApproved": false,
                         "status": "PENDING_APPROVAL",
                         "estimatedDurationSec": 4,
                         "executionLog": "Awaiting approval checkpoint"
                      }
                   ]
                }
            """.trimIndent()

            val requestJson = JSONObject().apply {
                val contents = JSONArray().apply {
                    val partObj = JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", systemInstruction) })
                        })
                    }
                    put(partObj)
                }
                put("contents", contents)
            }

            val body = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder().url(url).post(body).build()

            val response = client.newCall(request).execute()
            val respStr = response.body?.string() ?: ""

            if (response.isSuccessful) {
                val candidateText = parseGeminiResponse(respStr)
                JsonHelper.jsonToTaskPlan(cleanJsonMarkdown(candidateText))
                    ?: generateOfflineTaskPlan(prompt)
            } else {
                generateOfflineTaskPlan(prompt)
            }
        } catch (e: Exception) {
            generateOfflineTaskPlan(prompt)
        }
    }

    suspend fun refineToolWithChat(
        currentTool: ToolSpec,
        instruction: String
    ): Pair<ToolSpec, String> = withContext(Dispatchers.Default) {
        // Safe declarative tool modification
        val lower = instruction.lowercase()
        val updatedComps = currentTool.components.toMutableList()
        var summary = "Refined prototype according to instructions."

        if (lower.contains("tax") || lower.contains("rate") || lower.contains("discount") || lower.contains("margin")) {
            // Find calculator or add one
            val calcIdx = updatedComps.indexOfFirst { it is CalculatorComponent }
            if (calcIdx >= 0) {
                val oldCalc = updatedComps[calcIdx] as CalculatorComponent
                val newInputs = oldCalc.inputs.toMutableList().apply {
                    add(
                        CalcInputField(
                            id = "rate_${System.currentTimeMillis() % 1000}",
                            label = if (lower.contains("tax")) "Tax Rate (%)" else "Margin Overhead (%)",
                            defaultValue = 15.0,
                            min = 0.0,
                            max = 50.0,
                            step = 1.0,
                            unit = "%"
                        )
                    )
                }
                updatedComps[calcIdx] = oldCalc.copy(inputs = newInputs)
                summary = "Added adjustable Rate/Margin input slider to calculator component."
            }
        } else if (lower.contains("metric") || lower.contains("kpi") || lower.contains("card")) {
            val metricIdx = updatedComps.indexOfFirst { it is MetricGroupComponent }
            if (metricIdx >= 0) {
                val oldM = updatedComps[metricIdx] as MetricGroupComponent
                val newItems = oldM.metrics.toMutableList().apply {
                    add(MetricItem("Performance Index", "98.2%", "+4.5%", true, "speed"))
                }
                updatedComps[metricIdx] = oldM.copy(metrics = newItems)
                summary = "Added Performance Index KPI card to metric group."
            }
        } else if (lower.contains("table") || lower.contains("column") || lower.contains("row")) {
            val tableIdx = updatedComps.indexOfFirst { it is TableComponent }
            if (tableIdx >= 0) {
                val oldT = updatedComps[tableIdx] as TableComponent
                val newCols = oldT.columns + "Risk Score"
                val newRows = oldT.rows.map { it + "Low" }
                updatedComps[tableIdx] = oldT.copy(columns = newCols, rows = newRows)
                summary = "Expanded data table with new 'Risk Score' column."
            }
        } else if (lower.contains("chart") || lower.contains("graph")) {
            val chartIdx = updatedComps.indexOfFirst { it is ChartComponent }
            if (chartIdx >= 0) {
                val oldC = updatedComps[chartIdx] as ChartComponent
                val newBars = oldC.bars + ChartBarItem("Optimization Gain", 78.0f, "$780", "#00F0FF")
                updatedComps[chartIdx] = oldC.copy(bars = newBars)
                summary = "Injected 'Optimization Gain' data series into telemetry chart."
            } else {
                updatedComps.add(
                    ChartComponent(
                        id = "chart_added_${System.currentTimeMillis()}",
                        title = "Projected Growth & Efficiency",
                        bars = listOf(
                            ChartBarItem("Baseline", 45.0f, "45%", "#64748B"),
                            ChartBarItem("Optimized", 88.0f, "88%", "#00F0FF"),
                            ChartBarItem("Target Max", 95.0f, "95%", "#A855F7")
                        ),
                        maxValue = 100.0f
                    )
                )
                summary = "Added comparative Efficiency Chart component."
            }
        } else {
            // General addition
            updatedComps.add(
                ActionButtonsComponent(
                    id = "act_custom_${System.currentTimeMillis()}",
                    title = "Custom Operational Controls",
                    actions = listOf(
                        ActionItem("act_verify", "Verify Integrity", "check_circle", "System integrity verified 100%.", true)
                    )
                )
            )
            summary = "Updated tool architecture with custom operational verification controls."
        }

        Pair(
            currentTool.copy(components = updatedComps, isSimulation = !isGeminiConfigured()),
            summary
        )
    }

    private fun parseGeminiResponse(rawJson: String): String {
        return try {
            val root = JSONObject(rawJson)
            val candidates = root.getJSONArray("candidates")
            val first = candidates.getJSONObject(0)
            val content = first.getJSONObject("content")
            val parts = content.getJSONArray("parts")
            parts.getJSONObject(0).getString("text")
        } catch (e: Exception) {
            ""
        }
    }

    private fun cleanJsonMarkdown(text: String): String {
        var clean = text.trim()
        if (clean.startsWith("```json")) {
            clean = clean.removePrefix("```json")
        } else if (clean.startsWith("```")) {
            clean = clean.removePrefix("```")
        }
        if (clean.endsWith("```")) {
            clean = clean.removeSuffix("```")
        }
        return clean.trim()
    }

    private fun parseVisionAnalysisFromJson(jsonStr: String, isSimulation: Boolean): VisionAnalysis? {
        val clean = cleanJsonMarkdown(jsonStr)
        if (clean.isBlank()) return null
        return try {
            val root = JSONObject(clean)
            val title = root.optString("title", "Neural Vision Analysis")
            val summary = root.optString("summary", "Image scan completed.")

            val elemArr = root.optJSONArray("detectedElements") ?: JSONArray()
            val elemList = mutableListOf<DetectedElement>()
            for (i in 0 until elemArr.length()) {
                val o = elemArr.getJSONObject(i)
                elemList.add(
                    DetectedElement(
                        label = o.optString("label"),
                        category = o.optString("category"),
                        confidence = o.optDouble("confidence", 0.9).toFloat(),
                        boundingArea = o.optString("boundingArea")
                    )
                )
            }

            val nodeArr = root.optJSONArray("wireframeNodes") ?: JSONArray()
            val nodeList = mutableListOf<WireframeNode>()
            for (i in 0 until nodeArr.length()) {
                val n = nodeArr.getJSONObject(i)
                nodeList.add(
                    WireframeNode(
                        level = n.optInt("level", 1),
                        nodeType = n.optString("nodeType", "Container"),
                        description = n.optString("description")
                    )
                )
            }

            val telemObj = root.optJSONObject("extractedTelemetry") ?: JSONObject()
            val telemMap = mutableMapOf<String, String>()
            val keys = telemObj.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                telemMap[k] = telemObj.getString(k)
            }

            val colArr = root.optJSONArray("colorTokens") ?: JSONArray()
            val colList = mutableListOf<ColorToken>()
            for (i in 0 until colArr.length()) {
                val c = colArr.getJSONObject(i)
                colList.add(
                    ColorToken(
                        name = c.optString("name"),
                        hex = c.optString("hex", "#00F0FF"),
                        usageRole = c.optString("usageRole")
                    )
                )
            }

            val toolArr = root.optJSONArray("suggestedTools") ?: JSONArray()
            val toolList = mutableListOf<SuggestedToolAction>()
            for (i in 0 until toolArr.length()) {
                val t = toolArr.getJSONObject(i)
                toolList.add(
                    SuggestedToolAction(
                        id = t.optString("id", "tool_$i"),
                        title = t.optString("title"),
                        description = t.optString("description"),
                        targetToolType = t.optString("targetToolType", "CALCULATOR"),
                        prompt = t.optString("prompt")
                    )
                )
            }

            VisionAnalysis(
                title = title,
                summary = summary,
                detectedElements = elemList,
                wireframeNodes = nodeList,
                extractedTelemetry = telemMap,
                colorTokens = colList,
                suggestedTools = toolList,
                isSimulation = isSimulation
            )
        } catch (e: Exception) {
            null
        }
    }

    // High fidelity offline simulation engines
    private fun generateOfflineVisionAnalysis(
        prompt: String,
        isCustomUpload: Boolean
    ): VisionAnalysis {
        val title = if (isCustomUpload) "Custom Image Analysis: $prompt" else "Neural Wireframe Scan"
        return VisionAnalysis(
            title = title,
            summary = "NEXORA Neural Engine analyzed image features. Detected hierarchical dashboard layout, numeric data points, and modular control panels.",
            detectedElements = listOf(
                DetectedElement("Control Header & Navigation Bar", "Navigation", 0.97f, "Top Anchor (0,0) to (100,12)"),
                DetectedElement("Primary KPI Metric Quartet", "Metric Display", 0.95f, "Upper Quadrant"),
                DetectedElement("Data Visualization Canvas", "Chart / Telemetry", 0.92f, "Center Region (20,20) to (80,60)"),
                DetectedElement("Parameter Adjustment Form", "Interactive Controls", 0.94f, "Bottom Action Tray")
            ),
            wireframeNodes = listOf(
                WireframeNode(1, "Root Layout", "Cybernetic Dark Frame with Insets"),
                WireframeNode(2, "Top Bar", "Title, Telemetry Status, Action Badges"),
                WireframeNode(3, "Metric Panel", "4 Horizontal Metric Nodes with Delta Trends"),
                WireframeNode(4, "Interactive Canvas", "Dynamic Data Matrix & Slider Parameters"),
                WireframeNode(5, "Action Strip", "Execution Checkpoints & Export Options")
            ),
            extractedTelemetry = mapOf(
                "Scan Confidence" to "96.4%",
                "Detected Components" to "5 Modules",
                "Color Contrast Ratio" to "14.2:1 (AAA)",
                "Actionable Primitives" to "Calculators, Forms, Metric Cards"
            ),
            colorTokens = listOf(
                ColorToken("Cyber Cyan", "#00F0FF", "Active Indicators & Highlights"),
                ColorToken("Electric Violet", "#A855F7", "Secondary Accent & Focus"),
                ColorToken("Deep Obsidian", "#08090C", "Primary Frame Surface"),
                ColorToken("Metallic Silver", "#CBD5E1", "Typography & Outlines")
            ),
            suggestedTools = listOf(
                SuggestedToolAction(
                    id = "tool_extracted_1",
                    title = "Interactive KPI & Operations Calculator",
                    description = "Convert scanned dashboard metrics into a live calculation tool with adjustable variables",
                    targetToolType = "CALCULATOR",
                    prompt = "Build a dynamic Operations & Cost Calculator derived from the scanned wireframe."
                ),
                SuggestedToolAction(
                    id = "tool_extracted_2",
                    title = "Structured Telemetry Monitor",
                    description = "Deploy a tabular and graphical tracker with instant action triggers",
                    targetToolType = "DASHBOARD",
                    prompt = "Create a Telemetry Health Dashboard with metrics, tables, and verification actions."
                )
            ),
            isSimulation = true
        )
    }

    private fun generateOfflineToolSpec(prompt: String): ToolSpec {
        val lower = prompt.lowercase()
        return if (lower.contains("cost") || lower.contains("cloud") || lower.contains("server") || lower.contains("price")) {
            ToolSpec(
                id = "tool_${System.currentTimeMillis()}",
                name = "Cloud Infrastructure & Scale Calculator",
                description = "Simulate cloud resource sizing, RAM/CPU allocation, and monthly billing forecast.",
                category = "Cloud & DevOps",
                sourcePrompt = prompt,
                isSimulation = true,
                components = listOf(
                    MetricGroupComponent(
                        id = "m_sim_1",
                        title = "Resource Telemetry",
                        metrics = listOf(
                            MetricItem("Active Cores", "32 vCPU", "+14%", true, "cpu"),
                            MetricItem("Allocated RAM", "128 GB", "+6%", true, "memory"),
                            MetricItem("Monthly Burn", "$1,420", "-8%", true, "attach_money")
                        )
                    ),
                    CalculatorComponent(
                        id = "calc_sim_1",
                        title = "Resource Cost Calculator",
                        formulaType = "CLOUD_COST",
                        inputs = listOf(
                            CalcInputField("nodes", "Instances / Nodes", 6.0, 1.0, 32.0, 1.0, "nodes"),
                            CalcInputField("cpu_per_node", "vCPUs per Node", 8.0, 2.0, 32.0, 2.0, "cores"),
                            CalcInputField("ram_per_node", "RAM per Node (GB)", 16.0, 4.0, 64.0, 4.0, "GB")
                        ),
                        resultLabel = "Estimated Monthly Budget",
                        resultUnit = "$"
                    ),
                    TableComponent(
                        id = "tbl_sim_1",
                        title = "Instance Pool Inventory",
                        columns = listOf("Pool Name", "Type", "Count", "Health"),
                        rows = listOf(
                            listOf("ingress-edge-pool", "c6g.xlarge", "4", "OPTIMAL"),
                            listOf("compute-worker-pool", "m6g.2xlarge", "6", "OPTIMAL"),
                            listOf("cache-redis-cluster", "r6g.large", "2", "OPTIMAL")
                        )
                    ),
                    ActionButtonsComponent(
                        id = "act_sim_1",
                        title = "Operational Actions",
                        actions = listOf(
                            ActionItem("act_apply", "Apply Resource Profile", "check_circle", "Resource profile deployed to sandbox.", true),
                            ActionItem("act_reset", "Reset to Baseline", "refresh", "Values restored to defaults.", false)
                        )
                    )
                )
            )
        } else if (lower.contains("roi") || lower.contains("invest") || lower.contains("crypto") || lower.contains("finance") || lower.contains("yield")) {
            ToolSpec(
                id = "tool_${System.currentTimeMillis()}",
                name = "FinTech ROI & Yield Forecaster",
                description = "Interactive capital projection tool with interest compounding and risk variance sliders.",
                category = "FinTech & Analytics",
                sourcePrompt = prompt,
                isSimulation = true,
                components = listOf(
                    MetricGroupComponent(
                        id = "m_roi_1",
                        title = "Portfolio Yield Overview",
                        metrics = listOf(
                            MetricItem("Base Capital", "$25,000", "+0%", true, "account_balance"),
                            MetricItem("Projected APR", "14.5%", "+2.1%", true, "trending_up"),
                            MetricItem("Net 1-Yr Return", "$3,625", "+14.5%", true, "savings")
                        )
                    ),
                    CalculatorComponent(
                        id = "calc_roi_1",
                        title = "Compound Yield Calculator",
                        formulaType = "ROI",
                        inputs = listOf(
                            CalcInputField("principal", "Initial Capital ($)", 10000.0, 1000.0, 100000.0, 1000.0, "$"),
                            CalcInputField("annual_rate", "Expected Annual Return (%)", 12.0, 1.0, 40.0, 0.5, "%"),
                            CalcInputField("duration_months", "Duration (Months)", 12.0, 1.0, 60.0, 1.0, "months")
                        ),
                        resultLabel = "Estimated Total Net Profit",
                        resultUnit = "$"
                    ),
                    ChartComponent(
                        id = "chart_roi_1",
                        title = "Quarterly Capital Trajectory",
                        bars = listOf(
                            ChartBarItem("Q1 Growth", 25.0f, "$10,300", "#00F0FF"),
                            ChartBarItem("Q2 Growth", 50.0f, "$10,610", "#00B4D8"),
                            ChartBarItem("Q3 Growth", 75.0f, "$10,930", "#A855F7"),
                            ChartBarItem("Q4 Maturity", 100.0f, "$11,268", "#10B981")
                        ),
                        maxValue = 100.0f
                    ),
                    ActionButtonsComponent(
                        id = "act_roi_1",
                        title = "Investment Operations",
                        actions = listOf(
                            ActionItem("act_deposit", "Execute Strategy Simulation", "bolt", "Simulated strategy executed across 4 asset pools.", true),
                            ActionItem("act_report", "Export PDF Audit Sheet", "description", "Audit sheet generated.", false)
                        )
                    )
                )
            )
        } else {
            ToolSpec(
                id = "tool_${System.currentTimeMillis()}",
                name = "Adaptive Operational Control Matrix",
                description = "Multi-purpose interactive prototype synthesized from: $prompt",
                category = "Operations",
                sourcePrompt = prompt,
                isSimulation = true,
                components = listOf(
                    MetricGroupComponent(
                        id = "m_gen_1",
                        title = "Core KPI Scorecards",
                        metrics = listOf(
                            MetricItem("Efficiency", "94.8%", "+3.2%", true, "speed"),
                            MetricItem("Active Jobs", "18 Tasks", "+2", true, "work"),
                            MetricItem("Error Rate", "0.02%", "-0.01%", true, "check")
                        )
                    ),
                    CalculatorComponent(
                        id = "calc_gen_1",
                        title = "Efficiency & Volume Estimator",
                        formulaType = "CUSTOM_MULTIPLIER",
                        inputs = listOf(
                            CalcInputField("daily_volume", "Daily Transaction Volume", 500.0, 50.0, 5000.0, 50.0, "units"),
                            CalcInputField("unit_price", "Unit Value / Fee ($)", 25.0, 1.0, 200.0, 1.0, "$"),
                            CalcInputField("efficiency_boost", "Automation Efficiency Multiplier", 1.25, 1.0, 3.0, 0.05, "x")
                        ),
                        resultLabel = "Estimated Total Productivity Output",
                        resultUnit = "$"
                    ),
                    FormComponent(
                        id = "form_gen_1",
                        title = "Operational Parameters",
                        fields = listOf(
                            FormFieldItem("op_name", "Workflow Name", "TEXT", "Production Pipeline Alpha"),
                            FormFieldItem("op_mode", "Automation Level", "DROPDOWN", "Assisted Autonomous", listOf("Manual", "Assisted Autonomous", "Fully Autonomous")),
                            FormFieldItem("op_audit", "Log Telemetry to Immutable Ledger", "TOGGLE", "true")
                        )
                    ),
                    ActionButtonsComponent(
                        id = "act_gen_1",
                        title = "Execution Actions",
                        actions = listOf(
                            ActionItem("act_trigger", "Trigger Pipeline Test", "play_arrow", "Pipeline execution test completed successfully with 0 errors.", true),
                            ActionItem("act_sync", "Synchronize Parameters", "sync", "Local parameters synced with cloud registry.", false)
                        )
                    )
                )
            )
        }
    }

    private fun generateOfflineTaskPlan(prompt: String): TaskPlan {
        return TaskPlan(
            id = "plan_${System.currentTimeMillis()}",
            title = "Execution Plan: $prompt",
            objective = "Coordinate structured execution of steps with mandatory approval gates before modifying live states.",
            overallStatus = PlanStatus.READY_FOR_APPROVAL,
            isSimulation = true,
            steps = listOf(
                TaskStep(
                    id = "step_1",
                    order = 1,
                    title = "Pre-Execution Environment & Dependency Check",
                    description = "Verify network connectivity, schema compliance, and authorization tokens.",
                    requiresApproval = false,
                    isApproved = true,
                    status = StepStatus.COMPLETED,
                    executionLog = "Dependencies satisfied. Checksum match 100%."
                ),
                TaskStep(
                    id = "step_2",
                    order = 2,
                    title = "Configure Telemetry Stream & Allocate Buffer",
                    description = "Set up real-time monitoring channels and reserve RAM scratchpad.",
                    requiresApproval = false,
                    isApproved = true,
                    status = StepStatus.APPROVED,
                    executionLog = "Allocated 64MB buffer on port 9042."
                ),
                TaskStep(
                    id = "step_3",
                    order = 3,
                    title = "Apply State Mutations (CRITICAL CHECKPOINT)",
                    description = "Modify target workspace entities and rebalance pipeline parameters. Requires operator sign-off.",
                    requiresApproval = true,
                    isApproved = false,
                    status = StepStatus.PENDING_APPROVAL,
                    executionLog = "Halted at approval gate. Waiting for operator authorization."
                ),
                TaskStep(
                    id = "step_4",
                    order = 4,
                    title = "Post-Run Validation & Health Assertion",
                    description = "Execute synthetic smoke tests to ensure zero regression in production performance.",
                    requiresApproval = true,
                    isApproved = false,
                    status = StepStatus.PENDING_APPROVAL,
                    executionLog = "Scheduled after Step 3 execution."
                )
            )
        )
    }
}
