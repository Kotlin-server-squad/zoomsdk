package com.kss.zoom.sdk.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class User(
    val id: UserId,
    val email: Email,
    val firstName: String,
    val lastName: String,
    val userType: UserType
)

enum class UserType {
    Basic,
    Licensed,
    None
}

//data class CreateUser(
//    val email: Email,
//    val firstName: String,
//    val lastName: String,
//    val displayName: String? = null
//)

data class UpdateUser(
    val email: Email? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val displayName: String? = null
)

typealias Email = String
typealias UserId = String


@Serializable
data class CreateUserRequest(
    val action: Action,
    @SerialName("user_info")
    val userInfo: UserInfo,
){
    enum class Action {
        create,
        ssoCreate,
    }
}

@Serializable
data class UserInfo(
    val email: Email? = null,
    @SerialName("first_name")
    val firstName: String? = null,
    @SerialName("last_name")
    val lastName: String? = null,
    @SerialName("display_name")
    val displayName: String? = null,
    val type: Int,
)

@Serializable
data class CreateUserResponse(
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    val id: String,
    val email: String,
    val type: Int,
)

data class GetUsersResponse(
    @SerialName("total_records")
    val totalRecords: Int,
    val users: List<GetUser>
)

@Serializable
data class GetUser(
    val id: String,
    val email: String,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    val type: Int,
)
