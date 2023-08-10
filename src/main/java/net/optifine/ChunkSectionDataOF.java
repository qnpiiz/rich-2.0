package net.optifine;

public class ChunkSectionDataOF
{
    private short blockRefCount;
    private short tickRefCount;
    private short fluidRefCount;

    public ChunkSectionDataOF(short blockRefCount, short tickRefCount, short fluidRefCount)
    {
        this.blockRefCount = blockRefCount;
        this.tickRefCount = tickRefCount;
        this.fluidRefCount = fluidRefCount;
    }

    public short getBlockRefCount()
    {
        return this.blockRefCount;
    }

    public short getTickRefCount()
    {
        return this.tickRefCount;
    }

    public short getFluidRefCount()
    {
        return this.fluidRefCount;
    }
}
