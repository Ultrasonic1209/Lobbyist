# Lobbyist
 An intuitive and easy-to-use /lobby plugin for [Velocity](https://github.com/PaperMC/Velocity).
 
Also binds to `/l` and `/hub`.
 
**Usage:**

1. Download the plugin [here](https://github.com/Ultrasonic1209/Lobbyist/releases/latest).
2. Drag to your plugins folder.
3. Grant `lobbyist.lobby` to your players.
4. (optional) Grant `lobbyist.lobby.others` to admins if you wish them to be able to /lobby others.

**Configuration:**

Lobbyist will generate a `lobbyist.conf` file which you can find in the `Lobbyist` folder in your plugins folder.
No edits should have to be made to this file unless you have specific needs for your server.

Format:
```
server-name=lobby
register-hub=true
register-l=true
```
**server-name** (string) - The server that Lobbyist will place you in as defined in your `velocity.toml`. Generates as `lobby`.
**The config will regenerate if this value is missing.**

**register-hub** (boolean) - Determines if Lobbyist shall register `/hub` or not. Defaults to `false`, but generates as `true`.

**register-l** (boolean) - Determines if Lobbyist shall register `/l` or not. Defaults to `false`, but generates as `true`.
