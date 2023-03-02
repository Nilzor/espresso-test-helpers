package com.nilsnett.testhelpers

import android.app.Activity
import android.view.View
import androidx.annotation.IdRes
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.hamcrest.Matcher

/**
 * Waits until the [condition] function returns true to return. Blocks calling thread.
 * If [condition] is still false after [timeoutSeconds], a TimeoutAssertionError will be thrown,
 * optionally with custom [errorMessage]
 */
fun waitUntilTrue(timeoutSeconds: Long = 10, errorMessage: String? = null, condition: (() -> Boolean)) {
    val intervalMs = 100L
    val timeoutMs = timeoutSeconds * 1000L
    val timeStart = System.currentTimeMillis()
    do {
        if (condition.invoke()) return
        Thread.sleep(intervalMs)
        val timeSpent = System.currentTimeMillis() - timeStart
    } while (timeSpent < timeoutMs)
    var messageToPrint = "Timeout after $timeoutMs ms"
    if (errorMessage != null) messageToPrint = "$errorMessage - $messageToPrint"
    throw TimeoutAssertionError(messageToPrint)
}

/**
 * Executes the [action] until it no longer throws an exception.
 * If the action still throws after timeout, the exception will be not be caught.
 */
fun executeUntilNotThrowing(timeoutSeconds: Long = 10, action: (() -> Unit)) {
    val intervalMs = 100L
    val timeoutMs = timeoutSeconds * 1000L
    val timeStart = System.currentTimeMillis()
    do {
        if (tryCatchToBoolean { action.invoke() }) return
        Thread.sleep(intervalMs)
        val timeSpent = System.currentTimeMillis() - timeStart
    } while (timeSpent < timeoutMs)
    action.invoke()
}

/**
 * Executes the condition-checking lambda until it returns true. If it does not return true
 * within the timeout period a TimeoutAssertionError will be thrown
 */
fun assertTrueWithinTimeout(condition: (() -> Boolean), action: () -> Unit, timeoutSeconds: Long = 10) {
    waitUntilTrue(timeoutSeconds, condition = {
        if (!condition()) action.invoke()
        condition.invoke()
    })
}

/**
 * Waits for the [matcher] to be found and visible. Waits at most [timeoutSeconds] seconds.
 * Espresso will print the error message if it fails after the timeout
 */
fun checkForCompletelyDisplayed(matcher: Matcher<View>, timeoutSeconds: Long = 10): ViewInteraction {
    val interaction = Espresso.onView(matcher)
    val assertion = matches(ViewMatchers.isCompletelyDisplayed())
    executeUntilAssertionSucceeds(interaction, assertion, null, timeoutSeconds)
    return interaction
}

/**
 * Repeats optional [action] provided until the assertion succeeds or timeout happens
 */
fun executeUntilAssertionSucceeds(interaction: ViewInteraction, assertion: ViewAssertion, action: (() -> Unit)?, timeoutSeconds: Long = 10) {
    try {
        waitUntilTrue(timeoutSeconds) {
            action?.invoke()
            var success = true
            interaction
                .withFailureHandler { _, _ -> success = false }
                .check(assertion)
            success
        }
    } catch (err: TimeoutAssertionError) {
        // Catch and let check below produce better error message
    }

    interaction.check(assertion)
}

/**
 * Waits for the a view [viewId] to be displayed then checks the [text] value. Works for TextView, Button, Switch, among others.
 * Waits a maximum of [timeoutSeconds] seconds. If it fails then an Espresso-formatted error message is rendered
 */
fun waitAndCheckForText(@IdRes viewId: Int, text: String, timeoutSeconds: Long = 10) {
    // Check that the view exists and is visible
    val interaction = Espresso.onView(withId(viewId))
    try {
        waitUntilTrue(timeoutSeconds) {
            interaction.isDisplayed()
        }
    } catch (err: java.lang.AssertionError) {
        // Redo the visibility check for a better error message instead of just "Timeout"
        interaction.isDisplayed()
    }

    // Check that the text value is as expected
    interaction.check(matches(withText(text)))
}

/**
 * Waits for [viewMatcher] at most [timeoutSeconds] seconds to be visible then clicks it
 */
fun waitForThenClick(viewMatcher: Matcher<View>, timeoutSeconds: Long = 10) {
    val viewInteraction = Espresso.onView(viewMatcher)
    checkForCompletelyDisplayed(viewMatcher, timeoutSeconds)
    viewInteraction.perform(ViewActions.click())
}

fun ViewInteraction.isDisplayed(): Boolean {
    return tryCatchToBoolean {
        this.check(matches(ViewMatchers.isCompletelyDisplayed()))
    }
}

fun tryCatchToBoolean(action: () -> Unit): Boolean {
    return try {
        action()
        true
    } catch(ex: Throwable) {
        false
    }
}

fun <T> ActivityScenario<T>.getActivityBlocking() : T where T : Activity {
    var activity: T? = null
    this.onActivity { activity = it }
    waitUntilTrue { activity != null }
    return activity!!
}

fun <T> ActivityScenarioRule<T>.getActivityBlocking() : T where T : Activity = scenario.getActivityBlocking()

class TimeoutAssertionError(message: String) : AssertionError(message)