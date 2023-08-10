package net.minecraft.world.gen.feature.structure;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class BuriedTreasure
{
    public static class Piece extends StructurePiece
    {
        public Piece(BlockPos p_i48882_1_)
        {
            super(IStructurePieceType.BTP, 0);
            this.boundingBox = new MutableBoundingBox(p_i48882_1_.getX(), p_i48882_1_.getY(), p_i48882_1_.getZ(), p_i48882_1_.getX(), p_i48882_1_.getY(), p_i48882_1_.getZ());
        }

        public Piece(TemplateManager p_i50677_1_, CompoundNBT p_i50677_2_)
        {
            super(IStructurePieceType.BTP, p_i50677_2_);
        }

        protected void readAdditional(CompoundNBT tagCompound)
        {
        }

        public boolean func_230383_a_(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_)
        {
            int i = p_230383_1_.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, this.boundingBox.minX, this.boundingBox.minZ);
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(this.boundingBox.minX, i, this.boundingBox.minZ);

            while (blockpos$mutable.getY() > 0)
            {
                BlockState blockstate = p_230383_1_.getBlockState(blockpos$mutable);
                BlockState blockstate1 = p_230383_1_.getBlockState(blockpos$mutable.down());

                if (blockstate1 == Blocks.SANDSTONE.getDefaultState() || blockstate1 == Blocks.STONE.getDefaultState() || blockstate1 == Blocks.ANDESITE.getDefaultState() || blockstate1 == Blocks.GRANITE.getDefaultState() || blockstate1 == Blocks.DIORITE.getDefaultState())
                {
                    BlockState blockstate2 = !blockstate.isAir() && !this.func_204295_a(blockstate) ? blockstate : Blocks.SAND.getDefaultState();

                    for (Direction direction : Direction.values())
                    {
                        BlockPos blockpos = blockpos$mutable.offset(direction);
                        BlockState blockstate3 = p_230383_1_.getBlockState(blockpos);

                        if (blockstate3.isAir() || this.func_204295_a(blockstate3))
                        {
                            BlockPos blockpos1 = blockpos.down();
                            BlockState blockstate4 = p_230383_1_.getBlockState(blockpos1);

                            if ((blockstate4.isAir() || this.func_204295_a(blockstate4)) && direction != Direction.UP)
                            {
                                p_230383_1_.setBlockState(blockpos, blockstate1, 3);
                            }
                            else
                            {
                                p_230383_1_.setBlockState(blockpos, blockstate2, 3);
                            }
                        }
                    }

                    this.boundingBox = new MutableBoundingBox(blockpos$mutable.getX(), blockpos$mutable.getY(), blockpos$mutable.getZ(), blockpos$mutable.getX(), blockpos$mutable.getY(), blockpos$mutable.getZ());
                    return this.generateChest(p_230383_1_, p_230383_5_, p_230383_4_, blockpos$mutable, LootTables.CHESTS_BURIED_TREASURE, (BlockState)null);
                }

                blockpos$mutable.move(0, -1, 0);
            }

            return false;
        }

        private boolean func_204295_a(BlockState p_204295_1_)
        {
            return p_204295_1_ == Blocks.WATER.getDefaultState() || p_204295_1_ == Blocks.LAVA.getDefaultState();
        }
    }
}
