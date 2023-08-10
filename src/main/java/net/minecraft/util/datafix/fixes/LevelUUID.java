package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.TypeReferences;

public class LevelUUID extends AbstractUUIDFix
{
    public LevelUUID(Schema p_i231459_1_)
    {
        super(p_i231459_1_, TypeReferences.LEVEL);
    }

    protected TypeRewriteRule makeRule()
    {
        return this.fixTypeEverywhereTyped("LevelUUIDFix", this.getInputSchema().getType(this.reference), (p_233308_1_) ->
        {
            return p_233308_1_.updateTyped(DSL.remainderFinder(), (p_233311_1_) -> {
                return p_233311_1_.update(DSL.remainderFinder(), (p_233323_1_) -> {
                    p_233323_1_ = this.func_233315_d_(p_233323_1_);
                    p_233323_1_ = this.func_233314_c_(p_233323_1_);
                    return this.func_233313_b_(p_233323_1_);
                });
            });
        });
    }

    private Dynamic<?> func_233313_b_(Dynamic<?> p_233313_1_)
    {
        return func_233058_a_(p_233313_1_, "WanderingTraderId", "WanderingTraderId").orElse(p_233313_1_);
    }

    private Dynamic<?> func_233314_c_(Dynamic<?> p_233314_1_)
    {
        return p_233314_1_.update("DimensionData", (p_233320_0_) ->
        {
            return p_233320_0_.updateMapValues((p_233312_0_) -> {
                return p_233312_0_.mapSecond((p_233321_0_) -> {
                    return p_233321_0_.update("DragonFight", (p_233322_0_) -> {
                        return func_233064_c_(p_233322_0_, "DragonUUID", "Dragon").orElse(p_233322_0_);
                    });
                });
            });
        });
    }

    private Dynamic<?> func_233315_d_(Dynamic<?> p_233315_1_)
    {
        return p_233315_1_.update("CustomBossEvents", (p_233316_0_) ->
        {
            return p_233316_0_.updateMapValues((p_233309_0_) -> {
                return p_233309_0_.mapSecond((p_233317_0_) -> {
                    return p_233317_0_.update("Players", (p_233310_1_) -> {
                        return p_233317_0_.createList(p_233310_1_.asStream().map((p_233318_0_) -> {
                            return func_233054_a_(p_233318_0_).orElseGet(() -> {
                                LOGGER.warn("CustomBossEvents contains invalid UUIDs.");
                                return p_233318_0_;
                            });
                        }));
                    });
                });
            });
        });
    }
}
