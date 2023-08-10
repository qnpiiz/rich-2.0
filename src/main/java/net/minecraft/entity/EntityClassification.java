package net.minecraft.entity;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.util.IStringSerializable;

public enum EntityClassification implements IStringSerializable
{
    MONSTER("monster", 70, false, false, 128),
    CREATURE("creature", 10, true, true, 128),
    AMBIENT("ambient", 15, true, false, 128),
    WATER_CREATURE("water_creature", 5, true, false, 128),
    WATER_AMBIENT("water_ambient", 20, true, false, 64),
    MISC("misc", -1, true, true, 128);

    public static final Codec<EntityClassification> CODEC = IStringSerializable.createEnumCodec(EntityClassification::values, EntityClassification::getClassificationByName);
    private static final Map<String, EntityClassification> VALUES_MAP = Arrays.stream(values()).collect(Collectors.toMap(EntityClassification::getName, (classification) -> {
        return classification;
    }));
    private final int maxNumberOfCreature;
    private final boolean isPeacefulCreature;
    private final boolean isAnimal;
    private final String name;
    private final int randomDespawnDistance = 32;
    private final int instantDespawnDistance;

    private EntityClassification(String name, int maxNumberOfCreature, boolean isPeacefulCreature, boolean isAnimal, int instantDespawnDistance)
    {
        this.name = name;
        this.maxNumberOfCreature = maxNumberOfCreature;
        this.isPeacefulCreature = isPeacefulCreature;
        this.isAnimal = isAnimal;
        this.instantDespawnDistance = instantDespawnDistance;
    }

    public String getName()
    {
        return this.name;
    }

    public String getString()
    {
        return this.name;
    }

    public static EntityClassification getClassificationByName(String name)
    {
        return VALUES_MAP.get(name);
    }

    public int getMaxNumberOfCreature()
    {
        return this.maxNumberOfCreature;
    }

    /**
     * Gets whether or not this creature type is peaceful.
     */
    public boolean getPeacefulCreature()
    {
        return this.isPeacefulCreature;
    }

    /**
     * Return whether this creature type is an animal.
     */
    public boolean getAnimal()
    {
        return this.isAnimal;
    }

    public int getInstantDespawnDistance()
    {
        return this.instantDespawnDistance;
    }

    public int getRandomDespawnDistance()
    {
        return 32;
    }
}
