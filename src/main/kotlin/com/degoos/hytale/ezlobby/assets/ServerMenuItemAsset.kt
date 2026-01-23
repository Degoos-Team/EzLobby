package com.degoos.hytale.ezlobby.assets

import com.hypixel.hytale.assetstore.codec.AssetCodec
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.logger.HytaleLogger
import com.hypixel.hytale.protocol.InteractionState
import com.hypixel.hytale.protocol.InteractionType
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.asset.type.item.config.Item
import com.hypixel.hytale.server.core.entity.InteractionContext
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction
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
            LOGGER.atInfo().log("CommandBuffer is null")
            return
        }

        val world = commandBuffer.getExternalData().world // just to show how to get the world if needed
        val store: Store<EntityStore?>? =
            commandBuffer.getExternalData().store // just to show how to get the store if needed
        val ref: Ref<EntityStore?> = interactionContext.entity
        val player: Player? = commandBuffer.getComponent(ref, Player.getComponentType())
        if (player == null) {
            interactionContext.state.state = InteractionState.Failed
            LOGGER.atInfo().log("Player is null")
            return
        }

        val itemStack = interactionContext.heldItem
        if (itemStack == null) {
            interactionContext.state.state = InteractionState.Failed
            LOGGER.atInfo().log("ItemStack is null")
            return
        }

        player.sendMessage(Message.raw("You have used the custom item +" + itemStack.getItemId()))
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

class ServerMenuItemAsset(item: Item) : Item(item) {
    override fun getInteractions(): Map<InteractionType?, String?> {
        return InteractionType.VALUES.associateWith {
            ServerMenuItemInteraction.INTERACTION_ID
        }
    }

    companion object {
        val MENU_ASSET_ID: String = "ezlobby:server_menu_item"
        val CODEC: AssetCodec<String?, Item?> = Item.CODEC
    }
}