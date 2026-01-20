package com.degoos.hytale.ezlobby.commands.ezlobby.server

import com.degoos.hytale.ezlobby.EzLobby
import com.degoos.hytale.ezlobby.models.Server
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase

class ServerListCommand : CommandBase("list", "ezlobby.commands.ezlobby.server.list.desc") {

    override fun executeSync(context: CommandContext) {
        val serversConfig = EzLobby.getServersConfig()
        val config = serversConfig?.get()

        if (config == null) {
            context.sendMessage(Message.raw("[EzLobby] What? There is no servers file :/"))
            return
        }

        if(config.servers.isEmpty()) {
            context.sendMessage(Message.raw("[EzLobby] There are no servers added yet. Use `/server add` to add one!"))
            return
        }

        config.servers.forEachIndexed { idx, server ->
            context.sendMessage(Message.raw("[$idx] ${server.name} - ${server.displayName} (${server.host}:${server.port})"))
        }
    }
}