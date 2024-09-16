package dev.okano.kotlintesting.helpers

fun MutableList<Thread>.newThread(body: () -> Unit) {
    this.add(Thread { body() })
}

fun List<Thread>.startAndWaitAll() {
    println("Starting all threads")
    this.parallelStream().forEach { it.start() }
    this.parallelStream().forEach { it.join() }
    println("All threads finished")
}