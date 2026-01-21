package com.degoos.hytale.ezlobby.commands.ezlobby.spawn

import com.degoos.hytale.ezlobby.EzLobby
import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore

class SetSpawnCommand : AbstractPlayerCommand("set", "ezlobby.commands.ezlobby.spawn.set.desc") {
    override fun execute(
        context: CommandContext,
        store: Store<EntityStore?>,
        refStore: Ref<EntityStore?>,
        playerRef: PlayerRef,
        world: World
    ) {
        val mainConfig = EzLobby.getMainConfig()
        val config = mainConfig?.get()

        if (config == null) {
            context.sendMessage(Message.translation("ezlobby_messages.error.spawn_config_missing"))
            return
        }

        config.spawnPointWorldName = world.name
        config.spawnPointPosition = playerRef.transform.position
        config.spawnPointRotation = playerRef.transform.rotation

        mainConfig.save()
        context.sendMessage(
            Message.raw("ezlobby_messages.success.spawn_set")
                .param("name", world.name)
                .param("position", playerRef.transform.position.toString())
                .param("rotation", playerRef.transform.rotation.toString())
        )
    }
}