package com.degoos.hytale.ezlobby

import com.degoos.hytale.ezlobby.commands.NotifyCommand
import com.degoos.hytale.ezlobby.commands.ezlobby.EzLobbyCommand
import com.degoos.hytale.ezlobby.systems.BreakEventSystem
import com.degoos.hytale.ezlobby.systems.DamageEventSystem
import com.degoos.hytale.ezlobby.systems.PickupEventSystem
import com.degoos.hytale.ezlobby.systems.PlaceEventSystem
import com.degoos.hytale.ezlobby.systems.UseEventSystem
import com.degoos.kayle.KotlinPlugin
import com.hypixel.hytale.server.core.plugin.JavaPluginInit

@Suppress("unused")
class EzLobby(init: JavaPluginInit) : KotlinPlugin(init) {

    companion object {
    }

    override fun setup() {
        logger.atInfo().log("[Degoos:EzLobby] Plugin has been loaded")
    }

    override fun start() {
        entityStoreRegistry.registerSystem(BreakEventSystem())
        entityStoreRegistry.registerSystem(PlaceEventSystem())
        entityStoreRegistry.registerSystem(DamageEventSystem())
        entityStoreRegistry.registerSystem(UseEventSystem())
        entityStoreRegistry.registerSystem(PickupEventSystem())

        commandRegistry.registerCommand(EzLobbyCommand())
        logger.atConfig().log("[Degoos:EzLobby] EzLobby Command Registered")
        commandRegistry.registerCommand(NotifyCommand())
        logger.atConfig().log("[Degoos:EzLobby] EzNotify Command Registered")


    }

    override fun shutdown() {
        // logger.atInfo().log("Shutdown")
    }
}