package com.degoos.hytale.ezlobby.listeners.globals

import com.degoos.hytale.ezlobby.managers.TransferTracker
import com.degoos.kayle.extension.world
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent

class PlayerDisconnectListener {
    fun onPlayerDisconnect(event: PlayerDisconnectEvent) {
        val playerRef = event.playerRef
        val server = TransferTracker.consume(playerRef.uuid) ?: return
        val world = playerRef.world ?: return
        val message = Message.translation("ezlobby.messages.transfer.player.went")
            .param("player", playerRef.username)
            .param("server", server.name)
        world.sendMessage(message)
    }
}
