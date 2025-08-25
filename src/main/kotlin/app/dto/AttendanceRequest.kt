package app.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class AttendanceRequest(
    @get:NotNull
    @JsonProperty("employee_id")
    val employeeId: String,
    val dateTime: String? = null
)
