package net.minecraft.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.function.Supplier;
import net.minecraft.util.ResourceLocation;

public class BlockModelWriter implements Supplier<JsonElement>
{
    private final ResourceLocation field_240218_a_;

    public BlockModelWriter(ResourceLocation p_i232545_1_)
    {
        this.field_240218_a_ = p_i232545_1_;
    }

    public JsonElement get()
    {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("parent", this.field_240218_a_.toString());
        return jsonobject;
    }
}
