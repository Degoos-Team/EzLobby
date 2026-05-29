package com.degoos.hytale.ezlobby.ui

import com.degoos.hytale.ezlobby.EzLobby
import com.degoos.hytale.ezlobby.managers.ServerStatusChecker
import com.degoos.hytale.ezlobby.models.ServerStatus
import com.degoos.hytale.ezlobby.utils.createEzLobbyReferralData
import com.degoos.hytale.ezlobby.utils.findServer
import com.degoos.kayle.extension.dispatcher
import com.degoos.kayle.extension.world
import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage
import com.hypixel.hytale.server.core.ui.builder.EventData
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.coroutines.EmptyCoroutineContext

class ServerListEvent(var action: String? = null, var serverId: UUID? = null) {

    companion object {

        const val KEY_ACTION = "EzLobby:Action"
        const val KEY_SERVER = "EzLobby:Server"
        const val ACTION_CONNECT = "Connect"


        @JvmStatic
        val CODEC: BuilderCodec<ServerListEvent> = BuilderCodec.builder(ServerListEvent::class.java, ::ServerListEvent)
            .append(
                KeyedCodec(KEY_ACTION, Codec.STRING),
                { data, value -> data.action = value },
                { data -> data.action }
            ).add()
            .append(
                KeyedCodec(KEY_SERVER, Codec.STRING),
                { data, value -> data.serverId = runCatching { UUID.fromString(value) }.getOrNull() },
                { data -> data.serverId?.toString() }
            ).add()
            .build()
    }
}



class ServerListPage(player: PlayerRef) :
    InteractiveCustomUIPage<ServerListEvent>(player, CustomPageLifetime.CanDismiss, ServerListEvent.CODEC) {

    private var pageScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun build(
        ref: Ref<EntityStore>,
        uiCommandBuilder: UICommandBuilder,
        uiEventBuilder: UIEventBuilder,
        store: Store<EntityStore>
    ) {
        pageScope.cancel()
        pageScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

        uiCommandBuilder.append("Pages/EzLobby/ServerList.ui")

        val servers = EzLobby.getServersConfig()?.get()?.servers ?: return

        // Fire requestCheck for all servers before rendering (D-02)
        servers.forEach { server -> ServerStatusChecker.requestCheck(server, pageScope) }

        servers.forEachIndexed { index, server ->
            val buttonSelector = "#Content[$index]"
            val status = ServerStatusChecker.getStatus(server)

            uiCommandBuilder.append("#Content", "Pages/EzLobby/ServerRow.ui")

            // Use utility to populate the server row with button tinting and status circle
            ServerRowUtils.populateServerRow(uiCommandBuilder, buttonSelector, server, status)

            val connectEvent = EventData.of(ServerListEvent.KEY_ACTION, ServerListEvent.ACTION_CONNECT)
                .append(ServerListEvent.KEY_SERVER, server.id.toString())
            uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, buttonSelector, connectEvent)
            uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "$buttonSelector #StatusCircle", connectEvent)
        }

        // Launch page-scoped coroutine: poll until no CHECKING, sendUpdate, then periodic 30s refresh
        pageScope.launch {
            // Initial poll: wait until no server is CHECKING or 2500ms cap reached
            val deadline = System.currentTimeMillis() + 2500
            while (servers.any { ServerStatusChecker.getStatus(it) == ServerStatus.CHECKING }
                && System.currentTimeMillis() < deadline) {
                delay(100)
            }
            val worldDispatcher = playerRef.world?.dispatcher ?: return@launch
            if (isActive) {
                EzLobby.instance?.launch(worldDispatcher) { sendUpdate() }
            }

            // Periodic 30s refresh loop
            while (isActive) {
                delay(30_000)
                servers.forEach { server -> ServerStatusChecker.requestCheck(server, pageScope) }
                val refreshDeadline = System.currentTimeMillis() + 2500
                while (servers.any { ServerStatusChecker.getStatus(it) == ServerStatus.CHECKING }
                    && System.currentTimeMillis() < refreshDeadline) {
                    delay(100)
                }
                if (isActive) {
                    EzLobby.instance?.launch(worldDispatcher) { sendUpdate() }
                }
            }
        }
    }

    override fun onDismiss(ref: Ref<EntityStore>, store: Store<EntityStore>) {
        pageScope.cancel()
    }

    override fun handleDataEvent(
        ref: Ref<EntityStore>,
        store: Store<EntityStore>,
        data: ServerListEvent
    ) {
        when (data.action) {
            ServerListEvent.ACTION_CONNECT -> {
                val uuid = data.serverId ?: return
                val server = findServer(null, null, uuid) ?: return
                if (ServerStatusChecker.getStatus(server) == ServerStatus.OFFLINE) {
                    EzLobby.instance?.launch(playerRef.world?.dispatcher ?: EmptyCoroutineContext) {
                        playerRef.sendMessage(
                            Message.translation("ezlobby.messages.error.server.offline")
                        )
                    }
                    return
                }
                sendUpdate()  // only re-render when actually proceeding to connect
                EzLobby.instance?.launch(playerRef.world?.dispatcher ?: EmptyCoroutineContext) {
                    playerRef.referToServer(server.host, server.port, createEzLobbyReferralData())
                }
            }
        }
    }

}
