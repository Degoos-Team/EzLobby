package com.degoos.hytale.ezlobby.ui

import com.degoos.hytale.ezlobby.EzLobby
import com.degoos.hytale.ezlobby.utils.createEzLobbyReferralData
import com.degoos.kayle.dsl.dispatcher
import com.degoos.kayle.dsl.world
import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage
import com.hypixel.hytale.server.core.ui.builder.EventData
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

class ServerListEvent(var action: String? = null, var serverIndex: Int? = null) {

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
                { data, value -> data.serverIndex = value.toIntOrNull() },
                { data -> data.serverIndex?.toString() }
            ).add()
            .build()
    }
}



class ServerListPage(player: PlayerRef) :
    InteractiveCustomUIPage<ServerListEvent>(player, CustomPageLifetime.CanDismiss, ServerListEvent.CODEC) {

    override fun build(
        ref: Ref<EntityStore>,
        uiCommandBuilder: UICommandBuilder,
        uiEventBuilder: UIEventBuilder,
        store: Store<EntityStore>
    ) {
        uiCommandBuilder.append("Pages/EzLobby/ServerList.ui")

        EzLobby.getServersConfig()?.get()?.servers?.forEachIndexed { index, server ->
            val buttonSelector = "#Content[$index]"

            uiCommandBuilder.append("#Content", "Pages/EzLobby/ServerRow.ui")

            // Use utility to populate the server row with button tinting
            ServerRowUtils.populateServerRow(uiCommandBuilder, buttonSelector, server)


            // Bind connect event
            uiEventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating, buttonSelector,
                EventData.of(ServerListEvent.KEY_ACTION, ServerListEvent.ACTION_CONNECT)
                    .append(ServerListEvent.KEY_SERVER, index.toString())
            )
        }
    }

    override fun handleDataEvent(
        ref: Ref<EntityStore>,
        store: Store<EntityStore>,
        data: ServerListEvent
    ) {
        sendUpdate()
        when (data.action) {
            ServerListEvent.ACTION_CONNECT -> {
                val serverInde = data.serverIndex ?: return
                val server = EzLobby.getServersConfig()?.get()?.servers?.getOrNull(serverInde) ?: return
                EzLobby.instance?.launch(playerRef.world?.dispatcher ?: EmptyCoroutineContext) {
                    playerRef.referToServer(server.host, server.port, createEzLobbyReferralData())
                }
            }
        }
    }

}