package com.degoos.hytale.ezlobby_linkedserver.configs

import com.degoos.hytale.ezlobby_linkedserver.models.Server
import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.ExtraInfo
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.codec.codecs.array.ArrayCodec


class LobbyServersConfig {
    var forceEzLobbyReferral: Boolean = false
    var defaultDelayInMs: Int = 5
    var lobbyServers: MutableList<Server> = mutableListOf()

    companion object {
        val SERVERS_CODEC: ArrayCodec<Server> = ArrayCodec(Server.CODEC) { size: Int -> arrayOfNulls<Server>(size) }

        val CODEC: BuilderCodec<LobbyServersConfig?> = BuilderCodec.builder(
            LobbyServersConfig::class.java
        )
        { LobbyServersConfig() }
            .append(
                KeyedCodec("ForceEzLobbyReferral", Codec.BOOLEAN),
                LobbyServersConfig::forceEzLobbyReferral.setter,
                LobbyServersConfig::forceEzLobbyReferral
            )
            .add()

            .append(
                KeyedCodec("DefaultDelayInMs", Codec.INTEGER),
                LobbyServersConfig::defaultDelayInMs.setter,
                LobbyServersConfig::defaultDelayInMs
            )
            .add()

            .append(
                KeyedCodec(
                    "LobbyServers",
                    SERVERS_CODEC
                ),
                { config: LobbyServersConfig?, value: Array<Server>, _: ExtraInfo? ->
                    config!!.lobbyServers = value.toMutableList()
                },
                { config: LobbyServersConfig?, _: ExtraInfo? ->
                    config!!.lobbyServers.toTypedArray()
                }
            ).add()

            .build()
    }
}