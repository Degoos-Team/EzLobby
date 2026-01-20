package com.degoos.hytale.ezlobby.commands.ezlobby.server

import com.degoos.hytale.ezlobby.EzLobby
import com.degoos.hytale.ezlobby.models.Server
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase
import java.util.UUID

class ServerAddCommand : CommandBase("add", "ezlobby.commands.ezlobby.server.add.desc") {
    private val nameArg: RequiredArg<String?> = this.withRequiredArg(
        "name", "ezlobby.commands.server.add.arg.name",
        ArgTypes.STRING
    )

    private val hostArg: RequiredArg<String?> = this.withRequiredArg(
        "host", "ezlobby.commands.server.add.arg.host",
        ArgTypes.STRING
    )

    private val portArg: RequiredArg<Int?> = this.withRequiredArg(
        "port", "ezlobby.commands.server.add.arg.port",
        ArgTypes.INTEGER
    )

    private val uiIconArg: OptionalArg<String?> = this.withOptionalArg(
        "icon", "ezlobby.commands.server.add.arg.uiIcon",
        ArgTypes.STRING
    )

    private val uiColorTintArg: OptionalArg<String?> = this.withOptionalArg(
        "colorTint", "ezlobby.commands.server.add.arg.uiColorTint",
        ArgTypes.STRING
    )

    private val uiBackgroundArg: OptionalArg<String?> = this.withOptionalArg(
        "background", "ezlobby.commands.server.add.arg.uiBackground",
        ArgTypes.STRING
    )

    private val displayNameArg: OptionalArg<String?> = this.withOptionalArg(
        "displayName", "ezlobby.commands.server.add.arg.displayName",
        ArgTypes.STRING
    )

    private val descriptionArg: RequiredArg<String?> = this.withRequiredArg(
        "description", "ezlobby.commands.server.add.arg.description",
        ArgTypes.STRING
    )

    override fun executeSync(context: CommandContext) {
        val serversConfig = EzLobby.getServersConfig()
        val config = serversConfig?.get()

        if (config == null) {
            context.sendMessage(Message.raw("[EzLobby] Could not add a server, servers file is missing :/"))
            return
        }

        val uuid = UUID.randomUUID()
        val name = context.get<String>(this.nameArg)
        val host = context.get<String>(this.hostArg)
        val port = context.get<Int>(this.portArg)
        val uiIcon = context.get<String?>(this.uiIconArg)
        val uiColorTint = context.get<String?>(this.uiColorTintArg)
        val uiBackground = context.get<String?>(this.uiBackgroundArg)
        val displayName = context.get<String?>(this.displayNameArg)
        val description = context.get<String?>(this.descriptionArg)

        config.servers.add(Server(uuid, name, host, port, uiIcon, uiColorTint, uiBackground, displayName, description))

        context.sendMessage(Message.raw("[EzLobby] Added server '$name' ($uuid) with address $host:$port"))
        serversConfig.save()
    }
}