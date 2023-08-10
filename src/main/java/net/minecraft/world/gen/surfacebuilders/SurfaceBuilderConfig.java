package net.minecraft.world.gen.surfacebuilders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;

public class SurfaceBuilderConfig implements ISurfaceBuilderConfig
{
    public static final Codec<SurfaceBuilderConfig> field_237203_a_ = RecordCodecBuilder.create((p_237204_0_) ->
    {
        return p_237204_0_.group(BlockState.CODEC.fieldOf("top_material").forGetter((p_237207_0_) -> {
            return p_237207_0_.topMaterial;
        }), BlockState.CODEC.fieldOf("under_material").forGetter((p_237206_0_) -> {
            return p_237206_0_.underMaterial;
        }), BlockState.CODEC.fieldOf("underwater_material").forGetter((p_237205_0_) -> {
            return p_237205_0_.underWaterMaterial;
        })).apply(p_237204_0_, SurfaceBuilderConfig::new);
    });
    private final BlockState topMaterial;
    private final BlockState underMaterial;
    private final BlockState underWaterMaterial;

    public SurfaceBuilderConfig(BlockState topMaterial, BlockState underMaterial, BlockState underWaterMaterial)
    {
        this.topMaterial = topMaterial;
        this.underMaterial = underMaterial;
        this.underWaterMaterial = underWaterMaterial;
    }

    public BlockState getTop()
    {
        return this.topMaterial;
    }

    public BlockState getUnder()
    {
        return this.underMaterial;
    }

    public BlockState getUnderWaterMaterial()
    {
        return this.underWaterMaterial;
    }
}
