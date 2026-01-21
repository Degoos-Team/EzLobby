package com.degoos.hytale.ezlobby.configs

import com.degoos.hytale.ezlobby.models.Server
import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.ExtraInfo
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.codec.codecs.array.ArrayCodec


class ServersConfig {
    var fallbackIcon: String = "Unknown"
    var servers: MutableList<Server> = mutableListOf()

    companion object {
        val SERVERS_CODEC: ArrayCodec<Server> = ArrayCodec(Server.CODEC) { size: Int -> arrayOfNulls<Server>(size) }

        val CODEC: BuilderCodec<ServersConfig?> = BuilderCodec.builder(
            ServersConfig::class.java
        )
        { ServersConfig() }
            .append(
                KeyedCodec(
                    "Servers",
                    SERVERS_CODEC
                ),
                { config: ServersConfig?, value: Array<Server>, _: ExtraInfo? ->
                    config!!.servers = value.toMutableList()
                },
                { config: ServersConfig?, _: ExtraInfo? ->
                    config!!.servers.toTypedArray()
                }
            ).add()

            .append(
                KeyedCodec("FallbackIcon", Codec.STRING),
                ServersConfig::fallbackIcon.setter,
                ServersConfig::fallbackIcon
            ).add()

            .build()
    }
}