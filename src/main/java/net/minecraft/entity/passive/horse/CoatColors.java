package net.minecraft.entity.passive.horse;

import java.util.Arrays;
import java.util.Comparator;

public enum CoatColors
{
    WHITE(0),
    CREAMY(1),
    CHESTNUT(2),
    BROWN(3),
    BLACK(4),
    GRAY(5),
    DARKBROWN(6);

    private static final CoatColors[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(CoatColors::getId)).toArray((p_234255_0_) -> {
        return new CoatColors[p_234255_0_];
    });
    private final int id;

    private CoatColors(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return this.id;
    }

    public static CoatColors func_234254_a_(int p_234254_0_)
    {
        return VALUES[p_234254_0_ % VALUES.length];
    }
}
