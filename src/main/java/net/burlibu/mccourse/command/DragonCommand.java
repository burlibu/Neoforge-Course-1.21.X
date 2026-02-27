package net.burlibu.mccourse.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DragonCommand {
    
    public DragonCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("dragon")
            .requires(source -> source.hasPermission(2))
            .executes(this::summonBasicDragon)
            .then(Commands.literal("summon")
                .executes(this::summonBasicDragon))
            .then(Commands.literal("fire")
                .executes(this::summonFireDragon))
            .then(Commands.literal("ice")
                .executes(this::summonIceDragon))
            .then(Commands.literal("shadow")
                .executes(this::summonShadowDragon))
            .then(Commands.literal("army")
                .executes(this::summonDragonArmy))
            .then(Commands.literal("custom")
                .then(Commands.argument("name", StringArgumentType.string())
                    .executes(this::summonCustomDragon)))
            .then(Commands.literal("rain")
                .executes(this::dragonRain)));
    }

    private int summonBasicDragon(CommandContext<CommandSourceStack> context) {
        return summonDragon(context, "Drago Base", false, false, false, 1);
    }

    private int summonFireDragon(CommandContext<CommandSourceStack> context) {
        return summonDragon(context, "§cDrago di Fuoco§r", true, false, false, 1);
    }

    private int summonIceDragon(CommandContext<CommandSourceStack> context) {
        return summonDragon(context, "§bDrago di Ghiaccio§r", false, true, false, 1);
    }

    private int summonShadowDragon(CommandContext<CommandSourceStack> context) {
        return summonDragon(context, "§8Drago delle Ombre§r", false, false, true, 1);
    }

    private int summonDragonArmy(CommandContext<CommandSourceStack> context) {
        return summonDragon(context, "§5Armata dei Draghi§r", true, true, true, 3);
    }

    private int summonCustomDragon(CommandContext<CommandSourceStack> context) {
        String customName = StringArgumentType.getString(context, "name");
        return summonDragon(context, "§d" + customName + "§r", true, false, false, 1);
    }

    private int dragonRain(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = context.getSource().getPlayerOrException();
            ServerLevel level = player.serverLevel();
            BlockPos playerPos = player.blockPosition();
            
            // Crea una pioggia di mini draghi
            for (int i = 0; i < 10; i++) {
                int offsetX = (int) (Math.random() * 40 - 20);
                int offsetZ = (int) (Math.random() * 40 - 20);
                int offsetY = (int) (Math.random() * 20 + 30);
                
                BlockPos spawnPos = playerPos.offset(offsetX, offsetY, offsetZ);
                summonDragonAt(level, spawnPos, "§6Mini Drago §e#" + (i + 1), false, false, false);
            }
            
            final String playerName = player.getName().getString();
            
            context.getSource().sendSuccess(() -> 
                Component.literal("§6Pioggia di draghi evocata! §710 mini draghi stanno arrivando!"), true);
            
            level.getServer().getPlayerList().broadcastSystemMessage(
                Component.literal("§e" + playerName + " ha scatenato una pioggia di draghi! §cATTENZIONE!"), false);
            
            // Suono epico
            level.playSound(null, playerPos, SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 2.0F, 0.5F);
            
            return 1;
            
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("§cErrore nell'evocare la pioggia di draghi: " + e.getMessage()));
            return 0;
        }
    }

    private int summonDragon(CommandContext<CommandSourceStack> context, String dragonName, boolean fireType, boolean iceType, boolean shadowType, int count) {
        try {
            ServerPlayer player = context.getSource().getPlayerOrException();
            ServerLevel level = player.serverLevel();
            BlockPos playerPos = player.blockPosition();
            
            int dragonsSpawned = 0;
            
            for (int i = 0; i < count; i++) {
                // Trova posizione di spawn sopra il giocatore
                BlockPos spawnPos = playerPos.offset(
                    (int) (Math.random() * 20 - 10), 
                    15 + i * 5, 
                    (int) (Math.random() * 20 - 10)
                );
                
                if (summonDragonAt(level, spawnPos, dragonName + (count > 1 ? " #" + (i + 1) : ""), fireType, iceType, shadowType)) {
                    dragonsSpawned++;
                }
            }
            
            final String playerName = player.getName().getString();
            final int finalDragonsSpawned = dragonsSpawned;
            final String finalDragonName = dragonName;
            
            context.getSource().sendSuccess(() -> 
                Component.literal("§a" + finalDragonsSpawned + " " + finalDragonName + 
                    (finalDragonsSpawned > 1 ? " evocati" : " evocato") + " con successo!"), true);
            
            // Annuncio drammatico
            level.getServer().getPlayerList().broadcastSystemMessage(
                Component.literal("§c⚡ " + playerName + " ha evocato " + finalDragonName + "! ⚡"), false);
            
            // Effetti sonori e visivi
            level.playSound(null, playerPos, SoundEvents.ENDER_DRAGON_AMBIENT, SoundSource.HOSTILE, 3.0F, 0.8F);
            level.playSound(null, playerPos, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 1.0F, 1.0F);
            
            // Lampi nel cielo
            for (int i = 0; i < 3; i++) {
                BlockPos lightningPos = playerPos.offset(
                    (int) (Math.random() * 30 - 15), 
                    0, 
                    (int) (Math.random() * 30 - 15)
                );
                level.explode(null, lightningPos.getX(), lightningPos.getY() + 20, lightningPos.getZ(), 
                    0.0F, false, Level.ExplosionInteraction.NONE);
            }
            
            return dragonsSpawned;
            
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("§cErrore nell'evocare il drago: " + e.getMessage()));
            return 0;
        }
    }

    private boolean summonDragonAt(ServerLevel level, BlockPos pos, String name, boolean fireType, boolean iceType, boolean shadowType) {
        try {
            // Crea l'Ender Dragon
            EnderDragon dragon = EntityType.ENDER_DRAGON.create(level);
            if (dragon == null) return false;
            
            // Posiziona il drago
            dragon.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            
            // Imposta nome personalizzato
            dragon.setCustomName(Component.literal(name));
            dragon.setCustomNameVisible(true);
            
            // Rendi il drago più forte
            dragon.setHealth(dragon.getMaxHealth());
            
            // Effetti speciali basati sul tipo
            if (fireType) {
                addFireDragonEffects(dragon);
            }
            if (iceType) {
                addIceDragonEffects(dragon);
            }
            if (shadowType) {
                addShadowDragonEffects(dragon, level, pos);
            }
            
            // Spawn del drago
            level.addFreshEntity(dragon);
            
            // Effetti di spawn
            createSpawnEffects(level, pos, fireType, iceType, shadowType);
            
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }

    private void addFireDragonEffects(EnderDragon dragon) {
        // Il drago di fuoco è immune al fuoco e fa più danni
        dragon.setRemainingFireTicks(0);
        dragon.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
        dragon.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, Integer.MAX_VALUE, 2, false, false));
    }

    private void addIceDragonEffects(EnderDragon dragon) {
        // Il drago di ghiaccio rallenta i nemici e ha resistenza
        dragon.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1, false, false));
        dragon.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, Integer.MAX_VALUE, 1, false, false));
    }

    private void addShadowDragonEffects(EnderDragon dragon, ServerLevel level, BlockPos pos) {
        // Il drago delle ombre evoca minion
        dragon.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 200, 0, false, false));
        dragon.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, Integer.MAX_VALUE, 1, false, false));
        
        // Evoca minion delle ombre
        spawnShadowMinions(level, pos);
    }

    private void spawnShadowMinions(ServerLevel level, BlockPos centerPos) {
        // Evoca scheletri e zombie come minion
        for (int i = 0; i < 4; i++) {
            BlockPos minionPos = centerPos.offset(
                (int) (Math.random() * 16 - 8), 
                -2, 
                (int) (Math.random() * 16 - 8)
            );
            
            if (Math.random() < 0.5) {
                // Skeleton minion
                Skeleton skeleton = EntityType.SKELETON.create(level);
                if (skeleton != null) {
                    skeleton.setPos(minionPos.getX(), minionPos.getY(), minionPos.getZ());
                    skeleton.setCustomName(Component.literal("§8Scheletro delle Ombre"));
                    skeleton.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, Integer.MAX_VALUE, 1));
                    skeleton.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, Integer.MAX_VALUE, 1));
                    level.addFreshEntity(skeleton);
                }
            } else {
                // Zombie minion
                Zombie zombie = EntityType.ZOMBIE.create(level);
                if (zombie != null) {
                    zombie.setPos(minionPos.getX(), minionPos.getY(), minionPos.getZ());
                    zombie.setCustomName(Component.literal("§8Zombie delle Ombre"));
                    zombie.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, Integer.MAX_VALUE, 1));
                    zombie.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
                    level.addFreshEntity(zombie);
                }
            }
        }
        
        // Evoca anche una strega
        Witch witch = EntityType.WITCH.create(level);
        if (witch != null) {
            witch.setPos(centerPos.getX(), centerPos.getY() - 3, centerPos.getZ());
            witch.setCustomName(Component.literal("§5Strega delle Ombre"));
            witch.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, Integer.MAX_VALUE, 2));
            level.addFreshEntity(witch);
        }
    }

    private void createSpawnEffects(ServerLevel level, BlockPos pos, boolean fireType, boolean iceType, boolean shadowType) {
        // Esplosioni decorative
        if (fireType) {
            // Effetti di fuoco
            for (int i = 0; i < 5; i++) {
                BlockPos effectPos = pos.offset(
                    (int) (Math.random() * 10 - 5), 
                    (int) (Math.random() * 5), 
                    (int) (Math.random() * 10 - 5)
                );
                level.explode(null, effectPos.getX(), effectPos.getY(), effectPos.getZ(), 
                    2.0F, false, Level.ExplosionInteraction.NONE);
            }
            level.playSound(null, pos, SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 2.0F, 0.5F);
        }
        
        if (iceType) {
            // Effetti di ghiaccio (suoni freddi)
            level.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 2.0F, 0.3F);
            level.playSound(null, pos, SoundEvents.POWDER_SNOW_STEP, SoundSource.HOSTILE, 2.0F, 0.5F);
        }
        
        if (shadowType) {
            // Effetti di ombra
            level.playSound(null, pos, SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.0F, 0.8F);
            level.playSound(null, pos, SoundEvents.SOUL_ESCAPE.value(), SoundSource.HOSTILE, 2.0F, 0.5F);
        }
        
        // Effetto generale di evocazione
        level.playSound(null, pos, SoundEvents.END_PORTAL_SPAWN, SoundSource.HOSTILE, 1.5F, 1.0F);
    }
}