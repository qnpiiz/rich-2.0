package fun.rich.event;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import fun.rich.event.events.Event;
import fun.rich.event.events.EventStoppable;
import fun.rich.event.types.Priority;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventManager {

    private static final Map<Class<? extends Event>, List<MethodData>> REGISTRY_MAP = Maps.newLinkedHashMap();

    public static void register(Object object) {
        Arrays.stream(object.getClass().getDeclaredMethods())
                .filter(method -> !isMethodBad(method))
                .forEach(method -> register(method, object));
    }

    public static void unregister(Object object) {
        for (List<MethodData> dataList : REGISTRY_MAP.values())
            dataList.removeIf(data -> data.getSource().equals(object));

        REGISTRY_MAP.entrySet().removeIf(classListEntry -> classListEntry.getValue().isEmpty());
    }

    private static void register(Method method, Object object) {
        Class<? extends Event> indexClass = (Class<? extends Event>) method.getParameterTypes()[0];
        MethodData data = new MethodData(object, method, method.getAnnotation(EventTarget.class).value());

        boolean accesible = data.getTarget().isAccessible();
        if (!accesible)
            data.getTarget().setAccessible(true);

        if (REGISTRY_MAP.containsKey(indexClass)) {
            if (!REGISTRY_MAP.get(indexClass).contains(data)) {
                REGISTRY_MAP.get(indexClass).add(data);
                sortListValue(indexClass);
            }
        } else
            REGISTRY_MAP.put(indexClass, Lists.newCopyOnWriteArrayList(Collections.singletonList(data)));

        data.getTarget().setAccessible(accesible);
    }

    private static void sortListValue(Class<? extends Event> indexClass) {
        List<MethodData> sortedList = new CopyOnWriteArrayList<>();

        for (byte priority : Priority.VALUE_ARRAY) {
            for (MethodData data : REGISTRY_MAP.get(indexClass))
                if (data.getPriority() == priority)
                    sortedList.add(data);
        }

        REGISTRY_MAP.put(indexClass, sortedList);
    }

    @SneakyThrows
    public static Event call(Event event) {
        List<MethodData> dataList = REGISTRY_MAP.get(event.getClass());

        if (dataList != null) {
            if (event instanceof EventStoppable) {
                EventStoppable stoppable = (EventStoppable) event;

                for (MethodData data : dataList) {
                    data.getTarget().invoke(data.getSource(), event);

                    if (stoppable.isStopped())
                        break;
                }
            } else {
                for (MethodData data : dataList)
                    data.getTarget().invoke(data.getSource(), event);
            }
        }

        return event;
    }

    private static boolean isMethodBad(Method method) {
        return method.getParameterTypes().length != 1 || !method.isAnnotationPresent(EventTarget.class);
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @AllArgsConstructor
    @Getter
    private static final class MethodData {
        Object source;
        Method target;
        byte priority;
    }
}
