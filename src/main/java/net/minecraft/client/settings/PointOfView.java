package net.minecraft.client.settings;

public enum PointOfView
{
    FIRST_PERSON(true, false),
    THIRD_PERSON_BACK(false, false),
    THIRD_PERSON_FRONT(false, true);

    private static final PointOfView[] field_243189_d = values();
    private boolean field_243190_e;
    private boolean field_243191_f;

    private PointOfView(boolean p_i242049_3_, boolean p_i242049_4_)
    {
        this.field_243190_e = p_i242049_3_;
        this.field_243191_f = p_i242049_4_;
    }

    public boolean func_243192_a()
    {
        return this.field_243190_e;
    }

    public boolean func_243193_b()
    {
        return this.field_243191_f;
    }

    public PointOfView func_243194_c()
    {
        return field_243189_d[(this.ordinal() + 1) % field_243189_d.length];
    }
}
