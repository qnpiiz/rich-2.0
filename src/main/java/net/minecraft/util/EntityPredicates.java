package net.minecraft.util;

import com.google.common.base.Predicates;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Team;
import net.minecraft.world.Difficulty;

public final class EntityPredicates
{
    public static final Predicate<Entity> IS_ALIVE = Entity::isAlive;
    public static final Predicate<LivingEntity> IS_LIVING_ALIVE = LivingEntity::isAlive;
    public static final Predicate<Entity> IS_STANDALONE = (entity) ->
    {
        return entity.isAlive() && !entity.isBeingRidden() && !entity.isPassenger();
    };
    public static final Predicate<Entity> HAS_INVENTORY = (entity) ->
    {
        return entity instanceof IInventory && entity.isAlive();
    };
    public static final Predicate<Entity> CAN_AI_TARGET = (entity) ->
    {
        return !(entity instanceof PlayerEntity) || !entity.isSpectator() && !((PlayerEntity)entity).isCreative();
    };
    public static final Predicate<Entity> CAN_HOSTILE_AI_TARGET = (entity) ->
    {
        return !(entity instanceof PlayerEntity) || !entity.isSpectator() && !((PlayerEntity)entity).isCreative() && entity.world.getDifficulty() != Difficulty.PEACEFUL;
    };
    public static final Predicate<Entity> NOT_SPECTATING = (entity) ->
    {
        return !entity.isSpectator();
    };

    public static Predicate<Entity> withinRange(double x, double y, double z, double range)
    {
        double d0 = range * range;
        return (entity) ->
        {
            return entity != null && entity.getDistanceSq(x, y, z) <= d0;
        };
    }

    public static Predicate<Entity> pushableBy(Entity entityIn)
    {
        Team team = entityIn.getTeam();
        Team.CollisionRule team$collisionrule = team == null ? Team.CollisionRule.ALWAYS : team.getCollisionRule();
        return (Predicate<Entity>)(team$collisionrule == Team.CollisionRule.NEVER ? Predicates.alwaysFalse() : NOT_SPECTATING.and((entity) ->
        {
            if (!entity.canBePushed())
            {
                return false;
            }
            else if (!entityIn.world.isRemote || entity instanceof PlayerEntity && ((PlayerEntity)entity).isUser())
            {
                Team team1 = entity.getTeam();
                Team.CollisionRule team$collisionrule1 = team1 == null ? Team.CollisionRule.ALWAYS : team1.getCollisionRule();

                if (team$collisionrule1 == Team.CollisionRule.NEVER)
                {
                    return false;
                }
                else
                {
                    boolean flag = team != null && team.isSameTeam(team1);

                    if ((team$collisionrule == Team.CollisionRule.PUSH_OWN_TEAM || team$collisionrule1 == Team.CollisionRule.PUSH_OWN_TEAM) && flag)
                    {
                        return false;
                    }
                    else
                    {
                        return team$collisionrule != Team.CollisionRule.PUSH_OTHER_TEAMS && team$collisionrule1 != Team.CollisionRule.PUSH_OTHER_TEAMS || flag;
                    }
                }
            }
            else {
                return false;
            }
        }));
    }

    public static Predicate<Entity> notRiding(Entity entityIn)
    {
        return (entity) ->
        {
            while (true)
            {
                if (entity.isPassenger())
                {
                    entity = entity.getRidingEntity();

                    if (entity != entityIn)
                    {
                        continue;
                    }

                    return false;
                }

                return true;
            }
        };
    }

    public static class ArmoredMob implements Predicate<Entity>
    {
        private final ItemStack armor;

        public ArmoredMob(ItemStack armor)
        {
            this.armor = armor;
        }

        public boolean test(@Nullable Entity p_test_1_)
        {
            if (!p_test_1_.isAlive())
            {
                return false;
            }
            else if (!(p_test_1_ instanceof LivingEntity))
            {
                return false;
            }
            else
            {
                LivingEntity livingentity = (LivingEntity)p_test_1_;
                return livingentity.canPickUpItem(this.armor);
            }
        }
    }
}
