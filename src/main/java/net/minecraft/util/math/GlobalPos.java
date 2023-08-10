package net.minecraft.util.math;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;

public final class GlobalPos
{
    public static final Codec<GlobalPos> CODEC = RecordCodecBuilder.create((builderInstance) ->
    {
        return builderInstance.group(World.CODEC.fieldOf("dimension").forGetter(GlobalPos::getDimension), BlockPos.CODEC.fieldOf("pos").forGetter(GlobalPos::getPos)).apply(builderInstance, GlobalPos::getPosition);
    });
    private final RegistryKey<World> dimension;
    private final BlockPos pos;

    private GlobalPos(RegistryKey<World> dimension, BlockPos pos)
    {
        this.dimension = dimension;
        this.pos = pos;
    }

    public static GlobalPos getPosition(RegistryKey<World> dimension, BlockPos pos)
    {
        return new GlobalPos(dimension, pos);
    }

    public RegistryKey<World> getDimension()
    {
        return this.dimension;
    }

    public BlockPos getPos()
    {
        return this.pos;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass())
        {
            GlobalPos globalpos = (GlobalPos)p_equals_1_;
            return Objects.equals(this.dimension, globalpos.dimension) && Objects.equals(this.pos, globalpos.pos);
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return Objects.hash(this.dimension, this.pos);
    }

    public String toString()
    {
        return this.dimension.toString() + " " + this.pos;
    }
}
