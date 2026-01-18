package com.degoos.hytale.ezlobby.commands.ezlobby

import com.degoos.hytale.ezlobby.commands.ezlobby.server.ServerCommand
import com.degoos.hytale.ezlobby.commands.ezlobby.spawn.SpawnCommand
import com.degoos.kayle.KotlinPlugin
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection

class EzLobbyCommand(plugin : KotlinPlugin): AbstractCommandCollection("ezlobby", "ezlobby.commands.ezlobby.desc") {
    init {
        this.addSubCommand(ServerCommand(plugin))
        this.addSubCommand(SpawnCommand(plugin))

        this.requirePermission("ezlobby.admin")
    }
}