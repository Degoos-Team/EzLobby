package com.degoos.hytale.ezlobby.systems

import com.degoos.hytale.ezlobby.EzLobby
import com.degoos.kayle.dsl.world
import com.hypixel.hytale.component.Archetype
import com.hypixel.hytale.component.ArchetypeChunk
import com.hypixel.hytale.component.CommandBuffer
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.component.system.EntityEventSystem
import com.hypixel.hytale.protocol.GameMode
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.event.events.ecs.DropItemEvent
import com.hypixel.hytale.server.core.permissions.PermissionsModule

import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore

private fun fetchPlayer(index: Int, chunkArchetype: ArchetypeChunk<EntityStore>): PlayerRef? {
    val ref = chunkArchetype.getReferenceTo(index)
    return ref.store.getComponent(ref, PlayerRef.getComponentType())
}

private fun shouldCancelInventoryEvent(playerRef: PlayerRef, permission: String): Boolean {
    val worldName = EzLobby.getMainConfig()?.get()?.spawnPointWorldName ?: return false
    val playerWorldName = playerRef.world?.name ?: return false
    if (worldName != playerWorldName) return false

    if(PermissionsModule.get().hasPermission(playerRef.uuid, permission)) return false;

    val player = playerRef.reference?.let { it.store.getComponent(it, Player.getComponentType()) } ?: return false
    return player.gameMode == GameMode.Adventure
}

class DropItemSystem : EntityEventSystem<EntityStore, DropItemEvent>(DropItemEvent::class.java) {

    override fun handle(
        index: Int,
        chunkArchetype: ArchetypeChunk<EntityStore>,
        store: Store<EntityStore>,
        commandBuffer: CommandBuffer<EntityStore>,
        event: DropItemEvent
    ) {
        val player = fetchPlayer(index, chunkArchetype) ?: return
        if (shouldCancelInventoryEvent(player, "ezlobby.inventory.drop")) {
            event.isCancelled = true
        }
    }

    override fun getQuery(): Archetype<EntityStore> = Archetype.empty()
}