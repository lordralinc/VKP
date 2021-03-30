package dev.idm.vkp.idmapi.models

data class CommandResponseItem(var name: String, var command: String, var description: String)

data class Command(var response: List<CommandResponseItem>?, var errpr: ErrorResponse?)
