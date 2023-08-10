package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.TypeReferences;

public class SavedDataUUID extends AbstractUUIDFix
{
    public SavedDataUUID(Schema p_i231465_1_)
    {
        super(p_i231465_1_, TypeReferences.SAVED_DATA);
    }

    protected TypeRewriteRule makeRule()
    {
        return this.fixTypeEverywhereTyped("SavedDataUUIDFix", this.getInputSchema().getType(this.reference), (p_233386_0_) ->
        {
            return p_233386_0_.updateTyped(p_233386_0_.getType().findField("data"), (p_233387_0_) -> {
                return p_233387_0_.update(DSL.remainderFinder(), (p_233388_0_) -> {
                    return p_233388_0_.update("Raids", (p_233389_0_) -> {
                        return p_233389_0_.createList(p_233389_0_.asStream().map((p_233390_0_) -> {
                            return p_233390_0_.update("HeroesOfTheVillage", (p_233391_0_) -> {
                                return p_233391_0_.createList(p_233391_0_.asStream().map((p_233392_0_) -> {
                                    return func_233065_d_(p_233392_0_, "UUIDMost", "UUIDLeast").orElseGet(() -> {
                                        LOGGER.warn("HeroesOfTheVillage contained invalid UUIDs.");
                                        return p_233392_0_;
                                    });
                                }));
                            });
                        }));
                    });
                });
            });
        });
    }
}
