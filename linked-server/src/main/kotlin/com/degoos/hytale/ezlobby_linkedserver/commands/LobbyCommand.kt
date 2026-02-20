package com.degoos.hytale.ezlobby_linkedserver.commands

import com.degoos.hytale.ezlobby_linkedserver.EzLobbyLinkedServer
import com.degoos.hytale.ezlobby_linkedserver.utils.getRandomServer
import com.degoos.hytale.ezlobby_linkedserver.utils.validateServersConfig
import com.degoos.kayle.extension.dispatcher
import com.degoos.kayle.extension.world
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase
import com.hypixel.hytale.server.core.universe.PlayerRef
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.EmptyCoroutineContext


class LobbyCommand : CommandBase("lobby", "ezlobby.linkedserver.commands.lobbyservers.desc") {
    init {
        this.requirePermission("ezlobby.linkedserver.use")
        this.addAliases("hub")
    }

    override fun executeSync(context: CommandContext) {
        validateServersConfig(context) ?: return

        val server = getRandomServer() ?: return

        EzLobbyLinkedServer.instance?.launch {
            val ref = context.senderAsPlayerRef() ?: return@launch
            val localPlayer = withContext(ref.world.dispatcher) {
                ref.store.getComponent(ref, PlayerRef.getComponentType())
            }

            if (localPlayer == null) {
                context.sendMessage(Message.translation("ezlobby.linkedserver.messages.error.player.not.found"))
                return@launch
            }

            withContext(localPlayer.world?.dispatcher ?: EmptyCoroutineContext) {
                val config = EzLobbyLinkedServer.getLobbyServersConfig()?.get()
                val delaySeconds = config?.defaultDelayInMs ?: 0
                val startPosition = localPlayer.transform.position.clone()

                if (delaySeconds <= 0) {
                    localPlayer.referToServer(server.host, server.port)
                    return@withContext
                }

                var remaining = delaySeconds
                while (remaining > 0) {
                    val currentPosition = localPlayer.transform.position
                    if (startPosition.distanceSquaredTo(currentPosition) > CANCEL_DISTANCE_SQUARED) {
                        localPlayer.sendMessage(
                            Message.translation("ezlobby.linkedserver.messages.lobby.cancelled.moved")
                        )
                        return@withContext
                    }

                    localPlayer.sendMessage(
                        Message.translation("ezlobby.linkedserver.messages.lobby.countdown")
                            .param("seconds", remaining.toString())
                    )
                    remaining -= 1
                    delay(1000)
                }

                localPlayer.sendMessage(
                    Message.translation("ezlobby.linkedserver.messages.lobby.teleporting")
                )
                localPlayer.referToServer(server.host, server.port)
            }
        }
    }

    companion object {
        private const val CANCEL_DISTANCE_SQUARED = 4.0
    }
}