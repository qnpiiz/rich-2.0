package net.minecraft.block;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.material.Material;
import net.minecraft.block.pattern.BlockMaterialMatcher;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.enchantment.IArmorVanishable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class CarvedPumpkinBlock extends HorizontalBlock implements IArmorVanishable
{
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    @Nullable
    private BlockPattern snowmanBasePattern;
    @Nullable
    private BlockPattern snowmanPattern;
    @Nullable
    private BlockPattern golemBasePattern;
    @Nullable
    private BlockPattern golemPattern;
    private static final Predicate<BlockState> IS_PUMPKIN = (state) ->
    {
        return state != null && (state.isIn(Blocks.CARVED_PUMPKIN) || state.isIn(Blocks.JACK_O_LANTERN));
    };

    protected CarvedPumpkinBlock(AbstractBlock.Properties properties)
    {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
    }

    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
    {
        if (!oldState.isIn(state.getBlock()))
        {
            this.trySpawnGolem(worldIn, pos);
        }
    }

    public boolean canDispenserPlace(IWorldReader reader, BlockPos pos)
    {
        return this.getSnowmanBasePattern().match(reader, pos) != null || this.getGolemBasePattern().match(reader, pos) != null;
    }

    private void trySpawnGolem(World world, BlockPos pos)
    {
        BlockPattern.PatternHelper blockpattern$patternhelper = this.getSnowmanPattern().match(world, pos);

        if (blockpattern$patternhelper != null)
        {
            for (int i = 0; i < this.getSnowmanPattern().getThumbLength(); ++i)
            {
                CachedBlockInfo cachedblockinfo = blockpattern$patternhelper.translateOffset(0, i, 0);
                world.setBlockState(cachedblockinfo.getPos(), Blocks.AIR.getDefaultState(), 2);
                world.playEvent(2001, cachedblockinfo.getPos(), Block.getStateId(cachedblockinfo.getBlockState()));
            }

            SnowGolemEntity snowgolementity = EntityType.SNOW_GOLEM.create(world);
            BlockPos blockpos1 = blockpattern$patternhelper.translateOffset(0, 2, 0).getPos();
            snowgolementity.setLocationAndAngles((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.05D, (double)blockpos1.getZ() + 0.5D, 0.0F, 0.0F);
            world.addEntity(snowgolementity);

            for (ServerPlayerEntity serverplayerentity : world.getEntitiesWithinAABB(ServerPlayerEntity.class, snowgolementity.getBoundingBox().grow(5.0D)))
            {
                CriteriaTriggers.SUMMONED_ENTITY.trigger(serverplayerentity, snowgolementity);
            }

            for (int l = 0; l < this.getSnowmanPattern().getThumbLength(); ++l)
            {
                CachedBlockInfo cachedblockinfo3 = blockpattern$patternhelper.translateOffset(0, l, 0);
                world.func_230547_a_(cachedblockinfo3.getPos(), Blocks.AIR);
            }
        }
        else
        {
            blockpattern$patternhelper = this.getGolemPattern().match(world, pos);

            if (blockpattern$patternhelper != null)
            {
                for (int j = 0; j < this.getGolemPattern().getPalmLength(); ++j)
                {
                    for (int k = 0; k < this.getGolemPattern().getThumbLength(); ++k)
                    {
                        CachedBlockInfo cachedblockinfo2 = blockpattern$patternhelper.translateOffset(j, k, 0);
                        world.setBlockState(cachedblockinfo2.getPos(), Blocks.AIR.getDefaultState(), 2);
                        world.playEvent(2001, cachedblockinfo2.getPos(), Block.getStateId(cachedblockinfo2.getBlockState()));
                    }
                }

                BlockPos blockpos = blockpattern$patternhelper.translateOffset(1, 2, 0).getPos();
                IronGolemEntity irongolementity = EntityType.IRON_GOLEM.create(world);
                irongolementity.setPlayerCreated(true);
                irongolementity.setLocationAndAngles((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.05D, (double)blockpos.getZ() + 0.5D, 0.0F, 0.0F);
                world.addEntity(irongolementity);

                for (ServerPlayerEntity serverplayerentity1 : world.getEntitiesWithinAABB(ServerPlayerEntity.class, irongolementity.getBoundingBox().grow(5.0D)))
                {
                    CriteriaTriggers.SUMMONED_ENTITY.trigger(serverplayerentity1, irongolementity);
                }

                for (int i1 = 0; i1 < this.getGolemPattern().getPalmLength(); ++i1)
                {
                    for (int j1 = 0; j1 < this.getGolemPattern().getThumbLength(); ++j1)
                    {
                        CachedBlockInfo cachedblockinfo1 = blockpattern$patternhelper.translateOffset(i1, j1, 0);
                        world.func_230547_a_(cachedblockinfo1.getPos(), Blocks.AIR);
                    }
                }
            }
        }
    }

    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
    }

    private BlockPattern getSnowmanBasePattern()
    {
        if (this.snowmanBasePattern == null)
        {
            this.snowmanBasePattern = BlockPatternBuilder.start().aisle(" ", "#", "#").where('#', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(Blocks.SNOW_BLOCK))).build();
        }

        return this.snowmanBasePattern;
    }

    private BlockPattern getSnowmanPattern()
    {
        if (this.snowmanPattern == null)
        {
            this.snowmanPattern = BlockPatternBuilder.start().aisle("^", "#", "#").where('^', CachedBlockInfo.hasState(IS_PUMPKIN)).where('#', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(Blocks.SNOW_BLOCK))).build();
        }

        return this.snowmanPattern;
    }

    private BlockPattern getGolemBasePattern()
    {
        if (this.golemBasePattern == null)
        {
            this.golemBasePattern = BlockPatternBuilder.start().aisle("~ ~", "###", "~#~").where('#', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(Blocks.IRON_BLOCK))).where('~', CachedBlockInfo.hasState(BlockMaterialMatcher.forMaterial(Material.AIR))).build();
        }

        return this.golemBasePattern;
    }

    private BlockPattern getGolemPattern()
    {
        if (this.golemPattern == null)
        {
            this.golemPattern = BlockPatternBuilder.start().aisle("~^~", "###", "~#~").where('^', CachedBlockInfo.hasState(IS_PUMPKIN)).where('#', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(Blocks.IRON_BLOCK))).where('~', CachedBlockInfo.hasState(BlockMaterialMatcher.forMaterial(Material.AIR))).build();
        }

        return this.golemPattern;
    }
}
