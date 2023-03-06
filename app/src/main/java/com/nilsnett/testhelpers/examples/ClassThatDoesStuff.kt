package com.nilsnett.testhelpers.examples

import com.nilsnett.testhelpers.Logger

class ClassThatDoesStuff(
    private val logger: Logger
) {

    fun doStuff() {
        logger.d("TAG", "Stuff is being done")
    }
}
/*
class ProdCode {
    val doer = ClassThatDoesStuff(LogcatLogger())
}

class TestCode {
    val doer = ClassThatDoesStuff(ConsoleLogger())
}

class LogcatLogger : Logger {
    override fun d(tag: String, s: String) {
        android.util.Log.d(tag, s)
    }
}

class ConsoleLogger : Logger {
    override fun d(tag: String, s: String) {
        println("$tag: $s")
    }
}*/