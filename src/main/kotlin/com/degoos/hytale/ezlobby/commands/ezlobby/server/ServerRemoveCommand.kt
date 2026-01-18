package com.degoos.hytale.ezlobby.commands.ezlobby.server

import com.degoos.hytale.ezlobby.EzLobby
import com.degoos.hytale.ezlobby.models.Server
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase

class ServerRemoveCommand : CommandBase("remove", "ezlobby.commands.ezlobby.server.add.desc", true) {
    private val indexArg: RequiredArg<Int?> = this.withRequiredArg(
        "index", "ezlobby.commands.server.add.arg.idx",
        ArgTypes.INTEGER
    )

    override fun executeSync(context: CommandContext) {
        val serversConfig = EzLobby.getServersConfig()
        val config = serversConfig?.get()

        if (config == null) {
            context.sendMessage(Message.raw("[EzLobby] Could not add a server, servers file is missing :/"))
            return
        }

        val index = context.get<Int>(this.indexArg)


        val serverToRemove = config.servers[index]
        config.servers.removeAt(index)
        serversConfig.save()

        context.sendMessage(Message.raw("[EzLobby] Server '${serverToRemove.name}' with address '${serverToRemove.host}:${serverToRemove.port}' was removed"))
    }
}