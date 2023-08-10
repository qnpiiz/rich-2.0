package net.optifine;

import java.util.Comparator;

public class CustomItemsComparator implements Comparator
{
    public int compare(Object o1, Object o2)
    {
        CustomItemProperties customitemproperties = (CustomItemProperties)o1;
        CustomItemProperties customitemproperties1 = (CustomItemProperties)o2;

        if (customitemproperties.weight != customitemproperties1.weight)
        {
            return customitemproperties1.weight - customitemproperties.weight;
        }
        else
        {
            return !Config.equals(customitemproperties.basePath, customitemproperties1.basePath) ? customitemproperties.basePath.compareTo(customitemproperties1.basePath) : customitemproperties.name.compareTo(customitemproperties1.name);
        }
    }
}
