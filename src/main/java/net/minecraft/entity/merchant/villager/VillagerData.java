package net.minecraft.entity.merchant.villager;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.villager.VillagerType;
import net.minecraft.util.registry.Registry;

public class VillagerData
{
    private static final int[] LEVEL_EXPERIENCE_AMOUNTS = new int[] {0, 10, 70, 150, 250};
    public static final Codec<VillagerData> CODEC = RecordCodecBuilder.create((dataInstance) ->
    {
        return dataInstance.group(Registry.VILLAGER_TYPE.fieldOf("type").orElseGet(() -> {
            return VillagerType.PLAINS;
        }).forGetter((data) -> {
            return data.type;
        }), Registry.VILLAGER_PROFESSION.fieldOf("profession").orElseGet(() -> {
            return VillagerProfession.NONE;
        }).forGetter((data) -> {
            return data.profession;
        }), Codec.INT.fieldOf("level").orElse(1).forGetter((data) -> {
            return data.level;
        })).apply(dataInstance, VillagerData::new);
    });
    private final VillagerType type;
    private final VillagerProfession profession;
    private final int level;

    public VillagerData(VillagerType type, VillagerProfession profession, int level)
    {
        this.type = type;
        this.profession = profession;
        this.level = Math.max(1, level);
    }

    public VillagerType getType()
    {
        return this.type;
    }

    public VillagerProfession getProfession()
    {
        return this.profession;
    }

    public int getLevel()
    {
        return this.level;
    }

    public VillagerData withType(VillagerType typeIn)
    {
        return new VillagerData(typeIn, this.profession, this.level);
    }

    public VillagerData withProfession(VillagerProfession professionIn)
    {
        return new VillagerData(this.type, professionIn, this.level);
    }

    public VillagerData withLevel(int levelIn)
    {
        return new VillagerData(this.type, this.profession, levelIn);
    }

    public static int getExperiencePrevious(int level)
    {
        return canLevelUp(level) ? LEVEL_EXPERIENCE_AMOUNTS[level - 1] : 0;
    }

    public static int getExperienceNext(int level)
    {
        return canLevelUp(level) ? LEVEL_EXPERIENCE_AMOUNTS[level] : 0;
    }

    public static boolean canLevelUp(int level)
    {
        return level >= 1 && level < 5;
    }
}
