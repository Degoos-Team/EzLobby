package com.degoos.hytale.ezlobby.assets

import com.degoos.hytale.ezlobby.EzLobby
import com.degoos.kayle.asset.MemoryCommonAsset
import com.hypixel.hytale.server.core.asset.common.CommonAsset
import com.hypixel.hytale.server.core.asset.common.CommonAssetModule
import com.hypixel.hytale.server.core.asset.common.CommonAssetRegistry
import java.io.File
import java.util.*

object ServerIconsStorage {
    const val PACK_NAME = "Degoos:EzLobby"
    const val ICONS_RELATIVE_DIRECTORY = "icons"
    private var icons = mutableMapOf<UUID, CommonAsset>()

    fun findIconForServer(serverId: UUID): CommonAsset? {
        synchronized(icons) {
            return icons[serverId]
        }
    }

    fun recreateIcons() {
        synchronized(icons) {
            val plugin = EzLobby.instance ?: return

            icons.forEach { asset ->
                CommonAssetRegistry.removeCommonAssetByName(PACK_NAME, asset.value.name)
            }

            val dataDir = plugin.dataDirectory.toFile()
            val iconsDir = File(dataDir, ICONS_RELATIVE_DIRECTORY)
            if (!iconsDir.isDirectory) {
                if (!iconsDir.mkdirs()) {
                    plugin.logger.atSevere().log("Could not create icons directory!")
                    return
                }
            }

            EzLobby.getServersConfig()?.get()?.servers?.forEach { server ->
                val iconPath = server.uiIcon ?: return@forEach
                val file = iconsDir.resolve(iconPath)
                if (!file.isFile) return@forEach

                try {
                    val bytes = file.readBytes()
                    val icon = MemoryCommonAsset("Icons/$iconPath", bytes)
                    CommonAssetModule.get().addCommonAsset(PACK_NAME, icon)
                    icons[server.id] = icon

                    plugin.logger.atInfo().log("Server icon loaded: ${file.absolutePath}")
                } catch (e: Exception) {
                    plugin.logger.atWarning().log("Could not read icon file ${file.absolutePath}")
                    e.printStackTrace()
                }
            }
        }
    }

}