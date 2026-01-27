package com.degoos.hytale.ezlobby_linkedserver.utils

import com.degoos.hytale.ezlobby_linkedserver.EzLobbyLinkedServer
import com.degoos.hytale.ezlobby_linkedserver.configs.LobbyServersConfig
import com.degoos.hytale.ezlobby_linkedserver.models.Server
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext

fun findServer(index: Int?): Server? {
    val server = EzLobbyLinkedServer.getLobbyServersConfig()?.get()?.lobbyServers ?: return null
    if (index != null) {
        server.getOrNull(index)?.let { return it }
    }
    return null
}

fun getRandomServer(): Server? {
    val servers = EzLobbyLinkedServer.getLobbyServersConfig()?.get()?.lobbyServers ?: return null
    if (servers.isEmpty()) return null
    return servers.random()
}

fun validateServersConfig(context: CommandContext, showIfEmpty: Boolean = false): LobbyServersConfig? {
    val serversConfig = EzLobbyLinkedServer.getLobbyServersConfig()
    val config = serversConfig?.get()

    if (config == null) {
        context.sendMessage(Message.translation("ezlobby.linkedserver.messages.error.config.missing"))
        return null
    }

    if (config.lobbyServers.isEmpty() && !showIfEmpty) {
        context.sendMessage(Message.translation("ezlobby.linkedserver.messages.error.servers.empty"))
        return null
    }

    return config
}
