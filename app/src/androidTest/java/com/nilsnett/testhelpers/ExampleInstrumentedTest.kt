package com.nilsnett.testhelpers

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
   // OkHttp3IdlingResource.create("OkHttp", client) Mention why I don't use this
    // - Specific per use case (not all delays are OkHttp). Cumbersome

    @get:Rule
    val scenario = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testWaitForThenClick_onElementNotPresent() {
        waitForThenClick(withId(R.id.textInSecondScreen))
    }

    @Test
    fun testWaitAndCheckForText_elementGoneThenVisibleWithCorrectText() {
        onView(withId(R.id.loadButton)).perform(click())
        waitAndCheckForText(R.id.loadingDoneIndicator, "Loading is done")
    }

    @Test
    fun executeUntilNotThrowing_waiting_for_text_content() {
        onView(withId(R.id.loadButton)).perform(click())
        executeUntilNotThrowing(timeoutSeconds = 3) {
            onView(withId(R.id.loadTarget)).check(matches(withText("Content")))
        }
    }

    @Test
    fun waitAndCheckForText() {
        onView(withId(R.id.loadButton)).perform(click())
        // executeUntilNotThrowing_waiting_for_text_content Simplified to..:
        waitAndCheckForText(R.id.loadTarget, "Content", timeoutSeconds = 4)
    }
}