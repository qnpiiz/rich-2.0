package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.LongArrayNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.gen.Heightmap;
import net.optifine.ChunkDataOF;
import net.optifine.ChunkOF;

public class SChunkDataPacket implements IPacket<IClientPlayNetHandler>
{
    private int chunkX;
    private int chunkZ;
    private int availableSections;
    private CompoundNBT heightmapTags;
    @Nullable
    private int[] biomes;
    private byte[] buffer;
    private List<CompoundNBT> tileEntityTags;
    private boolean fullChunk;
    private Map<String, Object> customData;

    public SChunkDataPacket()
    {
    }

    public SChunkDataPacket(Chunk p_i242081_1_, int p_i242081_2_)
    {
        ChunkPos chunkpos = p_i242081_1_.getPos();
        this.chunkX = chunkpos.x;
        this.chunkZ = chunkpos.z;
        this.fullChunk = p_i242081_2_ == 65535;
        this.heightmapTags = new CompoundNBT();

        for (Entry<Heightmap.Type, Heightmap> entry : p_i242081_1_.getHeightmaps())
        {
            if (entry.getKey().isUsageClient())
            {
                this.heightmapTags.put(entry.getKey().getId(), new LongArrayNBT(entry.getValue().getDataArray()));
            }
        }

        if (this.fullChunk)
        {
            this.biomes = p_i242081_1_.getBiomes().getBiomeIds();
        }

        this.buffer = new byte[this.calculateChunkSize(p_i242081_1_, p_i242081_2_)];
        this.availableSections = this.extractChunkData(new PacketBuffer(this.getWriteBuffer()), p_i242081_1_, p_i242081_2_);
        this.tileEntityTags = Lists.newArrayList();

        for (Entry<BlockPos, TileEntity> entry1 : p_i242081_1_.getTileEntityMap().entrySet())
        {
            BlockPos blockpos = entry1.getKey();
            TileEntity tileentity = entry1.getValue();
            int i = blockpos.getY() >> 4;

            if (this.isFullChunk() || (p_i242081_2_ & 1 << i) != 0)
            {
                CompoundNBT compoundnbt = tileentity.getUpdateTag();
                this.tileEntityTags.add(compoundnbt);
            }
        }

        this.customData = new HashMap<>();
        ChunkDataOF chunkdataof = ChunkOF.makeChunkDataOF(p_i242081_1_);
        this.customData.put("ChunkDataOF", chunkdataof);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.chunkX = buf.readInt();
        this.chunkZ = buf.readInt();
        this.fullChunk = buf.readBoolean();
        this.availableSections = buf.readVarInt();
        this.heightmapTags = buf.readCompoundTag();

        if (this.fullChunk)
        {
            this.biomes = buf.readVarIntArray(BiomeContainer.BIOMES_SIZE);
        }

        int i = buf.readVarInt();

        if (i > 2097152)
        {
            throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
        }
        else
        {
            this.buffer = new byte[i];
            buf.readBytes(this.buffer);
            int j = buf.readVarInt();
            this.tileEntityTags = Lists.newArrayList();

            for (int k = 0; k < j; ++k)
            {
                this.tileEntityTags.add(buf.readCompoundTag());
            }
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeInt(this.chunkX);
        buf.writeInt(this.chunkZ);
        buf.writeBoolean(this.fullChunk);
        buf.writeVarInt(this.availableSections);
        buf.writeCompoundTag(this.heightmapTags);

        if (this.biomes != null)
        {
            buf.writeVarIntArray(this.biomes);
        }

        buf.writeVarInt(this.buffer.length);
        buf.writeBytes(this.buffer);
        buf.writeVarInt(this.tileEntityTags.size());

        for (CompoundNBT compoundnbt : this.tileEntityTags)
        {
            buf.writeCompoundTag(compoundnbt);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(IClientPlayNetHandler handler)
    {
        handler.handleChunkData(this);
    }

    public PacketBuffer getReadBuffer()
    {
        return new PacketBuffer(Unpooled.wrappedBuffer(this.buffer), this.customData);
    }

    private ByteBuf getWriteBuffer()
    {
        ByteBuf bytebuf = Unpooled.wrappedBuffer(this.buffer);
        bytebuf.writerIndex(0);
        return bytebuf;
    }

    public int extractChunkData(PacketBuffer buf, Chunk chunkIn, int writeSkylight)
    {
        int i = 0;
        ChunkSection[] achunksection = chunkIn.getSections();
        int j = 0;

        for (int k = achunksection.length; j < k; ++j)
        {
            ChunkSection chunksection = achunksection[j];

            if (chunksection != Chunk.EMPTY_SECTION && (!this.isFullChunk() || !chunksection.isEmpty()) && (writeSkylight & 1 << j) != 0)
            {
                i |= 1 << j;
                chunksection.write(buf);
            }
        }

        return i;
    }

    protected int calculateChunkSize(Chunk chunkIn, int changedSectionsIn)
    {
        int i = 0;
        ChunkSection[] achunksection = chunkIn.getSections();
        int j = 0;

        for (int k = achunksection.length; j < k; ++j)
        {
            ChunkSection chunksection = achunksection[j];

            if (chunksection != Chunk.EMPTY_SECTION && (!this.isFullChunk() || !chunksection.isEmpty()) && (changedSectionsIn & 1 << j) != 0)
            {
                i += chunksection.getSize();
            }
        }

        return i;
    }

    public int getChunkX()
    {
        return this.chunkX;
    }

    public int getChunkZ()
    {
        return this.chunkZ;
    }

    public int getAvailableSections()
    {
        return this.availableSections;
    }

    public boolean isFullChunk()
    {
        return this.fullChunk;
    }

    public CompoundNBT getHeightmapTags()
    {
        return this.heightmapTags;
    }

    public List<CompoundNBT> getTileEntityTags()
    {
        return this.tileEntityTags;
    }

    @Nullable
    public int[] func_244296_i()
    {
        return this.biomes;
    }
}
