package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.JsonHelper
import com.example.data.PresetData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("NEXORA AI", appName)
  }

  @Test
  fun `verify starter tool serialization and deserialization`() {
    val starter = PresetData.getStarterProjects().first()
    val json = JsonHelper.toolSpecToJson(starter)
    assertNotNull(json)
    val parsed = JsonHelper.jsonToToolSpec(json)
    assertNotNull(parsed)
    assertEquals(starter.name, parsed?.name)
    assertEquals(starter.category, parsed?.category)
  }
}
