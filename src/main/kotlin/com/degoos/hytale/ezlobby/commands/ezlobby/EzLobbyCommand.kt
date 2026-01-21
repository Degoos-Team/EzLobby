package com.degoos.hytale.ezlobby.commands.ezlobby

import com.degoos.hytale.ezlobby.commands.ezlobby.server.ServerCommand
import com.degoos.hytale.ezlobby.commands.ezlobby.spawn.SpawnCommand
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection

class EzLobbyCommand : AbstractCommandCollection("ezlobby", "ezlobby.commands.ezlobby.desc") {
    init {
        this.addSubCommand(ServerCommand())
        this.addSubCommand(SpawnCommand())

        this.requirePermission("ezlobby.admin")
    }
}