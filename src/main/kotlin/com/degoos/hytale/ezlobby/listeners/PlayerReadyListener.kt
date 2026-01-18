package com.degoos.hytale.ezlobby.listeners

import com.degoos.hytale.ezlobby.EzLobby
import com.degoos.kayle.dsl.teleport
import com.degoos.kayle.dsl.transform
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent
import kotlinx.coroutines.launch
import com.hypixel.hytale.server.core.universe.Universe


class PlayerReadyListener {
    fun onPlayerReady(event: PlayerReadyEvent) {
        val ezLobbyConfig = EzLobby.getMainConfig()?.get() ?: return

        val spawnPointWorldName = ezLobbyConfig.spawnPointWorldName ?: return
        val spawnPointPosition = ezLobbyConfig.spawnPointPosition ?: return
        val spawnPointRotation = ezLobbyConfig.spawnPointRotation ?: return

        val world = Universe.get().getWorld(spawnPointWorldName) ?: return

        // todo: teleport player to spawnpoint instead of welcoming (MOTD to be defined)
        val player = event.player

        var expectedSpawnPoint: String? = null
        expectedSpawnPoint = "$spawnPointWorldName<${spawnPointPosition.x}, ${spawnPointPosition.y}, ${spawnPointPosition.z}>, " +
            "<${spawnPointRotation.x}, ${spawnPointRotation.y}, ${spawnPointRotation.z}>"

        player.sendMessage(Message.raw("Welcome ${player.displayName}! Your spawn point is set to: $expectedSpawnPoint"))

        event.playerRef.teleport(spawnPointPosition, spawnPointRotation, world)
    }
}