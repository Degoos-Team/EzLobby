package com.degoos.hytale.ezlobby.managers

import com.degoos.hytale.ezlobby.models.Server
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object TransferTracker {
    private data class PendingTransfer(val server: Server, val recordedAt: Long)

    private val pending = ConcurrentHashMap<UUID, PendingTransfer>()
    private const val TTL_MS = 10_000L

    // MUST be called before referToServer() — TransferTracker is read at PlayerDisconnectEvent time
    fun record(playerUuid: UUID, server: Server) {
        pending[playerUuid] = PendingTransfer(server, System.currentTimeMillis())
    }

    fun consume(playerUuid: UUID): Server? {
        val entry = pending.remove(playerUuid) ?: return null
        return if (System.currentTimeMillis() - entry.recordedAt <= TTL_MS) entry.server else null
    }
}
