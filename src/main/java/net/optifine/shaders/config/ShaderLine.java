package net.optifine.shaders.config;

import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.math.vector.Vector4f;
import net.optifine.Config;
import net.optifine.util.StrUtils;

public class ShaderLine
{
    private ShaderLine.Type type;
    private String name;
    private String value;
    private String line;

    public ShaderLine(ShaderLine.Type type, String name, String value, String line)
    {
        this.type = type;
        this.name = name;
        this.value = value;
        this.line = line;
    }

    public ShaderLine.Type getType()
    {
        return this.type;
    }

    public String getName()
    {
        return this.name;
    }

    public String getValue()
    {
        return this.value;
    }

    public boolean isUniform()
    {
        return this.type == ShaderLine.Type.UNIFORM;
    }

    public boolean isUniform(String name)
    {
        return this.isUniform() && name.equals(this.name);
    }

    public boolean isAttribute()
    {
        return this.type == ShaderLine.Type.ATTRIBUTE;
    }

    public boolean isAttribute(String name)
    {
        return this.isAttribute() && name.equals(this.name);
    }

    public boolean isProperty()
    {
        return this.type == ShaderLine.Type.PROPERTY;
    }

    public boolean isConstInt()
    {
        return this.type == ShaderLine.Type.CONST_INT;
    }

    public boolean isConstFloat()
    {
        return this.type == ShaderLine.Type.CONST_FLOAT;
    }

    public boolean isConstBool()
    {
        return this.type == ShaderLine.Type.CONST_BOOL;
    }

    public boolean isExtension()
    {
        return this.type == ShaderLine.Type.EXTENSION;
    }

    public boolean isConstVec2()
    {
        return this.type == ShaderLine.Type.CONST_VEC2;
    }

    public boolean isConstVec4()
    {
        return this.type == ShaderLine.Type.CONST_VEC4;
    }

    public boolean isConstIVec3()
    {
        return this.type == ShaderLine.Type.CONST_IVEC3;
    }

    public boolean isLayout()
    {
        return this.type == ShaderLine.Type.LAYOUT;
    }

    public boolean isLayout(String name)
    {
        return this.isLayout() && name.equals(this.name);
    }

    public boolean isProperty(String name)
    {
        return this.isProperty() && name.equals(this.name);
    }

    public boolean isProperty(String name, String value)
    {
        return this.isProperty(name) && value.equals(this.value);
    }

    public boolean isConstInt(String name)
    {
        return this.isConstInt() && name.equals(this.name);
    }

    public boolean isConstIntSuffix(String suffix)
    {
        return this.isConstInt() && this.name.endsWith(suffix);
    }

    public boolean isConstIVec3(String name)
    {
        return this.isConstIVec3() && name.equals(this.name);
    }

    public boolean isConstFloat(String name)
    {
        return this.isConstFloat() && name.equals(this.name);
    }

    public boolean isConstBool(String name)
    {
        return this.isConstBool() && name.equals(this.name);
    }

    public boolean isExtension(String name)
    {
        return this.isExtension() && name.equals(this.name);
    }

    public boolean isConstBoolSuffix(String suffix)
    {
        return this.isConstBool() && this.name.endsWith(suffix);
    }

    public boolean isConstBoolSuffix(String suffix, boolean val)
    {
        return this.isConstBoolSuffix(suffix) && this.getValueBool() == val;
    }

    public boolean isConstBool(String name1, String name2)
    {
        return this.isConstBool(name1) || this.isConstBool(name2);
    }

    public boolean isConstBool(String name1, String name2, String name3)
    {
        return this.isConstBool(name1) || this.isConstBool(name2) || this.isConstBool(name3);
    }

    public boolean isConstBool(String name, boolean val)
    {
        return this.isConstBool(name) && this.getValueBool() == val;
    }

    public boolean isConstBool(String name1, String name2, boolean val)
    {
        return this.isConstBool(name1, name2) && this.getValueBool() == val;
    }

    public boolean isConstBool(String name1, String name2, String name3, boolean val)
    {
        return this.isConstBool(name1, name2, name3) && this.getValueBool() == val;
    }

    public boolean isConstVec2(String name)
    {
        return this.isConstVec2() && name.equals(this.name);
    }

    public boolean isConstVec4Suffix(String suffix)
    {
        return this.isConstVec4() && this.name.endsWith(suffix);
    }

    public int getValueInt()
    {
        try
        {
            return Integer.parseInt(this.value);
        }
        catch (NumberFormatException numberformatexception)
        {
            throw new NumberFormatException("Invalid integer: " + this.value + ", line: " + this.line);
        }
    }

    public float getValueFloat()
    {
        try
        {
            return Float.parseFloat(this.value);
        }
        catch (NumberFormatException numberformatexception)
        {
            throw new NumberFormatException("Invalid float: " + this.value + ", line: " + this.line);
        }
    }

    public Vector3i getValueIVec3()
    {
        if (this.value == null)
        {
            return null;
        }
        else
        {
            String s = this.value.trim();
            s = StrUtils.removePrefix(s, "ivec3");
            s = StrUtils.trim(s, " ()");
            String[] astring = Config.tokenize(s, ", ");

            if (astring.length != 3)
            {
                return null;
            }
            else
            {
                int[] aint = new int[3];

                for (int i = 0; i < astring.length; ++i)
                {
                    String s1 = astring[i];
                    int j = Config.parseInt(s1, Integer.MIN_VALUE);

                    if (j == Integer.MIN_VALUE)
                    {
                        return null;
                    }

                    aint[i] = j;
                }

                return new Vector3i(aint[0], aint[1], aint[2]);
            }
        }
    }

    public Vector2f getValueVec2()
    {
        if (this.value == null)
        {
            return null;
        }
        else
        {
            String s = this.value.trim();
            s = StrUtils.removePrefix(s, "vec2");
            s = StrUtils.trim(s, " ()");
            String[] astring = Config.tokenize(s, ", ");

            if (astring.length != 2)
            {
                return null;
            }
            else
            {
                float[] afloat = new float[2];

                for (int i = 0; i < astring.length; ++i)
                {
                    String s1 = astring[i];
                    s1 = StrUtils.removeSuffix(s1, new String[] {"F", "f"});
                    float f = Config.parseFloat(s1, Float.MAX_VALUE);

                    if (f == Float.MAX_VALUE)
                    {
                        return null;
                    }

                    afloat[i] = f;
                }

                return new Vector2f(afloat[0], afloat[1]);
            }
        }
    }

    public Vector4f getValueVec4()
    {
        if (this.value == null)
        {
            return null;
        }
        else
        {
            String s = this.value.trim();
            s = StrUtils.removePrefix(s, "vec4");
            s = StrUtils.trim(s, " ()");
            String[] astring = Config.tokenize(s, ", ");

            if (astring.length != 4)
            {
                return null;
            }
            else
            {
                float[] afloat = new float[4];

                for (int i = 0; i < astring.length; ++i)
                {
                    String s1 = astring[i];
                    s1 = StrUtils.removeSuffix(s1, new String[] {"F", "f"});
                    float f = Config.parseFloat(s1, Float.MAX_VALUE);

                    if (f == Float.MAX_VALUE)
                    {
                        return null;
                    }

                    afloat[i] = f;
                }

                return new Vector4f(afloat[0], afloat[1], afloat[2], afloat[3]);
            }
        }
    }

    public boolean getValueBool()
    {
        String s = this.value.toLowerCase();

        if (!s.equals("true") && !s.equals("false"))
        {
            throw new RuntimeException("Invalid boolean: " + this.value + ", line: " + this.line);
        }
        else
        {
            return Boolean.valueOf(this.value);
        }
    }

    public String toString()
    {
        return "" + this.line;
    }

    public static enum Type
    {
        UNIFORM,
        ATTRIBUTE,
        CONST_INT,
        CONST_IVEC3,
        CONST_FLOAT,
        CONST_VEC2,
        CONST_VEC4,
        CONST_BOOL,
        PROPERTY,
        EXTENSION,
        LAYOUT;
    }
}
