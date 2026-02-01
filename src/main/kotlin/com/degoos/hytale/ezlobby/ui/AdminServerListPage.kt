package com.degoos.hytale.ezlobby.ui

import com.degoos.hytale.ezlobby.EzLobby
import com.degoos.hytale.ezlobby.models.Server
import com.degoos.kayle.extension.dispatcher
import com.degoos.kayle.extension.world
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
import java.util.UUID
import kotlin.coroutines.EmptyCoroutineContext

class AdminServerListEvent(var action: String? = null, var serverIndex: Int? = null) {

    companion object {

        const val KEY_ACTION = "EzLobby:AdminAction"
        const val KEY_SERVER = "EzLobby:AdminServer"
        const val ACTION_CONNECT = "Connect"
        const val ACTION_EDIT = "Edit"
        const val ACTION_ADD = "Add"


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
            bindServerActionButton(uiEventBuilder, index, AdminServerListEvent.ACTION_CONNECT, "#ConnectButton")
            bindServerActionButton(uiEventBuilder, index, AdminServerListEvent.ACTION_EDIT, "#EditButton")
        }

        // Bind Add Server button
        uiEventBuilder.addEventBinding(
            CustomUIEventBindingType.Activating, "#AddServerButton",
            EventData.of(AdminServerListEvent.KEY_ACTION, AdminServerListEvent.ACTION_ADD)
        )
    }

    private fun bindServerActionButton(uiEventBuilder: UIEventBuilder, index: Int, action: String, buttonId: String) {
        uiEventBuilder.addEventBinding(
            CustomUIEventBindingType.Activating, "#Content[$index] $buttonId",
            EventData.of(AdminServerListEvent.KEY_ACTION, action)
                .append(AdminServerListEvent.KEY_SERVER, index.toString())
        )
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
                runInEzLobbyScope {
                    playerRef.referToServer(server.host, server.port)
                }
            }
            AdminServerListEvent.ACTION_EDIT -> {
                val serverIndex = data.serverIndex ?: return
                EzLobby.getServersConfig()?.get()?.servers?.getOrNull(serverIndex) ?: return
                openAdminServerEditPage(serverIndex)
            }
            AdminServerListEvent.ACTION_ADD -> {
                val config = EzLobby.getServersConfig() ?: return
                val serversConfig = config.get() ?: return

                // Create a new server with default values
                val newServer = Server(
                    id = UUID.randomUUID(),
                    name = "NewServer",
                    host = "127.0.0.1",
                    port = 5520,
                )

                // Add to list
                serversConfig.servers.add(newServer)

                // Save configuration
                config.save()

                // Open edit page for the new server
                openAdminServerEditPage(serversConfig.servers.size - 1)
            }
        }
    }

    private fun openAdminServerEditPage(serverIndex: Int) {
        runInEzLobbyScope {
            val player = playerRef.reference?.store?.getComponent(playerRef.reference!!, Player.getComponentType()) ?: return@runInEzLobbyScope
            val reference = player.reference ?: return@runInEzLobbyScope
            player.pageManager.openCustomPage(reference, reference.store, AdminServerEditPage(playerRef, serverIndex))
        }
    }

    private fun runInEzLobbyScope(block: suspend () -> Unit) {
        EzLobby.instance?.launch(playerRef.world?.dispatcher ?: EmptyCoroutineContext) {
            block()
        }
    }
}
