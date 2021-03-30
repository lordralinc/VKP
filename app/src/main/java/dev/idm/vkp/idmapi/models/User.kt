package dev.idm.vkp.idmapi.models

data class UserResponse(
    var id: Int? = null,
    var is_agent: Boolean = false

)


data class User(
    var response: UserResponse? = null,
    var error: ErrorResponse? = null
)
