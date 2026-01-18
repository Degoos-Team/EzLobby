package com.degoos.hytale.ezlobby.systems

import com.hypixel.hytale.component.Archetype
import com.hypixel.hytale.component.ArchetypeChunk
import com.hypixel.hytale.component.CommandBuffer
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.component.system.EntityEventSystem
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent
import com.hypixel.hytale.server.core.event.events.ecs.DamageBlockEvent
import com.hypixel.hytale.server.core.event.events.ecs.InteractivelyPickupItemEvent
import com.hypixel.hytale.server.core.event.events.ecs.PlaceBlockEvent
import com.hypixel.hytale.server.core.event.events.ecs.UseBlockEvent
import com.hypixel.hytale.server.core.permissions.PermissionsModule
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore

private fun fetchPlayer(index: Int, chunkArchetype: ArchetypeChunk<EntityStore>): PlayerRef? {
    val ref = chunkArchetype.getReferenceTo(index)
    return ref.store.getComponent(ref, PlayerRef.getComponentType())
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
        if (!PermissionsModule.get().hasPermission(player.uuid, "ezlobby.block.break")) {
            player.sendMessage(Message.raw("You don't have permission to break blocks!"))
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
        if (!PermissionsModule.get().hasPermission(player.uuid, "ezlobby.block.place")) {
            player.sendMessage(Message.raw("You don't have permission to place blocks!"))
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
        if (!PermissionsModule.get().hasPermission(player.uuid, "ezlobby.block.damage")) {
            player.sendMessage(Message.raw("You don't have permission to damage blocks!"))
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
        if (!PermissionsModule.get().hasPermission(player.uuid, "ezlobby.block.use")) {
            player.sendMessage(Message.raw("You don't have permission to use blocks!"))
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
        if (!PermissionsModule.get().hasPermission(player.uuid, "ezlobby.block.pickup")) {
            player.sendMessage(Message.raw("You don't have permission to use blocks!"))
            event.isCancelled = true
        }
    }

    override fun getQuery(): Archetype<EntityStore> = Archetype.empty()

}