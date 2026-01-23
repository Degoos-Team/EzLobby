package com.degoos.hytale.ezlobby

import com.degoos.hytale.ezlobby.assets.ServerIconsStorage
import com.degoos.hytale.ezlobby.assets.ServerMenuItemInteraction
import com.degoos.hytale.ezlobby.commands.ServersCommand
import com.degoos.hytale.ezlobby.commands.TitleCommand
import com.degoos.hytale.ezlobby.commands.ezlobby.EzLobbyCommand
import com.degoos.hytale.ezlobby.configs.EzLobbyConfig
import com.degoos.hytale.ezlobby.configs.ServersConfig
import com.degoos.hytale.ezlobby.listeners.globals.PlayerConnectListener
import com.degoos.hytale.ezlobby.listeners.locals.playerMouseButtonEventConsumer
import com.degoos.hytale.ezlobby.systems.*
import com.degoos.kayle.KotlinPlugin
import com.hypixel.hytale.event.EventPriority
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent
import com.hypixel.hytale.server.core.event.events.player.PlayerMouseButtonEvent
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction
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
        // region Systems
        entityStoreRegistry.registerSystem(BreakEventSystem())
        entityStoreRegistry.registerSystem(PlaceEventSystem())
        entityStoreRegistry.registerSystem(DamageEventSystem())
        entityStoreRegistry.registerSystem(UseEventSystem())
        entityStoreRegistry.registerSystem(PickupEventSystem())
        entityStoreRegistry.registerSystem(DropItemSystem())
        //

        // region Commands
        commandRegistry.registerCommand(EzLobbyCommand())
        commandRegistry.registerCommand(TitleCommand())
        commandRegistry.registerCommand(ServersCommand())
        // endregion

        // region Global Events
        this.eventRegistry.registerGlobal(
            PlayerConnectEvent::class.java
        ) { event: PlayerConnectEvent -> PlayerConnectListener().onPlayerReady(event) }
        // endregion

        // region Events
//        this.eventRegistry.registerGlobal(
//            EventPriority.EARLY,
//            PlayerMouseButtonEvent::class.java,
//            playerMouseButtonEventConsumer
//        )



        this.eventRegistry.registerGlobal(PlayerMouseButtonEvent::class.java) {
                println("[EzLobby] Player Mouse Button Event")
        }
        // endregion

        // region Interactions
        this.getCodecRegistry(Interaction.CODEC).register(
            ServerMenuItemInteraction.INTERACTION_ID,
            ServerMenuItemInteraction::class.java, ServerMenuItemInteraction.CODEC
        )
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

        fun getEventRegistry() = instance?.eventRegistry

        fun getAssetRegistry() = instance?.assetRegistry
    }
}