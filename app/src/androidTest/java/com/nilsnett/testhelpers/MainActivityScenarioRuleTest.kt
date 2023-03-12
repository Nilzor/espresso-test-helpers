package com.nilsnett.testhelpers

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
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
class MainActivityScenarioRuleTest {
   // OkHttp3IdlingResource.create("OkHttp", client) Mention why I don't use this
    // - Specific per use case (not all delays are OkHttp). Cumbersome

    @get:Rule
    val scenario = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun scenario1() {
        scenario.scenario.onActivity { act ->
            // This deadlocks
            onView(withId(R.id.loadButton)).check(matches(ViewMatchers.isDisplayed()))
        }
    }


    @Test
    fun scenario2() {
        // This works but you don't have access to the activity
        onView(withId(R.id.loadButton)).check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun scenario3() {
        // This works AND you have access to the activity.
        val activity = scenario.scenario.getActivityBlocking()
        onView(withId(R.id.loadButton)).check(matches(ViewMatchers.isDisplayed()))
    }
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
fun contentTextView_immediately() {
    onView(withId(R.id.loadButton)).perform(click())
    onView(withId(R.id.contentTextView)).check(matches(withText("Content")))
}

    @Test
    fun contentTextView_withSleep() {
        onView(withId(R.id.loadButton)).perform(click())
        Thread.sleep(3000)
        onView(withId(R.id.contentTextView)).check(matches(withText("Content")))
    }

    @Test
    fun contentTextView_withAdaptiveSleep() {
        onView(withId(R.id.loadButton)).perform(click())
        waitUntilNotThrowing(timeoutSeconds = 10) {
            onView(withId(R.id.contentTextView)).check(matches(withText("Content")))
        }
    }

    @Test
    fun executeUntilNotThrowing_waiting_for_text_content() {
        onView(withId(R.id.loadButton)).perform(click())
        waitUntilNotThrowing(timeoutSeconds = 3) {
            onView(withId(R.id.contentTextView)).check(matches(withText("Content")))
        }
    }

    @Test
    fun executeUntilNotThrowing_waiting_for_text_content2() {
        onView(withId(R.id.loadButton)).perform(click())
        executeUntilAssertionSucceeds(withId(R.id.contentTextView), matches(withText("Content")), { }, 3)
    }

    @Test
    fun waitAndCheckForText() {
        onView(withId(R.id.loadButton)).perform(click())
        waitAndCheckForText(R.id.contentTextView, "Content", timeoutSeconds = 4)
    }
}