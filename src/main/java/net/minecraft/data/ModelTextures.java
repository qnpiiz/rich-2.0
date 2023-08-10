package net.minecraft.data;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class ModelTextures
{
    private final Map<StockTextureAliases, ResourceLocation> field_240337_a_ = Maps.newHashMap();
    private final Set<StockTextureAliases> field_240338_b_ = Sets.newHashSet();

    public ModelTextures func_240349_a_(StockTextureAliases p_240349_1_, ResourceLocation p_240349_2_)
    {
        this.field_240337_a_.put(p_240349_1_, p_240349_2_);
        return this;
    }

    public Stream<StockTextureAliases> func_240342_a_()
    {
        return this.field_240338_b_.stream();
    }

    public ModelTextures func_240355_b_(StockTextureAliases p_240355_1_, StockTextureAliases p_240355_2_)
    {
        this.field_240337_a_.put(p_240355_2_, this.field_240337_a_.get(p_240355_1_));
        this.field_240338_b_.add(p_240355_2_);
        return this;
    }

    public ResourceLocation func_240348_a_(StockTextureAliases p_240348_1_)
    {
        for (StockTextureAliases stocktexturealiases = p_240348_1_; stocktexturealiases != null; stocktexturealiases = stocktexturealiases.getAlias())
        {
            ResourceLocation resourcelocation = this.field_240337_a_.get(stocktexturealiases);

            if (resourcelocation != null)
            {
                return resourcelocation;
            }
        }

        throw new IllegalStateException("Can't find texture for slot " + p_240348_1_);
    }

    public ModelTextures func_240360_c_(StockTextureAliases p_240360_1_, ResourceLocation p_240360_2_)
    {
        ModelTextures modeltextures = new ModelTextures();
        modeltextures.field_240337_a_.putAll(this.field_240337_a_);
        modeltextures.field_240338_b_.addAll(this.field_240338_b_);
        modeltextures.func_240349_a_(p_240360_1_, p_240360_2_);
        return modeltextures;
    }

    public static ModelTextures func_240345_a_(Block p_240345_0_)
    {
        ResourceLocation resourcelocation = func_240341_C_(p_240345_0_);
        return func_240356_b_(resourcelocation);
    }

    public static ModelTextures func_240353_b_(Block p_240353_0_)
    {
        ResourceLocation resourcelocation = func_240341_C_(p_240353_0_);
        return func_240350_a_(resourcelocation);
    }

    public static ModelTextures func_240350_a_(ResourceLocation p_240350_0_)
    {
        return (new ModelTextures()).func_240349_a_(StockTextureAliases.TEXTURE, p_240350_0_);
    }

    public static ModelTextures func_240356_b_(ResourceLocation p_240356_0_)
    {
        return (new ModelTextures()).func_240349_a_(StockTextureAliases.ALL, p_240356_0_);
    }

    public static ModelTextures func_240358_c_(Block p_240358_0_)
    {
        return func_240364_d_(StockTextureAliases.CROSS, func_240341_C_(p_240358_0_));
    }

    public static ModelTextures func_240361_c_(ResourceLocation p_240361_0_)
    {
        return func_240364_d_(StockTextureAliases.CROSS, p_240361_0_);
    }

    public static ModelTextures func_240362_d_(Block p_240362_0_)
    {
        return func_240364_d_(StockTextureAliases.PLANT, func_240341_C_(p_240362_0_));
    }

    public static ModelTextures func_240365_d_(ResourceLocation p_240365_0_)
    {
        return func_240364_d_(StockTextureAliases.PLANT, p_240365_0_);
    }

    public static ModelTextures func_240366_e_(Block p_240366_0_)
    {
        return func_240364_d_(StockTextureAliases.RAIL, func_240341_C_(p_240366_0_));
    }

    public static ModelTextures func_240367_e_(ResourceLocation p_240367_0_)
    {
        return func_240364_d_(StockTextureAliases.RAIL, p_240367_0_);
    }

    public static ModelTextures func_240368_f_(Block p_240368_0_)
    {
        return func_240364_d_(StockTextureAliases.WOOL, func_240341_C_(p_240368_0_));
    }

    public static ModelTextures func_240369_g_(Block p_240369_0_)
    {
        return func_240364_d_(StockTextureAliases.STEM, func_240341_C_(p_240369_0_));
    }

    public static ModelTextures func_240346_a_(Block p_240346_0_, Block p_240346_1_)
    {
        return (new ModelTextures()).func_240349_a_(StockTextureAliases.STEM, func_240341_C_(p_240346_0_)).func_240349_a_(StockTextureAliases.UPPERSTEM, func_240341_C_(p_240346_1_));
    }

    public static ModelTextures func_240371_h_(Block p_240371_0_)
    {
        return func_240364_d_(StockTextureAliases.PATTERN, func_240341_C_(p_240371_0_));
    }

    public static ModelTextures func_240373_i_(Block p_240373_0_)
    {
        return func_240364_d_(StockTextureAliases.FAN, func_240341_C_(p_240373_0_));
    }

    public static ModelTextures func_240370_g_(ResourceLocation p_240370_0_)
    {
        return func_240364_d_(StockTextureAliases.CROP, p_240370_0_);
    }

    public static ModelTextures func_240354_b_(Block p_240354_0_, Block p_240354_1_)
    {
        return (new ModelTextures()).func_240349_a_(StockTextureAliases.PANE, func_240341_C_(p_240354_0_)).func_240349_a_(StockTextureAliases.EDGE, func_240347_a_(p_240354_1_, "_top"));
    }

    public static ModelTextures func_240364_d_(StockTextureAliases p_240364_0_, ResourceLocation p_240364_1_)
    {
        return (new ModelTextures()).func_240349_a_(p_240364_0_, p_240364_1_);
    }

    public static ModelTextures func_240375_j_(Block p_240375_0_)
    {
        return (new ModelTextures()).func_240349_a_(StockTextureAliases.SIDE, func_240347_a_(p_240375_0_, "_side")).func_240349_a_(StockTextureAliases.END, func_240347_a_(p_240375_0_, "_top"));
    }

    public static ModelTextures func_240377_k_(Block p_240377_0_)
    {
        return (new ModelTextures()).func_240349_a_(StockTextureAliases.SIDE, func_240347_a_(p_240377_0_, "_side")).func_240349_a_(StockTextureAliases.TOP, func_240347_a_(p_240377_0_, "_top"));
    }

    public static ModelTextures func_240378_l_(Block p_240378_0_)
    {
        return (new ModelTextures()).func_240349_a_(StockTextureAliases.SIDE, func_240341_C_(p_240378_0_)).func_240349_a_(StockTextureAliases.END, func_240347_a_(p_240378_0_, "_top"));
    }

    public static ModelTextures func_240351_a_(ResourceLocation p_240351_0_, ResourceLocation p_240351_1_)
    {
        return (new ModelTextures()).func_240349_a_(StockTextureAliases.SIDE, p_240351_0_).func_240349_a_(StockTextureAliases.END, p_240351_1_);
    }

    public static ModelTextures func_240379_m_(Block p_240379_0_)
    {
        return (new ModelTextures()).func_240349_a_(StockTextureAliases.SIDE, func_240347_a_(p_240379_0_, "_side")).func_240349_a_(StockTextureAliases.TOP, func_240347_a_(p_240379_0_, "_top")).func_240349_a_(StockTextureAliases.BOTTOM, func_240347_a_(p_240379_0_, "_bottom"));
    }

    public static ModelTextures func_240380_n_(Block p_240380_0_)
    {
        ResourceLocation resourcelocation = func_240341_C_(p_240380_0_);
        return (new ModelTextures()).func_240349_a_(StockTextureAliases.WALL, resourcelocation).func_240349_a_(StockTextureAliases.SIDE, resourcelocation).func_240349_a_(StockTextureAliases.TOP, func_240347_a_(p_240380_0_, "_top")).func_240349_a_(StockTextureAliases.BOTTOM, func_240347_a_(p_240380_0_, "_bottom"));
    }

    public static ModelTextures func_240381_o_(Block p_240381_0_)
    {
        ResourceLocation resourcelocation = func_240341_C_(p_240381_0_);
        return (new ModelTextures()).func_240349_a_(StockTextureAliases.WALL, resourcelocation).func_240349_a_(StockTextureAliases.SIDE, resourcelocation).func_240349_a_(StockTextureAliases.END, func_240347_a_(p_240381_0_, "_top"));
    }

    public static ModelTextures func_240382_p_(Block p_240382_0_)
    {
        return (new ModelTextures()).func_240349_a_(StockTextureAliases.TOP, func_240347_a_(p_240382_0_, "_top")).func_240349_a_(StockTextureAliases.BOTTOM, func_240347_a_(p_240382_0_, "_bottom"));
    }

    public static ModelTextures func_240383_q_(Block p_240383_0_)
    {
        return (new ModelTextures()).func_240349_a_(StockTextureAliases.PARTICLE, func_240341_C_(p_240383_0_));
    }

    public static ModelTextures func_240372_h_(ResourceLocation p_240372_0_)
    {
        return (new ModelTextures()).func_240349_a_(StockTextureAliases.PARTICLE, p_240372_0_);
    }

    public static ModelTextures func_240384_r_(Block p_240384_0_)
    {
        return (new ModelTextures()).func_240349_a_(StockTextureAliases.FIRE, func_240347_a_(p_240384_0_, "_0"));
    }

    public static ModelTextures func_240385_s_(Block p_240385_0_)
    {
        return (new ModelTextures()).func_240349_a_(StockTextureAliases.FIRE, func_240347_a_(p_240385_0_, "_1"));
    }

    public static ModelTextures func_240386_t_(Block p_240386_0_)
    {
        return (new ModelTextures()).func_240349_a_(StockTextureAliases.LANTERN, func_240341_C_(p_240386_0_));
    }

    public static ModelTextures func_240387_u_(Block p_240387_0_)
    {
        return (new ModelTextures()).func_240349_a_(StockTextureAliases.TORCH, func_240341_C_(p_240387_0_));
    }

    public static ModelTextures func_240374_i_(ResourceLocation p_240374_0_)
    {
        return (new ModelTextures()).func_240349_a_(StockTextureAliases.TORCH, p_240374_0_);
    }

    public static ModelTextures func_240343_a_(Item p_240343_0_)
    {
        return (new ModelTextures()).func_240349_a_(StockTextureAliases.PARTICLE, func_240357_c_(p_240343_0_));
    }

    public static ModelTextures func_240388_v_(Block p_240388_0_)
    {
        return (new ModelTextures()).func_240349_a_(StockTextureAliases.SIDE, func_240347_a_(p_240388_0_, "_side")).func_240349_a_(StockTextureAliases.FRONT, func_240347_a_(p_240388_0_, "_front")).func_240349_a_(StockTextureAliases.BACK, func_240347_a_(p_240388_0_, "_back"));
    }

    public static ModelTextures func_240389_w_(Block p_240389_0_)
    {
        return (new ModelTextures()).func_240349_a_(StockTextureAliases.SIDE, func_240347_a_(p_240389_0_, "_side")).func_240349_a_(StockTextureAliases.FRONT, func_240347_a_(p_240389_0_, "_front")).func_240349_a_(StockTextureAliases.TOP, func_240347_a_(p_240389_0_, "_top")).func_240349_a_(StockTextureAliases.BOTTOM, func_240347_a_(p_240389_0_, "_bottom"));
    }

    public static ModelTextures func_240390_x_(Block p_240390_0_)
    {
        return (new ModelTextures()).func_240349_a_(StockTextureAliases.SIDE, func_240347_a_(p_240390_0_, "_side")).func_240349_a_(StockTextureAliases.FRONT, func_240347_a_(p_240390_0_, "_front")).func_240349_a_(StockTextureAliases.TOP, func_240347_a_(p_240390_0_, "_top"));
    }

    public static ModelTextures func_240391_y_(Block p_240391_0_)
    {
        return (new ModelTextures()).func_240349_a_(StockTextureAliases.SIDE, func_240347_a_(p_240391_0_, "_side")).func_240349_a_(StockTextureAliases.FRONT, func_240347_a_(p_240391_0_, "_front")).func_240349_a_(StockTextureAliases.END, func_240347_a_(p_240391_0_, "_end"));
    }

    public static ModelTextures func_240392_z_(Block p_240392_0_)
    {
        return (new ModelTextures()).func_240349_a_(StockTextureAliases.TOP, func_240347_a_(p_240392_0_, "_top"));
    }

    public static ModelTextures func_240359_c_(Block p_240359_0_, Block p_240359_1_)
    {
        return (new ModelTextures()).func_240349_a_(StockTextureAliases.PARTICLE, func_240347_a_(p_240359_0_, "_front")).func_240349_a_(StockTextureAliases.DOWN, func_240341_C_(p_240359_1_)).func_240349_a_(StockTextureAliases.UP, func_240347_a_(p_240359_0_, "_top")).func_240349_a_(StockTextureAliases.NORTH, func_240347_a_(p_240359_0_, "_front")).func_240349_a_(StockTextureAliases.EAST, func_240347_a_(p_240359_0_, "_side")).func_240349_a_(StockTextureAliases.SOUTH, func_240347_a_(p_240359_0_, "_side")).func_240349_a_(StockTextureAliases.WEST, func_240347_a_(p_240359_0_, "_front"));
    }

    public static ModelTextures func_240363_d_(Block p_240363_0_, Block p_240363_1_)
    {
        return (new ModelTextures()).func_240349_a_(StockTextureAliases.PARTICLE, func_240347_a_(p_240363_0_, "_front")).func_240349_a_(StockTextureAliases.DOWN, func_240341_C_(p_240363_1_)).func_240349_a_(StockTextureAliases.UP, func_240347_a_(p_240363_0_, "_top")).func_240349_a_(StockTextureAliases.NORTH, func_240347_a_(p_240363_0_, "_front")).func_240349_a_(StockTextureAliases.SOUTH, func_240347_a_(p_240363_0_, "_front")).func_240349_a_(StockTextureAliases.EAST, func_240347_a_(p_240363_0_, "_side")).func_240349_a_(StockTextureAliases.WEST, func_240347_a_(p_240363_0_, "_side"));
    }

    public static ModelTextures func_240339_A_(Block p_240339_0_)
    {
        return (new ModelTextures()).func_240349_a_(StockTextureAliases.LIT_LOG, func_240347_a_(p_240339_0_, "_log_lit")).func_240349_a_(StockTextureAliases.FIRE, func_240347_a_(p_240339_0_, "_fire"));
    }

    public static ModelTextures func_240352_b_(Item p_240352_0_)
    {
        return (new ModelTextures()).func_240349_a_(StockTextureAliases.LAYER_ZERO, func_240357_c_(p_240352_0_));
    }

    public static ModelTextures func_240340_B_(Block p_240340_0_)
    {
        return (new ModelTextures()).func_240349_a_(StockTextureAliases.LAYER_ZERO, func_240341_C_(p_240340_0_));
    }

    public static ModelTextures func_240376_j_(ResourceLocation p_240376_0_)
    {
        return (new ModelTextures()).func_240349_a_(StockTextureAliases.LAYER_ZERO, p_240376_0_);
    }

    public static ResourceLocation func_240341_C_(Block p_240341_0_)
    {
        ResourceLocation resourcelocation = Registry.BLOCK.getKey(p_240341_0_);
        return new ResourceLocation(resourcelocation.getNamespace(), "block/" + resourcelocation.getPath());
    }

    public static ResourceLocation func_240347_a_(Block p_240347_0_, String p_240347_1_)
    {
        ResourceLocation resourcelocation = Registry.BLOCK.getKey(p_240347_0_);
        return new ResourceLocation(resourcelocation.getNamespace(), "block/" + resourcelocation.getPath() + p_240347_1_);
    }

    public static ResourceLocation func_240357_c_(Item p_240357_0_)
    {
        ResourceLocation resourcelocation = Registry.ITEM.getKey(p_240357_0_);
        return new ResourceLocation(resourcelocation.getNamespace(), "item/" + resourcelocation.getPath());
    }

    public static ResourceLocation func_240344_a_(Item p_240344_0_, String p_240344_1_)
    {
        ResourceLocation resourcelocation = Registry.ITEM.getKey(p_240344_0_);
        return new ResourceLocation(resourcelocation.getNamespace(), "item/" + resourcelocation.getPath() + p_240344_1_);
    }
}
