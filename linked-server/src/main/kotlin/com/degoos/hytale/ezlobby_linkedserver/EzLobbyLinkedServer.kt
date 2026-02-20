package com.degoos.hytale.ezlobby_linkedserver


import com.degoos.hytale.ezlobby_linkedserver.commands.LobbyCommand
import com.degoos.hytale.ezlobby_linkedserver.commands.LobbyServersAdminCommand
import com.degoos.hytale.ezlobby_linkedserver.configs.LobbyServersConfig
import com.degoos.hytale.ezlobby_linkedserver.utils.getRandomServer
import com.degoos.hytale.ezlobby_linkedserver.utils.validateReferralData
import com.degoos.kayle.KotlinPlugin
import com.hypixel.hytale.server.core.event.events.ShutdownEvent
import com.hypixel.hytale.server.core.event.events.player.PlayerSetupConnectEvent
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.Universe
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

        // region Events
        if (lobbyServersConfig.get()?.redirectToLobbyOnShutdown == true) {
            // Priority -60 fires before ShutdownEvent.DISCONNECT_PLAYERS (-48), so packets go out before transport tears down
            eventRegistry.register((-60).toShort(), ShutdownEvent::class.java) {
                val universe = Universe.get() ?: return@register
                val players = universe.players.toList()
                if (players.isEmpty()) return@register
                logger.atInfo().log("[EzLobby:LinkedServer] Redirecting %d player(s) to lobby before shutdown.", players.size)
                players.forEach { player -> redirectPlayerToLobby(player) }
                // Give Netty a moment to flush the referral packets before transport closes
                Thread.sleep(3_000)
            }
        }
        // endregion

        // region Global Events
        if(lobbyServersConfig.get()?.forceEzLobbyReferral ?: false) {
            logger.atInfo().log("Force referral is enabled, players without valid referral will be redirected to a lobby server")
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

        fun redirectPlayerToLobby(player: PlayerRef) {
            val server = getRandomServer() ?: return
            player.referToServer(server.host, server.port)
        }
    }
}