package app.model

import com.fasterxml.jackson.annotation.JsonProperty

enum class Department {
    @JsonProperty("hr")
    HR,

    @JsonProperty("engineering")
    ENGINEERING,

    @JsonProperty("sales")
    SALES,

    @JsonProperty("marketing")
    MARKETING;
    companion object {
        fun fromName(name: String): Department {
            return try {
                valueOf(name.uppercase())
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid department: $name")
            }
        }
    }
}
