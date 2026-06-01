package com.degoos.hytale.ezlobby.listeners.globals

import com.degoos.hytale.ezlobby.EzLobby
import com.hypixel.hytale.registry.Registration
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent
import com.hypixel.hytale.server.core.inventory.InventoryComponent
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

        val targetPlayerRef = event.playerRef

        var readyReg: Registration? = null
        var disconnectReg: Registration? = null

        fun cleanup() {
            readyReg?.unregister()
            disconnectReg?.unregister()
            readyReg = null
            disconnectReg = null
        }

        disconnectReg = EzLobby.getEventRegistry()?.registerGlobal(PlayerDisconnectEvent::class.java) { disconnectEvent ->
            if (disconnectEvent.playerRef == targetPlayerRef) cleanup()
        }

        readyReg = EzLobby.getEventRegistry()?.registerGlobal(PlayerReadyEvent::class.java) { readyEvent ->
            if (readyEvent.playerRef != targetPlayerRef) return@registerGlobal

            val hotbar = readyEvent.playerRef.store
                .getComponent(readyEvent.playerRef, InventoryComponent.Hotbar.getComponentType())
                ?.getInventory() ?: run {
                EzLobby.instance?.logger?.atWarning()
                    ?.log("[EzLobby] Hotbar still missing at PlayerReadyEvent — cannot grant items")
                cleanup()
                return@registerGlobal
            }

            if (ezLobbyConfig.serverMenuItemOnJoin) {
                if (!hotbar.containsItemStacksStackableWith(ezLobbyConfig.serversMenuItemStack)) {
                    hotbar.setItemStackForSlot(0, ezLobbyConfig.serversMenuItemStack)
                }
            }

            if (ezLobbyConfig.visibilityTogglerItemOnJoin) {
                if (!hotbar.containsItemStacksStackableWith(ezLobbyConfig.visibilityTogglerItemStack)) {
                    hotbar.setItemStackForSlot(4, ezLobbyConfig.visibilityTogglerItemStack)
                }
            }

            cleanup()
        }
    }
}
