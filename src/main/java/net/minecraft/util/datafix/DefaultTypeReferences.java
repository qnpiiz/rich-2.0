package net.minecraft.util.datafix;

import com.mojang.datafixers.DSL.TypeReference;

public enum DefaultTypeReferences
{
    LEVEL(TypeReferences.LEVEL),
    PLAYER(TypeReferences.PLAYER),
    CHUNK(TypeReferences.CHUNK),
    HOTBAR(TypeReferences.HOTBAR),
    OPTIONS(TypeReferences.OPTIONS),
    STRUCTURE(TypeReferences.STRUCTURE),
    STATS(TypeReferences.STATS),
    SAVED_DATA(TypeReferences.SAVED_DATA),
    ADVANCEMENTS(TypeReferences.ADVANCEMENTS),
    POI_CHUNK(TypeReferences.POI_CHUNK),
    WORLD_GEN_SETTINGS(TypeReferences.WORLD_GEN_SETTINGS);

    private final TypeReference reference;

    private DefaultTypeReferences(TypeReference reference)
    {
        this.reference = reference;
    }

    public TypeReference getTypeReference()
    {
        return this.reference;
    }
}
