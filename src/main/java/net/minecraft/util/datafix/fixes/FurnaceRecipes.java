package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class FurnaceRecipes extends DataFix
{
    public FurnaceRecipes(Schema p_i231454_1_, boolean p_i231454_2_)
    {
        super(p_i231454_1_, p_i231454_2_);
    }

    protected TypeRewriteRule makeRule()
    {
        return this.func_233248_a_(this.getOutputSchema().getTypeRaw(TypeReferences.RECIPE));
    }

    private <R> TypeRewriteRule func_233248_a_(Type<R> p_233248_1_)
    {
        Type < Pair < Either < Pair < List<Pair<R, Integer>>, Dynamic<? >> , Unit > , Dynamic<? >>> type = DSL.and(DSL.optional(DSL.field("RecipesUsed", DSL.and(DSL.compoundList(p_233248_1_, DSL.intType()), DSL.remainderType()))), DSL.remainderType());
        OpticFinder<?> opticfinder = DSL.namedChoice("minecraft:furnace", this.getInputSchema().getChoiceType(TypeReferences.BLOCK_ENTITY, "minecraft:furnace"));
        OpticFinder<?> opticfinder1 = DSL.namedChoice("minecraft:blast_furnace", this.getInputSchema().getChoiceType(TypeReferences.BLOCK_ENTITY, "minecraft:blast_furnace"));
        OpticFinder<?> opticfinder2 = DSL.namedChoice("minecraft:smoker", this.getInputSchema().getChoiceType(TypeReferences.BLOCK_ENTITY, "minecraft:smoker"));
        Type<?> type1 = this.getOutputSchema().getChoiceType(TypeReferences.BLOCK_ENTITY, "minecraft:furnace");
        Type<?> type2 = this.getOutputSchema().getChoiceType(TypeReferences.BLOCK_ENTITY, "minecraft:blast_furnace");
        Type<?> type3 = this.getOutputSchema().getChoiceType(TypeReferences.BLOCK_ENTITY, "minecraft:smoker");
        Type<?> type4 = this.getInputSchema().getType(TypeReferences.BLOCK_ENTITY);
        Type<?> type5 = this.getOutputSchema().getType(TypeReferences.BLOCK_ENTITY);
        return this.fixTypeEverywhereTyped("FurnaceRecipesFix", type4, type5, (p_233247_9_) ->
        {
            return p_233247_9_.updateTyped(opticfinder, type1, (p_233254_3_) -> {
                return this.func_233249_a_(p_233248_1_, type, p_233254_3_);
            }).updateTyped(opticfinder1, type2, (p_233253_3_) -> {
                return this.func_233249_a_(p_233248_1_, type, p_233253_3_);
            }).updateTyped(opticfinder2, type3, (p_233252_3_) -> {
                return this.func_233249_a_(p_233248_1_, type, p_233252_3_);
            });
        });
    }

    private <R> Typed<?> func_233249_a_(Type<R> p_233249_1_, Type < Pair < Either < Pair < List<Pair<R, Integer>>, Dynamic<? >> , Unit > , Dynamic<? >>> p_233249_2_, Typed<?> p_233249_3_)
    {
        Dynamic<?> dynamic = p_233249_3_.getOrCreate(DSL.remainderFinder());
        int i = dynamic.get("RecipesUsedSize").asInt(0);
        dynamic = dynamic.remove("RecipesUsedSize");
        List<Pair<R, Integer>> list = Lists.newArrayList();

        for (int j = 0; j < i; ++j)
        {
            String s = "RecipeLocation" + j;
            String s1 = "RecipeAmount" + j;
            Optional <? extends Dynamic<? >> optional = dynamic.get(s).result();
            int k = dynamic.get(s1).asInt(0);

            if (k > 0)
            {
                optional.ifPresent((p_233250_3_) ->
                {
                    Optional <? extends Pair < R, ? extends Dynamic<? >>> optional1 = p_233249_1_.read(p_233250_3_).result();
                    optional1.ifPresent((p_233251_2_) -> {
                        list.add(Pair.of(p_233251_2_.getFirst(), k));
                    });
                });
            }

            dynamic = dynamic.remove(s).remove(s1);
        }

        return p_233249_3_.set(DSL.remainderFinder(), p_233249_2_, Pair.of(Either.left(Pair.of(list, dynamic.emptyMap())), dynamic));
    }
}
