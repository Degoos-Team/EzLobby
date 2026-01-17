package com.degoos.hytale.ezlobby

import com.degoos.hytale.ezlobby.commands.NotifyCommand
import com.degoos.hytale.ezlobby.commands.ezlobby.EzLobbyCommand
import com.hypixel.hytale.server.core.plugin.JavaPlugin
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import java.util.concurrent.CompletableFuture

class KotlinPlugin(init: JavaPluginInit) : JavaPlugin(init) {

    override fun setup() {
        logger.atInfo().log("[Degoos:EzLobby] Plugin has been loaded")
        CompletableFuture.runAsync {
            commandRegistry.registerCommand(EzLobbyCommand())
            commandRegistry.registerCommand(NotifyCommand())
        }
    }

    override fun start() {
        // logger.atInfo().log("Start")
    }

    override fun shutdown() {
        // logger.atInfo().log("Shutdown")
    }
}