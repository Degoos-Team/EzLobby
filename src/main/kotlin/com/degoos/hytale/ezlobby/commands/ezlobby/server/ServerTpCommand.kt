package com.degoos.hytale.ezlobby.commands.ezlobby.server

import com.degoos.hytale.ezlobby.EzLobby
import com.degoos.hytale.ezlobby.models.Server
import com.degoos.kayle.KotlinPlugin
import com.degoos.kayle.dsl.dispatcher
import com.degoos.kayle.dsl.world
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.AbstractCommand
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes
import com.hypixel.hytale.server.core.universe.PlayerRef
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.launch
import java.util.concurrent.CompletableFuture

class ServerTpCommand(val plugin: KotlinPlugin) :
    AbstractCommand("tp", "ezlobby.commands.ezlobby.server.tp.desc") {

    private val serverIdArg: RequiredArg<Int> = this.withRequiredArg(
        "serverId", "ezlobby.commands.ezlobby.server.tp.arg.serverId",
        ArgTypes.INTEGER
    )

    private val playerArg: OptionalArg<PlayerRef> = this.withOptionalArg(
        "player", "ezlobby.commands.title.arg.player",
        ArgTypes.PLAYER_REF
    )

    override fun execute(context: CommandContext): CompletableFuture<Void>? {
        val serverId = context.get<Int>(this.serverIdArg)

//        val senderRef = context.senderAsPlayerRef()
//        val player: PlayerRef? = context.get<PlayerRef>(this.playerArg) ?: senderRef?.store?.getComponent(
//            senderRef,
//            PlayerRef.getComponentType()
//        )

        val player: PlayerRef? = context.get<PlayerRef>(this.playerArg)

        if (player == null || serverId == null) {
            context.sendMessage(Message.raw("[EzLobby] Something really weird has happened. No player could be found, not even yourself :o/"))
            return null
        }

        val server: Server = EzLobby.getServersConfig()?.get()?.servers?.getOrNull(serverId) ?: run {
            context.sendMessage(Message.raw("[EzLobby] Could not find a server with id $serverId"))
            return null
        }

        if (player.world == null) {
            context.sendMessage(Message.raw("[EzLobby] The player is not in a world right now."))
            return null
        }

        val job = plugin.launch(player.world!!.dispatcher) {
            player.referToServer(server.host, server.port)
            context.sendMessage(Message.raw("[EzLobby] User '${player.username}' is being teleported to server '${server.name}' (${server.host}:${server.port})"))
        }

        return job.asCompletableFuture().thenApply { return@thenApply null }
    }
}