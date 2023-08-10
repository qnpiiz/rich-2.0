package net.minecraft.world.gen.feature.structure;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class MineshaftStructure extends Structure<MineshaftConfig>
{
    public MineshaftStructure(Codec<MineshaftConfig> p_i231969_1_)
    {
        super(p_i231969_1_);
    }

    protected boolean func_230363_a_(ChunkGenerator p_230363_1_, BiomeProvider p_230363_2_, long p_230363_3_, SharedSeedRandom p_230363_5_, int p_230363_6_, int p_230363_7_, Biome p_230363_8_, ChunkPos p_230363_9_, MineshaftConfig p_230363_10_)
    {
        p_230363_5_.setLargeFeatureSeed(p_230363_3_, p_230363_6_, p_230363_7_);
        double d0 = (double)p_230363_10_.probability;
        return p_230363_5_.nextDouble() < d0;
    }

    public Structure.IStartFactory<MineshaftConfig> getStartFactory()
    {
        return MineshaftStructure.Start::new;
    }

    public static class Start extends StructureStart<MineshaftConfig>
    {
        public Start(Structure<MineshaftConfig> p_i225811_1_, int p_i225811_2_, int p_i225811_3_, MutableBoundingBox p_i225811_4_, int p_i225811_5_, long p_i225811_6_)
        {
            super(p_i225811_1_, p_i225811_2_, p_i225811_3_, p_i225811_4_, p_i225811_5_, p_i225811_6_);
        }

        public void func_230364_a_(DynamicRegistries p_230364_1_, ChunkGenerator p_230364_2_, TemplateManager p_230364_3_, int p_230364_4_, int p_230364_5_, Biome p_230364_6_, MineshaftConfig p_230364_7_)
        {
            MineshaftPieces.Room mineshaftpieces$room = new MineshaftPieces.Room(0, this.rand, (p_230364_4_ << 4) + 2, (p_230364_5_ << 4) + 2, p_230364_7_.type);
            this.components.add(mineshaftpieces$room);
            mineshaftpieces$room.buildComponent(mineshaftpieces$room, this.components, this.rand);
            this.recalculateStructureSize();

            if (p_230364_7_.type == MineshaftStructure.Type.MESA)
            {
                int i = -5;
                int j = p_230364_2_.func_230356_f_() - this.bounds.maxY + this.bounds.getYSize() / 2 - -5;
                this.bounds.offset(0, j, 0);

                for (StructurePiece structurepiece : this.components)
                {
                    structurepiece.offset(0, j, 0);
                }
            }
            else
            {
                this.func_214628_a(p_230364_2_.func_230356_f_(), this.rand, 10);
            }
        }
    }

    public static enum Type implements IStringSerializable
    {
        NORMAL("normal"),
        MESA("mesa");

        public static final Codec<MineshaftStructure.Type> field_236324_c_ = IStringSerializable.createEnumCodec(MineshaftStructure.Type::values, MineshaftStructure.Type::byName);
        private static final Map<String, MineshaftStructure.Type> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(MineshaftStructure.Type::getName, (p_214716_0_) -> {
            return p_214716_0_;
        }));
        private final String name;

        private Type(String nameIn)
        {
            this.name = nameIn;
        }

        public String getName()
        {
            return this.name;
        }

        private static MineshaftStructure.Type byName(String p_214715_0_)
        {
            return BY_NAME.get(p_214715_0_);
        }

        public static MineshaftStructure.Type byId(int id)
        {
            return id >= 0 && id < values().length ? values()[id] : NORMAL;
        }

        public String getString()
        {
            return this.name;
        }
    }
}
