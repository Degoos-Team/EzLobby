package com.degoos.hytale.ezlobby.listeners.locals

import com.degoos.hytale.ezlobby.EzLobby
import com.degoos.hytale.ezlobby.ui.ServerListPage
import com.degoos.kayle.dsl.dispatcher
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.event.events.player.PlayerMouseButtonEvent
import com.hypixel.hytale.server.core.universe.PlayerRef
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext


val playerMouseButtonEventConsumer: (PlayerMouseButtonEvent) -> Unit = playerMouseButtonEventConsumer@{ event ->
    println("[EzLobby] Player Mouse Button Event Consumer")
    val player: Player = event.player
    val playerRef: PlayerRef = player.toHolder().getComponent(PlayerRef.getComponentType()) ?: return@playerMouseButtonEventConsumer

    println("[EzLobby] Player: $player")

    // Custom item interaction
    val item = event.itemInHand

    println("[EzLobby] Item: ${item.id}")
    // todo if item is serversMenuItem stack, cancel event and open servers list
    if (item != null && item.getId() == "Degoos_Compass") {
        println("[EzLobby] Item is servers menu item")
        event.isCancelled = true

        EzLobby.instance?.launch(player.world?.dispatcher ?: EmptyCoroutineContext)  {
            val reference = player.reference ?: return@launch
            player.pageManager.openCustomPage(reference, reference.store, ServerListPage(playerRef))
        }
    }
}


//    override fun handle(
//        index: Int,
//        chunkArchetype: ArchetypeChunk<EntityStore>,
//        store: Store<EntityStore>,
//        commandBuffer: CommandBuffer<EntityStore>,
//        event: UseBlockEvent.Pre
//    ) {
//        val ref = chunkArchetype.getReferenceTo(index)
//        val playerRef = ref.store.getComponent(ref, PlayerRef.getComponentType()) ?: return
//        val player = ref.store.getComponent(ref, Player.getComponentType()) ?: return
//
//        // Get the servers menu item from config
//        val serversMenuItem = EzLobby.Companion.getMainConfig()?.get()?.serversMenuItem ?: return
//
//        // Get the item in player's hand
//        val heldItem = player.inventory.getStack(player.inventory.selectedSlot)
//        if (heldItem == null || heldItem.prefabName != serversMenuItem) return
//
//        // Cancel the event to prevent default behavior
//        event.isCancelled = true
//
//        // Open the ServerListPage
//        EzLobby.Companion.instance?.launch(EmptyCoroutineContext) {
//            val reference = player.reference ?: return@launch
//            player.pageManager.openCustomPage(reference, reference.store, ServerListPage(playerRef))
//        }
//    }
//
//    override fun getQuery(): Archetype<EntityStore> = Archetype.empty()
