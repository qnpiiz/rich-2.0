package net.optifine.config;

import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.optifine.Config;

public class MatchProfession
{
    private VillagerProfession profession;
    private int[] levels;

    public MatchProfession(VillagerProfession profession)
    {
        this(profession, (int[])null);
    }

    public MatchProfession(VillagerProfession profession, int level)
    {
        this(profession, new int[] {level});
    }

    public MatchProfession(VillagerProfession profession, int[] levels)
    {
        this.profession = profession;
        this.levels = levels;
    }

    public boolean matches(VillagerProfession prof, int lev)
    {
        if (this.profession != prof)
        {
            return false;
        }
        else
        {
            return this.levels == null || Config.equalsOne(lev, this.levels);
        }
    }

    private boolean hasLevel(int lev)
    {
        return this.levels == null ? false : Config.equalsOne(lev, this.levels);
    }

    public boolean addLevel(int lev)
    {
        if (this.levels == null)
        {
            this.levels = new int[] {lev};
            return true;
        }
        else if (this.hasLevel(lev))
        {
            return false;
        }
        else
        {
            this.levels = Config.addIntToArray(this.levels, lev);
            return true;
        }
    }

    public VillagerProfession getProfession()
    {
        return this.profession;
    }

    public int[] getLevels()
    {
        return this.levels;
    }

    public static boolean matchesOne(VillagerProfession prof, int level, MatchProfession[] mps)
    {
        if (mps == null)
        {
            return false;
        }
        else
        {
            for (int i = 0; i < mps.length; ++i)
            {
                MatchProfession matchprofession = mps[i];

                if (matchprofession.matches(prof, level))
                {
                    return true;
                }
            }

            return false;
        }
    }

    public String toString()
    {
        return this.levels == null ? "" + this.profession : "" + this.profession + ":" + Config.arrayToString(this.levels);
    }
}
