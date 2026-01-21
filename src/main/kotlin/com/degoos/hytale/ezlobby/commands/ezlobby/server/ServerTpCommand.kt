package com.degoos.hytale.ezlobby.commands.ezlobby.server

import com.degoos.hytale.ezlobby.EzLobby
import com.degoos.kayle.KotlinPlugin
import com.degoos.kayle.dsl.dispatcher
import com.degoos.kayle.dsl.world
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes
import com.hypixel.hytale.server.core.universe.PlayerRef
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.EmptyCoroutineContext

class ServerTpCommand() :
    AbstractServerCommand("tp", "ezlobby.commands.ezlobby.server.tp.desc") {

    private val playerArg: OptionalArg<PlayerRef> = this.withOptionalArg(
        "player", "ezlobby.commands.title.arg.player",
        ArgTypes.PLAYER_REF
    )

    override fun executeSync(context: CommandContext) {
        val playerRef: PlayerRef? = context.get<PlayerRef>(this.playerArg)

        val server = context.targetServer
        if (server == null) {
            context.sendMessage(context.errorMessage)
            return
        }

        EzLobby.instance?.launch {
            val localPlayer = if (playerRef == null) {
                val ref = context.senderAsPlayerRef() ?: return@launch
                withContext(ref.world.dispatcher) {
                    ref.store.getComponent(ref, PlayerRef.getComponentType())
                }
            } else {
                playerRef
            }

            if (localPlayer == null) {
                context.sendMessage(Message.raw("[EzLobby] Something really weird has happened. No player could be found, not even yourself :o/"))
                return@launch
            }

            withContext(localPlayer.world?.dispatcher ?: EmptyCoroutineContext) {
                localPlayer.referToServer(server.host, server.port)
            }

            context.sendMessage(
                Message.raw(
                    "[EzLobby] User '${localPlayer.username}' is being teleported to server " +
                            "'${server.name}' (${server.host}:${server.port})"
                )
            )
        }
    }
}