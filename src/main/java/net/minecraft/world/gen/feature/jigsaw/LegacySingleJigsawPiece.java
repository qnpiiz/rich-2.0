package net.minecraft.world.gen.feature.jigsaw;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessorList;
import net.minecraft.world.gen.feature.template.Template;

public class LegacySingleJigsawPiece extends SingleJigsawPiece
{
    public static final Codec<LegacySingleJigsawPiece> field_236832_a_ = RecordCodecBuilder.create((p_236833_0_) ->
    {
        return p_236833_0_.group(func_236846_c_(), func_236844_b_(), func_236848_d_()).apply(p_236833_0_, LegacySingleJigsawPiece::new);
    });

    protected LegacySingleJigsawPiece(Either<ResourceLocation, Template> p_i242007_1_, Supplier<StructureProcessorList> p_i242007_2_, JigsawPattern.PlacementBehaviour p_i242007_3_)
    {
        super(p_i242007_1_, p_i242007_2_, p_i242007_3_);
    }

    protected PlacementSettings func_230379_a_(Rotation p_230379_1_, MutableBoundingBox p_230379_2_, boolean p_230379_3_)
    {
        PlacementSettings placementsettings = super.func_230379_a_(p_230379_1_, p_230379_2_, p_230379_3_);
        placementsettings.removeProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
        placementsettings.addProcessor(BlockIgnoreStructureProcessor.AIR_AND_STRUCTURE_BLOCK);
        return placementsettings;
    }

    public IJigsawDeserializer<?> getType()
    {
        return IJigsawDeserializer.field_236849_e_;
    }

    public String toString()
    {
        return "LegacySingle[" + this.field_236839_c_ + "]";
    }
}
