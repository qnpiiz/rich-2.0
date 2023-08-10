package net.minecraft.realms;

import com.google.gson.Gson;

public class PersistenceSerializer
{
    private final Gson field_237693_a_ = new Gson();

    public String func_237694_a_(IPersistentSerializable p_237694_1_)
    {
        return this.field_237693_a_.toJson(p_237694_1_);
    }

    public <T extends IPersistentSerializable> T func_237695_a_(String p_237695_1_, Class<T> p_237695_2_)
    {
        return this.field_237693_a_.fromJson(p_237695_1_, p_237695_2_);
    }
}
