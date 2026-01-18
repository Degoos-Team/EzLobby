package com.degoos.hytale.ezlobby.commands.ezlobby.spawn

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection

class SpawnCommand: AbstractCommandCollection("spawn", "ezlobby.commands.ezlobby.spawn.desc") {
    init {
        this.addSubCommand(SetSpawnCommand())
        this.addSubCommand(ProtectSpawnCommand())
        this.requirePermission("ezlobby.spawn.manage")
    }
}