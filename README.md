# FortX-reports

## Description

FortX-reports is an advanced player reporting management plugin for Minecraft servers. This plugin enables efficient communication between players and administrators, as well as among admin team members, even when they are on different servers within your network.

## Features

- **Player Report System** - allows players to report rule violations by other players
- **Multi-server Support** - synchronizes reports across all servers in your network
- **Admin Chat** - private communication channel for administrators across all servers
- **Highly Configurable** - easily customize message appearance and plugin behavior

## Commands

| Command | Permission | Description |
|---------|------------|-------------|
| `/report <player> <reason>` | `report.use` | Reports a player to administration with specified reason |
| `/adminchat <message>` | `adminchat.use` | Sends a message to all administrators |
| `/ac <message>` | `adminchat.use` | Shortcut for the adminchat command |

## Permissions

| Permission | Description |
|------------|-------------|
| `report.use` | Allows the use of the `/report` command |
| `report.admin` | Allows seeing player reports |
| `report.bypass` | Protects a player from being reported |
| `adminchat.use` | Allows sending messages in admin chat |
| `adminchat.see` | Allows seeing messages in admin chat |

## Configuration

### Basic Configuration

The `config.yml` file contains basic plugin settings:

```yaml
# Debug mode (disabled by default)
debug: false

# Multi-server configuration
multiserver:
  enabled: false # Set to true to enable multi-server mode

# Server name configuration (used in reports and admin chat)
server:
  name: "lobby" # The name of this server

# Redis configuration (required only when multiserver is enabled)
redis:
  host: "localhost"
  port: 2137
  password: "your_redis_password"
  channels:
    adminchat: "adminchat"
    report: "report"
    helpop: "helpop"
```

### Message Configuration

The `messages.yml` file allows you to customize all messages:

```yaml
report:
  self: "&cYou cannot report yourself!"
  offline: "&cYou cannot report an offline player!"
  bypass: "&cYou cannot report a player with a bypass permission!"
  message: "&f{Player} &areported &f{Target} &afor &c{Reason}!"
  reason: "&cYou must provide a reason for the report!"

adminchat:
  format: "&c[ADMIN] &f{Player}: &7{Message}"
```

## Multi-server Setup

To use the multi-server features, you need to:

1. Set up a Redis database
2. Enable `multiserver.enabled` in `config.yml`
3. Configure the Redis connection in the `redis` section
4. Set a unique name for each server in the `server.name` section

After proper configuration:
- Reports will be visible to administrators on all servers
- Admin chat will work across all servers

## Support

If you have any problems or questions:
- Documentation: https://reports.fortx.top/
- Discord: https://discord.gg/fortx