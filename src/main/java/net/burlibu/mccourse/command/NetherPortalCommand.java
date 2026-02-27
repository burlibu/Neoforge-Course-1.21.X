package net.burlibu.mccourse.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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

import javax.annotation.Nonnull;

public class NetherPortalCommand {
    private static final int DEFAULT_WIDTH = 4;
    private static final int DEFAULT_HEIGHT = 5;
    private static final int MIN_SIZE = 2;
    private static final int MAX_SIZE = 23;

    public NetherPortalCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("netherportal")
            .executes(this::createPortalAtPlayer)
            .then(Commands.argument("width", IntegerArgumentType.integer(MIN_SIZE, MAX_SIZE))
                .then(Commands.argument("height", IntegerArgumentType.integer(MIN_SIZE, MAX_SIZE))
                    .executes(this::createPortalWithSize))));
    }

    private int createPortalAtPlayer(CommandContext<CommandSourceStack> context) {
        return createPortal(context, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    private int createPortalWithSize(CommandContext<CommandSourceStack> context) {
        int width = IntegerArgumentType.getInteger(context, "width");
        int height = IntegerArgumentType.getInteger(context, "height");
        return createPortal(context, width, height);
    }

    private int createPortal(CommandContext<CommandSourceStack> context, int width, int height) {
        try {
            ServerPlayer player = context.getSource().getPlayerOrException();
            String player_orientation = player.getDirection().toString().toLowerCase();
            ServerLevel level = player.serverLevel();
            BlockPos playerPos = player.blockPosition();
            BlockPos portalPos = findSafePortalLocation(level, playerPos, width, height);
            
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
            final BlockPos finalPortalPos = portalPos;
            buildPortalFrame(level, finalPortalPos, player_orientation, width, height);
            lightPortal(level, finalPortalPos, player_orientation, width, height);
            
            context.getSource().sendSuccess(() -> 
                Component.literal("Nether Portal (" + width + "x" + height + ") created at " + 
                    finalPortalPos.getX() + ", " + finalPortalPos.getY() + ", " + finalPortalPos.getZ()), true);
            
            return 1;
            
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Failed to create portal: " + e.getMessage()));
            return 0;
        }
    }

    @Nonnull
    private BlockPos findSafePortalLocation(@Nonnull ServerLevel level, @Nonnull BlockPos startPos, int width, int height) {
        for (int y = -2; y <= 5; y++) {
            BlockPos checkPos = startPos.offset(0, y, 0);
            if (isValidPortalLocation(level, checkPos, width, height)) {
                return checkPos;
            }
        }
        return startPos;
    }

    private boolean isValidPortalLocation(@Nonnull ServerLevel level, @Nonnull BlockPos pos, int width, int height) {
        for (int x = -1; x <= width; x++) {
            for (int y = -1; y <= height; y++) {
                BlockPos checkPos = pos.offset(x, y, 0);
                if (!level.getBlockState(checkPos).isAir() && 
                    !level.getBlockState(checkPos).canBeReplaced()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void buildPortalFrame(@Nonnull ServerLevel level, @Nonnull BlockPos basePos, @Nonnull String orientation, int width, int height) {
        BlockState obsidian = Blocks.OBSIDIAN.defaultBlockState();

        if (orientation.equals("north") || orientation.equals("south")) {
            // Portale orientato nord-sud (frame lungo l'asse X)
            // Base e top del portale
            for (int x = 0; x < width; x++) {
                level.setBlock(basePos.offset(x, -1, 0), obsidian, 3);
                level.setBlock(basePos.offset(x, height, 0), obsidian, 3);
            }
            // Lati del portale
            for (int y = 0; y < height; y++) {
                level.setBlock(basePos.offset(-1, y, 0), obsidian, 3);
                level.setBlock(basePos.offset(width, y, 0), obsidian, 3);
            }
        } else { // Portale orientato est-ovest (frame lungo l'asse Z)
            // Base e top del portale
            for (int z = 0; z < width; z++) {
                level.setBlock(basePos.offset(0, -1, z), obsidian, 3);
                level.setBlock(basePos.offset(0, height, z), obsidian, 3);
            }
            // Lati del portale
            for (int y = 0; y < height; y++) {
                level.setBlock(basePos.offset(0, y, -1), obsidian, 3);
                level.setBlock(basePos.offset(0, y, width), obsidian, 3);
            }
        }
    }

    private void lightPortal(@Nonnull ServerLevel level, @Nonnull BlockPos basePos, @Nonnull String orientation, int width, int height) {
        if (orientation.equals("north") || orientation.equals("south")) {
            // Riempi l'interno del portale (orientamento nord-sud) - asse X
            BlockState netherPortal = Blocks.NETHER_PORTAL.defaultBlockState()
                .setValue(NetherPortalBlock.AXIS, Direction.Axis.X);
            
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    level.setBlock(basePos.offset(x, y, 0), netherPortal, 3);
                }
            }
        } else {
            // Riempi l'interno del portale (orientamento est-ovest) - asse Z
            BlockState netherPortal = Blocks.NETHER_PORTAL.defaultBlockState()
                .setValue(NetherPortalBlock.AXIS, Direction.Axis.Z);
            
            for (int z = 0; z < width; z++) {
                for (int y = 0; y < height; y++) {
                    level.setBlock(basePos.offset(0, y, z), netherPortal, 3);
                }
            }
        }
    }
}