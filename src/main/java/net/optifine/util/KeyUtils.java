package net.optifine.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;

public class KeyUtils
{
    public static void fixKeyConflicts(KeyBinding[] keys, KeyBinding[] keysPrio)
    {
        Set<String> set = new HashSet<>();

        for (int i = 0; i < keysPrio.length; ++i)
        {
            KeyBinding keybinding = keysPrio[i];
            set.add(keybinding.getTranslationKey());
        }

        Set<KeyBinding> set1 = new HashSet<>(Arrays.asList(keys));
        set1.removeAll(Arrays.asList(keysPrio));

        for (KeyBinding keybinding1 : set1)
        {
            String s = keybinding1.getTranslationKey();

            if (set.contains(s))
            {
                keybinding1.bind(InputMappings.INPUT_INVALID);
            }
        }
    }
}
