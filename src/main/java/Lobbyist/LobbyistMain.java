package Lobbyist;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.NoSuchElementException;

@Plugin(id = "lobbyist", name = "Lobbyist", version = "1.1.0",
        url = "https://github.com/Ultrasonic1209/Lobbyist", description = "An easy-to-use lobby plugin.", authors = {"Ultrasonic#7662"})
public class LobbyistMain {

    private final ProxyServer server;
    private final Logger logger;
    private final CommandManager commandManager;
    private final Path dataDirectory;

    private @Nullable String configuredServer;
    private boolean registerL = true;
    private boolean registerHub = true;

    @Inject
    public LobbyistMain(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.commandManager = server.getCommandManager();

    }

    public static void main(String[] args) {
        System.err.println("This plugin is intended to be run as a Velocity plugin.");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .path(this.dataDirectory.resolve("lobbyist.conf")) // Set where we will load and save to
                .build();

        CommentedConfigurationNode root;
        try {
            root = loader.load();
        } catch (IOException e) {
            this.logger.error("An error occurred while loading lobbyist.conf: " + e.getMessage());
            if (e.getCause() != null) {
                e.getCause().printStackTrace();
            }
            return;
        }

        this.configuredServer = root.node("server-name").getString();

        if (this.configuredServer == null) {
            this.logger.warn("Invalid lobbyist.conf! Repairing...");
            try {
                root.node("server-name").set("lobby");
                loader.save(root);
            } catch (final ConfigurateException e) {
                this.logger.error("Unable to recreate lobbyist.conf: " + e.getMessage());
                return;
            }
        }

        RegisteredServer lobby;

        try {
            lobby = this.server.getServer(this.configuredServer).get();
        } catch (NoSuchElementException exc) {
            this.logger.error("Configured server (" + this.configuredServer + ") was not found. Lobbyist will not initialise.");
            return;
        }

        CommandMeta.Builder proto = this.commandManager.metaBuilder("lobby");

        if (this.registerHub) {
            proto.aliases("hub");
        }
        if (this.registerL) {
            proto.aliases("l");
        }

        CommandMeta meta = proto.build();

        this.commandManager.register(meta, new Lobby(server, lobby));
    }
}