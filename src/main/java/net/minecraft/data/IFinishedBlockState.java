package net.minecraft.data;

import com.google.gson.JsonElement;
import java.util.function.Supplier;
import net.minecraft.block.Block;

public interface IFinishedBlockState extends Supplier<JsonElement>
{
    Block func_230524_a_();
}
