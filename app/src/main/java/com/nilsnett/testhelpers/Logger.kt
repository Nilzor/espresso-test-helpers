package com.nilsnett.testhelpers

interface Logger  {
    fun d(tag: String, s: String)
    fun d(tag: String, msg: String, vararg args: Any)
    fun i(tag: String, s: String)
    fun w(tag: String, s: String)
    fun w(tag: String, s: String, t: Throwable)
    fun e(tag: String, s: String)
    fun e(tag: String, s: String, t: Throwable)
    fun wtf(tag: String, s: String, t: Throwable)
}