package com.example.vref_solutions_tablet_application.Models

import com.example.vref_solutions_tablet_application.Enums.UserType
import java.util.*

class User (
    val id: Long,
    val email: String,
    val firstName: String,
    val lastName: String,
    val organization: Organization,
    val userType: UserType
) {
    fun FullName(): String {
        val firstname = StringToUppercase(firstName)
        val lastname = StringToUppercase(lastName)

        return "$firstname $lastname"
    }

    fun GetFirstNameAndInitialLastName(): String {
        return "${StringToUppercase(firstName)} ${StringToUppercase(lastName)[0]}."
    }

    private fun StringToUppercase(s: String): String {
        return s.replaceFirstChar { if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()) else it.toString() }
    }
}