package net.minecraft.village;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class PointOfInterest
{
    private final BlockPos pos;
    private final PointOfInterestType type;
    private int freeTickets;
    private final Runnable onChange;

    public static Codec<PointOfInterest> func_234150_a_(Runnable p_234150_0_)
    {
        return RecordCodecBuilder.create((p_234151_1_) ->
        {
            return p_234151_1_.group(BlockPos.CODEC.fieldOf("pos").forGetter((point) -> {
                return point.pos;
            }), Registry.POINT_OF_INTEREST_TYPE.fieldOf("type").forGetter((point) -> {
                return point.type;
            }), Codec.INT.fieldOf("free_tickets").orElse(0).forGetter((p_234149_0_) -> {
                return p_234149_0_.freeTickets;
            }), RecordCodecBuilder.point(p_234150_0_)).apply(p_234151_1_, PointOfInterest::new);
        });
    }

    private PointOfInterest(BlockPos posIn, PointOfInterestType typeIn, int freeTicketsIn, Runnable onChangeIn)
    {
        this.pos = posIn.toImmutable();
        this.type = typeIn;
        this.freeTickets = freeTicketsIn;
        this.onChange = onChangeIn;
    }

    public PointOfInterest(BlockPos posIn, PointOfInterestType typeIn, Runnable onChangeIn)
    {
        this(posIn, typeIn, typeIn.getMaxFreeTickets(), onChangeIn);
    }

    protected boolean claim()
    {
        if (this.freeTickets <= 0)
        {
            return false;
        }
        else
        {
            --this.freeTickets;
            this.onChange.run();
            return true;
        }
    }

    protected boolean release()
    {
        if (this.freeTickets >= this.type.getMaxFreeTickets())
        {
            return false;
        }
        else
        {
            ++this.freeTickets;
            this.onChange.run();
            return true;
        }
    }

    public boolean hasSpace()
    {
        return this.freeTickets > 0;
    }

    public boolean isOccupied()
    {
        return this.freeTickets != this.type.getMaxFreeTickets();
    }

    public BlockPos getPos()
    {
        return this.pos;
    }

    public PointOfInterestType getType()
    {
        return this.type;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else
        {
            return p_equals_1_ != null && this.getClass() == p_equals_1_.getClass() ? Objects.equals(this.pos, ((PointOfInterest)p_equals_1_).pos) : false;
        }
    }

    public int hashCode()
    {
        return this.pos.hashCode();
    }
}
