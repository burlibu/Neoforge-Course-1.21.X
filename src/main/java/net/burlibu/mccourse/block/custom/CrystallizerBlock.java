package net.burlibu.mccourse.block.custom;

import com.mojang.serialization.MapCodec;
import net.burlibu.mccourse.block.ModBlocks;
import net.burlibu.mccourse.block.entity.ModBlockEntities;
import net.burlibu.mccourse.block.entity.custom.CrystallizerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

// & OLD CODE
//public class CrystallizerBlock extends HorizontalDirectionalBlock {
//    public static final MapCodec<CrystallizerBlock> CODEC = simpleCodec(CrystallizerBlock::new);
//
//    public CrystallizerBlock(Properties pProperties) {
//        super(pProperties);
//    }
//
//    @Override
//    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
//        return CODEC;
//    }
//
//    @Nullable
//    @Override
//    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
//        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
//    }
//
//    @Override
//    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
//        pBuilder.add(FACING);
//    }
//
//    @Override
//    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
//        // THIS METHOD IS !CLIENT ONLY!
//        double xPos = pPos.getX() + 0.5f; // per centrare le particelle siccome partono da angolo basso
//        double yPos = pPos.getY() + 1.25f;
//        double zPos = pPos.getZ() + 0.5f;
//        double offset = pRandom.nextDouble() * 0.6 - 0.3;
//        pLevel.addParticle(ParticleTypes.SMOKE, xPos + offset, yPos, zPos + offset, 0.0, 0.0, 0.0);
//        pLevel.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, ModBlocks.BLACK_OPAL_BLOCK.get().defaultBlockState()),
//                xPos + offset, yPos, zPos + offset, 0.0, 0.0, 0.0);
//    }
//}

public class CrystallizerBlock extends BaseEntityBlock {
    public static final MapCodec<CrystallizerBlock> CODEC = simpleCodec(CrystallizerBlock::new);

    public CrystallizerBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    /* BLOCK ENTITY */

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CrystallizerBlockEntity(pPos, pState);
    }

    @Override
    protected RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof CrystallizerBlockEntity crystallizerBlockEntity) {
                crystallizerBlockEntity.drops();
            }
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos,
                                              Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if(entity instanceof CrystallizerBlockEntity crystallizerBlockEntity) {
                ((ServerPlayer) pPlayer).openMenu(new SimpleMenuProvider(crystallizerBlockEntity, Component.literal("Crystallizer")), pPos);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }

        return ItemInteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide()) {
            return null;
        }

        return createTickerHelper(pBlockEntityType, ModBlockEntities.CRYSTALLIZER_BE.get(),
                (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1));
    }
}