package app

import app.config.AttendanceConfiguration
import app.dao.EmployeeDAO
import app.dao.AttendanceDAO
import app.service.EmployeeService
import app.service.AttendanceService
import app.resource.EmployeeResource
import app.resource.AttendanceResource
import io.dropwizard.core.Application
import io.dropwizard.core.setup.Bootstrap
import io.dropwizard.core.setup.Environment
import io.dropwizard.jdbi3.JdbiFactory
import org.eclipse.jetty.servlets.CrossOriginFilter
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.jdbi.v3.core.kotlin.KotlinPlugin
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.databind.SerializationFeature
import jakarta.servlet.DispatcherType
import java.util.EnumSet

class AttendanceApplication : Application<AttendanceConfiguration>() {

    override fun initialize(bootstrap: Bootstrap<AttendanceConfiguration>) {
        // Register Kotlin  Java Time Modules for JSON
        bootstrap.objectMapper.registerModule(kotlinModule())
        bootstrap.objectMapper.registerModule(JavaTimeModule())
        bootstrap.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    override fun run(configuration: AttendanceConfiguration, environment: Environment) {
        // Setup JDBI
        val factory = JdbiFactory()
        val jdbi: Jdbi = factory.build(environment, configuration.database, "postgresql")
        jdbi.installPlugin(SqlObjectPlugin())
        jdbi.installPlugin(KotlinPlugin())
        val employeeDAO = jdbi.onDemand(EmployeeDAO::class.java)


        // Initialize Services
        val employeeService = EmployeeService(employeeDAO)

        // Register Resources
        environment.jersey().register(EmployeeResource(employeeService))

        val attendanceDAO = jdbi.onDemand(AttendanceDAO::class.java)
        val attendanceService = AttendanceService(attendanceDAO)
        environment.jersey().register(AttendanceResource(attendanceService))

        // Register Health Check
        environment.healthChecks().register("basic", BasicHealthCheck())

        // Configure CORS
        val cors = environment.servlets().addFilter("CORS", CrossOriginFilter::class.java)
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*")
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin,Authorization")
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD")
        cors.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true")
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType::class.java), true, "/*")
    }
}

fun main(args: Array<String>) {
    AttendanceApplication().run(*args)
}
