package com.degoos.hytale.ezlobby.listeners

import com.degoos.hytale.ezlobby.EzLobby
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent
import com.hypixel.hytale.server.core.universe.Universe


class PlayerConnectListener {
    fun onPlayerReady(event: PlayerConnectEvent) {
        val ezLobbyConfig = EzLobby.getMainConfig()?.get() ?: return

        val spawnPointWorldName = ezLobbyConfig.spawnPointWorldName ?: return
        val spawnPointPosition = ezLobbyConfig.spawnPointPosition ?: return
        val spawnPointRotation = ezLobbyConfig.spawnPointRotation ?: return

        val world = Universe.get().getWorld(spawnPointWorldName) ?: return

        event.world = world
        event.holder.tryRemoveComponent(TransformComponent.getComponentType())
        event.holder.addComponent(TransformComponent.getComponentType(),
            TransformComponent(spawnPointPosition, spawnPointRotation))
    }
}