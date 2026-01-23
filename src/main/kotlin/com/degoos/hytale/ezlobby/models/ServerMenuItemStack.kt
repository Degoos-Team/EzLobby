package com.degoos.hytale.ezlobby.models

import com.degoos.hytale.ezlobby.EzLobby
import com.degoos.hytale.ezlobby.assets.ServerMenuItemAsset
import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.server.core.asset.type.item.config.Item
import com.hypixel.hytale.server.core.inventory.ItemStack

class ServerMenuItemStack(itemId: String): ItemStack(itemId, 1) {
    var item = ServerMenuItemAsset(super.item)

    init {
        this.withMetadata(OWNER, "EzLobby")
        this.withMetadata(TYPE, "Servers")
    }

    override fun getItem(): Item {
        return item
    }

    companion object {
        val OWNER: KeyedCodec<String> = KeyedCodec("Owner", Codec.STRING)
        val TYPE: KeyedCodec<String> = KeyedCodec("Type", Codec.STRING)
    }
}