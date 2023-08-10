package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootTables;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DungeonsFeature extends Feature<NoFeatureConfig>
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final EntityType<?>[] SPAWNERTYPES = new EntityType[] {EntityType.SKELETON, EntityType.ZOMBIE, EntityType.ZOMBIE, EntityType.SPIDER};
    private static final BlockState CAVE_AIR = Blocks.CAVE_AIR.getDefaultState();

    public DungeonsFeature(Codec<NoFeatureConfig> p_i231970_1_)
    {
        super(p_i231970_1_);
    }

    public boolean func_241855_a(ISeedReader p_241855_1_, ChunkGenerator p_241855_2_, Random p_241855_3_, BlockPos p_241855_4_, NoFeatureConfig p_241855_5_)
    {
        int i = 3;
        int j = p_241855_3_.nextInt(2) + 2;
        int k = -j - 1;
        int l = j + 1;
        int i1 = -1;
        int j1 = 4;
        int k1 = p_241855_3_.nextInt(2) + 2;
        int l1 = -k1 - 1;
        int i2 = k1 + 1;
        int j2 = 0;

        for (int k2 = k; k2 <= l; ++k2)
        {
            for (int l2 = -1; l2 <= 4; ++l2)
            {
                for (int i3 = l1; i3 <= i2; ++i3)
                {
                    BlockPos blockpos = p_241855_4_.add(k2, l2, i3);
                    Material material = p_241855_1_.getBlockState(blockpos).getMaterial();
                    boolean flag = material.isSolid();

                    if (l2 == -1 && !flag)
                    {
                        return false;
                    }

                    if (l2 == 4 && !flag)
                    {
                        return false;
                    }

                    if ((k2 == k || k2 == l || i3 == l1 || i3 == i2) && l2 == 0 && p_241855_1_.isAirBlock(blockpos) && p_241855_1_.isAirBlock(blockpos.up()))
                    {
                        ++j2;
                    }
                }
            }
        }

        if (j2 >= 1 && j2 <= 5)
        {
            for (int k3 = k; k3 <= l; ++k3)
            {
                for (int i4 = 3; i4 >= -1; --i4)
                {
                    for (int k4 = l1; k4 <= i2; ++k4)
                    {
                        BlockPos blockpos1 = p_241855_4_.add(k3, i4, k4);
                        BlockState blockstate = p_241855_1_.getBlockState(blockpos1);

                        if (k3 != k && i4 != -1 && k4 != l1 && k3 != l && i4 != 4 && k4 != i2)
                        {
                            if (!blockstate.isIn(Blocks.CHEST) && !blockstate.isIn(Blocks.SPAWNER))
                            {
                                p_241855_1_.setBlockState(blockpos1, CAVE_AIR, 2);
                            }
                        }
                        else if (blockpos1.getY() >= 0 && !p_241855_1_.getBlockState(blockpos1.down()).getMaterial().isSolid())
                        {
                            p_241855_1_.setBlockState(blockpos1, CAVE_AIR, 2);
                        }
                        else if (blockstate.getMaterial().isSolid() && !blockstate.isIn(Blocks.CHEST))
                        {
                            if (i4 == -1 && p_241855_3_.nextInt(4) != 0)
                            {
                                p_241855_1_.setBlockState(blockpos1, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 2);
                            }
                            else
                            {
                                p_241855_1_.setBlockState(blockpos1, Blocks.COBBLESTONE.getDefaultState(), 2);
                            }
                        }
                    }
                }
            }

            for (int l3 = 0; l3 < 2; ++l3)
            {
                for (int j4 = 0; j4 < 3; ++j4)
                {
                    int l4 = p_241855_4_.getX() + p_241855_3_.nextInt(j * 2 + 1) - j;
                    int i5 = p_241855_4_.getY();
                    int j5 = p_241855_4_.getZ() + p_241855_3_.nextInt(k1 * 2 + 1) - k1;
                    BlockPos blockpos2 = new BlockPos(l4, i5, j5);

                    if (p_241855_1_.isAirBlock(blockpos2))
                    {
                        int j3 = 0;

                        for (Direction direction : Direction.Plane.HORIZONTAL)
                        {
                            if (p_241855_1_.getBlockState(blockpos2.offset(direction)).getMaterial().isSolid())
                            {
                                ++j3;
                            }
                        }

                        if (j3 == 1)
                        {
                            p_241855_1_.setBlockState(blockpos2, StructurePiece.correctFacing(p_241855_1_, blockpos2, Blocks.CHEST.getDefaultState()), 2);
                            LockableLootTileEntity.setLootTable(p_241855_1_, p_241855_3_, blockpos2, LootTables.CHESTS_SIMPLE_DUNGEON);
                            break;
                        }
                    }
                }
            }

            p_241855_1_.setBlockState(p_241855_4_, Blocks.SPAWNER.getDefaultState(), 2);
            TileEntity tileentity = p_241855_1_.getTileEntity(p_241855_4_);

            if (tileentity instanceof MobSpawnerTileEntity)
            {
                ((MobSpawnerTileEntity)tileentity).getSpawnerBaseLogic().setEntityType(this.getRandomDungeonMob(p_241855_3_));
            }
            else
            {
                LOGGER.error("Failed to fetch mob spawner entity at ({}, {}, {})", p_241855_4_.getX(), p_241855_4_.getY(), p_241855_4_.getZ());
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    private EntityType<?> getRandomDungeonMob(Random rand)
    {
        return Util.getRandomObject(SPAWNERTYPES, rand);
    }
}
