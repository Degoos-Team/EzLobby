package com.degoos.hytale.ezlobby.listeners.globals

import com.degoos.hytale.ezlobby.EzLobby
import com.hypixel.hytale.registry.Registration
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent
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
        event.holder.addComponent(
            TransformComponent.getComponentType(),
            TransformComponent(spawnPointPosition, spawnPointRotation)
        )

        if (!ezLobbyConfig.serverMenuItemOnJoin) return

        var registration: Registration? = null
        registration = EzLobby.getEventRegistry()?.registerGlobal(AddPlayerToWorldEvent::class.java) { addEvent ->
            if (event.world != addEvent.world || event.holder != addEvent.holder) {
                return@registerGlobal
            }

            addEvent.world.execute {
                val player = addEvent.holder.getComponent(Player.getComponentType())
                if(player == null) {
                    registration?.unregister()
                    return@execute
                }

                val playerHasServerMenuItemStack =
                    player.inventory.combinedHotbarFirst.containsItemStacksStackableWith(ezLobbyConfig.serversMenuItemStack!!)
                if (!playerHasServerMenuItemStack) {
                    player.inventory.combinedHotbarFirst.addItemStack(ezLobbyConfig.serversMenuItemStack!!)
                }
                registration?.unregister()
            }
        }
    }
}