package dev.okano.kotlintesting.helpers

import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class MutableClock(private var instant: Instant, private val zone: ZoneId = ZoneId.of("UTC")) : Clock() {
    override fun instant(): Instant = instant

    override fun withZone(zone: ZoneId): Clock = MutableClock(instant, zone)

    override fun getZone(): ZoneId = zone

    fun rewind(duration: Duration): MutableClock = apply {
        instant = instant.minus(duration)
    }

    fun rewind(amount: Long, unit: ChronoUnit) = apply {
        instant = instant.minus(amount, unit)
    }

    fun advance(duration: Duration) = apply {
        instant = instant.plus(duration)
    }

    fun advance(amount: Long, unit: ChronoUnit) = apply {
        instant = instant.plus(amount, unit)
    }
}