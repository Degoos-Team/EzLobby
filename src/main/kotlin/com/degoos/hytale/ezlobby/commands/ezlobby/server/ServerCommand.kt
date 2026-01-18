package com.degoos.hytale.ezlobby.commands.ezlobby.server

import com.degoos.kayle.KotlinPlugin
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection

class ServerCommand(plugin : KotlinPlugin): AbstractCommandCollection("server", "ezlobby.commands.ezlobby.server.desc") {
    init {
        this.addSubCommand(ServerAddCommand())
        this.addSubCommand(ServerRemoveCommand())
        this.addSubCommand(ServerListCommand())
        this.addSubCommand(ServerTpCommand(plugin))
        this.requirePermission("ezlobby.server.manage")
    }
}