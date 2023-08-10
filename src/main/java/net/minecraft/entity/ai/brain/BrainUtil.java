package net.minecraft.entity.ai.brain;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShootableItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPosWrapper;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class BrainUtil
{
    public static void lookApproachEachOther(LivingEntity firstEntity, LivingEntity secondEntity, float speed)
    {
        lookAtEachOther(firstEntity, secondEntity);
        approachEachOther(firstEntity, secondEntity, speed);
    }

    public static boolean canSee(Brain<?> brainIn, LivingEntity target)
    {
        return brainIn.getMemory(MemoryModuleType.VISIBLE_MOBS).filter((visible) ->
        {
            return visible.contains(target);
        }).isPresent();
    }

    public static boolean isCorrectVisibleType(Brain<?> brains, MemoryModuleType <? extends LivingEntity > memorymodule, EntityType<?> entityTypeIn)
    {
        return canSeeEntity(brains, memorymodule, (livingEntity) ->
        {
            return livingEntity.getType() == entityTypeIn;
        });
    }

    private static boolean canSeeEntity(Brain<?> brain, MemoryModuleType <? extends LivingEntity > memoryType, Predicate<LivingEntity> livingPredicate)
    {
        return brain.getMemory(memoryType).filter(livingPredicate).filter(LivingEntity::isAlive).filter((livingEntity) ->
        {
            return canSee(brain, livingEntity);
        }).isPresent();
    }

    private static void lookAtEachOther(LivingEntity firstEntity, LivingEntity secondEntity)
    {
        lookAt(firstEntity, secondEntity);
        lookAt(secondEntity, firstEntity);
    }

    public static void lookAt(LivingEntity entityIn, LivingEntity targetIn)
    {
        entityIn.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(targetIn, true));
    }

    private static void approachEachOther(LivingEntity firstEntity, LivingEntity secondEntity, float speed)
    {
        int i = 2;
        setTargetEntity(firstEntity, secondEntity, speed, 2);
        setTargetEntity(secondEntity, firstEntity, speed, 2);
    }

    public static void setTargetEntity(LivingEntity livingEntity, Entity target, float speed, int distance)
    {
        WalkTarget walktarget = new WalkTarget(new EntityPosWrapper(target, false), speed, distance);
        livingEntity.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(target, true));
        livingEntity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, walktarget);
    }

    public static void setTargetPosition(LivingEntity livingEntity, BlockPos pos, float speed, int distance)
    {
        WalkTarget walktarget = new WalkTarget(new BlockPosWrapper(pos), speed, distance);
        livingEntity.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosWrapper(pos));
        livingEntity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, walktarget);
    }

    public static void spawnItemNearEntity(LivingEntity livingEntity, ItemStack stack, Vector3d offset)
    {
        double d0 = livingEntity.getPosYEye() - (double)0.3F;
        ItemEntity itementity = new ItemEntity(livingEntity.world, livingEntity.getPosX(), d0, livingEntity.getPosZ(), stack);
        float f = 0.3F;
        Vector3d vector3d = offset.subtract(livingEntity.getPositionVec());
        vector3d = vector3d.normalize().scale((double)0.3F);
        itementity.setMotion(vector3d);
        itementity.setDefaultPickupDelay();
        livingEntity.world.addEntity(itementity);
    }

    public static SectionPos getClosestVillageSection(ServerWorld serverWorldIn, SectionPos sectionPosIn, int radius)
    {
        int i = serverWorldIn.sectionsToVillage(sectionPosIn);
        return SectionPos.getAllInBox(sectionPosIn, radius).filter((sectionPos) ->
        {
            return serverWorldIn.sectionsToVillage(sectionPos) < i;
        }).min(Comparator.comparingInt(serverWorldIn::sectionsToVillage)).orElse(sectionPosIn);
    }

    public static boolean canFireAtTarget(MobEntity mob, LivingEntity target, int cooldown)
    {
        Item item = mob.getHeldItemMainhand().getItem();

        if (item instanceof ShootableItem && mob.func_230280_a_((ShootableItem)item))
        {
            int i = ((ShootableItem)item).func_230305_d_() - cooldown;
            return mob.isEntityInRange(target, (double)i);
        }
        else
        {
            return canAttackTarget(mob, target);
        }
    }

    public static boolean canAttackTarget(LivingEntity livingEntity, LivingEntity target)
    {
        double d0 = livingEntity.getDistanceSq(target.getPosX(), target.getPosY(), target.getPosZ());
        double d1 = (double)(livingEntity.getWidth() * 2.0F * livingEntity.getWidth() * 2.0F + target.getWidth());
        return d0 <= d1;
    }

    public static boolean isTargetWithinDistance(LivingEntity livingEntity, LivingEntity target, double distance)
    {
        Optional<LivingEntity> optional = livingEntity.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);

        if (!optional.isPresent())
        {
            return false;
        }
        else
        {
            double d0 = livingEntity.getDistanceSq(optional.get().getPositionVec());
            double d1 = livingEntity.getDistanceSq(target.getPositionVec());
            return d1 > d0 + distance * distance;
        }
    }

    public static boolean isMobVisible(LivingEntity livingEntity, LivingEntity target)
    {
        Brain<?> brain = livingEntity.getBrain();
        return !brain.hasMemory(MemoryModuleType.VISIBLE_MOBS) ? false : brain.getMemory(MemoryModuleType.VISIBLE_MOBS).get().contains(target);
    }

    public static LivingEntity getNearestEntity(LivingEntity centerEntity, Optional<LivingEntity> optionalEntity, LivingEntity livingEntity)
    {
        return !optionalEntity.isPresent() ? livingEntity : getNearestEntity(centerEntity, optionalEntity.get(), livingEntity);
    }

    public static LivingEntity getNearestEntity(LivingEntity centerEntity, LivingEntity livingEntity1, LivingEntity livingEntity2)
    {
        Vector3d vector3d = livingEntity1.getPositionVec();
        Vector3d vector3d1 = livingEntity2.getPositionVec();
        return centerEntity.getDistanceSq(vector3d) < centerEntity.getDistanceSq(vector3d1) ? livingEntity1 : livingEntity2;
    }

    public static Optional<LivingEntity> getTargetFromMemory(LivingEntity livingEntity, MemoryModuleType<UUID> targetMemory)
    {
        Optional<UUID> optional = livingEntity.getBrain().getMemory(targetMemory);
        return optional.map((uuid) ->
        {
            return (LivingEntity)((ServerWorld)livingEntity.world).getEntityByUuid(uuid);
        });
    }

    public static Stream<VillagerEntity> getNearbyVillagers(VillagerEntity villager, Predicate<VillagerEntity> villagerPredicate)
    {
        return villager.getBrain().getMemory(MemoryModuleType.MOBS).map((mobs) ->
        {
            return mobs.stream().filter((livingEntity) -> {
                return livingEntity instanceof VillagerEntity && livingEntity != villager;
            }).map((livingEntity) -> {
                return (VillagerEntity)livingEntity;
            }).filter(LivingEntity::isAlive).filter(villagerPredicate);
        }).orElseGet(Stream::empty);
    }
}
