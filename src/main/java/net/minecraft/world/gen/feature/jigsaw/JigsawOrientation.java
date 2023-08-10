package net.minecraft.world.gen.feature.jigsaw;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;

public enum JigsawOrientation implements IStringSerializable
{
    DOWN_EAST("down_east", Direction.DOWN, Direction.EAST),
    DOWN_NORTH("down_north", Direction.DOWN, Direction.NORTH),
    DOWN_SOUTH("down_south", Direction.DOWN, Direction.SOUTH),
    DOWN_WEST("down_west", Direction.DOWN, Direction.WEST),
    UP_EAST("up_east", Direction.UP, Direction.EAST),
    UP_NORTH("up_north", Direction.UP, Direction.NORTH),
    UP_SOUTH("up_south", Direction.UP, Direction.SOUTH),
    UP_WEST("up_west", Direction.UP, Direction.WEST),
    WEST_UP("west_up", Direction.WEST, Direction.UP),
    EAST_UP("east_up", Direction.EAST, Direction.UP),
    NORTH_UP("north_up", Direction.NORTH, Direction.UP),
    SOUTH_UP("south_up", Direction.SOUTH, Direction.UP);

    private static final Int2ObjectMap<JigsawOrientation> field_239637_m_ = new Int2ObjectOpenHashMap<>(values().length);
    private final String field_239638_n_;
    private final Direction field_239639_o_;
    private final Direction field_239640_p_;

    private static int func_239643_b_(Direction p_239643_0_, Direction p_239643_1_)
    {
        return p_239643_0_.ordinal() << 3 | p_239643_1_.ordinal();
    }

    private JigsawOrientation(String p_i232507_3_, Direction p_i232507_4_, Direction p_i232507_5_)
    {
        this.field_239638_n_ = p_i232507_3_;
        this.field_239640_p_ = p_i232507_4_;
        this.field_239639_o_ = p_i232507_5_;
    }

    public String getString()
    {
        return this.field_239638_n_;
    }

    public static JigsawOrientation func_239641_a_(Direction p_239641_0_, Direction p_239641_1_)
    {
        int i = func_239643_b_(p_239641_1_, p_239641_0_);
        return field_239637_m_.get(i);
    }

    public Direction func_239642_b_()
    {
        return this.field_239640_p_;
    }

    public Direction func_239644_c_()
    {
        return this.field_239639_o_;
    }

    static {
        for (JigsawOrientation jigsaworientation : values())
        {
            field_239637_m_.put(func_239643_b_(jigsaworientation.field_239639_o_, jigsaworientation.field_239640_p_), jigsaworientation);
        }
    }
}
