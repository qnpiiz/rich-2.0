package net.minecraft.world.gen.feature.template;

import com.mojang.serialization.Codec;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class IntegrityProcessor extends StructureProcessor
{
    public static final Codec<IntegrityProcessor> field_237077_a_ = Codec.FLOAT.fieldOf("integrity").orElse(1.0F).xmap(IntegrityProcessor::new, (p_237078_0_) ->
    {
        return p_237078_0_.integrity;
    }).codec();
    private final float integrity;

    public IntegrityProcessor(float integrity)
    {
        this.integrity = integrity;
    }

    @Nullable
    public Template.BlockInfo func_230386_a_(IWorldReader p_230386_1_, BlockPos p_230386_2_, BlockPos p_230386_3_, Template.BlockInfo p_230386_4_, Template.BlockInfo p_230386_5_, PlacementSettings p_230386_6_)
    {
        Random random = p_230386_6_.getRandom(p_230386_5_.pos);
        return !(this.integrity >= 1.0F) && !(random.nextFloat() <= this.integrity) ? null : p_230386_5_;
    }

    protected IStructureProcessorType<?> getType()
    {
        return IStructureProcessorType.BLOCK_ROT;
    }
}
