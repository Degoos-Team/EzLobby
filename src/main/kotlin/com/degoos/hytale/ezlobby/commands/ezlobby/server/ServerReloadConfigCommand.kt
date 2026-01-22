package com.degoos.hytale.ezlobby.commands.ezlobby.server

import com.degoos.hytale.ezlobby.EzLobby
import com.degoos.hytale.ezlobby.assets.ServerIconsStorage
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase

class ServerReloadConfigCommand : CommandBase("reload", "ezlobby.commands.ezlobby.server.reload.desc") {

    override fun executeSync(context: CommandContext) {
        context.sendMessage(Message.translation("ezlobby.messages.success.config.reloading"))
        EzLobby.getServersConfig()?.load()?.thenRun {
            ServerIconsStorage.recreateIcons()
            context.sendMessage(Message.translation("ezlobby.messages.success.config.reloaded"))
        }
    }
}