package net.minecraft.entity.projectile;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class FishingBobberEntity extends ProjectileEntity
{
    private final Random field_234596_b_ = new Random();
    private boolean field_234597_c_;
    private int field_234598_d_;
    private static final DataParameter<Integer> DATA_HOOKED_ENTITY = EntityDataManager.createKey(FishingBobberEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> field_234599_f_ = EntityDataManager.createKey(FishingBobberEntity.class, DataSerializers.BOOLEAN);
    private int ticksInGround;
    private int ticksCatchable;
    private int ticksCaughtDelay;
    private int ticksCatchableDelay;
    private float fishApproachAngle;
    private boolean field_234595_aq_ = true;
    private Entity caughtEntity;
    private FishingBobberEntity.State currentState = FishingBobberEntity.State.FLYING;
    private final int luck;
    private final int lureSpeed;

    private FishingBobberEntity(World p_i50219_1_, PlayerEntity p_i50219_2_, int p_i50219_3_, int p_i50219_4_)
    {
        super(EntityType.FISHING_BOBBER, p_i50219_1_);
        this.ignoreFrustumCheck = true;
        this.setShooter(p_i50219_2_);
        p_i50219_2_.fishingBobber = this;
        this.luck = Math.max(0, p_i50219_3_);
        this.lureSpeed = Math.max(0, p_i50219_4_);
    }

    public FishingBobberEntity(World worldIn, PlayerEntity p_i47290_2_, double x, double y, double z)
    {
        this(worldIn, p_i47290_2_, 0, 0);
        this.setPosition(x, y, z);
        this.prevPosX = this.getPosX();
        this.prevPosY = this.getPosY();
        this.prevPosZ = this.getPosZ();
    }

    public FishingBobberEntity(PlayerEntity p_i50220_1_, World p_i50220_2_, int p_i50220_3_, int p_i50220_4_)
    {
        this(p_i50220_2_, p_i50220_1_, p_i50220_3_, p_i50220_4_);
        float f = p_i50220_1_.rotationPitch;
        float f1 = p_i50220_1_.rotationYaw;
        float f2 = MathHelper.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = MathHelper.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -MathHelper.cos(-f * ((float)Math.PI / 180F));
        float f5 = MathHelper.sin(-f * ((float)Math.PI / 180F));
        double d0 = p_i50220_1_.getPosX() - (double)f3 * 0.3D;
        double d1 = p_i50220_1_.getPosYEye();
        double d2 = p_i50220_1_.getPosZ() - (double)f2 * 0.3D;
        this.setLocationAndAngles(d0, d1, d2, f1, f);
        Vector3d vector3d = new Vector3d((double)(-f3), (double)MathHelper.clamp(-(f5 / f4), -5.0F, 5.0F), (double)(-f2));
        double d3 = vector3d.length();
        vector3d = vector3d.mul(0.6D / d3 + 0.5D + this.rand.nextGaussian() * 0.0045D, 0.6D / d3 + 0.5D + this.rand.nextGaussian() * 0.0045D, 0.6D / d3 + 0.5D + this.rand.nextGaussian() * 0.0045D);
        this.setMotion(vector3d);
        this.rotationYaw = (float)(MathHelper.atan2(vector3d.x, vector3d.z) * (double)(180F / (float)Math.PI));
        this.rotationPitch = (float)(MathHelper.atan2(vector3d.y, (double)MathHelper.sqrt(horizontalMag(vector3d))) * (double)(180F / (float)Math.PI));
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
    }

    protected void registerData()
    {
        this.getDataManager().register(DATA_HOOKED_ENTITY, 0);
        this.getDataManager().register(field_234599_f_, false);
    }

    public void notifyDataManagerChange(DataParameter<?> key)
    {
        if (DATA_HOOKED_ENTITY.equals(key))
        {
            int i = this.getDataManager().get(DATA_HOOKED_ENTITY);
            this.caughtEntity = i > 0 ? this.world.getEntityByID(i - 1) : null;
        }

        if (field_234599_f_.equals(key))
        {
            this.field_234597_c_ = this.getDataManager().get(field_234599_f_);

            if (this.field_234597_c_)
            {
                this.setMotion(this.getMotion().x, (double)(-0.4F * MathHelper.nextFloat(this.field_234596_b_, 0.6F, 1.0F)), this.getMotion().z);
            }
        }

        super.notifyDataManagerChange(key);
    }

    /**
     * Checks if the entity is in range to render.
     */
    public boolean isInRangeToRenderDist(double distance)
    {
        double d0 = 64.0D;
        return distance < 4096.0D;
    }

    /**
     * Sets a target for the client to interpolate towards over the next few ticks
     */
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport)
    {
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void tick()
    {
        this.field_234596_b_.setSeed(this.getUniqueID().getLeastSignificantBits() ^ this.world.getGameTime());
        super.tick();
        PlayerEntity playerentity = this.func_234606_i_();

        if (playerentity == null)
        {
            this.remove();
        }
        else if (this.world.isRemote || !this.func_234600_a_(playerentity))
        {
            if (this.onGround)
            {
                ++this.ticksInGround;

                if (this.ticksInGround >= 1200)
                {
                    this.remove();
                    return;
                }
            }
            else
            {
                this.ticksInGround = 0;
            }

            float f = 0.0F;
            BlockPos blockpos = this.getPosition();
            FluidState fluidstate = this.world.getFluidState(blockpos);

            if (fluidstate.isTagged(FluidTags.WATER))
            {
                f = fluidstate.getActualHeight(this.world, blockpos);
            }

            boolean flag = f > 0.0F;

            if (this.currentState == FishingBobberEntity.State.FLYING)
            {
                if (this.caughtEntity != null)
                {
                    this.setMotion(Vector3d.ZERO);
                    this.currentState = FishingBobberEntity.State.HOOKED_IN_ENTITY;
                    return;
                }

                if (flag)
                {
                    this.setMotion(this.getMotion().mul(0.3D, 0.2D, 0.3D));
                    this.currentState = FishingBobberEntity.State.BOBBING;
                    return;
                }

                this.checkCollision();
            }
            else
            {
                if (this.currentState == FishingBobberEntity.State.HOOKED_IN_ENTITY)
                {
                    if (this.caughtEntity != null)
                    {
                        if (this.caughtEntity.removed)
                        {
                            this.caughtEntity = null;
                            this.currentState = FishingBobberEntity.State.FLYING;
                        }
                        else
                        {
                            this.setPosition(this.caughtEntity.getPosX(), this.caughtEntity.getPosYHeight(0.8D), this.caughtEntity.getPosZ());
                        }
                    }

                    return;
                }

                if (this.currentState == FishingBobberEntity.State.BOBBING)
                {
                    Vector3d vector3d = this.getMotion();
                    double d0 = this.getPosY() + vector3d.y - (double)blockpos.getY() - (double)f;

                    if (Math.abs(d0) < 0.01D)
                    {
                        d0 += Math.signum(d0) * 0.1D;
                    }

                    this.setMotion(vector3d.x * 0.9D, vector3d.y - d0 * (double)this.rand.nextFloat() * 0.2D, vector3d.z * 0.9D);

                    if (this.ticksCatchable <= 0 && this.ticksCatchableDelay <= 0)
                    {
                        this.field_234595_aq_ = true;
                    }
                    else
                    {
                        this.field_234595_aq_ = this.field_234595_aq_ && this.field_234598_d_ < 10 && this.func_234603_b_(blockpos);
                    }

                    if (flag)
                    {
                        this.field_234598_d_ = Math.max(0, this.field_234598_d_ - 1);

                        if (this.field_234597_c_)
                        {
                            this.setMotion(this.getMotion().add(0.0D, -0.1D * (double)this.field_234596_b_.nextFloat() * (double)this.field_234596_b_.nextFloat(), 0.0D));
                        }

                        if (!this.world.isRemote)
                        {
                            this.catchingFish(blockpos);
                        }
                    }
                    else
                    {
                        this.field_234598_d_ = Math.min(10, this.field_234598_d_ + 1);
                    }
                }
            }

            if (!fluidstate.isTagged(FluidTags.WATER))
            {
                this.setMotion(this.getMotion().add(0.0D, -0.03D, 0.0D));
            }

            this.move(MoverType.SELF, this.getMotion());
            this.func_234617_x_();

            if (this.currentState == FishingBobberEntity.State.FLYING && (this.onGround || this.collidedHorizontally))
            {
                this.setMotion(Vector3d.ZERO);
            }

            double d1 = 0.92D;
            this.setMotion(this.getMotion().scale(0.92D));
            this.recenterBoundingBox();
        }
    }

    private boolean func_234600_a_(PlayerEntity p_234600_1_)
    {
        ItemStack itemstack = p_234600_1_.getHeldItemMainhand();
        ItemStack itemstack1 = p_234600_1_.getHeldItemOffhand();
        boolean flag = itemstack.getItem() == Items.FISHING_ROD;
        boolean flag1 = itemstack1.getItem() == Items.FISHING_ROD;

        if (!p_234600_1_.removed && p_234600_1_.isAlive() && (flag || flag1) && !(this.getDistanceSq(p_234600_1_) > 1024.0D))
        {
            return false;
        }
        else
        {
            this.remove();
            return true;
        }
    }

    private void checkCollision()
    {
        RayTraceResult raytraceresult = ProjectileHelper.func_234618_a_(this, this::func_230298_a_);
        this.onImpact(raytraceresult);
    }

    protected boolean func_230298_a_(Entity p_230298_1_)
    {
        return super.func_230298_a_(p_230298_1_) || p_230298_1_.isAlive() && p_230298_1_ instanceof ItemEntity;
    }

    /**
     * Called when the arrow hits an entity
     */
    protected void onEntityHit(EntityRayTraceResult p_213868_1_)
    {
        super.onEntityHit(p_213868_1_);

        if (!this.world.isRemote)
        {
            this.caughtEntity = p_213868_1_.getEntity();
            this.setHookedEntity();
        }
    }

    protected void func_230299_a_(BlockRayTraceResult p_230299_1_)
    {
        super.func_230299_a_(p_230299_1_);
        this.setMotion(this.getMotion().normalize().scale(p_230299_1_.func_237486_a_(this)));
    }

    private void setHookedEntity()
    {
        this.getDataManager().set(DATA_HOOKED_ENTITY, this.caughtEntity.getEntityId() + 1);
    }

    private void catchingFish(BlockPos p_190621_1_)
    {
        ServerWorld serverworld = (ServerWorld)this.world;
        int i = 1;
        BlockPos blockpos = p_190621_1_.up();

        if (this.rand.nextFloat() < 0.25F && this.world.isRainingAt(blockpos))
        {
            ++i;
        }

        if (this.rand.nextFloat() < 0.5F && !this.world.canSeeSky(blockpos))
        {
            --i;
        }

        if (this.ticksCatchable > 0)
        {
            --this.ticksCatchable;

            if (this.ticksCatchable <= 0)
            {
                this.ticksCaughtDelay = 0;
                this.ticksCatchableDelay = 0;
                this.getDataManager().set(field_234599_f_, false);
            }
        }
        else if (this.ticksCatchableDelay > 0)
        {
            this.ticksCatchableDelay -= i;

            if (this.ticksCatchableDelay > 0)
            {
                this.fishApproachAngle = (float)((double)this.fishApproachAngle + this.rand.nextGaussian() * 4.0D);
                float f = this.fishApproachAngle * ((float)Math.PI / 180F);
                float f1 = MathHelper.sin(f);
                float f2 = MathHelper.cos(f);
                double d0 = this.getPosX() + (double)(f1 * (float)this.ticksCatchableDelay * 0.1F);
                double d1 = (double)((float)MathHelper.floor(this.getPosY()) + 1.0F);
                double d2 = this.getPosZ() + (double)(f2 * (float)this.ticksCatchableDelay * 0.1F);
                BlockState blockstate = serverworld.getBlockState(new BlockPos(d0, d1 - 1.0D, d2));

                if (blockstate.isIn(Blocks.WATER))
                {
                    if (this.rand.nextFloat() < 0.15F)
                    {
                        serverworld.spawnParticle(ParticleTypes.BUBBLE, d0, d1 - (double)0.1F, d2, 1, (double)f1, 0.1D, (double)f2, 0.0D);
                    }

                    float f3 = f1 * 0.04F;
                    float f4 = f2 * 0.04F;
                    serverworld.spawnParticle(ParticleTypes.FISHING, d0, d1, d2, 0, (double)f4, 0.01D, (double)(-f3), 1.0D);
                    serverworld.spawnParticle(ParticleTypes.FISHING, d0, d1, d2, 0, (double)(-f4), 0.01D, (double)f3, 1.0D);
                }
            }
            else
            {
                this.playSound(SoundEvents.ENTITY_FISHING_BOBBER_SPLASH, 0.25F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                double d3 = this.getPosY() + 0.5D;
                serverworld.spawnParticle(ParticleTypes.BUBBLE, this.getPosX(), d3, this.getPosZ(), (int)(1.0F + this.getWidth() * 20.0F), (double)this.getWidth(), 0.0D, (double)this.getWidth(), (double)0.2F);
                serverworld.spawnParticle(ParticleTypes.FISHING, this.getPosX(), d3, this.getPosZ(), (int)(1.0F + this.getWidth() * 20.0F), (double)this.getWidth(), 0.0D, (double)this.getWidth(), (double)0.2F);
                this.ticksCatchable = MathHelper.nextInt(this.rand, 20, 40);
                this.getDataManager().set(field_234599_f_, true);
            }
        }
        else if (this.ticksCaughtDelay > 0)
        {
            this.ticksCaughtDelay -= i;
            float f5 = 0.15F;

            if (this.ticksCaughtDelay < 20)
            {
                f5 = (float)((double)f5 + (double)(20 - this.ticksCaughtDelay) * 0.05D);
            }
            else if (this.ticksCaughtDelay < 40)
            {
                f5 = (float)((double)f5 + (double)(40 - this.ticksCaughtDelay) * 0.02D);
            }
            else if (this.ticksCaughtDelay < 60)
            {
                f5 = (float)((double)f5 + (double)(60 - this.ticksCaughtDelay) * 0.01D);
            }

            if (this.rand.nextFloat() < f5)
            {
                float f6 = MathHelper.nextFloat(this.rand, 0.0F, 360.0F) * ((float)Math.PI / 180F);
                float f7 = MathHelper.nextFloat(this.rand, 25.0F, 60.0F);
                double d4 = this.getPosX() + (double)(MathHelper.sin(f6) * f7 * 0.1F);
                double d5 = (double)((float)MathHelper.floor(this.getPosY()) + 1.0F);
                double d6 = this.getPosZ() + (double)(MathHelper.cos(f6) * f7 * 0.1F);
                BlockState blockstate1 = serverworld.getBlockState(new BlockPos(d4, d5 - 1.0D, d6));

                if (blockstate1.isIn(Blocks.WATER))
                {
                    serverworld.spawnParticle(ParticleTypes.SPLASH, d4, d5, d6, 2 + this.rand.nextInt(2), (double)0.1F, 0.0D, (double)0.1F, 0.0D);
                }
            }

            if (this.ticksCaughtDelay <= 0)
            {
                this.fishApproachAngle = MathHelper.nextFloat(this.rand, 0.0F, 360.0F);
                this.ticksCatchableDelay = MathHelper.nextInt(this.rand, 20, 80);
            }
        }
        else
        {
            this.ticksCaughtDelay = MathHelper.nextInt(this.rand, 100, 600);
            this.ticksCaughtDelay -= this.lureSpeed * 20 * 5;
        }
    }

    private boolean func_234603_b_(BlockPos p_234603_1_)
    {
        FishingBobberEntity.WaterType fishingbobberentity$watertype = FishingBobberEntity.WaterType.INVALID;

        for (int i = -1; i <= 2; ++i)
        {
            FishingBobberEntity.WaterType fishingbobberentity$watertype1 = this.func_234602_a_(p_234603_1_.add(-2, i, -2), p_234603_1_.add(2, i, 2));

            switch (fishingbobberentity$watertype1)
            {
                case INVALID:
                    return false;

                case ABOVE_WATER:
                    if (fishingbobberentity$watertype == FishingBobberEntity.WaterType.INVALID)
                    {
                        return false;
                    }

                    break;

                case INSIDE_WATER:
                    if (fishingbobberentity$watertype == FishingBobberEntity.WaterType.ABOVE_WATER)
                    {
                        return false;
                    }
            }

            fishingbobberentity$watertype = fishingbobberentity$watertype1;
        }

        return true;
    }

    private FishingBobberEntity.WaterType func_234602_a_(BlockPos p_234602_1_, BlockPos p_234602_2_)
    {
        return BlockPos.getAllInBox(p_234602_1_, p_234602_2_).map(this::func_234604_c_).reduce((p_234601_0_, p_234601_1_) ->
        {
            return p_234601_0_ == p_234601_1_ ? p_234601_0_ : FishingBobberEntity.WaterType.INVALID;
        }).orElse(FishingBobberEntity.WaterType.INVALID);
    }

    private FishingBobberEntity.WaterType func_234604_c_(BlockPos p_234604_1_)
    {
        BlockState blockstate = this.world.getBlockState(p_234604_1_);

        if (!blockstate.isAir() && !blockstate.isIn(Blocks.LILY_PAD))
        {
            FluidState fluidstate = blockstate.getFluidState();
            return fluidstate.isTagged(FluidTags.WATER) && fluidstate.isSource() && blockstate.getCollisionShape(this.world, p_234604_1_).isEmpty() ? FishingBobberEntity.WaterType.INSIDE_WATER : FishingBobberEntity.WaterType.INVALID;
        }
        else
        {
            return FishingBobberEntity.WaterType.ABOVE_WATER;
        }
    }

    public boolean func_234605_g_()
    {
        return this.field_234595_aq_;
    }

    public void writeAdditional(CompoundNBT compound)
    {
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(CompoundNBT compound)
    {
    }

    public int handleHookRetraction(ItemStack p_146034_1_)
    {
        PlayerEntity playerentity = this.func_234606_i_();

        if (!this.world.isRemote && playerentity != null)
        {
            int i = 0;

            if (this.caughtEntity != null)
            {
                this.bringInHookedEntity();
                CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayerEntity)playerentity, p_146034_1_, this, Collections.emptyList());
                this.world.setEntityState(this, (byte)31);
                i = this.caughtEntity instanceof ItemEntity ? 3 : 5;
            }
            else if (this.ticksCatchable > 0)
            {
                LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerWorld)this.world)).withParameter(LootParameters.field_237457_g_, this.getPositionVec()).withParameter(LootParameters.TOOL, p_146034_1_).withParameter(LootParameters.THIS_ENTITY, this).withRandom(this.rand).withLuck((float)this.luck + playerentity.getLuck());
                LootTable loottable = this.world.getServer().getLootTableManager().getLootTableFromLocation(LootTables.GAMEPLAY_FISHING);
                List<ItemStack> list = loottable.generate(lootcontext$builder.build(LootParameterSets.FISHING));
                CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayerEntity)playerentity, p_146034_1_, this, list);

                for (ItemStack itemstack : list)
                {
                    ItemEntity itementity = new ItemEntity(this.world, this.getPosX(), this.getPosY(), this.getPosZ(), itemstack);
                    double d0 = playerentity.getPosX() - this.getPosX();
                    double d1 = playerentity.getPosY() - this.getPosY();
                    double d2 = playerentity.getPosZ() - this.getPosZ();
                    double d3 = 0.1D;
                    itementity.setMotion(d0 * 0.1D, d1 * 0.1D + Math.sqrt(Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2)) * 0.08D, d2 * 0.1D);
                    this.world.addEntity(itementity);
                    playerentity.world.addEntity(new ExperienceOrbEntity(playerentity.world, playerentity.getPosX(), playerentity.getPosY() + 0.5D, playerentity.getPosZ() + 0.5D, this.rand.nextInt(6) + 1));

                    if (itemstack.getItem().isIn(ItemTags.FISHES))
                    {
                        playerentity.addStat(Stats.FISH_CAUGHT, 1);
                    }
                }

                i = 1;
            }

            if (this.onGround)
            {
                i = 2;
            }

            this.remove();
            return i;
        }
        else
        {
            return 0;
        }
    }

    /**
     * Handler for {@link World#setEntityState}
     */
    public void handleStatusUpdate(byte id)
    {
        if (id == 31 && this.world.isRemote && this.caughtEntity instanceof PlayerEntity && ((PlayerEntity)this.caughtEntity).isUser())
        {
            this.bringInHookedEntity();
        }

        super.handleStatusUpdate(id);
    }

    protected void bringInHookedEntity()
    {
        Entity entity = this.func_234616_v_();

        if (entity != null)
        {
            Vector3d vector3d = (new Vector3d(entity.getPosX() - this.getPosX(), entity.getPosY() - this.getPosY(), entity.getPosZ() - this.getPosZ())).scale(0.1D);
            this.caughtEntity.setMotion(this.caughtEntity.getMotion().add(vector3d));
        }
    }

    protected boolean canTriggerWalking()
    {
        return false;
    }

    /**
     * Queues the entity for removal from the world on the next tick.
     */
    public void remove()
    {
        super.remove();
        PlayerEntity playerentity = this.func_234606_i_();

        if (playerentity != null)
        {
            playerentity.fishingBobber = null;
        }
    }

    @Nullable
    public PlayerEntity func_234606_i_()
    {
        Entity entity = this.func_234616_v_();
        return entity instanceof PlayerEntity ? (PlayerEntity)entity : null;
    }

    @Nullable
    public Entity func_234607_k_()
    {
        return this.caughtEntity;
    }

    /**
     * Returns false if this Entity is a boss, true otherwise.
     */
    public boolean isNonBoss()
    {
        return false;
    }

    public IPacket<?> createSpawnPacket()
    {
        Entity entity = this.func_234616_v_();
        return new SSpawnObjectPacket(this, entity == null ? this.getEntityId() : entity.getEntityId());
    }

    static enum State
    {
        FLYING,
        HOOKED_IN_ENTITY,
        BOBBING;
    }

    static enum WaterType
    {
        ABOVE_WATER,
        INSIDE_WATER,
        INVALID;
    }
}
