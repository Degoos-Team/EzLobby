package com.degoos.hytale.ezlobby.commands.ezlobby.server

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection

class ServerCommand :
    AbstractCommandCollection("server", "ezlobby.commands.ezlobby.server.desc") {
    init {
        this.addSubCommand(ServerAddCommand())
        this.addSubCommand(ServerRemoveCommand())
        this.addSubCommand(ServerListCommand())
        this.addSubCommand(ServerTpCommand())
        this.addSubCommand(ServerReloadConfigCommand())
        this.requirePermission("ezlobby.server.manage")
    }
}