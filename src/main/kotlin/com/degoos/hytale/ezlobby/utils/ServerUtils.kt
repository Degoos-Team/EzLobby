package com.degoos.hytale.ezlobby.utils

import com.degoos.hytale.ezlobby.EzLobby
import com.degoos.hytale.ezlobby.configs.ServersConfig
import com.degoos.hytale.ezlobby.models.Server
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import java.util.*

fun findServer(name: String?, index: Int?, id: UUID?): Server? {
    val server = EzLobby.getServersConfig()?.get()?.servers ?: return null
    if (name != null) {
        server.find { it.name == name }?.let { return it }
    }
    if (index != null) {
        server.getOrNull(index)?.let { return it }
    }
    if (id != null) {
        return server.find { it.id == id }?.let { return it }
    }
    return null
}

fun validateServersConfig(context: CommandContext): ServersConfig? {
    val serversConfig = EzLobby.getServersConfig()
    val config = serversConfig?.get()

    if (config == null) {
        context.sendMessage(Message.translation("ezlobby_messages.error.config_missing"))
        return null
    }

    if (config.servers.isEmpty()) {
        context.sendMessage(Message.translation("ezlobby_messages.error.servers_empty"))
        return null
    }

    return config
}
