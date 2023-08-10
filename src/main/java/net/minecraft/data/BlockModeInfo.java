package net.minecraft.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.function.Function;

public class BlockModeInfo<T>
{
    private final String property;
    private final Function<T, JsonElement> value;

    public BlockModeInfo(String property, Function<T, JsonElement> value)
    {
        this.property = property;
        this.value = value;
    }

    public BlockModeInfo<T>.Field getFieldInfo(T element)
    {
        return new BlockModeInfo.Field(element);
    }

    public String toString()
    {
        return this.property;
    }

    public class Field
    {
        private final T element;

        public Field(T element)
        {
            this.element = element;
        }

        public void serialize(JsonObject json)
        {
            json.add(BlockModeInfo.this.property, BlockModeInfo.this.value.apply(this.element));
        }

        public String toString()
        {
            return BlockModeInfo.this.property + "=" + this.element;
        }
    }
}
