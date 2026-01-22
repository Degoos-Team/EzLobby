package com.degoos.hytale.ezlobby.ui

import com.degoos.hytale.ezlobby.EzLobby
import com.degoos.hytale.ezlobby.assets.ServerIconsStorage
import com.degoos.hytale.ezlobby.dsl.parseColors
import com.degoos.hytale.ezlobby.models.Server
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder

object ServerRowUtils {

    fun populateServerRow(uiCommandBuilder: UICommandBuilder, selector: String, server: Server) {
        uiCommandBuilder.set("$selector #Name.TextSpans", Message.raw(server.displayName ?: server.name).parseColors())
        uiCommandBuilder.set("$selector #Description.TextSpans", Message.raw(server.description ?: "No description").parseColors())

        val icon = ServerIconsStorage.findIconForServer(server.id)
        if (icon == null && server.uiIcon == null) {
            uiCommandBuilder.set("$selector #Icon.ItemId", EzLobby.getServersConfig()?.get()?.fallbackIcon ?: "Unknown")
            uiCommandBuilder.remove("$selector #Image")
        } else if (server.uiIcon != null) {
            uiCommandBuilder.set("$selector #Icon.ItemId", server.uiIcon!!)
            uiCommandBuilder.remove("$selector #Image")
        } else {
            uiCommandBuilder.set("$selector #Image.AssetPath", icon!!.name)
            uiCommandBuilder.remove("$selector #Icon")
        }

        if (server.uiColorTint != null) {
            uiCommandBuilder.set("$selector #IconGroup.Background.Color", server.uiColorTint!!)
        }
    }

    fun populateAdminServerRow(uiCommandBuilder: UICommandBuilder, selector: String, server: Server) {
        uiCommandBuilder.set("$selector #Name.TextSpans", Message.raw(server.displayName ?: server.name).parseColors())
        if (server.description != null) {
            uiCommandBuilder.set("$selector #Description.TextSpans", Message.raw(server.description!!).parseColors())
        }
        uiCommandBuilder.set("$selector #Host.Text", "${server.host}:${server.port}")
        uiCommandBuilder.set("$selector #Id.Text", server.id.toString())

        val icon = ServerIconsStorage.findIconForServer(server.id)
        if (icon == null && server.uiIcon == null) {
            uiCommandBuilder.set("$selector #Icon.ItemId", EzLobby.getServersConfig()?.get()?.fallbackIcon ?: "Unknown")
            uiCommandBuilder.remove("$selector #Image")
        } else if (server.uiIcon != null) {
            uiCommandBuilder.set("$selector #Icon.ItemId", server.uiIcon!!)
            uiCommandBuilder.remove("$selector #Image")
        } else {
            uiCommandBuilder.set("$selector #Image.AssetPath", icon!!.name)
            uiCommandBuilder.remove("$selector #Icon")
        }

        if (server.uiColorTint != null) {
            uiCommandBuilder.set("$selector #IconGroup.Background.Color", server.uiColorTint!!)
        }
    }
}
