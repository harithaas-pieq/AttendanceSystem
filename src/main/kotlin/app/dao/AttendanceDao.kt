package app.dao

import app.model.Attendance
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import java.util.UUID
import java.time.LocalDateTime

@RegisterConstructorMapper(Attendance::class)
interface AttendanceDAO {

    @SqlUpdate("""
        INSERT INTO attendance (attendance_id, employee_id, check_in_time)
        VALUES (:attendanceId, :employeeId, :checkInTime)
    """)
    fun checkIn(@BindBean attendance: Attendance)

    @SqlUpdate("""
        UPDATE attendance
        SET check_out_time = :checkOutTime, working_time = :workingTime
        WHERE employee_id = :employeeId AND check_out_time IS NULL
    """)
    fun checkOut(
        @Bind("employeeId") employeeId: UUID,
        @Bind("checkOutTime") checkOutTime: LocalDateTime,
        @Bind("workingTime") workingTime: String
    ): Int

    @SqlQuery("""
        SELECT * FROM attendance
        WHERE employee_id = :employeeId AND check_out_time IS NULL
        LIMIT 1
    """)
    fun getActiveCheckIn(@Bind("employeeId") employeeId: UUID): Attendance?

    @SqlQuery("SELECT * FROM attendance ORDER BY check_in_time DESC")
    fun getAll(): List<Attendance>

    @SqlUpdate("DELETE FROM attendance WHERE attendance_id = :attendanceId")
    fun delete(@Bind("attendanceId") attendanceId: UUID): Int

}
