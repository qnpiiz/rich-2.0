package net.minecraft.world.gen.feature.structure;

import com.mojang.serialization.Codec;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class ShipwreckStructure extends Structure<ShipwreckConfig>
{
    public ShipwreckStructure(Codec<ShipwreckConfig> p_i231989_1_)
    {
        super(p_i231989_1_);
    }

    public Structure.IStartFactory<ShipwreckConfig> getStartFactory()
    {
        return ShipwreckStructure.Start::new;
    }

    public static class Start extends StructureStart<ShipwreckConfig>
    {
        public Start(Structure<ShipwreckConfig> p_i225817_1_, int p_i225817_2_, int p_i225817_3_, MutableBoundingBox p_i225817_4_, int p_i225817_5_, long p_i225817_6_)
        {
            super(p_i225817_1_, p_i225817_2_, p_i225817_3_, p_i225817_4_, p_i225817_5_, p_i225817_6_);
        }

        public void func_230364_a_(DynamicRegistries p_230364_1_, ChunkGenerator p_230364_2_, TemplateManager p_230364_3_, int p_230364_4_, int p_230364_5_, Biome p_230364_6_, ShipwreckConfig p_230364_7_)
        {
            Rotation rotation = Rotation.randomRotation(this.rand);
            BlockPos blockpos = new BlockPos(p_230364_4_ * 16, 90, p_230364_5_ * 16);
            ShipwreckPieces.func_204760_a(p_230364_3_, blockpos, rotation, this.components, this.rand, p_230364_7_);
            this.recalculateStructureSize();
        }
    }
}
