package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.material.Material;
import net.minecraft.block.pattern.BlockMaterialMatcher;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class WitherSkeletonSkullBlock extends SkullBlock
{
    @Nullable
    private static BlockPattern witherPatternFull;
    @Nullable
    private static BlockPattern witherPatternBase;

    protected WitherSkeletonSkullBlock(AbstractBlock.Properties properties)
    {
        super(SkullBlock.Types.WITHER_SKELETON, properties);
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof SkullTileEntity)
        {
            checkWitherSpawn(worldIn, pos, (SkullTileEntity)tileentity);
        }
    }

    public static void checkWitherSpawn(World worldIn, BlockPos pos, SkullTileEntity tileEntity)
    {
        if (!worldIn.isRemote)
        {
            BlockState blockstate = tileEntity.getBlockState();
            boolean flag = blockstate.isIn(Blocks.WITHER_SKELETON_SKULL) || blockstate.isIn(Blocks.WITHER_SKELETON_WALL_SKULL);

            if (flag && pos.getY() >= 0 && worldIn.getDifficulty() != Difficulty.PEACEFUL)
            {
                BlockPattern blockpattern = getOrCreateWitherFull();
                BlockPattern.PatternHelper blockpattern$patternhelper = blockpattern.match(worldIn, pos);

                if (blockpattern$patternhelper != null)
                {
                    for (int i = 0; i < blockpattern.getPalmLength(); ++i)
                    {
                        for (int j = 0; j < blockpattern.getThumbLength(); ++j)
                        {
                            CachedBlockInfo cachedblockinfo = blockpattern$patternhelper.translateOffset(i, j, 0);
                            worldIn.setBlockState(cachedblockinfo.getPos(), Blocks.AIR.getDefaultState(), 2);
                            worldIn.playEvent(2001, cachedblockinfo.getPos(), Block.getStateId(cachedblockinfo.getBlockState()));
                        }
                    }

                    WitherEntity witherentity = EntityType.WITHER.create(worldIn);
                    BlockPos blockpos = blockpattern$patternhelper.translateOffset(1, 2, 0).getPos();
                    witherentity.setLocationAndAngles((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.55D, (double)blockpos.getZ() + 0.5D, blockpattern$patternhelper.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F, 0.0F);
                    witherentity.renderYawOffset = blockpattern$patternhelper.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F;
                    witherentity.ignite();

                    for (ServerPlayerEntity serverplayerentity : worldIn.getEntitiesWithinAABB(ServerPlayerEntity.class, witherentity.getBoundingBox().grow(50.0D)))
                    {
                        CriteriaTriggers.SUMMONED_ENTITY.trigger(serverplayerentity, witherentity);
                    }

                    worldIn.addEntity(witherentity);

                    for (int k = 0; k < blockpattern.getPalmLength(); ++k)
                    {
                        for (int l = 0; l < blockpattern.getThumbLength(); ++l)
                        {
                            worldIn.func_230547_a_(blockpattern$patternhelper.translateOffset(k, l, 0).getPos(), Blocks.AIR);
                        }
                    }
                }
            }
        }
    }

    public static boolean canSpawnMob(World world, BlockPos pos, ItemStack stack)
    {
        if (stack.getItem() == Items.WITHER_SKELETON_SKULL && pos.getY() >= 2 && world.getDifficulty() != Difficulty.PEACEFUL && !world.isRemote)
        {
            return getOrCreateWitherBase().match(world, pos) != null;
        }
        else
        {
            return false;
        }
    }

    private static BlockPattern getOrCreateWitherFull()
    {
        if (witherPatternFull == null)
        {
            witherPatternFull = BlockPatternBuilder.start().aisle("^^^", "###", "~#~").where('#', (cachedInfo) ->
            {
                return cachedInfo.getBlockState().isIn(BlockTags.WITHER_SUMMON_BASE_BLOCKS);
            }).where('^', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(Blocks.WITHER_SKELETON_SKULL).or(BlockStateMatcher.forBlock(Blocks.WITHER_SKELETON_WALL_SKULL)))).where('~', CachedBlockInfo.hasState(BlockMaterialMatcher.forMaterial(Material.AIR))).build();
        }

        return witherPatternFull;
    }

    private static BlockPattern getOrCreateWitherBase()
    {
        if (witherPatternBase == null)
        {
            witherPatternBase = BlockPatternBuilder.start().aisle("   ", "###", "~#~").where('#', (cachedInfo) ->
            {
                return cachedInfo.getBlockState().isIn(BlockTags.WITHER_SUMMON_BASE_BLOCKS);
            }).where('~', CachedBlockInfo.hasState(BlockMaterialMatcher.forMaterial(Material.AIR))).build();
        }

        return witherPatternBase;
    }
}
