package com.weatherdemo

import androidx.test.InstrumentationRegistry.getTargetContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.util.regex.Pattern

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
      //  val appContext = InstrumentationRegistry.getTargetContext()
        //assertEquals("com.weatherdemo", appContext.packageName)
    }

    @Test
    @Throws(Exception::class)
    fun checkLocationFetched() {
        // Confirm nav to DetailFragment and display title
     //   Espresso.onView(ViewMatchers.withId(R.id.txt_city_name)).check(Pattern.matches(""))

    }


}
