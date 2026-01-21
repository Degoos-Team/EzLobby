package com.degoos.hytale.ezlobby.commands.ezlobby.spawn

import com.degoos.hytale.ezlobby.EzLobby
import com.degoos.kayle.dsl.dispatcher
import com.hypixel.hytale.protocol.GameMode
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.AbstractCommand
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.universe.Universe
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.launch
import java.util.concurrent.CompletableFuture

class ProtectSpawnCommand :
    AbstractCommand("protect", "ezlobby.commands.ezlobby.spawn.protect.desc") {

    override fun execute(context: CommandContext): CompletableFuture<Void>? {
        val worldName = EzLobby.getMainConfig()?.get()?.spawnPointWorldName
        val spawnWorld = if (worldName != null) Universe.get().getWorld(worldName) else null

        if (spawnWorld == null) {
            context.sendMessage(Message.raw("[EzLobby] Could not set spawn point, World(name=${worldName}) does not exist :/"))
            return null
        }

        val job = EzLobby.instance?.launch(spawnWorld.dispatcher) {
            spawnWorld.worldConfig.isSpawningNPC = false
            spawnWorld.worldConfig.gameMode = GameMode.Adventure
            spawnWorld.worldConfig.isPvpEnabled = false

            Universe.get().worldConfigProvider.save(spawnWorld.savePath, spawnWorld.worldConfig, spawnWorld).thenRun {
                context.sendMessage(Message.raw("[EzLobby] Spawn protection enabled for World(name=${worldName})."))
            }
        }

        return job?.asCompletableFuture()?.thenApply { return@thenApply null }
    }
}