package net.minecraft.entity.monster;

import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class GiantEntity extends MonsterEntity
{
    public GiantEntity(EntityType <? extends GiantEntity > type, World worldIn)
    {
        super(type, worldIn);
    }

    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn)
    {
        return 10.440001F;
    }

    public static AttributeModifierMap.MutableAttribute func_234291_m_()
    {
        return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 100.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.5D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 50.0D);
    }

    public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn)
    {
        return worldIn.getBrightness(pos) - 0.5F;
    }
}
