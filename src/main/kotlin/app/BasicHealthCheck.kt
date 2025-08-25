package app

import com.codahale.metrics.health.HealthCheck

class BasicHealthCheck : HealthCheck() {

    override fun check(): Result {
        return Result.healthy()
    }
}