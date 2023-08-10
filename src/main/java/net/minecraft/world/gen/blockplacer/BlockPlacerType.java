package net.minecraft.world.gen.blockplacer;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public class BlockPlacerType<P extends BlockPlacer>
{
    public static final BlockPlacerType<SimpleBlockPlacer> SIMPLE_BLOCK = register("simple_block_placer", SimpleBlockPlacer.CODEC);
    public static final BlockPlacerType<DoublePlantBlockPlacer> DOUBLE_PLANT = register("double_plant_placer", DoublePlantBlockPlacer.CODEC);
    public static final BlockPlacerType<ColumnBlockPlacer> COLUMN = register("column_placer", ColumnBlockPlacer.CODEC);
    private final Codec<P> codec;

    private static <P extends BlockPlacer> BlockPlacerType<P> register(String name, Codec<P> codec)
    {
        return Registry.register(Registry.BLOCK_PLACER_TYPE, name, new BlockPlacerType<>(codec));
    }

    private BlockPlacerType(Codec<P> codec)
    {
        this.codec = codec;
    }

    public Codec<P> getCodec()
    {
        return this.codec;
    }
}
