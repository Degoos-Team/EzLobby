package com.degoos.hytale.ezlobby.interactions

import com.degoos.hytale.ezlobby.EzLobby
import com.degoos.hytale.ezlobby.dsl.parseColors

import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.logger.HytaleLogger
import com.hypixel.hytale.protocol.InteractionState
import com.hypixel.hytale.protocol.InteractionType
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.entity.InteractionContext
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore


class VisibilityTogglerItemInteraction : SimpleInstantInteraction {
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

        val visibilityManager = EzLobby.getVisibilityManager()
        if(visibilityManager != null) {
            if (visibilityManager.isHidingOthers(playerRef)) {
                player.sendMessage(Message.translation("ezlobby.messages.visibilitytoggler.enabled").parseColors())
            } else {
                player.sendMessage(Message.translation("ezlobby.messages.visibilitytoggler.disabled").parseColors())
            }
            visibilityManager.toggleVisibility(playerRef)
        }
    }

    companion object {
        const val INTERACTION_ID = "ezlobby:visibility_toggler_item_interaction"

        val CODEC: BuilderCodec<VisibilityTogglerItemInteraction> = BuilderCodec.builder(
            VisibilityTogglerItemInteraction::class.java,
            { VisibilityTogglerItemInteraction() },
            SimpleInstantInteraction.CODEC
        ).build()

        val LOGGER = HytaleLogger.forEnclosingClass()
    }
}