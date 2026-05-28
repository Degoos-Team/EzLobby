package com.degoos.hytale.ezlobby.listeners.globals

import com.degoos.hytale.ezlobby.EzLobby
import com.hypixel.hytale.registry.Registration
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent
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

        val world = Universe.get().getWorld(spawnPointWorldName) ?: run {
            EzLobby.instance?.logger?.atSevere()
                ?.log("[EzLobby] Spawn world '%s' not found in Universe — player cannot join", spawnPointWorldName)
            return
        }

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

        // Capture stable references so the lambdas below don't close over the mutable event.
        val targetWorld = world
        val targetHolder = event.holder
        val targetPlayerRef = event.playerRef

        var addToWorldReg: Registration? = null
        var disconnectReg: Registration? = null

        fun cleanup() {
            addToWorldReg?.unregister()
            disconnectReg?.unregister()
            addToWorldReg = null
            disconnectReg = null
        }

        // Unregister if the player disconnects before being added to the world.
        disconnectReg = EzLobby.getEventRegistry()?.registerGlobal(PlayerDisconnectEvent::class.java) { disconnectEvent ->
            if (disconnectEvent.playerRef == targetPlayerRef) cleanup()
        }

        addToWorldReg = EzLobby.getEventRegistry()?.registerGlobal(AddPlayerToWorldEvent::class.java) { addEvent ->
            if (addEvent.world != targetWorld || addEvent.holder != targetHolder) {
                return@registerGlobal
            }

            addEvent.world.execute {
                val player = addEvent.holder.getComponent(Player.getComponentType())
                if (player == null) {
                    cleanup()
                    return@execute
                }

                val hotbar = player.inventory.combinedHotbarFirst ?: run {
                    cleanup()
                    return@execute
                }

                if (ezLobbyConfig.serverMenuItemOnJoin) {
                    val playerHasServerMenuItemStack =
                        hotbar.containsItemStacksStackableWith(ezLobbyConfig.serversMenuItemStack)
                    if (!playerHasServerMenuItemStack) {
                        hotbar.setItemStackForSlot(0, ezLobbyConfig.serversMenuItemStack)
                    }
                }

                if (ezLobbyConfig.visibilityTogglerItemOnJoin) {
                    val playerHasVisibilityTogglerItemStack =
                        hotbar.containsItemStacksStackableWith(ezLobbyConfig.visibilityTogglerItemStack)
                    if (!playerHasVisibilityTogglerItemStack) {
                        hotbar.setItemStackForSlot(4, ezLobbyConfig.visibilityTogglerItemStack)
                    }
                }

                cleanup()
            }
        }
    }
}