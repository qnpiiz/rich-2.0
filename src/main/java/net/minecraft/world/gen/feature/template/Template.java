package net.minecraft.world.gen.feature.template;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.item.PaintingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.IClearable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.shapes.BitSetVoxelShapePart;
import net.minecraft.util.math.shapes.VoxelShapePart;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.EmptyBlockReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class Template
{
    private final List<Template.Palette> blocks = Lists.newArrayList();
    private final List<Template.EntityInfo> entities = Lists.newArrayList();
    private BlockPos size = BlockPos.ZERO;
    private String author = "?";

    public BlockPos getSize()
    {
        return this.size;
    }

    public void setAuthor(String authorIn)
    {
        this.author = authorIn;
    }

    public String getAuthor()
    {
        return this.author;
    }

    /**
     * takes blocks from the world and puts the data them into this template
     */
    public void takeBlocksFromWorld(World worldIn, BlockPos startPos, BlockPos size, boolean takeEntities, @Nullable Block toIgnore)
    {
        if (size.getX() >= 1 && size.getY() >= 1 && size.getZ() >= 1)
        {
            BlockPos blockpos = startPos.add(size).add(-1, -1, -1);
            List<Template.BlockInfo> list = Lists.newArrayList();
            List<Template.BlockInfo> list1 = Lists.newArrayList();
            List<Template.BlockInfo> list2 = Lists.newArrayList();
            BlockPos blockpos1 = new BlockPos(Math.min(startPos.getX(), blockpos.getX()), Math.min(startPos.getY(), blockpos.getY()), Math.min(startPos.getZ(), blockpos.getZ()));
            BlockPos blockpos2 = new BlockPos(Math.max(startPos.getX(), blockpos.getX()), Math.max(startPos.getY(), blockpos.getY()), Math.max(startPos.getZ(), blockpos.getZ()));
            this.size = size;

            for (BlockPos blockpos3 : BlockPos.getAllInBoxMutable(blockpos1, blockpos2))
            {
                BlockPos blockpos4 = blockpos3.subtract(blockpos1);
                BlockState blockstate = worldIn.getBlockState(blockpos3);

                if (toIgnore == null || toIgnore != blockstate.getBlock())
                {
                    TileEntity tileentity = worldIn.getTileEntity(blockpos3);
                    Template.BlockInfo template$blockinfo;

                    if (tileentity != null)
                    {
                        CompoundNBT compoundnbt = tileentity.write(new CompoundNBT());
                        compoundnbt.remove("x");
                        compoundnbt.remove("y");
                        compoundnbt.remove("z");
                        template$blockinfo = new Template.BlockInfo(blockpos4, blockstate, compoundnbt.copy());
                    }
                    else
                    {
                        template$blockinfo = new Template.BlockInfo(blockpos4, blockstate, (CompoundNBT)null);
                    }

                    func_237149_a_(template$blockinfo, list, list1, list2);
                }
            }

            List<Template.BlockInfo> list3 = func_237151_a_(list, list1, list2);
            this.blocks.clear();
            this.blocks.add(new Template.Palette(list3));

            if (takeEntities)
            {
                this.takeEntitiesFromWorld(worldIn, blockpos1, blockpos2.add(1, 1, 1));
            }
            else
            {
                this.entities.clear();
            }
        }
    }

    private static void func_237149_a_(Template.BlockInfo p_237149_0_, List<Template.BlockInfo> p_237149_1_, List<Template.BlockInfo> p_237149_2_, List<Template.BlockInfo> p_237149_3_)
    {
        if (p_237149_0_.nbt != null)
        {
            p_237149_2_.add(p_237149_0_);
        }
        else if (!p_237149_0_.state.getBlock().isVariableOpacity() && p_237149_0_.state.hasOpaqueCollisionShape(EmptyBlockReader.INSTANCE, BlockPos.ZERO))
        {
            p_237149_1_.add(p_237149_0_);
        }
        else
        {
            p_237149_3_.add(p_237149_0_);
        }
    }

    private static List<Template.BlockInfo> func_237151_a_(List<Template.BlockInfo> p_237151_0_, List<Template.BlockInfo> p_237151_1_, List<Template.BlockInfo> p_237151_2_)
    {
        Comparator<Template.BlockInfo> comparator = Comparator.<Template.BlockInfo>comparingInt((p_237154_0_) ->
        {
            return p_237154_0_.pos.getY();
        }).thenComparingInt((p_237153_0_) ->
        {
            return p_237153_0_.pos.getX();
        }).thenComparingInt((p_237148_0_) ->
        {
            return p_237148_0_.pos.getZ();
        });
        p_237151_0_.sort(comparator);
        p_237151_2_.sort(comparator);
        p_237151_1_.sort(comparator);
        List<Template.BlockInfo> list = Lists.newArrayList();
        list.addAll(p_237151_0_);
        list.addAll(p_237151_2_);
        list.addAll(p_237151_1_);
        return list;
    }

    /**
     * takes blocks from the world and puts the data them into this template
     */
    private void takeEntitiesFromWorld(World worldIn, BlockPos startPos, BlockPos endPos)
    {
        List<Entity> list = worldIn.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(startPos, endPos), (p_237142_0_) ->
        {
            return !(p_237142_0_ instanceof PlayerEntity);
        });
        this.entities.clear();

        for (Entity entity : list)
        {
            Vector3d vector3d = new Vector3d(entity.getPosX() - (double)startPos.getX(), entity.getPosY() - (double)startPos.getY(), entity.getPosZ() - (double)startPos.getZ());
            CompoundNBT compoundnbt = new CompoundNBT();
            entity.writeUnlessPassenger(compoundnbt);
            BlockPos blockpos;

            if (entity instanceof PaintingEntity)
            {
                blockpos = ((PaintingEntity)entity).getHangingPosition().subtract(startPos);
            }
            else
            {
                blockpos = new BlockPos(vector3d);
            }

            this.entities.add(new Template.EntityInfo(vector3d, blockpos, compoundnbt.copy()));
        }
    }

    public List<Template.BlockInfo> func_215381_a(BlockPos p_215381_1_, PlacementSettings p_215381_2_, Block p_215381_3_)
    {
        return this.func_215386_a(p_215381_1_, p_215381_2_, p_215381_3_, true);
    }

    public List<Template.BlockInfo> func_215386_a(BlockPos p_215386_1_, PlacementSettings p_215386_2_, Block p_215386_3_, boolean p_215386_4_)
    {
        List<Template.BlockInfo> list = Lists.newArrayList();
        MutableBoundingBox mutableboundingbox = p_215386_2_.getBoundingBox();

        if (this.blocks.isEmpty())
        {
            return Collections.emptyList();
        }
        else
        {
            for (Template.BlockInfo template$blockinfo : p_215386_2_.func_237132_a_(this.blocks, p_215386_1_).func_237158_a_(p_215386_3_))
            {
                BlockPos blockpos = p_215386_4_ ? transformedBlockPos(p_215386_2_, template$blockinfo.pos).add(p_215386_1_) : template$blockinfo.pos;

                if (mutableboundingbox == null || mutableboundingbox.isVecInside(blockpos))
                {
                    list.add(new Template.BlockInfo(blockpos, template$blockinfo.state.rotate(p_215386_2_.getRotation()), template$blockinfo.nbt));
                }
            }

            return list;
        }
    }

    public BlockPos calculateConnectedPos(PlacementSettings placementIn, BlockPos p_186262_2_, PlacementSettings p_186262_3_, BlockPos p_186262_4_)
    {
        BlockPos blockpos = transformedBlockPos(placementIn, p_186262_2_);
        BlockPos blockpos1 = transformedBlockPos(p_186262_3_, p_186262_4_);
        return blockpos.subtract(blockpos1);
    }

    public static BlockPos transformedBlockPos(PlacementSettings placementIn, BlockPos pos)
    {
        return getTransformedPos(pos, placementIn.getMirror(), placementIn.getRotation(), placementIn.getCenterOffset());
    }

    public void func_237144_a_(IServerWorld p_237144_1_, BlockPos p_237144_2_, PlacementSettings p_237144_3_, Random p_237144_4_)
    {
        p_237144_3_.setBoundingBoxFromChunk();
        this.func_237152_b_(p_237144_1_, p_237144_2_, p_237144_3_, p_237144_4_);
    }

    public void func_237152_b_(IServerWorld p_237152_1_, BlockPos p_237152_2_, PlacementSettings p_237152_3_, Random p_237152_4_)
    {
        this.func_237146_a_(p_237152_1_, p_237152_2_, p_237152_2_, p_237152_3_, p_237152_4_, 2);
    }

    public boolean func_237146_a_(IServerWorld p_237146_1_, BlockPos p_237146_2_, BlockPos p_237146_3_, PlacementSettings p_237146_4_, Random p_237146_5_, int p_237146_6_)
    {
        if (this.blocks.isEmpty())
        {
            return false;
        }
        else
        {
            List<Template.BlockInfo> list = p_237146_4_.func_237132_a_(this.blocks, p_237146_2_).func_237157_a_();

            if ((!list.isEmpty() || !p_237146_4_.getIgnoreEntities() && !this.entities.isEmpty()) && this.size.getX() >= 1 && this.size.getY() >= 1 && this.size.getZ() >= 1)
            {
                MutableBoundingBox mutableboundingbox = p_237146_4_.getBoundingBox();
                List<BlockPos> list1 = Lists.newArrayListWithCapacity(p_237146_4_.func_204763_l() ? list.size() : 0);
                List<Pair<BlockPos, CompoundNBT>> list2 = Lists.newArrayListWithCapacity(list.size());
                int i = Integer.MAX_VALUE;
                int j = Integer.MAX_VALUE;
                int k = Integer.MAX_VALUE;
                int l = Integer.MIN_VALUE;
                int i1 = Integer.MIN_VALUE;
                int j1 = Integer.MIN_VALUE;

                for (Template.BlockInfo template$blockinfo : func_237145_a_(p_237146_1_, p_237146_2_, p_237146_3_, p_237146_4_, list))
                {
                    BlockPos blockpos = template$blockinfo.pos;

                    if (mutableboundingbox == null || mutableboundingbox.isVecInside(blockpos))
                    {
                        FluidState fluidstate = p_237146_4_.func_204763_l() ? p_237146_1_.getFluidState(blockpos) : null;
                        BlockState blockstate = template$blockinfo.state.mirror(p_237146_4_.getMirror()).rotate(p_237146_4_.getRotation());

                        if (template$blockinfo.nbt != null)
                        {
                            TileEntity tileentity = p_237146_1_.getTileEntity(blockpos);
                            IClearable.clearObj(tileentity);
                            p_237146_1_.setBlockState(blockpos, Blocks.BARRIER.getDefaultState(), 20);
                        }

                        if (p_237146_1_.setBlockState(blockpos, blockstate, p_237146_6_))
                        {
                            i = Math.min(i, blockpos.getX());
                            j = Math.min(j, blockpos.getY());
                            k = Math.min(k, blockpos.getZ());
                            l = Math.max(l, blockpos.getX());
                            i1 = Math.max(i1, blockpos.getY());
                            j1 = Math.max(j1, blockpos.getZ());
                            list2.add(Pair.of(blockpos, template$blockinfo.nbt));

                            if (template$blockinfo.nbt != null)
                            {
                                TileEntity tileentity1 = p_237146_1_.getTileEntity(blockpos);

                                if (tileentity1 != null)
                                {
                                    template$blockinfo.nbt.putInt("x", blockpos.getX());
                                    template$blockinfo.nbt.putInt("y", blockpos.getY());
                                    template$blockinfo.nbt.putInt("z", blockpos.getZ());

                                    if (tileentity1 instanceof LockableLootTileEntity)
                                    {
                                        template$blockinfo.nbt.putLong("LootTableSeed", p_237146_5_.nextLong());
                                    }

                                    tileentity1.read(template$blockinfo.state, template$blockinfo.nbt);
                                    tileentity1.mirror(p_237146_4_.getMirror());
                                    tileentity1.rotate(p_237146_4_.getRotation());
                                }
                            }

                            if (fluidstate != null && blockstate.getBlock() instanceof ILiquidContainer)
                            {
                                ((ILiquidContainer)blockstate.getBlock()).receiveFluid(p_237146_1_, blockpos, blockstate, fluidstate);

                                if (!fluidstate.isSource())
                                {
                                    list1.add(blockpos);
                                }
                            }
                        }
                    }
                }

                boolean flag = true;
                Direction[] adirection = new Direction[] {Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

                while (flag && !list1.isEmpty())
                {
                    flag = false;
                    Iterator<BlockPos> iterator = list1.iterator();

                    while (iterator.hasNext())
                    {
                        BlockPos blockpos2 = iterator.next();
                        BlockPos blockpos3 = blockpos2;
                        FluidState fluidstate2 = p_237146_1_.getFluidState(blockpos2);

                        for (int k1 = 0; k1 < adirection.length && !fluidstate2.isSource(); ++k1)
                        {
                            BlockPos blockpos1 = blockpos3.offset(adirection[k1]);
                            FluidState fluidstate1 = p_237146_1_.getFluidState(blockpos1);

                            if (fluidstate1.getActualHeight(p_237146_1_, blockpos1) > fluidstate2.getActualHeight(p_237146_1_, blockpos3) || fluidstate1.isSource() && !fluidstate2.isSource())
                            {
                                fluidstate2 = fluidstate1;
                                blockpos3 = blockpos1;
                            }
                        }

                        if (fluidstate2.isSource())
                        {
                            BlockState blockstate2 = p_237146_1_.getBlockState(blockpos2);
                            Block block = blockstate2.getBlock();

                            if (block instanceof ILiquidContainer)
                            {
                                ((ILiquidContainer)block).receiveFluid(p_237146_1_, blockpos2, blockstate2, fluidstate2);
                                flag = true;
                                iterator.remove();
                            }
                        }
                    }
                }

                if (i <= l)
                {
                    if (!p_237146_4_.func_215218_i())
                    {
                        VoxelShapePart voxelshapepart = new BitSetVoxelShapePart(l - i + 1, i1 - j + 1, j1 - k + 1);
                        int l1 = i;
                        int i2 = j;
                        int j2 = k;

                        for (Pair<BlockPos, CompoundNBT> pair1 : list2)
                        {
                            BlockPos blockpos5 = pair1.getFirst();
                            voxelshapepart.setFilled(blockpos5.getX() - l1, blockpos5.getY() - i2, blockpos5.getZ() - j2, true, true);
                        }

                        func_222857_a(p_237146_1_, p_237146_6_, voxelshapepart, l1, i2, j2);
                    }

                    for (Pair<BlockPos, CompoundNBT> pair : list2)
                    {
                        BlockPos blockpos4 = pair.getFirst();

                        if (!p_237146_4_.func_215218_i())
                        {
                            BlockState blockstate1 = p_237146_1_.getBlockState(blockpos4);
                            BlockState blockstate3 = Block.getValidBlockForPosition(blockstate1, p_237146_1_, blockpos4);

                            if (blockstate1 != blockstate3)
                            {
                                p_237146_1_.setBlockState(blockpos4, blockstate3, p_237146_6_ & -2 | 16);
                            }

                            p_237146_1_.func_230547_a_(blockpos4, blockstate3.getBlock());
                        }

                        if (pair.getSecond() != null)
                        {
                            TileEntity tileentity2 = p_237146_1_.getTileEntity(blockpos4);

                            if (tileentity2 != null)
                            {
                                tileentity2.markDirty();
                            }
                        }
                    }
                }

                if (!p_237146_4_.getIgnoreEntities())
                {
                    this.func_237143_a_(p_237146_1_, p_237146_2_, p_237146_4_.getMirror(), p_237146_4_.getRotation(), p_237146_4_.getCenterOffset(), mutableboundingbox, p_237146_4_.func_237134_m_());
                }

                return true;
            }
            else
            {
                return false;
            }
        }
    }

    public static void func_222857_a(IWorld worldIn, int p_222857_1_, VoxelShapePart voxelShapePartIn, int xIn, int yIn, int zIn)
    {
        voxelShapePartIn.forEachFace((p_237141_5_, p_237141_6_, p_237141_7_, p_237141_8_) ->
        {
            BlockPos blockpos = new BlockPos(xIn + p_237141_6_, yIn + p_237141_7_, zIn + p_237141_8_);
            BlockPos blockpos1 = blockpos.offset(p_237141_5_);
            BlockState blockstate = worldIn.getBlockState(blockpos);
            BlockState blockstate1 = worldIn.getBlockState(blockpos1);
            BlockState blockstate2 = blockstate.updatePostPlacement(p_237141_5_, blockstate1, worldIn, blockpos, blockpos1);

            if (blockstate != blockstate2)
            {
                worldIn.setBlockState(blockpos, blockstate2, p_222857_1_ & -2);
            }

            BlockState blockstate3 = blockstate1.updatePostPlacement(p_237141_5_.getOpposite(), blockstate2, worldIn, blockpos1, blockpos);

            if (blockstate1 != blockstate3)
            {
                worldIn.setBlockState(blockpos1, blockstate3, p_222857_1_ & -2);
            }
        });
    }

    public static List<Template.BlockInfo> func_237145_a_(IWorld p_237145_0_, BlockPos p_237145_1_, BlockPos p_237145_2_, PlacementSettings p_237145_3_, List<Template.BlockInfo> p_237145_4_)
    {
        List<Template.BlockInfo> list = Lists.newArrayList();

        for (Template.BlockInfo template$blockinfo : p_237145_4_)
        {
            BlockPos blockpos = transformedBlockPos(p_237145_3_, template$blockinfo.pos).add(p_237145_1_);
            Template.BlockInfo template$blockinfo1 = new Template.BlockInfo(blockpos, template$blockinfo.state, template$blockinfo.nbt != null ? template$blockinfo.nbt.copy() : null);

            for (Iterator<StructureProcessor> iterator = p_237145_3_.getProcessors().iterator(); template$blockinfo1 != null && iterator.hasNext(); template$blockinfo1 = iterator.next().func_230386_a_(p_237145_0_, p_237145_1_, p_237145_2_, template$blockinfo, template$blockinfo1, p_237145_3_))
            {
            }

            if (template$blockinfo1 != null)
            {
                list.add(template$blockinfo1);
            }
        }

        return list;
    }

    private void func_237143_a_(IServerWorld p_237143_1_, BlockPos p_237143_2_, Mirror p_237143_3_, Rotation p_237143_4_, BlockPos p_237143_5_, @Nullable MutableBoundingBox p_237143_6_, boolean p_237143_7_)
    {
        for (Template.EntityInfo template$entityinfo : this.entities)
        {
            BlockPos blockpos = getTransformedPos(template$entityinfo.blockPos, p_237143_3_, p_237143_4_, p_237143_5_).add(p_237143_2_);

            if (p_237143_6_ == null || p_237143_6_.isVecInside(blockpos))
            {
                CompoundNBT compoundnbt = template$entityinfo.nbt.copy();
                Vector3d vector3d = getTransformedPos(template$entityinfo.pos, p_237143_3_, p_237143_4_, p_237143_5_);
                Vector3d vector3d1 = vector3d.add((double)p_237143_2_.getX(), (double)p_237143_2_.getY(), (double)p_237143_2_.getZ());
                ListNBT listnbt = new ListNBT();
                listnbt.add(DoubleNBT.valueOf(vector3d1.x));
                listnbt.add(DoubleNBT.valueOf(vector3d1.y));
                listnbt.add(DoubleNBT.valueOf(vector3d1.z));
                compoundnbt.put("Pos", listnbt);
                compoundnbt.remove("UUID");
                loadEntity(p_237143_1_, compoundnbt).ifPresent((p_242927_6_) ->
                {
                    float f = p_242927_6_.getMirroredYaw(p_237143_3_);
                    f = f + (p_242927_6_.rotationYaw - p_242927_6_.getRotatedYaw(p_237143_4_));
                    p_242927_6_.setLocationAndAngles(vector3d1.x, vector3d1.y, vector3d1.z, f, p_242927_6_.rotationPitch);

                    if (p_237143_7_ && p_242927_6_ instanceof MobEntity)
                    {
                        ((MobEntity)p_242927_6_).onInitialSpawn(p_237143_1_, p_237143_1_.getDifficultyForLocation(new BlockPos(vector3d1)), SpawnReason.STRUCTURE, (ILivingEntityData)null, compoundnbt);
                    }

                    p_237143_1_.func_242417_l(p_242927_6_);
                });
            }
        }
    }

    private static Optional<Entity> loadEntity(IServerWorld worldIn, CompoundNBT nbt)
    {
        try
        {
            return EntityType.loadEntityUnchecked(nbt, worldIn.getWorld());
        }
        catch (Exception exception)
        {
            return Optional.empty();
        }
    }

    public BlockPos transformedSize(Rotation rotationIn)
    {
        switch (rotationIn)
        {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                return new BlockPos(this.size.getZ(), this.size.getY(), this.size.getX());

            default:
                return this.size;
        }
    }

    public static BlockPos getTransformedPos(BlockPos targetPos, Mirror mirrorIn, Rotation rotationIn, BlockPos offset)
    {
        int i = targetPos.getX();
        int j = targetPos.getY();
        int k = targetPos.getZ();
        boolean flag = true;

        switch (mirrorIn)
        {
            case LEFT_RIGHT:
                k = -k;
                break;

            case FRONT_BACK:
                i = -i;
                break;

            default:
                flag = false;
        }

        int l = offset.getX();
        int i1 = offset.getZ();

        switch (rotationIn)
        {
            case COUNTERCLOCKWISE_90:
                return new BlockPos(l - i1 + k, j, l + i1 - i);

            case CLOCKWISE_90:
                return new BlockPos(l + i1 - k, j, i1 - l + i);

            case CLOCKWISE_180:
                return new BlockPos(l + l - i, j, i1 + i1 - k);

            default:
                return flag ? new BlockPos(i, j, k) : targetPos;
        }
    }

    public static Vector3d getTransformedPos(Vector3d target, Mirror mirrorIn, Rotation rotationIn, BlockPos centerOffset)
    {
        double d0 = target.x;
        double d1 = target.y;
        double d2 = target.z;
        boolean flag = true;

        switch (mirrorIn)
        {
            case LEFT_RIGHT:
                d2 = 1.0D - d2;
                break;

            case FRONT_BACK:
                d0 = 1.0D - d0;
                break;

            default:
                flag = false;
        }

        int i = centerOffset.getX();
        int j = centerOffset.getZ();

        switch (rotationIn)
        {
            case COUNTERCLOCKWISE_90:
                return new Vector3d((double)(i - j) + d2, d1, (double)(i + j + 1) - d0);

            case CLOCKWISE_90:
                return new Vector3d((double)(i + j + 1) - d2, d1, (double)(j - i) + d0);

            case CLOCKWISE_180:
                return new Vector3d((double)(i + i + 1) - d0, d1, (double)(j + j + 1) - d2);

            default:
                return flag ? new Vector3d(d0, d1, d2) : target;
        }
    }

    public BlockPos getZeroPositionWithTransform(BlockPos p_189961_1_, Mirror p_189961_2_, Rotation p_189961_3_)
    {
        return getZeroPositionWithTransform(p_189961_1_, p_189961_2_, p_189961_3_, this.getSize().getX(), this.getSize().getZ());
    }

    public static BlockPos getZeroPositionWithTransform(BlockPos p_191157_0_, Mirror p_191157_1_, Rotation p_191157_2_, int p_191157_3_, int p_191157_4_)
    {
        --p_191157_3_;
        --p_191157_4_;
        int i = p_191157_1_ == Mirror.FRONT_BACK ? p_191157_3_ : 0;
        int j = p_191157_1_ == Mirror.LEFT_RIGHT ? p_191157_4_ : 0;
        BlockPos blockpos = p_191157_0_;

        switch (p_191157_2_)
        {
            case COUNTERCLOCKWISE_90:
                blockpos = p_191157_0_.add(j, 0, p_191157_3_ - i);
                break;

            case CLOCKWISE_90:
                blockpos = p_191157_0_.add(p_191157_4_ - j, 0, i);
                break;

            case CLOCKWISE_180:
                blockpos = p_191157_0_.add(p_191157_3_ - i, 0, p_191157_4_ - j);
                break;

            case NONE:
                blockpos = p_191157_0_.add(i, 0, j);
        }

        return blockpos;
    }

    public MutableBoundingBox getMutableBoundingBox(PlacementSettings p_215388_1_, BlockPos p_215388_2_)
    {
        return this.func_237150_a_(p_215388_2_, p_215388_1_.getRotation(), p_215388_1_.getCenterOffset(), p_215388_1_.getMirror());
    }

    public MutableBoundingBox func_237150_a_(BlockPos p_237150_1_, Rotation p_237150_2_, BlockPos p_237150_3_, Mirror p_237150_4_)
    {
        BlockPos blockpos = this.transformedSize(p_237150_2_);
        int i = p_237150_3_.getX();
        int j = p_237150_3_.getZ();
        int k = blockpos.getX() - 1;
        int l = blockpos.getY() - 1;
        int i1 = blockpos.getZ() - 1;
        MutableBoundingBox mutableboundingbox = new MutableBoundingBox(0, 0, 0, 0, 0, 0);

        switch (p_237150_2_)
        {
            case COUNTERCLOCKWISE_90:
                mutableboundingbox = new MutableBoundingBox(i - j, 0, i + j - i1, i - j + k, l, i + j);
                break;

            case CLOCKWISE_90:
                mutableboundingbox = new MutableBoundingBox(i + j - k, 0, j - i, i + j, l, j - i + i1);
                break;

            case CLOCKWISE_180:
                mutableboundingbox = new MutableBoundingBox(i + i - k, 0, j + j - i1, i + i, l, j + j);
                break;

            case NONE:
                mutableboundingbox = new MutableBoundingBox(0, 0, 0, k, l, i1);
        }

        switch (p_237150_4_)
        {
            case LEFT_RIGHT:
                this.func_215385_a(p_237150_2_, i1, k, mutableboundingbox, Direction.NORTH, Direction.SOUTH);
                break;

            case FRONT_BACK:
                this.func_215385_a(p_237150_2_, k, i1, mutableboundingbox, Direction.WEST, Direction.EAST);

            case NONE:
        }

        mutableboundingbox.offset(p_237150_1_.getX(), p_237150_1_.getY(), p_237150_1_.getZ());
        return mutableboundingbox;
    }

    private void func_215385_a(Rotation rotationIn, int offsetFront, int p_215385_3_, MutableBoundingBox p_215385_4_, Direction p_215385_5_, Direction p_215385_6_)
    {
        BlockPos blockpos = BlockPos.ZERO;

        if (rotationIn != Rotation.CLOCKWISE_90 && rotationIn != Rotation.COUNTERCLOCKWISE_90)
        {
            if (rotationIn == Rotation.CLOCKWISE_180)
            {
                blockpos = blockpos.offset(p_215385_6_, offsetFront);
            }
            else
            {
                blockpos = blockpos.offset(p_215385_5_, offsetFront);
            }
        }
        else
        {
            blockpos = blockpos.offset(rotationIn.rotate(p_215385_5_), p_215385_3_);
        }

        p_215385_4_.offset(blockpos.getX(), 0, blockpos.getZ());
    }

    public CompoundNBT writeToNBT(CompoundNBT nbt)
    {
        if (this.blocks.isEmpty())
        {
            nbt.put("blocks", new ListNBT());
            nbt.put("palette", new ListNBT());
        }
        else
        {
            List<Template.BasicPalette> list = Lists.newArrayList();
            Template.BasicPalette template$basicpalette = new Template.BasicPalette();
            list.add(template$basicpalette);

            for (int i = 1; i < this.blocks.size(); ++i)
            {
                list.add(new Template.BasicPalette());
            }

            ListNBT listnbt1 = new ListNBT();
            List<Template.BlockInfo> list1 = this.blocks.get(0).func_237157_a_();

            for (int j = 0; j < list1.size(); ++j)
            {
                Template.BlockInfo template$blockinfo = list1.get(j);
                CompoundNBT compoundnbt = new CompoundNBT();
                compoundnbt.put("pos", this.writeInts(template$blockinfo.pos.getX(), template$blockinfo.pos.getY(), template$blockinfo.pos.getZ()));
                int k = template$basicpalette.idFor(template$blockinfo.state);
                compoundnbt.putInt("state", k);

                if (template$blockinfo.nbt != null)
                {
                    compoundnbt.put("nbt", template$blockinfo.nbt);
                }

                listnbt1.add(compoundnbt);

                for (int l = 1; l < this.blocks.size(); ++l)
                {
                    Template.BasicPalette template$basicpalette1 = list.get(l);
                    template$basicpalette1.addMapping((this.blocks.get(l).func_237157_a_().get(j)).state, k);
                }
            }

            nbt.put("blocks", listnbt1);

            if (list.size() == 1)
            {
                ListNBT listnbt2 = new ListNBT();

                for (BlockState blockstate : template$basicpalette)
                {
                    listnbt2.add(NBTUtil.writeBlockState(blockstate));
                }

                nbt.put("palette", listnbt2);
            }
            else
            {
                ListNBT listnbt3 = new ListNBT();

                for (Template.BasicPalette template$basicpalette2 : list)
                {
                    ListNBT listnbt4 = new ListNBT();

                    for (BlockState blockstate1 : template$basicpalette2)
                    {
                        listnbt4.add(NBTUtil.writeBlockState(blockstate1));
                    }

                    listnbt3.add(listnbt4);
                }

                nbt.put("palettes", listnbt3);
            }
        }

        ListNBT listnbt = new ListNBT();

        for (Template.EntityInfo template$entityinfo : this.entities)
        {
            CompoundNBT compoundnbt1 = new CompoundNBT();
            compoundnbt1.put("pos", this.writeDoubles(template$entityinfo.pos.x, template$entityinfo.pos.y, template$entityinfo.pos.z));
            compoundnbt1.put("blockPos", this.writeInts(template$entityinfo.blockPos.getX(), template$entityinfo.blockPos.getY(), template$entityinfo.blockPos.getZ()));

            if (template$entityinfo.nbt != null)
            {
                compoundnbt1.put("nbt", template$entityinfo.nbt);
            }

            listnbt.add(compoundnbt1);
        }

        nbt.put("entities", listnbt);
        nbt.put("size", this.writeInts(this.size.getX(), this.size.getY(), this.size.getZ()));
        nbt.putInt("DataVersion", SharedConstants.getVersion().getWorldVersion());
        return nbt;
    }

    public void read(CompoundNBT compound)
    {
        this.blocks.clear();
        this.entities.clear();
        ListNBT listnbt = compound.getList("size", 3);
        this.size = new BlockPos(listnbt.getInt(0), listnbt.getInt(1), listnbt.getInt(2));
        ListNBT listnbt1 = compound.getList("blocks", 10);

        if (compound.contains("palettes", 9))
        {
            ListNBT listnbt2 = compound.getList("palettes", 9);

            for (int i = 0; i < listnbt2.size(); ++i)
            {
                this.readPalletesAndBlocks(listnbt2.getList(i), listnbt1);
            }
        }
        else
        {
            this.readPalletesAndBlocks(compound.getList("palette", 10), listnbt1);
        }

        ListNBT listnbt5 = compound.getList("entities", 10);

        for (int j = 0; j < listnbt5.size(); ++j)
        {
            CompoundNBT compoundnbt = listnbt5.getCompound(j);
            ListNBT listnbt3 = compoundnbt.getList("pos", 6);
            Vector3d vector3d = new Vector3d(listnbt3.getDouble(0), listnbt3.getDouble(1), listnbt3.getDouble(2));
            ListNBT listnbt4 = compoundnbt.getList("blockPos", 3);
            BlockPos blockpos = new BlockPos(listnbt4.getInt(0), listnbt4.getInt(1), listnbt4.getInt(2));

            if (compoundnbt.contains("nbt"))
            {
                CompoundNBT compoundnbt1 = compoundnbt.getCompound("nbt");
                this.entities.add(new Template.EntityInfo(vector3d, blockpos, compoundnbt1));
            }
        }
    }

    private void readPalletesAndBlocks(ListNBT palletesNBT, ListNBT blocksNBT)
    {
        Template.BasicPalette template$basicpalette = new Template.BasicPalette();

        for (int i = 0; i < palletesNBT.size(); ++i)
        {
            template$basicpalette.addMapping(NBTUtil.readBlockState(palletesNBT.getCompound(i)), i);
        }

        List<Template.BlockInfo> list2 = Lists.newArrayList();
        List<Template.BlockInfo> list = Lists.newArrayList();
        List<Template.BlockInfo> list1 = Lists.newArrayList();

        for (int j = 0; j < blocksNBT.size(); ++j)
        {
            CompoundNBT compoundnbt = blocksNBT.getCompound(j);
            ListNBT listnbt = compoundnbt.getList("pos", 3);
            BlockPos blockpos = new BlockPos(listnbt.getInt(0), listnbt.getInt(1), listnbt.getInt(2));
            BlockState blockstate = template$basicpalette.stateFor(compoundnbt.getInt("state"));
            CompoundNBT compoundnbt1;

            if (compoundnbt.contains("nbt"))
            {
                compoundnbt1 = compoundnbt.getCompound("nbt");
            }
            else
            {
                compoundnbt1 = null;
            }

            Template.BlockInfo template$blockinfo = new Template.BlockInfo(blockpos, blockstate, compoundnbt1);
            func_237149_a_(template$blockinfo, list2, list, list1);
        }

        List<Template.BlockInfo> list3 = func_237151_a_(list2, list, list1);
        this.blocks.add(new Template.Palette(list3));
    }

    private ListNBT writeInts(int... values)
    {
        ListNBT listnbt = new ListNBT();

        for (int i : values)
        {
            listnbt.add(IntNBT.valueOf(i));
        }

        return listnbt;
    }

    private ListNBT writeDoubles(double... values)
    {
        ListNBT listnbt = new ListNBT();

        for (double d0 : values)
        {
            listnbt.add(DoubleNBT.valueOf(d0));
        }

        return listnbt;
    }

    static class BasicPalette implements Iterable<BlockState>
    {
        public static final BlockState DEFAULT_BLOCK_STATE = Blocks.AIR.getDefaultState();
        private final ObjectIntIdentityMap<BlockState> ids = new ObjectIntIdentityMap<>(16);
        private int lastId;

        private BasicPalette()
        {
        }

        public int idFor(BlockState state)
        {
            int i = this.ids.getId(state);

            if (i == -1)
            {
                i = this.lastId++;
                this.ids.put(state, i);
            }

            return i;
        }

        @Nullable
        public BlockState stateFor(int id)
        {
            BlockState blockstate = this.ids.getByValue(id);
            return blockstate == null ? DEFAULT_BLOCK_STATE : blockstate;
        }

        public Iterator<BlockState> iterator()
        {
            return this.ids.iterator();
        }

        public void addMapping(BlockState p_189956_1_, int p_189956_2_)
        {
            this.ids.put(p_189956_1_, p_189956_2_);
        }
    }

    public static class BlockInfo
    {
        public final BlockPos pos;
        public final BlockState state;
        public final CompoundNBT nbt;

        public BlockInfo(BlockPos pos, BlockState state, @Nullable CompoundNBT nbt)
        {
            this.pos = pos;
            this.state = state;
            this.nbt = nbt;
        }

        public String toString()
        {
            return String.format("<StructureBlockInfo | %s | %s | %s>", this.pos, this.state, this.nbt);
        }
    }

    public static class EntityInfo
    {
        public final Vector3d pos;
        public final BlockPos blockPos;
        public final CompoundNBT nbt;

        public EntityInfo(Vector3d vecIn, BlockPos posIn, CompoundNBT nbt)
        {
            this.pos = vecIn;
            this.blockPos = posIn;
            this.nbt = nbt;
        }
    }

    public static final class Palette
    {
        private final List<Template.BlockInfo> field_237155_a_;
        private final Map<Block, List<Template.BlockInfo>> field_237156_b_ = Maps.newHashMap();

        private Palette(List<Template.BlockInfo> p_i232120_1_)
        {
            this.field_237155_a_ = p_i232120_1_;
        }

        public List<Template.BlockInfo> func_237157_a_()
        {
            return this.field_237155_a_;
        }

        public List<Template.BlockInfo> func_237158_a_(Block p_237158_1_)
        {
            return this.field_237156_b_.computeIfAbsent(p_237158_1_, (p_237160_1_) ->
            {
                return this.field_237155_a_.stream().filter((p_237159_1_) -> {
                    return p_237159_1_.state.isIn(p_237160_1_);
                }).collect(Collectors.toList());
            });
        }
    }
}
