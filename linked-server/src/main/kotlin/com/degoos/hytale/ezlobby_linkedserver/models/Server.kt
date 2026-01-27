package com.degoos.hytale.ezlobby_linkedserver.models

import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec


class Server(
    var host: String,
    var port: Int,
) {

    companion object {
        val CODEC: BuilderCodec<Server> = BuilderCodec.builder(Server::class.java) { Server("", 0) }
            .append(KeyedCodec("Address", Codec.STRING), Server::host.setter, Server::host).add()
            .append(KeyedCodec("Port", Codec.INTEGER), Server::port.setter, Server::port).add()
            .build()
    }
}
