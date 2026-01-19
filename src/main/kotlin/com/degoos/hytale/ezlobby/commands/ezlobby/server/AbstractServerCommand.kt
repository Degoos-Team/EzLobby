package com.degoos.hytale.ezlobby.commands.ezlobby.server

import com.degoos.hytale.ezlobby.utils.findServer
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase
import java.util.*

abstract class AbstractServerCommand(
    name: String, description:
    String,
    requiresConfirmation: Boolean = false
) : CommandBase(
    name,
    description,
    requiresConfirmation
) {

    private val indexArg: OptionalArg<Int?> = this.withOptionalArg(
        "index", "ezlobby.commands.server.arg.index",
        ArgTypes.INTEGER
    )

    private val idArg: OptionalArg<String?> = this.withOptionalArg(
        "id", "ezlobby.commands.server.arg.id",
        ArgTypes.STRING
    )

    private val nameArg: OptionalArg<String?> = this.withOptionalArg(
        "name", "ezlobby.commands.server.arg.name",
        ArgTypes.STRING
    )

    val CommandContext.targetServer
        get() = findServer(
            get<String>(nameArg),
            get<Int>(indexArg),
            get<String>(idArg)?.let { UUID.fromString(it) }
        )

    val CommandContext.errorMessage: Message
        get() {
            val name = get<String>(nameArg)
            val index = get<Int>(indexArg)
            val id = get<String>(idArg)?.let { UUID.fromString(it) }
            return Message.raw(
                "[EzLobby] No server found with the provided selector: " +
                        (if (index != null) "--index $index " else "") +
                        (if (id != null) "--id $id " else "") +
                        (if (name != null) "--name $name" else "")
            )
        }

}