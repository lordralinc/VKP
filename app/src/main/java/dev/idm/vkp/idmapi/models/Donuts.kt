package dev.idm.vkp.idmapi.models

@Suppress("ArrayInDataClass")
data class VerifiedResponse (
    var owner: IntArray? = null,
    var agents: IntArray? = null,
    var helpers: IntArray? = null,
    var donuts: IntArray? = null,
)

data class Verified (
    var response: VerifiedResponse? = null,
    var error: ErrorResponse? = null
)
