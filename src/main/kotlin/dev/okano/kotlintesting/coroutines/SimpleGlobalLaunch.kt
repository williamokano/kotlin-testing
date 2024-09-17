package dev.okano.kotlintesting.coroutines

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch

fun main() {
    val latch = CountDownLatch(1)

    GlobalScope.launch {
        println("Hello World")
        delay(500)
        latch.countDown()
    }

    latch.await()
    println("Main finished")
}