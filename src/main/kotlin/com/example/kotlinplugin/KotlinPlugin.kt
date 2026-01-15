package com.example.kotlinplugin

import com.hypixel.hytale.server.core.plugin.JavaPlugin
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import java.util.concurrent.CompletableFuture

class KotlinPlugin(init: JavaPluginInit) : JavaPlugin(init) {

    override fun setup() {
        logger.atInfo().log("Setup")
        CompletableFuture.runAsync {  }
    }

    override fun start() {
        logger.atInfo().log("Start")
    }

    override fun shutdown() {
        logger.atInfo().log("Shutdown")
    }
}