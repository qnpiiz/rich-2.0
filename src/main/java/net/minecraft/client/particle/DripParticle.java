package net.minecraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;

public class DripParticle extends SpriteTexturedParticle
{
    private final Fluid fluid;
    protected boolean fullbright;

    private DripParticle(ClientWorld world, double x, double y, double z, Fluid fluid)
    {
        super(world, x, y, z);
        this.setSize(0.01F, 0.01F);
        this.particleGravity = 0.06F;
        this.fluid = fluid;
    }

    public IParticleRenderType getRenderType()
    {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public int getBrightnessForRender(float partialTick)
    {
        return this.fullbright ? 240 : super.getBrightnessForRender(partialTick);
    }

    public void tick()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.ageParticle();

        if (!this.isExpired)
        {
            this.motionY -= (double)this.particleGravity;
            this.move(this.motionX, this.motionY, this.motionZ);
            this.updateMotion();

            if (!this.isExpired)
            {
                this.motionX *= (double)0.98F;
                this.motionY *= (double)0.98F;
                this.motionZ *= (double)0.98F;
                BlockPos blockpos = new BlockPos(this.posX, this.posY, this.posZ);
                FluidState fluidstate = this.world.getFluidState(blockpos);

                if (fluidstate.getFluid() == this.fluid && this.posY < (double)((float)blockpos.getY() + fluidstate.getActualHeight(this.world, blockpos)))
                {
                    this.setExpired();
                }
            }
        }
    }

    protected void ageParticle()
    {
        if (this.maxAge-- <= 0)
        {
            this.setExpired();
        }
    }

    protected void updateMotion()
    {
    }

    static class Dripping extends DripParticle
    {
        private final IParticleData particleData;

        private Dripping(ClientWorld world, double x, double y, double z, Fluid fluid, IParticleData particleData)
        {
            super(world, x, y, z, fluid);
            this.particleData = particleData;
            this.particleGravity *= 0.02F;
            this.maxAge = 40;
        }

        protected void ageParticle()
        {
            if (this.maxAge-- <= 0)
            {
                this.setExpired();
                this.world.addParticle(this.particleData, this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ);
            }
        }

        protected void updateMotion()
        {
            this.motionX *= 0.02D;
            this.motionY *= 0.02D;
            this.motionZ *= 0.02D;
        }
    }

    public static class DrippingHoneyFactory implements IParticleFactory<BasicParticleType>
    {
        protected final IAnimatedSprite spriteWithAge;

        public DrippingHoneyFactory(IAnimatedSprite spriteWithAge)
        {
            this.spriteWithAge = spriteWithAge;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            DripParticle.Dripping dripparticle$dripping = new DripParticle.Dripping(worldIn, x, y, z, Fluids.EMPTY, ParticleTypes.FALLING_HONEY);
            dripparticle$dripping.particleGravity *= 0.01F;
            dripparticle$dripping.maxAge = 100;
            dripparticle$dripping.setColor(0.622F, 0.508F, 0.082F);
            dripparticle$dripping.selectSpriteRandomly(this.spriteWithAge);
            return dripparticle$dripping;
        }
    }

    static class DrippingLava extends DripParticle.Dripping
    {
        private DrippingLava(ClientWorld world, double x, double y, double z, Fluid fluid, IParticleData particleData)
        {
            super(world, x, y, z, fluid, particleData);
        }

        protected void ageParticle()
        {
            this.particleRed = 1.0F;
            this.particleGreen = 16.0F / (float)(40 - this.maxAge + 16);
            this.particleBlue = 4.0F / (float)(40 - this.maxAge + 8);
            super.ageParticle();
        }
    }

    public static class DrippingLavaFactory implements IParticleFactory<BasicParticleType>
    {
        protected final IAnimatedSprite spriteSet;

        public DrippingLavaFactory(IAnimatedSprite spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            DripParticle.DrippingLava dripparticle$drippinglava = new DripParticle.DrippingLava(worldIn, x, y, z, Fluids.LAVA, ParticleTypes.FALLING_LAVA);
            dripparticle$drippinglava.selectSpriteRandomly(this.spriteSet);
            return dripparticle$drippinglava;
        }
    }

    public static class DrippingObsidianTearFactory implements IParticleFactory<BasicParticleType>
    {
        protected final IAnimatedSprite spriteSet;

        public DrippingObsidianTearFactory(IAnimatedSprite spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            DripParticle.Dripping dripparticle$dripping = new DripParticle.Dripping(worldIn, x, y, z, Fluids.EMPTY, ParticleTypes.FALLING_OBSIDIAN_TEAR);
            dripparticle$dripping.fullbright = true;
            dripparticle$dripping.particleGravity *= 0.01F;
            dripparticle$dripping.maxAge = 100;
            dripparticle$dripping.setColor(0.51171875F, 0.03125F, 0.890625F);
            dripparticle$dripping.selectSpriteRandomly(this.spriteSet);
            return dripparticle$dripping;
        }
    }

    public static class DrippingWaterFactory implements IParticleFactory<BasicParticleType>
    {
        protected final IAnimatedSprite spriteSet;

        public DrippingWaterFactory(IAnimatedSprite spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            DripParticle dripparticle = new DripParticle.Dripping(worldIn, x, y, z, Fluids.WATER, ParticleTypes.FALLING_WATER);
            dripparticle.setColor(0.2F, 0.3F, 1.0F);
            dripparticle.selectSpriteRandomly(this.spriteSet);
            return dripparticle;
        }
    }

    public static class FallingHoneyFactory implements IParticleFactory<BasicParticleType>
    {
        protected final IAnimatedSprite spriteSet;

        public FallingHoneyFactory(IAnimatedSprite spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            DripParticle dripparticle = new DripParticle.FallingHoneyParticle(worldIn, x, y, z, Fluids.EMPTY, ParticleTypes.LANDING_HONEY);
            dripparticle.particleGravity = 0.01F;
            dripparticle.setColor(0.582F, 0.448F, 0.082F);
            dripparticle.selectSpriteRandomly(this.spriteSet);
            return dripparticle;
        }
    }

    static class FallingHoneyParticle extends DripParticle.FallingLiquidParticle
    {
        private FallingHoneyParticle(ClientWorld world, double x, double y, double z, Fluid fluid, IParticleData particleData)
        {
            super(world, x, y, z, fluid, particleData);
        }

        protected void updateMotion()
        {
            if (this.onGround)
            {
                this.setExpired();
                this.world.addParticle(this.particleData, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
                this.world.playSound(this.posX + 0.5D, this.posY, this.posZ + 0.5D, SoundEvents.BLOCK_BEEHIVE_DROP, SoundCategory.BLOCKS, 0.3F + this.world.rand.nextFloat() * 2.0F / 3.0F, 1.0F, false);
            }
        }
    }

    public static class FallingLavaFactory implements IParticleFactory<BasicParticleType>
    {
        protected final IAnimatedSprite spriteSet;

        public FallingLavaFactory(IAnimatedSprite spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            DripParticle dripparticle = new DripParticle.FallingLiquidParticle(worldIn, x, y, z, Fluids.LAVA, ParticleTypes.LANDING_LAVA);
            dripparticle.setColor(1.0F, 0.2857143F, 0.083333336F);
            dripparticle.selectSpriteRandomly(this.spriteSet);
            return dripparticle;
        }
    }

    static class FallingLiquidParticle extends DripParticle.FallingNectarParticle
    {
        protected final IParticleData particleData;

        private FallingLiquidParticle(ClientWorld world, double x, double y, double z, Fluid fluid, IParticleData particleData)
        {
            super(world, x, y, z, fluid);
            this.particleData = particleData;
        }

        protected void updateMotion()
        {
            if (this.onGround)
            {
                this.setExpired();
                this.world.addParticle(this.particleData, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    public static class FallingNectarFactory implements IParticleFactory<BasicParticleType>
    {
        protected final IAnimatedSprite spriteSet;

        public FallingNectarFactory(IAnimatedSprite spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            DripParticle dripparticle = new DripParticle.FallingNectarParticle(worldIn, x, y, z, Fluids.EMPTY);
            dripparticle.maxAge = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
            dripparticle.particleGravity = 0.007F;
            dripparticle.setColor(0.92F, 0.782F, 0.72F);
            dripparticle.selectSpriteRandomly(this.spriteSet);
            return dripparticle;
        }
    }

    static class FallingNectarParticle extends DripParticle
    {
        private FallingNectarParticle(ClientWorld world, double x, double y, double z, Fluid fluid)
        {
            super(world, x, y, z, fluid);
            this.maxAge = (int)(64.0D / (Math.random() * 0.8D + 0.2D));
        }

        protected void updateMotion()
        {
            if (this.onGround)
            {
                this.setExpired();
            }
        }
    }

    public static class FallingObsidianTearFactory implements IParticleFactory<BasicParticleType>
    {
        protected final IAnimatedSprite spriteSet;

        public FallingObsidianTearFactory(IAnimatedSprite spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            DripParticle dripparticle = new DripParticle.FallingLiquidParticle(worldIn, x, y, z, Fluids.EMPTY, ParticleTypes.LANDING_OBSIDIAN_TEAR);
            dripparticle.fullbright = true;
            dripparticle.particleGravity = 0.01F;
            dripparticle.setColor(0.51171875F, 0.03125F, 0.890625F);
            dripparticle.selectSpriteRandomly(this.spriteSet);
            return dripparticle;
        }
    }

    public static class FallingWaterFactory implements IParticleFactory<BasicParticleType>
    {
        protected final IAnimatedSprite spriteSet;

        public FallingWaterFactory(IAnimatedSprite spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            DripParticle dripparticle = new DripParticle.FallingLiquidParticle(worldIn, x, y, z, Fluids.WATER, ParticleTypes.SPLASH);
            dripparticle.setColor(0.2F, 0.3F, 1.0F);
            dripparticle.selectSpriteRandomly(this.spriteSet);
            return dripparticle;
        }
    }

    static class Landing extends DripParticle
    {
        private Landing(ClientWorld world, double x, double y, double z, Fluid fluid)
        {
            super(world, x, y, z, fluid);
            this.maxAge = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
        }
    }

    public static class LandingHoneyFactory implements IParticleFactory<BasicParticleType>
    {
        protected final IAnimatedSprite spriteSet;

        public LandingHoneyFactory(IAnimatedSprite spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            DripParticle dripparticle = new DripParticle.Landing(worldIn, x, y, z, Fluids.EMPTY);
            dripparticle.maxAge = (int)(128.0D / (Math.random() * 0.8D + 0.2D));
            dripparticle.setColor(0.522F, 0.408F, 0.082F);
            dripparticle.selectSpriteRandomly(this.spriteSet);
            return dripparticle;
        }
    }

    public static class LandingLavaFactory implements IParticleFactory<BasicParticleType>
    {
        protected final IAnimatedSprite spriteSet;

        public LandingLavaFactory(IAnimatedSprite spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            DripParticle dripparticle = new DripParticle.Landing(worldIn, x, y, z, Fluids.LAVA);
            dripparticle.setColor(1.0F, 0.2857143F, 0.083333336F);
            dripparticle.selectSpriteRandomly(this.spriteSet);
            return dripparticle;
        }
    }

    public static class LandingObsidianTearFactory implements IParticleFactory<BasicParticleType>
    {
        protected final IAnimatedSprite spriteSet;

        public LandingObsidianTearFactory(IAnimatedSprite spriteSet)
        {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            DripParticle dripparticle = new DripParticle.Landing(worldIn, x, y, z, Fluids.EMPTY);
            dripparticle.fullbright = true;
            dripparticle.maxAge = (int)(28.0D / (Math.random() * 0.8D + 0.2D));
            dripparticle.setColor(0.51171875F, 0.03125F, 0.890625F);
            dripparticle.selectSpriteRandomly(this.spriteSet);
            return dripparticle;
        }
    }
}
