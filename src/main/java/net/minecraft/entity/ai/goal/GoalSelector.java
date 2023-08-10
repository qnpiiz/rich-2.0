package net.minecraft.entity.ai.goal;

import com.google.common.collect.Sets;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.profiler.IProfiler;
import net.optifine.util.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GoalSelector
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final PrioritizedGoal DUMMY = new PrioritizedGoal(Integer.MAX_VALUE, new Goal()
    {
        public boolean shouldExecute()
        {
            return false;
        }
    })
    {
        public boolean isRunning()
        {
            return false;
        }
    };
    private final Map<Goal.Flag, PrioritizedGoal> flagGoals = new EnumMap<>(Goal.Flag.class);
    private final Set<PrioritizedGoal> goals = Sets.newLinkedHashSet();
    private final Supplier<IProfiler> profiler;
    private final EnumSet<Goal.Flag> disabledFlags = EnumSet.noneOf(Goal.Flag.class);
    private int tickRate = 3;

    public GoalSelector(Supplier<IProfiler> profiler)
    {
        this.profiler = profiler;
    }

    /**
     * Add a now AITask. Args : priority, task
     */
    public void addGoal(int priority, Goal task)
    {
        this.goals.add(new PrioritizedGoal(priority, task));
    }

    /**
     * removes the indicated task from the entity's AI tasks.
     */
    public void removeGoal(Goal task)
    {
        this.goals.stream().filter((p_lambda$removeGoal$0_1_) ->
        {
            return p_lambda$removeGoal$0_1_.getGoal() == task;
        }).filter(PrioritizedGoal::isRunning).forEach(PrioritizedGoal::resetTask);
        this.goals.removeIf((p_lambda$removeGoal$1_1_) ->
        {
            return p_lambda$removeGoal$1_1_.getGoal() == task;
        });
    }

    public void tick()
    {
        IProfiler iprofiler = this.profiler.get();
        iprofiler.startSection("goalCleanup");

        if (this.goals.size() > 0)
        {
            for (PrioritizedGoal prioritizedgoal : this.goals)
            {
                if (prioritizedgoal.isRunning() && (!prioritizedgoal.isRunning() || CollectionUtils.anyMatch(prioritizedgoal.getMutexFlags(), this.disabledFlags) || !prioritizedgoal.shouldContinueExecuting()))
                {
                    prioritizedgoal.resetTask();
                }
            }
        }

        if (this.flagGoals.size() > 0)
        {
            this.flagGoals.forEach((p_lambda$tick$2_1_, p_lambda$tick$2_2_) ->
            {
                if (!p_lambda$tick$2_2_.isRunning())
                {
                    this.flagGoals.remove(p_lambda$tick$2_1_);
                }
            });
        }

        iprofiler.endSection();
        iprofiler.startSection("goalUpdate");

        if (this.goals.size() > 0)
        {
            for (PrioritizedGoal prioritizedgoal1 : this.goals)
            {
                if (!prioritizedgoal1.isRunning() && CollectionUtils.noneMatch(prioritizedgoal1.getMutexFlags(), this.disabledFlags) && allPreemptedBy(prioritizedgoal1, prioritizedgoal1.getMutexFlags(), this.flagGoals) && prioritizedgoal1.shouldExecute())
                {
                    resetTasks(prioritizedgoal1, prioritizedgoal1.getMutexFlags(), this.flagGoals);
                    prioritizedgoal1.startExecuting();
                }
            }
        }

        iprofiler.endSection();
        iprofiler.startSection("goalTick");

        if (this.goals.size() > 0)
        {
            for (PrioritizedGoal prioritizedgoal2 : this.goals)
            {
                if (prioritizedgoal2.isRunning())
                {
                    prioritizedgoal2.tick();
                }
            }
        }

        iprofiler.endSection();
    }

    private static boolean allPreemptedBy(PrioritizedGoal p_allPreemptedBy_0_, EnumSet<Goal.Flag> p_allPreemptedBy_1_, Map<Goal.Flag, PrioritizedGoal> p_allPreemptedBy_2_)
    {
        if (p_allPreemptedBy_1_.isEmpty())
        {
            return true;
        }
        else
        {
            for (Goal.Flag goal$flag : p_allPreemptedBy_1_)
            {
                PrioritizedGoal prioritizedgoal = p_allPreemptedBy_2_.getOrDefault(goal$flag, DUMMY);

                if (!prioritizedgoal.isPreemptedBy(p_allPreemptedBy_0_))
                {
                    return false;
                }
            }

            return true;
        }
    }

    private static void resetTasks(PrioritizedGoal p_resetTasks_0_, EnumSet<Goal.Flag> p_resetTasks_1_, Map<Goal.Flag, PrioritizedGoal> p_resetTasks_2_)
    {
        if (!p_resetTasks_1_.isEmpty())
        {
            for (Goal.Flag goal$flag : p_resetTasks_1_)
            {
                PrioritizedGoal prioritizedgoal = p_resetTasks_2_.getOrDefault(goal$flag, DUMMY);
                prioritizedgoal.resetTask();
                p_resetTasks_2_.put(goal$flag, p_resetTasks_0_);
            }
        }
    }

    public Stream<PrioritizedGoal> getRunningGoals()
    {
        return this.goals.stream().filter(PrioritizedGoal::isRunning);
    }

    public void disableFlag(Goal.Flag flag)
    {
        this.disabledFlags.add(flag);
    }

    public void enableFlag(Goal.Flag flag)
    {
        this.disabledFlags.remove(flag);
    }

    public void setFlag(Goal.Flag flag, boolean p_220878_2_)
    {
        if (p_220878_2_)
        {
            this.enableFlag(flag);
        }
        else
        {
            this.disableFlag(flag);
        }
    }
}
