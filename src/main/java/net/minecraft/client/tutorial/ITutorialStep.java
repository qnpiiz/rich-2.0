package net.minecraft.client.tutorial;

import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

public interface ITutorialStep
{
default void onStop()
    {
    }

default void tick()
    {
    }

default void handleMovement(MovementInput input)
    {
    }

default void onMouseMove(double velocityX, double velocityY)
    {
    }

default void onMouseHover(ClientWorld worldIn, RayTraceResult result)
    {
    }

default void onHitBlock(ClientWorld worldIn, BlockPos pos, BlockState state, float diggingStage)
    {
    }

default void openInventory()
    {
    }

default void handleSetSlot(ItemStack stack)
    {
    }
}
