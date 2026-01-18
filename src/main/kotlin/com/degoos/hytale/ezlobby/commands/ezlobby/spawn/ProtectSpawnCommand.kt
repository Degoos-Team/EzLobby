package com.degoos.hytale.ezlobby.commands.ezlobby.spawn

import com.degoos.hytale.ezlobby.EzLobby
import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.protocol.GameMode
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.Universe
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.universe.world.WorldConfig
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore

class ProtectSpawnCommand : AbstractPlayerCommand("protect", "ezlobby.commands.ezlobby.spawn.protect.desc") {
    override fun execute(
        context: CommandContext,
        store: Store<EntityStore?>,
        refStore: Ref<EntityStore?>,
        playerRef: PlayerRef,
        world: World
    ) {
        val worldName = EzLobby.getMainConfig()?.get()?.spawnPointWorldName
        val spawnWorld = if(worldName != null) Universe.get().getWorld(worldName) else null

        if (spawnWorld == null) {
            context.sendMessage(Message.raw("[EzLobby] Could not set spawn point, World(name=${worldName}) does not exist :/"))
            return
        }

        spawnWorld.worldConfig.isSpawningNPC = false
        spawnWorld.worldConfig.gameMode = GameMode.Adventure
        spawnWorld.worldConfig.isPvpEnabled = false

        // todo: ensure worldConfig changes are saved persistently (?)

        playerRef.sendMessage(Message.raw("[EzLobby] Spawn protection enabled for World(name=${worldName})."))
    }
}