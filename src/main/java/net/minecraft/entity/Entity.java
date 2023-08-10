package net.minecraft.entity;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.HoneyBlock;
import net.minecraft.block.PortalInfo;
import net.minecraft.block.PortalSize;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.PushReaction;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.INameable;
import net.minecraft.util.Mirror;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ReuseableStream;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TeleportationRepositioner;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Entity implements INameable, ICommandSource
{
    protected static final Logger LOGGER = LogManager.getLogger();
    private static final AtomicInteger NEXT_ENTITY_ID = new AtomicInteger();
    private static final List<ItemStack> EMPTY_EQUIPMENT = Collections.emptyList();
    private static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
    private static double renderDistanceWeight = 1.0D;
    private final EntityType<?> type;
    private int entityId = NEXT_ENTITY_ID.incrementAndGet();
    public boolean preventEntitySpawning;
    private final List<Entity> passengers = Lists.newArrayList();
    protected int rideCooldown;
    @Nullable
    private Entity ridingEntity;
    public boolean forceSpawn;
    public World world;
    public double prevPosX;
    public double prevPosY;
    public double prevPosZ;
    private Vector3d positionVec;
    private BlockPos position;
    private Vector3d motion = Vector3d.ZERO;
    public float rotationYaw;
    public float rotationPitch;
    public float prevRotationYaw;
    public float prevRotationPitch;
    private AxisAlignedBB boundingBox = ZERO_AABB;
    protected boolean onGround;
    public boolean collidedHorizontally;
    public boolean collidedVertically;
    public boolean velocityChanged;
    protected Vector3d motionMultiplier = Vector3d.ZERO;
    public boolean removed;
    public float prevDistanceWalkedModified;
    public float distanceWalkedModified;
    public float distanceWalkedOnStepModified;
    public float fallDistance;
    private float nextStepDistance = 1.0F;
    private float nextFlap = 1.0F;
    public double lastTickPosX;
    public double lastTickPosY;
    public double lastTickPosZ;
    public float stepHeight;
    public boolean noClip;
    public float entityCollisionReduction;
    protected final Random rand = new Random();
    public int ticksExisted;
    private int fire = -this.getFireImmuneTicks();
    protected boolean inWater;
    protected Object2DoubleMap<ITag<Fluid>> eyesFluidLevel = new Object2DoubleArrayMap<>(2);
    protected boolean eyesInWater;
    @Nullable
    protected ITag<Fluid> field_241335_O_;
    public int hurtResistantTime;
    protected boolean firstUpdate = true;
    protected final EntityDataManager dataManager;
    protected static final DataParameter<Byte> FLAGS = EntityDataManager.createKey(Entity.class, DataSerializers.BYTE);
    private static final DataParameter<Integer> AIR = EntityDataManager.createKey(Entity.class, DataSerializers.VARINT);
    private static final DataParameter<Optional<ITextComponent>> CUSTOM_NAME = EntityDataManager.createKey(Entity.class, DataSerializers.OPTIONAL_TEXT_COMPONENT);
    private static final DataParameter<Boolean> CUSTOM_NAME_VISIBLE = EntityDataManager.createKey(Entity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SILENT = EntityDataManager.createKey(Entity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> NO_GRAVITY = EntityDataManager.createKey(Entity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Pose> POSE = EntityDataManager.createKey(Entity.class, DataSerializers.POSE);
    public boolean addedToChunk;
    public int chunkCoordX;
    public int chunkCoordY;
    public int chunkCoordZ;
    private boolean isLoaded;
    private Vector3d field_242272_av;
    public boolean ignoreFrustumCheck;
    public boolean isAirBorne;
    private int field_242273_aw;
    protected boolean inPortal;
    protected int portalCounter;
    protected BlockPos field_242271_ac;
    private boolean invulnerable;
    protected UUID entityUniqueID = MathHelper.getRandomUUID(this.rand);
    protected String cachedUniqueIdString = this.entityUniqueID.toString();
    protected boolean glowing;
    private final Set<String> tags = Sets.newHashSet();
    private boolean isPositionDirty;
    private final double[] pistonDeltas = new double[] {0.0D, 0.0D, 0.0D};
    private long pistonDeltasGameTime;
    private EntitySize size;
    private float eyeHeight;

    public Entity(EntityType<?> entityTypeIn, World worldIn)
    {
        this.type = entityTypeIn;
        this.world = worldIn;
        this.size = entityTypeIn.getSize();
        this.positionVec = Vector3d.ZERO;
        this.position = BlockPos.ZERO;
        this.field_242272_av = Vector3d.ZERO;
        this.setPosition(0.0D, 0.0D, 0.0D);
        this.dataManager = new EntityDataManager(this);
        this.dataManager.register(FLAGS, (byte)0);
        this.dataManager.register(AIR, this.getMaxAir());
        this.dataManager.register(CUSTOM_NAME_VISIBLE, false);
        this.dataManager.register(CUSTOM_NAME, Optional.empty());
        this.dataManager.register(SILENT, false);
        this.dataManager.register(NO_GRAVITY, false);
        this.dataManager.register(POSE, Pose.STANDING);
        this.registerData();
        this.eyeHeight = this.getEyeHeight(Pose.STANDING, this.size);
    }

    public boolean func_242278_a(BlockPos p_242278_1_, BlockState p_242278_2_)
    {
        VoxelShape voxelshape = p_242278_2_.getCollisionShape(this.world, p_242278_1_, ISelectionContext.forEntity(this));
        VoxelShape voxelshape1 = voxelshape.withOffset((double)p_242278_1_.getX(), (double)p_242278_1_.getY(), (double)p_242278_1_.getZ());
        return VoxelShapes.compare(voxelshape1, VoxelShapes.create(this.getBoundingBox()), IBooleanFunction.AND);
    }

    public int getTeamColor()
    {
        Team team = this.getTeam();
        return team != null && team.getColor().getColor() != null ? team.getColor().getColor() : 16777215;
    }

    /**
     * Returns true if the player is in spectator mode.
     */
    public boolean isSpectator()
    {
        return false;
    }

    public final void detach()
    {
        if (this.isBeingRidden())
        {
            this.removePassengers();
        }

        if (this.isPassenger())
        {
            this.stopRiding();
        }
    }

    public void setPacketCoordinates(double x, double y, double z)
    {
        this.func_242277_a(new Vector3d(x, y, z));
    }

    public void func_242277_a(Vector3d p_242277_1_)
    {
        this.field_242272_av = p_242277_1_;
    }

    public Vector3d func_242274_V()
    {
        return this.field_242272_av;
    }

    public EntityType<?> getType()
    {
        return this.type;
    }

    public int getEntityId()
    {
        return this.entityId;
    }

    public void setEntityId(int id)
    {
        this.entityId = id;
    }

    public Set<String> getTags()
    {
        return this.tags;
    }

    public boolean addTag(String tag)
    {
        return this.tags.size() >= 1024 ? false : this.tags.add(tag);
    }

    public boolean removeTag(String tag)
    {
        return this.tags.remove(tag);
    }

    /**
     * Called by the /kill command.
     */
    public void onKillCommand()
    {
        this.remove();
    }

    protected abstract void registerData();

    public EntityDataManager getDataManager()
    {
        return this.dataManager;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (p_equals_1_ instanceof Entity)
        {
            return ((Entity)p_equals_1_).entityId == this.entityId;
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return this.entityId;
    }

    /**
     * Keeps moving the entity up so it isn't colliding with blocks and other requirements for this entity to be spawned
     * (only actually used on players though its also on Entity)
     */
    protected void preparePlayerToSpawn()
    {
        if (this.world != null)
        {
            for (double d0 = this.getPosY(); d0 > 0.0D && d0 < 256.0D; ++d0)
            {
                this.setPosition(this.getPosX(), d0, this.getPosZ());

                if (this.world.hasNoCollisions(this))
                {
                    break;
                }
            }

            this.setMotion(Vector3d.ZERO);
            this.rotationPitch = 0.0F;
        }
    }

    /**
     * Queues the entity for removal from the world on the next tick.
     */
    public void remove()
    {
        this.removed = true;
    }

    public void setPose(Pose poseIn)
    {
        this.dataManager.set(POSE, poseIn);
    }

    public Pose getPose()
    {
        return this.dataManager.get(POSE);
    }

    public boolean isEntityInRange(Entity entity, double distance)
    {
        double d0 = entity.positionVec.x - this.positionVec.x;
        double d1 = entity.positionVec.y - this.positionVec.y;
        double d2 = entity.positionVec.z - this.positionVec.z;
        return d0 * d0 + d1 * d1 + d2 * d2 < distance * distance;
    }

    /**
     * Sets the rotation of the entity.
     */
    protected void setRotation(float yaw, float pitch)
    {
        this.rotationYaw = yaw % 360.0F;
        this.rotationPitch = pitch % 360.0F;
    }

    /**
     * Sets the x,y,z of the entity from the given parameters. Also seems to set up a bounding box.
     */
    public void setPosition(double x, double y, double z)
    {
        this.setRawPosition(x, y, z);
        this.setBoundingBox(this.size.func_242285_a(x, y, z));
    }

    /**
     * Recomputes this entity's bounding box so that it is positioned at this entity's X/Y/Z.
     */
    protected void recenterBoundingBox()
    {
        this.setPosition(this.positionVec.x, this.positionVec.y, this.positionVec.z);
    }

    public void rotateTowards(double yaw, double pitch)
    {
        double d0 = pitch * 0.15D;
        double d1 = yaw * 0.15D;
        this.rotationPitch = (float)((double)this.rotationPitch + d0);
        this.rotationYaw = (float)((double)this.rotationYaw + d1);
        this.rotationPitch = MathHelper.clamp(this.rotationPitch, -90.0F, 90.0F);
        this.prevRotationPitch = (float)((double)this.prevRotationPitch + d0);
        this.prevRotationYaw = (float)((double)this.prevRotationYaw + d1);
        this.prevRotationPitch = MathHelper.clamp(this.prevRotationPitch, -90.0F, 90.0F);

        if (this.ridingEntity != null)
        {
            this.ridingEntity.applyOrientationToEntity(this);
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void tick()
    {
        if (!this.world.isRemote)
        {
            this.setFlag(6, this.isGlowing());
        }

        this.baseTick();
    }

    /**
     * Gets called every tick from main Entity class
     */
    public void baseTick()
    {
        this.world.getProfiler().startSection("entityBaseTick");

        if (this.isPassenger() && this.getRidingEntity().removed)
        {
            this.stopRiding();
        }

        if (this.rideCooldown > 0)
        {
            --this.rideCooldown;
        }

        this.prevDistanceWalkedModified = this.distanceWalkedModified;
        this.prevRotationPitch = this.rotationPitch;
        this.prevRotationYaw = this.rotationYaw;
        this.updatePortal();

        if (this.func_230269_aK_())
        {
            this.func_233569_aL_();
        }

        this.func_233566_aG_();
        this.updateEyesInWater();
        this.updateSwimming();

        if (this.world.isRemote)
        {
            this.extinguish();
        }
        else if (this.fire > 0)
        {
            if (this.isImmuneToFire())
            {
                this.forceFireTicks(this.fire - 4);

                if (this.fire < 0)
                {
                    this.extinguish();
                }
            }
            else
            {
                if (this.fire % 20 == 0 && !this.isInLava())
                {
                    this.attackEntityFrom(DamageSource.ON_FIRE, 1.0F);
                }

                this.forceFireTicks(this.fire - 1);
            }
        }

        if (this.isInLava())
        {
            this.setOnFireFromLava();
            this.fallDistance *= 0.5F;
        }

        if (this.getPosY() < -64.0D)
        {
            this.outOfWorld();
        }

        if (!this.world.isRemote)
        {
            this.setFlag(0, this.fire > 0);
        }

        this.firstUpdate = false;
        this.world.getProfiler().endSection();
    }

    public void func_242279_ag()
    {
        this.field_242273_aw = this.getPortalCooldown();
    }

    public boolean func_242280_ah()
    {
        return this.field_242273_aw > 0;
    }

    /**
     * Decrements the counter for the remaining time until the entity may use a portal again.
     */
    protected void decrementTimeUntilPortal()
    {
        if (this.func_242280_ah())
        {
            --this.field_242273_aw;
        }
    }

    /**
     * Return the amount of time this entity should stay in a portal before being transported.
     */
    public int getMaxInPortalTime()
    {
        return 0;
    }

    /**
     * Called whenever the entity is walking inside of lava.
     */
    protected void setOnFireFromLava()
    {
        if (!this.isImmuneToFire())
        {
            this.setFire(15);
            this.attackEntityFrom(DamageSource.LAVA, 4.0F);
        }
    }

    /**
     * Sets entity to burn for x amount of seconds, cannot lower amount of existing fire.
     */
    public void setFire(int seconds)
    {
        int i = seconds * 20;

        if (this instanceof LivingEntity)
        {
            i = ProtectionEnchantment.getFireTimeForEntity((LivingEntity)this, i);
        }

        if (this.fire < i)
        {
            this.forceFireTicks(i);
        }
    }

    public void forceFireTicks(int ticks)
    {
        this.fire = ticks;
    }

    public int getFireTimer()
    {
        return this.fire;
    }

    /**
     * Removes fire from entity.
     */
    public void extinguish()
    {
        this.forceFireTicks(0);
    }

    /**
     * sets the dead flag. Used when you fall off the bottom of the world.
     */
    protected void outOfWorld()
    {
        this.remove();
    }

    /**
     * Checks if the offset position from the entity's current position is inside of a liquid.
     */
    public boolean isOffsetPositionInLiquid(double x, double y, double z)
    {
        return this.isLiquidPresentInAABB(this.getBoundingBox().offset(x, y, z));
    }

    /**
     * Determines if a liquid is present within the specified AxisAlignedBB.
     */
    private boolean isLiquidPresentInAABB(AxisAlignedBB bb)
    {
        return this.world.hasNoCollisions(this, bb) && !this.world.containsAnyLiquid(bb);
    }

    public void setOnGround(boolean grounded)
    {
        this.onGround = grounded;
    }

    public boolean isOnGround()
    {
        return this.onGround;
    }

    public void move(MoverType typeIn, Vector3d pos)
    {
        if (this.noClip)
        {
            this.setBoundingBox(this.getBoundingBox().offset(pos));
            this.resetPositionToBB();
        }
        else
        {
            if (typeIn == MoverType.PISTON)
            {
                pos = this.handlePistonMovement(pos);

                if (pos.equals(Vector3d.ZERO))
                {
                    return;
                }
            }

            this.world.getProfiler().startSection("move");

            if (this.motionMultiplier.lengthSquared() > 1.0E-7D)
            {
                pos = pos.mul(this.motionMultiplier);
                this.motionMultiplier = Vector3d.ZERO;
                this.setMotion(Vector3d.ZERO);
            }

            pos = this.maybeBackOffFromEdge(pos, typeIn);
            Vector3d vector3d = this.getAllowedMovement(pos);

            if (vector3d.lengthSquared() > 1.0E-7D)
            {
                this.setBoundingBox(this.getBoundingBox().offset(vector3d));
                this.resetPositionToBB();
            }

            this.world.getProfiler().endSection();
            this.world.getProfiler().startSection("rest");
            this.collidedHorizontally = !MathHelper.epsilonEquals(pos.x, vector3d.x) || !MathHelper.epsilonEquals(pos.z, vector3d.z);
            this.collidedVertically = pos.y != vector3d.y;
            this.onGround = this.collidedVertically && pos.y < 0.0D;
            BlockPos blockpos = this.getOnPosition();
            BlockState blockstate = this.world.getBlockState(blockpos);
            this.updateFallState(vector3d.y, this.onGround, blockstate, blockpos);
            Vector3d vector3d1 = this.getMotion();

            if (pos.x != vector3d.x)
            {
                this.setMotion(0.0D, vector3d1.y, vector3d1.z);
            }

            if (pos.z != vector3d.z)
            {
                this.setMotion(vector3d1.x, vector3d1.y, 0.0D);
            }

            Block block = blockstate.getBlock();

            if (pos.y != vector3d.y)
            {
                block.onLanded(this.world, this);
            }

            if (this.onGround && !this.isSteppingCarefully())
            {
                block.onEntityWalk(this.world, blockpos, this);
            }

            if (this.canTriggerWalking() && !this.isPassenger())
            {
                double d0 = vector3d.x;
                double d1 = vector3d.y;
                double d2 = vector3d.z;

                if (!block.isIn(BlockTags.CLIMBABLE))
                {
                    d1 = 0.0D;
                }

                this.distanceWalkedModified = (float)((double)this.distanceWalkedModified + (double)MathHelper.sqrt(horizontalMag(vector3d)) * 0.6D);
                this.distanceWalkedOnStepModified = (float)((double)this.distanceWalkedOnStepModified + (double)MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 0.6D);

                if (this.distanceWalkedOnStepModified > this.nextStepDistance && !blockstate.isAir())
                {
                    this.nextStepDistance = this.determineNextStepDistance();

                    if (this.isInWater())
                    {
                        Entity entity = this.isBeingRidden() && this.getControllingPassenger() != null ? this.getControllingPassenger() : this;
                        float f = entity == this ? 0.35F : 0.4F;
                        Vector3d vector3d2 = entity.getMotion();
                        float f1 = MathHelper.sqrt(vector3d2.x * vector3d2.x * (double)0.2F + vector3d2.y * vector3d2.y + vector3d2.z * vector3d2.z * (double)0.2F) * f;

                        if (f1 > 1.0F)
                        {
                            f1 = 1.0F;
                        }

                        this.playSwimSound(f1);
                    }
                    else
                    {
                        this.playStepSound(blockpos, blockstate);
                    }
                }
                else if (this.distanceWalkedOnStepModified > this.nextFlap && this.makeFlySound() && blockstate.isAir())
                {
                    this.nextFlap = this.playFlySound(this.distanceWalkedOnStepModified);
                }
            }

            try
            {
                this.doBlockCollisions();
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
                this.fillCrashReport(crashreportcategory);
                throw new ReportedException(crashreport);
            }

            float f2 = this.getSpeedFactor();
            this.setMotion(this.getMotion().mul((double)f2, 1.0D, (double)f2));

            if (this.world.getStatesInArea(this.getBoundingBox().shrink(0.001D)).noneMatch((p_233572_0_) ->
        {
            return p_233572_0_.isIn(BlockTags.FIRE) || p_233572_0_.isIn(Blocks.LAVA);
            }) && this.fire <= 0)
            {
                this.forceFireTicks(-this.getFireImmuneTicks());
            }

            if (this.isInWaterRainOrBubbleColumn() && this.isBurning())
            {
                this.playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                this.forceFireTicks(-this.getFireImmuneTicks());
            }

            this.world.getProfiler().endSection();
        }
    }

    protected BlockPos getOnPosition()
    {
        int i = MathHelper.floor(this.positionVec.x);
        int j = MathHelper.floor(this.positionVec.y - (double)0.2F);
        int k = MathHelper.floor(this.positionVec.z);
        BlockPos blockpos = new BlockPos(i, j, k);

        if (this.world.getBlockState(blockpos).isAir())
        {
            BlockPos blockpos1 = blockpos.down();
            BlockState blockstate = this.world.getBlockState(blockpos1);
            Block block = blockstate.getBlock();

            if (block.isIn(BlockTags.FENCES) || block.isIn(BlockTags.WALLS) || block instanceof FenceGateBlock)
            {
                return blockpos1;
            }
        }

        return blockpos;
    }

    protected float getJumpFactor()
    {
        float f = this.world.getBlockState(this.getPosition()).getBlock().getJumpFactor();
        float f1 = this.world.getBlockState(this.getPositionUnderneath()).getBlock().getJumpFactor();
        return (double)f == 1.0D ? f1 : f;
    }

    protected float getSpeedFactor()
    {
        Block block = this.world.getBlockState(this.getPosition()).getBlock();
        float f = block.getSpeedFactor();

        if (block != Blocks.WATER && block != Blocks.BUBBLE_COLUMN)
        {
            return (double)f == 1.0D ? this.world.getBlockState(this.getPositionUnderneath()).getBlock().getSpeedFactor() : f;
        }
        else
        {
            return f;
        }
    }

    protected BlockPos getPositionUnderneath()
    {
        return new BlockPos(this.positionVec.x, this.getBoundingBox().minY - 0.5000001D, this.positionVec.z);
    }

    protected Vector3d maybeBackOffFromEdge(Vector3d vec, MoverType mover)
    {
        return vec;
    }

    protected Vector3d handlePistonMovement(Vector3d pos)
    {
        if (pos.lengthSquared() <= 1.0E-7D)
        {
            return pos;
        }
        else
        {
            long i = this.world.getGameTime();

            if (i != this.pistonDeltasGameTime)
            {
                Arrays.fill(this.pistonDeltas, 0.0D);
                this.pistonDeltasGameTime = i;
            }

            if (pos.x != 0.0D)
            {
                double d2 = this.calculatePistonDeltas(Direction.Axis.X, pos.x);
                return Math.abs(d2) <= (double)1.0E-5F ? Vector3d.ZERO : new Vector3d(d2, 0.0D, 0.0D);
            }
            else if (pos.y != 0.0D)
            {
                double d1 = this.calculatePistonDeltas(Direction.Axis.Y, pos.y);
                return Math.abs(d1) <= (double)1.0E-5F ? Vector3d.ZERO : new Vector3d(0.0D, d1, 0.0D);
            }
            else if (pos.z != 0.0D)
            {
                double d0 = this.calculatePistonDeltas(Direction.Axis.Z, pos.z);
                return Math.abs(d0) <= (double)1.0E-5F ? Vector3d.ZERO : new Vector3d(0.0D, 0.0D, d0);
            }
            else
            {
                return Vector3d.ZERO;
            }
        }
    }

    private double calculatePistonDeltas(Direction.Axis axis, double distance)
    {
        int i = axis.ordinal();
        double d0 = MathHelper.clamp(distance + this.pistonDeltas[i], -0.51D, 0.51D);
        distance = d0 - this.pistonDeltas[i];
        this.pistonDeltas[i] = d0;
        return distance;
    }

    /**
     * Given a motion vector, return an updated vector that takes into account restrictions such as collisions (from all
     * directions) and step-up from stepHeight
     */
    private Vector3d getAllowedMovement(Vector3d vec)
    {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        ISelectionContext iselectioncontext = ISelectionContext.forEntity(this);
        VoxelShape voxelshape = this.world.getWorldBorder().getShape();
        Stream<VoxelShape> stream = VoxelShapes.compare(voxelshape, VoxelShapes.create(axisalignedbb.shrink(1.0E-7D)), IBooleanFunction.AND) ? Stream.empty() : Stream.of(voxelshape);
        Stream<VoxelShape> stream1 = this.world.func_230318_c_(this, axisalignedbb.expand(vec), (p_233561_0_) ->
        {
            return true;
        });
        ReuseableStream<VoxelShape> reuseablestream = new ReuseableStream<>(Stream.concat(stream1, stream));
        Vector3d vector3d = vec.lengthSquared() == 0.0D ? vec : collideBoundingBoxHeuristically(this, vec, axisalignedbb, this.world, iselectioncontext, reuseablestream);
        boolean flag = vec.x != vector3d.x;
        boolean flag1 = vec.y != vector3d.y;
        boolean flag2 = vec.z != vector3d.z;
        boolean flag3 = this.onGround || flag1 && vec.y < 0.0D;

        if (this.stepHeight > 0.0F && flag3 && (flag || flag2))
        {
            Vector3d vector3d1 = collideBoundingBoxHeuristically(this, new Vector3d(vec.x, (double)this.stepHeight, vec.z), axisalignedbb, this.world, iselectioncontext, reuseablestream);
            Vector3d vector3d2 = collideBoundingBoxHeuristically(this, new Vector3d(0.0D, (double)this.stepHeight, 0.0D), axisalignedbb.expand(vec.x, 0.0D, vec.z), this.world, iselectioncontext, reuseablestream);

            if (vector3d2.y < (double)this.stepHeight)
            {
                Vector3d vector3d3 = collideBoundingBoxHeuristically(this, new Vector3d(vec.x, 0.0D, vec.z), axisalignedbb.offset(vector3d2), this.world, iselectioncontext, reuseablestream).add(vector3d2);

                if (horizontalMag(vector3d3) > horizontalMag(vector3d1))
                {
                    vector3d1 = vector3d3;
                }
            }

            if (horizontalMag(vector3d1) > horizontalMag(vector3d))
            {
                return vector3d1.add(collideBoundingBoxHeuristically(this, new Vector3d(0.0D, -vector3d1.y + vec.y, 0.0D), axisalignedbb.offset(vector3d1), this.world, iselectioncontext, reuseablestream));
            }
        }

        return vector3d;
    }

    public static double horizontalMag(Vector3d vec)
    {
        return vec.x * vec.x + vec.z * vec.z;
    }

    public static Vector3d collideBoundingBoxHeuristically(@Nullable Entity entity, Vector3d vec, AxisAlignedBB collisionBox, World world, ISelectionContext context, ReuseableStream<VoxelShape> potentialHits)
    {
        boolean flag = vec.x == 0.0D;
        boolean flag1 = vec.y == 0.0D;
        boolean flag2 = vec.z == 0.0D;

        if ((!flag || !flag1) && (!flag || !flag2) && (!flag1 || !flag2))
        {
            ReuseableStream<VoxelShape> reuseablestream = new ReuseableStream<>(Stream.concat(potentialHits.createStream(), world.getCollisionShapes(entity, collisionBox.expand(vec))));
            return collideBoundingBox(vec, collisionBox, reuseablestream);
        }
        else
        {
            return getAllowedMovement(vec, collisionBox, world, context, potentialHits);
        }
    }

    public static Vector3d collideBoundingBox(Vector3d vec, AxisAlignedBB collisionBox, ReuseableStream<VoxelShape> potentialHits)
    {
        double d0 = vec.x;
        double d1 = vec.y;
        double d2 = vec.z;

        if (d1 != 0.0D)
        {
            d1 = VoxelShapes.getAllowedOffset(Direction.Axis.Y, collisionBox, potentialHits.createStream(), d1);

            if (d1 != 0.0D)
            {
                collisionBox = collisionBox.offset(0.0D, d1, 0.0D);
            }
        }

        boolean flag = Math.abs(d0) < Math.abs(d2);

        if (flag && d2 != 0.0D)
        {
            d2 = VoxelShapes.getAllowedOffset(Direction.Axis.Z, collisionBox, potentialHits.createStream(), d2);

            if (d2 != 0.0D)
            {
                collisionBox = collisionBox.offset(0.0D, 0.0D, d2);
            }
        }

        if (d0 != 0.0D)
        {
            d0 = VoxelShapes.getAllowedOffset(Direction.Axis.X, collisionBox, potentialHits.createStream(), d0);

            if (!flag && d0 != 0.0D)
            {
                collisionBox = collisionBox.offset(d0, 0.0D, 0.0D);
            }
        }

        if (!flag && d2 != 0.0D)
        {
            d2 = VoxelShapes.getAllowedOffset(Direction.Axis.Z, collisionBox, potentialHits.createStream(), d2);
        }

        return new Vector3d(d0, d1, d2);
    }

    public static Vector3d getAllowedMovement(Vector3d vec, AxisAlignedBB collisionBox, IWorldReader worldIn, ISelectionContext selectionContext, ReuseableStream<VoxelShape> potentialHits)
    {
        double d0 = vec.x;
        double d1 = vec.y;
        double d2 = vec.z;

        if (d1 != 0.0D)
        {
            d1 = VoxelShapes.getAllowedOffset(Direction.Axis.Y, collisionBox, worldIn, d1, selectionContext, potentialHits.createStream());

            if (d1 != 0.0D)
            {
                collisionBox = collisionBox.offset(0.0D, d1, 0.0D);
            }
        }

        boolean flag = Math.abs(d0) < Math.abs(d2);

        if (flag && d2 != 0.0D)
        {
            d2 = VoxelShapes.getAllowedOffset(Direction.Axis.Z, collisionBox, worldIn, d2, selectionContext, potentialHits.createStream());

            if (d2 != 0.0D)
            {
                collisionBox = collisionBox.offset(0.0D, 0.0D, d2);
            }
        }

        if (d0 != 0.0D)
        {
            d0 = VoxelShapes.getAllowedOffset(Direction.Axis.X, collisionBox, worldIn, d0, selectionContext, potentialHits.createStream());

            if (!flag && d0 != 0.0D)
            {
                collisionBox = collisionBox.offset(d0, 0.0D, 0.0D);
            }
        }

        if (!flag && d2 != 0.0D)
        {
            d2 = VoxelShapes.getAllowedOffset(Direction.Axis.Z, collisionBox, worldIn, d2, selectionContext, potentialHits.createStream());
        }

        return new Vector3d(d0, d1, d2);
    }

    protected float determineNextStepDistance()
    {
        return (float)((int)this.distanceWalkedOnStepModified + 1);
    }

    /**
     * Resets the entity's position to the center (planar) and bottom (vertical) points of its bounding box.
     */
    public void resetPositionToBB()
    {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        this.setRawPosition((axisalignedbb.minX + axisalignedbb.maxX) / 2.0D, axisalignedbb.minY, (axisalignedbb.minZ + axisalignedbb.maxZ) / 2.0D);
    }

    protected SoundEvent getSwimSound()
    {
        return SoundEvents.ENTITY_GENERIC_SWIM;
    }

    protected SoundEvent getSplashSound()
    {
        return SoundEvents.ENTITY_GENERIC_SPLASH;
    }

    protected SoundEvent getHighspeedSplashSound()
    {
        return SoundEvents.ENTITY_GENERIC_SPLASH;
    }

    protected void doBlockCollisions()
    {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        BlockPos blockpos = new BlockPos(axisalignedbb.minX + 0.001D, axisalignedbb.minY + 0.001D, axisalignedbb.minZ + 0.001D);
        BlockPos blockpos1 = new BlockPos(axisalignedbb.maxX - 0.001D, axisalignedbb.maxY - 0.001D, axisalignedbb.maxZ - 0.001D);
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

        if (this.world.isAreaLoaded(blockpos, blockpos1))
        {
            for (int i = blockpos.getX(); i <= blockpos1.getX(); ++i)
            {
                for (int j = blockpos.getY(); j <= blockpos1.getY(); ++j)
                {
                    for (int k = blockpos.getZ(); k <= blockpos1.getZ(); ++k)
                    {
                        blockpos$mutable.setPos(i, j, k);
                        BlockState blockstate = this.world.getBlockState(blockpos$mutable);

                        try
                        {
                            blockstate.onEntityCollision(this.world, blockpos$mutable, this);
                            this.onInsideBlock(blockstate);
                        }
                        catch (Throwable throwable)
                        {
                            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Colliding entity with block");
                            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being collided with");
                            CrashReportCategory.addBlockInfo(crashreportcategory, blockpos$mutable, blockstate);
                            throw new ReportedException(crashreport);
                        }
                    }
                }
            }
        }
    }

    protected void onInsideBlock(BlockState state)
    {
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn)
    {
        if (!blockIn.getMaterial().isLiquid())
        {
            BlockState blockstate = this.world.getBlockState(pos.up());
            SoundType soundtype = blockstate.isIn(Blocks.SNOW) ? blockstate.getSoundType() : blockIn.getSoundType();
            this.playSound(soundtype.getStepSound(), soundtype.getVolume() * 0.15F, soundtype.getPitch());
        }
    }

    protected void playSwimSound(float volume)
    {
        this.playSound(this.getSwimSound(), volume, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
    }

    protected float playFlySound(float volume)
    {
        return 0.0F;
    }

    protected boolean makeFlySound()
    {
        return false;
    }

    public void playSound(SoundEvent soundIn, float volume, float pitch)
    {
        if (!this.isSilent())
        {
            this.world.playSound((PlayerEntity)null, this.getPosX(), this.getPosY(), this.getPosZ(), soundIn, this.getSoundCategory(), volume, pitch);
        }
    }

    /**
     * @return True if this entity will not play sounds
     */
    public boolean isSilent()
    {
        return this.dataManager.get(SILENT);
    }

    /**
     * When set to true the entity will not play sounds.
     */
    public void setSilent(boolean isSilent)
    {
        this.dataManager.set(SILENT, isSilent);
    }

    public boolean hasNoGravity()
    {
        return this.dataManager.get(NO_GRAVITY);
    }

    public void setNoGravity(boolean noGravity)
    {
        this.dataManager.set(NO_GRAVITY, noGravity);
    }

    protected boolean canTriggerWalking()
    {
        return true;
    }

    protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos)
    {
        if (onGroundIn)
        {
            if (this.fallDistance > 0.0F)
            {
                state.getBlock().onFallenUpon(this.world, pos, this, this.fallDistance);
            }

            this.fallDistance = 0.0F;
        }
        else if (y < 0.0D)
        {
            this.fallDistance = (float)((double)this.fallDistance - y);
        }
    }

    public boolean isImmuneToFire()
    {
        return this.getType().isImmuneToFire();
    }

    public boolean onLivingFall(float distance, float damageMultiplier)
    {
        if (this.isBeingRidden())
        {
            for (Entity entity : this.getPassengers())
            {
                entity.onLivingFall(distance, damageMultiplier);
            }
        }

        return false;
    }

    /**
     * Checks if this entity is inside water (if inWater field is true as a result of handleWaterMovement() returning
     * true)
     */
    public boolean isInWater()
    {
        return this.inWater;
    }

    private boolean isInRain()
    {
        BlockPos blockpos = this.getPosition();
        return this.world.isRainingAt(blockpos) || this.world.isRainingAt(new BlockPos((double)blockpos.getX(), this.getBoundingBox().maxY, (double)blockpos.getZ()));
    }

    private boolean isInBubbleColumn()
    {
        return this.world.getBlockState(this.getPosition()).isIn(Blocks.BUBBLE_COLUMN);
    }

    /**
     * Checks if this entity is either in water or on an open air block in rain (used in wolves).
     */
    public boolean isWet()
    {
        return this.isInWater() || this.isInRain();
    }

    public boolean isInWaterRainOrBubbleColumn()
    {
        return this.isInWater() || this.isInRain() || this.isInBubbleColumn();
    }

    public boolean isInWaterOrBubbleColumn()
    {
        return this.isInWater() || this.isInBubbleColumn();
    }

    public boolean canSwim()
    {
        return this.eyesInWater && this.isInWater();
    }

    public void updateSwimming()
    {
        if (this.isSwimming())
        {
            this.setSwimming(this.isSprinting() && this.isInWater() && !this.isPassenger());
        }
        else
        {
            this.setSwimming(this.isSprinting() && this.canSwim() && !this.isPassenger());
        }
    }

    protected boolean func_233566_aG_()
    {
        this.eyesFluidLevel.clear();
        this.func_233567_aH_();
        double d0 = this.world.getDimensionType().isUltrawarm() ? 0.007D : 0.0023333333333333335D;
        boolean flag = this.handleFluidAcceleration(FluidTags.LAVA, d0);
        return this.isInWater() || flag;
    }

    void func_233567_aH_()
    {
        if (this.getRidingEntity() instanceof BoatEntity)
        {
            this.inWater = false;
        }
        else if (this.handleFluidAcceleration(FluidTags.WATER, 0.014D))
        {
            if (!this.inWater && !this.firstUpdate)
            {
                this.doWaterSplashEffect();
            }

            this.fallDistance = 0.0F;
            this.inWater = true;
            this.extinguish();
        }
        else
        {
            this.inWater = false;
        }
    }

    private void updateEyesInWater()
    {
        this.eyesInWater = this.areEyesInFluid(FluidTags.WATER);
        this.field_241335_O_ = null;
        double d0 = this.getPosYEye() - (double)0.11111111F;
        Entity entity = this.getRidingEntity();

        if (entity instanceof BoatEntity)
        {
            BoatEntity boatentity = (BoatEntity)entity;

            if (!boatentity.canSwim() && boatentity.getBoundingBox().maxY >= d0 && boatentity.getBoundingBox().minY <= d0)
            {
                return;
            }
        }

        BlockPos blockpos = new BlockPos(this.getPosX(), d0, this.getPosZ());
        FluidState fluidstate = this.world.getFluidState(blockpos);

        for (ITag<Fluid> itag : FluidTags.getAllTags())
        {
            if (fluidstate.isTagged(itag))
            {
                double d1 = (double)((float)blockpos.getY() + fluidstate.getActualHeight(this.world, blockpos));

                if (d1 > d0)
                {
                    this.field_241335_O_ = itag;
                }

                return;
            }
        }
    }

    /**
     * Plays the {@link #getSplashSound() splash sound}, and the {@link ParticleType#WATER_BUBBLE} and {@link
     * ParticleType#WATER_SPLASH} particles.
     */
    protected void doWaterSplashEffect()
    {
        Entity entity = this.isBeingRidden() && this.getControllingPassenger() != null ? this.getControllingPassenger() : this;
        float f = entity == this ? 0.2F : 0.9F;
        Vector3d vector3d = entity.getMotion();
        float f1 = MathHelper.sqrt(vector3d.x * vector3d.x * (double)0.2F + vector3d.y * vector3d.y + vector3d.z * vector3d.z * (double)0.2F) * f;

        if (f1 > 1.0F)
        {
            f1 = 1.0F;
        }

        if ((double)f1 < 0.25D)
        {
            this.playSound(this.getSplashSound(), f1, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
        }
        else
        {
            this.playSound(this.getHighspeedSplashSound(), f1, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
        }

        float f2 = (float)MathHelper.floor(this.getPosY());

        for (int i = 0; (float)i < 1.0F + this.size.width * 20.0F; ++i)
        {
            double d0 = (this.rand.nextDouble() * 2.0D - 1.0D) * (double)this.size.width;
            double d1 = (this.rand.nextDouble() * 2.0D - 1.0D) * (double)this.size.width;
            this.world.addParticle(ParticleTypes.BUBBLE, this.getPosX() + d0, (double)(f2 + 1.0F), this.getPosZ() + d1, vector3d.x, vector3d.y - this.rand.nextDouble() * (double)0.2F, vector3d.z);
        }

        for (int j = 0; (float)j < 1.0F + this.size.width * 20.0F; ++j)
        {
            double d2 = (this.rand.nextDouble() * 2.0D - 1.0D) * (double)this.size.width;
            double d3 = (this.rand.nextDouble() * 2.0D - 1.0D) * (double)this.size.width;
            this.world.addParticle(ParticleTypes.SPLASH, this.getPosX() + d2, (double)(f2 + 1.0F), this.getPosZ() + d3, vector3d.x, vector3d.y, vector3d.z);
        }
    }

    protected BlockState getStateBelow()
    {
        return this.world.getBlockState(this.getOnPosition());
    }

    public boolean func_230269_aK_()
    {
        return this.isSprinting() && !this.isInWater() && !this.isSpectator() && !this.isCrouching() && !this.isInLava() && this.isAlive();
    }

    protected void func_233569_aL_()
    {
        int i = MathHelper.floor(this.getPosX());
        int j = MathHelper.floor(this.getPosY() - (double)0.2F);
        int k = MathHelper.floor(this.getPosZ());
        BlockPos blockpos = new BlockPos(i, j, k);
        BlockState blockstate = this.world.getBlockState(blockpos);

        if (blockstate.getRenderType() != BlockRenderType.INVISIBLE)
        {
            Vector3d vector3d = this.getMotion();
            this.world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, blockstate), this.getPosX() + (this.rand.nextDouble() - 0.5D) * (double)this.size.width, this.getPosY() + 0.1D, this.getPosZ() + (this.rand.nextDouble() - 0.5D) * (double)this.size.width, vector3d.x * -4.0D, 1.5D, vector3d.z * -4.0D);
        }
    }

    public boolean areEyesInFluid(ITag<Fluid> tagIn)
    {
        return this.field_241335_O_ == tagIn;
    }

    public boolean isInLava()
    {
        return !this.firstUpdate && this.eyesFluidLevel.getDouble(FluidTags.LAVA) > 0.0D;
    }

    public void moveRelative(float p_213309_1_, Vector3d relative)
    {
        Vector3d vector3d = getAbsoluteMotion(relative, p_213309_1_, this.rotationYaw);
        this.setMotion(this.getMotion().add(vector3d));
    }

    private static Vector3d getAbsoluteMotion(Vector3d relative, float p_213299_1_, float facing)
    {
        double d0 = relative.lengthSquared();

        if (d0 < 1.0E-7D)
        {
            return Vector3d.ZERO;
        }
        else
        {
            Vector3d vector3d = (d0 > 1.0D ? relative.normalize() : relative).scale((double)p_213299_1_);
            float f = MathHelper.sin(facing * ((float)Math.PI / 180F));
            float f1 = MathHelper.cos(facing * ((float)Math.PI / 180F));
            return new Vector3d(vector3d.x * (double)f1 - vector3d.z * (double)f, vector3d.y, vector3d.z * (double)f1 + vector3d.x * (double)f);
        }
    }

    /**
     * Gets how bright this entity is.
     */
    public float getBrightness()
    {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(this.getPosX(), 0.0D, this.getPosZ());

        if (this.world.isBlockLoaded(blockpos$mutable))
        {
            blockpos$mutable.setY(MathHelper.floor(this.getPosYEye()));
            return this.world.getBrightness(blockpos$mutable);
        }
        else
        {
            return 0.0F;
        }
    }

    /**
     * Sets the reference to the World object.
     */
    public void setWorld(World worldIn)
    {
        this.world = worldIn;
    }

    /**
     * Sets position and rotation, clamping and wrapping params to valid values. Used by network code.
     */
    public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch)
    {
        this.func_242281_f(x, y, z);
        this.rotationYaw = yaw % 360.0F;
        this.rotationPitch = MathHelper.clamp(pitch, -90.0F, 90.0F) % 360.0F;
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
    }

    public void func_242281_f(double p_242281_1_, double p_242281_3_, double p_242281_5_)
    {
        double d0 = MathHelper.clamp(p_242281_1_, -3.0E7D, 3.0E7D);
        double d1 = MathHelper.clamp(p_242281_5_, -3.0E7D, 3.0E7D);
        this.prevPosX = d0;
        this.prevPosY = p_242281_3_;
        this.prevPosZ = d1;
        this.setPosition(d0, p_242281_3_, d1);
    }

    public void moveForced(Vector3d vec)
    {
        this.moveForced(vec.x, vec.y, vec.z);
    }

    public void moveForced(double x, double y, double z)
    {
        this.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
    }

    public void moveToBlockPosAndAngles(BlockPos pos, float rotationYawIn, float rotationPitchIn)
    {
        this.setLocationAndAngles((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, rotationYawIn, rotationPitchIn);
    }

    /**
     * Sets the location and Yaw/Pitch of an entity in the world
     */
    public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch)
    {
        this.forceSetPosition(x, y, z);
        this.rotationYaw = yaw;
        this.rotationPitch = pitch;
        this.recenterBoundingBox();
    }

    /**
     * Like {@link #setRawPosition}, but also sets {@link #prevPosX}/Y/Z and {@link #lastTickPosX}/Y/Z. {@link
     * #setLocationAndAngles} does the same thing, except it also updates the bounding box.
     */
    public void forceSetPosition(double x, double y, double z)
    {
        this.setRawPosition(x, y, z);
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.lastTickPosX = x;
        this.lastTickPosY = y;
        this.lastTickPosZ = z;
    }

    /**
     * Returns the distance to the entity.
     */
    public float getDistance(Entity entityIn)
    {
        float f = (float)(this.getPosX() - entityIn.getPosX());
        float f1 = (float)(this.getPosY() - entityIn.getPosY());
        float f2 = (float)(this.getPosZ() - entityIn.getPosZ());
        return MathHelper.sqrt(f * f + f1 * f1 + f2 * f2);
    }

    /**
     * Gets the squared distance to the position.
     */
    public double getDistanceSq(double x, double y, double z)
    {
        double d0 = this.getPosX() - x;
        double d1 = this.getPosY() - y;
        double d2 = this.getPosZ() - z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    /**
     * Returns the squared distance to the entity.
     */
    public double getDistanceSq(Entity entityIn)
    {
        return this.getDistanceSq(entityIn.getPositionVec());
    }

    public double getDistanceSq(Vector3d vec)
    {
        double d0 = this.getPosX() - vec.x;
        double d1 = this.getPosY() - vec.y;
        double d2 = this.getPosZ() - vec.z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    public void onCollideWithPlayer(PlayerEntity entityIn)
    {
    }

    /**
     * Applies a velocity to the entities, to push them away from eachother.
     */
    public void applyEntityCollision(Entity entityIn)
    {
        if (!this.isRidingSameEntity(entityIn))
        {
            if (!entityIn.noClip && !this.noClip)
            {
                double d0 = entityIn.getPosX() - this.getPosX();
                double d1 = entityIn.getPosZ() - this.getPosZ();
                double d2 = MathHelper.absMax(d0, d1);

                if (d2 >= (double)0.01F)
                {
                    d2 = (double)MathHelper.sqrt(d2);
                    d0 = d0 / d2;
                    d1 = d1 / d2;
                    double d3 = 1.0D / d2;

                    if (d3 > 1.0D)
                    {
                        d3 = 1.0D;
                    }

                    d0 = d0 * d3;
                    d1 = d1 * d3;
                    d0 = d0 * (double)0.05F;
                    d1 = d1 * (double)0.05F;
                    d0 = d0 * (double)(1.0F - this.entityCollisionReduction);
                    d1 = d1 * (double)(1.0F - this.entityCollisionReduction);

                    if (!this.isBeingRidden())
                    {
                        this.addVelocity(-d0, 0.0D, -d1);
                    }

                    if (!entityIn.isBeingRidden())
                    {
                        entityIn.addVelocity(d0, 0.0D, d1);
                    }
                }
            }
        }
    }

    /**
     * Adds to the current velocity of the entity, and sets {@link #isAirBorne} to true.
     */
    public void addVelocity(double x, double y, double z)
    {
        this.setMotion(this.getMotion().add(x, y, z));
        this.isAirBorne = true;
    }

    /**
     * Marks this entity's velocity as changed, so that it can be re-synced with the client later
     */
    protected void markVelocityChanged()
    {
        this.velocityChanged = true;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (this.isInvulnerableTo(source))
        {
            return false;
        }
        else
        {
            this.markVelocityChanged();
            return false;
        }
    }

    /**
     * interpolated look vector
     */
    public final Vector3d getLook(float partialTicks)
    {
        return this.getVectorForRotation(this.getPitch(partialTicks), this.getYaw(partialTicks));
    }

    /**
     * Gets the current pitch of the entity.
     */
    public float getPitch(float partialTicks)
    {
        return partialTicks == 1.0F ? this.rotationPitch : MathHelper.lerp(partialTicks, this.prevRotationPitch, this.rotationPitch);
    }

    /**
     * Gets the current yaw of the entity
     */
    public float getYaw(float partialTicks)
    {
        return partialTicks == 1.0F ? this.rotationYaw : MathHelper.lerp(partialTicks, this.prevRotationYaw, this.rotationYaw);
    }

    /**
     * Creates a Vec3 using the pitch and yaw of the entities rotation.
     */
    protected final Vector3d getVectorForRotation(float pitch, float yaw)
    {
        float f = pitch * ((float)Math.PI / 180F);
        float f1 = -yaw * ((float)Math.PI / 180F);
        float f2 = MathHelper.cos(f1);
        float f3 = MathHelper.sin(f1);
        float f4 = MathHelper.cos(f);
        float f5 = MathHelper.sin(f);
        return new Vector3d((double)(f3 * f4), (double)(-f5), (double)(f2 * f4));
    }

    public final Vector3d getUpVector(float partialTicks)
    {
        return this.calculateUpVector(this.getPitch(partialTicks), this.getYaw(partialTicks));
    }

    protected final Vector3d calculateUpVector(float pitch, float yaw)
    {
        return this.getVectorForRotation(pitch - 90.0F, yaw);
    }

    public final Vector3d getEyePosition(float partialTicks)
    {
        if (partialTicks == 1.0F)
        {
            return new Vector3d(this.getPosX(), this.getPosYEye(), this.getPosZ());
        }
        else
        {
            double d0 = MathHelper.lerp((double)partialTicks, this.prevPosX, this.getPosX());
            double d1 = MathHelper.lerp((double)partialTicks, this.prevPosY, this.getPosY()) + (double)this.getEyeHeight();
            double d2 = MathHelper.lerp((double)partialTicks, this.prevPosZ, this.getPosZ());
            return new Vector3d(d0, d1, d2);
        }
    }

    public Vector3d func_241842_k(float p_241842_1_)
    {
        return this.getEyePosition(p_241842_1_);
    }

    public final Vector3d func_242282_l(float p_242282_1_)
    {
        double d0 = MathHelper.lerp((double)p_242282_1_, this.prevPosX, this.getPosX());
        double d1 = MathHelper.lerp((double)p_242282_1_, this.prevPosY, this.getPosY());
        double d2 = MathHelper.lerp((double)p_242282_1_, this.prevPosZ, this.getPosZ());
        return new Vector3d(d0, d1, d2);
    }

    public RayTraceResult pick(double rayTraceDistance, float partialTicks, boolean p_213324_4_)
    {
        Vector3d vector3d = this.getEyePosition(partialTicks);
        Vector3d vector3d1 = this.getLook(partialTicks);
        Vector3d vector3d2 = vector3d.add(vector3d1.x * rayTraceDistance, vector3d1.y * rayTraceDistance, vector3d1.z * rayTraceDistance);
        return this.world.rayTraceBlocks(new RayTraceContext(vector3d, vector3d2, RayTraceContext.BlockMode.OUTLINE, p_213324_4_ ? RayTraceContext.FluidMode.ANY : RayTraceContext.FluidMode.NONE, this));
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return false;
    }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    public boolean canBePushed()
    {
        return false;
    }

    public void awardKillScore(Entity killed, int scoreValue, DamageSource damageSource)
    {
        if (killed instanceof ServerPlayerEntity)
        {
            CriteriaTriggers.ENTITY_KILLED_PLAYER.trigger((ServerPlayerEntity)killed, this, damageSource);
        }
    }

    public boolean isInRangeToRender3d(double x, double y, double z)
    {
        double d0 = this.getPosX() - x;
        double d1 = this.getPosY() - y;
        double d2 = this.getPosZ() - z;
        double d3 = d0 * d0 + d1 * d1 + d2 * d2;
        return this.isInRangeToRenderDist(d3);
    }

    /**
     * Checks if the entity is in range to render.
     */
    public boolean isInRangeToRenderDist(double distance)
    {
        double d0 = this.getBoundingBox().getAverageEdgeLength();

        if (Double.isNaN(d0))
        {
            d0 = 1.0D;
        }

        d0 = d0 * 64.0D * renderDistanceWeight;
        return distance < d0 * d0;
    }

    /**
     * Writes this entity to NBT, unless it has been removed. Also writes this entity's passengers, and the entity type
     * ID (so the produced NBT is sufficient to recreate the entity).
     *  
     * Generally, {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} should be used instead of this method.
     *  
     * @return True if the entity was written (and the passed compound should be saved); false if the entity was not
     * written.
     */
    public boolean writeUnlessRemoved(CompoundNBT compound)
    {
        String s = this.getEntityString();

        if (!this.removed && s != null)
        {
            compound.putString("id", s);
            this.writeWithoutTypeId(compound);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Writes this entity to NBT, unless it has been removed or it is a passenger. Also writes this entity's passengers,
     * and the entity type ID (so the produced NBT is sufficient to recreate the entity).
     * To always write the entity, use {@link #writeWithoutTypeId}.
     *  
     * @return True if the entity was written (and the passed compound should be saved); false if the entity was not
     * written.
     */
    public boolean writeUnlessPassenger(CompoundNBT compound)
    {
        return this.isPassenger() ? false : this.writeUnlessRemoved(compound);
    }

    /**
     * Writes this entity, including passengers, to NBT, regardless as to whether or not it is removed or a passenger.
     * Does <b>not</b> include the entity's type ID, so the NBT is insufficient to recreate the entity using {@link
     * AnvilChunkLoader#readWorldEntity}. Use {@link #writeUnlessPassenger} for that purpose.
     */
    public CompoundNBT writeWithoutTypeId(CompoundNBT compound)
    {
        try
        {
            if (this.ridingEntity != null)
            {
                compound.put("Pos", this.newDoubleNBTList(this.ridingEntity.getPosX(), this.getPosY(), this.ridingEntity.getPosZ()));
            }
            else
            {
                compound.put("Pos", this.newDoubleNBTList(this.getPosX(), this.getPosY(), this.getPosZ()));
            }

            Vector3d vector3d = this.getMotion();
            compound.put("Motion", this.newDoubleNBTList(vector3d.x, vector3d.y, vector3d.z));
            compound.put("Rotation", this.newFloatNBTList(this.rotationYaw, this.rotationPitch));
            compound.putFloat("FallDistance", this.fallDistance);
            compound.putShort("Fire", (short)this.fire);
            compound.putShort("Air", (short)this.getAir());
            compound.putBoolean("OnGround", this.onGround);
            compound.putBoolean("Invulnerable", this.invulnerable);
            compound.putInt("PortalCooldown", this.field_242273_aw);
            compound.putUniqueId("UUID", this.getUniqueID());
            ITextComponent itextcomponent = this.getCustomName();

            if (itextcomponent != null)
            {
                compound.putString("CustomName", ITextComponent.Serializer.toJson(itextcomponent));
            }

            if (this.isCustomNameVisible())
            {
                compound.putBoolean("CustomNameVisible", this.isCustomNameVisible());
            }

            if (this.isSilent())
            {
                compound.putBoolean("Silent", this.isSilent());
            }

            if (this.hasNoGravity())
            {
                compound.putBoolean("NoGravity", this.hasNoGravity());
            }

            if (this.glowing)
            {
                compound.putBoolean("Glowing", this.glowing);
            }

            if (!this.tags.isEmpty())
            {
                ListNBT listnbt = new ListNBT();

                for (String s : this.tags)
                {
                    listnbt.add(StringNBT.valueOf(s));
                }

                compound.put("Tags", listnbt);
            }

            this.writeAdditional(compound);

            if (this.isBeingRidden())
            {
                ListNBT listnbt1 = new ListNBT();

                for (Entity entity : this.getPassengers())
                {
                    CompoundNBT compoundnbt = new CompoundNBT();

                    if (entity.writeUnlessRemoved(compoundnbt))
                    {
                        listnbt1.add(compoundnbt);
                    }
                }

                if (!listnbt1.isEmpty())
                {
                    compound.put("Passengers", listnbt1);
                }
            }

            return compound;
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Saving entity NBT");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being saved");
            this.fillCrashReport(crashreportcategory);
            throw new ReportedException(crashreport);
        }
    }

    /**
     * Reads the entity from NBT (calls an abstract helper method to read specialized data)
     */
    public void read(CompoundNBT compound)
    {
        try
        {
            ListNBT listnbt = compound.getList("Pos", 6);
            ListNBT listnbt1 = compound.getList("Motion", 6);
            ListNBT listnbt2 = compound.getList("Rotation", 5);
            double d0 = listnbt1.getDouble(0);
            double d1 = listnbt1.getDouble(1);
            double d2 = listnbt1.getDouble(2);
            this.setMotion(Math.abs(d0) > 10.0D ? 0.0D : d0, Math.abs(d1) > 10.0D ? 0.0D : d1, Math.abs(d2) > 10.0D ? 0.0D : d2);
            this.forceSetPosition(listnbt.getDouble(0), listnbt.getDouble(1), listnbt.getDouble(2));
            this.rotationYaw = listnbt2.getFloat(0);
            this.rotationPitch = listnbt2.getFloat(1);
            this.prevRotationYaw = this.rotationYaw;
            this.prevRotationPitch = this.rotationPitch;
            this.setRotationYawHead(this.rotationYaw);
            this.setRenderYawOffset(this.rotationYaw);
            this.fallDistance = compound.getFloat("FallDistance");
            this.fire = compound.getShort("Fire");
            this.setAir(compound.getShort("Air"));
            this.onGround = compound.getBoolean("OnGround");
            this.invulnerable = compound.getBoolean("Invulnerable");
            this.field_242273_aw = compound.getInt("PortalCooldown");

            if (compound.hasUniqueId("UUID"))
            {
                this.entityUniqueID = compound.getUniqueId("UUID");
                this.cachedUniqueIdString = this.entityUniqueID.toString();
            }

            if (Double.isFinite(this.getPosX()) && Double.isFinite(this.getPosY()) && Double.isFinite(this.getPosZ()))
            {
                if (Double.isFinite((double)this.rotationYaw) && Double.isFinite((double)this.rotationPitch))
                {
                    this.recenterBoundingBox();
                    this.setRotation(this.rotationYaw, this.rotationPitch);

                    if (compound.contains("CustomName", 8))
                    {
                        String s = compound.getString("CustomName");

                        try
                        {
                            this.setCustomName(ITextComponent.Serializer.getComponentFromJson(s));
                        }
                        catch (Exception exception)
                        {
                            LOGGER.warn("Failed to parse entity custom name {}", s, exception);
                        }
                    }

                    this.setCustomNameVisible(compound.getBoolean("CustomNameVisible"));
                    this.setSilent(compound.getBoolean("Silent"));
                    this.setNoGravity(compound.getBoolean("NoGravity"));
                    this.setGlowing(compound.getBoolean("Glowing"));

                    if (compound.contains("Tags", 9))
                    {
                        this.tags.clear();
                        ListNBT listnbt3 = compound.getList("Tags", 8);
                        int i = Math.min(listnbt3.size(), 1024);

                        for (int j = 0; j < i; ++j)
                        {
                            this.tags.add(listnbt3.getString(j));
                        }
                    }

                    this.readAdditional(compound);

                    if (this.shouldSetPosAfterLoading())
                    {
                        this.recenterBoundingBox();
                    }
                }
                else
                {
                    throw new IllegalStateException("Entity has invalid rotation");
                }
            }
            else
            {
                throw new IllegalStateException("Entity has invalid position");
            }
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Loading entity NBT");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being loaded");
            this.fillCrashReport(crashreportcategory);
            throw new ReportedException(crashreport);
        }
    }

    protected boolean shouldSetPosAfterLoading()
    {
        return true;
    }

    @Nullable

    /**
     * Returns the string that identifies this Entity's class
     */
    protected final String getEntityString()
    {
        EntityType<?> entitytype = this.getType();
        ResourceLocation resourcelocation = EntityType.getKey(entitytype);
        return entitytype.isSerializable() && resourcelocation != null ? resourcelocation.toString() : null;
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected abstract void readAdditional(CompoundNBT compound);

    protected abstract void writeAdditional(CompoundNBT compound);

    /**
     * creates a NBT list from the array of doubles passed to this function
     */
    protected ListNBT newDoubleNBTList(double... numbers)
    {
        ListNBT listnbt = new ListNBT();

        for (double d0 : numbers)
        {
            listnbt.add(DoubleNBT.valueOf(d0));
        }

        return listnbt;
    }

    /**
     * Returns a new NBTTagList filled with the specified floats
     */
    protected ListNBT newFloatNBTList(float... numbers)
    {
        ListNBT listnbt = new ListNBT();

        for (float f : numbers)
        {
            listnbt.add(FloatNBT.valueOf(f));
        }

        return listnbt;
    }

    @Nullable
    public ItemEntity entityDropItem(IItemProvider itemIn)
    {
        return this.entityDropItem(itemIn, 0);
    }

    @Nullable
    public ItemEntity entityDropItem(IItemProvider itemIn, int offset)
    {
        return this.entityDropItem(new ItemStack(itemIn), (float)offset);
    }

    @Nullable
    public ItemEntity entityDropItem(ItemStack stack)
    {
        return this.entityDropItem(stack, 0.0F);
    }

    @Nullable

    /**
     * Drops an item at the position of the entity.
     */
    public ItemEntity entityDropItem(ItemStack stack, float offsetY)
    {
        if (stack.isEmpty())
        {
            return null;
        }
        else if (this.world.isRemote)
        {
            return null;
        }
        else
        {
            ItemEntity itementity = new ItemEntity(this.world, this.getPosX(), this.getPosY() + (double)offsetY, this.getPosZ(), stack);
            itementity.setDefaultPickupDelay();
            this.world.addEntity(itementity);
            return itementity;
        }
    }

    /**
     * Returns true if the entity has not been {@link #removed}.
     */
    public boolean isAlive()
    {
        return !this.removed;
    }

    /**
     * Checks if this entity is inside of an opaque block
     */
    public boolean isEntityInsideOpaqueBlock()
    {
        if (this.noClip)
        {
            return false;
        }
        else
        {
            float f = 0.1F;
            float f1 = this.size.width * 0.8F;
            AxisAlignedBB axisalignedbb = AxisAlignedBB.withSizeAtOrigin((double)f1, (double)0.1F, (double)f1).offset(this.getPosX(), this.getPosYEye(), this.getPosZ());
            return this.world.func_241457_a_(this, axisalignedbb, (p_241338_1_, p_241338_2_) ->
            {
                return p_241338_1_.isSuffocating(this.world, p_241338_2_);
            }).findAny().isPresent();
        }
    }

    public ActionResultType processInitialInteract(PlayerEntity player, Hand hand)
    {
        return ActionResultType.PASS;
    }

    public boolean canCollide(Entity entity)
    {
        return entity.func_241845_aY() && !this.isRidingSameEntity(entity);
    }

    public boolean func_241845_aY()
    {
        return false;
    }

    /**
     * Handles updating while riding another entity
     */
    public void updateRidden()
    {
        this.setMotion(Vector3d.ZERO);
        this.tick();

        if (this.isPassenger())
        {
            this.getRidingEntity().updatePassenger(this);
        }
    }

    public void updatePassenger(Entity passenger)
    {
        this.positionRider(passenger, Entity::setPosition);
    }

    private void positionRider(Entity entity, Entity.IMoveCallback callback)
    {
        if (this.isPassenger(entity))
        {
            double d0 = this.getPosY() + this.getMountedYOffset() + entity.getYOffset();
            callback.accept(entity, this.getPosX(), d0, this.getPosZ());
        }
    }

    /**
     * Applies this entity's orientation (pitch/yaw) to another entity. Used to update passenger orientation.
     */
    public void applyOrientationToEntity(Entity entityToUpdate)
    {
    }

    /**
     * Returns the Y Offset of this entity.
     */
    public double getYOffset()
    {
        return 0.0D;
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    public double getMountedYOffset()
    {
        return (double)this.size.height * 0.75D;
    }

    public boolean startRiding(Entity entityIn)
    {
        return this.startRiding(entityIn, false);
    }

    public boolean isLiving()
    {
        return this instanceof LivingEntity;
    }

    public boolean startRiding(Entity entityIn, boolean force)
    {
        for (Entity entity = entityIn; entity.ridingEntity != null; entity = entity.ridingEntity)
        {
            if (entity.ridingEntity == this)
            {
                return false;
            }
        }

        if (force || this.canBeRidden(entityIn) && entityIn.canFitPassenger(this))
        {
            if (this.isPassenger())
            {
                this.stopRiding();
            }

            this.setPose(Pose.STANDING);
            this.ridingEntity = entityIn;
            this.ridingEntity.addPassenger(this);
            return true;
        }
        else
        {
            return false;
        }
    }

    protected boolean canBeRidden(Entity entityIn)
    {
        return !this.isSneaking() && this.rideCooldown <= 0;
    }

    protected boolean isPoseClear(Pose pose)
    {
        return this.world.hasNoCollisions(this, this.getBoundingBox(pose).shrink(1.0E-7D));
    }

    /**
     * Dismounts all entities riding this entity from this entity.
     */
    public void removePassengers()
    {
        for (int i = this.passengers.size() - 1; i >= 0; --i)
        {
            this.passengers.get(i).stopRiding();
        }
    }

    public void dismount()
    {
        if (this.ridingEntity != null)
        {
            Entity entity = this.ridingEntity;
            this.ridingEntity = null;
            entity.removePassenger(this);
        }
    }

    /**
     * Dismounts this entity from the entity it is riding.
     */
    public void stopRiding()
    {
        this.dismount();
    }

    protected void addPassenger(Entity passenger)
    {
        if (passenger.getRidingEntity() != this)
        {
            throw new IllegalStateException("Use x.startRiding(y), not y.addPassenger(x)");
        }
        else
        {
            if (!this.world.isRemote && passenger instanceof PlayerEntity && !(this.getControllingPassenger() instanceof PlayerEntity))
            {
                this.passengers.add(0, passenger);
            }
            else
            {
                this.passengers.add(passenger);
            }
        }
    }

    protected void removePassenger(Entity passenger)
    {
        if (passenger.getRidingEntity() == this)
        {
            throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
        }
        else
        {
            this.passengers.remove(passenger);
            passenger.rideCooldown = 60;
        }
    }

    protected boolean canFitPassenger(Entity passenger)
    {
        return this.getPassengers().size() < 1;
    }

    /**
     * Sets a target for the client to interpolate towards over the next few ticks
     */
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport)
    {
        this.setPosition(x, y, z);
        this.setRotation(yaw, pitch);
    }

    public void setHeadRotation(float yaw, int pitch)
    {
        this.setRotationYawHead(yaw);
    }

    public float getCollisionBorderSize()
    {
        return 0.0F;
    }

    /**
     * returns a (normalized) vector of where this entity is looking
     */
    public Vector3d getLookVec()
    {
        return this.getVectorForRotation(this.rotationPitch, this.rotationYaw);
    }

    /**
     * returns the Entity's pitch and yaw as a Vec2f
     */
    public Vector2f getPitchYaw()
    {
        return new Vector2f(this.rotationPitch, this.rotationYaw);
    }

    public Vector3d getForward()
    {
        return Vector3d.fromPitchYaw(this.getPitchYaw());
    }

    /**
     * Marks the entity as being inside a portal, activating teleportation logic in onEntityUpdate() in the following
     * tick(s).
     */
    public void setPortal(BlockPos pos)
    {
        if (this.func_242280_ah())
        {
            this.func_242279_ag();
        }
        else
        {
            if (!this.world.isRemote && !pos.equals(this.field_242271_ac))
            {
                this.field_242271_ac = pos.toImmutable();
            }

            this.inPortal = true;
        }
    }

    protected void updatePortal()
    {
        if (this.world instanceof ServerWorld)
        {
            int i = this.getMaxInPortalTime();
            ServerWorld serverworld = (ServerWorld)this.world;

            if (this.inPortal)
            {
                MinecraftServer minecraftserver = serverworld.getServer();
                RegistryKey<World> registrykey = this.world.getDimensionKey() == World.THE_NETHER ? World.OVERWORLD : World.THE_NETHER;
                ServerWorld serverworld1 = minecraftserver.getWorld(registrykey);

                if (serverworld1 != null && minecraftserver.getAllowNether() && !this.isPassenger() && this.portalCounter++ >= i)
                {
                    this.world.getProfiler().startSection("portal");
                    this.portalCounter = i;
                    this.func_242279_ag();
                    this.changeDimension(serverworld1);
                    this.world.getProfiler().endSection();
                }

                this.inPortal = false;
            }
            else
            {
                if (this.portalCounter > 0)
                {
                    this.portalCounter -= 4;
                }

                if (this.portalCounter < 0)
                {
                    this.portalCounter = 0;
                }
            }

            this.decrementTimeUntilPortal();
        }
    }

    /**
     * Return the amount of cooldown before this entity can use a portal again.
     */
    public int getPortalCooldown()
    {
        return 300;
    }

    /**
     * Updates the entity motion clientside, called by packets from the server
     */
    public void setVelocity(double x, double y, double z)
    {
        this.setMotion(x, y, z);
    }

    /**
     * Handler for {@link World#setEntityState}
     */
    public void handleStatusUpdate(byte id)
    {
        switch (id)
        {
            case 53:
                HoneyBlock.entitySlideParticles(this);

            default:
        }
    }

    /**
     * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
     */
    public void performHurtAnimation()
    {
    }

    public Iterable<ItemStack> getHeldEquipment()
    {
        return EMPTY_EQUIPMENT;
    }

    public Iterable<ItemStack> getArmorInventoryList()
    {
        return EMPTY_EQUIPMENT;
    }

    public Iterable<ItemStack> getEquipmentAndArmor()
    {
        return Iterables.concat(this.getHeldEquipment(), this.getArmorInventoryList());
    }

    public void setItemStackToSlot(EquipmentSlotType slotIn, ItemStack stack)
    {
    }

    /**
     * Returns true if the entity is on fire. Used by render to add the fire effect on rendering.
     */
    public boolean isBurning()
    {
        boolean flag = this.world != null && this.world.isRemote;
        return !this.isImmuneToFire() && (this.fire > 0 || flag && this.getFlag(0));
    }

    public boolean isPassenger()
    {
        return this.getRidingEntity() != null;
    }

    /**
     * If at least 1 entity is riding this one
     */
    public boolean isBeingRidden()
    {
        return !this.getPassengers().isEmpty();
    }

    public boolean canBeRiddenInWater()
    {
        return true;
    }

    public void setSneaking(boolean keyDownIn)
    {
        this.setFlag(1, keyDownIn);
    }

    public boolean isSneaking()
    {
        return this.getFlag(1);
    }

    public boolean isSteppingCarefully()
    {
        return this.isSneaking();
    }

    public boolean isSuppressingBounce()
    {
        return this.isSneaking();
    }

    public boolean isDiscrete()
    {
        return this.isSneaking();
    }

    public boolean isDescending()
    {
        return this.isSneaking();
    }

    public boolean isCrouching()
    {
        return this.getPose() == Pose.CROUCHING;
    }

    /**
     * Get if the Entity is sprinting.
     */
    public boolean isSprinting()
    {
        return this.getFlag(3);
    }

    /**
     * Set sprinting switch for Entity.
     */
    public void setSprinting(boolean sprinting)
    {
        this.setFlag(3, sprinting);
    }

    public boolean isSwimming()
    {
        return this.getFlag(4);
    }

    public boolean isActualySwimming()
    {
        return this.getPose() == Pose.SWIMMING;
    }

    public boolean isVisuallySwimming()
    {
        return this.isActualySwimming() && !this.isInWater();
    }

    public void setSwimming(boolean swimming)
    {
        this.setFlag(4, swimming);
    }

    public boolean isGlowing()
    {
        return this.glowing || this.world.isRemote && this.getFlag(6);
    }

    public void setGlowing(boolean glowingIn)
    {
        this.glowing = glowingIn;

        if (!this.world.isRemote)
        {
            this.setFlag(6, this.glowing);
        }
    }

    public boolean isInvisible()
    {
        return this.getFlag(5);
    }

    /**
     * Only used by renderer in EntityLivingBase subclasses.
     * Determines if an entity is visible or not to a specific player, if the entity is normally invisible.
     * For EntityLivingBase subclasses, returning false when invisible will render the entity semi-transparent.
     */
    public boolean isInvisibleToPlayer(PlayerEntity player)
    {
        if (player.isSpectator())
        {
            return false;
        }
        else
        {
            Team team = this.getTeam();
            return team != null && player != null && player.getTeam() == team && team.getSeeFriendlyInvisiblesEnabled() ? false : this.isInvisible();
        }
    }

    @Nullable
    public Team getTeam()
    {
        return this.world.getScoreboard().getPlayersTeam(this.getScoreboardName());
    }

    /**
     * Returns whether this Entity is on the same team as the given Entity.
     */
    public boolean isOnSameTeam(Entity entityIn)
    {
        return this.isOnScoreboardTeam(entityIn.getTeam());
    }

    /**
     * Returns whether this Entity is on the given scoreboard team.
     */
    public boolean isOnScoreboardTeam(Team teamIn)
    {
        return this.getTeam() != null ? this.getTeam().isSameTeam(teamIn) : false;
    }

    public void setInvisible(boolean invisible)
    {
        this.setFlag(5, invisible);
    }

    /**
     * Returns true if the flag is active for the entity. Known flags: 0: burning; 1: sneaking; 2: unused; 3: sprinting;
     * 4: swimming; 5: invisible; 6: glowing; 7: elytra flying
     */
    protected boolean getFlag(int flag)
    {
        return (this.dataManager.get(FLAGS) & 1 << flag) != 0;
    }

    /**
     * Enable or disable a entity flag, see getEntityFlag to read the know flags.
     */
    protected void setFlag(int flag, boolean set)
    {
        byte b0 = this.dataManager.get(FLAGS);

        if (set)
        {
            this.dataManager.set(FLAGS, (byte)(b0 | 1 << flag));
        }
        else
        {
            this.dataManager.set(FLAGS, (byte)(b0 & ~(1 << flag)));
        }
    }

    public int getMaxAir()
    {
        return 300;
    }

    public int getAir()
    {
        return this.dataManager.get(AIR);
    }

    public void setAir(int air)
    {
        this.dataManager.set(AIR, air);
    }

    public void func_241841_a(ServerWorld p_241841_1_, LightningBoltEntity p_241841_2_)
    {
        this.forceFireTicks(this.fire + 1);

        if (this.fire == 0)
        {
            this.setFire(8);
        }

        this.attackEntityFrom(DamageSource.LIGHTNING_BOLT, 5.0F);
    }

    public void onEnterBubbleColumnWithAirAbove(boolean downwards)
    {
        Vector3d vector3d = this.getMotion();
        double d0;

        if (downwards)
        {
            d0 = Math.max(-0.9D, vector3d.y - 0.03D);
        }
        else
        {
            d0 = Math.min(1.8D, vector3d.y + 0.1D);
        }

        this.setMotion(vector3d.x, d0, vector3d.z);
    }

    public void onEnterBubbleColumn(boolean downwards)
    {
        Vector3d vector3d = this.getMotion();
        double d0;

        if (downwards)
        {
            d0 = Math.max(-0.3D, vector3d.y - 0.03D);
        }
        else
        {
            d0 = Math.min(0.7D, vector3d.y + 0.06D);
        }

        this.setMotion(vector3d.x, d0, vector3d.z);
        this.fallDistance = 0.0F;
    }

    public void func_241847_a(ServerWorld p_241847_1_, LivingEntity p_241847_2_)
    {
    }

    protected void pushOutOfBlocks(double x, double y, double z)
    {
        BlockPos blockpos = new BlockPos(x, y, z);
        Vector3d vector3d = new Vector3d(x - (double)blockpos.getX(), y - (double)blockpos.getY(), z - (double)blockpos.getZ());
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        Direction direction = Direction.UP;
        double d0 = Double.MAX_VALUE;

        for (Direction direction1 : new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP})
        {
            blockpos$mutable.setAndMove(blockpos, direction1);

            if (!this.world.getBlockState(blockpos$mutable).hasOpaqueCollisionShape(this.world, blockpos$mutable))
            {
                double d1 = vector3d.getCoordinate(direction1.getAxis());
                double d2 = direction1.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0D - d1 : d1;

                if (d2 < d0)
                {
                    d0 = d2;
                    direction = direction1;
                }
            }
        }
        float f = this.rand.nextFloat() * 0.2F + 0.1F;
        float f1 = (float)direction.getAxisDirection().getOffset();
        Vector3d vector3d1 = this.getMotion().scale(0.75D);

        if (direction.getAxis() == Direction.Axis.X)
        {
            this.setMotion((double)(f1 * f), vector3d1.y, vector3d1.z);
        }
        else if (direction.getAxis() == Direction.Axis.Y)
        {
            this.setMotion(vector3d1.x, (double)(f1 * f), vector3d1.z);
        }
        else if (direction.getAxis() == Direction.Axis.Z)
        {
            this.setMotion(vector3d1.x, vector3d1.y, (double)(f1 * f));
        }
    }

    public void setMotionMultiplier(BlockState state, Vector3d motionMultiplierIn)
    {
        this.fallDistance = 0.0F;
        this.motionMultiplier = motionMultiplierIn;
    }

    private static ITextComponent func_233573_b_(ITextComponent p_233573_0_)
    {
        IFormattableTextComponent iformattabletextcomponent = p_233573_0_.copyRaw().setStyle(p_233573_0_.getStyle().setClickEvent((ClickEvent)null));

        for (ITextComponent itextcomponent : p_233573_0_.getSiblings())
        {
            iformattabletextcomponent.append(func_233573_b_(itextcomponent));
        }

        return iformattabletextcomponent;
    }

    public ITextComponent getName()
    {
        ITextComponent itextcomponent = this.getCustomName();
        return itextcomponent != null ? func_233573_b_(itextcomponent) : this.getProfessionName();
    }

    protected ITextComponent getProfessionName()
    {
        return this.type.getName();
    }

    /**
     * Returns true if Entity argument is equal to this Entity
     */
    public boolean isEntityEqual(Entity entityIn)
    {
        return this == entityIn;
    }

    public float getRotationYawHead()
    {
        return 0.0F;
    }

    /**
     * Sets the head's yaw rotation of the entity.
     */
    public void setRotationYawHead(float rotation)
    {
    }

    /**
     * Set the render yaw offset
     */
    public void setRenderYawOffset(float offset)
    {
    }

    /**
     * Returns true if it's possible to attack this entity with an item.
     */
    public boolean canBeAttackedWithItem()
    {
        return true;
    }

    /**
     * Called when a player attacks an entity. If this returns true the attack will not happen.
     */
    public boolean hitByEntity(Entity entityIn)
    {
        return false;
    }

    public String toString()
    {
        return String.format(Locale.ROOT, "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]", this.getClass().getSimpleName(), this.getName().getString(), this.entityId, this.world == null ? "~NULL~" : this.world.toString(), this.getPosX(), this.getPosY(), this.getPosZ());
    }

    /**
     * Returns whether this Entity is invulnerable to the given DamageSource.
     */
    public boolean isInvulnerableTo(DamageSource source)
    {
        return this.invulnerable && source != DamageSource.OUT_OF_WORLD && !source.isCreativePlayer();
    }

    public boolean isInvulnerable()
    {
        return this.invulnerable;
    }

    /**
     * Sets whether this Entity is invulnerable.
     */
    public void setInvulnerable(boolean isInvulnerable)
    {
        this.invulnerable = isInvulnerable;
    }

    /**
     * Sets this entity's location and angles to the location and angles of the passed in entity.
     */
    public void copyLocationAndAnglesFrom(Entity entityIn)
    {
        this.setLocationAndAngles(entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ(), entityIn.rotationYaw, entityIn.rotationPitch);
    }

    /**
     * Prepares this entity in new dimension by copying NBT data from entity in old dimension
     */
    public void copyDataFromOld(Entity entityIn)
    {
        CompoundNBT compoundnbt = entityIn.writeWithoutTypeId(new CompoundNBT());
        compoundnbt.remove("Dimension");
        this.read(compoundnbt);
        this.field_242273_aw = entityIn.field_242273_aw;
        this.field_242271_ac = entityIn.field_242271_ac;
    }

    @Nullable
    public Entity changeDimension(ServerWorld server)
    {
        if (this.world instanceof ServerWorld && !this.removed)
        {
            this.world.getProfiler().startSection("changeDimension");
            this.detach();
            this.world.getProfiler().startSection("reposition");
            PortalInfo portalinfo = this.func_241829_a(server);

            if (portalinfo == null)
            {
                return null;
            }
            else
            {
                this.world.getProfiler().endStartSection("reloading");
                Entity entity = this.getType().create(server);

                if (entity != null)
                {
                    entity.copyDataFromOld(this);
                    entity.setLocationAndAngles(portalinfo.pos.x, portalinfo.pos.y, portalinfo.pos.z, portalinfo.rotationYaw, entity.rotationPitch);
                    entity.setMotion(portalinfo.motion);
                    server.addFromAnotherDimension(entity);

                    if (server.getDimensionKey() == World.THE_END)
                    {
                        ServerWorld.func_241121_a_(server);
                    }
                }

                this.setDead();
                this.world.getProfiler().endSection();
                ((ServerWorld)this.world).resetUpdateEntityTick();
                server.resetUpdateEntityTick();
                this.world.getProfiler().endSection();
                return entity;
            }
        }
        else
        {
            return null;
        }
    }

    protected void setDead()
    {
        this.removed = true;
    }

    @Nullable
    protected PortalInfo func_241829_a(ServerWorld p_241829_1_)
    {
        boolean flag = this.world.getDimensionKey() == World.THE_END && p_241829_1_.getDimensionKey() == World.OVERWORLD;
        boolean flag1 = p_241829_1_.getDimensionKey() == World.THE_END;

        if (!flag && !flag1)
        {
            boolean flag2 = p_241829_1_.getDimensionKey() == World.THE_NETHER;

            if (this.world.getDimensionKey() != World.THE_NETHER && !flag2)
            {
                return null;
            }
            else
            {
                WorldBorder worldborder = p_241829_1_.getWorldBorder();
                double d0 = Math.max(-2.9999872E7D, worldborder.minX() + 16.0D);
                double d1 = Math.max(-2.9999872E7D, worldborder.minZ() + 16.0D);
                double d2 = Math.min(2.9999872E7D, worldborder.maxX() - 16.0D);
                double d3 = Math.min(2.9999872E7D, worldborder.maxZ() - 16.0D);
                double d4 = DimensionType.getCoordinateDifference(this.world.getDimensionType(), p_241829_1_.getDimensionType());
                BlockPos blockpos1 = new BlockPos(MathHelper.clamp(this.getPosX() * d4, d0, d2), this.getPosY(), MathHelper.clamp(this.getPosZ() * d4, d1, d3));
                return this.func_241830_a(p_241829_1_, blockpos1, flag2).map((p_242275_2_) ->
                {
                    BlockState blockstate = this.world.getBlockState(this.field_242271_ac);
                    Direction.Axis direction$axis;
                    Vector3d vector3d;

                    if (blockstate.hasProperty(BlockStateProperties.HORIZONTAL_AXIS))
                    {
                        direction$axis = blockstate.get(BlockStateProperties.HORIZONTAL_AXIS);
                        TeleportationRepositioner.Result teleportationrepositioner$result = TeleportationRepositioner.findLargestRectangle(this.field_242271_ac, direction$axis, 21, Direction.Axis.Y, 21, (p_242276_2_) ->
                        {
                            return this.world.getBlockState(p_242276_2_) == blockstate;
                        });
                        vector3d = this.func_241839_a(direction$axis, teleportationrepositioner$result);
                    }
                    else {
                        direction$axis = Direction.Axis.X;
                        vector3d = new Vector3d(0.5D, 0.0D, 0.0D);
                    }

                    return PortalSize.func_242963_a(p_241829_1_, p_242275_2_, direction$axis, vector3d, this.getSize(this.getPose()), this.getMotion(), this.rotationYaw, this.rotationPitch);
                }).orElse((PortalInfo)null);
            }
        }
        else
        {
            BlockPos blockpos;

            if (flag1)
            {
                blockpos = ServerWorld.field_241108_a_;
            }
            else
            {
                blockpos = p_241829_1_.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, p_241829_1_.getSpawnPoint());
            }

            return new PortalInfo(new Vector3d((double)blockpos.getX() + 0.5D, (double)blockpos.getY(), (double)blockpos.getZ() + 0.5D), this.getMotion(), this.rotationYaw, this.rotationPitch);
        }
    }

    protected Vector3d func_241839_a(Direction.Axis axis, TeleportationRepositioner.Result result)
    {
        return PortalSize.func_242973_a(result, axis, this.getPositionVec(), this.getSize(this.getPose()));
    }

    protected Optional<TeleportationRepositioner.Result> func_241830_a(ServerWorld p_241830_1_, BlockPos p_241830_2_, boolean p_241830_3_)
    {
        return p_241830_1_.getDefaultTeleporter().getExistingPortal(p_241830_2_, p_241830_3_);
    }

    /**
     * Returns false if this Entity is a boss, true otherwise.
     */
    public boolean isNonBoss()
    {
        return true;
    }

    /**
     * Explosion resistance of a block relative to this entity
     */
    public float getExplosionResistance(Explosion explosionIn, IBlockReader worldIn, BlockPos pos, BlockState blockStateIn, FluidState fluidState, float explosionPower)
    {
        return explosionPower;
    }

    public boolean canExplosionDestroyBlock(Explosion explosionIn, IBlockReader worldIn, BlockPos pos, BlockState blockStateIn, float explosionPower)
    {
        return true;
    }

    /**
     * The maximum height from where the entity is alowed to jump (used in pathfinder)
     */
    public int getMaxFallHeight()
    {
        return 3;
    }

    /**
     * Return whether this entity should NOT trigger a pressure plate or a tripwire.
     */
    public boolean doesEntityNotTriggerPressurePlate()
    {
        return false;
    }

    public void fillCrashReport(CrashReportCategory category)
    {
        category.addDetail("Entity Type", () ->
        {
            return EntityType.getKey(this.getType()) + " (" + this.getClass().getCanonicalName() + ")";
        });
        category.addDetail("Entity ID", this.entityId);
        category.addDetail("Entity Name", () ->
        {
            return this.getName().getString();
        });
        category.addDetail("Entity's Exact location", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.getPosX(), this.getPosY(), this.getPosZ()));
        category.addDetail("Entity's Block location", CrashReportCategory.getCoordinateInfo(MathHelper.floor(this.getPosX()), MathHelper.floor(this.getPosY()), MathHelper.floor(this.getPosZ())));
        Vector3d vector3d = this.getMotion();
        category.addDetail("Entity's Momentum", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", vector3d.x, vector3d.y, vector3d.z));
        category.addDetail("Entity's Passengers", () ->
        {
            return this.getPassengers().toString();
        });
        category.addDetail("Entity's Vehicle", () ->
        {
            return this.getRidingEntity().toString();
        });
    }

    /**
     * Return whether this entity should be rendered as on fire.
     */
    public boolean canRenderOnFire()
    {
        return this.isBurning() && !this.isSpectator();
    }

    public void setUniqueId(UUID uniqueIdIn)
    {
        this.entityUniqueID = uniqueIdIn;
        this.cachedUniqueIdString = this.entityUniqueID.toString();
    }

    /**
     * Returns the UUID of this entity.
     */
    public UUID getUniqueID()
    {
        return this.entityUniqueID;
    }

    public String getCachedUniqueIdString()
    {
        return this.cachedUniqueIdString;
    }

    /**
     * Returns a String to use as this entity's name in the scoreboard/entity selector systems
     */
    public String getScoreboardName()
    {
        return this.cachedUniqueIdString;
    }

    public boolean isPushedByWater()
    {
        return true;
    }

    public static double getRenderDistanceWeight()
    {
        return renderDistanceWeight;
    }

    public static void setRenderDistanceWeight(double renderDistWeight)
    {
        renderDistanceWeight = renderDistWeight;
    }

    public ITextComponent getDisplayName()
    {
        return ScorePlayerTeam.func_237500_a_(this.getTeam(), this.getName()).modifyStyle((p_211516_1_) ->
        {
            return p_211516_1_.setHoverEvent(this.getHoverEvent()).setInsertion(this.getCachedUniqueIdString());
        });
    }

    public void setCustomName(@Nullable ITextComponent name)
    {
        this.dataManager.set(CUSTOM_NAME, Optional.ofNullable(name));
    }

    @Nullable
    public ITextComponent getCustomName()
    {
        return this.dataManager.get(CUSTOM_NAME).orElse((ITextComponent)null);
    }

    public boolean hasCustomName()
    {
        return this.dataManager.get(CUSTOM_NAME).isPresent();
    }

    public void setCustomNameVisible(boolean alwaysRenderNameTag)
    {
        this.dataManager.set(CUSTOM_NAME_VISIBLE, alwaysRenderNameTag);
    }

    public boolean isCustomNameVisible()
    {
        return this.dataManager.get(CUSTOM_NAME_VISIBLE);
    }

    /**
     * Teleports the entity, forcing the destination to stay loaded for a short time
     */
    public final void teleportKeepLoaded(double x, double y, double z)
    {
        if (this.world instanceof ServerWorld)
        {
            ChunkPos chunkpos = new ChunkPos(new BlockPos(x, y, z));
            ((ServerWorld)this.world).getChunkProvider().registerTicket(TicketType.POST_TELEPORT, chunkpos, 0, this.getEntityId());
            this.world.getChunk(chunkpos.x, chunkpos.z);
            this.setPositionAndUpdate(x, y, z);
        }
    }

    /**
     * Sets the position of the entity and updates the 'last' variables
     */
    public void setPositionAndUpdate(double x, double y, double z)
    {
        if (this.world instanceof ServerWorld)
        {
            ServerWorld serverworld = (ServerWorld)this.world;
            this.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
            this.getSelfAndPassengers().forEach((p_233565_1_) ->
            {
                serverworld.chunkCheck(p_233565_1_);
                p_233565_1_.isPositionDirty = true;

                for (Entity entity : p_233565_1_.passengers)
                {
                    p_233565_1_.positionRider(entity, Entity::moveForced);
                }
            });
        }
    }

    public boolean getAlwaysRenderNameTagForRender()
    {
        return this.isCustomNameVisible();
    }

    public void notifyDataManagerChange(DataParameter<?> key)
    {
        if (POSE.equals(key))
        {
            this.recalculateSize();
        }
    }

    public void recalculateSize()
    {
        EntitySize entitysize = this.size;
        Pose pose = this.getPose();
        EntitySize entitysize1 = this.getSize(pose);
        this.size = entitysize1;
        this.eyeHeight = this.getEyeHeight(pose, entitysize1);

        if (entitysize1.width < entitysize.width)
        {
            double d0 = (double)entitysize1.width / 2.0D;
            this.setBoundingBox(new AxisAlignedBB(this.getPosX() - d0, this.getPosY(), this.getPosZ() - d0, this.getPosX() + d0, this.getPosY() + (double)entitysize1.height, this.getPosZ() + d0));
        }
        else
        {
            AxisAlignedBB axisalignedbb = this.getBoundingBox();
            this.setBoundingBox(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + (double)entitysize1.width, axisalignedbb.minY + (double)entitysize1.height, axisalignedbb.minZ + (double)entitysize1.width));

            if (entitysize1.width > entitysize.width && !this.firstUpdate && !this.world.isRemote)
            {
                float f = entitysize.width - entitysize1.width;
                this.move(MoverType.SELF, new Vector3d((double)f, 0.0D, (double)f));
            }
        }
    }

    /**
     * Gets the horizontal facing direction of this Entity.
     */
    public Direction getHorizontalFacing()
    {
        return Direction.fromAngle((double)this.rotationYaw);
    }

    /**
     * Gets the horizontal facing direction of this Entity, adjusted to take specially-treated entity types into
     * account.
     */
    public Direction getAdjustedHorizontalFacing()
    {
        return this.getHorizontalFacing();
    }

    protected HoverEvent getHoverEvent()
    {
        return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new HoverEvent.EntityHover(this.getType(), this.getUniqueID(), this.getName()));
    }

    public boolean isSpectatedByPlayer(ServerPlayerEntity player)
    {
        return true;
    }

    public AxisAlignedBB getBoundingBox()
    {
        return this.boundingBox;
    }

    /**
     * Gets the bounding box of this Entity, adjusted to take auxiliary entities into account (e.g. the tile contained
     * by a minecart, such as a command block).
     */
    public AxisAlignedBB getRenderBoundingBox()
    {
        return this.getBoundingBox();
    }

    protected AxisAlignedBB getBoundingBox(Pose pose)
    {
        EntitySize entitysize = this.getSize(pose);
        float f = entitysize.width / 2.0F;
        Vector3d vector3d = new Vector3d(this.getPosX() - (double)f, this.getPosY(), this.getPosZ() - (double)f);
        Vector3d vector3d1 = new Vector3d(this.getPosX() + (double)f, this.getPosY() + (double)entitysize.height, this.getPosZ() + (double)f);
        return new AxisAlignedBB(vector3d, vector3d1);
    }

    public void setBoundingBox(AxisAlignedBB bb)
    {
        this.boundingBox = bb;
    }

    protected float getEyeHeight(Pose poseIn, EntitySize sizeIn)
    {
        return sizeIn.height * 0.85F;
    }

    public float getEyeHeight(Pose pose)
    {
        return this.getEyeHeight(pose, this.getSize(pose));
    }

    public final float getEyeHeight()
    {
        return this.eyeHeight;
    }

    public Vector3d func_241205_ce_()
    {
        return new Vector3d(0.0D, (double)this.getEyeHeight(), (double)(this.getWidth() * 0.4F));
    }

    public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn)
    {
        return false;
    }

    /**
     * Send a chat message to the CommandSender
     */
    public void sendMessage(ITextComponent component, UUID senderUUID)
    {
    }

    /**
     * Get the world, if available. <b>{@code null} is not allowed!</b> If you are not an entity in the world, return
     * the overworld
     */
    public World getEntityWorld()
    {
        return this.world;
    }

    @Nullable

    /**
     * Get the Minecraft server instance
     */
    public MinecraftServer getServer()
    {
        return this.world.getServer();
    }

    /**
     * Applies the given player interaction to this Entity.
     */
    public ActionResultType applyPlayerInteraction(PlayerEntity player, Vector3d vec, Hand hand)
    {
        return ActionResultType.PASS;
    }

    public boolean isImmuneToExplosions()
    {
        return false;
    }

    public void applyEnchantments(LivingEntity entityLivingBaseIn, Entity entityIn)
    {
        if (entityIn instanceof LivingEntity)
        {
            EnchantmentHelper.applyThornEnchantments((LivingEntity)entityIn, entityLivingBaseIn);
        }

        EnchantmentHelper.applyArthropodEnchantments(entityLivingBaseIn, entityIn);
    }

    /**
     * Add the given player to the list of players tracking this entity. For instance, a player may track a boss in
     * order to view its associated boss bar.
     */
    public void addTrackingPlayer(ServerPlayerEntity player)
    {
    }

    /**
     * Removes the given player from the list of players tracking this entity. See {@link Entity#addTrackingPlayer} for
     * more information on tracking.
     */
    public void removeTrackingPlayer(ServerPlayerEntity player)
    {
    }

    /**
     * Transforms the entity's current yaw with the given Rotation and returns it. This does not have a side-effect.
     */
    public float getRotatedYaw(Rotation transformRotation)
    {
        float f = MathHelper.wrapDegrees(this.rotationYaw);

        switch (transformRotation)
        {
            case CLOCKWISE_180:
                return f + 180.0F;

            case COUNTERCLOCKWISE_90:
                return f + 270.0F;

            case CLOCKWISE_90:
                return f + 90.0F;

            default:
                return f;
        }
    }

    /**
     * Transforms the entity's current yaw with the given Mirror and returns it. This does not have a side-effect.
     */
    public float getMirroredYaw(Mirror transformMirror)
    {
        float f = MathHelper.wrapDegrees(this.rotationYaw);

        switch (transformMirror)
        {
            case LEFT_RIGHT:
                return -f;

            case FRONT_BACK:
                return 180.0F - f;

            default:
                return f;
        }
    }

    /**
     * Checks if players can use this entity to access operator (permission level 2) commands either directly or
     * indirectly, such as give or setblock. A similar method exists for entities at {@link
     * net.minecraft.tileentity.TileEntity#onlyOpsCanSetNbt()}.<p>For example, {@link
     * net.minecraft.entity.item.EntityMinecartCommandBlock#ignoreItemEntityData() command block minecarts} and {@link
     * net.minecraft.entity.item.EntityMinecartMobSpawner#ignoreItemEntityData() mob spawner minecarts} (spawning
     * command block minecarts or drops) are considered accessible.</p>@return true if this entity offers ways for
     * unauthorized players to use restricted commands
     */
    public boolean ignoreItemEntityData()
    {
        return false;
    }

    public boolean func_233577_ch_()
    {
        boolean flag = this.isPositionDirty;
        this.isPositionDirty = false;
        return flag;
    }

    public boolean func_233578_ci_()
    {
        boolean flag = this.isLoaded;
        this.isLoaded = false;
        return flag;
    }

    @Nullable

    /**
     * For vehicles, the first passenger is generally considered the controller and "drives" the vehicle. For example,
     * Pigs, Horses, and Boats are generally "steered" by the controlling passenger.
     */
    public Entity getControllingPassenger()
    {
        return null;
    }

    public List<Entity> getPassengers()
    {
        return (List<Entity>)(this.passengers.isEmpty() ? Collections.emptyList() : Lists.newArrayList(this.passengers));
    }

    public boolean isPassenger(Entity entityIn)
    {
        for (Entity entity : this.getPassengers())
        {
            if (entity.equals(entityIn))
            {
                return true;
            }
        }

        return false;
    }

    public boolean isPassenger(Class <? extends Entity > entityClazz)
    {
        for (Entity entity : this.getPassengers())
        {
            if (entityClazz.isAssignableFrom(entity.getClass()))
            {
                return true;
            }
        }

        return false;
    }

    public Collection<Entity> getRecursivePassengers()
    {
        Set<Entity> set = Sets.newHashSet();

        for (Entity entity : this.getPassengers())
        {
            set.add(entity);
            entity.getRecursivePassengers(false, set);
        }

        return set;
    }

    public Stream<Entity> getSelfAndPassengers()
    {
        return Stream.concat(Stream.of(this), this.passengers.stream().flatMap(Entity::getSelfAndPassengers));
    }

    public boolean isOnePlayerRiding()
    {
        Set<Entity> set = Sets.newHashSet();
        this.getRecursivePassengers(true, set);
        return set.size() == 1;
    }

    private void getRecursivePassengers(boolean playersOnly, Set<Entity> p_200604_2_)
    {
        for (Entity entity : this.getPassengers())
        {
            if (!playersOnly || ServerPlayerEntity.class.isAssignableFrom(entity.getClass()))
            {
                p_200604_2_.add(entity);
            }

            entity.getRecursivePassengers(playersOnly, p_200604_2_);
        }
    }

    public Entity getLowestRidingEntity()
    {
        Entity entity;

        for (entity = this; entity.isPassenger(); entity = entity.getRidingEntity())
        {
        }

        return entity;
    }

    public boolean isRidingSameEntity(Entity entityIn)
    {
        return this.getLowestRidingEntity() == entityIn.getLowestRidingEntity();
    }

    public boolean isRidingOrBeingRiddenBy(Entity entityIn)
    {
        for (Entity entity : this.getPassengers())
        {
            if (entity.equals(entityIn))
            {
                return true;
            }

            if (entity.isRidingOrBeingRiddenBy(entityIn))
            {
                return true;
            }
        }

        return false;
    }

    public boolean canPassengerSteer()
    {
        Entity entity = this.getControllingPassenger();

        if (entity instanceof PlayerEntity)
        {
            return ((PlayerEntity)entity).isUser();
        }
        else
        {
            return !this.world.isRemote;
        }
    }

    protected static Vector3d func_233559_a_(double p_233559_0_, double p_233559_2_, float p_233559_4_)
    {
        double d0 = (p_233559_0_ + p_233559_2_ + (double)1.0E-5F) / 2.0D;
        float f = -MathHelper.sin(p_233559_4_ * ((float)Math.PI / 180F));
        float f1 = MathHelper.cos(p_233559_4_ * ((float)Math.PI / 180F));
        float f2 = Math.max(Math.abs(f), Math.abs(f1));
        return new Vector3d((double)f * d0 / (double)f2, 0.0D, (double)f1 * d0 / (double)f2);
    }

    public Vector3d func_230268_c_(LivingEntity livingEntity)
    {
        return new Vector3d(this.getPosX(), this.getBoundingBox().maxY, this.getPosZ());
    }

    @Nullable

    /**
     * Get entity this is riding
     */
    public Entity getRidingEntity()
    {
        return this.ridingEntity;
    }

    public PushReaction getPushReaction()
    {
        return PushReaction.NORMAL;
    }

    public SoundCategory getSoundCategory()
    {
        return SoundCategory.NEUTRAL;
    }

    protected int getFireImmuneTicks()
    {
        return 1;
    }

    public CommandSource getCommandSource()
    {
        return new CommandSource(this, this.getPositionVec(), this.getPitchYaw(), this.world instanceof ServerWorld ? (ServerWorld)this.world : null, this.getPermissionLevel(), this.getName().getString(), this.getDisplayName(), this.world.getServer(), this);
    }

    protected int getPermissionLevel()
    {
        return 0;
    }

    public boolean hasPermissionLevel(int level)
    {
        return this.getPermissionLevel() >= level;
    }

    public boolean shouldReceiveFeedback()
    {
        return this.world.getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK);
    }

    public boolean shouldReceiveErrors()
    {
        return true;
    }

    public boolean allowLogging()
    {
        return true;
    }

    public void lookAt(EntityAnchorArgument.Type anchor, Vector3d target)
    {
        Vector3d vector3d = anchor.apply(this);
        double d0 = target.x - vector3d.x;
        double d1 = target.y - vector3d.y;
        double d2 = target.z - vector3d.z;
        double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
        this.rotationPitch = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(d1, d3) * (double)(180F / (float)Math.PI))));
        this.rotationYaw = MathHelper.wrapDegrees((float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F);
        this.setRotationYawHead(this.rotationYaw);
        this.prevRotationPitch = this.rotationPitch;
        this.prevRotationYaw = this.rotationYaw;
    }

    public boolean handleFluidAcceleration(ITag<Fluid> fluidTag, double p_210500_2_)
    {
        AxisAlignedBB axisalignedbb = this.getBoundingBox().shrink(0.001D);
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.ceil(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.minY);
        int l = MathHelper.ceil(axisalignedbb.maxY);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.ceil(axisalignedbb.maxZ);

        if (!this.world.isAreaLoaded(i, k, i1, j, l, j1))
        {
            return false;
        }
        else
        {
            double d0 = 0.0D;
            boolean flag = this.isPushedByWater();
            boolean flag1 = false;
            Vector3d vector3d = Vector3d.ZERO;
            int k1 = 0;
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

            for (int l1 = i; l1 < j; ++l1)
            {
                for (int i2 = k; i2 < l; ++i2)
                {
                    for (int j2 = i1; j2 < j1; ++j2)
                    {
                        blockpos$mutable.setPos(l1, i2, j2);
                        FluidState fluidstate = this.world.getFluidState(blockpos$mutable);

                        if (fluidstate.isTagged(fluidTag))
                        {
                            double d1 = (double)((float)i2 + fluidstate.getActualHeight(this.world, blockpos$mutable));

                            if (d1 >= axisalignedbb.minY)
                            {
                                flag1 = true;
                                d0 = Math.max(d1 - axisalignedbb.minY, d0);

                                if (flag)
                                {
                                    Vector3d vector3d1 = fluidstate.getFlow(this.world, blockpos$mutable);

                                    if (d0 < 0.4D)
                                    {
                                        vector3d1 = vector3d1.scale(d0);
                                    }

                                    vector3d = vector3d.add(vector3d1);
                                    ++k1;
                                }
                            }
                        }
                    }
                }
            }

            if (vector3d.length() > 0.0D)
            {
                if (k1 > 0)
                {
                    vector3d = vector3d.scale(1.0D / (double)k1);
                }

                if (!(this instanceof PlayerEntity))
                {
                    vector3d = vector3d.normalize();
                }

                Vector3d vector3d2 = this.getMotion();
                vector3d = vector3d.scale(p_210500_2_ * 1.0D);
                double d2 = 0.003D;

                if (Math.abs(vector3d2.x) < 0.003D && Math.abs(vector3d2.z) < 0.003D && vector3d.length() < 0.0045000000000000005D)
                {
                    vector3d = vector3d.normalize().scale(0.0045000000000000005D);
                }

                this.setMotion(this.getMotion().add(vector3d));
            }

            this.eyesFluidLevel.put(fluidTag, d0);
            return flag1;
        }
    }

    public double func_233571_b_(ITag<Fluid> p_233571_1_)
    {
        return this.eyesFluidLevel.getDouble(p_233571_1_);
    }

    public double func_233579_cu_()
    {
        return (double)this.getEyeHeight() < 0.4D ? 0.0D : 0.4D;
    }

    public final float getWidth()
    {
        return this.size.width;
    }

    public final float getHeight()
    {
        return this.size.height;
    }

    public abstract IPacket<?> createSpawnPacket();

    public EntitySize getSize(Pose poseIn)
    {
        return this.type.getSize();
    }

    public Vector3d getPositionVec()
    {
        return this.positionVec;
    }

    public BlockPos getPosition()
    {
        return this.position;
    }

    public Vector3d getMotion()
    {
        return this.motion;
    }

    public void setMotion(Vector3d motionIn)
    {
        this.motion = motionIn;
    }

    public void setMotion(double x, double y, double z)
    {
        this.setMotion(new Vector3d(x, y, z));
    }

    public final double getPosX()
    {
        return this.positionVec.x;
    }

    public double getPosXWidth(double p_226275_1_)
    {
        return this.positionVec.x + (double)this.getWidth() * p_226275_1_;
    }

    public double getPosXRandom(double p_226282_1_)
    {
        return this.getPosXWidth((2.0D * this.rand.nextDouble() - 1.0D) * p_226282_1_);
    }

    public final double getPosY()
    {
        return this.positionVec.y;
    }

    public double getPosYHeight(double p_226283_1_)
    {
        return this.positionVec.y + (double)this.getHeight() * p_226283_1_;
    }

    public double getPosYRandom()
    {
        return this.getPosYHeight(this.rand.nextDouble());
    }

    public double getPosYEye()
    {
        return this.positionVec.y + (double)this.eyeHeight;
    }

    public final double getPosZ()
    {
        return this.positionVec.z;
    }

    public double getPosZWidth(double p_226285_1_)
    {
        return this.positionVec.z + (double)this.getWidth() * p_226285_1_;
    }

    public double getPosZRandom(double p_226287_1_)
    {
        return this.getPosZWidth((2.0D * this.rand.nextDouble() - 1.0D) * p_226287_1_);
    }

    /**
     * Directly updates the {@link #posX}, {@link posY}, and {@link posZ} fields, without performing any collision
     * checks, updating the bounding box position, or sending any packets. In general, this is not what you want and
     * {@link #setPosition} is better, as that handles the bounding box.
     */
    public void setRawPosition(double x, double y, double z)
    {
        if (this.positionVec.x != x || this.positionVec.y != y || this.positionVec.z != z)
        {
            this.positionVec = new Vector3d(x, y, z);
            int i = MathHelper.floor(x);
            int j = MathHelper.floor(y);
            int k = MathHelper.floor(z);

            if (i != this.position.getX() || j != this.position.getY() || k != this.position.getZ())
            {
                this.position = new BlockPos(i, j, k);
            }

            this.isLoaded = true;
        }
    }

    /**
     * Makes the entity despawn if requirements are reached
     */
    public void checkDespawn()
    {
    }

    public Vector3d getLeashPosition(float partialTicks)
    {
        return this.func_242282_l(partialTicks).add(0.0D, (double)this.eyeHeight * 0.7D, 0.0D);
    }

    @FunctionalInterface
    public interface IMoveCallback
    {
        void accept(Entity p_accept_1_, double p_accept_2_, double p_accept_4_, double p_accept_6_);
    }
}
