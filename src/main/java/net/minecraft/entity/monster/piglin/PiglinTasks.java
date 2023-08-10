package net.minecraft.entity.monster.piglin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.task.AttackStrafingTask;
import net.minecraft.entity.ai.brain.task.AttackTargetTask;
import net.minecraft.entity.ai.brain.task.DummyTask;
import net.minecraft.entity.ai.brain.task.EndAttackTask;
import net.minecraft.entity.ai.brain.task.FindInteractionAndLookTargetTask;
import net.minecraft.entity.ai.brain.task.FindNewAttackTargetTask;
import net.minecraft.entity.ai.brain.task.FirstShuffledTask;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.GetAngryTask;
import net.minecraft.entity.ai.brain.task.HuntCelebrationTask;
import net.minecraft.entity.ai.brain.task.InteractWithDoorTask;
import net.minecraft.entity.ai.brain.task.InteractWithEntityTask;
import net.minecraft.entity.ai.brain.task.LookAtEntityTask;
import net.minecraft.entity.ai.brain.task.LookTask;
import net.minecraft.entity.ai.brain.task.MoveToTargetTask;
import net.minecraft.entity.ai.brain.task.PickupWantedItemTask;
import net.minecraft.entity.ai.brain.task.PiglinIdleActivityTask;
import net.minecraft.entity.ai.brain.task.PredicateTask;
import net.minecraft.entity.ai.brain.task.RideEntityTask;
import net.minecraft.entity.ai.brain.task.RunAwayTask;
import net.minecraft.entity.ai.brain.task.RunSometimesTask;
import net.minecraft.entity.ai.brain.task.ShootTargetTask;
import net.minecraft.entity.ai.brain.task.StopRidingEntityTask;
import net.minecraft.entity.ai.brain.task.SupplementedTask;
import net.minecraft.entity.ai.brain.task.WalkRandomlyTask;
import net.minecraft.entity.ai.brain.task.WalkToTargetTask;
import net.minecraft.entity.ai.brain.task.WalkTowardsLookTargetTask;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.HoglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;

public class PiglinTasks
{
    public static final Item field_234444_a_ = Items.GOLD_INGOT;
    private static final RangedInteger field_234445_b_ = TickRangeConverter.convertRange(30, 120);
    private static final RangedInteger field_234446_c_ = TickRangeConverter.convertRange(10, 40);
    private static final RangedInteger field_234447_d_ = TickRangeConverter.convertRange(10, 30);
    private static final RangedInteger field_234448_e_ = TickRangeConverter.convertRange(5, 20);
    private static final RangedInteger field_234449_f_ = TickRangeConverter.convertRange(5, 7);
    private static final RangedInteger field_241418_g_ = TickRangeConverter.convertRange(5, 7);
    private static final Set<Item> field_234450_g_ = ImmutableSet.of(Items.PORKCHOP, Items.COOKED_PORKCHOP);

    protected static Brain<?> func_234469_a_(PiglinEntity p_234469_0_, Brain<PiglinEntity> p_234469_1_)
    {
        func_234464_a_(p_234469_1_);
        func_234485_b_(p_234469_1_);
        func_234502_d_(p_234469_1_);
        func_234488_b_(p_234469_0_, p_234469_1_);
        func_234495_c_(p_234469_1_);
        func_234507_e_(p_234469_1_);
        func_234511_f_(p_234469_1_);
        p_234469_1_.setDefaultActivities(ImmutableSet.of(Activity.CORE));
        p_234469_1_.setFallbackActivity(Activity.IDLE);
        p_234469_1_.switchToFallbackActivity();
        return p_234469_1_;
    }

    protected static void func_234466_a_(PiglinEntity p_234466_0_)
    {
        int i = field_234445_b_.getRandomWithinRange(p_234466_0_.world.rand);
        p_234466_0_.getBrain().replaceMemory(MemoryModuleType.HUNTED_RECENTLY, true, (long)i);
    }

    private static void func_234464_a_(Brain<PiglinEntity> p_234464_0_)
    {
        p_234464_0_.registerActivity(Activity.CORE, 0, ImmutableList. < net.minecraft.entity.ai.brain.task.Task <? super PiglinEntity >> of(new LookTask(45, 90), new WalkToTargetTask(), new InteractWithDoorTask(), func_241428_d_(), func_234500_d_(), new StartAdmiringItemTask(), new AdmireItemTask(120), new EndAttackTask(300, PiglinTasks::func_234461_a_), new GetAngryTask()));
    }

    private static void func_234485_b_(Brain<PiglinEntity> p_234485_0_)
    {
        p_234485_0_.registerActivity(Activity.IDLE, 10, ImmutableList.of(new LookAtEntityTask(PiglinTasks::func_234482_b_, 14.0F), new ForgetAttackTargetTask<>(AbstractPiglinEntity::func_242337_eM, PiglinTasks::func_234526_m_), new SupplementedTask<>(PiglinEntity::func_234422_eK_, new StartHuntTask<>()), func_234493_c_(), func_234505_e_(), func_234458_a_(), func_234481_b_(), new FindInteractionAndLookTargetTask(EntityType.PLAYER, 4)));
    }

    private static void func_234488_b_(PiglinEntity p_234488_0_, Brain<PiglinEntity> p_234488_1_)
    {
        p_234488_1_.registerActivity(Activity.FIGHT, 10, ImmutableList. < net.minecraft.entity.ai.brain.task.Task <? super PiglinEntity >> of(new FindNewAttackTargetTask<>((p_234523_1_) ->
        {
            return !func_234504_d_(p_234488_0_, p_234523_1_);
        }), new SupplementedTask<>(PiglinTasks::func_234494_c_, new AttackStrafingTask<>(5, 0.75F)), new MoveToTargetTask(1.0F), new AttackTargetTask(20), new ShootTargetTask(), new FinishedHuntTask(), new PredicateTask<>(PiglinTasks::func_234525_l_, MemoryModuleType.ATTACK_TARGET)), MemoryModuleType.ATTACK_TARGET);
    }

    private static void func_234495_c_(Brain<PiglinEntity> p_234495_0_)
    {
        p_234495_0_.registerActivity(Activity.CELEBRATE, 10, ImmutableList. < net.minecraft.entity.ai.brain.task.Task <? super PiglinEntity >> of(func_234493_c_(), new LookAtEntityTask(PiglinTasks::func_234482_b_, 14.0F), new ForgetAttackTargetTask<PiglinEntity>(AbstractPiglinEntity::func_242337_eM, PiglinTasks::func_234526_m_), new SupplementedTask<PiglinEntity>((p_234457_0_) ->
        {
            return !p_234457_0_.func_234425_eN_();
        }, new HuntCelebrationTask<>(2, 1.0F)), new SupplementedTask<PiglinEntity>(PiglinEntity::func_234425_eN_, new HuntCelebrationTask<>(4, 0.6F)), new FirstShuffledTask(ImmutableList.of(Pair.of(new LookAtEntityTask(EntityType.PIGLIN, 8.0F), 1), Pair.of(new WalkRandomlyTask(0.6F, 2, 1), 1), Pair.of(new DummyTask(10, 20), 1)))), MemoryModuleType.CELEBRATE_LOCATION);
    }

    private static void func_234502_d_(Brain<PiglinEntity> p_234502_0_)
    {
        p_234502_0_.registerActivity(Activity.ADMIRE_ITEM, 10, ImmutableList. < net.minecraft.entity.ai.brain.task.Task <? super PiglinEntity >> of(new PickupWantedItemTask<>(PiglinTasks::func_234455_E_, 1.0F, true, 9), new ForgetAdmiredItemTask(9), new StopReachingItemTask(200, 200)), MemoryModuleType.ADMIRING_ITEM);
    }

    private static void func_234507_e_(Brain<PiglinEntity> p_234507_0_)
    {
        p_234507_0_.registerActivity(Activity.AVOID, 10, ImmutableList.of(RunAwayTask.func_233965_b_(MemoryModuleType.AVOID_TARGET, 1.0F, 12, true), func_234458_a_(), func_234481_b_(), new PredicateTask<PiglinEntity>(PiglinTasks::func_234533_t_, MemoryModuleType.AVOID_TARGET)), MemoryModuleType.AVOID_TARGET);
    }

    private static void func_234511_f_(Brain<PiglinEntity> p_234511_0_)
    {
        p_234511_0_.registerActivity(Activity.RIDE, 10, ImmutableList.of(new RideEntityTask<>(0.8F), new LookAtEntityTask(PiglinTasks::func_234482_b_, 8.0F), new SupplementedTask<>(Entity::isPassenger, func_234458_a_()), new StopRidingEntityTask<>(8, PiglinTasks::func_234467_a_)), MemoryModuleType.RIDE_TARGET);
    }

    private static FirstShuffledTask<PiglinEntity> func_234458_a_()
    {
        return new FirstShuffledTask<>(ImmutableList.of(Pair.of(new LookAtEntityTask(EntityType.PLAYER, 8.0F), 1), Pair.of(new LookAtEntityTask(EntityType.PIGLIN, 8.0F), 1), Pair.of(new LookAtEntityTask(8.0F), 1), Pair.of(new DummyTask(30, 60), 1)));
    }

    private static FirstShuffledTask<PiglinEntity> func_234481_b_()
    {
        return new FirstShuffledTask<>(ImmutableList.of(Pair.of(new WalkRandomlyTask(0.6F), 2), Pair.of(InteractWithEntityTask.func_220445_a(EntityType.PIGLIN, 8, MemoryModuleType.INTERACTION_TARGET, 0.6F, 2), 2), Pair.of(new SupplementedTask<>(PiglinTasks::func_234514_g_, new WalkTowardsLookTargetTask(0.6F, 3)), 2), Pair.of(new DummyTask(30, 60), 1)));
    }

    private static RunAwayTask<BlockPos> func_234493_c_()
    {
        return RunAwayTask.func_233963_a_(MemoryModuleType.NEAREST_REPELLENT, 1.0F, 8, false);
    }

    private static PiglinIdleActivityTask<PiglinEntity, LivingEntity> func_241428_d_()
    {
        return new PiglinIdleActivityTask<>(PiglinEntity::isChild, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.AVOID_TARGET, field_241418_g_);
    }

    private static PiglinIdleActivityTask<PiglinEntity, LivingEntity> func_234500_d_()
    {
        return new PiglinIdleActivityTask<>(PiglinTasks::func_234525_l_, MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, MemoryModuleType.AVOID_TARGET, field_234449_f_);
    }

    protected static void func_234486_b_(PiglinEntity p_234486_0_)
    {
        Brain<PiglinEntity> brain = p_234486_0_.getBrain();
        Activity activity = brain.getTemporaryActivity().orElse((Activity)null);
        brain.switchActivities(ImmutableList.of(Activity.ADMIRE_ITEM, Activity.FIGHT, Activity.AVOID, Activity.CELEBRATE, Activity.RIDE, Activity.IDLE));
        Activity activity1 = brain.getTemporaryActivity().orElse((Activity)null);

        if (activity != activity1)
        {
            func_241429_d_(p_234486_0_).ifPresent(p_234486_0_::func_241417_a_);
        }

        p_234486_0_.setAggroed(brain.hasMemory(MemoryModuleType.ATTACK_TARGET));

        if (!brain.hasMemory(MemoryModuleType.RIDE_TARGET) && func_234522_j_(p_234486_0_))
        {
            p_234486_0_.stopRiding();
        }

        if (!brain.hasMemory(MemoryModuleType.CELEBRATE_LOCATION))
        {
            brain.removeMemory(MemoryModuleType.DANCING);
        }

        p_234486_0_.func_234442_u_(brain.hasMemory(MemoryModuleType.DANCING));
    }

    private static boolean func_234522_j_(PiglinEntity p_234522_0_)
    {
        if (!p_234522_0_.isChild())
        {
            return false;
        }
        else
        {
            Entity entity = p_234522_0_.getRidingEntity();
            return entity instanceof PiglinEntity && ((PiglinEntity)entity).isChild() || entity instanceof HoglinEntity && ((HoglinEntity)entity).isChild();
        }
    }

    protected static void func_234470_a_(PiglinEntity p_234470_0_, ItemEntity p_234470_1_)
    {
        func_234531_r_(p_234470_0_);
        ItemStack itemstack;

        if (p_234470_1_.getItem().getItem() == Items.GOLD_NUGGET)
        {
            p_234470_0_.onItemPickup(p_234470_1_, p_234470_1_.getItem().getCount());
            itemstack = p_234470_1_.getItem();
            p_234470_1_.remove();
        }
        else
        {
            p_234470_0_.onItemPickup(p_234470_1_, 1);
            itemstack = func_234465_a_(p_234470_1_);
        }

        Item item = itemstack.getItem();

        if (func_234480_a_(item))
        {
            p_234470_0_.getBrain().removeMemory(MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM);
            func_241427_c_(p_234470_0_, itemstack);
            func_234501_d_(p_234470_0_);
        }
        else if (func_234499_c_(item) && !func_234538_z_(p_234470_0_))
        {
            func_234536_x_(p_234470_0_);
        }
        else
        {
            boolean flag = p_234470_0_.func_233665_g_(itemstack);

            if (!flag)
            {
                func_234498_c_(p_234470_0_, itemstack);
            }
        }
    }

    private static void func_241427_c_(PiglinEntity p_241427_0_, ItemStack p_241427_1_)
    {
        if (func_234454_D_(p_241427_0_))
        {
            p_241427_0_.entityDropItem(p_241427_0_.getHeldItem(Hand.OFF_HAND));
        }

        p_241427_0_.func_234439_n_(p_241427_1_);
    }

    private static ItemStack func_234465_a_(ItemEntity p_234465_0_)
    {
        ItemStack itemstack = p_234465_0_.getItem();
        ItemStack itemstack1 = itemstack.split(1);

        if (itemstack.isEmpty())
        {
            p_234465_0_.remove();
        }
        else
        {
            p_234465_0_.setItem(itemstack);
        }

        return itemstack1;
    }

    protected static void func_234477_a_(PiglinEntity p_234477_0_, boolean p_234477_1_)
    {
        ItemStack itemstack = p_234477_0_.getHeldItem(Hand.OFF_HAND);
        p_234477_0_.setHeldItem(Hand.OFF_HAND, ItemStack.EMPTY);

        if (p_234477_0_.func_242337_eM())
        {
            boolean flag = func_234492_b_(itemstack.getItem());

            if (p_234477_1_ && flag)
            {
                func_234475_a_(p_234477_0_, func_234524_k_(p_234477_0_));
            }
            else if (!flag)
            {
                boolean flag1 = p_234477_0_.func_233665_g_(itemstack);

                if (!flag1)
                {
                    func_234498_c_(p_234477_0_, itemstack);
                }
            }
        }
        else
        {
            boolean flag2 = p_234477_0_.func_233665_g_(itemstack);

            if (!flag2)
            {
                ItemStack itemstack1 = p_234477_0_.getHeldItemMainhand();

                if (func_234480_a_(itemstack1.getItem()))
                {
                    func_234498_c_(p_234477_0_, itemstack1);
                }
                else
                {
                    func_234475_a_(p_234477_0_, Collections.singletonList(itemstack1));
                }

                p_234477_0_.func_234438_m_(itemstack);
            }
        }
    }

    protected static void func_234496_c_(PiglinEntity p_234496_0_)
    {
        if (func_234451_A_(p_234496_0_) && !p_234496_0_.getHeldItemOffhand().isEmpty())
        {
            p_234496_0_.entityDropItem(p_234496_0_.getHeldItemOffhand());
            p_234496_0_.setHeldItem(Hand.OFF_HAND, ItemStack.EMPTY);
        }
    }

    private static void func_234498_c_(PiglinEntity p_234498_0_, ItemStack p_234498_1_)
    {
        ItemStack itemstack = p_234498_0_.func_234436_k_(p_234498_1_);
        func_234490_b_(p_234498_0_, Collections.singletonList(itemstack));
    }

    private static void func_234475_a_(PiglinEntity p_234475_0_, List<ItemStack> p_234475_1_)
    {
        Optional<PlayerEntity> optional = p_234475_0_.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER);

        if (optional.isPresent())
        {
            func_234472_a_(p_234475_0_, optional.get(), p_234475_1_);
        }
        else
        {
            func_234490_b_(p_234475_0_, p_234475_1_);
        }
    }

    private static void func_234490_b_(PiglinEntity p_234490_0_, List<ItemStack> p_234490_1_)
    {
        func_234476_a_(p_234490_0_, p_234490_1_, func_234537_y_(p_234490_0_));
    }

    private static void func_234472_a_(PiglinEntity p_234472_0_, PlayerEntity p_234472_1_, List<ItemStack> p_234472_2_)
    {
        func_234476_a_(p_234472_0_, p_234472_2_, p_234472_1_.getPositionVec());
    }

    private static void func_234476_a_(PiglinEntity p_234476_0_, List<ItemStack> p_234476_1_, Vector3d p_234476_2_)
    {
        if (!p_234476_1_.isEmpty())
        {
            p_234476_0_.swingArm(Hand.OFF_HAND);

            for (ItemStack itemstack : p_234476_1_)
            {
                BrainUtil.spawnItemNearEntity(p_234476_0_, itemstack, p_234476_2_.add(0.0D, 1.0D, 0.0D));
            }
        }
    }

    private static List<ItemStack> func_234524_k_(PiglinEntity p_234524_0_)
    {
        LootTable loottable = p_234524_0_.world.getServer().getLootTableManager().getLootTableFromLocation(LootTables.PIGLIN_BARTERING);
        return loottable.generate((new LootContext.Builder((ServerWorld)p_234524_0_.world)).withParameter(LootParameters.THIS_ENTITY, p_234524_0_).withRandom(p_234524_0_.world.rand).build(LootParameterSets.field_237453_h_));
    }

    private static boolean func_234461_a_(LivingEntity p_234461_0_, LivingEntity p_234461_1_)
    {
        if (p_234461_1_.getType() != EntityType.HOGLIN)
        {
            return false;
        }
        else
        {
            return (new Random(p_234461_0_.world.getGameTime())).nextFloat() < 0.1F;
        }
    }

    protected static boolean func_234474_a_(PiglinEntity p_234474_0_, ItemStack p_234474_1_)
    {
        Item item = p_234474_1_.getItem();

        if (item.isIn(ItemTags.PIGLIN_REPELLENTS))
        {
            return false;
        }
        else if (func_234453_C_(p_234474_0_) && p_234474_0_.getBrain().hasMemory(MemoryModuleType.ATTACK_TARGET))
        {
            return false;
        }
        else if (func_234492_b_(item))
        {
            return func_234455_E_(p_234474_0_);
        }
        else
        {
            boolean flag = p_234474_0_.func_234437_l_(p_234474_1_);

            if (item == Items.GOLD_NUGGET)
            {
                return flag;
            }
            else if (func_234499_c_(item))
            {
                return !func_234538_z_(p_234474_0_) && flag;
            }
            else if (!func_234480_a_(item))
            {
                return p_234474_0_.func_234440_o_(p_234474_1_);
            }
            else
            {
                return func_234455_E_(p_234474_0_) && flag;
            }
        }
    }

    protected static boolean func_234480_a_(Item p_234480_0_)
    {
        return p_234480_0_.isIn(ItemTags.PIGLIN_LOVED);
    }

    private static boolean func_234467_a_(PiglinEntity p_234467_0_, Entity p_234467_1_)
    {
        if (!(p_234467_1_ instanceof MobEntity))
        {
            return false;
        }
        else
        {
            MobEntity mobentity = (MobEntity)p_234467_1_;
            return !mobentity.isChild() || !mobentity.isAlive() || func_234517_h_(p_234467_0_) || func_234517_h_(mobentity) || mobentity instanceof PiglinEntity && mobentity.getRidingEntity() == null;
        }
    }

    private static boolean func_234504_d_(PiglinEntity p_234504_0_, LivingEntity p_234504_1_)
    {
        return func_234526_m_(p_234504_0_).filter((p_234483_1_) ->
        {
            return p_234483_1_ == p_234504_1_;
        }).isPresent();
    }

    private static boolean func_234525_l_(PiglinEntity p_234525_0_)
    {
        Brain<PiglinEntity> brain = p_234525_0_.getBrain();

        if (brain.hasMemory(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED))
        {
            LivingEntity livingentity = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED).get();
            return p_234525_0_.isEntityInRange(livingentity, 6.0D);
        }
        else
        {
            return false;
        }
    }

    private static Optional <? extends LivingEntity > func_234526_m_(PiglinEntity p_234526_0_)
    {
        Brain<PiglinEntity> brain = p_234526_0_.getBrain();

        if (func_234525_l_(p_234526_0_))
        {
            return Optional.empty();
        }
        else
        {
            Optional<LivingEntity> optional = BrainUtil.getTargetFromMemory(p_234526_0_, MemoryModuleType.ANGRY_AT);

            if (optional.isPresent() && func_234506_e_(optional.get()))
            {
                return optional;
            }
            else
            {
                if (brain.hasMemory(MemoryModuleType.UNIVERSAL_ANGER))
                {
                    Optional<PlayerEntity> optional1 = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER);

                    if (optional1.isPresent())
                    {
                        return optional1;
                    }
                }

                Optional<MobEntity> optional3 = brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS);

                if (optional3.isPresent())
                {
                    return optional3;
                }
                else
                {
                    Optional<PlayerEntity> optional2 = brain.getMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD);
                    return optional2.isPresent() && func_234506_e_(optional2.get()) ? optional2 : Optional.empty();
                }
            }
        }
    }

    public static void func_234478_a_(PlayerEntity p_234478_0_, boolean p_234478_1_)
    {
        List<PiglinEntity> list = p_234478_0_.world.getEntitiesWithinAABB(PiglinEntity.class, p_234478_0_.getBoundingBox().grow(16.0D));
        list.stream().filter(PiglinTasks::func_234520_i_).filter((p_234491_2_) ->
        {
            return !p_234478_1_ || BrainUtil.isMobVisible(p_234491_2_, p_234478_0_);
        }).forEach((p_234479_1_) ->
        {
            if (p_234479_1_.world.getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER))
            {
                func_241431_f_(p_234479_1_, p_234478_0_);
            }
            else {
                func_234497_c_(p_234479_1_, p_234478_0_);
            }
        });
    }

    public static ActionResultType func_234471_a_(PiglinEntity p_234471_0_, PlayerEntity p_234471_1_, Hand p_234471_2_)
    {
        ItemStack itemstack = p_234471_1_.getHeldItem(p_234471_2_);

        if (func_234489_b_(p_234471_0_, itemstack))
        {
            ItemStack itemstack1 = itemstack.split(1);
            func_241427_c_(p_234471_0_, itemstack1);
            func_234501_d_(p_234471_0_);
            func_234531_r_(p_234471_0_);
            return ActionResultType.CONSUME;
        }
        else
        {
            return ActionResultType.PASS;
        }
    }

    protected static boolean func_234489_b_(PiglinEntity p_234489_0_, ItemStack p_234489_1_)
    {
        return !func_234453_C_(p_234489_0_) && !func_234451_A_(p_234489_0_) && p_234489_0_.func_242337_eM() && func_234492_b_(p_234489_1_.getItem());
    }

    protected static void func_234468_a_(PiglinEntity p_234468_0_, LivingEntity p_234468_1_)
    {
        if (!(p_234468_1_ instanceof PiglinEntity))
        {
            if (func_234454_D_(p_234468_0_))
            {
                func_234477_a_(p_234468_0_, false);
            }

            Brain<PiglinEntity> brain = p_234468_0_.getBrain();
            brain.removeMemory(MemoryModuleType.CELEBRATE_LOCATION);
            brain.removeMemory(MemoryModuleType.DANCING);
            brain.removeMemory(MemoryModuleType.ADMIRING_ITEM);

            if (p_234468_1_ instanceof PlayerEntity)
            {
                brain.replaceMemory(MemoryModuleType.ADMIRING_DISABLED, true, 400L);
            }

            func_234515_g_(p_234468_0_).ifPresent((p_234462_2_) ->
            {
                if (p_234462_2_.getType() != p_234468_1_.getType())
                {
                    brain.removeMemory(MemoryModuleType.AVOID_TARGET);
                }
            });

            if (p_234468_0_.isChild())
            {
                brain.replaceMemory(MemoryModuleType.AVOID_TARGET, p_234468_1_, 100L);

                if (func_234506_e_(p_234468_1_))
                {
                    func_234487_b_(p_234468_0_, p_234468_1_);
                }
            }
            else if (p_234468_1_.getType() == EntityType.HOGLIN && func_234535_v_(p_234468_0_))
            {
                func_234521_i_(p_234468_0_, p_234468_1_);
                func_234516_g_(p_234468_0_, p_234468_1_);
            }
            else
            {
                func_234509_e_(p_234468_0_, p_234468_1_);
            }
        }
    }

    protected static void func_234509_e_(AbstractPiglinEntity p_234509_0_, LivingEntity p_234509_1_)
    {
        if (!p_234509_0_.getBrain().hasActivity(Activity.AVOID))
        {
            if (func_234506_e_(p_234509_1_))
            {
                if (!BrainUtil.isTargetWithinDistance(p_234509_0_, p_234509_1_, 4.0D))
                {
                    if (p_234509_1_.getType() == EntityType.PLAYER && p_234509_0_.world.getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER))
                    {
                        func_241431_f_(p_234509_0_, p_234509_1_);
                        func_241430_f_(p_234509_0_);
                    }
                    else
                    {
                        func_234497_c_(p_234509_0_, p_234509_1_);
                        func_234487_b_(p_234509_0_, p_234509_1_);
                    }
                }
            }
        }
    }

    public static Optional<SoundEvent> func_241429_d_(PiglinEntity p_241429_0_)
    {
        return p_241429_0_.getBrain().getTemporaryActivity().map((p_241426_1_) ->
        {
            return func_241422_a_(p_241429_0_, p_241426_1_);
        });
    }

    private static SoundEvent func_241422_a_(PiglinEntity p_241422_0_, Activity p_241422_1_)
    {
        if (p_241422_1_ == Activity.FIGHT)
        {
            return SoundEvents.ENTITY_PIGLIN_ANGRY;
        }
        else if (p_241422_0_.func_242336_eL())
        {
            return SoundEvents.ENTITY_PIGLIN_RETREAT;
        }
        else if (p_241422_1_ == Activity.AVOID && func_234528_o_(p_241422_0_))
        {
            return SoundEvents.ENTITY_PIGLIN_RETREAT;
        }
        else if (p_241422_1_ == Activity.ADMIRE_ITEM)
        {
            return SoundEvents.ENTITY_PIGLIN_ADMIRING_ITEM;
        }
        else if (p_241422_1_ == Activity.CELEBRATE)
        {
            return SoundEvents.ENTITY_PIGLIN_CELEBRATE;
        }
        else if (func_234510_f_(p_241422_0_))
        {
            return SoundEvents.ENTITY_PIGLIN_JEALOUS;
        }
        else
        {
            return func_234452_B_(p_241422_0_) ? SoundEvents.ENTITY_PIGLIN_RETREAT : SoundEvents.ENTITY_PIGLIN_AMBIENT;
        }
    }

    private static boolean func_234528_o_(PiglinEntity p_234528_0_)
    {
        Brain<PiglinEntity> brain = p_234528_0_.getBrain();
        return !brain.hasMemory(MemoryModuleType.AVOID_TARGET) ? false : brain.getMemory(MemoryModuleType.AVOID_TARGET).get().isEntityInRange(p_234528_0_, 12.0D);
    }

    protected static boolean func_234508_e_(PiglinEntity p_234508_0_)
    {
        return p_234508_0_.getBrain().hasMemory(MemoryModuleType.HUNTED_RECENTLY) || func_234529_p_(p_234508_0_).stream().anyMatch((p_234456_0_) ->
        {
            return p_234456_0_.getBrain().hasMemory(MemoryModuleType.HUNTED_RECENTLY);
        });
    }

    private static List<AbstractPiglinEntity> func_234529_p_(PiglinEntity p_234529_0_)
    {
        return p_234529_0_.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS).orElse(ImmutableList.of());
    }

    private static List<AbstractPiglinEntity> func_234530_q_(AbstractPiglinEntity p_234530_0_)
    {
        return p_234530_0_.getBrain().getMemory(MemoryModuleType.NEAREST_ADULT_PIGLINS).orElse(ImmutableList.of());
    }

    public static boolean func_234460_a_(LivingEntity p_234460_0_)
    {
        for (ItemStack itemstack : p_234460_0_.getArmorInventoryList())
        {
            Item item = itemstack.getItem();

            if (item instanceof ArmorItem && ((ArmorItem)item).getArmorMaterial() == ArmorMaterial.GOLD)
            {
                return true;
            }
        }

        return false;
    }

    private static void func_234531_r_(PiglinEntity p_234531_0_)
    {
        p_234531_0_.getBrain().removeMemory(MemoryModuleType.WALK_TARGET);
        p_234531_0_.getNavigator().clearPath();
    }

    private static RunSometimesTask<PiglinEntity> func_234505_e_()
    {
        return new RunSometimesTask<>(new PiglinIdleActivityTask<>(PiglinEntity::isChild, MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, MemoryModuleType.RIDE_TARGET, field_234447_d_), field_234446_c_);
    }

    protected static void func_234487_b_(AbstractPiglinEntity p_234487_0_, LivingEntity p_234487_1_)
    {
        func_234530_q_(p_234487_0_).forEach((p_234484_1_) ->
        {
            if (p_234487_1_.getType() != EntityType.HOGLIN || p_234484_1_.func_234422_eK_() && ((HoglinEntity)p_234487_1_).func_234365_eM_())
            {
                func_234513_f_(p_234484_1_, p_234487_1_);
            }
        });
    }

    protected static void func_241430_f_(AbstractPiglinEntity p_241430_0_)
    {
        func_234530_q_(p_241430_0_).forEach((p_241419_0_) ->
        {
            func_241432_i_(p_241419_0_).ifPresent((p_241421_1_) -> {
                func_234497_c_(p_241419_0_, p_241421_1_);
            });
        });
    }

    protected static void func_234512_f_(PiglinEntity p_234512_0_)
    {
        func_234529_p_(p_234512_0_).forEach(PiglinTasks::func_234518_h_);
    }

    protected static void func_234497_c_(AbstractPiglinEntity p_234497_0_, LivingEntity p_234497_1_)
    {
        if (func_234506_e_(p_234497_1_))
        {
            p_234497_0_.getBrain().removeMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
            p_234497_0_.getBrain().replaceMemory(MemoryModuleType.ANGRY_AT, p_234497_1_.getUniqueID(), 600L);

            if (p_234497_1_.getType() == EntityType.HOGLIN && p_234497_0_.func_234422_eK_())
            {
                func_234518_h_(p_234497_0_);
            }

            if (p_234497_1_.getType() == EntityType.PLAYER && p_234497_0_.world.getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER))
            {
                p_234497_0_.getBrain().replaceMemory(MemoryModuleType.UNIVERSAL_ANGER, true, 600L);
            }
        }
    }

    private static void func_241431_f_(AbstractPiglinEntity p_241431_0_, LivingEntity p_241431_1_)
    {
        Optional<PlayerEntity> optional = func_241432_i_(p_241431_0_);

        if (optional.isPresent())
        {
            func_234497_c_(p_241431_0_, optional.get());
        }
        else
        {
            func_234497_c_(p_241431_0_, p_241431_1_);
        }
    }

    private static void func_234513_f_(AbstractPiglinEntity p_234513_0_, LivingEntity p_234513_1_)
    {
        Optional<LivingEntity> optional = func_234532_s_(p_234513_0_);
        LivingEntity livingentity = BrainUtil.getNearestEntity(p_234513_0_, optional, p_234513_1_);

        if (!optional.isPresent() || optional.get() != livingentity)
        {
            func_234497_c_(p_234513_0_, livingentity);
        }
    }

    private static Optional<LivingEntity> func_234532_s_(AbstractPiglinEntity p_234532_0_)
    {
        return BrainUtil.getTargetFromMemory(p_234532_0_, MemoryModuleType.ANGRY_AT);
    }

    public static Optional<LivingEntity> func_234515_g_(PiglinEntity p_234515_0_)
    {
        return p_234515_0_.getBrain().hasMemory(MemoryModuleType.AVOID_TARGET) ? p_234515_0_.getBrain().getMemory(MemoryModuleType.AVOID_TARGET) : Optional.empty();
    }

    public static Optional<PlayerEntity> func_241432_i_(AbstractPiglinEntity p_241432_0_)
    {
        return p_241432_0_.getBrain().hasMemory(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER) ? p_241432_0_.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER) : Optional.empty();
    }

    private static void func_234516_g_(PiglinEntity p_234516_0_, LivingEntity p_234516_1_)
    {
        func_234529_p_(p_234516_0_).stream().filter((p_242341_0_) ->
        {
            return p_242341_0_ instanceof PiglinEntity;
        }).forEach((p_234463_1_) ->
        {
            func_234519_h_((PiglinEntity)p_234463_1_, p_234516_1_);
        });
    }

    private static void func_234519_h_(PiglinEntity p_234519_0_, LivingEntity p_234519_1_)
    {
        Brain<PiglinEntity> brain = p_234519_0_.getBrain();
        LivingEntity lvt_3_1_ = BrainUtil.getNearestEntity(p_234519_0_, brain.getMemory(MemoryModuleType.AVOID_TARGET), p_234519_1_);
        lvt_3_1_ = BrainUtil.getNearestEntity(p_234519_0_, brain.getMemory(MemoryModuleType.ATTACK_TARGET), lvt_3_1_);
        func_234521_i_(p_234519_0_, lvt_3_1_);
    }

    private static boolean func_234533_t_(PiglinEntity p_234533_0_)
    {
        Brain<PiglinEntity> brain = p_234533_0_.getBrain();

        if (!brain.hasMemory(MemoryModuleType.AVOID_TARGET))
        {
            return true;
        }
        else
        {
            LivingEntity livingentity = brain.getMemory(MemoryModuleType.AVOID_TARGET).get();
            EntityType<?> entitytype = livingentity.getType();

            if (entitytype == EntityType.HOGLIN)
            {
                return func_234534_u_(p_234533_0_);
            }
            else if (func_234459_a_(entitytype))
            {
                return !brain.hasMemory(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, livingentity);
            }
            else
            {
                return false;
            }
        }
    }

    private static boolean func_234534_u_(PiglinEntity p_234534_0_)
    {
        return !func_234535_v_(p_234534_0_);
    }

    private static boolean func_234535_v_(PiglinEntity p_234535_0_)
    {
        int i = p_234535_0_.getBrain().getMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT).orElse(0) + 1;
        int j = p_234535_0_.getBrain().getMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT).orElse(0);
        return j > i;
    }

    private static void func_234521_i_(PiglinEntity p_234521_0_, LivingEntity p_234521_1_)
    {
        p_234521_0_.getBrain().removeMemory(MemoryModuleType.ANGRY_AT);
        p_234521_0_.getBrain().removeMemory(MemoryModuleType.ATTACK_TARGET);
        p_234521_0_.getBrain().removeMemory(MemoryModuleType.WALK_TARGET);
        p_234521_0_.getBrain().replaceMemory(MemoryModuleType.AVOID_TARGET, p_234521_1_, (long)field_234448_e_.getRandomWithinRange(p_234521_0_.world.rand));
        func_234518_h_(p_234521_0_);
    }

    protected static void func_234518_h_(AbstractPiglinEntity p_234518_0_)
    {
        p_234518_0_.getBrain().replaceMemory(MemoryModuleType.HUNTED_RECENTLY, true, (long)field_234445_b_.getRandomWithinRange(p_234518_0_.world.rand));
    }

    private static void func_234536_x_(PiglinEntity p_234536_0_)
    {
        p_234536_0_.getBrain().replaceMemory(MemoryModuleType.ATE_RECENTLY, true, 200L);
    }

    private static Vector3d func_234537_y_(PiglinEntity p_234537_0_)
    {
        Vector3d vector3d = RandomPositionGenerator.getLandPos(p_234537_0_, 4, 2);
        return vector3d == null ? p_234537_0_.getPositionVec() : vector3d;
    }

    private static boolean func_234538_z_(PiglinEntity p_234538_0_)
    {
        return p_234538_0_.getBrain().hasMemory(MemoryModuleType.ATE_RECENTLY);
    }

    protected static boolean func_234520_i_(AbstractPiglinEntity p_234520_0_)
    {
        return p_234520_0_.getBrain().hasActivity(Activity.IDLE);
    }

    private static boolean func_234494_c_(LivingEntity p_234494_0_)
    {
        return p_234494_0_.canEquip(Items.CROSSBOW);
    }

    private static void func_234501_d_(LivingEntity p_234501_0_)
    {
        p_234501_0_.getBrain().replaceMemory(MemoryModuleType.ADMIRING_ITEM, true, 120L);
    }

    private static boolean func_234451_A_(PiglinEntity p_234451_0_)
    {
        return p_234451_0_.getBrain().hasMemory(MemoryModuleType.ADMIRING_ITEM);
    }

    private static boolean func_234492_b_(Item p_234492_0_)
    {
        return p_234492_0_ == field_234444_a_;
    }

    private static boolean func_234499_c_(Item p_234499_0_)
    {
        return field_234450_g_.contains(p_234499_0_);
    }

    private static boolean func_234506_e_(LivingEntity p_234506_0_)
    {
        return EntityPredicates.CAN_HOSTILE_AI_TARGET.test(p_234506_0_);
    }

    private static boolean func_234452_B_(PiglinEntity p_234452_0_)
    {
        return p_234452_0_.getBrain().hasMemory(MemoryModuleType.NEAREST_REPELLENT);
    }

    private static boolean func_234510_f_(LivingEntity p_234510_0_)
    {
        return p_234510_0_.getBrain().hasMemory(MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM);
    }

    private static boolean func_234514_g_(LivingEntity p_234514_0_)
    {
        return !func_234510_f_(p_234514_0_);
    }

    public static boolean func_234482_b_(LivingEntity p_234482_0_)
    {
        return p_234482_0_.getType() == EntityType.PLAYER && p_234482_0_.func_233634_a_(PiglinTasks::func_234480_a_);
    }

    private static boolean func_234453_C_(PiglinEntity p_234453_0_)
    {
        return p_234453_0_.getBrain().hasMemory(MemoryModuleType.ADMIRING_DISABLED);
    }

    private static boolean func_234517_h_(LivingEntity p_234517_0_)
    {
        return p_234517_0_.getBrain().hasMemory(MemoryModuleType.HURT_BY);
    }

    private static boolean func_234454_D_(PiglinEntity p_234454_0_)
    {
        return !p_234454_0_.getHeldItemOffhand().isEmpty();
    }

    private static boolean func_234455_E_(PiglinEntity p_234455_0_)
    {
        return p_234455_0_.getHeldItemOffhand().isEmpty() || !func_234480_a_(p_234455_0_.getHeldItemOffhand().getItem());
    }

    public static boolean func_234459_a_(EntityType p_234459_0_)
    {
        return p_234459_0_ == EntityType.ZOMBIFIED_PIGLIN || p_234459_0_ == EntityType.ZOGLIN;
    }
}
