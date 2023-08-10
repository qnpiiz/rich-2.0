package net.optifine.shaders.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.util.math.vector.Vector3i;
import net.optifine.Config;

public class ShaderParser
{
    public static Pattern PATTERN_UNIFORM = Pattern.compile("[\\w\\s(,=)]*uniform\\s+\\w+\\s+(\\w+).*");
    public static Pattern PATTERN_ATTRIBUTE = Pattern.compile("\\s*attribute\\s+\\w+\\s+(\\w+).*");
    public static Pattern PATTERN_CONST_INT = Pattern.compile("\\s*const\\s+int\\s+(\\w+)\\s*=\\s*([-+.\\w]+)\\s*;.*");
    public static Pattern PATTERN_CONST_IVEC3 = Pattern.compile("\\s*const\\s+ivec3\\s+(\\w+)\\s*=\\s*(.+)\\s*;.*");
    public static Pattern PATTERN_CONST_FLOAT = Pattern.compile("\\s*const\\s+float\\s+(\\w+)\\s*=\\s*([-+.\\w]+)\\s*;.*");
    public static Pattern PATTERN_CONST_VEC2 = Pattern.compile("\\s*const\\s+vec2\\s+(\\w+)\\s*=\\s*(.+)\\s*;.*");
    public static Pattern PATTERN_CONST_VEC4 = Pattern.compile("\\s*const\\s+vec4\\s+(\\w+)\\s*=\\s*(.+)\\s*;.*");
    public static Pattern PATTERN_CONST_BOOL = Pattern.compile("\\s*const\\s+bool\\s+(\\w+)\\s*=\\s*(\\w+)\\s*;.*");
    public static Pattern PATTERN_PROPERTY = Pattern.compile("\\s*(/\\*|//)?\\s*([A-Z]+):\\s*([\\w.,]+)\\s*(\\*/.*|\\s*)");
    public static Pattern PATTERN_EXTENSION = Pattern.compile("\\s*#\\s*extension\\s+(\\w+)\\s*:\\s*(\\w+).*");
    public static Pattern PATTERN_LAYOUT = Pattern.compile("\\s*layout\\s*\\((.*)\\)\\s*(\\w+).*");
    public static Pattern PATTERN_DRAW_BUFFERS = Pattern.compile("[0-9N]+");
    public static Pattern PATTERN_RENDER_TARGETS = Pattern.compile("[0-9N,]+");

    public static ShaderLine parseLine(String line)
    {
        Matcher matcher = PATTERN_UNIFORM.matcher(line);

        if (matcher.matches())
        {
            return new ShaderLine(ShaderLine.Type.UNIFORM, matcher.group(1), "", line);
        }
        else
        {
            Matcher matcher1 = PATTERN_ATTRIBUTE.matcher(line);

            if (matcher1.matches())
            {
                return new ShaderLine(ShaderLine.Type.ATTRIBUTE, matcher1.group(1), "", line);
            }
            else
            {
                Matcher matcher2 = PATTERN_CONST_INT.matcher(line);

                if (matcher2.matches())
                {
                    return new ShaderLine(ShaderLine.Type.CONST_INT, matcher2.group(1), matcher2.group(2), line);
                }
                else
                {
                    Matcher matcher3 = PATTERN_CONST_IVEC3.matcher(line);

                    if (matcher3.matches())
                    {
                        return new ShaderLine(ShaderLine.Type.CONST_IVEC3, matcher3.group(1), matcher3.group(2), line);
                    }
                    else
                    {
                        Matcher matcher4 = PATTERN_CONST_FLOAT.matcher(line);

                        if (matcher4.matches())
                        {
                            return new ShaderLine(ShaderLine.Type.CONST_FLOAT, matcher4.group(1), matcher4.group(2), line);
                        }
                        else
                        {
                            Matcher matcher5 = PATTERN_CONST_VEC2.matcher(line);

                            if (matcher5.matches())
                            {
                                return new ShaderLine(ShaderLine.Type.CONST_VEC2, matcher5.group(1), matcher5.group(2), line);
                            }
                            else
                            {
                                Matcher matcher6 = PATTERN_CONST_VEC4.matcher(line);

                                if (matcher6.matches())
                                {
                                    return new ShaderLine(ShaderLine.Type.CONST_VEC4, matcher6.group(1), matcher6.group(2), line);
                                }
                                else
                                {
                                    Matcher matcher7 = PATTERN_CONST_BOOL.matcher(line);

                                    if (matcher7.matches())
                                    {
                                        return new ShaderLine(ShaderLine.Type.CONST_BOOL, matcher7.group(1), matcher7.group(2), line);
                                    }
                                    else
                                    {
                                        Matcher matcher8 = PATTERN_PROPERTY.matcher(line);

                                        if (matcher8.matches())
                                        {
                                            return new ShaderLine(ShaderLine.Type.PROPERTY, matcher8.group(2), matcher8.group(3), line);
                                        }
                                        else
                                        {
                                            Matcher matcher9 = PATTERN_EXTENSION.matcher(line);

                                            if (matcher9.matches())
                                            {
                                                return new ShaderLine(ShaderLine.Type.EXTENSION, matcher9.group(1), matcher9.group(2), line);
                                            }
                                            else
                                            {
                                                Matcher matcher10 = PATTERN_LAYOUT.matcher(line);
                                                return matcher10.matches() ? new ShaderLine(ShaderLine.Type.LAYOUT, matcher10.group(2), matcher10.group(1), line) : null;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static int getIndex(String uniform, String prefix, int minIndex, int maxIndex)
    {
        if (!uniform.startsWith(prefix))
        {
            return -1;
        }
        else
        {
            String s = uniform.substring(prefix.length());
            int i = Config.parseInt(s, -1);
            return i >= minIndex && i <= maxIndex ? i : -1;
        }
    }

    public static int getShadowDepthIndex(String uniform)
    {
        byte b0 = -1;

        switch (uniform.hashCode())
        {
            case -903579360:
                if (uniform.equals("shadow"))
                {
                    b0 = 0;
                }

                break;

            case 1235669239:
                if (uniform.equals("watershadow"))
                {
                    b0 = 1;
                }
        }

        switch (b0)
        {
            case 0:
                return 0;

            case 1:
                return 1;

            default:
                return getIndex(uniform, "shadowtex", 0, 1);
        }
    }

    public static int getShadowColorIndex(String uniform)
    {
        byte b0 = -1;

        switch (uniform.hashCode())
        {
            case -1560188349:
                if (uniform.equals("shadowcolor"))
                {
                    b0 = 0;
                }

            default:
                switch (b0)
                {
                    case 0:
                        return 0;

                    default:
                        return getIndex(uniform, "shadowcolor", 0, 1);
                }
        }
    }

    public static int getShadowColorImageIndex(String uniform)
    {
        return getIndex(uniform, "shadowcolorimg", 0, 1);
    }

    public static int getDepthIndex(String uniform)
    {
        return getIndex(uniform, "depthtex", 0, 2);
    }

    public static int getColorIndex(String uniform)
    {
        int i = getIndex(uniform, "gaux", 1, 4);
        return i > 0 ? i + 3 : getIndex(uniform, "colortex", 0, 15);
    }

    public static int getColorImageIndex(String uniform)
    {
        return getIndex(uniform, "colorimg", 0, 15);
    }

    public static String[] parseDrawBuffers(String str)
    {
        if (!PATTERN_DRAW_BUFFERS.matcher(str).matches())
        {
            return null;
        }
        else
        {
            str = str.trim();
            String[] astring = new String[str.length()];

            for (int i = 0; i < astring.length; ++i)
            {
                astring[i] = String.valueOf(str.charAt(i));
            }

            return astring;
        }
    }

    public static String[] parseRenderTargets(String str)
    {
        if (!PATTERN_RENDER_TARGETS.matcher(str).matches())
        {
            return null;
        }
        else
        {
            str = str.trim();
            return Config.tokenize(str, ",");
        }
    }

    public static Vector3i parseLocalSize(String value)
    {
        int i = 1;
        int j = 1;
        int k = 1;
        String[] astring = Config.tokenize(value, ",");

        for (int l = 0; l < astring.length; ++l)
        {
            String s = astring[l];
            String[] astring1 = Config.tokenize(s, "=");

            if (astring1.length == 2)
            {
                String s1 = astring1[0].trim();
                String s2 = astring1[1].trim();
                int i1 = Config.parseInt(s2, -1);

                if (i1 < 1)
                {
                    return null;
                }

                if (s1.equals("local_size_x"))
                {
                    i = i1;
                }

                if (s1.equals("local_size_y"))
                {
                    j = i1;
                }

                if (s1.equals("local_size_z"))
                {
                    k = i1;
                }
            }
        }

        return i == 1 && j == 1 && k == 1 ? null : new Vector3i(i, j, k);
    }
}
