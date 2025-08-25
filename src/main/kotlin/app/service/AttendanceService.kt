package app.service

import app.dao.AttendanceDAO
import app.model.Attendance
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID

class AttendanceService(private val attendanceDAO: AttendanceDAO) {

    private val log = LoggerFactory.getLogger(AttendanceService::class.java)

    /** Check-in with manual or system datetime */
    fun checkIn(employeeId: UUID, checkInDateTime: LocalDateTime): Attendance? {
        val date = checkInDateTime.toLocalDate()
        val existingCheckIn = attendanceDAO.getAll().find {
            it.employeeId == employeeId && it.checkInTime.toLocalDate() == date && it.checkOutTime == null
        }

        if (existingCheckIn != null) return null

        val attendance = Attendance(
            attendanceId = UUID.randomUUID(),
            employeeId = employeeId,
            checkInTime = checkInDateTime
        )

        attendanceDAO.checkIn(attendance)
        log.info("Checked in: $attendance")
        return attendance
    }

    /** Check-out with manual or system datetime */
    fun checkOut(employeeId: UUID, checkOutDateTime: LocalDateTime): Attendance? {
        val date = checkOutDateTime.toLocalDate()
        val active = attendanceDAO.getAll().find {
            it.employeeId == employeeId && it.checkInTime.toLocalDate() == date && it.checkOutTime == null
        } ?: return null

        val duration = Duration.between(active.checkInTime, checkOutDateTime)
        val workingTime = "${duration.toHours()}H ${duration.toMinutes() % 60}M"

        attendanceDAO.checkOut(active.attendanceId, checkOutDateTime, workingTime)
        val updatedAttendance = active.copy(checkOutTime = checkOutDateTime, workingTime = workingTime)
        log.info("Checked out: $updatedAttendance")
        return updatedAttendance
    }

    fun getAttendanceByEmployee(employeeId: UUID): List<Attendance> =
        attendanceDAO.getAll().filter { it.employeeId == employeeId }

    fun getAttendance(attendanceId: UUID): Attendance? =
        attendanceDAO.getAll().find { it.attendanceId == attendanceId }

    fun deleteAttendance(attendanceId: UUID): Boolean =
        try {
            attendanceDAO.delete(attendanceId) > 0
        } catch (e: Exception) {
            log.error("Error deleting attendance", e)
            false
        }

    fun getWorkingHoursSummary(employeeId: UUID, date: LocalDateTime? = null): String {
        val records = attendanceDAO.getAll().filter { it.employeeId == employeeId }
        val filtered = date?.toLocalDate()?.let { d -> records.filter { it.checkInTime.toLocalDate() == d } } ?: records

        val totalMinutes = filtered.map {
            val checkOut = it.checkOutTime ?: LocalDateTime.now()
            Duration.between(it.checkInTime, checkOut).toMinutes()
        }.sum()

        return "${totalMinutes / 60}H ${totalMinutes % 60}M"
    }

    fun getAllAttendance(): List<Attendance> = attendanceDAO.getAll()
}
