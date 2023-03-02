package com.nilsnett.testhelpers

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
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
import java.util.concurrent.atomic.AtomicReference

/** Waits until a give Espresso matcher is true. Checks every 100ms but times out after a given
 * timeout that is by default set to 10 seconds. Increase if network traffic is involved when waiting.
 */
fun waitUntil(timeoutSeconds: Long = 10, errorMessage: String? = null, condition: (() -> Boolean)) {
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

fun waitUntilNotThrowing(timeoutSeconds: Long = 10, action: (() -> Unit)) {
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

fun performUntil(condition: (() -> Boolean), action: () -> Unit, timeout: Long = 10) {
    waitUntil(timeout, condition = {
        if (!condition()) action.invoke()
        condition.invoke()
    })
}

/**
 * Waits for the matcher to be found and visible. Waits at most [timeoutSeconds] seconds.
 * Espresso will print the error message if it fails after the timeout
 */
fun waitAndCheckFor(matcher: Matcher<View>, timeoutSeconds: Long = 10): ViewInteraction {
    val interaction = Espresso.onView(matcher)
    val assertion = matches(ViewMatchers.isCompletelyDisplayed())
    repeatUntil(interaction, assertion, null, timeoutSeconds)
    return interaction
}

/**
 * Repeats optional [action] provided until the assertion succeeds or timeout happens
 */
fun repeatUntil(interaction: ViewInteraction, assertion: ViewAssertion, action: (() -> Unit)?, timeoutSeconds: Long = 10) {
    try {
        waitUntil(timeoutSeconds) {
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
        waitUntil(timeoutSeconds) {
            interaction.isDisplayed()
        }
    } catch (err: java.lang.AssertionError) {
        // Redo the visibility check for a better error message instead of just "Timeout"
        interaction.isDisplayed()
    }

    // Check that the text value is as expected
    interaction.check(matches(withText(text)))
}

fun waitForThenClick(viewMatcher: Matcher<View>, timeoutSeconds: Long = 10) {
    val viewInteraction = Espresso.onView(viewMatcher)
    waitAndCheckFor(viewMatcher, timeoutSeconds)
    viewInteraction.perform(ViewActions.click())
}

var waitMultiplicator: Int = 1

fun testWait(ms: Int) {
    // Added a sleep statement to match the app's execution delay.
    // The recommended way to handle such scenarios is to use Espresso idling resources:
    // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
    try {
        Thread.sleep(waitMultiplicator * ms.toLong())
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }
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
    waitUntil { activity != null }
    return activity!!
}

fun <T> ActivityScenarioRule<T>.getActivityBlocking() : T where T : Activity = scenario.getActivityBlocking()

class TimeoutAssertionError(message: String) : AssertionError(message)