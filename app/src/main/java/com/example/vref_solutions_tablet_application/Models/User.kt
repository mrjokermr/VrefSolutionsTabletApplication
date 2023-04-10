package com.example.vref_solutions_tablet_application.models

import com.example.vref_solutions_tablet_application.enums.UserType
import java.util.*

class User (
    val id: Long,
    val email: String,
    val firstName: String,
    val lastName: String,
    val organization: Organization,
    val userType: UserType
) {
    fun fullName(): String {
        val firstname = stringToUppercase(firstName)
        val lastname = stringToUppercase(lastName)

        return "$firstname $lastname"
    }

    fun getFirstNameAndInitialLastName(): String {
        return "${stringToUppercase(firstName)} ${stringToUppercase(lastName)[0]}."
    }

    private fun stringToUppercase(s: String): String {
        return s.replaceFirstChar { if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()) else it.toString() }
    }
}