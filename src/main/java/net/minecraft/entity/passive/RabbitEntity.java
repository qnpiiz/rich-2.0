package net.minecraft.entity.passive;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarrotBlock;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;

public class RabbitEntity extends AnimalEntity
{
    private static final DataParameter<Integer> RABBIT_TYPE = EntityDataManager.createKey(RabbitEntity.class, DataSerializers.VARINT);
    private static final ResourceLocation KILLER_BUNNY = new ResourceLocation("killer_bunny");
    private int jumpTicks;
    private int jumpDuration;
    private boolean wasOnGround;
    private int currentMoveTypeDuration;
    private int carrotTicks;

    public RabbitEntity(EntityType <? extends RabbitEntity > p_i50247_1_, World p_i50247_2_)
    {
        super(p_i50247_1_, p_i50247_2_);
        this.jumpController = new RabbitEntity.JumpHelperController(this);
        this.moveController = new RabbitEntity.MoveHelperController(this);
        this.setMovementSpeed(0.0D);
    }

    protected void registerGoals()
    {
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(1, new RabbitEntity.PanicGoal(this, 2.2D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 0.8D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, Ingredient.fromItems(Items.CARROT, Items.GOLDEN_CARROT, Blocks.DANDELION), false));
        this.goalSelector.addGoal(4, new RabbitEntity.AvoidEntityGoal<>(this, PlayerEntity.class, 8.0F, 2.2D, 2.2D));
        this.goalSelector.addGoal(4, new RabbitEntity.AvoidEntityGoal<>(this, WolfEntity.class, 10.0F, 2.2D, 2.2D));
        this.goalSelector.addGoal(4, new RabbitEntity.AvoidEntityGoal<>(this, MonsterEntity.class, 4.0F, 2.2D, 2.2D));
        this.goalSelector.addGoal(5, new RabbitEntity.RaidFarmGoal(this));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 0.6D));
        this.goalSelector.addGoal(11, new LookAtGoal(this, PlayerEntity.class, 10.0F));
    }

    protected float getJumpUpwardsMotion()
    {
        if (!this.collidedHorizontally && (!this.moveController.isUpdating() || !(this.moveController.getY() > this.getPosY() + 0.5D)))
        {
            Path path = this.navigator.getPath();

            if (path != null && !path.isFinished())
            {
                Vector3d vector3d = path.getPosition(this);

                if (vector3d.y > this.getPosY() + 0.5D)
                {
                    return 0.5F;
                }
            }

            return this.moveController.getSpeed() <= 0.6D ? 0.2F : 0.3F;
        }
        else
        {
            return 0.5F;
        }
    }

    /**
     * Causes this entity to do an upwards motion (jumping).
     */
    protected void jump()
    {
        super.jump();
        double d0 = this.moveController.getSpeed();

        if (d0 > 0.0D)
        {
            double d1 = horizontalMag(this.getMotion());

            if (d1 < 0.01D)
            {
                this.moveRelative(0.1F, new Vector3d(0.0D, 0.0D, 1.0D));
            }
        }

        if (!this.world.isRemote)
        {
            this.world.setEntityState(this, (byte)1);
        }
    }

    public float getJumpCompletion(float p_175521_1_)
    {
        return this.jumpDuration == 0 ? 0.0F : ((float)this.jumpTicks + p_175521_1_) / (float)this.jumpDuration;
    }

    public void setMovementSpeed(double newSpeed)
    {
        this.getNavigator().setSpeed(newSpeed);
        this.moveController.setMoveTo(this.moveController.getX(), this.moveController.getY(), this.moveController.getZ(), newSpeed);
    }

    public void setJumping(boolean jumping)
    {
        super.setJumping(jumping);

        if (jumping)
        {
            this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
        }
    }

    public void startJumping()
    {
        this.setJumping(true);
        this.jumpDuration = 10;
        this.jumpTicks = 0;
    }

    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(RABBIT_TYPE, 0);
    }

    public void updateAITasks()
    {
        if (this.currentMoveTypeDuration > 0)
        {
            --this.currentMoveTypeDuration;
        }

        if (this.carrotTicks > 0)
        {
            this.carrotTicks -= this.rand.nextInt(3);

            if (this.carrotTicks < 0)
            {
                this.carrotTicks = 0;
            }
        }

        if (this.onGround)
        {
            if (!this.wasOnGround)
            {
                this.setJumping(false);
                this.checkLandingDelay();
            }

            if (this.getRabbitType() == 99 && this.currentMoveTypeDuration == 0)
            {
                LivingEntity livingentity = this.getAttackTarget();

                if (livingentity != null && this.getDistanceSq(livingentity) < 16.0D)
                {
                    this.calculateRotationYaw(livingentity.getPosX(), livingentity.getPosZ());
                    this.moveController.setMoveTo(livingentity.getPosX(), livingentity.getPosY(), livingentity.getPosZ(), this.moveController.getSpeed());
                    this.startJumping();
                    this.wasOnGround = true;
                }
            }

            RabbitEntity.JumpHelperController rabbitentity$jumphelpercontroller = (RabbitEntity.JumpHelperController)this.jumpController;

            if (!rabbitentity$jumphelpercontroller.getIsJumping())
            {
                if (this.moveController.isUpdating() && this.currentMoveTypeDuration == 0)
                {
                    Path path = this.navigator.getPath();
                    Vector3d vector3d = new Vector3d(this.moveController.getX(), this.moveController.getY(), this.moveController.getZ());

                    if (path != null && !path.isFinished())
                    {
                        vector3d = path.getPosition(this);
                    }

                    this.calculateRotationYaw(vector3d.x, vector3d.z);
                    this.startJumping();
                }
            }
            else if (!rabbitentity$jumphelpercontroller.canJump())
            {
                this.enableJumpControl();
            }
        }

        this.wasOnGround = this.onGround;
    }

    public boolean func_230269_aK_()
    {
        return false;
    }

    private void calculateRotationYaw(double x, double z)
    {
        this.rotationYaw = (float)(MathHelper.atan2(z - this.getPosZ(), x - this.getPosX()) * (double)(180F / (float)Math.PI)) - 90.0F;
    }

    private void enableJumpControl()
    {
        ((RabbitEntity.JumpHelperController)this.jumpController).setCanJump(true);
    }

    private void disableJumpControl()
    {
        ((RabbitEntity.JumpHelperController)this.jumpController).setCanJump(false);
    }

    private void updateMoveTypeDuration()
    {
        if (this.moveController.getSpeed() < 2.2D)
        {
            this.currentMoveTypeDuration = 10;
        }
        else
        {
            this.currentMoveTypeDuration = 1;
        }
    }

    private void checkLandingDelay()
    {
        this.updateMoveTypeDuration();
        this.disableJumpControl();
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void livingTick()
    {
        super.livingTick();

        if (this.jumpTicks != this.jumpDuration)
        {
            ++this.jumpTicks;
        }
        else if (this.jumpDuration != 0)
        {
            this.jumpTicks = 0;
            this.jumpDuration = 0;
            this.setJumping(false);
        }
    }

    public static AttributeModifierMap.MutableAttribute func_234224_eJ_()
    {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 3.0D).createMutableAttribute(Attributes.MOVEMENT_SPEED, (double)0.3F);
    }

    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        compound.putInt("RabbitType", this.getRabbitType());
        compound.putInt("MoreCarrotTicks", this.carrotTicks);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
        this.setRabbitType(compound.getInt("RabbitType"));
        this.carrotTicks = compound.getInt("MoreCarrotTicks");
    }

    protected SoundEvent getJumpSound()
    {
        return SoundEvents.ENTITY_RABBIT_JUMP;
    }

    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.ENTITY_RABBIT_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_RABBIT_HURT;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_RABBIT_DEATH;
    }

    public boolean attackEntityAsMob(Entity entityIn)
    {
        if (this.getRabbitType() == 99)
        {
            this.playSound(SoundEvents.ENTITY_RABBIT_ATTACK, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 8.0F);
        }
        else
        {
            return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 3.0F);
        }
    }

    public SoundCategory getSoundCategory()
    {
        return this.getRabbitType() == 99 ? SoundCategory.HOSTILE : SoundCategory.NEUTRAL;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        return this.isInvulnerableTo(source) ? false : super.attackEntityFrom(source, amount);
    }

    private boolean isRabbitBreedingItem(Item itemIn)
    {
        return itemIn == Items.CARROT || itemIn == Items.GOLDEN_CARROT || itemIn == Blocks.DANDELION.asItem();
    }

    public RabbitEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_)
    {
        RabbitEntity rabbitentity = EntityType.RABBIT.create(p_241840_1_);
        int i = this.getRandomRabbitType(p_241840_1_);

        if (this.rand.nextInt(20) != 0)
        {
            if (p_241840_2_ instanceof RabbitEntity && this.rand.nextBoolean())
            {
                i = ((RabbitEntity)p_241840_2_).getRabbitType();
            }
            else
            {
                i = this.getRabbitType();
            }
        }

        rabbitentity.setRabbitType(i);
        return rabbitentity;
    }

    /**
     * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
     * the animal type)
     */
    public boolean isBreedingItem(ItemStack stack)
    {
        return this.isRabbitBreedingItem(stack.getItem());
    }

    public int getRabbitType()
    {
        return this.dataManager.get(RABBIT_TYPE);
    }

    public void setRabbitType(int rabbitTypeId)
    {
        if (rabbitTypeId == 99)
        {
            this.getAttribute(Attributes.ARMOR).setBaseValue(8.0D);
            this.goalSelector.addGoal(4, new RabbitEntity.EvilAttackGoal(this));
            this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setCallsForHelp());
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, WolfEntity.class, true));

            if (!this.hasCustomName())
            {
                this.setCustomName(new TranslationTextComponent(Util.makeTranslationKey("entity", KILLER_BUNNY)));
            }
        }

        this.dataManager.set(RABBIT_TYPE, rabbitTypeId);
    }

    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag)
    {
        int i = this.getRandomRabbitType(worldIn);

        if (spawnDataIn instanceof RabbitEntity.RabbitData)
        {
            i = ((RabbitEntity.RabbitData)spawnDataIn).typeData;
        }
        else
        {
            spawnDataIn = new RabbitEntity.RabbitData(i);
        }

        this.setRabbitType(i);
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    private int getRandomRabbitType(IWorld p_213610_1_)
    {
        Biome biome = p_213610_1_.getBiome(this.getPosition());
        int i = this.rand.nextInt(100);

        if (biome.getPrecipitation() == Biome.RainType.SNOW)
        {
            return i < 80 ? 1 : 3;
        }
        else if (biome.getCategory() == Biome.Category.DESERT)
        {
            return 4;
        }
        else
        {
            return i < 50 ? 0 : (i < 90 ? 5 : 2);
        }
    }

    public static boolean func_223321_c(EntityType<RabbitEntity> p_223321_0_, IWorld p_223321_1_, SpawnReason reason, BlockPos p_223321_3_, Random p_223321_4_)
    {
        BlockState blockstate = p_223321_1_.getBlockState(p_223321_3_.down());
        return (blockstate.isIn(Blocks.GRASS_BLOCK) || blockstate.isIn(Blocks.SNOW) || blockstate.isIn(Blocks.SAND)) && p_223321_1_.getLightSubtracted(p_223321_3_, 0) > 8;
    }

    /**
     * Returns true if {@link net.minecraft.entity.passive.EntityRabbit#carrotTicks carrotTicks} has reached zero
     */
    private boolean isCarrotEaten()
    {
        return this.carrotTicks == 0;
    }

    /**
     * Handler for {@link World#setEntityState}
     */
    public void handleStatusUpdate(byte id)
    {
        if (id == 1)
        {
            this.func_233569_aL_();
            this.jumpDuration = 10;
            this.jumpTicks = 0;
        }
        else
        {
            super.handleStatusUpdate(id);
        }
    }

    public Vector3d func_241205_ce_()
    {
        return new Vector3d(0.0D, (double)(0.6F * this.getEyeHeight()), (double)(this.getWidth() * 0.4F));
    }

    static class AvoidEntityGoal<T extends LivingEntity> extends net.minecraft.entity.ai.goal.AvoidEntityGoal<T>
    {
        private final RabbitEntity rabbit;

        public AvoidEntityGoal(RabbitEntity rabbit, Class<T> p_i46403_2_, float p_i46403_3_, double p_i46403_4_, double p_i46403_6_)
        {
            super(rabbit, p_i46403_2_, p_i46403_3_, p_i46403_4_, p_i46403_6_);
            this.rabbit = rabbit;
        }

        public boolean shouldExecute()
        {
            return this.rabbit.getRabbitType() != 99 && super.shouldExecute();
        }
    }

    static class EvilAttackGoal extends MeleeAttackGoal
    {
        public EvilAttackGoal(RabbitEntity rabbit)
        {
            super(rabbit, 1.4D, true);
        }

        protected double getAttackReachSqr(LivingEntity attackTarget)
        {
            return (double)(4.0F + attackTarget.getWidth());
        }
    }

    public class JumpHelperController extends JumpController
    {
        private final RabbitEntity rabbit;
        private boolean canJump;

        public JumpHelperController(RabbitEntity rabbit)
        {
            super(rabbit);
            this.rabbit = rabbit;
        }

        public boolean getIsJumping()
        {
            return this.isJumping;
        }

        public boolean canJump()
        {
            return this.canJump;
        }

        public void setCanJump(boolean canJumpIn)
        {
            this.canJump = canJumpIn;
        }

        public void tick()
        {
            if (this.isJumping)
            {
                this.rabbit.startJumping();
                this.isJumping = false;
            }
        }
    }

    static class MoveHelperController extends MovementController
    {
        private final RabbitEntity rabbit;
        private double nextJumpSpeed;

        public MoveHelperController(RabbitEntity rabbit)
        {
            super(rabbit);
            this.rabbit = rabbit;
        }

        public void tick()
        {
            if (this.rabbit.onGround && !this.rabbit.isJumping && !((RabbitEntity.JumpHelperController)this.rabbit.jumpController).getIsJumping())
            {
                this.rabbit.setMovementSpeed(0.0D);
            }
            else if (this.isUpdating())
            {
                this.rabbit.setMovementSpeed(this.nextJumpSpeed);
            }

            super.tick();
        }

        public void setMoveTo(double x, double y, double z, double speedIn)
        {
            if (this.rabbit.isInWater())
            {
                speedIn = 1.5D;
            }

            super.setMoveTo(x, y, z, speedIn);

            if (speedIn > 0.0D)
            {
                this.nextJumpSpeed = speedIn;
            }
        }
    }

    static class PanicGoal extends net.minecraft.entity.ai.goal.PanicGoal
    {
        private final RabbitEntity rabbit;

        public PanicGoal(RabbitEntity rabbit, double speedIn)
        {
            super(rabbit, speedIn);
            this.rabbit = rabbit;
        }

        public void tick()
        {
            super.tick();
            this.rabbit.setMovementSpeed(this.speed);
        }
    }

    public static class RabbitData extends AgeableEntity.AgeableData
    {
        public final int typeData;

        public RabbitData(int type)
        {
            super(1.0F);
            this.typeData = type;
        }
    }

    static class RaidFarmGoal extends MoveToBlockGoal
    {
        private final RabbitEntity rabbit;
        private boolean wantsToRaid;
        private boolean canRaid;

        public RaidFarmGoal(RabbitEntity rabbitIn)
        {
            super(rabbitIn, (double)0.7F, 16);
            this.rabbit = rabbitIn;
        }

        public boolean shouldExecute()
        {
            if (this.runDelay <= 0)
            {
                if (!this.rabbit.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING))
                {
                    return false;
                }

                this.canRaid = false;
                this.wantsToRaid = this.rabbit.isCarrotEaten();
                this.wantsToRaid = true;
            }

            return super.shouldExecute();
        }

        public boolean shouldContinueExecuting()
        {
            return this.canRaid && super.shouldContinueExecuting();
        }

        public void tick()
        {
            super.tick();
            this.rabbit.getLookController().setLookPosition((double)this.destinationBlock.getX() + 0.5D, (double)(this.destinationBlock.getY() + 1), (double)this.destinationBlock.getZ() + 0.5D, 10.0F, (float)this.rabbit.getVerticalFaceSpeed());

            if (this.getIsAboveDestination())
            {
                World world = this.rabbit.world;
                BlockPos blockpos = this.destinationBlock.up();
                BlockState blockstate = world.getBlockState(blockpos);
                Block block = blockstate.getBlock();

                if (this.canRaid && block instanceof CarrotBlock)
                {
                    Integer integer = blockstate.get(CarrotBlock.AGE);

                    if (integer == 0)
                    {
                        world.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 2);
                        world.destroyBlock(blockpos, true, this.rabbit);
                    }
                    else
                    {
                        world.setBlockState(blockpos, blockstate.with(CarrotBlock.AGE, Integer.valueOf(integer - 1)), 2);
                        world.playEvent(2001, blockpos, Block.getStateId(blockstate));
                    }

                    this.rabbit.carrotTicks = 40;
                }

                this.canRaid = false;
                this.runDelay = 10;
            }
        }

        protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos)
        {
            Block block = worldIn.getBlockState(pos).getBlock();

            if (block == Blocks.FARMLAND && this.wantsToRaid && !this.canRaid)
            {
                pos = pos.up();
                BlockState blockstate = worldIn.getBlockState(pos);
                block = blockstate.getBlock();

                if (block instanceof CarrotBlock && ((CarrotBlock)block).isMaxAge(blockstate))
                {
                    this.canRaid = true;
                    return true;
                }
            }

            return false;
        }
    }
}
