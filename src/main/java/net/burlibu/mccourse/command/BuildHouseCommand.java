package net.burlibu.mccourse.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BuildHouseCommand {
    
    public BuildHouseCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("buildhouse")
            .requires(source -> source.hasPermission(2))
            .executes(this::buildBasicHouse)
            .then(Commands.literal("small")
                .executes(this::buildSmallHouse))
            .then(Commands.literal("medium")
                .executes(this::buildMediumHouse))
            .then(Commands.literal("large")
                .executes(this::buildLargeHouse))
            .then(Commands.literal("mansion")
                .executes(this::buildMansion))
            .then(Commands.literal("custom")
                .then(Commands.argument("width", IntegerArgumentType.integer(3, 20))
                    .then(Commands.argument("height", IntegerArgumentType.integer(3, 15))
                        .then(Commands.argument("length", IntegerArgumentType.integer(3, 20))
                            .executes(this::buildCustomHouse)))))
            .then(Commands.literal("at")
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                    .executes(this::buildHouseAtPosition))));
    }

    private int buildBasicHouse(CommandContext<CommandSourceStack> context) {
        return buildHouse(context, 7, 5, 7, "casa base");
    }

    private int buildSmallHouse(CommandContext<CommandSourceStack> context) {
        return buildHouse(context, 5, 4, 5, "casa piccola");
    }

    private int buildMediumHouse(CommandContext<CommandSourceStack> context) {
        return buildHouse(context, 9, 6, 9, "casa media");
    }

    private int buildLargeHouse(CommandContext<CommandSourceStack> context) {
        return buildHouse(context, 12, 7, 12, "casa grande");
    }

    private int buildMansion(CommandContext<CommandSourceStack> context) {
        return buildHouse(context, 16, 9, 16, "villa");
    }

    private int buildCustomHouse(CommandContext<CommandSourceStack> context) {
        int width = IntegerArgumentType.getInteger(context, "width");
        int height = IntegerArgumentType.getInteger(context, "height");
        int length = IntegerArgumentType.getInteger(context, "length");
        
        return buildHouse(context, width, height, length, "casa personalizzata");
    }

    private int buildHouseAtPosition(CommandContext<CommandSourceStack> context) {
        try {
            BlockPos targetPos = BlockPosArgument.getBlockPos(context, "pos");
            return buildHouseAt(context, targetPos, 7, 5, 7, "casa base");
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("§cPosizione non valida!"));
            return 0;
        }
    }

    private int buildHouse(CommandContext<CommandSourceStack> context, int width, int height, int length, String houseType) {
        try {
            ServerPlayer player = context.getSource().getPlayerOrException();
            BlockPos playerPos = player.blockPosition();
            
            // Trova una posizione adatta davanti al giocatore
            BlockPos buildPos = findBuildPosition(player, width, length);
            
            return buildHouseAt(context, buildPos, width, height, length, houseType);
            
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("§cErrore nella costruzione: " + e.getMessage()));
            return 0;
        }
    }

    private int buildHouseAt(CommandContext<CommandSourceStack> context, BlockPos pos, int width, int height, int length, String houseType) {
        try {
            ServerLevel level = context.getSource().getLevel();
            
            // Materiali da costruzione
            BlockState foundation = Blocks.STONE_BRICKS.defaultBlockState();
            BlockState walls = Blocks.OAK_PLANKS.defaultBlockState();
            BlockState roof = Blocks.OAK_STAIRS.defaultBlockState();
            BlockState door = Blocks.OAK_DOOR.defaultBlockState();
            BlockState window = Blocks.GLASS.defaultBlockState();
            BlockState floor = Blocks.OAK_PLANKS.defaultBlockState();
            
            int blocksPlaced = 0;
            
            // 1. Costruisci fondamenta
            blocksPlaced += buildFoundation(level, pos, width, length, foundation);
            
            // 2. Costruisci pavimento
            blocksPlaced += buildFloor(level, pos, width, length, floor);
            
            // 3. Costruisci muri
            blocksPlaced += buildWalls(level, pos, width, height, length, walls);
            
            // 4. Aggiungi porta
            blocksPlaced += buildDoor(level, pos, width, length, door);
            
            // 5. Aggiungi finestre
            blocksPlaced += buildWindows(level, pos, width, height, length, window);
            
            // 6. Costruisci tetto
            blocksPlaced += buildRoof(level, pos, width, height, length, roof);
            
            // 7. Arreda la casa
            blocksPlaced += furnishHouse(level, pos, width, height, length);
            
            final String playerName = context.getSource().getEntity() != null ? 
                context.getSource().getEntity().getName().getString() : "Server";
            final int finalBlocksPlaced = blocksPlaced;
            final String finalHouseType = houseType;
            
            context.getSource().sendSuccess(() -> 
                Component.literal("§a" + finalHouseType.substring(0, 1).toUpperCase() + finalHouseType.substring(1) + 
                    " costruita con successo! §7(" + finalBlocksPlaced + " blocchi utilizzati)"), true);
            
            // Annuncio globale
            if (context.getSource().getEntity() instanceof ServerPlayer) {
                level.getServer().getPlayerList().broadcastSystemMessage(
                    Component.literal("§e" + playerName + " ha costruito una " + finalHouseType + "!"), false);
            }
            
            return 1;
            
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("§cErrore nella costruzione: " + e.getMessage()));
            return 0;
        }
    }

    private BlockPos findBuildPosition(ServerPlayer player, int width, int length) {
        BlockPos playerPos = player.blockPosition();
        float yaw = player.getYRot();
        
        // Calcola la direzione in cui sta guardando il giocatore
        int offsetX = 0;
        int offsetZ = 0;
        
        if (yaw >= -45 && yaw < 45) { // Sud (+Z)
            offsetZ = 3;
        } else if (yaw >= 45 && yaw < 135) { // Ovest (-X)
            offsetX = -width - 2;
        } else if (yaw >= 135 || yaw < -135) { // Nord (-Z)
            offsetZ = -length - 2;
        } else { // Est (+X)
            offsetX = 3;
        }
        
        return playerPos.offset(offsetX, -1, offsetZ);
    }

    private int buildFoundation(ServerLevel level, BlockPos pos, int width, int length, BlockState foundation) {
        int count = 0;
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < length; z++) {
                level.setBlock(pos.offset(x, -1, z), foundation, 3);
                count++;
            }
        }
        return count;
    }

    private int buildFloor(ServerLevel level, BlockPos pos, int width, int length, BlockState floor) {
        int count = 0;
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < length; z++) {
                level.setBlock(pos.offset(x, 0, z), floor, 3);
                count++;
            }
        }
        return count;
    }

    private int buildWalls(ServerLevel level, BlockPos pos, int width, int height, int length, BlockState walls) {
        int count = 0;
        
        for (int y = 1; y < height; y++) {
            // Muro davanti e dietro
            for (int x = 0; x < width; x++) {
                level.setBlock(pos.offset(x, y, 0), walls, 3);
                level.setBlock(pos.offset(x, y, length - 1), walls, 3);
                count += 2;
            }
            
            // Muro sinistro e destro
            for (int z = 1; z < length - 1; z++) {
                level.setBlock(pos.offset(0, y, z), walls, 3);
                level.setBlock(pos.offset(width - 1, y, z), walls, 3);
                count += 2;
            }
        }
        return count;
    }

    private int buildDoor(ServerLevel level, BlockPos pos, int width, int length, BlockState door) {
        int doorX = width / 2;
        int doorZ = 0;
        
        // Rimuovi i blocchi del muro per la porta
        level.setBlock(pos.offset(doorX, 1, doorZ), Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(pos.offset(doorX, 2, doorZ), Blocks.AIR.defaultBlockState(), 3);
        
        // Piazza la porta
        level.setBlock(pos.offset(doorX, 1, doorZ), door, 3);
        
        return 2;
    }

    private int buildWindows(ServerLevel level, BlockPos pos, int width, int height, int length, BlockState window) {
        int count = 0;
        int windowHeight = height / 2;
        
        // Finestre sui muri laterali se la casa è abbastanza grande
        if (width >= 7 && length >= 7) {
            // Finestra sinistra
            level.setBlock(pos.offset(0, windowHeight, length / 2), window, 3);
            count++;
            
            // Finestra destra  
            level.setBlock(pos.offset(width - 1, windowHeight, length / 2), window, 3);
            count++;
            
            // Finestra dietro
            level.setBlock(pos.offset(width / 2, windowHeight, length - 1), window, 3);
            count++;
        }
        
        return count;
    }

    private int buildRoof(ServerLevel level, BlockPos pos, int width, int height, int length, BlockState roof) {
        int count = 0;
        int roofLevel = height;
        
        // Tetto semplice piatto per ora
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < length; z++) {
                level.setBlock(pos.offset(x, roofLevel, z), Blocks.OAK_PLANKS.defaultBlockState(), 3);
                count++;
            }
        }
        
        return count;
    }

    private int furnishHouse(ServerLevel level, BlockPos pos, int width, int height, int length) {
        int count = 0;
        
        // Aggiungi alcuni mobili se c'è spazio
        if (width >= 5 && length >= 5) {
            // Letto nell'angolo
            level.setBlock(pos.offset(width - 2, 1, length - 2), Blocks.RED_BED.defaultBlockState(), 3);
            count++;
            
            // Tavolo da lavoro
            level.setBlock(pos.offset(1, 1, 1), Blocks.CRAFTING_TABLE.defaultBlockState(), 3);
            count++;
            
            // Fornace
            level.setBlock(pos.offset(2, 1, 1), Blocks.FURNACE.defaultBlockState(), 3);
            count++;
            
            // Cassa
            level.setBlock(pos.offset(1, 1, 2), Blocks.CHEST.defaultBlockState(), 3);
            count++;
        }
        
        return count;
    }
}