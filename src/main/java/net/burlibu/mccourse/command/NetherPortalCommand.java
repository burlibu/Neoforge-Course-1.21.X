package net.burlibu.mccourse.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;

public class NetherPortalCommand {
    
    public NetherPortalCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("netherportal")
            .executes(this::createPortalAtPlayer));
//            .then(Commands.argument("orientation", StringArgumentType.string())
//                .suggests((context, builder) -> {
//                    builder.suggest("north");
//                    builder.suggest("east");
//                    return builder.buildFuture();
//                })
//                .executes(this::createPortalWithOrientation)));
    }

    private int createPortalAtPlayer(CommandContext<CommandSourceStack> context) {
        return createPortal(context, "north");
    }

    private int createPortalWithOrientation(CommandContext<CommandSourceStack> context) {
        String orientation = StringArgumentType.getString(context, "orientation");
        if (!orientation.equals("north") && !orientation.equals("east")) {
            context.getSource().sendFailure(Component.literal("Invalid orientation! Use 'north' or 'east'"));
            return 0;
        }
        return createPortal(context, orientation);
    }

    private int createPortal(CommandContext<CommandSourceStack> context, String orientation) {
        try {
            ServerPlayer player = context.getSource().getPlayerOrException();
            String player_orientation = player.getDirection().toString().toLowerCase();
            ServerLevel level = player.serverLevel();
            BlockPos playerPos = player.blockPosition();
            BlockPos portalPos = findSafePortalLocation(level, playerPos);
            
            // Calcola la posizione finale del portale basandosi sulla direzione del player
            if (player_orientation.equals("west")) {
                portalPos = portalPos.offset(-1, 0, 0);
            } else if (player_orientation.equals("east")) {
                portalPos = portalPos.offset(1, 0, 0);
            } else if (player_orientation.equals("south")){
                portalPos = portalPos.offset(0, 0, 1);
            } else {
                portalPos = portalPos.offset(0, 0, -1);
            }
            final BlockPos finalPortalPos = portalPos; // Crea una variabile finale per la lambda
            buildPortalFrame(level, finalPortalPos, player_orientation);
            lightPortal(level, finalPortalPos, orientation);
            
            context.getSource().sendSuccess(() -> 
                Component.literal("Nether Portal created at " + 
                    finalPortalPos.getX() + ", " + finalPortalPos.getY() + ", " + finalPortalPos.getZ()), true);
            
            return 1;
            
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Failed to create portal: " + e.getMessage()));
            return 0;
        }
    }

    private BlockPos findSafePortalLocation(ServerLevel level, BlockPos startPos) {
        // Cerca una posizione piatta nelle vicinanze
        for (int y = -2; y <= 5; y++) {
            BlockPos checkPos = startPos.offset(0, y, 0);
            if (isValidPortalLocation(level, checkPos)) {
                return checkPos;
            }
        }
        // Se non trova una posizione ideale, usa quella del giocatore
        return startPos;
    }

    private boolean isValidPortalLocation(ServerLevel level, BlockPos pos) {
        // Controlla se c'è abbastanza spazio per un portale 4x5
        for (int x = -1; x <= 2; x++) {
            for (int y = 0; y <= 4; y++) {
                BlockPos checkPos = pos.offset(x, y, 0);
                if (!level.getBlockState(checkPos).isAir() && 
                    !level.getBlockState(checkPos).canBeReplaced()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void buildPortalFrame(ServerLevel level, BlockPos basePos, String orientation) {
        BlockState obsidian = Blocks.OBSIDIAN.defaultBlockState();

        if (orientation.equals("north") || orientation.equals("south")) {

            // Portale orientato nord-sud (frame lungo l'asse X)
            // Base del portale
            for (int x = 0; x < 4; x++) {
                level.setBlock(basePos.offset(x, -1, 0), obsidian, 3);
                level.setBlock(basePos.offset(x, 4, 0), obsidian, 3);
            }
            // Lati del portale
            for (int y = 0; y < 4; y++) {
                level.setBlock(basePos.offset(-1, y, 0), obsidian, 3);
                level.setBlock(basePos.offset(4, y, 0), obsidian, 3);
            }
        } else { // Portale orientato est-ovest (frame lungo l'asse Z)
            // Base del portale
            for (int z = 0; z < 4; z++) {
                level.setBlock(basePos.offset(0, -1, z), obsidian, 3);
                level.setBlock(basePos.offset(0, 4, z), obsidian, 3);
            }
            
            // Lati del portale
            for (int y = 0; y < 4; y++) {
                level.setBlock(basePos.offset(0, y, -1), obsidian, 3);
                level.setBlock(basePos.offset(0, y, 4), obsidian, 3);
            }
        }
    }

    private void lightPortal(ServerLevel level, BlockPos basePos, String orientation) {
        if (orientation.equals("north") || orientation.equals("south")) {
            // Riempi l'interno del portale (orientamento nord-sud) - asse X
            BlockState netherPortal = Blocks.NETHER_PORTAL.defaultBlockState()
                .setValue(NetherPortalBlock.AXIS, Direction.Axis.X);
            
            for (int x = -1; x < 3; x++) {
                for (int y = 0; y < 4; y++) {
                    level.setBlock(basePos.offset(x + 1, y, 0), netherPortal, 3);
                }
            }
        } else {
            // Riempi l'interno del portale (orientamento est-ovest) - asse Z
            BlockState netherPortal = Blocks.NETHER_PORTAL.defaultBlockState()
                .setValue(NetherPortalBlock.AXIS, Direction.Axis.Z);
            
            for (int z = -1; z < 3; z++) {
                for (int y = 0; y < 4; y++) {
                    level.setBlock(basePos.offset(0, y, z + 1), netherPortal, 3);
                }
            }
        }
    }
}