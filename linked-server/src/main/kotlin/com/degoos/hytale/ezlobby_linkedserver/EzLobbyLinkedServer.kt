package com.degoos.hytale.ezlobby_linkedserver


import com.degoos.hytale.ezlobby_linkedserver.commands.LobbyCommand
import com.degoos.hytale.ezlobby_linkedserver.commands.LobbyServersAdminCommand
import com.degoos.hytale.ezlobby_linkedserver.configs.LobbyServersConfig
import com.degoos.hytale.ezlobby_linkedserver.utils.getRandomServer
import com.degoos.hytale.ezlobby_linkedserver.utils.validateReferralData
import com.degoos.kayle.KotlinPlugin
import com.hypixel.hytale.server.core.event.events.player.PlayerSetupConnectEvent
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import com.hypixel.hytale.server.core.util.Config


@Suppress("unused")
class EzLobbyLinkedServer(init: JavaPluginInit) : KotlinPlugin(init) {
    private var lobbyServersConfig: Config<LobbyServersConfig?>

    init {
        instance = this
        lobbyServersConfig = this.withConfig("EzLobbyServers", LobbyServersConfig.CODEC)
    }

    override fun setup() {
        logger.atInfo().log("[Degoos:EzLobby:LinkedServer] Plugin has been loaded")
    }

    override fun start() {

        // region Commands
        commandRegistry.registerCommand(LobbyCommand())
        commandRegistry.registerCommand(LobbyServersAdminCommand())
        // endregion

        // region Global Events
        if(lobbyServersConfig.get()?.forceEzLobbyReferral ?: false) {
            eventRegistry.registerGlobal(
                PlayerSetupConnectEvent::class.java
            ) { event ->
                if(!event.isReferralConnection || !validateReferralData(event.referralData)) {
                    val server = getRandomServer() ?: return@registerGlobal
                    event.reason = "EzLobby Linked Server - Forced Referral"
                    event.referToServer(server.host, server.port)
                }
            }
        }
        // endregion
    }

    override fun shutdown() {
        // logger.atInfo().log("Shutdown")
    }

    companion object {
        var instance: EzLobbyLinkedServer? = null
            private set

        fun getLobbyServersConfig(): Config<LobbyServersConfig?>? {
            return instance?.lobbyServersConfig
        }

        fun getEventRegistry() = instance?.eventRegistry

        fun getAssetRegistry() = instance?.assetRegistry
    }
}