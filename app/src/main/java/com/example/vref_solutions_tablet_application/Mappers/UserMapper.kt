package com.example.vref_solutions_tablet_application.mappers

import com.example.vref_solutions_tablet_application.models.User
import com.example.vref_solutions_tablet_application.api.responseEntities.LoginResponseEntity
import com.example.vref_solutions_tablet_application.api.responseEntities.UserResponseEntity

object UserMapper {
    fun mapLoginResponseUser(entity: LoginResponseEntity): Result<User> = runCatching {
        User(
            id = entity.user!!.id!!,
            email = entity.user!!.email!!,
            firstName = entity.user!!.firstName!!,
            lastName = entity.user!!.lastName!!,
            organization = entity.user!!.organization!!,
            userType = entity.user!!.userType!!
        )
    }

    fun map(entity: UserResponseEntity): Result<User> = runCatching {
        User(
            id = entity.id!!,
            email = entity.email!!,
            firstName = entity.firstName!!,
            lastName = entity.lastName!!,
            organization = entity.organization!!,
            userType = entity.userType!!
        )
    }


    fun mapList(userEntityList: List<UserResponseEntity>): List<User> {
        val mappedList: MutableList<User> = emptyList<User>().toMutableList()

        for(userEntity in userEntityList) {
            map(entity = userEntity).onSuccess {
                mappedList.add(it)
            }
        }

        return mappedList
    }
}