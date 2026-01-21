package com.degoos.hytale.ezlobby.commands.ezlobby.server

import com.degoos.hytale.ezlobby.EzLobby
import com.degoos.hytale.ezlobby.ui.ServerListPage
import com.degoos.hytale.ezlobby.dsl.parseColors
import com.degoos.kayle.dsl.dispatcher
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.universe.PlayerRef
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ServerListCommand : CommandBase("list", "ezlobby.commands.ezlobby.server.list.desc") {

    override fun executeSync(context: CommandContext) {
        val serversConfig = EzLobby.getServersConfig()
        val config = serversConfig?.get()

        if (config == null) {
            context.sendMessage(Message.raw("[EzLobby] What? There is no servers file :/"))
            return
        }

        if (config.servers.isEmpty()) {
            context.sendMessage(Message.raw("[EzLobby] There are no servers added yet. Use `/server add` to add one!"))
            return
        }

        EzLobby.instance?.launch {
            if (!tryOpenListAsGUI(context)) {
                config.servers.forEachIndexed { idx, server ->

                    context.sendMessage(
                        Message.join(
                            Message.raw("[$idx] ${server.name} - "),
                            Message.raw(server.displayName ?: server.name).parseColors(),
                            Message.raw(" (${server.host}:${server.port})")
                        )
                    )
                }
            }
        }
    }


    private suspend fun tryOpenListAsGUI(context: CommandContext): Boolean {
        if (!context.isPlayer) return false

        val player = context.senderAs(Player::class.java)
        val world = player.world ?: return false
        val reference = player.reference ?: return false

        return withContext(world.dispatcher) {
            val playerRef = reference.store.getComponent(reference, PlayerRef.getComponentType())
                ?: return@withContext false
            player.pageManager.openCustomPage(reference, reference.store, ServerListPage(playerRef))
            true
        }
    }
}