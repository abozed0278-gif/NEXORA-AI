package com.example.data

import com.example.model.*
import org.json.JSONArray
import org.json.JSONObject

object JsonHelper {

    fun toolSpecToJson(tool: ToolSpec): String {
        val root = JSONObject()
        root.put("id", tool.id)
        root.put("name", tool.name)
        root.put("description", tool.description)
        root.put("category", tool.category)
        root.put("sourcePrompt", tool.sourcePrompt)
        root.put("isSimulation", tool.isSimulation)

        val comps = JSONArray()
        for (c in tool.components) {
            val cObj = JSONObject()
            cObj.put("id", c.id)
            cObj.put("title", c.title)
            when (c) {
                is MetricGroupComponent -> {
                    cObj.put("type", "METRIC_GROUP")
                    val items = JSONArray()
                    for (m in c.metrics) {
                        val mObj = JSONObject()
                        mObj.put("label", m.label)
                        mObj.put("value", m.value)
                        mObj.put("delta", m.delta)
                        mObj.put("isPositive", m.isPositive)
                        mObj.put("icon", m.icon)
                        items.put(mObj)
                    }
                    cObj.put("metrics", items)
                }
                is CalculatorComponent -> {
                    cObj.put("type", "CALCULATOR")
                    cObj.put("formulaType", c.formulaType)
                    cObj.put("resultLabel", c.resultLabel)
                    cObj.put("resultUnit", c.resultUnit)
                    val inputs = JSONArray()
                    for (inp in c.inputs) {
                        val iObj = JSONObject()
                        iObj.put("id", inp.id)
                        iObj.put("label", inp.label)
                        iObj.put("defaultValue", inp.defaultValue)
                        iObj.put("min", inp.min)
                        iObj.put("max", inp.max)
                        iObj.put("step", inp.step)
                        iObj.put("unit", inp.unit)
                        inputs.put(iObj)
                    }
                    cObj.put("inputs", inputs)
                }
                is FormComponent -> {
                    cObj.put("type", "FORM")
                    val fields = JSONArray()
                    for (f in c.fields) {
                        val fObj = JSONObject()
                        fObj.put("id", f.id)
                        fObj.put("label", f.label)
                        fObj.put("type", f.type)
                        fObj.put("defaultValue", f.defaultValue)
                        val opts = JSONArray()
                        for (o in f.options) opts.put(o)
                        fObj.put("options", opts)
                        fields.put(fObj)
                    }
                    cObj.put("fields", fields)
                }
                is TableComponent -> {
                    cObj.put("type", "TABLE")
                    val cols = JSONArray()
                    for (col in c.columns) cols.put(col)
                    cObj.put("columns", cols)

                    val rows = JSONArray()
                    for (row in c.rows) {
                        val rArr = JSONArray()
                        for (cell in row) rArr.put(cell)
                        rows.put(rArr)
                    }
                    cObj.put("rows", rows)
                }
                is ChartComponent -> {
                    cObj.put("type", "CHART")
                    cObj.put("maxValue", c.maxValue.toDouble())
                    val bars = JSONArray()
                    for (b in c.bars) {
                        val bObj = JSONObject()
                        bObj.put("label", b.label)
                        bObj.put("value", b.value.toDouble())
                        bObj.put("displayValue", b.displayValue)
                        bObj.put("colorHex", b.colorHex)
                        bars.put(bObj)
                    }
                    cObj.put("bars", bars)
                }
                is ActionButtonsComponent -> {
                    cObj.put("type", "ACTIONS")
                    val acts = JSONArray()
                    for (a in c.actions) {
                        val aObj = JSONObject()
                        aObj.put("id", a.id)
                        aObj.put("label", a.label)
                        aObj.put("iconName", a.iconName)
                        aObj.put("outcomeMessage", a.outcomeMessage)
                        aObj.put("isPrimary", a.isPrimary)
                        acts.put(aObj)
                    }
                    cObj.put("actions", acts)
                }
            }
            comps.put(cObj)
        }
        root.put("components", comps)
        return root.toString(2)
    }

    fun jsonToToolSpec(json: String): ToolSpec? {
        if (json.isBlank()) return null
        return try {
            val root = JSONObject(json)
            val compsList = mutableListOf<ToolComponent>()
            val comps = root.optJSONArray("components") ?: JSONArray()
            for (i in 0 until comps.length()) {
                val cObj = comps.getJSONObject(i)
                val id = cObj.getString("id")
                val title = cObj.getString("title")
                when (cObj.optString("type")) {
                    "METRIC_GROUP" -> {
                        val mArr = cObj.optJSONArray("metrics") ?: JSONArray()
                        val mList = mutableListOf<MetricItem>()
                        for (j in 0 until mArr.length()) {
                            val m = mArr.getJSONObject(j)
                            mList.add(
                                MetricItem(
                                    label = m.optString("label"),
                                    value = m.optString("value"),
                                    delta = m.optString("delta"),
                                    isPositive = m.optBoolean("isPositive", true),
                                    icon = m.optString("icon", "bolt")
                                )
                            )
                        }
                        compsList.add(MetricGroupComponent(id, title, mList))
                    }
                    "CALCULATOR" -> {
                        val formulaType = cObj.optString("formulaType", "CUSTOM_MULTIPLIER")
                        val resultLabel = cObj.optString("resultLabel", "Computed Total")
                        val resultUnit = cObj.optString("resultUnit", "")
                        val iArr = cObj.optJSONArray("inputs") ?: JSONArray()
                        val iList = mutableListOf<CalcInputField>()
                        for (j in 0 until iArr.length()) {
                            val inp = iArr.getJSONObject(j)
                            iList.add(
                                CalcInputField(
                                    id = inp.optString("id", "input_$j"),
                                    label = inp.optString("label"),
                                    defaultValue = inp.optDouble("defaultValue", 10.0),
                                    min = inp.optDouble("min", 0.0),
                                    max = inp.optDouble("max", 100.0),
                                    step = inp.optDouble("step", 1.0),
                                    unit = inp.optString("unit", "")
                                )
                            )
                        }
                        compsList.add(CalculatorComponent(id, title, formulaType, iList, resultLabel, resultUnit))
                    }
                    "FORM" -> {
                        val fArr = cObj.optJSONArray("fields") ?: JSONArray()
                        val fList = mutableListOf<FormFieldItem>()
                        for (j in 0 until fArr.length()) {
                            val f = fArr.getJSONObject(j)
                            val optArr = f.optJSONArray("options") ?: JSONArray()
                            val optList = mutableListOf<String>()
                            for (k in 0 until optArr.length()) optList.add(optArr.getString(k))
                            fList.add(
                                FormFieldItem(
                                    id = f.optString("id", "field_$j"),
                                    label = f.optString("label"),
                                    type = f.optString("type", "TEXT"),
                                    defaultValue = f.optString("defaultValue"),
                                    options = optList
                                )
                            )
                        }
                        compsList.add(FormComponent(id, title, fList))
                    }
                    "TABLE" -> {
                        val colArr = cObj.optJSONArray("columns") ?: JSONArray()
                        val colList = mutableListOf<String>()
                        for (j in 0 until colArr.length()) colList.add(colArr.getString(j))

                        val rowArr = cObj.optJSONArray("rows") ?: JSONArray()
                        val rowList = mutableListOf<List<String>>()
                        for (j in 0 until rowArr.length()) {
                            val r = rowArr.getJSONArray(j)
                            val rItems = mutableListOf<String>()
                            for (k in 0 until r.length()) rItems.add(r.getString(k))
                            rowList.add(rItems)
                        }
                        compsList.add(TableComponent(id, title, colList, rowList))
                    }
                    "CHART" -> {
                        val maxV = cObj.optDouble("maxValue", 100.0).toFloat()
                        val bArr = cObj.optJSONArray("bars") ?: JSONArray()
                        val bList = mutableListOf<ChartBarItem>()
                        for (j in 0 until bArr.length()) {
                            val b = bArr.getJSONObject(j)
                            bList.add(
                                ChartBarItem(
                                    label = b.optString("label"),
                                    value = b.optDouble("value", 0.0).toFloat(),
                                    displayValue = b.optString("displayValue"),
                                    colorHex = b.optString("colorHex", "#00F0FF")
                                )
                            )
                        }
                        compsList.add(ChartComponent(id, title, bList, maxV))
                    }
                    "ACTIONS" -> {
                        val aArr = cObj.optJSONArray("actions") ?: JSONArray()
                        val aList = mutableListOf<ActionItem>()
                        for (j in 0 until aArr.length()) {
                            val a = aArr.getJSONObject(j)
                            aList.add(
                                ActionItem(
                                    id = a.optString("id", "act_$j"),
                                    label = a.optString("label"),
                                    iconName = a.optString("iconName", "play"),
                                    outcomeMessage = a.optString("outcomeMessage"),
                                    isPrimary = a.optBoolean("isPrimary", false)
                                )
                            )
                        }
                        compsList.add(ActionButtonsComponent(id, title, aList))
                    }
                }
            }
            ToolSpec(
                id = root.getString("id"),
                name = root.getString("name"),
                description = root.optString("description"),
                category = root.optString("category", "General"),
                components = compsList,
                sourcePrompt = root.optString("sourcePrompt"),
                isSimulation = root.optBoolean("isSimulation", false)
            )
        } catch (e: Exception) {
            null
        }
    }

    fun taskPlanToJson(plan: TaskPlan): String {
        val root = JSONObject()
        root.put("id", plan.id)
        root.put("title", plan.title)
        root.put("objective", plan.objective)
        root.put("overallStatus", plan.overallStatus.name)
        root.put("isSimulation", plan.isSimulation)

        val steps = JSONArray()
        for (s in plan.steps) {
            val sObj = JSONObject()
            sObj.put("id", s.id)
            sObj.put("order", s.order)
            sObj.put("title", s.title)
            sObj.put("description", s.description)
            sObj.put("requiresApproval", s.requiresApproval)
            sObj.put("isApproved", s.isApproved)
            sObj.put("status", s.status.name)
            sObj.put("estimatedDurationSec", s.estimatedDurationSec)
            sObj.put("executionLog", s.executionLog)
            sObj.put("outputData", s.outputData)
            steps.put(sObj)
        }
        root.put("steps", steps)
        return root.toString(2)
    }

    fun jsonToTaskPlan(json: String): TaskPlan? {
        if (json.isBlank()) return null
        return try {
            val root = JSONObject(json)
            val stepsArr = root.optJSONArray("steps") ?: JSONArray()
            val stepList = mutableListOf<TaskStep>()
            for (i in 0 until stepsArr.length()) {
                val s = stepsArr.getJSONObject(i)
                stepList.add(
                    TaskStep(
                        id = s.optString("id", "step_$i"),
                        order = s.optInt("order", i + 1),
                        title = s.optString("title"),
                        description = s.optString("description"),
                        requiresApproval = s.optBoolean("requiresApproval", true),
                        isApproved = s.optBoolean("isApproved", false),
                        status = try {
                            StepStatus.valueOf(s.optString("status", StepStatus.PENDING_APPROVAL.name))
                        } catch (e: Exception) {
                            StepStatus.PENDING_APPROVAL
                        },
                        estimatedDurationSec = s.optInt("estimatedDurationSec", 3),
                        executionLog = s.optString("executionLog"),
                        outputData = s.optString("outputData")
                    )
                )
            }
            TaskPlan(
                id = root.getString("id"),
                title = root.getString("title"),
                objective = root.optString("objective"),
                steps = stepList,
                overallStatus = try {
                    PlanStatus.valueOf(root.optString("overallStatus", PlanStatus.IN_PLANNING.name))
                } catch (e: Exception) {
                    PlanStatus.IN_PLANNING
                },
                isSimulation = root.optBoolean("isSimulation", false)
            )
        } catch (e: Exception) {
            null
        }
    }
}
