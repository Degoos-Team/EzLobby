package com.degoos.hytale.ezlobby.managers

import com.degoos.hytale.ezlobby.models.Server
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object TransferTracker {
    private val pending = ConcurrentHashMap<UUID, Server>()

    // MUST be called before referToServer() — TransferTracker is read at PlayerDisconnectEvent time
    fun record(playerUuid: UUID, server: Server) {
        pending[playerUuid] = server
    }

    fun consume(playerUuid: UUID): Server? = pending.remove(playerUuid)
}
