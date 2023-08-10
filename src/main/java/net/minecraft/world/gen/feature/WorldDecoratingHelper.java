package net.minecraft.world.gen.feature;

import java.util.BitSet;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;

public class WorldDecoratingHelper
{
    private final ISeedReader field_242889_a;
    private final ChunkGenerator field_242890_b;

    public WorldDecoratingHelper(ISeedReader p_i242021_1_, ChunkGenerator p_i242021_2_)
    {
        this.field_242889_a = p_i242021_1_;
        this.field_242890_b = p_i242021_2_;
    }

    public int func_242893_a(Heightmap.Type p_242893_1_, int p_242893_2_, int p_242893_3_)
    {
        return this.field_242889_a.getHeight(p_242893_1_, p_242893_2_, p_242893_3_);
    }

    public int func_242891_a()
    {
        return this.field_242890_b.func_230355_e_();
    }

    public int func_242895_b()
    {
        return this.field_242890_b.func_230356_f_();
    }

    public BitSet func_242892_a(ChunkPos p_242892_1_, GenerationStage.Carving p_242892_2_)
    {
        return ((ChunkPrimer)this.field_242889_a.getChunk(p_242892_1_.x, p_242892_1_.z)).getOrAddCarvingMask(p_242892_2_);
    }

    public BlockState func_242894_a(BlockPos p_242894_1_)
    {
        return this.field_242889_a.getBlockState(p_242894_1_);
    }
}
