package net.minecraft.client.settings;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.util.math.MathHelper;

public enum GraphicsFanciness
{
    FAST(0, "options.graphics.fast"),
    FANCY(1, "options.graphics.fancy"),
    FABULOUS(2, "options.graphics.fabulous");

    private static final GraphicsFanciness[] field_238159_d_ = Arrays.stream(values()).sorted(Comparator.comparingInt(GraphicsFanciness::func_238162_a_)).toArray((p_238165_0_) -> {
        return new GraphicsFanciness[p_238165_0_];
    });
    private final int field_238160_e_;
    private final String field_238161_f_;

    private GraphicsFanciness(int p_i232238_3_, String p_i232238_4_)
    {
        this.field_238160_e_ = p_i232238_3_;
        this.field_238161_f_ = p_i232238_4_;
    }

    public int func_238162_a_()
    {
        return this.field_238160_e_;
    }

    public String func_238164_b_()
    {
        return this.field_238161_f_;
    }

    public GraphicsFanciness func_238166_c_()
    {
        return func_238163_a_(this.func_238162_a_() + 1);
    }

    public String toString()
    {
        switch (this)
        {
            case FAST:
                return "fast";

            case FANCY:
                return "fancy";

            case FABULOUS:
                return "fabulous";

            default:
                throw new IllegalArgumentException();
        }
    }

    public static GraphicsFanciness func_238163_a_(int p_238163_0_)
    {
        return field_238159_d_[MathHelper.normalizeAngle(p_238163_0_, field_238159_d_.length)];
    }
}
