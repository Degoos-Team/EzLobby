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
    private val lastResolved = ConcurrentHashMap<UUID, ServerStatus>()

    fun getStatus(server: Server): ServerStatus {
        return when (val current = statuses.getOrDefault(server.id, ServerStatus.UNKNOWN)) {
            ServerStatus.CHECKING -> lastResolved.getOrDefault(server.id, ServerStatus.CHECKING)
            else -> current
        }
    }

    fun isChecking(server: Server): Boolean =
        statuses.getOrDefault(server.id, ServerStatus.UNKNOWN) == ServerStatus.CHECKING

    fun requestCheck(server: Server, scope: CoroutineScope) {
        var shouldLaunch = false
        statuses.compute(server.id) { _, current ->
            if (current == ServerStatus.CHECKING) current
            else { shouldLaunch = true; ServerStatus.CHECKING }
        }
        if (!shouldLaunch) return

        scope.launch {
            val result = probe(server.host, server.port)
            if (result == ServerStatus.ONLINE || result == ServerStatus.OFFLINE) {
                lastResolved[server.id] = result
            }
            statuses.replace(server.id, ServerStatus.CHECKING, result)
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
            EzLobby.instance?.logger?.atFine()?.log(
                "TCP probe OK for %s:%d on %s", host, port, Thread.currentThread().name
            )
            ServerStatus.ONLINE
        } catch (e: Exception) {
            EzLobby.instance?.logger?.atWarning()?.log(
                "TCP probe FAILED for %s:%d (%s)", host, port, e.toString()
            )
            ServerStatus.OFFLINE
        }
    }
}
