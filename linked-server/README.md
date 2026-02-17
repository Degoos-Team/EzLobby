# EzLobby LinkedServer

A Hytale server plugin that allows players to return to the lobby from linked game servers. This plugin works alongside the main EzLobby plugin to create a seamless multi-server network experience.

## What It Does

EzLobby LinkedServer is installed on your game servers (survival, creative, minigames, etc.) to provide a quick way for players to return to the main lobby server. It includes a countdown system with movement cancellation to prevent accidental teleports during combat or important actions.

## Main Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/lobby` | Returns the player to the lobby after a configurable countdown | `ezlobby.linkedserver.use` |
| `/lobbyservers` | Opens the admin interface to manage lobby server connections | `ezlobby.linkedserver.admin` |

---

## /lobby Command

Teleports the player back to a random lobby server after a configurable countdown. The teleport will be cancelled if the player moves during the countdown.

### Usage
```
/lobby
```

### How It Works
1. Player executes `/lobby`
2. A countdown begins (configurable in config, default 5 seconds)
3. Player must stand still during countdown
4. If player moves more than 2 blocks, teleport is cancelled
5. After countdown completes, player is sent to a random lobby server

### Permission
- `ezlobby.linkedserver.use` - Required to use this command

### Configuration
The countdown delay can be configured in `plugins/EzLobby-LinkedServer/config.json`:
```json
{
  "DefaultDelayInMs": 5
}
```

---

## /lobbyservers Command

Administrative interface for managing lobby server connections. Opens a GUI where admins can add, edit, and remove lobby servers that players will be sent to.

### Usage
```
/lobbyservers
```

### Features
- **Add Server**: Create new lobby server entries with host and port
- **Edit Server**: Modify existing server connection details
- **Delete Server**: Remove servers with confirmation prompt
- **Simple Interface**: Clean UI with only the essential fields (host and port)

### Permission
- `ezlobby.linkedserver.admin` - Required to access the admin interface

---

## Admin Interface

The `/lobbyservers` command opens a graphical interface where you can:

### Server List Page
- View all configured lobby servers
- Shows host and port for each server
- Click **Edit** to modify a server
- Click **Delete** to remove a server (with confirmation)
- Click **Add Server** to create a new entry

### Server Edit Page
- **Host**: Server address (e.g., `lobby.example.com` or `127.0.0.1`)
- **Port**: Server port (default: `25565`)
- **Save**: Apply changes and return to list
- **Cancel**: Discard changes and return to list
- **Delete**: Remove this server (shows confirmation button)
- **Confirm Delete**: Final confirmation to delete the server

---

## Configuration

### Server Configuration
Lobby servers are stored in `plugins/EzLobby-LinkedServer/lobbyservers.json`.

**Example Configuration:**
```json
{
  "DefaultDelayInMs": 5,
  "LobbyServers": [
    {
      "Address": "lobby.example.com",
      "Port": 5520
    },
    {
      "Address": "lobby2.example.com",
      "Port": 5521
    }
  ]
}
```

### Configuration Fields

| Field | Type | Description |
|-------|------|-------------|
| `DefaultDelayInMs` | Integer | Countdown time in seconds before teleport (default: 5) |
| `LobbyServers` | Array | List of lobby servers players can be sent to |
| `Address` | String | Server host address or IP |
| `Port` | Integer | Server port number |

---

## Random Server Selection

When a player uses `/lobby`, the plugin randomly selects one of the configured lobby servers. This helps distribute players across multiple lobby instances for better performance.

**Benefits:**
- Load balancing across multiple lobby servers
- Automatic failover if configured with multiple servers
- Simple round-robin distribution

---

## Countdown & Movement Detection

The countdown system includes smart movement detection:

- **Distance Threshold**: 2 blocks (squared distance > 4.0)
- **Check Frequency**: Every second during countdown
- **Position Tracking**: Compares current position with starting position
- **Cancellation**: Automatic if player moves too far

**Messages During Countdown:**
- "Going to lobby in: X seconds" (updates each second)
- "Teleport cancelled because you moved" (if movement detected)
- "Teleporting to lobby!" (when countdown completes)

---

## Permissions Summary

| Permission | Description |
|------------|-------------|
| `ezlobby.linkedserver.use` | Use the `/lobby` command to return to lobby |
| `ezlobby.linkedserver.admin` | Access admin interface to manage lobby servers |

---

## Multi-Language Support

EzLobby LinkedServer supports multiple languages. Translation files are located in:
- `Server/Languages/en-US/ezlobby.linkedserver/`
- `Server/Languages/es-ES/ezlobby.linkedserver/`

### Available Languages
- **English** (`en-US`)
- **Spanish** (`es-ES`)

### Translation Files
- `commands.lang` - Command descriptions
- `messages.lang` - System messages (countdown, errors, etc.)
- `ui.lang` - Admin interface labels and buttons

---

## Setup Guide

### 1. Installation
Place `EzLobby-LinkedServer.jar` in the `plugins/` folder of your game servers (NOT the lobby server).

### 2. Configure Lobby Servers
Use the admin interface or edit the config file:

**Option A - Admin Interface:**
```
/lobbyservers
```
Then click "Add Server" and enter your lobby server details.

**Option B - Manual Configuration:**
Edit `plugins/EzLobby-LinkedServer/lobbyservers.json`:
```json
{
  "DefaultDelayInMs": 5,
  "LobbyServers": [
    {
      "Address": "lobby.example.com",
      "Port": 25565
    }
  ]
}
```

### 3. Set Countdown (Optional)
Adjust the `DefaultDelayInMs` value to change how long players must wait before teleporting.

### 4. Test
Join your game server and run:
```
/lobby
```

---

## Network Architecture

```
┌─────────────┐
│ Lobby Server│ ← Main EzLobby plugin
│   (Hub)     │
└─────────────┘
       ↑
       │ /lobby command
       │
   ┌───┴────┬────────┬────────┐
   │        │        │        │
┌──┴──┐  ┌──┴──┐  ┌──┴──┐  ┌──┴──┐
│Game │  │Game │  │Game │  │Game │
│ 1   │  │ 2   │  │ 3   │  │ 4   │
└─────┘  └─────┘  └─────┘  └─────┘
   ↑ EzLobby LinkedServer plugin
```

**Setup:**
1. Install **EzLobby** on the main lobby/hub server
2. Install **EzLobby LinkedServer** on all game servers
3. Configure game servers to point back to lobby
4. Players can browse servers from lobby using `/servers`
5. Players can return to lobby from game servers using `/lobby`

---

## Quick Start

1. **On Game Server**, add your lobby server:
   ```
   /lobbyservers
   ```
   Click "Add Server", enter:
   - Host: `lobby.example.com`
   - Port: `5520`
   - Click "Save"

2. **Test the connection:**
   ```
   /lobby
   ```

3. **Adjust countdown** (if needed):
   Edit `DefaultDelayInMs` in config file and reload

---

## Troubleshooting

### Player gets "There is no servers file" error
- Make sure at least one lobby server is configured
- Check that `lobbyservers.json` exists in the plugin folder
- Use `/lobbyservers` to add a server via the admin interface

### Countdown doesn't work
- Check `DefaultDelayInMs` value in config
- Ensure it's a positive number (in seconds)
- Value of 0 means instant teleport (no countdown)

### Player can't return to lobby
- Verify lobby server address and port are correct
- Check that lobby server is online and accepting connections
- Ensure player has `ezlobby.linkedserver.use` permission

### Movement detection too sensitive
- The threshold is 2 blocks (4.0 squared distance)
- This is intentional to prevent teleporting during combat
- Cannot be configured currently (hardcoded for safety)

---

## Authors

- Gael Rial - https://grialc.com
- Antonio Terrero - https://atalgaba.com

**Version:** 0.1.0

**Dependencies:**
- Kayle plugin
- EzLobby (on lobby server only, not required on game servers)

---

## Related

- **Main Plugin**: [EzLobby](../README.md) - Install on lobby server for server browsing
- **Repository**: [EzLobby Project](../)
