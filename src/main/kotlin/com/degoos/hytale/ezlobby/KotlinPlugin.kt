package com.degoos.hytale.ezlobby

import com.degoos.hytale.ezlobby.commands.NotifyCommand
import com.degoos.hytale.ezlobby.commands.ezlobby.EzLobbyCommand
import com.hypixel.hytale.server.core.plugin.JavaPlugin
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import java.util.concurrent.CompletableFuture

class KotlinPlugin(init: JavaPluginInit) : JavaPlugin(init) {

    override fun setup() {
        logger.atInfo().log("[Degoos:EzLobby] Plugin has been loaded")
    }

    override fun start() {
        CompletableFuture.runAsync {
            commandRegistry.registerCommand(EzLobbyCommand())
            logger.atConfig().log("[Degoos:EzLobby] EzLobby Command Registered")
            commandRegistry.registerCommand(NotifyCommand())
            logger.atConfig().log("[Degoos:EzLobby] EzNotify Command Registered")
        }
    }

    override fun shutdown() {
        // logger.atInfo().log("Shutdown")
    }
}