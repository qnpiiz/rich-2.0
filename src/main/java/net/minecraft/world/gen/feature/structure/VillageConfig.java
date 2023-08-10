package net.minecraft.world.gen.feature.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;

public class VillageConfig implements IFeatureConfig
{
    public static final Codec<VillageConfig> field_236533_a_ = RecordCodecBuilder.create((p_236535_0_) ->
    {
        return p_236535_0_.group(JigsawPattern.field_244392_b_.fieldOf("start_pool").forGetter(VillageConfig::func_242810_c), Codec.intRange(0, 7).fieldOf("size").forGetter(VillageConfig::func_236534_a_)).apply(p_236535_0_, VillageConfig::new);
    });
    private final Supplier<JigsawPattern> startPool;
    private final int size;

    public VillageConfig(Supplier<JigsawPattern> p_i241987_1_, int p_i241987_2_)
    {
        this.startPool = p_i241987_1_;
        this.size = p_i241987_2_;
    }

    public int func_236534_a_()
    {
        return this.size;
    }

    public Supplier<JigsawPattern> func_242810_c()
    {
        return this.startPool;
    }
}
