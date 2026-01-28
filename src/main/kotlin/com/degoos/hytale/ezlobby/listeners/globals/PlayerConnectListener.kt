package com.degoos.hytale.ezlobby.listeners.globals

import com.degoos.hytale.ezlobby.EzLobby
import com.hypixel.hytale.registry.Registration
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent
import com.hypixel.hytale.server.core.universe.Universe
import com.hypixel.hytale.server.core.util.PositionUtil


class PlayerConnectListener {
    fun onPlayerReady(event: PlayerConnectEvent) {
        val ezLobbyConfig = EzLobby.getMainConfig()?.get() ?: return

        val spawnPointWorldName = ezLobbyConfig.spawnPointWorldName ?: return
        val spawnPointPosition = ezLobbyConfig.spawnPointPosition ?: return
        val spawnPointBodyRotation = ezLobbyConfig.spawnPointBodyRotation ?: return
        val spawnPointHeadRotation = ezLobbyConfig.spawnPointHeadRotation ?: return

        val world = Universe.get().getWorld(spawnPointWorldName) ?: return


        val transformComponent = TransformComponent(spawnPointPosition, spawnPointBodyRotation)
        val transform = transformComponent.sentTransform
        PositionUtil.assign(transform.position!!, spawnPointPosition)
        PositionUtil.assign(transform.bodyOrientation!!, spawnPointBodyRotation)
        PositionUtil.assign(transform.lookOrientation!!, spawnPointHeadRotation)

        event.world = world
        event.holder.tryRemoveComponent(TransformComponent.getComponentType())
        event.holder.addComponent(
            TransformComponent.getComponentType(),
            transformComponent
        )

        event.holder.ensureComponent(HeadRotation.getComponentType())

        if (!ezLobbyConfig.serverMenuItemOnJoin && !ezLobbyConfig.visibilityTogglerItemOnJoin) return

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

                if(ezLobbyConfig.serverMenuItemOnJoin) {
                    val playerHasServerMenuItemStack =
                        player.inventory.combinedHotbarFirst.containsItemStacksStackableWith(ezLobbyConfig.serversMenuItemStack)
                    if (!playerHasServerMenuItemStack) {
                        player.inventory.combinedHotbarFirst.setItemStackForSlot(0, ezLobbyConfig.serversMenuItemStack)
                    }
                }

                if(ezLobbyConfig.visibilityTogglerItemOnJoin) {
                    val playerHasVisibilityTogglerItemStack =
                        player.inventory.combinedHotbarFirst.containsItemStacksStackableWith(ezLobbyConfig.visibilityTogglerItemStack)
                    if (!playerHasVisibilityTogglerItemStack) {
                        player.inventory.combinedHotbarFirst.setItemStackForSlot(4, ezLobbyConfig.visibilityTogglerItemStack)
                    }
                }

                registration?.unregister()
            }
        }
    }
}