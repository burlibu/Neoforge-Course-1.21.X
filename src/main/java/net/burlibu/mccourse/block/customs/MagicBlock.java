package net.burlibu.mccourse.block.customs;

import net.burlibu.mccourse.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class MagicBlock extends Block {

    public MagicBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        level.playSound(player, pos, SoundEvents.DONKEY_DEATH, SoundSource.BLOCKS,1,1);
        return InteractionResult.SUCCESS;
    }

    // this method checks if an entity is above it
    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof ItemEntity itemEntity){
            if(isValidItem(itemEntity.getItem())) {
                itemEntity.setItem(new ItemStack(Items.DIAMOND, itemEntity.getItem().getCount()));

            }
        }

        super.stepOn(level, pos, state, entity);
    }

    private boolean isValidItem(ItemStack item) {
        return item.getItem() == ModItems.RAW_BLACK_OPAL.get() || item.getItem() == Items.COAL || item.getItem() == Items.DANDELION;
    }
}
