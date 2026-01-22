package com.degoos.hytale.ezlobby.commands.ezlobby.server

import com.degoos.hytale.ezlobby.EzLobby
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext

class ServerRemoveCommand : AbstractServerCommand("remove", "ezlobby.commands.ezlobby.server.remove.desc", true) {

    override fun executeSync(context: CommandContext) {
        val serversConfig = EzLobby.getServersConfig()
        val config = serversConfig?.get()

        if (config == null) {
            context.sendMessage(Message.translation("ezlobby.messages.error.config.missing.add"))
            return
        }

        val serverToRemove = context.targetServer

        if (serverToRemove == null) {
            context.sendMessage(context.errorMessage)
            return
        }

        config.servers.remove(serverToRemove)
        serversConfig.save()

        context.sendMessage(
            Message
                .translation("ezlobby.messages.success.server.removed")
                .param("name", serverToRemove.name)
                .param("id", serverToRemove.id.toString())
                .param("host", serverToRemove.host)
                .param("port", serverToRemove.port.toString())
        )
    }
}