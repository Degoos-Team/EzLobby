package com.degoos.hytale.ezlobby_linkedserver.ui

import com.degoos.hytale.ezlobby_linkedserver.EzLobbyLinkedServer
import com.degoos.hytale.ezlobby_linkedserver.configs.LobbyServersConfig
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
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage
import com.hypixel.hytale.server.core.ui.builder.EventData
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

class LobbyServerEditEvent(
    var action: String? = null,
    var host: String? = null,
    var port: String? = null
) {
    companion object {
        const val KEY_ACTION = "Action"
        const val KEY_HOST = "@Host"
        const val KEY_PORT = "@Port"

        const val ACTION_SAVE = "Save"
        const val ACTION_CANCEL = "Cancel"
        const val ACTION_DELETE = "Delete"
        const val ACTION_CONFIRM_DELETE = "ConfirmDelete"

        @JvmStatic
        val CODEC: BuilderCodec<LobbyServerEditEvent> = BuilderCodec.builder(LobbyServerEditEvent::class.java, ::LobbyServerEditEvent)
            .append(
                KeyedCodec(KEY_ACTION, Codec.STRING),
                { data, value -> data.action = value },
                { data -> data.action }
            ).add()
            .append(
                KeyedCodec(KEY_HOST, Codec.STRING),
                { data, value -> data.host = value },
                { data -> data.host }
            ).add()
            .append(
                KeyedCodec(KEY_PORT, Codec.STRING),
                { data, value -> data.port = value },
                { data -> data.port }
            ).add()
            .build()
    }
}

class LobbyServerEditPage(player: PlayerRef, private val serverIndex: Int) :
    InteractiveCustomUIPage<LobbyServerEditEvent>(player, CustomPageLifetime.CanDismiss, LobbyServerEditEvent.CODEC) {

    override fun build(
        ref: Ref<EntityStore>,
        uiCommandBuilder: UICommandBuilder,
        uiEventBuilder: UIEventBuilder,
        store: Store<EntityStore>
    ) {
        uiCommandBuilder.append("Pages/EzLobbyLinked/LobbyServerEditPage.ui")

        val server = EzLobbyLinkedServer.getLobbyServersConfig()?.get()?.lobbyServers?.getOrNull(serverIndex)
        if (server == null) {
            playerRef.sendMessage(Message.raw("Server not found"))
            return
        }

        uiCommandBuilder.set("#HostField.Value", server.host)
        uiCommandBuilder.set("#PortField.Value", server.port.toString())

        uiEventBuilder.addEventBinding(
            CustomUIEventBindingType.Activating, "#SaveButton",
            EventData.of(LobbyServerEditEvent.KEY_ACTION, LobbyServerEditEvent.ACTION_SAVE)
                .append(LobbyServerEditEvent.KEY_HOST, "#HostField.Value")
                .append(LobbyServerEditEvent.KEY_PORT, "#PortField.Value")
        )

        uiEventBuilder.addEventBinding(
            CustomUIEventBindingType.Activating, "#CancelButton",
            EventData.of(LobbyServerEditEvent.KEY_ACTION, LobbyServerEditEvent.ACTION_CANCEL)
        )

        uiEventBuilder.addEventBinding(
            CustomUIEventBindingType.Activating, "#DeleteButton",
            EventData.of(LobbyServerEditEvent.KEY_ACTION, LobbyServerEditEvent.ACTION_DELETE)
        )

        uiEventBuilder.addEventBinding(
            CustomUIEventBindingType.Activating, "#ConfirmDeleteButton",
            EventData.of(LobbyServerEditEvent.KEY_ACTION, LobbyServerEditEvent.ACTION_CONFIRM_DELETE)
        )
    }

    override fun handleDataEvent(
        ref: Ref<EntityStore>,
        store: Store<EntityStore>,
        data: LobbyServerEditEvent
    ) {
        when (data.action) {
            LobbyServerEditEvent.ACTION_SAVE -> save(data)
            LobbyServerEditEvent.ACTION_CANCEL -> returnToList()
            LobbyServerEditEvent.ACTION_DELETE -> showDeleteConfirmation()
            LobbyServerEditEvent.ACTION_CONFIRM_DELETE -> deleteAndReturn()
        }
    }

    private fun save(data: LobbyServerEditEvent) {
        val config = EzLobbyLinkedServer.getLobbyServersConfig() ?: return
        val serversConfig = config.get() ?: LobbyServersConfig()
        val server = serversConfig.lobbyServers.getOrNull(serverIndex) ?: return

        data.host?.takeIf { it.isNotBlank() }?.let { server.host = it }
        data.port?.toIntOrNull()?.let { server.port = it }

        config.save()
        playerRef.sendMessage(Message.raw("Lobby server saved"))
        returnToList()
    }

    private fun deleteAndReturn() {
        val config = EzLobbyLinkedServer.getLobbyServersConfig() ?: return
        val serversConfig = config.get() ?: return
        if (serverIndex !in serversConfig.lobbyServers.indices) return

        serversConfig.lobbyServers.removeAt(serverIndex)
        config.save()
        playerRef.sendMessage(Message.raw("Lobby server removed"))
        returnToList()
    }

    private fun returnToList() {
        EzLobbyLinkedServer.instance?.launch(playerRef.world?.dispatcher ?: EmptyCoroutineContext) {
            val player = playerRef.reference?.store?.getComponent(playerRef.reference!!, Player.getComponentType()) ?: return@launch
            val reference = player.reference ?: return@launch
            player.pageManager.openCustomPage(reference, reference.store, LobbyServerListPage(playerRef))
        }
    }

    private fun showDeleteConfirmation() {
        val commandBuilder = UICommandBuilder()
        commandBuilder.set("#DeleteButton.Visible", false)
        commandBuilder.set("#ConfirmDeleteButton.Visible", true)
        sendUpdate(commandBuilder, UIEventBuilder(), false)
    }
}
