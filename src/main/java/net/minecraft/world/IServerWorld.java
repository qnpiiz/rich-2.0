package net.minecraft.world;

import net.minecraft.entity.Entity;
import net.minecraft.world.server.ServerWorld;

public interface IServerWorld extends IWorld
{
    ServerWorld getWorld();

default void func_242417_l(Entity p_242417_1_)
    {
        p_242417_1_.getSelfAndPassengers().forEach(this::addEntity);
    }
}
