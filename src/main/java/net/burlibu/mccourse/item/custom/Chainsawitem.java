package net.burlibu.mccourse.item.custom;

import net.burlibu.mccourse.component.ModDataComponentTypes;
import net.burlibu.mccourse.sound.ModSounds;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Objects;

public class Chainsawitem extends Item {
    public Chainsawitem(Properties pProperties){
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        if (!level.isClientSide()){ // distruggere/ modificare blocchi dal server e non client
            if (level.getBlockState(pContext.getClickedPos()).is(BlockTags.LOGS)) { // if hit a log
                level.destroyBlock(pContext.getClickedPos(), true, pContext.getPlayer());
                pContext.getItemInHand().hurtAndBreak(1,((ServerLevel) level),((ServerPlayer) pContext.getPlayer()),
                item -> Objects.requireNonNull(pContext.getPlayer()).onEquippedItemBroken(item, EquipmentSlot.MAINHAND));

                pContext.getItemInHand().set(ModDataComponentTypes.COORDINATES, pContext.getClickedPos());
                // to play the sound
                //
                pContext.getLevel().playSound(null, pContext.getPlayer().blockPosition(), ModSounds.CHAINSAW_CUT.get(),
                        SoundSource.PLAYERS,1f, 1f);
                // Server Particles (Via Server, Seen by all players)
                ((ServerLevel) pContext.getLevel()).sendParticles(ParticleTypes.SMOKE, pContext.getClickedPos().getX() + 0.5f, pContext.getClickedPos().getY() + 1.0f,
                        pContext.getClickedPos().getZ() + 0.5f, 25, 0.0, 0.05, 0.0, 0.15f);
            } else { // not log
                pContext.getLevel().playSound(null, pContext.getPlayer().blockPosition(), ModSounds.CHAINSAW_PULL.get(),
                        SoundSource.PLAYERS,1f, 1f);
            }
        }
        return InteractionResult.CONSUME;
    }
    // CUSTOM TOOLTIP
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("tooltip.mccourse.chainsaw.tooltip.1"));
            tooltipComponents.add(Component.translatable("tooltip.mccourse.chainsaw.tooltip.2"));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.mccourse.chainsaw.tooltip.shift"));
        }
        if(stack.get(ModDataComponentTypes.COORDINATES) != null) {
            tooltipComponents.add(Component.literal("Last Tree was chopped at " + stack.get(ModDataComponentTypes.COORDINATES)));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

}
