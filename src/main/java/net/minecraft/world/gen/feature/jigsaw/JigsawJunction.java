package net.minecraft.world.gen.feature.jigsaw;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;

public class JigsawJunction
{
    private final int sourceX;
    private final int sourceGroundY;
    private final int sourceZ;
    private final int deltaY;
    private final JigsawPattern.PlacementBehaviour destProjection;

    public JigsawJunction(int sourceX, int sourceGroundY, int sourceZ, int deltaY, JigsawPattern.PlacementBehaviour destProjection)
    {
        this.sourceX = sourceX;
        this.sourceGroundY = sourceGroundY;
        this.sourceZ = sourceZ;
        this.deltaY = deltaY;
        this.destProjection = destProjection;
    }

    public int getSourceX()
    {
        return this.sourceX;
    }

    public int getSourceGroundY()
    {
        return this.sourceGroundY;
    }

    public int getSourceZ()
    {
        return this.sourceZ;
    }

    public <T> Dynamic<T> func_236820_a_(DynamicOps<T> p_236820_1_)
    {
        Builder<T, T> builder = ImmutableMap.builder();
        builder.put(p_236820_1_.createString("source_x"), p_236820_1_.createInt(this.sourceX)).put(p_236820_1_.createString("source_ground_y"), p_236820_1_.createInt(this.sourceGroundY)).put(p_236820_1_.createString("source_z"), p_236820_1_.createInt(this.sourceZ)).put(p_236820_1_.createString("delta_y"), p_236820_1_.createInt(this.deltaY)).put(p_236820_1_.createString("dest_proj"), p_236820_1_.createString(this.destProjection.getName()));
        return new Dynamic<>(p_236820_1_, p_236820_1_.createMap(builder.build()));
    }

    public static <T> JigsawJunction func_236819_a_(Dynamic<T> p_236819_0_)
    {
        return new JigsawJunction(p_236819_0_.get("source_x").asInt(0), p_236819_0_.get("source_ground_y").asInt(0), p_236819_0_.get("source_z").asInt(0), p_236819_0_.get("delta_y").asInt(0), JigsawPattern.PlacementBehaviour.getBehaviour(p_236819_0_.get("dest_proj").asString("")));
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass())
        {
            JigsawJunction jigsawjunction = (JigsawJunction)p_equals_1_;

            if (this.sourceX != jigsawjunction.sourceX)
            {
                return false;
            }
            else if (this.sourceZ != jigsawjunction.sourceZ)
            {
                return false;
            }
            else if (this.deltaY != jigsawjunction.deltaY)
            {
                return false;
            }
            else
            {
                return this.destProjection == jigsawjunction.destProjection;
            }
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        int i = this.sourceX;
        i = 31 * i + this.sourceGroundY;
        i = 31 * i + this.sourceZ;
        i = 31 * i + this.deltaY;
        return 31 * i + this.destProjection.hashCode();
    }

    public String toString()
    {
        return "JigsawJunction{sourceX=" + this.sourceX + ", sourceGroundY=" + this.sourceGroundY + ", sourceZ=" + this.sourceZ + ", deltaY=" + this.deltaY + ", destProjection=" + this.destProjection + '}';
    }
}
