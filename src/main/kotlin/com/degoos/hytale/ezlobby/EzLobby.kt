package com.degoos.hytale.ezlobby

import com.degoos.hytale.ezlobby.assets.ServerIconsStorage
import com.degoos.hytale.ezlobby.commands.ServersCommand
import com.degoos.hytale.ezlobby.commands.TitleCommand
import com.degoos.hytale.ezlobby.commands.ezlobby.EzLobbyCommand
import com.degoos.hytale.ezlobby.configs.EzLobbyConfig
import com.degoos.hytale.ezlobby.configs.ServersConfig
import com.degoos.hytale.ezlobby.listeners.PlayerConnectListener
import com.degoos.hytale.ezlobby.systems.*
import com.degoos.kayle.KotlinPlugin
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import com.hypixel.hytale.server.core.util.Config

@Suppress("unused")
class EzLobby(init: JavaPluginInit) : KotlinPlugin(init) {
    private var mainConfig: Config<EzLobbyConfig?>
    private var serversConfig: Config<ServersConfig?>

    init {
        instance = this
        mainConfig = this.withConfig("EzLobbyConfig", EzLobbyConfig.CODEC)
        serversConfig = this.withConfig("EzLobbyServers", ServersConfig.CODEC)
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
        commandRegistry.registerCommand(EzLobbyCommand())
        commandRegistry.registerCommand(TitleCommand())
        commandRegistry.registerCommand(ServersCommand())
        // endregion

        // region Events
        this.eventRegistry.registerGlobal(
            PlayerConnectEvent::class.java
        ) { event: PlayerConnectEvent -> PlayerConnectListener().onPlayerReady(event) }
        // endregion

        // Load icons from storage.
        ServerIconsStorage.recreateIcons()
    }

    override fun shutdown() {
        // logger.atInfo().log("Shutdown")
    }

    companion object {
        var instance: EzLobby? = null
            private set

        fun getMainConfig(): Config<EzLobbyConfig?>? {
            return instance?.mainConfig
        }

        fun getServersConfig(): Config<ServersConfig?>? {
            return instance?.serversConfig
        }

        fun getEvetRegistry() = instance?.eventRegistry
    }
}