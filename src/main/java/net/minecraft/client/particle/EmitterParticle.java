package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.vector.Vector3d;

public class EmitterParticle extends MetaParticle
{
    private final Entity attachedEntity;
    private int age;
    private final int lifetime;
    private final IParticleData particleTypes;

    public EmitterParticle(ClientWorld world, Entity entity, IParticleData particleData)
    {
        this(world, entity, particleData, 3);
    }

    public EmitterParticle(ClientWorld world, Entity entity, IParticleData particleData, int lifetime)
    {
        this(world, entity, particleData, lifetime, entity.getMotion());
    }

    private EmitterParticle(ClientWorld world, Entity entity, IParticleData particleData, int lifetime, Vector3d motionVector)
    {
        super(world, entity.getPosX(), entity.getPosYHeight(0.5D), entity.getPosZ(), motionVector.x, motionVector.y, motionVector.z);
        this.attachedEntity = entity;
        this.lifetime = lifetime;
        this.particleTypes = particleData;
        this.tick();
    }

    public void tick()
    {
        for (int i = 0; i < 16; ++i)
        {
            double d0 = (double)(this.rand.nextFloat() * 2.0F - 1.0F);
            double d1 = (double)(this.rand.nextFloat() * 2.0F - 1.0F);
            double d2 = (double)(this.rand.nextFloat() * 2.0F - 1.0F);

            if (!(d0 * d0 + d1 * d1 + d2 * d2 > 1.0D))
            {
                double d3 = this.attachedEntity.getPosXWidth(d0 / 4.0D);
                double d4 = this.attachedEntity.getPosYHeight(0.5D + d1 / 4.0D);
                double d5 = this.attachedEntity.getPosZWidth(d2 / 4.0D);
                this.world.addParticle(this.particleTypes, false, d3, d4, d5, d0, d1 + 0.2D, d2);
            }
        }

        ++this.age;

        if (this.age >= this.lifetime)
        {
            this.setExpired();
        }
    }
}
