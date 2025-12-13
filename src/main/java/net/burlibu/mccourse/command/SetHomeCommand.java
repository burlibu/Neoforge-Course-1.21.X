package net.burlibu.mccourse.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class SetHomeCommand {
    //home set
    public SetHomeCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("home").then(Commands.literal("set")
                .executes(this::execute)));
    }

    private int execute(CommandContext<CommandSourceStack> context) {

        ServerPlayer player = context.getSource().getPlayer();
        String dimensionString = player.level().dimension().location().toString();
        player.getPersistentData().putString("mccourse.homedim", dimensionString);
        BlockPos playerPos = player.blockPosition();
        String positionString = "(" + playerPos.getX() + ", " + playerPos.getY() + ", " + playerPos.getZ() + ")";
        player.getPersistentData().putIntArray("mccourse.homepos", new int[] {playerPos.getX(),playerPos.getY(),playerPos.getZ()});

        context.getSource().sendSuccess(()-> Component.literal("Set home at "+positionString + " in the " + dimensionString), true);
        return 1;
    }
}
