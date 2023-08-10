package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractUUIDFix extends DataFix
{
    protected static final Logger LOGGER = LogManager.getLogger();
    protected TypeReference reference;

    public AbstractUUIDFix(Schema outputSchema, TypeReference reference)
    {
        super(outputSchema, false);
        this.reference = reference;
    }

    protected Typed<?> func_233053_a_(Typed<?> p_233053_1_, String p_233053_2_, Function < Dynamic<?>, Dynamic<? >> p_233053_3_)
    {
        Type<?> type = this.getInputSchema().getChoiceType(this.reference, p_233053_2_);
        Type<?> type1 = this.getOutputSchema().getChoiceType(this.reference, p_233053_2_);
        return p_233053_1_.updateTyped(DSL.namedChoice(p_233053_2_, type), type1, (p_233061_1_) ->
        {
            return p_233061_1_.update(DSL.remainderFinder(), p_233053_3_);
        });
    }

    protected static Optional < Dynamic<? >> func_233058_a_(Dynamic<?> p_233058_0_, String p_233058_1_, String p_233058_2_)
    {
        return func_233057_a_(p_233058_0_, p_233058_1_).map((p_233063_3_) ->
        {
            return p_233058_0_.remove(p_233058_1_).set(p_233058_2_, p_233063_3_);
        });
    }

    protected static Optional < Dynamic<? >> func_233062_b_(Dynamic<?> p_233062_0_, String p_233062_1_, String p_233062_2_)
    {
        return p_233062_0_.get(p_233062_1_).result().flatMap(AbstractUUIDFix::func_233054_a_).map((p_233059_3_) ->
        {
            return p_233062_0_.remove(p_233062_1_).set(p_233062_2_, p_233059_3_);
        });
    }

    protected static Optional < Dynamic<? >> func_233064_c_(Dynamic<?> p_233064_0_, String p_233064_1_, String p_233064_2_)
    {
        String s = p_233064_1_ + "Most";
        String s1 = p_233064_1_ + "Least";
        return func_233065_d_(p_233064_0_, s, s1).map((p_233060_4_) ->
        {
            return p_233064_0_.remove(s).remove(s1).set(p_233064_2_, p_233060_4_);
        });
    }

    protected static Optional < Dynamic<? >> func_233057_a_(Dynamic<?> p_233057_0_, String p_233057_1_)
    {
        return p_233057_0_.get(p_233057_1_).result().flatMap((p_233056_1_) ->
        {
            String s = p_233056_1_.asString((String)null);

            if (s != null)
            {
                try
                {
                    UUID uuid = UUID.fromString(s);
                    return func_233055_a_(p_233057_0_, uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
                }
                catch (IllegalArgumentException illegalargumentexception)
                {
                }
            }

            return Optional.empty();
        });
    }

    protected static Optional < Dynamic<? >> func_233054_a_(Dynamic<?> p_233054_0_)
    {
        return func_233065_d_(p_233054_0_, "M", "L");
    }

    protected static Optional < Dynamic<? >> func_233065_d_(Dynamic<?> p_233065_0_, String p_233065_1_, String p_233065_2_)
    {
        long i = p_233065_0_.get(p_233065_1_).asLong(0L);
        long j = p_233065_0_.get(p_233065_2_).asLong(0L);
        return i != 0L && j != 0L ? func_233055_a_(p_233065_0_, i, j) : Optional.empty();
    }

    protected static Optional < Dynamic<? >> func_233055_a_(Dynamic<?> p_233055_0_, long p_233055_1_, long p_233055_3_)
    {
        return Optional.of(p_233055_0_.createIntList(Arrays.stream(new int[] {(int)(p_233055_1_ >> 32), (int)p_233055_1_, (int)(p_233055_3_ >> 32), (int)p_233055_3_})));
    }
}
