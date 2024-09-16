package dev.okano.kotlintesting.concurrenthashmap

import java.util.concurrent.ConcurrentHashMap

fun main() {
    val concurrentHashMap = ConcurrentHashMap<String, Int>()
    val sharedKey = "counter"
    concurrentHashMap[sharedKey] = 0

    val allThreads = emptyList<Thread>().toMutableList()

    Thread {
        println("Starting thread1")
        Thread.sleep(2000)
        println("Trying reading counter thread1")
        println(concurrentHashMap[sharedKey])
        println("Finished thread1")
    }.also { allThreads.add(it) }

//    Thread {
//        println("Starting thread2")
//        Thread.sleep(2000)
//        println("Trying reading counter thread2")
//        println(concurrentHashMap[sharedKey])
//        println("Finished thread2")
//    }.also { allThreads.add(it) }
//
//    Thread {
//        println("Starting thread3")
//        Thread.sleep(1000)
//        println("Trying to write counter thread3")
//        concurrentHashMap[sharedKey] = 9999
//        println("Finished thread3")
//    }.also { allThreads.add(it) }

    Thread {
        println("Starting thread4")
        concurrentHashMap.compute(sharedKey) { _, value ->
            println("Computing counter on thread4")
            // Forcefully block for 1sec to check blocking on other threads
            Thread.sleep(10000)
            if (value == null) {
                0
            } else {
                value + 1
            }
        }
        println("Finished thread4")
    }.also { allThreads.add(it) }

//    Thread {
//        println("Starting thread5")
//        // poor-man-sync to make sure it runs after t4
//        Thread.sleep(1000)
//        println("Trying to computer key WOLOLO from thread5")
//        concurrentHashMap.compute("WOLOLO") { _, _ ->
//            println("Computed thread5")
//            10
//        }
//        println("Finished thread5")
//    }.also { allThreads.add(it) }

    // Launch and await all
    allThreads.parallelStream().forEach { it.start() }
    allThreads.parallelStream().forEach { it.join() }

    println("All threads finished :)")
}