package net.minecraft.data;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

public class ModelsUtil
{
    private final Optional<ResourceLocation> field_240225_a_;
    private final Set<StockTextureAliases> field_240226_b_;
    private Optional<String> field_240227_c_;

    public ModelsUtil(Optional<ResourceLocation> p_i232546_1_, Optional<String> p_i232546_2_, StockTextureAliases... p_i232546_3_)
    {
        this.field_240225_a_ = p_i232546_1_;
        this.field_240227_c_ = p_i232546_2_;
        this.field_240226_b_ = ImmutableSet.copyOf(p_i232546_3_);
    }

    public ResourceLocation func_240228_a_(Block p_240228_1_, ModelTextures p_240228_2_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_240228_3_)
    {
        return this.func_240234_a_(ModelsResourceUtil.func_240222_a_(p_240228_1_, this.field_240227_c_.orElse("")), p_240228_2_, p_240228_3_);
    }

    public ResourceLocation func_240229_a_(Block p_240229_1_, String p_240229_2_, ModelTextures p_240229_3_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_240229_4_)
    {
        return this.func_240234_a_(ModelsResourceUtil.func_240222_a_(p_240229_1_, p_240229_2_ + (String)this.field_240227_c_.orElse("")), p_240229_3_, p_240229_4_);
    }

    public ResourceLocation func_240235_b_(Block p_240235_1_, String p_240235_2_, ModelTextures p_240235_3_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_240235_4_)
    {
        return this.func_240234_a_(ModelsResourceUtil.func_240222_a_(p_240235_1_, p_240235_2_), p_240235_3_, p_240235_4_);
    }

    public ResourceLocation func_240234_a_(ResourceLocation p_240234_1_, ModelTextures p_240234_2_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_240234_3_)
    {
        Map<StockTextureAliases, ResourceLocation> map = this.func_240232_a_(p_240234_2_);
        p_240234_3_.accept(p_240234_1_, () ->
        {
            JsonObject jsonobject = new JsonObject();
            this.field_240225_a_.ifPresent((p_240231_1_) -> {
                jsonobject.addProperty("parent", p_240231_1_.toString());
            });

            if (!map.isEmpty())
            {
                JsonObject jsonobject1 = new JsonObject();
                map.forEach((p_240230_1_, p_240230_2_) ->
                {
                    jsonobject1.addProperty(p_240230_1_.getName(), p_240230_2_.toString());
                });
                jsonobject.add("textures", jsonobject1);
            }

            return jsonobject;
        });
        return p_240234_1_;
    }

    private Map<StockTextureAliases, ResourceLocation> func_240232_a_(ModelTextures p_240232_1_)
    {
        return Streams.concat(this.field_240226_b_.stream(), p_240232_1_.func_240342_a_()).collect(ImmutableMap.toImmutableMap(Function.identity(), p_240232_1_::func_240348_a_));
    }
}
