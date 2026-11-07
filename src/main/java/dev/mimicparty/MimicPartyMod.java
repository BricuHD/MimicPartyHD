package dev.mimicparty;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.EntityArgument;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

public class MimicPartyMod implements ModInitializer {
    private static final String PREFIX = "§d[Mimic Party] §f";
    private static final List<String> CHALLENGES = List.of(
        "§eCOPYCAT! §fChoose a player and copy their Minecraft movement for 20 seconds.",
        "§bBLOCK ART! §fBuild a tiny animal using only 12 blocks. You have 45 seconds.",
        "§aCOLOR DASH! §fStand on a block matching the color announced by the host: GREEN!",
        "§6SILENT MOVIE! §fAct out a Minecraft mob without typing its name. Others guess in chat.",
        "§dTREASURE MEMORY! §fThe host shows 5 items. Remember as many as possible!"
    );

    private static final Map<UUID, Integer> SCORES = new LinkedHashMap<>();
    private static boolean running = false;
    private static int round = 0;
    private static int ticksLeft = 0;

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            registerCommands(dispatcher));
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (!running) return;
            if (--ticksLeft <= 0) {
                nextRound(server);
            }
        });
    }

    private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("mimicparty")
            .then(Commands.literal("start").requires(s -> s.hasPermission(2))
                .executes(ctx -> {
                    MinecraftServer server = ctx.getSource().getServer();
                    if (server.getPlayerList().getPlayers().isEmpty()) {
                        ctx.getSource().sendFailure(Component.literal("No players are online."));
                        return 0;
                    }
                    SCORES.clear();
                    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                        SCORES.put(player.getUUID(), 0);
                    }
                    running = true;
                    round = 0;
                    broadcast(server, "§aThe party has started! Get ready...");
                    nextRound(server);
                    return 1;
                }))
            .then(Commands.literal("next").requires(s -> s.hasPermission(2))
                .executes(ctx -> {
                    if (!running) {
                        ctx.getSource().sendFailure(Component.literal("Start a party first: /mimicparty start"));
                        return 0;
                    }
                    nextRound(ctx.getSource().getServer());
                    return 1;
                }))
            .then(Commands.literal("stop").requires(s -> s.hasPermission(2))
                .executes(ctx -> {
                    running = false;
                    ticksLeft = 0;
                    broadcast(ctx.getSource().getServer(), "§cThe party is over!");
                    showScores(ctx.getSource().getServer());
                    return 1;
                }))
            .then(Commands.literal("point").requires(s -> s.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(ctx -> {
                        ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
                        SCORES.putIfAbsent(target.getUUID(), 0);
                        SCORES.compute(target.getUUID(), (id, score) -> score + 1);
                        broadcast(ctx.getSource().getServer(),
                            "§a" + target.getName().getString() + " earned 1 point! §fScore: " + SCORES.get(target.getUUID()));
                        return 1;
                    })))
            .then(Commands.literal("scores")
                .executes(ctx -> {
                    showScores(ctx.getSource().getServer());
                    return 1;
                }))
            .executes(ctx -> {
                ctx.getSource().sendSuccess(() -> Component.literal(
                    "§dMimic Party commands: /mimicparty start, next, stop, point <player>, scores"), false);
                return 1;
            })
        );
    }

    private static void nextRound(MinecraftServer server) {
        round++;
        if (round > CHALLENGES.size()) {
            running = false;
            broadcast(server, "§6All rounds complete! Final scores:");
            showScores(server);
            return;
        }
        ticksLeft = 20 * 60; // one minute per round
        broadcast(server, "§5§l===== ROUND " + round + " / " + CHALLENGES.size() + " =====");
        broadcast(server, CHALLENGES.get(round - 1));
        broadcast(server, "§7You have 60 seconds. Host: award points with /mimicparty point <player>.");
    }

    private static void broadcast(MinecraftServer server, String message) {
        server.getPlayerList().broadcastSystemMessage(Component.literal(PREFIX + message), false);
    }

    private static void showScores(MinecraftServer server) {
        broadcast(server, "§e§l--- PARTY SCORES ---");
        SCORES.entrySet().stream()
            .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
            .forEach(entry -> {
                ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
                String name = player == null ? entry.getKey().toString().substring(0, 8)
                    : player.getName().getString();
                broadcast(server, "§f" + name + " §7— §e" + entry.getValue() + " points");
            });
    }
}
