package net.burlibu.mccourse.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class HealCommand {

    // .then crea un ramo separato che aggiunge argomenti opzionali al comando, in questo caso "target" e "amount" 
    // e nota che dopo ognuno di essi
    // c'è un .executes che specifica cosa fare quando quel particolare comando viene eseguito
    
    public HealCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("heal")
                .requires(source -> source.hasPermission(2)) // Requires operator permission
                .executes(this::healSelf) // /heal
                .then(Commands.argument("target", EntityArgument.players()) // ramo target
                        .executes(this::healTarget) // /heal <player>
                        .then(Commands.argument("amount", FloatArgumentType.floatArg(0.0F)) // un altro ramo dopo il ramo target
                                .executes(this::healTargetWithAmount))) // /heal <player> <amount>
                .then(Commands.argument("amount", FloatArgumentType.floatArg(0.0F)) // ramo amount
                        .executes(this::healSelfWithAmount))); // /heal <amount>
    }

    private int healSelf(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        float maxHealth = player.getMaxHealth();
        player.setHealth(maxHealth);
        
        context.getSource().sendSuccess(() -> Component.literal("Healed " + player.getName().getString() + " to full health"), true);
        return 1;
    }

    private int healTarget(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "target");
        
        for (ServerPlayer target : targets) { // cura effettiva
            float maxHealth = target.getMaxHealth();
            target.setHealth(maxHealth);
        }
        // Questa logica server per inviare un messaggio diverso se è stato curato un singolo giocatore o più giocatori,
        //  migliorando la chiarezza del feedback al giocatore che ha eseguito il comando.
        if (targets.size() == 1) {
            ServerPlayer target = targets.iterator().next();
            context.getSource().sendSuccess(() -> Component.literal("Healed " + target.getName().getString() + " to full health"), true);
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Healed " + targets.size() + " players to full health"), true);
        }
        return targets.size();
    }

    private int healSelfWithAmount(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        float amount = FloatArgumentType.getFloat(context, "amount");
        
        float currentHealth = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float newHealth = Math.min(currentHealth + amount, maxHealth);
        
        player.setHealth(newHealth);
        
        context.getSource().sendSuccess(() -> Component.literal("Healed " + player.getName().getString() + " by " + amount + " health points"), true);
        return 1;
    }

    private int healTargetWithAmount(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "target");
        float amount = FloatArgumentType.getFloat(context, "amount");
        
        for (ServerPlayer target : targets) {
            float currentHealth = target.getHealth();
            float maxHealth = target.getMaxHealth();
            float newHealth = Math.min(currentHealth + amount, maxHealth);
            target.setHealth(newHealth);
        }
        if (targets.size() == 1) {
            ServerPlayer target = targets.iterator().next();
            context.getSource().sendSuccess(() -> Component.literal("Healed " + target.getName().getString() + " by " + amount + " health points"), true);
        } else {
            context.getSource().sendSuccess(() -> Component.literal("Healed " + targets.size() + " players by " + amount + " health points"), true);
        }
        return targets.size();
    }
}