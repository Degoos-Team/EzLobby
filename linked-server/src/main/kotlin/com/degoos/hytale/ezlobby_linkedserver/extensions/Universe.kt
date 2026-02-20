package com.degoos.hytale.ezlobby_linkedserver.extensions

import com.hypixel.hytale.server.core.universe.Universe

fun Universe.redirectAllPlayers(host: String, port: Int, data: ByteArray? = null) {
    this.players.forEach {
        it.referToServer(host, port, data)
    }
}