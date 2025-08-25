package app.service

import app.dao.EmployeeDAO
import app.model.Employee
import app.model.Role
import app.model.Department
import org.slf4j.LoggerFactory
import java.util.UUID

class EmployeeService(private val employeeDAO: EmployeeDAO) {

    private val log = LoggerFactory.getLogger(EmployeeService::class.java)

    fun createEmployee(
        firstName: String,
        lastName: String,
        role: Role,
        department: Department,
        reportingTo: UUID?
    ): Employee {
        if (firstName.isBlank() || lastName.isBlank()) {
            throw IllegalArgumentException("First and last name cannot be empty")
        }

        val emp = Employee(
            employeeId = UUID.randomUUID(),
            firstName = firstName,
            lastName = lastName,
            department = department,
            role = role,
            reportingTo = reportingTo
        )

        employeeDAO.insert(emp)
        log.info("Employee created: $emp")
        return emp
    }

    fun getEmployee(id: UUID): Employee? {
        return employeeDAO.findById(id)
    }

    fun getAllEmployees(limit: Int = 20): List<Employee> {
        return employeeDAO.getAll(limit)
    }

    fun deleteEmployee(id: UUID): Boolean {
        val deleted = employeeDAO.delete(id)
        return deleted > 0
    }
}
