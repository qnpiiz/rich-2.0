package net.minecraft.util.text;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;

public interface ITargetedTextComponent
{
    IFormattableTextComponent func_230535_a_(@Nullable CommandSource p_230535_1_, @Nullable Entity p_230535_2_, int p_230535_3_) throws CommandSyntaxException;
}
