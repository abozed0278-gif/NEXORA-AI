package com.example.data

import com.example.model.*

object PresetData {

    val samplePresets = listOf(
        VisionPreset(
            id = "preset_cloud_infra",
            title = "Kubernetes Multi-Region Mesh Architecture",
            description = "High-availability telemetry topology with 12 cluster nodes and ingress gateways",
            category = "DevOps & Cloud",
            iconName = "cloud",
            analysis = VisionAnalysis(
                title = "Multi-Region Cloud Topology Analysis",
                summary = "Detected distributed Kubernetes cluster across 3 availability zones (us-east, eu-west, ap-south). Ingress gateway handles 45k req/sec with active Redis cluster cache.",
                detectedElements = listOf(
                    DetectedElement("Ingress Envoy Proxy", "Network Gateway", 0.98f, "Top Center (x: 50%, y: 15%)"),
                    DetectedElement("Primary Node Pool (x8 c6g.4xlarge)", "Compute", 0.96f, "Center Grid"),
                    DetectedElement("Sharded Postgres Vector DB", "Storage", 0.94f, "Bottom Tier"),
                    DetectedElement("Redis Sentinel Cache Layer", "In-Memory", 0.92f, "Mid Tier Left")
                ),
                wireframeNodes = listOf(
                    WireframeNode(1, "Container", "Root Viewport / Topo Canvas"),
                    WireframeNode(2, "Header", "Region Selector & Overall Health Score (99.98%)"),
                    WireframeNode(3, "MetricGroup", "QPS, Latency (p99 18ms), CPU Throttle Rate"),
                    WireframeNode(4, "InteractiveGrid", "Node Status Matrix (12 Nodes active, 0 failed)"),
                    WireframeNode(5, "ControlPanel", "Autoscaling Thresholds & Traffic Splitting Sliders")
                ),
                extractedTelemetry = mapOf(
                    "Total Compute VCPU" to "128 Cores",
                    "Cluster RAM" to "512 GB DDR5",
                    "Ingress Throughput" to "4.2 Gbps",
                    "Estimated Monthly Burn" to "$3,840.00 / mo",
                    "Current P99 Latency" to "14.2 ms"
                ),
                colorTokens = listOf(
                    ColorToken("Cyber Cyan", "#00F0FF", "Active Traffic Flow & Ingress"),
                    ColorToken("Electric Violet", "#A855F7", "Compute & Memory Clusters"),
                    ColorToken("Emerald Pulse", "#10B981", "Healthy Sentinel Services"),
                    ColorToken("Obsidian Surface", "#0F1117", "Backdrop Grid Matrix")
                ),
                suggestedTools = listOf(
                    SuggestedToolAction(
                        id = "tool_cloud_calculator",
                        title = "Cloud Node Capacity & Cost Simulator",
                        description = "Simulate node scaling from 4 to 64 instances, estimating RAM overhead and monthly cloud bill",
                        targetToolType = "CALCULATOR",
                        prompt = "Build a Cloud Capacity & Cost Calculator with node count slider, memory per node, storage volume, and monthly cost output."
                    ),
                    SuggestedToolAction(
                        id = "tool_infra_health",
                        title = "Cluster Telemetry Live Dashboard",
                        description = "Key latency metrics, node status breakdown table, and simulated restart action triggers",
                        targetToolType = "DASHBOARD",
                        prompt = "Build an Infrastructure Health Monitor with CPU, Memory, Latency metrics, node status table, and restart actions."
                    )
                ),
                isSimulation = false
            )
        ),
        VisionPreset(
            id = "preset_fintech_terminal",
            title = "FinTech Liquidity & Yield Terminal",
            description = "DeFi liquidity pool curve, impermanent loss risk, and daily swap volumes",
            category = "FinTech & Web3",
            iconName = "trending_up",
            analysis = VisionAnalysis(
                title = "FinTech Liquidity Pool Vision Breakdown",
                summary = "Scanned financial trading terminal displaying $18.4M Total Value Locked (TVL), 24h trading volume of $3.2M, and a concentrated liquidity curve.",
                detectedElements = listOf(
                    DetectedElement("Liquidity Depth Histogram", "Chart Component", 0.97f, "Center Canvas"),
                    DetectedElement("Fee Tier Selector (0.05%, 0.3%, 1.0%)", "Form Control", 0.95f, "Top Right"),
                    DetectedElement("Impermanent Loss Radar", "Risk Gauge", 0.91f, "Bottom Left"),
                    DetectedElement("Token Pair Badge (ETH / USDC)", "Header", 0.99f, "Top Left")
                ),
                wireframeNodes = listOf(
                    WireframeNode(1, "Container", "Trading Viewport Frame"),
                    WireframeNode(2, "MetricGroup", "TVL ($18.4M), APY (14.2%), 24h Volume ($3.2M)"),
                    WireframeNode(3, "Calculator", "Impermanent Loss & Yield Forecast Model"),
                    WireframeNode(4, "Table", "Recent High-Value Liquidity Injections"),
                    WireframeNode(5, "ActionGroup", "Deposit, Rebalance Range, Harvest Rewards")
                ),
                extractedTelemetry = mapOf(
                    "Pool Pair" to "ETH / USDC",
                    "Current Price" to "$3,420.50",
                    "7-Day Fee APR" to "16.8%",
                    "Total Locked" to "$18,420,000",
                    "Volatility Index (30d)" to "42.8"
                ),
                colorTokens = listOf(
                    ColorToken("Neon Violet", "#A855F7", "Liquidity Depth Bounds"),
                    ColorToken("Cyber Cyan", "#00F0FF", "Current Active Tick Indicator"),
                    ColorToken("Amber Warning", "#F59E0B", "Impermanent Loss Caution Zone"),
                    ColorToken("Graphite Silver", "#CBD5E1", "Numerical Headers")
                ),
                suggestedTools = listOf(
                    SuggestedToolAction(
                        id = "tool_yield_calc",
                        title = "DeFi Liquidity Yield & Impermanent Loss Calculator",
                        description = "Adjust initial investment, price change ratio, and pool fee tier to compute net profit after divergence",
                        targetToolType = "CALCULATOR",
                        prompt = "Create a DeFi Yield Calculator with investment capital slider, price change percentage, pool fee tier, and projected return."
                    )
                ),
                isSimulation = false
            )
        ),
        VisionPreset(
            id = "preset_industrial_iot",
            title = "Megawatt Battery & Energy Storage Telemetry",
            description = "Industrial grid battery telemetry, thermal distribution, and peak shaved MW",
            category = "CleanTech & Hardware",
            iconName = "battery_charging_full",
            analysis = VisionAnalysis(
                title = "Smart Energy Grid Vision Decomposition",
                summary = "Thermal and voltage telemetry of a 20 MWh utility-scale lithium-iron-phosphate battery bank. Real-time state of charge (SoC) is at 84%.",
                detectedElements = listOf(
                    DetectedElement("Battery Rack State-of-Charge Matrix", "Sensor Grid", 0.98f, "Left Panel"),
                    DetectedElement("Inverter Phase Sync (60.02 Hz)", "Power Quality", 0.94f, "Top Center"),
                    DetectedElement("Coolant Loop Pressure Gauges", "Telemetry", 0.92f, "Right Panel"),
                    DetectedElement("Emergency Isolation Switch", "Safety Control", 0.99f, "Bottom Right")
                ),
                wireframeNodes = listOf(
                    WireframeNode(1, "Container", "Telemetry Dashboard Shell"),
                    WireframeNode(2, "MetricGroup", "SoC (84%), Output (8.4 MW), Rack Temp (24.2°C)"),
                    WireframeNode(3, "Chart", "Daily Load Curve & Peak Shaving Graph"),
                    WireframeNode(4, "ControlPanel", "Discharge Rate Slider & Target Grid Demand"),
                    WireframeNode(5, "ActionGroup", "Arm Battery, Cycle Coolant, Emergency Disconnect")
                ),
                extractedTelemetry = mapOf(
                    "Capacity" to "20.0 MWh",
                    "Active State of Charge" to "84.2%",
                    "Inverter Frequency" to "60.02 Hz",
                    "Coolant Temp" to "22.8 °C",
                    "Daily Shaved Peak" to "14.2 MWh"
                ),
                colorTokens = listOf(
                    ColorToken("Emerald Grid", "#10B981", "Normal Operating Temperature"),
                    ColorToken("Cyber Cyan", "#00F0FF", "Electrical Frequency & Phase"),
                    ColorToken("Crimson Alert", "#F43F5E", "Emergency Cutoff Logic"),
                    ColorToken("Dark Steel", "#1A1D27", "Rack Chassis Containers")
                ),
                suggestedTools = listOf(
                    SuggestedToolAction(
                        id = "tool_energy_planner",
                        title = "Industrial Grid Peak-Shaving Calculator",
                        description = "Compute power cost savings based on peak tariff rate, battery discharge duration, and demand fees",
                        targetToolType = "CALCULATOR",
                        prompt = "Create an Industrial Energy Storage Calculator with battery capacity, peak tariff, off-peak tariff, and monthly savings."
                    )
                ),
                isSimulation = false
            )
        )
    )

    // Preset starter projects so the workspace is immediately full of rich, working interactive prototypes!
    fun getStarterProjects(): List<ToolSpec> {
        return listOf(
            ToolSpec(
                id = "starter_cloud_calculator",
                name = "Cloud Cluster Cost & Capacity Forecaster",
                description = "Interactive simulation for sizing Kubernetes worker nodes, RAM ratio, and estimating monthly AWS/GCP bills.",
                category = "Cloud & DevOps",
                sourcePrompt = "Build a Cloud Capacity & Cost Calculator with node count slider, memory per node, storage volume, and monthly cost output.",
                isSimulation = false,
                components = listOf(
                    MetricGroupComponent(
                        id = "m_cloud_1",
                        title = "Resource Allocation Overview",
                        metrics = listOf(
                            MetricItem("Active VCPUs", "64 Cores", "+12%", true, "cpu"),
                            MetricItem("Allocated RAM", "256 GB", "+8%", true, "memory"),
                            MetricItem("IOPS Reserved", "12,000", "+0%", true, "storage"),
                            MetricItem("Estimated Bill", "$2,140/mo", "-5%", true, "attach_money")
                        )
                    ),
                    CalculatorComponent(
                        id = "calc_cloud_cost",
                        title = "Compute & Storage Cost Matrix",
                        formulaType = "CLOUD_COST",
                        inputs = listOf(
                            CalcInputField("nodes", "Cluster Worker Nodes", 8.0, 1.0, 32.0, 1.0, "nodes"),
                            CalcInputField("cpu_per_node", "VCPUs Per Node", 8.0, 2.0, 32.0, 2.0, "cores"),
                            CalcInputField("ram_per_node", "RAM Per Node (GB)", 32.0, 8.0, 128.0, 8.0, "GB"),
                            CalcInputField("storage_tb", "Persistent NVMe Storage", 4.0, 0.5, 20.0, 0.5, "TB")
                        ),
                        resultLabel = "Estimated Total Cloud Cost",
                        resultUnit = "$ / month"
                    ),
                    ChartComponent(
                        id = "chart_cloud_breakdown",
                        title = "Cost Allocation by Subsystem",
                        bars = listOf(
                            ChartBarItem("Compute (vCPU)", 55.0f, "$1,177", "#00F0FF"),
                            ChartBarItem("Memory (RAM)", 25.0f, "$535", "#A855F7"),
                            ChartBarItem("Storage (NVMe)", 12.0f, "$256", "#10B981"),
                            ChartBarItem("Egress & Ingress", 8.0f, "$172", "#CBD5E1")
                        ),
                        maxValue = 100.0f
                    ),
                    TableComponent(
                        id = "tbl_node_fleet",
                        title = "Worker Fleet Configuration",
                        columns = listOf("Node ID", "Zone", "Instance Type", "Memory Used", "Status"),
                        rows = listOf(
                            listOf("worker-us-east-1a", "us-east-1a", "c6g.2xlarge", "74%", "RUNNING"),
                            listOf("worker-us-east-1b", "us-east-1b", "c6g.2xlarge", "68%", "RUNNING"),
                            listOf("worker-us-east-1c", "us-east-1c", "c6g.2xlarge", "81%", "RUNNING"),
                            listOf("worker-eu-west-1a", "eu-west-1a", "c6g.2xlarge", "55%", "RUNNING")
                        )
                    ),
                    ActionButtonsComponent(
                        id = "act_cloud",
                        title = "Simulation Operations",
                        actions = listOf(
                            ActionItem("act_rebalance", "Rebalance Cluster", "refresh", "Fleet rebalanced across 3 availability zones.", true),
                            ActionItem("act_snapshot", "Take State Snapshot", "camera", "Configuration snapshot #NX-942 saved to registry.", false),
                            ActionItem("act_export", "Export Terraform HCL", "code", "Terraform module exported successfully.", false)
                        )
                    )
                )
            ),
            ToolSpec(
                id = "starter_ai_token_budget",
                name = "LLM Token Budget & Inference Planner",
                description = "Plan Gemini 2.5 Flash token usage, calculate prompt caching savings, and estimate end-user latency.",
                category = "AI Architecture",
                sourcePrompt = "Create an AI Token & Inference Cost Estimator with daily active users, requests per user, and token length sliders.",
                isSimulation = false,
                components = listOf(
                    MetricGroupComponent(
                        id = "m_ai_1",
                        title = "Inference Throughput & Cache Stats",
                        metrics = listOf(
                            MetricItem("Daily Queries", "125,000", "+24%", true, "chat"),
                            MetricItem("Cache Hit Rate", "68.4%", "+15%", true, "save"),
                            MetricItem("Avg Latency", "340 ms", "-20%", true, "speed"),
                            MetricItem("Net Cost / 1k req", "$0.042", "-12%", true, "savings")
                        )
                    ),
                    CalculatorComponent(
                        id = "calc_ai_tokens",
                        title = "Inference Cost Calculator",
                        formulaType = "AI_TOKEN_BUDGET",
                        inputs = listOf(
                            CalcInputField("dau", "Daily Active Users", 5000.0, 500.0, 50000.0, 500.0, "users"),
                            CalcInputField("prompts_per_user", "Prompts / User / Day", 6.0, 1.0, 30.0, 1.0, "prompts"),
                            CalcInputField("input_tokens", "Avg Input Tokens", 800.0, 100.0, 4000.0, 100.0, "tokens"),
                            CalcInputField("output_tokens", "Avg Output Tokens", 400.0, 50.0, 2000.0, 50.0, "tokens")
                        ),
                        resultLabel = "Monthly Gemini API Cost",
                        resultUnit = "$ / month"
                    ),
                    FormComponent(
                        id = "form_ai_settings",
                        title = "Model Parameters & Safety Toggles",
                        fields = listOf(
                            FormFieldItem("model_id", "Primary Foundation Model", "DROPDOWN", "Gemini 2.5 Flash", listOf("Gemini 2.5 Flash", "Gemini 1.5 Pro", "Gemini 1.5 Flash")),
                            FormFieldItem("caching_enabled", "Enable Context Caching", "TOGGLE", "true"),
                            FormFieldItem("safety_filter", "Content Filtering Tier", "DROPDOWN", "Standard Enterprise", listOf("Strict", "Standard Enterprise", "Permissive Research")),
                            FormFieldItem("system_instruction", "System Prompt Override", "TEXT", "You are NEXORA AI assistant specialized in declarative UI.")
                        )
                    ),
                    ActionButtonsComponent(
                        id = "act_ai_tools",
                        title = "Model Pipeline Controls",
                        actions = listOf(
                            ActionItem("act_run_bench", "Run Latency Benchmark", "play_arrow", "Benchmark executed: 99.4% queries under 450ms.", true),
                            ActionItem("act_flush_cache", "Flush Context Cache", "delete_outline", "Cache buffer flushed. New cache key generated.", false)
                        )
                    )
                )
            )
        )
    }

    fun getStarterTaskPlans(): List<TaskPlan> {
        return listOf(
            TaskPlan(
                id = "plan_db_migration",
                title = "Zero-Downtime Multi-Region Database Sync",
                objective = "Synchronize primary transactional database with secondary European node without dropping incoming user transactions.",
                overallStatus = PlanStatus.READY_FOR_APPROVAL,
                steps = listOf(
                    TaskStep(
                        id = "s_1",
                        order = 1,
                        title = "Snapshot & Checkpoint Validation",
                        description = "Verify WAL logs and create cold-storage snapshot before altering replica streams.",
                        requiresApproval = false,
                        isApproved = true,
                        status = StepStatus.COMPLETED,
                        executionLog = "Snapshot NX-DB-881 verified. 0 byte corruption detected."
                    ),
                    TaskStep(
                        id = "s_2",
                        order = 2,
                        title = "Pre-Provision Read Replica (eu-west-1)",
                        description = "Spin up r6g.4xlarge node with 500GB nvme and apply replication secret keys.",
                        requiresApproval = true,
                        isApproved = true,
                        status = StepStatus.COMPLETED,
                        executionLog = "Replica instance online in eu-west-1a with 8ms ping to primary."
                    ),
                    TaskStep(
                        id = "s_3",
                        order = 3,
                        title = "Cut Traffic Over to Ingress Gateway",
                        description = "CRITICAL ACTION: Re-route 50% of write traffic to dual-master coordinator. Requires operator approval checkpoint.",
                        requiresApproval = true,
                        isApproved = false,
                        status = StepStatus.PENDING_APPROVAL,
                        executionLog = "Awaiting human operator sign-off before modifying DNS routing table."
                    ),
                    TaskStep(
                        id = "s_4",
                        order = 4,
                        title = "Post-Migration Integrity Verification",
                        description = "Execute 10,000 synthetic transaction hashes to confirm cryptographic parity across all regions.",
                        requiresApproval = true,
                        isApproved = false,
                        status = StepStatus.PENDING_APPROVAL,
                        executionLog = "Scheduled after Step 3 completion."
                    )
                ),
                isSimulation = false
            ),
            TaskPlan(
                id = "plan_security_audit",
                title = "Automated Zero-Trust Vulnerability Audit",
                objective = "Audit IAM permissions, exposed API routes, and container base images across the entire production workspace.",
                overallStatus = PlanStatus.IN_PLANNING,
                steps = listOf(
                    TaskStep(
                        id = "sa_1",
                        order = 1,
                        title = "Static Container Image Vulnerability Scan",
                        description = "Scan production Docker image layers against CVE databases (Trivy & Grype).",
                        requiresApproval = false,
                        isApproved = true,
                        status = StepStatus.COMPLETED,
                        executionLog = "Scanned 14 image tags: 0 Critical, 2 Low CVEs found."
                    ),
                    TaskStep(
                        id = "sa_2",
                        order = 2,
                        title = "Audit Over-Privileged IAM Roles",
                        description = "Identify service accounts with wildcard permissions (`*`) or unused administrative keys.",
                        requiresApproval = true,
                        isApproved = false,
                        status = StepStatus.PENDING_APPROVAL,
                        executionLog = "Pending review of 8 service accounts."
                    ),
                    TaskStep(
                        id = "sa_3",
                        order = 3,
                        title = "Revoke Unused API Tokens & Rotate Certificates",
                        description = "Irreversible security action: Terminate tokens inactive for >90 days and rotate TLS certificates.",
                        requiresApproval = true,
                        isApproved = false,
                        status = StepStatus.PENDING_APPROVAL,
                        executionLog = "Requires explicit admin confirmation before token revocation."
                    )
                ),
                isSimulation = false
            )
        )
    }
}
