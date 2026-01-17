package com.degoos.hytale.ezlobby.commands

import com.degoos.hytale.ezlobby.utils.ServerUtil
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase
import com.hypixel.hytale.server.core.universe.Universe
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.util.EventTitleUtil
import javax.annotation.Nonnull


class NotifyCommand : CommandBase("notify", "Show a title to a player or the server", false) {
    @Nonnull
    private val titleArg: RequiredArg<String?> = this.withRequiredArg(
        "title", "ezlobby.commands.notify.arg.title",
        ArgTypes.STRING
    )

    @Nonnull
    private val subtitleArg: DefaultArg<String?> = this.withDefaultArg(
        "subtitle", "ezlobby.commands.notify.arg.subtitle",
        ArgTypes.STRING, "", "ezlobby.commands.notify.arg.subtitle.defaultValue"
    )

    @Nonnull
    private val worldArg: DefaultArg<World?> = this.withDefaultArg(
        "world", "ezlobby.commands.notify.arg.title",
        ArgTypes.WORLD, Universe.get().defaultWorld, "ezlobby.commands.notify.arg.world.defaultValue"
    )

    init {
        this.requirePermission("ezlobby.notify")
    }

    override fun executeSync(@Nonnull ctx: CommandContext) {
        val title = ctx.get<String>(this.titleArg)
        val subtitle = ctx.get<String>(this.subtitleArg)
        val world = ctx.get<World>(this.worldArg) ?: Universe.get().defaultWorld

        world?.execute { EventTitleUtil.showEventTitleToUniverse(
            Message.raw(title),
            Message.raw(subtitle),
            false,
            null,
            4f,
            1.5f,
            1.5f
        ) }
    }
}