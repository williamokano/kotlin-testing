package dev.okano.kotlintesting.coroutines

import dev.okano.kotlintesting.helpers.sha512
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch
import kotlin.random.Random

fun main() {
    val channel = Channel<String>()
    val websites = listOf(
        "https://www.google.com",
        "https://www.wikipedia.org",
        "https://www.youtube.com",
        "https://www.github.com",
        "https://stackoverflow.com",
        "https://www.reddit.com",
        "https://www.twitter.com",
        "https://www.facebook.com",
        "https://www.linkedin.com",
        "https://www.amazon.com",
        "https://www.netflix.com",
        "https://www.bbc.com",
        "https://www.cnn.com",
        "https://www.nytimes.com",
        "https://www.quora.com",
        "https://www.medium.com",
        "https://www.reddit.com/r/programming",
        "https://www.github.com/trending",
        "https://www.stackexchange.com",
        "https://www.theguardian.com"
    )

    val numOfWorkers = 4
    val latch = CountDownLatch(numOfWorkers + 1)

    GlobalScope.launch {
        for (website in websites) {
            channel.send(website)
        }
        channel.close()
    }

    val workersChannels = emptyList<Channel<Triple<Int, String, String>>>().toMutableList()
    repeat(numOfWorkers) { idx ->
        workersChannels.add(worker(latch) {
            for (website in channel) {
//                println("COROUTINE ${idx}: Consuming website $website")
                delay(Random.nextLong(300, 700))
                output.send(Triple(idx, website, website.sha512()))
            }
        })
    }

    GlobalScope.launch {
        for ((idx, website, sha512) in workersChannels.merge()) {
            println("WORKER $idx | Website: $website -> SHA-512: $sha512")
        }

        // Hacky just to wait for this as well
        latch.countDown()
    }

    println("Will wait for results")
    latch.await()
    println("Finished")
}

fun <T> List<Channel<T>>.merge(): Channel<T> {
    val mergedChannel = Channel<T>()
    val latch = CountDownLatch(size)

    forEach { inputChannel ->
        GlobalScope.launch {
            for (message in inputChannel) {
                mergedChannel.send(message)
            }
            latch.countDown()
        }
    }

    GlobalScope.launch {
        latch.await()
        mergedChannel.close()
    }

    return mergedChannel
}

class WorkerContext<T>(val output: Channel<T>)

fun <T> worker(latch: CountDownLatch, body: suspend WorkerContext<T>.() -> Unit): Channel<T> {
    val output = Channel<T>()
    val workerContext = WorkerContext(output)

    GlobalScope.launch {
        var throwable: Throwable? = null

        try {
            workerContext.body()
        } catch (e: Throwable) {
            throwable = e
        } finally {
            latch.countDown()
            output.close(throwable)
        }
    }

    return output
}