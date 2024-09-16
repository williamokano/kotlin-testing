package dev.okano.kotlintesting.concurrenthashmap

import dev.okano.kotlintesting.helpers.newThread
import dev.okano.kotlintesting.helpers.startAndWaitAll
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import kotlin.random.Random

fun main() {
    val startedComputeCondition = CountDownLatch(1)

    val concurrentHashMap = ConcurrentHashMap<String, Int>()
    val sharedKey = "counter"
    concurrentHashMap[sharedKey] = 0

    val threadPool = emptyList<Thread>().toMutableList()

    threadPool.newThread {
        println("Starting thread1")

        startedComputeCondition.await()

        println("Trying reading counter thread1")
        println(concurrentHashMap[sharedKey])
        println("Finished thread1")
    }

    threadPool.newThread {
        println("Starting thread2")

        startedComputeCondition.await()

        println("Trying reading counter thread2")
        println(concurrentHashMap[sharedKey])
        println("Finished thread2")
    }

    threadPool.newThread {
        println("Starting threadFOO")

        startedComputeCondition.await()
        println("trying to compute shared key")
        concurrentHashMap.compute(sharedKey) { _, _ -> 1337 }
        println("computed shared key")
        println("Finished threadFOO")
    }

    threadPool.newThread {
        println("Starting thread4")
        concurrentHashMap.compute(sharedKey) { _, value ->
            println("Computing counter on thread4")

            // signal other threads that I'm in the critical area and wait some time
            // if the assumption is correct, reads should wait here
            Thread {
                val waitTimeMillis = 2000L
                println("Sleeping $waitTimeMillis ms before allowing other processing")
                Thread.sleep(waitTimeMillis)
                startedComputeCondition.countDown()
                println("Processing enabled")
            }.start()

            Thread.sleep(10000)
            if (value == null) {
                0
            } else {
                value + 1
            }
        }
        println("Finished thread4")
    }

    threadPool.newThread {
        println("Starting writer thread")

        startedComputeCondition.await()

        val value = Random.nextInt()
        println("trying to write $value to the $sharedKey key")
        concurrentHashMap[sharedKey] = value
        println("wrote $value to the $sharedKey key")
        println("Ending writer thread")
    }

    threadPool.newThread {
        println("Starting thread5")
        // poor-man-sync to make sure it runs after t4

        startedComputeCondition.await()

        println("Trying to computer key WOLOLO from thread5")
        concurrentHashMap.compute("WOLOLO") { _, _ ->
            println("Computed thread5")
            10
        }
        println("Finished thread5")
    }

    // Launch and await all
    threadPool.startAndWaitAll()

    println("All threads finished :)")
    println("Final value ${concurrentHashMap[sharedKey]}")
}