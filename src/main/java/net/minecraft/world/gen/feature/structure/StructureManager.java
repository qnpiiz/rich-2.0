package net.minecraft.world.gen.feature.structure;

import com.mojang.datafixers.DataFixUtils;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.IStructureReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;

public class StructureManager
{
    private final IWorld field_235003_a_;
    private final DimensionGeneratorSettings field_235004_b_;

    public StructureManager(IWorld p_i231626_1_, DimensionGeneratorSettings p_i231626_2_)
    {
        this.field_235003_a_ = p_i231626_1_;
        this.field_235004_b_ = p_i231626_2_;
    }

    public StructureManager func_241464_a_(WorldGenRegion p_241464_1_)
    {
        if (p_241464_1_.getWorld() != this.field_235003_a_)
        {
            throw new IllegalStateException("Using invalid feature manager (source level: " + p_241464_1_.getWorld() + ", region: " + p_241464_1_);
        }
        else
        {
            return new StructureManager(p_241464_1_, this.field_235004_b_);
        }
    }

    public Stream <? extends StructureStart<? >> func_235011_a_(SectionPos p_235011_1_, Structure<?> p_235011_2_)
    {
        return this.field_235003_a_.getChunk(p_235011_1_.getSectionX(), p_235011_1_.getSectionZ(), ChunkStatus.STRUCTURE_REFERENCES).func_230346_b_(p_235011_2_).stream().map((p_235015_0_) ->
        {
            return SectionPos.from(new ChunkPos(p_235015_0_), 0);
        }).map((p_235006_2_) ->
        {
            return this.func_235013_a_(p_235006_2_, p_235011_2_, this.field_235003_a_.getChunk(p_235006_2_.getSectionX(), p_235006_2_.getSectionZ(), ChunkStatus.STRUCTURE_STARTS));
        }).filter((p_235007_0_) ->
        {
            return p_235007_0_ != null && p_235007_0_.isValid();
        });
    }

    @Nullable
    public StructureStart<?> func_235013_a_(SectionPos p_235013_1_, Structure<?> p_235013_2_, IStructureReader p_235013_3_)
    {
        return p_235013_3_.func_230342_a_(p_235013_2_);
    }

    public void func_235014_a_(SectionPos p_235014_1_, Structure<?> p_235014_2_, StructureStart<?> p_235014_3_, IStructureReader p_235014_4_)
    {
        p_235014_4_.func_230344_a_(p_235014_2_, p_235014_3_);
    }

    public void func_235012_a_(SectionPos p_235012_1_, Structure<?> p_235012_2_, long p_235012_3_, IStructureReader p_235012_5_)
    {
        p_235012_5_.func_230343_a_(p_235012_2_, p_235012_3_);
    }

    public boolean func_235005_a_()
    {
        return this.field_235004_b_.doesGenerateFeatures();
    }

    public StructureStart<?> func_235010_a_(BlockPos p_235010_1_, boolean p_235010_2_, Structure<?> p_235010_3_)
    {
        return DataFixUtils.orElse(this.func_235011_a_(SectionPos.from(p_235010_1_), p_235010_3_).filter((p_235009_1_) ->
        {
            return p_235009_1_.getBoundingBox().isVecInside(p_235010_1_);
        }).filter((p_235016_2_) ->
        {
            return !p_235010_2_ || p_235016_2_.getComponents().stream().anyMatch((p_235008_1_) -> {
                return p_235008_1_.getBoundingBox().isVecInside(p_235010_1_);
            });
        }).findFirst(), StructureStart.DUMMY);
    }
}
