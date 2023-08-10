package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Direction;
import net.minecraft.util.HandSide;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class ItemModelsProperties
{
    private static final Map<ResourceLocation, IItemPropertyGetter> GLOBAL_PROPERTY_MAP = Maps.newHashMap();
    private static final ResourceLocation DAMAGED = new ResourceLocation("damaged");
    private static final ResourceLocation DAMAGE = new ResourceLocation("damage");
    private static final IItemPropertyGetter field_239413_d_ = (p_239434_0_, p_239434_1_, p_239434_2_) ->
    {
        return p_239434_0_.isDamaged() ? 1.0F : 0.0F;
    };
    private static final IItemPropertyGetter field_239414_e_ = (p_239433_0_, p_239433_1_, p_239433_2_) ->
    {
        return MathHelper.clamp((float)p_239433_0_.getDamage() / (float)p_239433_0_.getMaxDamage(), 0.0F, 1.0F);
    };
    private static final Map<Item, Map<ResourceLocation, IItemPropertyGetter>> ITEM_PROPERTY_MAP = Maps.newHashMap();

    private static IItemPropertyGetter registerGlobalProperty(ResourceLocation id, IItemPropertyGetter propertyGetter)
    {
        GLOBAL_PROPERTY_MAP.put(id, propertyGetter);
        return propertyGetter;
    }

    private static void registerProperty(Item item, ResourceLocation p_239418_1_, IItemPropertyGetter p_239418_2_)
    {
        ITEM_PROPERTY_MAP.computeIfAbsent(item, (p_239416_0_) ->
        {
            return Maps.newHashMap();
        }).put(p_239418_1_, p_239418_2_);
    }

    @Nullable
    public static IItemPropertyGetter func_239417_a_(Item p_239417_0_, ResourceLocation p_239417_1_)
    {
        if (p_239417_0_.getMaxDamage() > 0)
        {
            if (DAMAGE.equals(p_239417_1_))
            {
                return field_239414_e_;
            }

            if (DAMAGED.equals(p_239417_1_))
            {
                return field_239413_d_;
            }
        }

        IItemPropertyGetter iitempropertygetter = GLOBAL_PROPERTY_MAP.get(p_239417_1_);

        if (iitempropertygetter != null)
        {
            return iitempropertygetter;
        }
        else
        {
            Map<ResourceLocation, IItemPropertyGetter> map = ITEM_PROPERTY_MAP.get(p_239417_0_);
            return map == null ? null : map.get(p_239417_1_);
        }
    }

    static
    {
        registerGlobalProperty(new ResourceLocation("lefthanded"), (p_239432_0_, p_239432_1_, p_239432_2_) ->
        {
            return p_239432_2_ != null && p_239432_2_.getPrimaryHand() != HandSide.RIGHT ? 1.0F : 0.0F;
        });
        registerGlobalProperty(new ResourceLocation("cooldown"), (p_239431_0_, p_239431_1_, p_239431_2_) ->
        {
            return p_239431_2_ instanceof PlayerEntity ? ((PlayerEntity)p_239431_2_).getCooldownTracker().getCooldown(p_239431_0_.getItem(), 0.0F) : 0.0F;
        });
        registerGlobalProperty(new ResourceLocation("custom_model_data"), (p_239430_0_, p_239430_1_, p_239430_2_) ->
        {
            return p_239430_0_.hasTag() ? (float)p_239430_0_.getTag().getInt("CustomModelData") : 0.0F;
        });
        registerProperty(Items.BOW, new ResourceLocation("pull"), (p_239429_0_, p_239429_1_, p_239429_2_) ->
        {
            if (p_239429_2_ == null)
            {
                return 0.0F;
            }
            else {
                return p_239429_2_.getActiveItemStack() != p_239429_0_ ? 0.0F : (float)(p_239429_0_.getUseDuration() - p_239429_2_.getItemInUseCount()) / 20.0F;
            }
        });
        registerProperty(Items.BOW, new ResourceLocation("pulling"), (p_239428_0_, p_239428_1_, p_239428_2_) ->
        {
            return p_239428_2_ != null && p_239428_2_.isHandActive() && p_239428_2_.getActiveItemStack() == p_239428_0_ ? 1.0F : 0.0F;
        });
        registerProperty(Items.CLOCK, new ResourceLocation("time"), new IItemPropertyGetter()
        {
            private double field_239435_a_;
            private double field_239436_b_;
            private long field_239437_c_;
            public float call(ItemStack p_call_1_, @Nullable ClientWorld p_call_2_, @Nullable LivingEntity p_call_3_)
            {
                Entity entity = (Entity)(p_call_3_ != null ? p_call_3_ : p_call_1_.getAttachedEntity());

                if (entity == null)
                {
                    return 0.0F;
                }
                else
                {
                    if (p_call_2_ == null && entity.world instanceof ClientWorld)
                    {
                        p_call_2_ = (ClientWorld)entity.world;
                    }

                    if (p_call_2_ == null)
                    {
                        return 0.0F;
                    }
                    else
                    {
                        double d0;

                        if (p_call_2_.getDimensionType().isNatural())
                        {
                            d0 = (double)p_call_2_.func_242415_f(1.0F);
                        }
                        else
                        {
                            d0 = Math.random();
                        }

                        d0 = this.func_239438_a_(p_call_2_, d0);
                        return (float)d0;
                    }
                }
            }
            private double func_239438_a_(World p_239438_1_, double p_239438_2_)
            {
                if (p_239438_1_.getGameTime() != this.field_239437_c_)
                {
                    this.field_239437_c_ = p_239438_1_.getGameTime();
                    double d0 = p_239438_2_ - this.field_239435_a_;
                    d0 = MathHelper.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
                    this.field_239436_b_ += d0 * 0.1D;
                    this.field_239436_b_ *= 0.9D;
                    this.field_239435_a_ = MathHelper.positiveModulo(this.field_239435_a_ + this.field_239436_b_, 1.0D);
                }

                return this.field_239435_a_;
            }
        });
        registerProperty(Items.COMPASS, new ResourceLocation("angle"), new IItemPropertyGetter()
        {
            private final ItemModelsProperties.Angle field_239439_a_ = new ItemModelsProperties.Angle();
            private final ItemModelsProperties.Angle field_239440_b_ = new ItemModelsProperties.Angle();
            public float call(ItemStack p_call_1_, @Nullable ClientWorld p_call_2_, @Nullable LivingEntity p_call_3_)
            {
                Entity entity = (Entity)(p_call_3_ != null ? p_call_3_ : p_call_1_.getAttachedEntity());

                if (entity == null)
                {
                    return 0.0F;
                }
                else
                {
                    if (p_call_2_ == null && entity.world instanceof ClientWorld)
                    {
                        p_call_2_ = (ClientWorld)entity.world;
                    }

                    BlockPos blockpos = CompassItem.func_234670_d_(p_call_1_) ? this.func_239442_a_(p_call_2_, p_call_1_.getOrCreateTag()) : this.func_239444_a_(p_call_2_);
                    long i = p_call_2_.getGameTime();

                    if (blockpos != null && !(entity.getPositionVec().squareDistanceTo((double)blockpos.getX() + 0.5D, entity.getPositionVec().getY(), (double)blockpos.getZ() + 0.5D) < (double)1.0E-5F))
                    {
                        boolean flag = p_call_3_ instanceof PlayerEntity && ((PlayerEntity)p_call_3_).isUser();
                        double d1 = 0.0D;

                        if (flag)
                        {
                            d1 = (double)p_call_3_.rotationYaw;
                        }
                        else if (entity instanceof ItemFrameEntity)
                        {
                            d1 = this.func_239441_a_((ItemFrameEntity)entity);
                        }
                        else if (entity instanceof ItemEntity)
                        {
                            d1 = (double)(180.0F - ((ItemEntity)entity).getItemHover(0.5F) / ((float)Math.PI * 2F) * 360.0F);
                        }
                        else if (p_call_3_ != null)
                        {
                            d1 = (double)p_call_3_.renderYawOffset;
                        }

                        d1 = MathHelper.positiveModulo(d1 / 360.0D, 1.0D);
                        double d2 = this.func_239443_a_(Vector3d.copyCentered(blockpos), entity) / (double)((float)Math.PI * 2F);
                        double d3;

                        if (flag)
                        {
                            if (this.field_239439_a_.func_239448_a_(i))
                            {
                                this.field_239439_a_.func_239449_a_(i, 0.5D - (d1 - 0.25D));
                            }

                            d3 = d2 + this.field_239439_a_.field_239445_a_;
                        }
                        else
                        {
                            d3 = 0.5D - (d1 - 0.25D - d2);
                        }

                        return MathHelper.positiveModulo((float)d3, 1.0F);
                    }
                    else
                    {
                        if (this.field_239440_b_.func_239448_a_(i))
                        {
                            this.field_239440_b_.func_239449_a_(i, Math.random());
                        }

                        double d0 = this.field_239440_b_.field_239445_a_ + (double)((float)p_call_1_.hashCode() / 2.14748365E9F);
                        return MathHelper.positiveModulo((float)d0, 1.0F);
                    }
                }
            }
            @Nullable
            private BlockPos func_239444_a_(ClientWorld p_239444_1_)
            {
                return p_239444_1_.getDimensionType().isNatural() ? p_239444_1_.func_239140_u_() : null;
            }
            @Nullable
            private BlockPos func_239442_a_(World p_239442_1_, CompoundNBT p_239442_2_)
            {
                boolean flag = p_239442_2_.contains("LodestonePos");
                boolean flag1 = p_239442_2_.contains("LodestoneDimension");

                if (flag && flag1)
                {
                    Optional<RegistryKey<World>> optional = CompassItem.func_234667_a_(p_239442_2_);

                    if (optional.isPresent() && p_239442_1_.getDimensionKey() == optional.get())
                    {
                        return NBTUtil.readBlockPos(p_239442_2_.getCompound("LodestonePos"));
                    }
                }

                return null;
            }
            private double func_239441_a_(ItemFrameEntity p_239441_1_)
            {
                Direction direction = p_239441_1_.getHorizontalFacing();
                int i = direction.getAxis().isVertical() ? 90 * direction.getAxisDirection().getOffset() : 0;
                return (double)MathHelper.wrapDegrees(180 + direction.getHorizontalIndex() * 90 + p_239441_1_.getRotation() * 45 + i);
            }
            private double func_239443_a_(Vector3d p_239443_1_, Entity p_239443_2_)
            {
                return Math.atan2(p_239443_1_.getZ() - p_239443_2_.getPosZ(), p_239443_1_.getX() - p_239443_2_.getPosX());
            }
        });
        registerProperty(Items.CROSSBOW, new ResourceLocation("pull"), (p_239427_0_, p_239427_1_, p_239427_2_) ->
        {
            if (p_239427_2_ == null)
            {
                return 0.0F;
            }
            else {
                return CrossbowItem.isCharged(p_239427_0_) ? 0.0F : (float)(p_239427_0_.getUseDuration() - p_239427_2_.getItemInUseCount()) / (float)CrossbowItem.getChargeTime(p_239427_0_);
            }
        });
        registerProperty(Items.CROSSBOW, new ResourceLocation("pulling"), (p_239426_0_, p_239426_1_, p_239426_2_) ->
        {
            return p_239426_2_ != null && p_239426_2_.isHandActive() && p_239426_2_.getActiveItemStack() == p_239426_0_ && !CrossbowItem.isCharged(p_239426_0_) ? 1.0F : 0.0F;
        });
        registerProperty(Items.CROSSBOW, new ResourceLocation("charged"), (p_239425_0_, p_239425_1_, p_239425_2_) ->
        {
            return p_239425_2_ != null && CrossbowItem.isCharged(p_239425_0_) ? 1.0F : 0.0F;
        });
        registerProperty(Items.CROSSBOW, new ResourceLocation("firework"), (p_239424_0_, p_239424_1_, p_239424_2_) ->
        {
            return p_239424_2_ != null && CrossbowItem.isCharged(p_239424_0_) && CrossbowItem.hasChargedProjectile(p_239424_0_, Items.FIREWORK_ROCKET) ? 1.0F : 0.0F;
        });
        registerProperty(Items.ELYTRA, new ResourceLocation("broken"), (p_239423_0_, p_239423_1_, p_239423_2_) ->
        {
            return ElytraItem.isUsable(p_239423_0_) ? 0.0F : 1.0F;
        });
        registerProperty(Items.FISHING_ROD, new ResourceLocation("cast"), (p_239422_0_, p_239422_1_, p_239422_2_) ->
        {
            if (p_239422_2_ == null)
            {
                return 0.0F;
            }
            else {
                boolean flag = p_239422_2_.getHeldItemMainhand() == p_239422_0_;
                boolean flag1 = p_239422_2_.getHeldItemOffhand() == p_239422_0_;

                if (p_239422_2_.getHeldItemMainhand().getItem() instanceof FishingRodItem)
                {
                    flag1 = false;
                }

                return (flag || flag1) && p_239422_2_ instanceof PlayerEntity && ((PlayerEntity)p_239422_2_).fishingBobber != null ? 1.0F : 0.0F;
            }
        });
        registerProperty(Items.SHIELD, new ResourceLocation("blocking"), (p_239421_0_, p_239421_1_, p_239421_2_) ->
        {
            return p_239421_2_ != null && p_239421_2_.isHandActive() && p_239421_2_.getActiveItemStack() == p_239421_0_ ? 1.0F : 0.0F;
        });
        registerProperty(Items.TRIDENT, new ResourceLocation("throwing"), (p_239419_0_, p_239419_1_, p_239419_2_) ->
        {
            return p_239419_2_ != null && p_239419_2_.isHandActive() && p_239419_2_.getActiveItemStack() == p_239419_0_ ? 1.0F : 0.0F;
        });
    }

    static class Angle
    {
        private double field_239445_a_;
        private double field_239446_b_;
        private long field_239447_c_;

        private Angle()
        {
        }

        private boolean func_239448_a_(long p_239448_1_)
        {
            return this.field_239447_c_ != p_239448_1_;
        }

        private void func_239449_a_(long p_239449_1_, double p_239449_3_)
        {
            this.field_239447_c_ = p_239449_1_;
            double d0 = p_239449_3_ - this.field_239445_a_;
            d0 = MathHelper.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
            this.field_239446_b_ += d0 * 0.1D;
            this.field_239446_b_ *= 0.8D;
            this.field_239445_a_ = MathHelper.positiveModulo(this.field_239445_a_ + this.field_239446_b_, 1.0D);
        }
    }
}
