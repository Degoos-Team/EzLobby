package com.degoos.hytale.ezlobby.managers

import com.degoos.kayle.extension.world
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.World
import java.util.UUID

class VisibilityManager {
    val hideOthersOptIn = mutableSetOf<PlayerRef>()

    fun isHidingOthers(playerRef: PlayerRef): Boolean {
        return hideOthersOptIn.contains(playerRef)
    }

    fun addPlayer(playerRef: PlayerRef) {
        hideOthersOptIn.add(playerRef)
    }

    fun removePlayer(playerRef: PlayerRef) {
        hideOthersOptIn.remove(playerRef)
    }

    fun hideOthers(playerRef: PlayerRef) {
        addPlayer(playerRef)
        hidePlayers(playerRef)
    }

    fun showOthers(playerRef: PlayerRef) {
        removePlayer(playerRef)
        showPlayers(playerRef)
    }

    fun toggleVisibility(playerRef: PlayerRef) {
        if (isHidingOthers(playerRef)) {
            showOthers(playerRef)
        } else {
            hideOthers(playerRef)
        }
    }

    fun hidePlayer(playerToHide: UUID) {
        hideOthersOptIn.forEach {
            it.hiddenPlayersManager.hidePlayer(playerToHide)
        }
    }

    fun hidePlayer(playerRef: PlayerRef, playerToHide: UUID) {
        playerRef.hiddenPlayersManager.hidePlayer(playerToHide)
    }

    fun hidePlayers(playerRef: PlayerRef) {
        playerRef.world?.playerRefs?.forEach {
            if(it.uuid != playerRef.uuid) playerRef.hiddenPlayersManager.hidePlayer(it.uuid)
        }
    }

    fun hidePlayers(playerRef: PlayerRef, world: World) {
        world.playerRefs.forEach {
            if(it.uuid != playerRef.uuid) playerRef.hiddenPlayersManager.showPlayer(it.uuid)
        }
    }

    fun showPlayer(playerToShow: UUID) {
        hideOthersOptIn.forEach {
            it.hiddenPlayersManager.showPlayer(playerToShow)
        }
    }

    fun showPlayer(playerRef: PlayerRef, playerToShow: UUID) {
        playerRef.hiddenPlayersManager.showPlayer(playerToShow)
    }

    fun showPlayers(playerRef: PlayerRef) {
        playerRef.world?.playerRefs?.forEach {
            if(it.uuid != playerRef.uuid) playerRef.hiddenPlayersManager.showPlayer(it.uuid)
        }
    }

    fun showPlayers(playerRef: PlayerRef, world: World) {
        world.playerRefs.forEach {
            if(it.uuid != playerRef.uuid) playerRef.hiddenPlayersManager.showPlayer(it.uuid)
        }
    }
}