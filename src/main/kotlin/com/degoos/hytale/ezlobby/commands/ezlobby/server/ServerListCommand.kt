package com.degoos.hytale.ezlobby.commands.ezlobby.server

import com.degoos.hytale.ezlobby.EzLobby
import com.degoos.hytale.ezlobby.ui.AdminServerListPage
import com.degoos.hytale.ezlobby.ui.ServerListPage
import com.degoos.hytale.ezlobby.utils.validateServersConfig
import com.degoos.kayle.extension.dispatcher
import com.degoos.kayle.extension.parseTags
import com.degoos.kayle.extension.world
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.permissions.PermissionsModule
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

        val config = validateServersConfig(context, true) ?: return

        EzLobby.instance?.launch {
            if (sendInChat || !tryOpenListAsGUI(context)) {
                config.servers.forEachIndexed { idx, server ->
                    // server.list.entry = [{idx}] {name} - {displayName} ({host}:{port})
                    context.sendMessage(
                        Message.translation("ezlobby.messages.server.list.entry")
                            .param("idx", idx.toString())
                            .param("name", server.name)
                            .param("displayName", Message.raw(server.displayName ?: server.name).parseTags())
                            .param("host", server.host)
                            .param("port", server.port.toString())
                    )
                }
            }
        }
    }


    private suspend fun tryOpenListAsGUI(context: CommandContext): Boolean {
        if (!context.isPlayer) return false

        val ref = context.senderAsPlayerRef() ?: return false

        return withContext(ref.world.dispatcher) {
            val playerRef = ref.store.getComponent(ref, PlayerRef.getComponentType()) ?: return@withContext false
            val player = ref.store.getComponent(ref, Player.getComponentType()) ?: return@withContext false
            val reference = player.reference ?: return@withContext false

            val hasManagePermission = PermissionsModule.get().hasPermission(playerRef.uuid, "ezlobby.server.manage")
            val page = if (hasManagePermission) {
                AdminServerListPage(playerRef)
            } else {
                ServerListPage(playerRef)
            }

            player.pageManager.openCustomPage(reference, reference.store, page)
            true
        }
    }
}