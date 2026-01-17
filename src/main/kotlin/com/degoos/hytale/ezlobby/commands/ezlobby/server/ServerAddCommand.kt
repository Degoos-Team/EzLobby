package com.degoos.hytale.ezlobby.commands.ezlobby.server

import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase
import javax.annotation.Nonnull


class ServerAddCommand : CommandBase("add", "ezlobby.commands.ezlobby.server.add.desc") {
    override fun executeSync(@Nonnull context: CommandContext) {
        context.sendMessage(Message.raw("Add a server, biatch."))
    }
}