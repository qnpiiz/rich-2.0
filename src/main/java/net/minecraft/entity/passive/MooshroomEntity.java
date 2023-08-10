package net.minecraft.entity.passive;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IShearable;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.commons.lang3.tuple.Pair;

public class MooshroomEntity extends CowEntity implements IShearable
{
    private static final DataParameter<String> MOOSHROOM_TYPE = EntityDataManager.createKey(MooshroomEntity.class, DataSerializers.STRING);
    private Effect hasStewEffect;
    private int effectDuration;

    /** Stores the UUID of the most recent lightning bolt to strike */
    private UUID lightningUUID;

    public MooshroomEntity(EntityType <? extends MooshroomEntity > type, World worldIn)
    {
        super(type, worldIn);
    }

    public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn)
    {
        return worldIn.getBlockState(pos.down()).isIn(Blocks.MYCELIUM) ? 10.0F : worldIn.getBrightness(pos) - 0.5F;
    }

    public static boolean func_223318_c(EntityType<MooshroomEntity> p_223318_0_, IWorld p_223318_1_, SpawnReason p_223318_2_, BlockPos p_223318_3_, Random p_223318_4_)
    {
        return p_223318_1_.getBlockState(p_223318_3_.down()).isIn(Blocks.MYCELIUM) && p_223318_1_.getLightSubtracted(p_223318_3_, 0) > 8;
    }

    public void func_241841_a(ServerWorld p_241841_1_, LightningBoltEntity p_241841_2_)
    {
        UUID uuid = p_241841_2_.getUniqueID();

        if (!uuid.equals(this.lightningUUID))
        {
            this.setMooshroomType(this.getMooshroomType() == MooshroomEntity.Type.RED ? MooshroomEntity.Type.BROWN : MooshroomEntity.Type.RED);
            this.lightningUUID = uuid;
            this.playSound(SoundEvents.ENTITY_MOOSHROOM_CONVERT, 2.0F, 1.0F);
        }
    }

    protected void registerData()
    {
        super.registerData();
        this.dataManager.register(MOOSHROOM_TYPE, MooshroomEntity.Type.RED.name);
    }

    public ActionResultType func_230254_b_(PlayerEntity p_230254_1_, Hand p_230254_2_)
    {
        ItemStack itemstack = p_230254_1_.getHeldItem(p_230254_2_);

        if (itemstack.getItem() == Items.BOWL && !this.isChild())
        {
            boolean flag = false;
            ItemStack itemstack1;

            if (this.hasStewEffect != null)
            {
                flag = true;
                itemstack1 = new ItemStack(Items.SUSPICIOUS_STEW);
                SuspiciousStewItem.addEffect(itemstack1, this.hasStewEffect, this.effectDuration);
                this.hasStewEffect = null;
                this.effectDuration = 0;
            }
            else
            {
                itemstack1 = new ItemStack(Items.MUSHROOM_STEW);
            }

            ItemStack itemstack2 = DrinkHelper.fill(itemstack, p_230254_1_, itemstack1, false);
            p_230254_1_.setHeldItem(p_230254_2_, itemstack2);
            SoundEvent soundevent;

            if (flag)
            {
                soundevent = SoundEvents.ENTITY_MOOSHROOM_SUSPICIOUS_MILK;
            }
            else
            {
                soundevent = SoundEvents.ENTITY_MOOSHROOM_MILK;
            }

            this.playSound(soundevent, 1.0F, 1.0F);
            return ActionResultType.func_233537_a_(this.world.isRemote);
        }
        else if (itemstack.getItem() == Items.SHEARS && this.isShearable())
        {
            this.shear(SoundCategory.PLAYERS);

            if (!this.world.isRemote)
            {
                itemstack.damageItem(1, p_230254_1_, (p_213442_1_) ->
                {
                    p_213442_1_.sendBreakAnimation(p_230254_2_);
                });
            }

            return ActionResultType.func_233537_a_(this.world.isRemote);
        }
        else if (this.getMooshroomType() == MooshroomEntity.Type.BROWN && itemstack.getItem().isIn(ItemTags.SMALL_FLOWERS))
        {
            if (this.hasStewEffect != null)
            {
                for (int i = 0; i < 2; ++i)
                {
                    this.world.addParticle(ParticleTypes.SMOKE, this.getPosX() + this.rand.nextDouble() / 2.0D, this.getPosYHeight(0.5D), this.getPosZ() + this.rand.nextDouble() / 2.0D, 0.0D, this.rand.nextDouble() / 5.0D, 0.0D);
                }
            }
            else
            {
                Optional<Pair<Effect, Integer>> optional = this.getStewEffect(itemstack);

                if (!optional.isPresent())
                {
                    return ActionResultType.PASS;
                }

                Pair<Effect, Integer> pair = optional.get();

                if (!p_230254_1_.abilities.isCreativeMode)
                {
                    itemstack.shrink(1);
                }

                for (int j = 0; j < 4; ++j)
                {
                    this.world.addParticle(ParticleTypes.EFFECT, this.getPosX() + this.rand.nextDouble() / 2.0D, this.getPosYHeight(0.5D), this.getPosZ() + this.rand.nextDouble() / 2.0D, 0.0D, this.rand.nextDouble() / 5.0D, 0.0D);
                }

                this.hasStewEffect = pair.getLeft();
                this.effectDuration = pair.getRight();
                this.playSound(SoundEvents.ENTITY_MOOSHROOM_EAT, 2.0F, 1.0F);
            }

            return ActionResultType.func_233537_a_(this.world.isRemote);
        }
        else
        {
            return super.func_230254_b_(p_230254_1_, p_230254_2_);
        }
    }

    public void shear(SoundCategory category)
    {
        this.world.playMovingSound((PlayerEntity)null, this, SoundEvents.ENTITY_MOOSHROOM_SHEAR, category, 1.0F, 1.0F);

        if (!this.world.isRemote())
        {
            ((ServerWorld)this.world).spawnParticle(ParticleTypes.EXPLOSION, this.getPosX(), this.getPosYHeight(0.5D), this.getPosZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            this.remove();
            CowEntity cowentity = EntityType.COW.create(this.world);
            cowentity.setLocationAndAngles(this.getPosX(), this.getPosY(), this.getPosZ(), this.rotationYaw, this.rotationPitch);
            cowentity.setHealth(this.getHealth());
            cowentity.renderYawOffset = this.renderYawOffset;

            if (this.hasCustomName())
            {
                cowentity.setCustomName(this.getCustomName());
                cowentity.setCustomNameVisible(this.isCustomNameVisible());
            }

            if (this.isNoDespawnRequired())
            {
                cowentity.enablePersistence();
            }

            cowentity.setInvulnerable(this.isInvulnerable());
            this.world.addEntity(cowentity);

            for (int i = 0; i < 5; ++i)
            {
                this.world.addEntity(new ItemEntity(this.world, this.getPosX(), this.getPosYHeight(1.0D), this.getPosZ(), new ItemStack(this.getMooshroomType().renderState.getBlock())));
            }
        }
    }

    public boolean isShearable()
    {
        return this.isAlive() && !this.isChild();
    }

    public void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        compound.putString("Type", this.getMooshroomType().name);

        if (this.hasStewEffect != null)
        {
            compound.putByte("EffectId", (byte)Effect.getId(this.hasStewEffect));
            compound.putInt("EffectDuration", this.effectDuration);
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
        this.setMooshroomType(MooshroomEntity.Type.getTypeByName(compound.getString("Type")));

        if (compound.contains("EffectId", 1))
        {
            this.hasStewEffect = Effect.get(compound.getByte("EffectId"));
        }

        if (compound.contains("EffectDuration", 3))
        {
            this.effectDuration = compound.getInt("EffectDuration");
        }
    }

    private Optional<Pair<Effect, Integer>> getStewEffect(ItemStack p_213443_1_)
    {
        Item item = p_213443_1_.getItem();

        if (item instanceof BlockItem)
        {
            Block block = ((BlockItem)item).getBlock();

            if (block instanceof FlowerBlock)
            {
                FlowerBlock flowerblock = (FlowerBlock)block;
                return Optional.of(Pair.of(flowerblock.getStewEffect(), flowerblock.getStewEffectDuration()));
            }
        }

        return Optional.empty();
    }

    private void setMooshroomType(MooshroomEntity.Type typeIn)
    {
        this.dataManager.set(MOOSHROOM_TYPE, typeIn.name);
    }

    public MooshroomEntity.Type getMooshroomType()
    {
        return MooshroomEntity.Type.getTypeByName(this.dataManager.get(MOOSHROOM_TYPE));
    }

    public MooshroomEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_)
    {
        MooshroomEntity mooshroomentity = EntityType.MOOSHROOM.create(p_241840_1_);
        mooshroomentity.setMooshroomType(this.func_213445_a((MooshroomEntity)p_241840_2_));
        return mooshroomentity;
    }

    private MooshroomEntity.Type func_213445_a(MooshroomEntity p_213445_1_)
    {
        MooshroomEntity.Type mooshroomentity$type = this.getMooshroomType();
        MooshroomEntity.Type mooshroomentity$type1 = p_213445_1_.getMooshroomType();
        MooshroomEntity.Type mooshroomentity$type2;

        if (mooshroomentity$type == mooshroomentity$type1 && this.rand.nextInt(1024) == 0)
        {
            mooshroomentity$type2 = mooshroomentity$type == MooshroomEntity.Type.BROWN ? MooshroomEntity.Type.RED : MooshroomEntity.Type.BROWN;
        }
        else
        {
            mooshroomentity$type2 = this.rand.nextBoolean() ? mooshroomentity$type : mooshroomentity$type1;
        }

        return mooshroomentity$type2;
    }

    public static enum Type
    {
        RED("red", Blocks.RED_MUSHROOM.getDefaultState()),
        BROWN("brown", Blocks.BROWN_MUSHROOM.getDefaultState());

        private final String name;
        private final BlockState renderState;

        private Type(String nameIn, BlockState renderStateIn)
        {
            this.name = nameIn;
            this.renderState = renderStateIn;
        }

        public BlockState getRenderState()
        {
            return this.renderState;
        }

        private static MooshroomEntity.Type getTypeByName(String nameIn)
        {
            for (MooshroomEntity.Type mooshroomentity$type : values())
            {
                if (mooshroomentity$type.name.equals(nameIn))
                {
                    return mooshroomentity$type;
                }
            }

            return RED;
        }
    }
}
