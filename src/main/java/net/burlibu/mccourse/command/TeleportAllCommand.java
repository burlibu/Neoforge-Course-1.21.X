package net.burlibu.mccourse.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;

import java.util.List;

public class TeleportAllCommand {
    
    public TeleportAllCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("teleportall")
            .requires(source -> source.hasPermission(2)) // Richiede livello operatore
            .executes(this::teleportAllPlayers)
            .then(Commands.literal("here")
                .executes(this::teleportAllPlayers))
            .then(Commands.literal("spawn")
                .executes(this::teleportAllToSpawn)));
    }

    private int teleportAllPlayers(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer commandPlayer = context.getSource().getPlayerOrException();
            ServerLevel level = commandPlayer.serverLevel();
            Vec3 targetPos = commandPlayer.position();
            
            // Ottieni tutti i giocatori online
            List<ServerPlayer> allPlayers = level.getServer().getPlayerList().getPlayers();
            int teleportedCount = 0;
            
            for (ServerPlayer player : allPlayers) {
                if (!player.equals(commandPlayer)) { // Non teletrasportare se stesso
                    player.teleportTo(level, targetPos.x, targetPos.y, targetPos.z, 
                                    commandPlayer.getYRot(), commandPlayer.getXRot());
                    
                    // Invia messaggio al giocatore teletrasportato
                    player.sendSystemMessage(Component.literal("§6Sei stato teletrasportato da " + 
                        commandPlayer.getName().getString() + "!"));
                    
                    teleportedCount++;
                }
            }
            
            final int finalTeleportedCount = teleportedCount;
            final String playerName = commandPlayer.getName().getString();
            
            if (finalTeleportedCount > 0) {
                context.getSource().sendSuccess(() -> 
                    Component.literal("§a" + finalTeleportedCount + " giocatori sono stati teletrasportati alla tua posizione!"), true);
                
                // Annuncio globale
                level.getServer().getPlayerList().broadcastSystemMessage(
                    Component.literal("§e" + playerName + 
                        " ha teletrasportato tutti i giocatori alla sua posizione!"), false);
            } else {
                context.getSource().sendFailure(Component.literal("§cNessun altro giocatore da teletrasportare!"));
            }
            
            return finalTeleportedCount;
            
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("§cErrore nel teletrasportare i giocatori: " + e.getMessage()));
            return 0;
        }
    }
    
    private int teleportAllToSpawn(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer commandPlayer = context.getSource().getPlayerOrException();
            ServerLevel level = commandPlayer.serverLevel();
            
            // Posizione dello spawn del mondo
            BlockPos spawnPos = level.getSharedSpawnPos();
            
            List<ServerPlayer> allPlayers = level.getServer().getPlayerList().getPlayers();
            int teleportedCount = 0;
            
            for (ServerPlayer player : allPlayers) {
                player.teleportTo(level, spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0.0F, 0.0F);
                
                player.sendSystemMessage(Component.literal("§6Sei stato teletrasportato allo spawn da " + 
                    commandPlayer.getName().getString() + "!"));
                
                teleportedCount++;
            }
            
            final int finalTeleportedCount = teleportedCount;
            final String playerName = commandPlayer.getName().getString();
            
            context.getSource().sendSuccess(() -> 
                Component.literal("§a" + finalTeleportedCount + " giocatori sono stati teletrasportati allo spawn!"), true);
            
            // Annuncio globale
            level.getServer().getPlayerList().broadcastSystemMessage(
                Component.literal("§e" + playerName + 
                    " ha teletrasportato tutti i giocatori allo spawn!"), false);
            
            return finalTeleportedCount;
            
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("§cErrore nel teletrasportare i giocatori allo spawn: " + e.getMessage()));
            return 0;
        }
    }
}