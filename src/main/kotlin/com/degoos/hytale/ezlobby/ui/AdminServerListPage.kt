package com.degoos.hytale.ezlobby.ui

import com.degoos.hytale.ezlobby.EzLobby
import com.degoos.kayle.dsl.dispatcher
import com.degoos.kayle.dsl.world
import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage
import com.hypixel.hytale.server.core.ui.builder.EventData
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

class AdminServerListEvent(var action: String? = null, var serverIndex: Int? = null) {

    companion object {

        const val KEY_ACTION = "EzLobby:AdminAction"
        const val KEY_SERVER = "EzLobby:AdminServer"
        const val ACTION_CONNECT = "Connect"
        const val ACTION_EDIT = "Edit"


        @JvmStatic
        val CODEC: BuilderCodec<AdminServerListEvent> = BuilderCodec.builder(AdminServerListEvent::class.java, ::AdminServerListEvent)
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



class AdminServerListPage(player: PlayerRef) :
    InteractiveCustomUIPage<AdminServerListEvent>(player, CustomPageLifetime.CanDismiss, AdminServerListEvent.CODEC) {

    override fun build(
        ref: Ref<EntityStore>,
        uiCommandBuilder: UICommandBuilder,
        uiEventBuilder: UIEventBuilder,
        store: Store<EntityStore>
    ) {
        uiCommandBuilder.append("Pages/EzLobby/AdminServerListPage.ui")

        EzLobby.getServersConfig()?.get()?.servers?.forEachIndexed { index, server ->
            uiCommandBuilder.append("#Content", "Pages/EzLobby/AdminServerRow.ui")

            // Use utility to populate the server row
            ServerRowUtils.populateAdminServerRow(uiCommandBuilder, "#Content[$index] #ConnectButton", server)

            // Bind events
            uiEventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating, "#Content[$index] #ConnectButton",
                EventData.of(AdminServerListEvent.KEY_ACTION, AdminServerListEvent.ACTION_CONNECT)
                    .append(AdminServerListEvent.KEY_SERVER, index.toString())
            )

            uiEventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating, "#Content[$index] #EditButton",
                EventData.of(AdminServerListEvent.KEY_ACTION, AdminServerListEvent.ACTION_EDIT)
                    .append(AdminServerListEvent.KEY_SERVER, index.toString())
            )
        }
    }

    override fun handleDataEvent(
        ref: Ref<EntityStore>,
        store: Store<EntityStore>,
        data: AdminServerListEvent
    ) {
        sendUpdate()
        when (data.action) {
            AdminServerListEvent.ACTION_CONNECT -> {
                val serverIndex = data.serverIndex ?: return
                val server = EzLobby.getServersConfig()?.get()?.servers?.getOrNull(serverIndex) ?: return
                EzLobby.instance?.launch(playerRef.world?.dispatcher ?: EmptyCoroutineContext) {
                    playerRef.referToServer(server.host, server.port)
                }
            }
            AdminServerListEvent.ACTION_EDIT -> {
                val serverIndex = data.serverIndex ?: return
                EzLobby.getServersConfig()?.get()?.servers?.getOrNull(serverIndex) ?: return

                EzLobby.instance?.launch(playerRef.world?.dispatcher ?: EmptyCoroutineContext) {
                    val player = playerRef.reference?.store?.getComponent(playerRef.reference!!, Player.getComponentType()) ?: return@launch
                    val reference = player.reference ?: return@launch
                    player.pageManager.openCustomPage(reference, reference.store, AdminServerEditPage(playerRef, serverIndex))
                }
            }
        }
    }

}
