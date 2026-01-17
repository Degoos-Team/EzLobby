package com.degoos.hytale.ezlobby.utils

import com.hypixel.hytale.server.core.universe.Universe

object ServerUtil {
    /*
     * Executes a task on the default world.
     */
    fun executeWorld(task: Runnable) {
        Universe.get().defaultWorld?.execute(task)
    }
}