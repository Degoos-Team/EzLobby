package com.degoos.hytale.ezlobby.commands

import com.degoos.hytale.ezlobby.EzLobby
import com.degoos.hytale.ezlobby.commands.ezlobby.server.AbstractServerCommand
import com.degoos.hytale.ezlobby.ui.ServerListPage
import com.degoos.hytale.ezlobby.utils.validateServersConfig
import com.degoos.kayle.extension.dispatcher
import com.degoos.kayle.extension.world
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.universe.PlayerRef
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.EmptyCoroutineContext

class ServersCommand : AbstractServerCommand("servers", "ezlobby.commands.server.desc") {
    init {
        this.requirePermission("ezlobby.servers")
    }

    override fun executeSync(context: CommandContext) {
        validateServersConfig(context) ?: return

        val server = context.targetServer

        EzLobby.instance?.launch {
            if (server == null) {
                tryOpenListAsGUI(context)
            } else {
                val ref = context.senderAsPlayerRef() ?: return@launch
                val localPlayer = withContext(ref.world.dispatcher) {
                    ref.store.getComponent(ref, PlayerRef.getComponentType())
                }

                if (localPlayer == null) {
                    context.sendMessage(Message.translation("ezlobby.messages.error.player.not.found"))
                    return@launch
                }

                withContext(localPlayer.world?.dispatcher ?: EmptyCoroutineContext) {
                    localPlayer.referToServer(server.host, server.port)
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