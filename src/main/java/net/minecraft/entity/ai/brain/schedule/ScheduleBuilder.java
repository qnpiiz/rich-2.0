package net.minecraft.entity.ai.brain.schedule;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Collectors;

public class ScheduleBuilder
{
    private final Schedule schedule;
    private final List<ScheduleBuilder.ActivityEntry> entries = Lists.newArrayList();

    public ScheduleBuilder(Schedule schedule)
    {
        this.schedule = schedule;
    }

    public ScheduleBuilder add(int duration, Activity activityIn)
    {
        this.entries.add(new ScheduleBuilder.ActivityEntry(duration, activityIn));
        return this;
    }

    public Schedule build()
    {
        this.entries.stream().map(ScheduleBuilder.ActivityEntry::getActivity).collect(Collectors.toSet()).forEach(this.schedule::createDutiesFor);
        this.entries.forEach((activityEntry) ->
        {
            Activity activity = activityEntry.getActivity();
            this.schedule.getAllDutiesExcept(activity).forEach((scheduleDuties) -> {
                scheduleDuties.addDutyTime(activityEntry.getDuration(), 0.0F);
            });
            this.schedule.getDutiesFor(activity).addDutyTime(activityEntry.getDuration(), 1.0F);
        });
        return this.schedule;
    }

    static class ActivityEntry
    {
        private final int duration;
        private final Activity activity;

        public ActivityEntry(int durationIn, Activity activityIn)
        {
            this.duration = durationIn;
            this.activity = activityIn;
        }

        public int getDuration()
        {
            return this.duration;
        }

        public Activity getActivity()
        {
            return this.activity;
        }
    }
}
