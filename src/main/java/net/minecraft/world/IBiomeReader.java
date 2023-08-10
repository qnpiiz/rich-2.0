package net.minecraft.world;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IWorldGenerationReader;

public interface IBiomeReader extends IEntityReader, IWorldReader, IWorldGenerationReader
{
default Stream<VoxelShape> func_230318_c_(@Nullable Entity p_230318_1_, AxisAlignedBB p_230318_2_, Predicate<Entity> p_230318_3_)
    {
        return IEntityReader.super.func_230318_c_(p_230318_1_, p_230318_2_, p_230318_3_);
    }

default boolean checkNoEntityCollision(@Nullable Entity entityIn, VoxelShape shape)
    {
        return IEntityReader.super.checkNoEntityCollision(entityIn, shape);
    }

default BlockPos getHeight(Heightmap.Type heightmapType, BlockPos pos)
    {
        return IWorldReader.super.getHeight(heightmapType, pos);
    }

    DynamicRegistries func_241828_r();

default Optional<RegistryKey<Biome>> func_242406_i(BlockPos p_242406_1_)
    {
        return this.func_241828_r().getRegistry(Registry.BIOME_KEY).getOptionalKey(this.getBiome(p_242406_1_));
    }
}
