package app.model
import com.fasterxml.jackson.annotation.JsonProperty

enum class Role {
    @JsonProperty("manager")
    MANAGER,

    @JsonProperty("developer")
    DEVELOPER,

    @JsonProperty("tester")
    TESTER,

    @JsonProperty("intern")
    INTERN;
    companion object {
        fun fromName(name: String): Role {
            return try {
                valueOf(name.uppercase())
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid role: $name")
            }
        }
    }
}

