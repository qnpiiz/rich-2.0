package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public abstract class StructureStart<C extends IFeatureConfig>
{
    public static final StructureStart<?> DUMMY = new StructureStart<MineshaftConfig>(Structure.field_236367_c_, 0, 0, MutableBoundingBox.getNewBoundingBox(), 0, 0L)
    {
        public void func_230364_a_(DynamicRegistries p_230364_1_, ChunkGenerator p_230364_2_, TemplateManager p_230364_3_, int p_230364_4_, int p_230364_5_, Biome p_230364_6_, MineshaftConfig p_230364_7_)
        {
        }
    };
    private final Structure<C> structure;
    protected final List<StructurePiece> components = Lists.newArrayList();
    protected MutableBoundingBox bounds;
    private final int chunkPosX;
    private final int chunkPosZ;
    private int references;
    protected final SharedSeedRandom rand;

    public StructureStart(Structure<C> p_i225876_1_, int p_i225876_2_, int p_i225876_3_, MutableBoundingBox p_i225876_4_, int p_i225876_5_, long p_i225876_6_)
    {
        this.structure = p_i225876_1_;
        this.chunkPosX = p_i225876_2_;
        this.chunkPosZ = p_i225876_3_;
        this.references = p_i225876_5_;
        this.rand = new SharedSeedRandom();
        this.rand.setLargeFeatureSeed(p_i225876_6_, p_i225876_2_, p_i225876_3_);
        this.bounds = p_i225876_4_;
    }

    public abstract void func_230364_a_(DynamicRegistries p_230364_1_, ChunkGenerator p_230364_2_, TemplateManager p_230364_3_, int p_230364_4_, int p_230364_5_, Biome p_230364_6_, C p_230364_7_);

    public MutableBoundingBox getBoundingBox()
    {
        return this.bounds;
    }

    public List<StructurePiece> getComponents()
    {
        return this.components;
    }

    public void func_230366_a_(ISeedReader p_230366_1_, StructureManager p_230366_2_, ChunkGenerator p_230366_3_, Random p_230366_4_, MutableBoundingBox p_230366_5_, ChunkPos p_230366_6_)
    {
        synchronized (this.components)
        {
            if (!this.components.isEmpty())
            {
                MutableBoundingBox mutableboundingbox = (this.components.get(0)).boundingBox;
                Vector3i vector3i = mutableboundingbox.func_215126_f();
                BlockPos blockpos = new BlockPos(vector3i.getX(), mutableboundingbox.minY, vector3i.getZ());
                Iterator<StructurePiece> iterator = this.components.iterator();

                while (iterator.hasNext())
                {
                    StructurePiece structurepiece = iterator.next();

                    if (structurepiece.getBoundingBox().intersectsWith(p_230366_5_) && !structurepiece.func_230383_a_(p_230366_1_, p_230366_2_, p_230366_3_, p_230366_4_, p_230366_5_, p_230366_6_, blockpos))
                    {
                        iterator.remove();
                    }
                }

                this.recalculateStructureSize();
            }
        }
    }

    protected void recalculateStructureSize()
    {
        this.bounds = MutableBoundingBox.getNewBoundingBox();

        for (StructurePiece structurepiece : this.components)
        {
            this.bounds.expandTo(structurepiece.getBoundingBox());
        }
    }

    public CompoundNBT write(int chunkX, int chunkZ)
    {
        CompoundNBT compoundnbt = new CompoundNBT();

        if (this.isValid())
        {
            compoundnbt.putString("id", Registry.STRUCTURE_FEATURE.getKey(this.getStructure()).toString());
            compoundnbt.putInt("ChunkX", chunkX);
            compoundnbt.putInt("ChunkZ", chunkZ);
            compoundnbt.putInt("references", this.references);
            compoundnbt.put("BB", this.bounds.toNBTTagIntArray());
            ListNBT lvt_4_1_ = new ListNBT();

            synchronized (this.components)
            {
                for (StructurePiece structurepiece : this.components)
                {
                    lvt_4_1_.add(structurepiece.write());
                }
            }

            compoundnbt.put("Children", lvt_4_1_);
            return compoundnbt;
        }
        else
        {
            compoundnbt.putString("id", "INVALID");
            return compoundnbt;
        }
    }

    protected void func_214628_a(int p_214628_1_, Random p_214628_2_, int p_214628_3_)
    {
        int i = p_214628_1_ - p_214628_3_;
        int j = this.bounds.getYSize() + 1;

        if (j < i)
        {
            j += p_214628_2_.nextInt(i - j);
        }

        int k = j - this.bounds.maxY;
        this.bounds.offset(0, k, 0);

        for (StructurePiece structurepiece : this.components)
        {
            structurepiece.offset(0, k, 0);
        }
    }

    protected void func_214626_a(Random p_214626_1_, int p_214626_2_, int p_214626_3_)
    {
        int i = p_214626_3_ - p_214626_2_ + 1 - this.bounds.getYSize();
        int j;

        if (i > 1)
        {
            j = p_214626_2_ + p_214626_1_.nextInt(i);
        }
        else
        {
            j = p_214626_2_;
        }

        int k = j - this.bounds.minY;
        this.bounds.offset(0, k, 0);

        for (StructurePiece structurepiece : this.components)
        {
            structurepiece.offset(0, k, 0);
        }
    }

    /**
     * currently only defined for Villages, returns true if Village has more than 2 non-road components
     */
    public boolean isValid()
    {
        return !this.components.isEmpty();
    }

    public int getChunkPosX()
    {
        return this.chunkPosX;
    }

    public int getChunkPosZ()
    {
        return this.chunkPosZ;
    }

    public BlockPos getPos()
    {
        return new BlockPos(this.chunkPosX << 4, 0, this.chunkPosZ << 4);
    }

    public boolean isRefCountBelowMax()
    {
        return this.references < this.getMaxRefCount();
    }

    public void incrementRefCount()
    {
        ++this.references;
    }

    public int getRefCount()
    {
        return this.references;
    }

    protected int getMaxRefCount()
    {
        return 1;
    }

    public Structure<?> getStructure()
    {
        return this.structure;
    }
}
