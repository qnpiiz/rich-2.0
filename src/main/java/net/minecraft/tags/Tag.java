package net.minecraft.tags;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;

public class Tag<T> implements ITag<T>
{
    private final ImmutableList<T> immutableContents;
    private final Set<T> contents;
    @VisibleForTesting
    protected final Class<?> contentsClassType;

    protected Tag(Set<T> contents, Class<?> contentsClassType)
    {
        this.contentsClassType = contentsClassType;
        this.contents = contents;
        this.immutableContents = ImmutableList.copyOf(contents);
    }

    public static <T> Tag<T> getEmptyTag()
    {
        return new Tag<>(ImmutableSet.of(), Void.class);
    }

    public static <T> Tag<T> getTagFromContents(Set<T> contents)
    {
        return new Tag<>(contents, getContentsClass(contents));
    }

    public boolean contains(T element)
    {
        return this.contentsClassType.isInstance(element) && this.contents.contains(element);
    }

    public List<T> getAllElements()
    {
        return this.immutableContents;
    }

    private static <T> Class<?> getContentsClass(Set<T> contents)
    {
        if (contents.isEmpty())
        {
            return Void.class;
        }
        else
        {
            Class<?> oclass = null;

            for (T t : contents)
            {
                if (oclass == null)
                {
                    oclass = t.getClass();
                }
                else
                {
                    oclass = findCommonParentClass(oclass, t.getClass());
                }
            }

            return oclass;
        }
    }

    private static Class<?> findCommonParentClass(Class<?> input, Class<?> comparison)
    {
        while (!input.isAssignableFrom(comparison))
        {
            input = input.getSuperclass();
        }

        return input;
    }
}
