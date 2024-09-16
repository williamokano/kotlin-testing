package dev.okano.kotlintesting.helpers

import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Duration
import java.time.Instant

class MutableClockTest {

    @Test
    fun `should keep time static`() {
        val clock = MutableClock(Instant.parse("2024-09-19T14:00:00Z"))
        val beforeSleep = clock.instant()
        Thread.sleep(50)
        val afterSleep = clock.instant()

        afterSleep shouldBeEqual beforeSleep
    }

    @Test
    fun `should advance time using regular clock`() {
        val clock = Clock.systemUTC()
        val beforeSleep = clock.instant()
        Thread.sleep(50)
        val afterSleep = clock.instant()

        afterSleep shouldNotBeEqual beforeSleep
    }

    @Test
    fun `should be able to advance time`() {
        val clock = MutableClock(Instant.parse("2024-09-19T14:00:00Z"))
        clock.advance(Duration.ofMinutes(15))

        clock.instant() shouldBeEqual Instant.parse("2024-09-19T14:15:00Z")
    }

    @Test
    fun `should be able to rewind time`() {
        val clock = MutableClock(Instant.parse("2024-09-19T14:15:00Z"))
        clock.rewind(Duration.ofMinutes(13))

        clock.instant() shouldBeEqual Instant.parse("2024-09-19T14:02:00Z")
    }
}