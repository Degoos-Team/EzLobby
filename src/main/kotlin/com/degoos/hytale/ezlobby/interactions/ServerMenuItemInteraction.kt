package com.degoos.hytale.ezlobby.interactions

import com.degoos.hytale.ezlobby.ui.ServerListPage

import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.logger.HytaleLogger
import com.hypixel.hytale.protocol.InteractionState
import com.hypixel.hytale.protocol.InteractionType
import com.hypixel.hytale.server.core.entity.InteractionContext
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore


class ServerMenuItemInteraction : SimpleInstantInteraction {
    constructor() : super(INTERACTION_ID)

    override fun firstRun(
        interactionType: InteractionType,
        interactionContext: InteractionContext,
        cooldownHandler: CooldownHandler
    ) {
        val commandBuffer = interactionContext.commandBuffer
        if (commandBuffer == null) {
            interactionContext.state.state = InteractionState.Failed
            return
        }


        val ref: Ref<EntityStore?> = interactionContext.entity
        val player: Player? = commandBuffer.getComponent(ref, Player.getComponentType())
        if (player == null) {
            interactionContext.state.state = InteractionState.Failed
            return
        }

        val itemStack = interactionContext.heldItem
        if (itemStack == null) {
            interactionContext.state.state = InteractionState.Failed
            return
        }

        val playerRef = ref.store.getComponent(ref, PlayerRef.getComponentType()) ?: return
        player.pageManager.openCustomPage(ref, ref.store, ServerListPage(playerRef))
    }

    companion object {
        const val INTERACTION_ID = "ezlobby:server_menu_item_interaction"

        val CODEC: BuilderCodec<ServerMenuItemInteraction> = BuilderCodec.builder(
            ServerMenuItemInteraction::class.java,
            { ServerMenuItemInteraction() },
            SimpleInstantInteraction.CODEC
        ).build()

        val LOGGER = HytaleLogger.forEnclosingClass()
    }
}