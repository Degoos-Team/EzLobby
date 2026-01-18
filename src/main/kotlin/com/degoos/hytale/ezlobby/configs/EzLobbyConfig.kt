package com.degoos.hytale.ezlobby.configs

import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.ExtraInfo
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.math.vector.Vector3d
import com.hypixel.hytale.math.vector.Vector3f


class EzLobbyConfig {
    var spawnPointWorldName: String? = null
    var spawnPointPosition: Vector3d? = null
    var spawnPointRotation: Vector3f? = null

    companion object {
        val CODEC: BuilderCodec<EzLobbyConfig?> = BuilderCodec.builder(
            EzLobbyConfig::class.java
        )
        { EzLobbyConfig() }
            .append(
                KeyedCodec(
                    "SpawnPointWorldName",
                    Codec.STRING
                ),
                { config: EzLobbyConfig?, value: String?, info: ExtraInfo? ->
                    config!!.spawnPointWorldName = value
                },
                { config: EzLobbyConfig?, info: ExtraInfo? ->
                    config!!.spawnPointWorldName
                }
            )
            .add()

            .append(
                KeyedCodec(
                    "SpawnPointPosition",
                    Codec.DOUBLE_ARRAY
                ),
                { config: EzLobbyConfig?, value: DoubleArray?, info: ExtraInfo? ->
                    config!!.spawnPointPosition = if (value != null) Vector3d(
                        value[0],
                        value[1],
                        value[2]
                    ) else null
                },
                { config: EzLobbyConfig?, info: ExtraInfo? ->
                    config!!.spawnPointPosition?.let {
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
                    "SpawnPointRotation",
                    Codec.DOUBLE_ARRAY
                ),
                { config: EzLobbyConfig?, value: DoubleArray?, info: ExtraInfo? ->
                    config!!.spawnPointRotation = if (value != null) Vector3f(
                        value[0].toFloat(),
                        value[1].toFloat(),
                        value[2].toFloat()
                    ) else null
                },
                { config: EzLobbyConfig?, info: ExtraInfo? ->
                    config!!.spawnPointRotation?.let {
                        doubleArrayOf(
                            it.x.toDouble(),
                            it.y.toDouble(),
                            it.z.toDouble()
                        )
                    }
                }
            )
            .add()
            .build()
    }
}