package com.degoos.hytale.ezlobby

import com.degoos.hytale.ezlobby.commands.TitleCommand
import com.degoos.hytale.ezlobby.commands.ezlobby.EzLobbyCommand
import com.degoos.hytale.ezlobby.configs.EzLobbyConfig
import com.degoos.hytale.ezlobby.listeners.PlayerReadyListener
import com.degoos.kayle.KotlinPlugin
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import com.hypixel.hytale.server.core.util.Config
import java.util.concurrent.CompletableFuture


@Suppress("unused")
class EzLobby(init: JavaPluginInit) : KotlinPlugin(init) {
    private var mainConfig: Config<EzLobbyConfig?>

    init {
        instance = this
        mainConfig = this.withConfig("EzLobbyConfig", EzLobbyConfig.CODEC)
    }

    override fun setup() {
        logger.atInfo().log("[Degoos:EzLobby] Plugin has been loaded")
    }

    override fun start() {
        CompletableFuture.runAsync {
            // region Commands
            commandRegistry.registerCommand(EzLobbyCommand())
            logger.atConfig().log("[Degoos:EzLobby] EzLobby Command Registered")
            commandRegistry.registerCommand(TitleCommand())
            logger.atConfig().log("[Degoos:EzLobby] EzTitle Command Registered")
            // endregion

            // region Events
            this.eventRegistry.registerGlobal(
                PlayerReadyEvent::class.java
            ) { event: PlayerReadyEvent -> PlayerReadyListener().onPlayerReady(event) }
            // endregion
        }
    }

    override fun shutdown() {
        // logger.atInfo().log("Shutdown")
    }

    companion object {
        private var instance: EzLobby? = null
        fun getMainConfig(): Config<EzLobbyConfig?>? {
            return instance?.mainConfig
        }
        fun getEvetRegistry() = instance?.eventRegistry
    }
}