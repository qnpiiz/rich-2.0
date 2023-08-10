package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.stream.LongStream;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.math.MathHelper;

public class BitStorageAlignFix extends DataFix
{
    public BitStorageAlignFix(Schema p_i231446_1_)
    {
        super(p_i231446_1_, false);
    }

    protected TypeRewriteRule makeRule()
    {
        Type<?> type = this.getInputSchema().getType(TypeReferences.CHUNK);
        Type<?> type1 = type.findFieldType("Level");
        OpticFinder<?> opticfinder = DSL.fieldFinder("Level", type1);
        OpticFinder<?> opticfinder1 = opticfinder.type().findField("Sections");
        Type<?> type2 = ((ListType)opticfinder1.type()).getElement();
        OpticFinder<?> opticfinder2 = DSL.typeFinder(type2);
        Type < Pair < String, Dynamic<? >>> type3 = DSL.named(TypeReferences.BLOCK_STATE.typeName(), DSL.remainderType());
        OpticFinder < List < Pair < String, Dynamic<? >>> > opticfinder3 = DSL.fieldFinder("Palette", DSL.list(type3));
        return this.fixTypeEverywhereTyped("BitStorageAlignFix", type, this.getOutputSchema().getType(TypeReferences.CHUNK), (p_233088_5_) ->
        {
            return p_233088_5_.updateTyped(opticfinder, (p_233099_4_) -> {
                return this.func_233092_a_(func_233089_a_(opticfinder1, opticfinder2, opticfinder3, p_233099_4_));
            });
        });
    }

    private Typed<?> func_233092_a_(Typed<?> p_233092_1_)
    {
        return p_233092_1_.update(DSL.remainderFinder(), (p_233093_0_) ->
        {
            return p_233093_0_.update("Heightmaps", (p_233096_1_) -> {
                return p_233096_1_.updateMapValues((p_233095_1_) -> {
                    return p_233095_1_.mapSecond((p_233100_1_) -> {
                        return func_233097_a_(p_233093_0_, p_233100_1_, 256, 9);
                    });
                });
            });
        });
    }

    private static Typed<?> func_233089_a_(OpticFinder<?> p_233089_0_, OpticFinder<?> p_233089_1_, OpticFinder < List < Pair < String, Dynamic<? >>> > p_233089_2_, Typed<?> p_233089_3_)
    {
        return p_233089_3_.updateTyped(p_233089_0_, (p_233090_2_) ->
        {
            return p_233090_2_.updateTyped(p_233089_1_, (p_233091_1_) -> {
                int i = p_233091_1_.getOptional(p_233089_2_).map((p_233098_0_) -> {
                    return Math.max(4, DataFixUtils.ceillog2(p_233098_0_.size()));
                }).orElse(0);
                return i != 0 && !MathHelper.isPowerOfTwo(i) ? p_233091_1_.update(DSL.remainderFinder(), (p_233087_1_) -> {
                    return p_233087_1_.update("BlockStates", (p_233094_2_) -> {
                        return func_233097_a_(p_233087_1_, p_233094_2_, 4096, i);
                    });
                }) : p_233091_1_;
            });
        });
    }

    private static Dynamic<?> func_233097_a_(Dynamic<?> p_233097_0_, Dynamic<?> p_233097_1_, int p_233097_2_, int p_233097_3_)
    {
        long[] along = p_233097_1_.asLongStream().toArray();
        long[] along1 = func_233086_a_(p_233097_2_, p_233097_3_, along);
        return p_233097_0_.createLongList(LongStream.of(along1));
    }

    public static long[] func_233086_a_(int p_233086_0_, int p_233086_1_, long[] p_233086_2_)
    {
        int i = p_233086_2_.length;

        if (i == 0)
        {
            return p_233086_2_;
        }
        else
        {
            long j = (1L << p_233086_1_) - 1L;
            int k = 64 / p_233086_1_;
            int l = (p_233086_0_ + k - 1) / k;
            long[] along = new long[l];
            int i1 = 0;
            int j1 = 0;
            long k1 = 0L;
            int l1 = 0;
            long i2 = p_233086_2_[0];
            long j2 = i > 1 ? p_233086_2_[1] : 0L;

            for (int k2 = 0; k2 < p_233086_0_; ++k2)
            {
                int l2 = k2 * p_233086_1_;
                int i3 = l2 >> 6;
                int j3 = (k2 + 1) * p_233086_1_ - 1 >> 6;
                int k3 = l2 ^ i3 << 6;

                if (i3 != l1)
                {
                    i2 = j2;
                    j2 = i3 + 1 < i ? p_233086_2_[i3 + 1] : 0L;
                    l1 = i3;
                }

                long l3;

                if (i3 == j3)
                {
                    l3 = i2 >>> k3 & j;
                }
                else
                {
                    int i4 = 64 - k3;
                    l3 = (i2 >>> k3 | j2 << i4) & j;
                }

                int j4 = j1 + p_233086_1_;

                if (j4 >= 64)
                {
                    along[i1++] = k1;
                    k1 = l3;
                    j1 = p_233086_1_;
                }
                else
                {
                    k1 |= l3 << j1;
                    j1 = j4;
                }
            }

            if (k1 != 0L)
            {
                along[i1] = k1;
            }

            return along;
        }
    }
}
