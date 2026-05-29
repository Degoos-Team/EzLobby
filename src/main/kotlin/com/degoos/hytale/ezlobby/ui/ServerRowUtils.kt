package com.degoos.hytale.ezlobby.ui

import com.degoos.hytale.ezlobby.EzLobby
import com.degoos.hytale.ezlobby.assets.ServerIconsStorage
import com.degoos.hytale.ezlobby.models.Server
import com.degoos.hytale.ezlobby.models.ServerStatus
import com.degoos.hytale.ezlobby.utils.ColorUtils
import com.degoos.kayle.extension.parseTags
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder

object ServerRowUtils {

    /**
     * Populates a ServerRow.ui component with server data, including color tinting for button states
     */
    fun populateServerRow(
        uiCommandBuilder: UICommandBuilder,
        selector: String,
        server: Server,
        status: ServerStatus = ServerStatus.UNKNOWN
    ) {
        uiCommandBuilder.set("$selector #Name.TextSpans", Message.raw(server.displayName ?: server.name).parseTags())
        uiCommandBuilder.set(
            "$selector #Description.TextSpans",
            Message.raw(server.description ?: "No description").parseTags()
        )

        // Set icon using extracted method
        setServerIcon(uiCommandBuilder, selector, server)

        // Apply color tint to icon background and button states
        // Skip Default/Hovered tint when OFFLINE — those states are overridden below with grey
        if (server.uiColorTint != null) {
            uiCommandBuilder.set("$selector #IconGroup.Background.Color", server.uiColorTint!!)
            if (status != ServerStatus.OFFLINE) {
                applyColorTintToButton(uiCommandBuilder, selector, server.uiColorTint!!)
            } else {
                // Only apply Pressed tint; Default/Hovered handled by OFFLINE block below
                val stateColors = ColorUtils.generateButtonStateColors(server.uiColorTint!!)
                uiCommandBuilder.set("$selector.Style.Pressed.Background.Color", stateColors["Pressed"]!!)
            }
        }

        // Status circle color — path format: space before # (child element selector)
        val circleColor = when (status) {
            ServerStatus.ONLINE   -> "#00CC44"
            ServerStatus.OFFLINE  -> "#CC2200"
            ServerStatus.UNKNOWN,
            ServerStatus.CHECKING -> "#FFCC00"
        }
        uiCommandBuilder.set("$selector #StatusCircle.Background.Color", circleColor)

        // OFFLINE visual disable — override Default and Hovered button backgrounds (D-07)
        // Color override keeps rows clickable at framework level; guard runs in handleDataEvent
        if (status == ServerStatus.OFFLINE) {
            uiCommandBuilder.set("$selector.Style.Default.Background.Color", "#66666666")
            uiCommandBuilder.set("$selector.Style.Hovered.Background.Color", "#66666666")
        }
    }

    /**
     * Populates an AdminServerRow.ui component with server data
     */
    fun populateAdminServerRow(uiCommandBuilder: UICommandBuilder, selector: String, server: Server) {
        uiCommandBuilder.set("$selector #Name.TextSpans", Message.raw(server.displayName ?: server.name).parseTags())
        if (server.description != null) {
            uiCommandBuilder.set("$selector #Description.TextSpans", Message.raw(server.description!!).parseTags())
        }
        uiCommandBuilder.set("$selector #Host.Text", "${server.host}:${server.port}")
        uiCommandBuilder.set("$selector #Id.Text", server.id.toString())

        // Set icon using extracted method
        setServerIcon(uiCommandBuilder, selector, server)

        // Apply color tint to icon background
        if (server.uiColorTint != null) {
            uiCommandBuilder.set("$selector #IconGroup.Background.Color", server.uiColorTint!!)

            applyColorTintToButton(uiCommandBuilder, selector, server.uiColorTint!!)
        }
    }

    /**
     * Sets the server icon (ItemIcon or AssetImage) and toggles visibility
     */
    private fun setServerIcon(uiCommandBuilder: UICommandBuilder, selector: String, server: Server) {
        val icon = ServerIconsStorage.findIconForServer(server.id)

        if (icon == null && server.uiIcon == null) {
            // Use fallback icon
            uiCommandBuilder.set("$selector #Icon.ItemId", EzLobby.getServersConfig()?.get()?.fallbackIcon ?: "Unknown")
            uiCommandBuilder.set("$selector #Icon.Visible", true)
            uiCommandBuilder.set("$selector #Image.Visible", false)
        } else if (server.uiIcon != null) {
            // Use ItemIcon
            uiCommandBuilder.set("$selector #Icon.ItemId", server.uiIcon!!)
            uiCommandBuilder.set("$selector #Icon.Visible", true)
            uiCommandBuilder.set("$selector #Image.Visible", false)
        } else {
            // Use AssetImage
            uiCommandBuilder.set("$selector #Image.AssetPath", icon!!.name)
            uiCommandBuilder.set("$selector #Icon.Visible", false)
            uiCommandBuilder.set("$selector #Image.Visible", true)
        }
    }

    /**
     * Applies color tint to button background states (Default, Hovered, Pressed)
     */
    private fun applyColorTintToButton(uiCommandBuilder: UICommandBuilder, selector: String, colorTint: String) {
        val stateColors = ColorUtils.generateButtonStateColors(colorTint)
        stateColors.forEach { (state, color) ->
            uiCommandBuilder.set(
                "$selector.Style.$state.Background.Color",
                color
            )
        }
    }
}
