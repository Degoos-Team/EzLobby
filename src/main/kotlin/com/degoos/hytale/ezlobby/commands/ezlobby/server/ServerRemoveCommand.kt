package com.degoos.hytale.ezlobby.commands.ezlobby.server

import com.degoos.hytale.ezlobby.EzLobby
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext

class ServerRemoveCommand : AbstractServerCommand("remove", "ezlobby.commands.ezlobby.server.add.desc", true) {

    override fun executeSync(context: CommandContext) {
        val serversConfig = EzLobby.getServersConfig()
        val config = serversConfig?.get()

        if (config == null) {
            context.sendMessage(Message.raw("[EzLobby] Could not add a server, servers file is missing :/"))
            return
        }

        val serverToRemove = context.targetServer

        if (serverToRemove == null) {
            context.sendMessage(context.errorMessage)
            return
        }

        config.servers.remove(serverToRemove)
        serversConfig.save()

        context.sendMessage(Message.raw("[EzLobby] Server '${serverToRemove.name}' (${serverToRemove.id}) with address '${serverToRemove.host}:${serverToRemove.port}' was removed"))
    }
}