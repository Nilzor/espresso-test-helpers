package com.nilsnett.testhelpers

import android.util.Log
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.ActivityAction
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.junit.Test

class MainActivityScenarioTest {
    @Test
    fun testScenarioManually() {
        // This causes a deadlock. UI freezes after checkpoint 2
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity(ActivityAction<MainActivity> { activity: MainActivity ->
                Log.d("TEST", "checkpoint 2")
                val viewInteraction = Espresso.onView(ViewMatchers.withId(R.id.loadButton))
                Log.d("TEST", "checkpoint 2")
                viewInteraction.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                Log.d("TEST", "checkpoint 3")
            })
        }

    }
}