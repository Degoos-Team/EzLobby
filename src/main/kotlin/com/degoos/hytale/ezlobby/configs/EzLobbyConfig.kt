package com.degoos.hytale.ezlobby.configs

import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.ExtraInfo
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.math.vector.Vector3d
import com.hypixel.hytale.math.vector.Vector3f
import com.hypixel.hytale.server.core.inventory.ItemStack


class EzLobbyConfig {
    var spawnPointWorldName: String? = null
    var spawnPointPosition: Vector3d? = null
    var spawnPointBodyRotation: Vector3f? = null
    var spawnPointHeadRotation: Vector3f? = null

    var serverMenuItemOnJoin: Boolean = false
    var serversMenuItemStack: ItemStack? = ItemStack("Degoos_Compass", 1)

    companion object {
        val CODEC: BuilderCodec<EzLobbyConfig> = BuilderCodec.builder(
            EzLobbyConfig::class.java
        )
        { EzLobbyConfig() }
            .append(
                KeyedCodec(
                    "SpawnPointWorldName",
                    Codec.STRING
                ),
                { config: EzLobbyConfig, value: String?, info: ExtraInfo? ->
                    config.spawnPointWorldName = value
                },
                { config: EzLobbyConfig, info: ExtraInfo? ->
                    config.spawnPointWorldName
                }
            )
            .add()

            .append(
                KeyedCodec(
                    "SpawnPointPosition",
                    Codec.DOUBLE_ARRAY
                ),
                { config: EzLobbyConfig, value: DoubleArray?, info: ExtraInfo? ->
                    config.spawnPointPosition = if (value != null) Vector3d(
                        value[0],
                        value[1],
                        value[2]
                    ) else null
                },
                { config: EzLobbyConfig, info: ExtraInfo? ->
                    config.spawnPointPosition?.let {
                        doubleArrayOf(
                            it.x,
                            it.y,
                            it.z
                        )
                    }
                }
            )
            .add()

            .append(
                KeyedCodec(
                    "SpawnPointBodyRotation",
                    Codec.DOUBLE_ARRAY
                ),
                { config: EzLobbyConfig, value: DoubleArray?, info: ExtraInfo? ->
                    config.spawnPointBodyRotation = if (value != null) Vector3f(
                        value[0].toFloat(),
                        value[1].toFloat(),
                        value[2].toFloat()
                    ) else null
                },
                { config: EzLobbyConfig, info: ExtraInfo? ->
                    config.spawnPointBodyRotation?.let {
                        doubleArrayOf(
                            it.x.toDouble(),
                            it.y.toDouble(),
                            it.z.toDouble()
                        )
                    }
                }
            )
            .add()

            .append(
                KeyedCodec(
                    "SpawnPointHeadRotation",
                    Codec.DOUBLE_ARRAY
                ),
                { config: EzLobbyConfig, value: DoubleArray?, info: ExtraInfo? ->
                    config.spawnPointHeadRotation = if (value != null) Vector3f(
                        value[0].toFloat(),
                        value[1].toFloat(),
                        value[2].toFloat()
                    ) else null
                },
                { config: EzLobbyConfig, info: ExtraInfo? ->
                    config.spawnPointHeadRotation?.let {
                        doubleArrayOf(
                            it.x.toDouble(),
                            it.y.toDouble(),
                            it.z.toDouble()
                        )
                    }
                }
            )
            .add()

            .append(
                KeyedCodec("ServerMenuItemOnJoin", Codec.BOOLEAN),
                EzLobbyConfig::serverMenuItemOnJoin.setter,
                EzLobbyConfig::serverMenuItemOnJoin
            )
            .add()

            .build()
    }
}