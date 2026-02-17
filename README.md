# EzLobby

A Hytale server plugin for managing lobby servers. EzLobby provides a graphical interface for players to browse and connect to game servers, along with administrative tools for managing the server list and spawn settings.

## Main Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/servers` | Opens the server browser or connects to a specific server | `ezlobby.servers` |
| `/ezlobby` | Main admin command for managing the plugin | `ezlobby.admin` |
| `/eztitle` | Display titles and subtitles to players | `ezlobby.title` |

## /servers Command

Opens a graphical interface showing all configured servers. Players can click any server to connect.

### Usage
```
/servers
/servers <selector>
```

### Arguments
- `selector` (optional) - Server index, UUID, or name to connect directly

### Permission
- `ezlobby.servers` - Required to use this command

---

## /ezlobby Command

Administrative command collection for managing the lobby plugin.

### Subcommands

| Subcommand | Description | Permission |
|------------|-------------|------------|
| `/ezlobby server` | Manage servers in the lobby | `ezlobby.server.manage` |
| `/ezlobby spawn` | Manage spawn point settings | `ezlobby.spawn.manage` |

---

## /ezlobby server

Manages the server list configuration.

### Subcommands

| Subcommand | Description | Permission |
|------------|-------------|------------|
| `/ezlobby server add` | Add a new server to the list | `ezlobby.server.manage` |
| `/ezlobby server remove` | Remove a server from the list | `ezlobby.server.manage` |
| `/ezlobby server list` | Open the admin server management interface | `ezlobby.server.manage` |
| `/ezlobby server tp` | Teleport a player to a server | `ezlobby.server.manage` |
| `/ezlobby server reload` | Reload server configuration from disk | `ezlobby.server.manage` |

### /ezlobby server add

Adds a new server to the lobby list.

**Usage:**
```
/ezlobby server add <name> <host> <port>
```

**Optional Parameters:**
- `--displayName <text>` - Name shown to players (supports color codes)
- `--description <text>` - Server description
- `--uiIcon <item>` - Icon item ID (e.g. `Soil_Grass`)
- `--uiColorTint <color>` - Hex color for the icon background (e.g. `#4CAF50`)

**Example:**
```
/ezlobby server add survival survival.example.com 25565 --displayName "Survival World" --description "Classic survival gameplay" --uiIcon Soil_Grass --uiColorTint #4CAF50
```

### /ezlobby server remove

Removes a server from the lobby list.

**Usage:**
```
/ezlobby server remove <selector>
```

**Arguments:**
- `selector` - Server index, UUID, or name

**Example:**
```
/ezlobby server remove survival
```

### /ezlobby server list

Opens an admin interface showing all servers with detailed information. Allows editing servers with a live preview.

**Usage:**
```
/ezlobby server list
```

### /ezlobby server tp

Teleports a player to a specific server.

**Usage:**
```
/ezlobby server tp <selector>
```

**Optional Parameters:**
- `--player <name>` - Player to teleport (defaults to yourself)

**Example:**
```
/ezlobby server tp survival --player Steve
```

### /ezlobby server reload

Reloads the server configuration from disk.

**Usage:**
```
/ezlobby server reload
```

---

## /ezlobby spawn

Manages spawn point configuration.

### Subcommands

| Subcommand | Description | Permission |
|------------|-------------|------------|
| `/ezlobby spawn set` | Set spawn point to current location | `ezlobby.spawn.manage` |
| `/ezlobby spawn protect` | Toggle spawn protection | `ezlobby.spawn.manage` |

### /ezlobby spawn set

Sets the server spawn point to your current location.

**Usage:**
```
/ezlobby spawn set
```

### /ezlobby spawn protect

Enables or disables spawn protection for the spawn world.

**Usage:**
```
/ezlobby spawn protect
```

---

## /eztitle Command

Displays a title and subtitle to players.

### Usage
```
/eztitle <title>
```

### Optional Parameters
- `--subtitle <text>` - Text displayed below the title
- `--world <name>` - Specific world to show the title in
- `--player <name>` - Specific player to show the title to
- `--broadcast <true/false>` - Whether to show to all players (default: false)

### Example
```
/eztitle "Welcome!" --subtitle "Enjoy your stay" --broadcast true
```

---

## Configuration

Server configuration is stored in JSON format at `plugins/EzLobby/servers.json`.

### Server Entry Structure
```json
{
  "Id": "uuid-here",
  "Name": "survival",
  "Address": "survival.example.com",
  "Port": 25565,
  "DisplayName": "Survival World",
  "Description": "Classic survival gameplay",
  "UIIcon": "Soil_Grass",
  "UIColorTint": "#4CAF50"
}
```

After manually editing the configuration file, run `/ezlobby server reload` to apply changes.

---

## Permissions Summary

| Permission | Description |
|------------|-------------|
| `ezlobby.servers` | Access to the `/servers` command |
| `ezlobby.admin` | Access to the `/ezlobby` command |
| `ezlobby.server.manage` | Manage server list (add, remove, edit, reload) |
| `ezlobby.spawn.manage` | Manage spawn settings |

---

## Quick Start

1. Set your spawn point:
   ```
   /ezlobby spawn set
   ```

2. Add a server:
   ```
   /ezlobby server add survival survival.example.com 25565 --displayName "Survival" --uiIcon Soil_Grass
   ```

3. Open the server list:
   ```
   /servers
   ```

---

## Authors

- Gael Rial - https://grialc.com
- Antonio Terrero - https://atalgaba.com

**Version:** 0.1.0

**Dependency:** Kayle plugin

