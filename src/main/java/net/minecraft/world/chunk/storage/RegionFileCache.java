package net.minecraft.world.chunk.storage;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.math.ChunkPos;

public final class RegionFileCache implements AutoCloseable
{
    private final Long2ObjectLinkedOpenHashMap<RegionFile> cache = new Long2ObjectLinkedOpenHashMap<>();
    private final File folder;
    private final boolean field_235986_c_;

    RegionFileCache(File p_i231895_1_, boolean p_i231895_2_)
    {
        this.folder = p_i231895_1_;
        this.field_235986_c_ = p_i231895_2_;
    }

    private RegionFile loadFile(ChunkPos pos) throws IOException
    {
        long i = ChunkPos.asLong(pos.getRegionCoordX(), pos.getRegionCoordZ());
        RegionFile regionfile = this.cache.getAndMoveToFirst(i);

        if (regionfile != null)
        {
            return regionfile;
        }
        else
        {
            if (this.cache.size() >= 256)
            {
                this.cache.removeLast().close();
            }

            if (!this.folder.exists())
            {
                this.folder.mkdirs();
            }

            File file1 = new File(this.folder, "r." + pos.getRegionCoordX() + "." + pos.getRegionCoordZ() + ".mca");
            RegionFile regionfile1 = new RegionFile(file1, this.folder, this.field_235986_c_);
            this.cache.putAndMoveToFirst(i, regionfile1);
            return regionfile1;
        }
    }

    @Nullable
    public CompoundNBT readChunk(ChunkPos pos) throws IOException
    {
        RegionFile regionfile = this.loadFile(pos);
        Object object;

        try (DataInputStream datainputstream = regionfile.func_222666_a(pos))
        {
            if (datainputstream != null)
            {
                return CompressedStreamTools.read(datainputstream);
            }

            object = null;
        }

        return (CompoundNBT)object;
    }

    protected void writeChunk(ChunkPos pos, CompoundNBT compound) throws IOException
    {
        RegionFile regionfile = this.loadFile(pos);

        try (DataOutputStream dataoutputstream = regionfile.func_222661_c(pos))
        {
            CompressedStreamTools.write(compound, dataoutputstream);
        }
    }

    public void close() throws IOException
    {
        SuppressedExceptions<IOException> suppressedexceptions = new SuppressedExceptions<>();

        for (RegionFile regionfile : this.cache.values())
        {
            try
            {
                regionfile.close();
            }
            catch (IOException ioexception)
            {
                suppressedexceptions.func_233003_a_(ioexception);
            }
        }

        suppressedexceptions.func_233002_a_();
    }

    public void func_235987_a_() throws IOException
    {
        for (RegionFile regionfile : this.cache.values())
        {
            regionfile.func_235985_a_();
        }
    }
}
