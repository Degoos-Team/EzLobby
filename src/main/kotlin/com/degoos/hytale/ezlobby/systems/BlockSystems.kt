package com.degoos.hytale.ezlobby.systems

import com.degoos.hytale.ezlobby.EzLobby
import com.degoos.kayle.dsl.world
import com.hypixel.hytale.component.Archetype
import com.hypixel.hytale.component.ArchetypeChunk
import com.hypixel.hytale.component.CommandBuffer
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.component.system.EntityEventSystem
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.event.events.ecs.*
import com.hypixel.hytale.server.core.permissions.PermissionsModule
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore

private fun fetchPlayer(index: Int, chunkArchetype: ArchetypeChunk<EntityStore>): PlayerRef? {
    val ref = chunkArchetype.getReferenceTo(index)
    return ref.store.getComponent(ref, PlayerRef.getComponentType())
}

private fun canDoBlockEvent(player: PlayerRef, permission: String): Boolean {
    val worldName = EzLobby.getMainConfig()?.get()?.spawnPointWorldName ?: return true
    val playerWorldName = player.world?.name ?: return true
    if (worldName != playerWorldName) return true
    return PermissionsModule.get().hasPermission(player.uuid, permission)
}

class BreakEventSystem : EntityEventSystem<EntityStore, BreakBlockEvent>(BreakBlockEvent::class.java) {

    override fun handle(
        index: Int,
        chunkArchetype: ArchetypeChunk<EntityStore>,
        store: Store<EntityStore>,
        commandBuffer: CommandBuffer<EntityStore>,
        event: BreakBlockEvent
    ) {
        val player = fetchPlayer(index, chunkArchetype) ?: return
        if (!canDoBlockEvent(player, "ezlobby.block.break")) {
            player.sendMessage(Message.translation("ezlobby_messages.error.break_blocks"))
            event.isCancelled = true
        }
    }

    override fun getQuery(): Archetype<EntityStore> = Archetype.empty()

}

class PlaceEventSystem : EntityEventSystem<EntityStore, PlaceBlockEvent>(PlaceBlockEvent::class.java) {

    override fun handle(
        index: Int,
        chunkArchetype: ArchetypeChunk<EntityStore>,
        store: Store<EntityStore>,
        commandBuffer: CommandBuffer<EntityStore>,
        event: PlaceBlockEvent
    ) {
        val player = fetchPlayer(index, chunkArchetype) ?: return
        if (!canDoBlockEvent(player,"ezlobby.block.place")) {
            player.sendMessage(Message.translation("ezlobby_messages.error.place_blocks"))
            event.isCancelled = true
        }
    }

    override fun getQuery(): Archetype<EntityStore> = Archetype.empty()

}

class DamageEventSystem : EntityEventSystem<EntityStore, DamageBlockEvent>(DamageBlockEvent::class.java) {

    override fun handle(
        index: Int,
        chunkArchetype: ArchetypeChunk<EntityStore>,
        store: Store<EntityStore>,
        commandBuffer: CommandBuffer<EntityStore>,
        event: DamageBlockEvent
    ) {
        val player = fetchPlayer(index, chunkArchetype) ?: return
        if (!canDoBlockEvent(player,"ezlobby.block.damage")) {
            player.sendMessage(Message.translation("ezlobby_messages.error.damage_blocks"))
            event.isCancelled = true
        }
    }

    override fun getQuery(): Archetype<EntityStore> = Archetype.empty()

}

class UseEventSystem : EntityEventSystem<EntityStore, UseBlockEvent.Pre>(UseBlockEvent.Pre::class.java) {

    override fun handle(
        index: Int,
        chunkArchetype: ArchetypeChunk<EntityStore>,
        store: Store<EntityStore>,
        commandBuffer: CommandBuffer<EntityStore>,
        event: UseBlockEvent.Pre
    ) {
        val player = fetchPlayer(index, chunkArchetype) ?: return
        if (!canDoBlockEvent(player,"ezlobby.block.use")) {
            player.sendMessage(Message.translation("ezlobby_messages.error.use_blocks"))
            event.isCancelled = true
        }
    }

    override fun getQuery(): Archetype<EntityStore> = Archetype.empty()

}

class PickupEventSystem :
    EntityEventSystem<EntityStore, InteractivelyPickupItemEvent>(InteractivelyPickupItemEvent::class.java) {

    override fun handle(
        index: Int,
        chunkArchetype: ArchetypeChunk<EntityStore>,
        store: Store<EntityStore>,
        commandBuffer: CommandBuffer<EntityStore>,
        event: InteractivelyPickupItemEvent
    ) {
        val player = fetchPlayer(index, chunkArchetype) ?: return
        if (!canDoBlockEvent(player,"ezlobby.block.pickup")) {
            player.sendMessage(Message.translation("ezlobby_messages.error.pickup_blocks"))
            event.isCancelled = true
        }
    }

    override fun getQuery(): Archetype<EntityStore> = Archetype.empty()

}