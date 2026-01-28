package com.degoos.hytale.ezlobby

import com.degoos.hytale.ezlobby.assets.ServerIconsStorage
import com.degoos.hytale.ezlobby.commands.ServersCommand
import com.degoos.hytale.ezlobby.commands.TitleCommand
import com.degoos.hytale.ezlobby.commands.ezlobby.EzLobbyCommand
import com.degoos.hytale.ezlobby.configs.EzLobbyConfig
import com.degoos.hytale.ezlobby.configs.ServersConfig
import com.degoos.hytale.ezlobby.interactions.ServerMenuItemInteraction
import com.degoos.hytale.ezlobby.interactions.VisibilityTogglerItemInteraction
import com.degoos.hytale.ezlobby.listeners.globals.PlayerConnectListener
import com.degoos.hytale.ezlobby.listeners.globals.PlayerReadyListener
import com.degoos.hytale.ezlobby.managers.VisibilityManager
import com.degoos.hytale.ezlobby.systems.*
import com.degoos.kayle.KotlinPlugin
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import com.hypixel.hytale.server.core.util.Config

@Suppress("unused")
class EzLobby(init: JavaPluginInit) : KotlinPlugin(init) {
    private var mainConfig: Config<EzLobbyConfig?>
    private var serversConfig: Config<ServersConfig?>

    private var visibilityManager: VisibilityManager = VisibilityManager()

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

        this.eventRegistry.registerGlobal(PlayerReadyEvent::class.java) { event: PlayerReadyEvent ->
            PlayerReadyListener().onPlayerReady(
                event
            )
        }
        // endregion

        // region Interactions
        this.getCodecRegistry(Interaction.CODEC).register(
            ServerMenuItemInteraction.INTERACTION_ID,
            ServerMenuItemInteraction::class.java,
            ServerMenuItemInteraction.CODEC
        )
        this.getCodecRegistry(Interaction.CODEC).register(
            VisibilityTogglerItemInteraction.INTERACTION_ID,
            VisibilityTogglerItemInteraction::class.java,
            VisibilityTogglerItemInteraction.CODEC
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

        fun getVisibilityManager(): VisibilityManager? {
            return instance?.visibilityManager
        }
    }
}