package com.degoos.hytale.ezlobby_linkedserver.mixins;

import com.degoos.hytale.ezlobby_linkedserver.EzLobbyLinkedServer;
import com.degoos.hytale.ezlobby_linkedserver.configs.LobbyServersConfig;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.util.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;
import java.util.UUID;

@Mixin(Universe.class)
public class RedirectOnShutdownMixin {

    @Unique
    public boolean shouldRedirectOnShutdown() {
        Config<LobbyServersConfig> lobbyServersConfig = EzLobbyLinkedServer.Companion.getLobbyServersConfig();
        if(lobbyServersConfig != null && lobbyServersConfig.get() != null) {
            return lobbyServersConfig.get().getRedirectToLobbyOnShutdown();
        }
        return false;
    }

    @Shadow
    private Map<UUID, PlayerRef> players;

    @Shadow
    public void disconnectAllPlayers() {}

    @Shadow
    public void shutdownAllWorlds() {}

    @Overwrite
    public void shutdown() {
        if(shouldRedirectOnShutdown()) {
            this.players.forEach ((_, player) -> {
                EzLobbyLinkedServer.Companion.redirectPlayerToLobby(player);
            });
        } else {
            this.disconnectAllPlayers();
        }
        this.shutdownAllWorlds();
    }
}
