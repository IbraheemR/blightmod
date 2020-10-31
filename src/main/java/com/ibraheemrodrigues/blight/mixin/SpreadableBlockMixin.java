package com.ibraheemrodrigues.blight.mixin;

import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

import static com.ibraheemrodrigues.blight.block.Blocks.BLIGHT_BLOCK;
import static net.minecraft.block.Blocks.GRASS_BLOCK;
import static net.minecraft.block.Blocks.MYCELIUM;
import static net.minecraft.block.SnowyBlock.SNOWY;

@Mixin(SpreadableBlock.class)
public class SpreadableBlockMixin extends Block {

    public SpreadableBlockMixin(Settings settings) {
        super(settings);
    }

    private static boolean canSurvive(BlockState state, WorldView worldView, BlockPos pos) {
        BlockPos blockPos = pos.up();

        if (state.isOf(BLIGHT_BLOCK)) {
            blockPos = blockPos.up();
        }

        BlockState blockState = worldView.getBlockState(blockPos);



        if (blockState.isOf(Blocks.SNOW) && (Integer)blockState.get(SnowBlock.LAYERS) == 1) {
            return true;
        } else if (blockState.getFluidState().getLevel() == 8) {
            return false;
        } else {
            int i = ChunkLightProvider.getRealisticOpacity(worldView, state, pos, blockState, blockPos, Direction.UP, blockState.getOpacity(worldView, blockPos));
            return i < worldView.getMaxLightLevel();
        }
    }

    private static boolean canSpread(BlockState state, WorldView worldView, BlockPos pos) {
        BlockPos blockPos = pos.up();

        return canSurvive(state, worldView, pos) && !worldView.getFluidState(blockPos).isIn(FluidTags.WATER);
    }

    private static boolean canSpreadBlock(BlockState source, BlockState target) {
        if (target.isOf(Blocks.DIRT)) {
            return true;
        }

        if (source.isOf(GRASS_BLOCK)) {
            return target.isOf(BLIGHT_BLOCK);
        }

        if (source.isOf(Blocks.MYCELIUM)) {
            return target.isOf(GRASS_BLOCK);
        }

        if (source.isOf(BLIGHT_BLOCK)) {
            if (target.isOf(Blocks.FARMLAND) || target.isOf(Blocks.MYCELIUM)) {
            }
            return target.isOf(Blocks.FARMLAND) || target.isOf(Blocks.MYCELIUM);
        }

        return false;
    }

    @Inject(at=@At("HEAD"), cancellable = true, method = "randomTick")
    public void randomTick(BlockState sourceState, ServerWorld world, BlockPos pos, Random random, CallbackInfo info) {
        if (!canSurvive(sourceState, world, pos)) {
            world.setBlockState(pos, Blocks.DIRT.getDefaultState());
        } else {
            if (world.getLightLevel(pos.up()) >= 9) {
                BlockState sourceDefaultState = this.getDefaultState();

                for(int i = 0; i < 4; ++i) {
                    BlockPos targetPos = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                    BlockState currentTargetState = world.getBlockState(targetPos);
                    if (canSpread(sourceDefaultState, world, targetPos) && canSpreadBlock(sourceDefaultState, currentTargetState)) {
                        world.setBlockState(targetPos, sourceDefaultState.with(SNOWY, world.getBlockState(targetPos.up()).isOf(Blocks.SNOW)));
                    }
                }
            }

        }

        info.cancel();
    }
}

