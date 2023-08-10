package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class ValueObject
{
    public String toString()
    {
        StringBuilder stringbuilder = new StringBuilder("{");

        for (Field field : this.getClass().getFields())
        {
            if (!func_230801_b_(field))
            {
                try
                {
                    stringbuilder.append(func_237702_a_(field)).append("=").append(field.get(this)).append(" ");
                }
                catch (IllegalAccessException illegalaccessexception)
                {
                }
            }
        }

        stringbuilder.deleteCharAt(stringbuilder.length() - 1);
        stringbuilder.append('}');
        return stringbuilder.toString();
    }

    private static String func_237702_a_(Field p_237702_0_)
    {
        SerializedName serializedname = p_237702_0_.getAnnotation(SerializedName.class);
        return serializedname != null ? serializedname.value() : p_237702_0_.getName();
    }

    private static boolean func_230801_b_(Field p_230801_0_)
    {
        return Modifier.isStatic(p_230801_0_.getModifiers());
    }
}
