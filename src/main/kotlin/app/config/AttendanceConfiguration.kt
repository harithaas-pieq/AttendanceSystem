package app.config

import io.dropwizard.core.Configuration
import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.db.DataSourceFactory
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull

class AttendanceConfiguration : Configuration() {

    @Valid
    @NotNull
    @JsonProperty("database")
    var database: DataSourceFactory = DataSourceFactory()
}
