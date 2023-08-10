package net.optifine.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.util.PropertiesOrdered;

public class ConfigUtils
{
    public static String readString(String fileName, String property)
    {
        Properties properties = readProperties(fileName);

        if (properties == null)
        {
            return null;
        }
        else
        {
            String s = properties.getProperty(property);

            if (s != null)
            {
                s = s.trim();
            }

            return s;
        }
    }

    public static Properties readProperties(String fileName)
    {
        try
        {
            ResourceLocation resourcelocation = new ResourceLocation(fileName);
            InputStream inputstream = Config.getResourceStream(resourcelocation);

            if (inputstream == null)
            {
                return null;
            }
            else
            {
                Properties properties = new PropertiesOrdered();
                properties.load(inputstream);
                inputstream.close();
                return properties;
            }
        }
        catch (FileNotFoundException filenotfoundexception)
        {
            return null;
        }
        catch (IOException ioexception)
        {
            Config.warn("Error parsing: " + fileName);
            Config.warn(ioexception.getClass().getName() + ": " + ioexception.getMessage());
            return null;
        }
    }
}
