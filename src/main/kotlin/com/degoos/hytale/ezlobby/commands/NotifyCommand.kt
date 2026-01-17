package com.degoos.hytale.ezlobby.commands

import com.degoos.hytale.ezlobby.utils.ServerUtil
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.Universe
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.util.EventTitleUtil


class NotifyCommand : CommandBase("notify", "Show a title to a player or the server", false) {
    private val titleArg: RequiredArg<String?> = this.withRequiredArg(
        "title", "ezlobby.commands.notify.arg.title",
        ArgTypes.STRING
    )

    private val subtitleArg: DefaultArg<String?> = this.withDefaultArg(
        "subtitle", "ezlobby.commands.notify.arg.subtitle",
        ArgTypes.STRING, "", "ezlobby.commands.notify.arg.subtitle.defaultValue"
    )

    private val worldArg: DefaultArg<World?> = this.withDefaultArg(
        "world", "ezlobby.commands.notify.arg.world",
        ArgTypes.WORLD, Universe.get().defaultWorld, "ezlobby.commands.notify.arg.world.defaultValue"
    )

    private val playerArg: OptionalArg<PlayerRef> = this.withOptionalArg(
        "player", "ezlobby.commands.notify.arg.player",
        ArgTypes.PLAYER_REF
    )

    private val broadcastArg: DefaultArg<Boolean> = this.withDefaultArg(
        "world", "ezlobby.commands.notify.arg.world",
        ArgTypes.BOOLEAN, true, "ezlobby.commands.notify.arg.broadcast.defaultValue"
    )

    init {
        this.requirePermission("ezlobby.notify")
    }

    fun broadcastNotify(title: String, subtitle: String) {
        ServerUtil.executeWorld {
            EventTitleUtil.showEventTitleToUniverse(
                Message.raw(title),
                Message.raw(subtitle),
                false,
                null,
                4f,
                1.5f,
                1.5f
            )
        }
    }

    fun notifyWorld(world: World, title: String, subtitle: String) {
        EventTitleUtil.showEventTitleToWorld(
            Message.raw(title),
            Message.raw(subtitle),
            false,
            null,
            4f,
            1.5f,
            1.5f,
            world.entityStore.store
        )
    }

    fun notifyPlayer(player: PlayerRef, title: String, subtitle: String) {
        EventTitleUtil.showEventTitleToPlayer(
            player,
            Message.raw(title),
            Message.raw(subtitle),
            false,
            null,
            4f,
            1.5f,
            1.5f
        )
    }

    override fun executeSync(ctx: CommandContext) {
        val title = ctx.get<String>(this.titleArg)
        val subtitle = ctx.get<String>(this.subtitleArg)
        val world = ctx.get<World>(this.worldArg) ?: Universe.get().defaultWorld
        val player = ctx.get<PlayerRef?>(this.playerArg)
        val broadcast = ctx.get<Boolean>(this.broadcastArg)

        if(broadcast) {
            broadcastNotify(title, subtitle)
        } else if(player != null) {
            notifyPlayer(player, title, subtitle)
        } else if(world != null) {
            notifyWorld(world, title, subtitle)
        }
    }
}