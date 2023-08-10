package net.minecraft.command.arguments;

import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

public interface ILocationArgument
{
    Vector3d getPosition(CommandSource source);

    Vector2f getRotation(CommandSource source);

default BlockPos getBlockPos(CommandSource source)
    {
        return new BlockPos(this.getPosition(source));
    }

    boolean isXRelative();

    boolean isYRelative();

    boolean isZRelative();
}
