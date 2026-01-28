package com.degoos.hytale.ezlobby.listeners.globals

import com.degoos.hytale.ezlobby.EzLobby
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent
import com.hypixel.hytale.server.core.universe.PlayerRef


class PlayerReadyListener {
    fun onPlayerReady(event: PlayerReadyEvent) {
        val playerRef = event.playerRef.store.getComponent(event.playerRef, PlayerRef.getComponentType())
        if(playerRef != null) {
            EzLobby.getVisibilityManager()?.hidePlayer(playerRef.uuid) ?: return
        }
    }
}