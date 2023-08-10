package net.optifine.config;

import net.minecraft.block.BlockState;
import net.optifine.Config;

public class MatchBlock
{
    private int blockId = -1;
    private int[] metadatas = null;

    public MatchBlock(int blockId)
    {
        this.blockId = blockId;
    }

    public MatchBlock(int blockId, int metadata)
    {
        this.blockId = blockId;

        if (metadata >= 0)
        {
            this.metadatas = new int[] {metadata};
        }
    }

    public MatchBlock(int blockId, int[] metadatas)
    {
        this.blockId = blockId;
        this.metadatas = metadatas;
    }

    public int getBlockId()
    {
        return this.blockId;
    }

    public int[] getMetadatas()
    {
        return this.metadatas;
    }

    public boolean matches(BlockState blockState)
    {
        if (blockState.getBlockId() != this.blockId)
        {
            return false;
        }
        else
        {
            return Matches.metadata(blockState.getMetadata(), this.metadatas);
        }
    }

    public boolean matches(int id, int metadata)
    {
        if (id != this.blockId)
        {
            return false;
        }
        else
        {
            return Matches.metadata(metadata, this.metadatas);
        }
    }

    public void addMetadata(int metadata)
    {
        if (this.metadatas != null)
        {
            if (metadata >= 0)
            {
                for (int i = 0; i < this.metadatas.length; ++i)
                {
                    if (this.metadatas[i] == metadata)
                    {
                        return;
                    }
                }

                this.metadatas = Config.addIntToArray(this.metadatas, metadata);
            }
        }
    }

    public void addMetadatas(int[] mds)
    {
        for (int i = 0; i < mds.length; ++i)
        {
            int j = mds[i];
            this.addMetadata(j);
        }
    }

    public String toString()
    {
        return "" + this.blockId + ":" + Config.arrayToString(this.metadatas);
    }
}
