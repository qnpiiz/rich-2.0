package net.minecraft.util;

import net.minecraft.util.text.Style;

@FunctionalInterface
public interface ICharacterConsumer
{
    boolean accept(int p_accept_1_, Style p_accept_2_, int p_accept_3_);
}
