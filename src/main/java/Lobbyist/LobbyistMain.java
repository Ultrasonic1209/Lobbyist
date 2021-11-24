package Lobbyist;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.slf4j.Logger;

import java.util.NoSuchElementException;

@Plugin(id = "lobbyist", name = "Lobbyist", version = "1.0.0",
        url = "https://github.com/Ultrasonic1209/Lobbyist", description = "An easy-to-use lobby plugin.", authors = {"Ultrasonic#7662"})
public class LobbyistMain {

    private final ProxyServer server;
    private final Logger logger;
    private final CommandManager commandManager;
    @Inject
    public LobbyistMain(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
        this.commandManager = server.getCommandManager();

    }

    public static void main(String[] args) {
        System.err.println("This plugin is intended to be run as a Velocity plugin.");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        RegisteredServer lobby;

        try {
            lobby = this.server.getServer("lobby").get();
        } catch (NoSuchElementException exc) {
            this.logger.error("No \"lobby\" server was found. Lobbyist will not initialise.");
            return;
        }

        CommandMeta meta = this.commandManager.metaBuilder("lobby")
                // Specify other aliases (optional)
                .aliases("l", "hub")
                .build();

        this.commandManager.register(meta, new Lobby(server, lobby));
    }
}