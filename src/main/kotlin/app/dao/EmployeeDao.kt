package app.dao

import app.model.Employee
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import java.util.UUID

@RegisterConstructorMapper(Employee::class)
interface EmployeeDAO {

    @SqlUpdate("""
        INSERT INTO employees (employee_id, first_name, last_name, department, role, reporting_to)
        VALUES (:employeeId, :firstName, :lastName, :department, :role, :reportingTo)
    """)
    fun insert(@BindBean employee: Employee)

    @SqlQuery("SELECT * FROM employees WHERE employee_id = :id")
    fun findById(@Bind("id") id: UUID): Employee?


    @SqlQuery("SELECT * FROM employees LIMIT :limit")
    fun getAll(@Bind("limit") limit: Int): List<Employee>

    @SqlUpdate("DELETE FROM employees WHERE employee_id = :id")
    fun delete(@Bind("id") id: UUID): Int
}
