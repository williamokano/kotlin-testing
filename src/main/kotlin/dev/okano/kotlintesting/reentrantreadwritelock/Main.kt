package dev.okano.kotlintesting.reentrantreadwritelock

import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.random.Random
import kotlin.time.measureTime

fun main() {
    val lock = ReentrantReadWriteLock()
    val readLock = lock.readLock()
    val writeLock = lock.writeLock()
    val threadPool = emptyList<Thread>().toMutableList()

    // ----
    val numbers = emptyList<Int>().toMutableList()
    var stopThreads = false
    threadPool.newThread {
        println("reader thread started")
        while (!stopThreads) {
            Thread.sleep(100)
            println("Attempting reading")
            val time = measureTime {
                readLock.lock()
                println(numbers)
                readLock.unlock()
            }
            println("Reading took $time")
        }
        println("reader thread ended")
    }

    // Writer
    threadPool.newThread {
        val sleepTime = 500L
        println("Writer thread started")
        while (!stopThreads) {
            println("Looping")
            writeLock.lock()
            Thread.sleep(Random.nextLong(1000) + 2000)
            writeLock.unlock()
            println("loop finished. Waiting $sleepTime")
            Thread.sleep(sleepTime)
            println("restarting")
        }
        println("Writer thread stopped")
    }

    threadPool.newThread {
        Thread.sleep(10000)
        stopThreads = true
    }
    // ----

    threadPool.startAndWaitAll()
    println("Program finished")
}

fun MutableList<Thread>.newThread(body: () -> Unit) {
    this.add(Thread { body() })
}

fun List<Thread>.startAndWaitAll() {
    println("Starting all threads")
    this.parallelStream().forEach { it.start() }
    this.parallelStream().forEach { it.join() }
    println("All threads finished")
}