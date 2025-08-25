package app.resource

import app.dto.AttendanceRequest
import app.model.Attendance
import app.model.ApiResponse
import app.service.AttendanceService
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.UUID

@Path("/api/v1/attendance")
@Produces(MediaType.APPLICATION_JSON)
class AttendanceResource(private val service: AttendanceService) {

    private val log = LoggerFactory.getLogger(AttendanceResource::class.java)

    @POST
    @Path("/checkin")
    fun checkIn(@Valid request: AttendanceRequest): Response {
        val employeeId = uuidValidator(request.employeeId)
            ?: return errorResponse("Invalid employeeId UUID format")

        val checkInDateTime = request.dateTime?.let {
            try { LocalDateTime.parse(it) } catch (_: Exception) {
                return errorResponse("Invalid datetime format, use yyyy-MM-dd'T'HH:mm")
            }
        } ?: LocalDateTime.now()

        val attendance = service.checkIn(employeeId, checkInDateTime)
            ?: return Response.status(Response.Status.BAD_REQUEST)
                .entity(ApiResponse(false, "Employee has already checked in for ${checkInDateTime.toLocalDate()}", null))
                .build()

        return Response.status(Response.Status.CREATED)
            .entity(ApiResponse(true, "Check-in successful", attendance))
            .build()
    }

    @PUT
    @Path("/{employeeId}/checkout")
    fun checkOut(
        @PathParam("employeeId") employeeId: String,
        @QueryParam("date_time") dateTimeStr: String? = null
    ): Response {
        val empId = uuidValidator(employeeId) ?: return errorResponse("Invalid employeeId UUID format")

        val checkOutDateTime = dateTimeStr?.let {
            try { LocalDateTime.parse(it) } catch (_: Exception) { return errorResponse("Invalid datetime format, use yyyy-MM-dd'T'HH:mm") }
        } ?: LocalDateTime.now()

        val updatedAttendance = service.checkOut(empId, checkOutDateTime)
            ?: return Response.status(Response.Status.BAD_REQUEST)
                .entity(ApiResponse(false, "No active check-in found for ${checkOutDateTime.toLocalDate()}", null))
                .build()

        return Response.ok(ApiResponse(true, "Check-out successful", updatedAttendance)).build()
    }

    @GET
    fun getAllAttendance(): Response =
        Response.ok(ApiResponse(true, "Attendance records fetched", service.getAllAttendance())).build()

    @GET
    @Path("/employee/{employeeId}")
    fun getAttendanceByEmployee(@PathParam("employeeId") employeeId: String): Response {
        val empId = uuidValidator(employeeId) ?: return errorResponse("Invalid UUID format")
        return Response.ok(ApiResponse(true, "Attendance records for employee fetched", service.getAttendanceByEmployee(empId))).build()
    }

    @GET
    @Path("/{attendanceId}")
    fun getAttendance(@PathParam("attendanceId") attendanceId: String): Response {
        val attId = uuidValidator(attendanceId) ?: return errorResponse("Invalid UUID format")
        val record = service.getAttendance(attId) ?: return Response.status(Response.Status.NOT_FOUND)
            .entity(ApiResponse(false, "Attendance record not found", null))
            .build()

        return Response.ok(ApiResponse(true, "Attendance record found", record)).build()
    }

    @DELETE
    @Path("/{attendanceId}")
    fun deleteAttendance(@PathParam("attendanceId") attendanceId: String): Response {
        val attId = uuidValidator(attendanceId) ?: return errorResponse("Invalid UUID format")
        val deleted = service.deleteAttendance(attId)

        return if (deleted)
            Response.ok(ApiResponse(true, "Attendance record deleted", true)).build()
        else
            Response.status(Response.Status.NOT_FOUND)
                .entity(ApiResponse(false, "Attendance record not found", false))
                .build()
    }

    @GET
    @Path("/employee/{employeeId}/summary")
    fun getWorkingHoursSummary(
        @PathParam("employeeId") employeeId: String,
        @QueryParam("date_time") dateTimeStr: String? = null
    ): Response {
        val empId = uuidValidator(employeeId) ?: return errorResponse("Invalid UUID format")

        val dateTime = dateTimeStr?.let {
            try { LocalDateTime.parse(it) } catch (_: Exception) { return errorResponse("Invalid datetime format, use yyyy-MM-dd'T'HH:mm") }
        }

        val summary = service.getWorkingHoursSummary(empId, dateTime)
        return Response.ok(ApiResponse(true, "Working hours summary fetched", summary)).build()
    }

    private fun uuidValidator(id: String): UUID? = try { UUID.fromString(id) } catch (_: IllegalArgumentException) { null }

    private fun errorResponse(message: String): Response =
        Response.status(Response.Status.BAD_REQUEST)
            .entity(ApiResponse(false, message, null))
            .build()
}
