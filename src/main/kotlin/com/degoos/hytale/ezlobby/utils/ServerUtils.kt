package com.degoos.hytale.ezlobby.utils

import com.degoos.hytale.ezlobby.EzLobby
import com.degoos.hytale.ezlobby.models.Server
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