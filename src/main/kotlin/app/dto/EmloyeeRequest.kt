package app.dto

data class EmployeeRequest(
    val firstName: String,
    val lastName: String,
    val role: String,
    val department: String,
    val reportingTo: String? = null // UUID in String form if provided
)
