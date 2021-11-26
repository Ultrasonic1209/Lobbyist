package Lobbyist;

import com.google.common.collect.ImmutableList;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.*;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Lobby implements SimpleCommand {
    RegisteredServer server;
    ProxyServer proxy;
    Logger logger;

    public Lobby(ProxyServer proxy, RegisteredServer server, Logger logger) {
        super();
        this.proxy = proxy;
        this.server = server;
        this.logger = logger;
    }

    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();
        boolean console = (source instanceof ConsoleCommandSource);

        // Get the arguments after the command alias
        String[] args = invocation.arguments();

        Player player = null;

        Optional<Player> target = Optional.empty();
        if (args.length == 1) {

            target = this.proxy.getPlayer(args[0]);
            boolean isSamePlayer = false;

            if (target.isPresent()) {
                isSamePlayer = target.get().equals(source);
            }

            if ((!invocation.source().hasPermission("lobbyist.lobby.others")) && !(isSamePlayer)) {
                source.sendMessage(Component.text("You can't do that!").color(NamedTextColor.RED));
                return;
            }
            if (target.isPresent()) {
                player = target.get();
            } else {
                source.sendMessage(Component.text("That player is not connected to the server.").color(NamedTextColor.RED));
                return;
            }
        }

        if ((console) && (target.isEmpty())) {
            source.sendMessage(Component.text("Please specify a player!").color(NamedTextColor.RED));
            return;
        } else if ((!console) && (target.isEmpty())) {
            player = (Player) source;
        }

        Optional<ServerConnection> serverConnection = player.getCurrentServer();
        AtomicBoolean alreadyHere = new AtomicBoolean(false);

        serverConnection.ifPresent(currentConnection -> {
            if (currentConnection.getServer().equals(this.server)) {
                alreadyHere.set(true);
            }
        });

        if (alreadyHere.get()) {
            if ((console) || ((args.length == 1)) && (!player.equals(source))) {
                source.sendMessage(Component.text("That player is already in the lobby!").color(NamedTextColor.RED));
            } else {
                source.sendMessage(Component.text("You're already in the lobby!").color(NamedTextColor.RED));
            }
            return;
        } else if ((console) || (args.length == 1)) {
            source.sendMessage(Component.text("Sending " + player.getUsername() + " to the lobby.").color(NamedTextColor.GREEN));
        }


        //source.sendMessage(Component.text("Hello!").color(NamedTextColor.AQUA));

        try {
            ConnectionRequestBuilder connector = (player).createConnectionRequest(this.server);
            connector.connect().thenAccept(result -> {
                if (!result.isSuccessful()) {
                    source.sendMessage(Component.text("Unable to connect to the lobby.\n" + result.getReasonComponent()).color(NamedTextColor.RED));
                }
            });
        } catch (Exception exception) {
            this.logger.error("Lobbyist has encountered an error:", exception);
            source.sendMessage(Component.text("An internal error occurred while connecting to the lobby.").color(NamedTextColor.RED));
        }
    }

    @Override
    public List<String> suggest(final Invocation invocation) {

        if (!invocation.source().hasPermission("lobbyist.lobby.others")) {
            return ImmutableList.of();
        }

        List<Player> players = (List<Player>) this.proxy.getAllPlayers();
        ArrayList<String> plrNames = new ArrayList<>();

        players.forEach(player -> plrNames.add(player.getUsername()));

        return plrNames;
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {

        return invocation.source().hasPermission("lobbyist.lobby.others") || invocation.source().hasPermission("lobbyist.lobby");
    }
}