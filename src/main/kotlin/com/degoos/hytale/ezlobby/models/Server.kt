package com.degoos.hytale.ezlobby.models

import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec


class Server(
    var name: String,
    var host: String,
    var port: Int,
    var uiIcon: String? = null,
    var uiColorTint: String? = null,
    var uiBackground: String? = null
) {

    companion object {
        val CODEC: BuilderCodec<Server> = BuilderCodec.builder(Server::class.java) { Server("", "", 0) }
            .append(KeyedCodec("Name", Codec.STRING), Server::name.setter, Server::name).add()
            .append(KeyedCodec("Address", Codec.STRING), Server::host.setter, Server::host).add()
            .append(KeyedCodec("Port", Codec.INTEGER), Server::port.setter, Server::port).add()
            .append(KeyedCodec("UIIcon", Codec.STRING), Server::uiIcon.setter, Server::uiIcon).add()
            .append(KeyedCodec("UIColorTint", Codec.STRING), Server::uiColorTint.setter, Server::uiColorTint).add()
            .append(KeyedCodec("UIBackground", Codec.STRING), Server::uiBackground.setter, Server::uiBackground).add()
            .build()
    }
}
