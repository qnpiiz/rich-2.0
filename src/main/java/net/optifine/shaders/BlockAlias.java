package net.optifine.shaders;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.optifine.Config;
import net.optifine.config.MatchBlock;

public class BlockAlias
{
    private int aliasBlockId;
    private int aliasMetadata;
    private MatchBlock[] matchBlocks;

    public BlockAlias(int aliasBlockId, int aliasMetadata, MatchBlock[] matchBlocks)
    {
        this.aliasBlockId = aliasBlockId;
        this.aliasMetadata = aliasMetadata;
        this.matchBlocks = matchBlocks;
    }

    public BlockAlias(int aliasBlockId, MatchBlock[] matchBlocks)
    {
        this.aliasBlockId = aliasBlockId;
        this.matchBlocks = matchBlocks;
    }

    public int getAliasBlockId()
    {
        return this.aliasBlockId;
    }

    public int getAliasMetadata()
    {
        return this.aliasMetadata;
    }

    public MatchBlock[] getMatchBlocks()
    {
        return this.matchBlocks;
    }

    public boolean matches(int id, int metadata)
    {
        for (int i = 0; i < this.matchBlocks.length; ++i)
        {
            MatchBlock matchblock = this.matchBlocks[i];

            if (matchblock.matches(id, metadata))
            {
                return true;
            }
        }

        return false;
    }

    public int[] getMatchBlockIds()
    {
        Set<Integer> set = new HashSet<>();

        for (int i = 0; i < this.matchBlocks.length; ++i)
        {
            MatchBlock matchblock = this.matchBlocks[i];
            int j = matchblock.getBlockId();
            set.add(j);
        }

        Integer[] ainteger = set.toArray(new Integer[set.size()]);
        return Config.toPrimitive(ainteger);
    }

    public MatchBlock[] getMatchBlocks(int matchBlockId)
    {
        List<MatchBlock> list = new ArrayList<>();

        for (int i = 0; i < this.matchBlocks.length; ++i)
        {
            MatchBlock matchblock = this.matchBlocks[i];

            if (matchblock.getBlockId() == matchBlockId)
            {
                list.add(matchblock);
            }
        }

        return list.toArray(new MatchBlock[list.size()]);
    }

    public String toString()
    {
        return "block." + this.aliasBlockId + ":" + this.aliasMetadata + "=" + Config.arrayToString((Object[])this.matchBlocks);
    }
}
