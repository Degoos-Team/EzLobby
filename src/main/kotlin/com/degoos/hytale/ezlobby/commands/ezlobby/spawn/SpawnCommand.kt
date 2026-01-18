package com.degoos.hytale.ezlobby.commands.ezlobby.spawn

import com.degoos.kayle.KotlinPlugin
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection

class SpawnCommand(plugin : KotlinPlugin): AbstractCommandCollection("spawn", "ezlobby.commands.ezlobby.spawn.desc") {
    init {
        this.addSubCommand(SetSpawnCommand())
        this.addSubCommand(ProtectSpawnCommand(plugin))
        this.requirePermission("ezlobby.spawn.manage")
    }
}