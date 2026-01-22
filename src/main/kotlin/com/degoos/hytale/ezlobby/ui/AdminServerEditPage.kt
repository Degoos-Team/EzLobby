package com.degoos.hytale.ezlobby.ui

import com.degoos.hytale.ezlobby.EzLobby
import com.degoos.hytale.ezlobby.models.Server
import com.degoos.kayle.dsl.dispatcher
import com.degoos.kayle.dsl.world
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
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

class AdminServerEditEvent(
    var action: String? = null,
    var name: String? = null,
    var displayName: String? = null,
    var description: String? = null,
    var host: String? = null,
    var port: String? = null,
    var icon: String? = null,
    var colorTint: String? = null
) {

    companion object {
        const val KEY_ACTION = "Action"
        const val KEY_NAME = "@Name"
        const val KEY_DISPLAY_NAME = "@DisplayName"
        const val KEY_DESCRIPTION = "@Description"
        const val KEY_HOST = "@Host"
        const val KEY_PORT = "@Port"
        const val KEY_ICON = "@Icon"
        const val KEY_COLOR_TINT = "@ColorTint"

        const val ACTION_SAVE = "Save"
        const val ACTION_CANCEL = "Cancel"

        @JvmStatic
        val CODEC: BuilderCodec<AdminServerEditEvent> = BuilderCodec.builder(AdminServerEditEvent::class.java, ::AdminServerEditEvent)
            .append(
                KeyedCodec(KEY_ACTION, Codec.STRING),
                { data, value -> data.action = value },
                { data -> data.action }
            ).add()
            .append(
                KeyedCodec(KEY_NAME, Codec.STRING),
                { data, value -> data.name = value },
                { data -> data.name }
            ).add()
            .append(
                KeyedCodec(KEY_DISPLAY_NAME, Codec.STRING),
                { data, value -> data.displayName = value },
                { data -> data.displayName }
            ).add()
            .append(
                KeyedCodec(KEY_DESCRIPTION, Codec.STRING),
                { data, value -> data.description = value },
                { data -> data.description }
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
            .append(
                KeyedCodec(KEY_ICON, Codec.STRING),
                { data, value -> data.icon = value },
                { data -> data.icon }
            ).add()
            .append(
                KeyedCodec(KEY_COLOR_TINT, Codec.STRING),
                { data, value -> data.colorTint = value },
                { data -> data.colorTint }
            ).add()
            .build()
    }
}

class AdminServerEditPage(player: PlayerRef, private val serverIndex: Int) :
    InteractiveCustomUIPage<AdminServerEditEvent>(player, CustomPageLifetime.CanDismiss, AdminServerEditEvent.CODEC) {

    val server: Server? = EzLobby.getServersConfig()?.get()?.servers?.getOrNull(serverIndex)

    // Store current values for live preview
    private var currentName: String = ""
    private var currentDisplayName: String? = null
    private var currentDescription: String? = null
    private var currentHost: String = ""
    private var currentPort: Int = 25565
    private var currentIcon: String? = null
    private var currentColorTint: String? = null

    override fun build(
        ref: Ref<EntityStore>,
        uiCommandBuilder: UICommandBuilder,
        uiEventBuilder: UIEventBuilder,
        store: Store<EntityStore>
    ) {
        uiCommandBuilder.append("Pages/EzLobby/AdminServerEditPage.ui")

        if (server == null) {
            playerRef.sendMessage(Message.translation("ezlobby.messages.error.server.not.found")
                .param("selector", serverIndex.toString()))
            return
        }

        // Initialize current values
        currentName = server.name
        currentDisplayName = server.displayName
        currentDescription = server.description
        currentHost = server.host
        currentPort = server.port
        currentIcon = server.uiIcon
        currentColorTint = server.uiColorTint

        // Set field values
        uiCommandBuilder.set("#NameField.Value", server.name)
        uiCommandBuilder.set("#DisplayNameField.Value", server.displayName ?: "")
        uiCommandBuilder.set("#DescriptionField.Value", server.description ?: "")
        uiCommandBuilder.set("#HostField.Value", server.host)
        uiCommandBuilder.set("#PortField.Value", server.port.toString())
        uiCommandBuilder.set("#IconField.Value", server.uiIcon ?: "")

        // Set ColorPicker value
        if (server.uiColorTint != null) {
            uiCommandBuilder.set("#ColorTintPicker.Value", server.uiColorTint!!)
        }

        // Inject ServerRow preview and populate it
        uiCommandBuilder.append("#PreviewContainer", "Pages/EzLobby/ServerRow.ui")
        updatePreview(uiCommandBuilder, server)

        // Bind ValueChanged events for live preview (with false parameter to avoid full page reload)
        uiEventBuilder.addEventBinding(
            CustomUIEventBindingType.ValueChanged, "#NameField",
            EventData.of(AdminServerEditEvent.KEY_NAME, "#NameField.Value"), false
        )
        uiEventBuilder.addEventBinding(
            CustomUIEventBindingType.ValueChanged, "#DisplayNameField",
            EventData.of(AdminServerEditEvent.KEY_DISPLAY_NAME, "#DisplayNameField.Value"), false
        )
        uiEventBuilder.addEventBinding(
            CustomUIEventBindingType.ValueChanged, "#DescriptionField",
            EventData.of(AdminServerEditEvent.KEY_DESCRIPTION, "#DescriptionField.Value"), false
        )
        uiEventBuilder.addEventBinding(
            CustomUIEventBindingType.ValueChanged, "#IconField",
            EventData.of(AdminServerEditEvent.KEY_ICON, "#IconField.Value"), false
        )
        uiEventBuilder.addEventBinding(
            CustomUIEventBindingType.ValueChanged, "#ColorTintPicker",
            EventData.of(AdminServerEditEvent.KEY_COLOR_TINT, "#ColorTintPicker.Value"), false
        )

        // Bind Save button - capture all field values when clicked
        uiEventBuilder.addEventBinding(
            CustomUIEventBindingType.Activating, "#SaveButton",
            EventData.of(AdminServerEditEvent.KEY_ACTION, AdminServerEditEvent.ACTION_SAVE)
                .append(AdminServerEditEvent.KEY_NAME, "#NameField.Value")
                .append(AdminServerEditEvent.KEY_DISPLAY_NAME, "#DisplayNameField.Value")
                .append(AdminServerEditEvent.KEY_DESCRIPTION, "#DescriptionField.Value")
                .append(AdminServerEditEvent.KEY_HOST, "#HostField.Value")
                .append(AdminServerEditEvent.KEY_PORT, "#PortField.Value")
                .append(AdminServerEditEvent.KEY_ICON, "#IconField.Value")
                .append(AdminServerEditEvent.KEY_COLOR_TINT, "#ColorTintPicker.Value")
        )

        uiEventBuilder.addEventBinding(
            CustomUIEventBindingType.Activating, "#CancelButton",
            EventData.of(AdminServerEditEvent.KEY_ACTION, AdminServerEditEvent.ACTION_CANCEL)
        )
    }

    private fun updatePreview(uiCommandBuilder: UICommandBuilder, server: Server) {
        // Use utility to populate the preview
        ServerRowUtils.populateServerRow(uiCommandBuilder, "#PreviewContainer[0]", server)
    }


    override fun handleDataEvent(
        ref: Ref<EntityStore>,
        store: Store<EntityStore>,
        data: AdminServerEditEvent
    ) {
        when (data.action) {
            AdminServerEditEvent.ACTION_SAVE -> {
                saveServerChanges(data)
                close()
            }
            AdminServerEditEvent.ACTION_CANCEL -> {
                close()
            }
            else -> {
                // Handle preview updates
                var previewNeedsUpdate = false

                data.name?.let {
                    currentName = it
                    previewNeedsUpdate = true
                }
                data.displayName?.let {
                    currentDisplayName = it.ifBlank { null }
                    previewNeedsUpdate = true
                }
                data.description?.let {
                    currentDescription = it.ifBlank { null }
                    previewNeedsUpdate = true
                }
                data.icon?.let {
                    currentIcon = it.ifBlank { null }
                    previewNeedsUpdate = true
                }
                data.colorTint?.let {
                    currentColorTint = it.ifBlank { null }
                    previewNeedsUpdate = true
                }

                if (previewNeedsUpdate && server != null) {
                    // Create a temporary server instance with current values for preview
                    val previewServer = Server(
                        id = server.id,
                        name = currentName,
                        displayName = currentDisplayName,
                        description = currentDescription,
                        host = currentHost,
                        port = currentPort,
                        uiIcon = currentIcon,
                        uiColorTint = currentColorTint
                    )

                    // Update only the preview component using the utility
                    val commandBuilder = UICommandBuilder()
                    ServerRowUtils.populateServerRow(commandBuilder, "#PreviewContainer[0]", previewServer)
                    sendUpdate(commandBuilder, UIEventBuilder(), false)
                }
            }
        }
    }

    private fun saveServerChanges(data: AdminServerEditEvent) {
        val config = EzLobby.getServersConfig() ?: return
        val serversConfig = config.get() ?: return
        val server = serversConfig.servers.getOrNull(serverIndex) ?: return

        // Update server properties
        data.name?.takeIf { it.isNotBlank() }?.let { server.name = it }
        server.displayName = data.displayName?.takeIf { it.isNotBlank() }
        server.description = data.description?.takeIf { it.isNotBlank() }
        data.host?.takeIf { it.isNotBlank() }?.let { server.host = it }
        data.port?.toIntOrNull()?.let { server.port = it }
        server.uiIcon = data.icon?.takeIf { it.isNotBlank() }
        server.uiColorTint = data.colorTint?.takeIf { it.isNotBlank() }

        // Save configuration
        config.save()

        EzLobby.instance?.launch(playerRef.world?.dispatcher ?: EmptyCoroutineContext) {
            playerRef.sendMessage(
                Message.translation("ezlobby.messages.success.server.updated")
                    .param("name", server.name)
            )
        }
    }
}
