package net.minecraft.data;

import com.google.gson.JsonPrimitive;
import net.minecraft.util.ResourceLocation;

public class BlockModelFields
{
    public static final BlockModeInfo<BlockModelFields.Rotation> field_240200_a_ = new BlockModeInfo<>("x", (p_240207_0_) ->
    {
        return new JsonPrimitive(p_240207_0_.field_240208_e_);
    });
    public static final BlockModeInfo<BlockModelFields.Rotation> field_240201_b_ = new BlockModeInfo<>("y", (p_240205_0_) ->
    {
        return new JsonPrimitive(p_240205_0_.field_240208_e_);
    });
    public static final BlockModeInfo<ResourceLocation> field_240202_c_ = new BlockModeInfo<>("model", (p_240206_0_) ->
    {
        return new JsonPrimitive(p_240206_0_.toString());
    });
    public static final BlockModeInfo<Boolean> field_240203_d_ = new BlockModeInfo<>("uvlock", JsonPrimitive::new);
    public static final BlockModeInfo<Integer> field_240204_e_ = new BlockModeInfo<>("weight", JsonPrimitive::new);

    public static enum Rotation
    {
        R0(0),
        R90(90),
        R180(180),
        R270(270);

        private final int field_240208_e_;

        private Rotation(int p_i232542_3_)
        {
            this.field_240208_e_ = p_i232542_3_;
        }
    }
}
