package com.nilsnett.testhelpers.examples

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

class DispatcherMocking(var dispatcher: CoroutineDispatcher) : ViewModel() {
    suspend fun doStuff() {
        withContext(dispatcher) {

        }
    }
}