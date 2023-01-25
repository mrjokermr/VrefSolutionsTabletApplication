package com.example.vref_solutions_tablet_application.Mappers

import com.example.vref_solutions_tablet_application.Models.User
import com.example.vref_solutions_tablet_application.API.ResponseEntities.LoginResponseEntity
import com.example.vref_solutions_tablet_application.API.ResponseEntities.UserResponseEntity
import com.example.vref_solutions_tablet_application.Models.TrainingEvent

class UserMapper {

    companion object {
        fun MapLoginResponseUser(entity: LoginResponseEntity): Result<User> = runCatching {

            User(
                id = entity.user!!.id!!,
                email = entity.user!!.email!!,
                firstName = entity.user!!.firstName!!,
                lastName = entity.user!!.lastName!!,
                organization = entity.user!!.organization!!,
                userType = entity.user!!.userType!!
            )
        }

        fun Map(entity: UserResponseEntity): Result<User> = runCatching {
            User(
                id = entity.id!!,
                email = entity.email!!,
                firstName = entity.firstName!!,
                lastName = entity.lastName!!,
                organization = entity.organization!!,
                userType = entity.userType!!
            )
        }


        fun MapList(userEntityList: List<UserResponseEntity>): List<User> {
            val mappedList: MutableList<User> = emptyList<User>().toMutableList()

            for(userEntity in userEntityList) {
                Map(entity = userEntity).onSuccess {
                    mappedList.add(it)
                }
            }

            return mappedList
        }
    }


}