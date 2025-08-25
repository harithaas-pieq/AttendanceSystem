package app.resource

import app.dto.EmployeeRequest
import app.model.Department
import app.model.Role
import app.service.EmployeeService
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.slf4j.LoggerFactory
import java.util.UUID

@Path("/api/v1/employees")
@Produces(MediaType.APPLICATION_JSON)
class EmployeeResource(private val employeeService: EmployeeService) {

    private val log = LoggerFactory.getLogger(EmployeeResource::class.java)

    @POST
    fun createEmployee(@Valid request: EmployeeRequest): Response {
        try {
            val roleEnum = Role.fromName(request.role.trim())
                ?: return errorResponse("Invalid role '${request.role}'")

            val departmentEnum = Department.fromName(request.department.trim())
                ?: return errorResponse("Invalid department '${request.department}'")

            val reportingToId: UUID? = request.reportingTo?.let {
                uuidValidator(it) ?: return errorResponse("Invalid reportingTo UUID format")
            }

            val emp = employeeService.createEmployee(
                firstName = request.firstName.trim(),
                lastName = request.lastName.trim(),
                role = roleEnum,
                department = departmentEnum,
                reportingTo = reportingToId
            )

            return Response.status(Response.Status.CREATED)
                .entity(mapOf("status" to "success", "message" to "Employee created successfully", "data" to emp))
                .build()

        } catch (e: IllegalArgumentException) {
            return errorResponse(e.message ?: "Invalid input")
        } catch (e: Exception) {
            log.error("Error creating employee", e)
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("status" to "error", "message" to (e.message ?: "Unknown error")))
                .build()
        }
    }

    @GET
    @Path("/{id}")
    fun getEmployeeById(@PathParam("id") id: String): Response {
        val uuid = uuidValidator(id) ?: return errorResponse("Invalid UUID format")

        val emp = employeeService.getEmployee(uuid)
        return if (emp != null) {
            Response.ok(mapOf("status" to "success", "data" to emp)).build()
        } else {
            Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("status" to "error", "message" to "Employee not found"))
                .build()
        }
    }

    @GET
    fun listEmployees(@QueryParam("limit") @DefaultValue("20") limit: Int): Response {
        val employees = employeeService.getAllEmployees(limit)
        return Response.ok(mapOf("status" to "success", "data" to employees)).build()
    }

    @DELETE
    @Path("/{id}")
    fun deleteEmployee(@PathParam("id") id: String): Response {
        val uuid = uuidValidator(id) ?: return errorResponse("Invalid UUID format")

        val deleted = employeeService.deleteEmployee(uuid)
        return if (deleted) {
            Response.ok(mapOf("status" to "success", "message" to "Employee deleted successfully")).build()
        } else {
            Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("status" to "error", "message" to "Employee not found"))
                .build()
        }
    }

    private fun uuidValidator(id: String): UUID? {
        return try {
            UUID.fromString(id)
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    private fun errorResponse(message: String): Response {
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(mapOf("status" to "error", "message" to message))
            .build()
    }
}
