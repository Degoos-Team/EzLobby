package com.degoos.hytale.ezlobby.commands.ezlobby.server

import com.degoos.hytale.ezlobby.EzLobby
import com.degoos.hytale.ezlobby.dsl.parseColors
import com.degoos.hytale.ezlobby.ui.ServerListPage
import com.degoos.hytale.ezlobby.utils.validateServersConfig
import com.degoos.kayle.dsl.dispatcher
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.universe.PlayerRef
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ServerListCommand : CommandBase("list", "ezlobby.commands.ezlobby.server.list.desc") {
    private val chatArg: DefaultArg<Boolean> = this.withDefaultArg(
        "chat", "ezlobby.commands.server.list.arg.chat",
        ArgTypes.BOOLEAN, false, "ezlobby.commands.server.list.arg.chat"
    )

    init {
        addAliases("servers")
    }

    override fun executeSync(context: CommandContext) {
        val sendInChat = context.get<Boolean>(this.chatArg)

        val config = validateServersConfig(context) ?: return

        EzLobby.instance?.launch {
            if (sendInChat || !tryOpenListAsGUI(context)) {
                config.servers.forEachIndexed { idx, server ->
                    // server_list.entry = [{idx}] {name} - {displayName} ({host}:{port})
                    context.sendMessage(
                        Message.translation("server_list.entry")
                            .param("idx", idx.toString())
                            .param("name", server.name)
                            .param("displayName", Message.raw(server.displayName ?: server.name).parseColors())
                            .param("host", server.host)
                            .param("port", server.port.toString())
                    )
                }
            }
        }
    }


    private suspend fun tryOpenListAsGUI(context: CommandContext): Boolean {
        // todo: create a server ADMIN gui
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