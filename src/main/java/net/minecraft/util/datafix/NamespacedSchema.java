package net.minecraft.util.datafix;

import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.Const.PrimitiveType;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.minecraft.util.ResourceLocation;

public class NamespacedSchema extends Schema
{
    public static final PrimitiveCodec<String> field_233455_a_ = new PrimitiveCodec<String>()
    {
        public <T> DataResult<String> read(DynamicOps<T> p_read_1_, T p_read_2_)
        {
            return p_read_1_.getStringValue(p_read_2_).map(NamespacedSchema::ensureNamespaced);
        }
        public <T> T write(DynamicOps<T> p_write_1_, String p_write_2_)
        {
            return p_write_1_.createString(p_write_2_);
        }
        public String toString()
        {
            return "NamespacedString";
        }
    };
    private static final Type<String> field_233456_b_ = new PrimitiveType<>(field_233455_a_);

    public NamespacedSchema(int versionKey, Schema schema)
    {
        super(versionKey, schema);
    }

    public static String ensureNamespaced(String string)
    {
        ResourceLocation resourcelocation = ResourceLocation.tryCreate(string);
        return resourcelocation != null ? resourcelocation.toString() : string;
    }

    public static Type<String> func_233457_a_()
    {
        return field_233456_b_;
    }

    public Type<?> getChoiceType(TypeReference p_getChoiceType_1_, String p_getChoiceType_2_)
    {
        return super.getChoiceType(p_getChoiceType_1_, ensureNamespaced(p_getChoiceType_2_));
    }
}
