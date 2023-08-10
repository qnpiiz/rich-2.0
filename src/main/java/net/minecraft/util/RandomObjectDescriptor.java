package net.minecraft.util;

import java.util.Random;
import java.util.UUID;

public class RandomObjectDescriptor
{
    private static final String[] FIRST_PART = new String[] {"Slim", "Far", "River", "Silly", "Fat", "Thin", "Fish", "Bat", "Dark", "Oak", "Sly", "Bush", "Zen", "Bark", "Cry", "Slack", "Soup", "Grim", "Hook", "Dirt", "Mud", "Sad", "Hard", "Crook", "Sneak", "Stink", "Weird", "Fire", "Soot", "Soft", "Rough", "Cling", "Scar"};
    private static final String[] SECOND_PART = new String[] {"Fox", "Tail", "Jaw", "Whisper", "Twig", "Root", "Finder", "Nose", "Brow", "Blade", "Fry", "Seek", "Wart", "Tooth", "Foot", "Leaf", "Stone", "Fall", "Face", "Tongue", "Voice", "Lip", "Mouth", "Snail", "Toe", "Ear", "Hair", "Beard", "Shirt", "Fist"};

    public static String getRandomObjectDescriptor(UUID uuid)
    {
        Random random = getRandomFromUUID(uuid);
        return getRandomString(random, FIRST_PART) + getRandomString(random, SECOND_PART);
    }

    private static String getRandomString(Random rand, String[] strings)
    {
        return Util.getRandomObject(strings, rand);
    }

    private static Random getRandomFromUUID(UUID uuid)
    {
        return new Random((long)(uuid.hashCode() >> 2));
    }
}
