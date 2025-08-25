package app.model

import jakarta.validation.constraints.NotBlank
import java.util.UUID
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor
import org.jdbi.v3.core.mapper.reflect.ColumnName
data class Employee  @JdbiConstructor constructor(
    @ColumnName("employee_id")
    val employeeId: UUID = UUID.randomUUID(),

    @get:NotBlank
    @ColumnName("first_name")
    val firstName: String,

    @get:NotBlank
    @ColumnName("last_name")
    val lastName: String,

    @ColumnName("department")
    val department: Department,

    @ColumnName("role")
    val role: Role,

    @ColumnName("reporting_to")
    val reportingTo: UUID? = null
) {
    override fun toString(): String {
        return "Employee(employeeId=$employeeId, firstName='$firstName', lastName='$lastName', department=$department, role=$role, reportingTo=$reportingTo)"
    }
}

