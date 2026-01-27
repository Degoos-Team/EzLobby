package com.degoos.hytale.ezlobby_linkedserver.ui

import com.degoos.hytale.ezlobby_linkedserver.EzLobbyLinkedServer
import com.degoos.hytale.ezlobby_linkedserver.models.Server
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

class LobbyServerListEvent(var action: String? = null, var index: Int? = null) {
    companion object {
        const val KEY_ACTION = "Action"
        const val KEY_INDEX = "Index"
        const val ACTION_ADD = "Add"
        const val ACTION_EDIT = "Edit"
        const val ACTION_DELETE = "Delete"
        const val ACTION_CONFIRM_DELETE = "ConfirmDelete"

        @JvmStatic
        val CODEC: BuilderCodec<LobbyServerListEvent> = BuilderCodec.builder(LobbyServerListEvent::class.java, ::LobbyServerListEvent)
            .append(
                KeyedCodec(KEY_ACTION, Codec.STRING),
                { data, value -> data.action = value },
                { data -> data.action }
            ).add()
            .append(
                KeyedCodec(KEY_INDEX, Codec.STRING),
                { data, value -> data.index = value.toIntOrNull() },
                { data -> data.index?.toString() }
            ).add()
            .build()
    }
}

class LobbyServerListPage(player: PlayerRef) :
    InteractiveCustomUIPage<LobbyServerListEvent>(player, CustomPageLifetime.CanDismiss, LobbyServerListEvent.CODEC) {

    override fun build(
        ref: Ref<EntityStore>,
        uiCommandBuilder: UICommandBuilder,
        uiEventBuilder: UIEventBuilder,
        store: Store<EntityStore>
    ) {
        uiCommandBuilder.append("Pages/EzLobbyLinked/LobbyServerListPage.ui")

        val servers = EzLobbyLinkedServer.getLobbyServersConfig()?.get()?.lobbyServers ?: emptyList()
        servers.forEachIndexed { index, server ->
            uiCommandBuilder.append("#Content", "Pages/EzLobbyLinked/LobbyServerRow.ui")

            uiCommandBuilder.set("#Content[$index] #Host.Text", server.host)
            uiCommandBuilder.set("#Content[$index] #Port.Text", server.port.toString())

            bindButton(uiEventBuilder, index, LobbyServerListEvent.ACTION_EDIT, "#EditButton")
            bindButton(uiEventBuilder, index, LobbyServerListEvent.ACTION_DELETE, "#DeleteButton")
            bindButton(uiEventBuilder, index, LobbyServerListEvent.ACTION_CONFIRM_DELETE, "#ConfirmDeleteButton")
        }

        uiEventBuilder.addEventBinding(
            CustomUIEventBindingType.Activating, "#AddServerButton",
            EventData.of(LobbyServerListEvent.KEY_ACTION, LobbyServerListEvent.ACTION_ADD)
        )
    }

    private fun bindButton(uiEventBuilder: UIEventBuilder, index: Int, action: String, buttonId: String) {
        uiEventBuilder.addEventBinding(
            CustomUIEventBindingType.Activating, "#Content[$index] $buttonId",
            EventData.of(LobbyServerListEvent.KEY_ACTION, action)
                .append(LobbyServerListEvent.KEY_INDEX, index.toString())
        )
    }

    override fun handleDataEvent(
        ref: Ref<EntityStore>,
        store: Store<EntityStore>,
        data: LobbyServerListEvent
    ) {
        when (data.action) {
            LobbyServerListEvent.ACTION_ADD -> addServer()
            LobbyServerListEvent.ACTION_EDIT -> openEditPage(data.index ?: return)
            LobbyServerListEvent.ACTION_DELETE -> showDeleteConfirmation(data.index ?: return)
            LobbyServerListEvent.ACTION_CONFIRM_DELETE -> deleteServer(data.index ?: return)
        }
    }

    private fun addServer() {
        val config = EzLobbyLinkedServer.getLobbyServersConfig() ?: return
        val serversConfig = config.get() ?: return
        val newServer = Server(host = "127.0.0.1", port = 5520)
        serversConfig.lobbyServers.add(newServer)
        config.save()
        openEditPage(serversConfig.lobbyServers.size - 1)
    }

    private fun showDeleteConfirmation(index: Int) {
        val commandBuilder = UICommandBuilder()
        commandBuilder.set("#Content[$index] #DeleteButton.Visible", false)
        commandBuilder.set("#Content[$index] #ConfirmDeleteButton.Visible", true)
        sendUpdate(commandBuilder, UIEventBuilder(), false)
    }

    private fun deleteServer(index: Int) {
        val config = EzLobbyLinkedServer.getLobbyServersConfig() ?: return
        val serversConfig = config.get() ?: return

        if (index !in serversConfig.lobbyServers.indices) return
        serversConfig.lobbyServers.removeAt(index)
        config.save()

        refreshList()
    }

    private fun refreshList() {
        EzLobbyLinkedServer.instance?.launch(playerRef.world?.dispatcher ?: EmptyCoroutineContext) {
            val player = playerRef.reference?.store?.getComponent(playerRef.reference!!, Player.getComponentType()) ?: return@launch
            val reference = player.reference ?: return@launch
            player.pageManager.openCustomPage(reference, reference.store, LobbyServerListPage(playerRef))
        }
    }

    private fun openEditPage(index: Int) {
        EzLobbyLinkedServer.instance?.launch(playerRef.world?.dispatcher ?: EmptyCoroutineContext) {
            val player = playerRef.reference?.store?.getComponent(playerRef.reference!!, Player.getComponentType()) ?: return@launch
            val reference = player.reference ?: return@launch
            player.pageManager.openCustomPage(reference, reference.store, LobbyServerEditPage(playerRef, index))
        }
    }
}
