package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.actions.SystemActionDispatcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Maya AI", appName)
  }

  @Test
  fun `verify package mappings`() {
    assertEquals("com.google.android.youtube", SystemActionDispatcher.APP_PACKAGES["youtube"])
    assertEquals("com.facebook.katana", SystemActionDispatcher.APP_PACKAGES["facebook"])
    assertEquals("com.zhiliaoapp.musically", SystemActionDispatcher.APP_PACKAGES["tiktok"])
    assertEquals("com.imo.android.imoim", SystemActionDispatcher.APP_PACKAGES["imo"])
  }
}
