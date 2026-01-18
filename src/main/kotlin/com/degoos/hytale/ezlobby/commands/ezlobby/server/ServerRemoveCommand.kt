package com.degoos.hytale.ezlobby.commands.ezlobby.server

import com.degoos.hytale.ezlobby.EzLobby
import com.degoos.hytale.ezlobby.models.Server
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase
import java.util.UUID

class ServerRemoveCommand : CommandBase("remove", "ezlobby.commands.ezlobby.server.add.desc", true) {
    private val indexArg: OptionalArg<Int?> = this.withOptionalArg(
        "index", "ezlobby.commands.server.add.arg.index",
        ArgTypes.INTEGER
    )

    private val idArg: OptionalArg<String?> = this.withOptionalArg(
        "id", "ezlobby.commands.server.add.arg.id",
        ArgTypes.STRING
    )

    private val nameArg: OptionalArg<String?> = this.withOptionalArg(
        "name", "ezlobby.commands.server.add.arg.name",
        ArgTypes.STRING
    )
    override fun executeSync(context: CommandContext) {
        val serversConfig = EzLobby.getServersConfig()
        val config = serversConfig?.get()


        if (config == null) {
            context.sendMessage(Message.raw("[EzLobby] Could not add a server, servers file is missing :/"))
            return
        }

        val index = context.get<Int>(this.indexArg)
        val id = UUID.fromString(context.get<String>(this.idArg))
        val name = context.get<String>(this.nameArg)

        if(index == null && id == null && name == null) {
            context.sendMessage(Message.raw("[EzLobby] No Server selector has been provided. Please use --index, --id or --name arguments"))
        }

        var serverToRemove: Server?

        var foundServers = config.servers.filter { server -> server.id == id || server.name.equals(name, true) }

        if(index != null) {
            serverToRemove = config.servers[index]
        } else {
            serverToRemove = foundServers.firstOrNull()
        }

        if(serverToRemove == null) {
            context.sendMessage(Message.raw("[EzLobby] No server found with the provided selector: " +
                    (if(index != null) "--index $index " else "") +
                    (if(id != null) "--id $id " else "") +
                    (if(name != null) "--name $name" else "")
            ))
            return
        }

        config.servers.remove(serverToRemove)
        serversConfig.save()

        context.sendMessage(Message.raw("[EzLobby] Server '${serverToRemove.name}' (${serverToRemove.id}) with address '${serverToRemove.host}:${serverToRemove.port}' was removed"))
    }
}