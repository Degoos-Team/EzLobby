package com.degoos.hytale.ezlobby.managers

import com.degoos.hytale.ezlobby.EzLobby
import com.degoos.hytale.ezlobby.models.Server
import com.degoos.hytale.ezlobby.models.ServerStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object ServerStatusChecker {
    private val statuses = ConcurrentHashMap<UUID, ServerStatus>()

    fun getStatus(server: Server): ServerStatus = statuses.getOrDefault(server.id, ServerStatus.UNKNOWN)

    fun requestCheck(server: Server, scope: CoroutineScope) {
        var shouldLaunch = false
        statuses.compute(server.id) { _, current ->
            if (current == ServerStatus.CHECKING) current
            else { shouldLaunch = true; ServerStatus.CHECKING }
        }
        if (!shouldLaunch) return

        scope.launch {
            statuses.replace(server.id, ServerStatus.CHECKING, probe(server.host, server.port))
        }.invokeOnCompletion { cause ->
            // invokeOnCompletion fires even when the job is cancelled before it starts,
            // covering the window between compute() and launch() scheduling (pre-start race).
            if (cause is CancellationException) {
                statuses.replace(server.id, ServerStatus.CHECKING, ServerStatus.UNKNOWN)
            }
        }
    }

    private suspend fun probe(host: String, port: Int): ServerStatus = withContext(Dispatchers.IO) {
        try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(host, port), 2000)
            }
            EzLobby.instance?.logger?.atInfo()?.log(
                "TCP probe OK for %s:%d on %s", host, port, Thread.currentThread().name
            )
            ServerStatus.ONLINE
        } catch (e: Exception) {
            EzLobby.instance?.logger?.atInfo()?.log(
                "TCP probe FAILED for %s:%d (%s) on %s", host, port, e.toString(), Thread.currentThread().name
            )
            ServerStatus.OFFLINE
        }
    }
}
