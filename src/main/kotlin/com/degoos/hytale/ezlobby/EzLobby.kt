package com.degoos.hytale.ezlobby

import com.degoos.hytale.ezlobby.commands.TitleCommand
import com.degoos.hytale.ezlobby.commands.ezlobby.EzLobbyCommand
import com.degoos.hytale.ezlobby.systems.BreakEventSystem
import com.degoos.hytale.ezlobby.systems.DamageEventSystem
import com.degoos.hytale.ezlobby.systems.PickupEventSystem
import com.degoos.hytale.ezlobby.systems.PlaceEventSystem
import com.degoos.hytale.ezlobby.systems.UseEventSystem
import com.degoos.hytale.ezlobby.configs.EzLobbyConfig
import com.degoos.hytale.ezlobby.listeners.PlayerConnectListener
import com.degoos.kayle.KotlinPlugin
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import com.hypixel.hytale.server.core.util.Config

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
        entityStoreRegistry.registerSystem(BreakEventSystem())
        entityStoreRegistry.registerSystem(PlaceEventSystem())
        entityStoreRegistry.registerSystem(DamageEventSystem())
        entityStoreRegistry.registerSystem(UseEventSystem())
        entityStoreRegistry.registerSystem(PickupEventSystem())

        // region Commands
        commandRegistry.registerCommand(EzLobbyCommand(this))
        logger.atConfig().log("[Degoos:EzLobby] EzLobby Command Registered")
        commandRegistry.registerCommand(TitleCommand())
        logger.atConfig().log("[Degoos:EzLobby] EzNotify Command Registered")
        // endregion

        // region Events
        this.eventRegistry.registerGlobal(
            PlayerConnectEvent::class.java
        ) { event: PlayerConnectEvent -> PlayerConnectListener().onPlayerReady(event) }
        // endregion
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