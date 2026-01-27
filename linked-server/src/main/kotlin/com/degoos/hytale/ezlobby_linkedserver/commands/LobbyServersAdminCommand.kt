package com.degoos.hytale.ezlobby_linkedserver.commands

import com.degoos.hytale.ezlobby_linkedserver.EzLobbyLinkedServer
import com.degoos.hytale.ezlobby_linkedserver.ui.LobbyServerListPage
import com.degoos.hytale.ezlobby_linkedserver.utils.validateServersConfig
import com.degoos.kayle.dsl.dispatcher
import com.degoos.kayle.dsl.world
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.universe.PlayerRef
import kotlin.coroutines.EmptyCoroutineContext

class LobbyServersAdminCommand : CommandBase("lobbyservers", "ezlobby.linkedserver.commands.lobbyservers.admin.desc") {
    init {
        this.requirePermission("ezlobby.linkedserver.admin")
    }

    override fun executeSync(context: CommandContext) {
        validateServersConfig(context, showIfEmpty = true) ?: return
        if (!context.isPlayer) {
            context.sendMessage(Message.raw("This command can only be used by players"))
            return
        }

        EzLobbyLinkedServer.instance?.launch {
            val player = context.senderAs(Player::class.java)
            val world = player.world ?: return@launch
            val reference = player.reference ?: return@launch

            val playerRef = withContext(world.dispatcher) {
                reference.store.getComponent(reference, PlayerRef.getComponentType())
            } ?: return@launch

            withContext(playerRef.world?.dispatcher ?: EmptyCoroutineContext) {
                player.pageManager.openCustomPage(reference, reference.store, LobbyServerListPage(playerRef))
            }
        }
    }
}
