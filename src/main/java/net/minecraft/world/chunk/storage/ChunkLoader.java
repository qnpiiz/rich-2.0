package net.minecraft.world.chunk.storage;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.LegacyStructureDataUtil;
import net.minecraft.world.storage.DimensionSavedDataManager;

public class ChunkLoader implements AutoCloseable
{
    private final IOWorker field_227077_a_;
    protected final DataFixer dataFixer;
    @Nullable
    private LegacyStructureDataUtil field_219167_a;

    public ChunkLoader(File p_i231889_1_, DataFixer p_i231889_2_, boolean p_i231889_3_)
    {
        this.dataFixer = p_i231889_2_;
        this.field_227077_a_ = new IOWorker(p_i231889_1_, p_i231889_3_, "chunk");
    }

    public CompoundNBT func_235968_a_(RegistryKey<World> p_235968_1_, Supplier<DimensionSavedDataManager> p_235968_2_, CompoundNBT p_235968_3_)
    {
        int i = getDataVersion(p_235968_3_);
        int j = 1493;

        if (i < 1493)
        {
            p_235968_3_ = NBTUtil.update(this.dataFixer, DefaultTypeReferences.CHUNK, p_235968_3_, i, 1493);

            if (p_235968_3_.getCompound("Level").getBoolean("hasLegacyStructureData"))
            {
                if (this.field_219167_a == null)
                {
                    this.field_219167_a = LegacyStructureDataUtil.func_236992_a_(p_235968_1_, p_235968_2_.get());
                }

                p_235968_3_ = this.field_219167_a.func_212181_a(p_235968_3_);
            }
        }

        p_235968_3_ = NBTUtil.update(this.dataFixer, DefaultTypeReferences.CHUNK, p_235968_3_, Math.max(1493, i));

        if (i < SharedConstants.getVersion().getWorldVersion())
        {
            p_235968_3_.putInt("DataVersion", SharedConstants.getVersion().getWorldVersion());
        }

        return p_235968_3_;
    }

    public static int getDataVersion(CompoundNBT compound)
    {
        return compound.contains("DataVersion", 99) ? compound.getInt("DataVersion") : -1;
    }

    @Nullable
    public CompoundNBT readChunk(ChunkPos p_227078_1_) throws IOException
    {
        return this.field_227077_a_.func_227090_a_(p_227078_1_);
    }

    public void writeChunk(ChunkPos pos, CompoundNBT compound)
    {
        this.field_227077_a_.func_227093_a_(pos, compound);

        if (this.field_219167_a != null)
        {
            this.field_219167_a.func_208216_a(pos.asLong());
        }
    }

    public void func_227079_i_()
    {
        this.field_227077_a_.func_227088_a_().join();
    }

    public void close() throws IOException
    {
        this.field_227077_a_.close();
    }
}
