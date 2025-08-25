package app.model

import jakarta.validation.constraints.NotNull
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor
import org.jdbi.v3.core.mapper.reflect.ColumnName
import java.time.LocalDateTime
import java.util.UUID

data class Attendance @JdbiConstructor constructor(
    @ColumnName("attendance_id")
    val attendanceId: UUID = UUID.randomUUID(),

    @get:NotNull
    @ColumnName("employee_id")
    val employeeId: UUID,

    @get:NotNull
    @ColumnName("check_in_time")
    val checkInTime: LocalDateTime,

    @ColumnName("check_out_time")
    val checkOutTime: LocalDateTime? = null,

    @ColumnName("working_time")
    val workingTime: String? = null,


) {
    override fun toString(): String {
        return "Attendance(attendanceId=$attendanceId, employeeId=$employeeId, checkInTime=$checkInTime, checkOutTime=$checkOutTime, workingTime=$workingTime)"
    }
}
