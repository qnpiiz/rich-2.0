package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.fluid.FluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class WaterDebugRenderer implements DebugRenderer.IDebugRenderer
{
    private final Minecraft minecraft;

    public WaterDebugRenderer(Minecraft minecraftIn)
    {
        this.minecraft = minecraftIn;
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, double camX, double camY, double camZ)
    {
        BlockPos blockpos = this.minecraft.player.getPosition();
        IWorldReader iworldreader = this.minecraft.player.world;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(0.0F, 1.0F, 0.0F, 0.75F);
        RenderSystem.disableTexture();
        RenderSystem.lineWidth(6.0F);

        for (BlockPos blockpos1 : BlockPos.getAllInBoxMutable(blockpos.add(-10, -10, -10), blockpos.add(10, 10, 10)))
        {
            FluidState fluidstate = iworldreader.getFluidState(blockpos1);

            if (fluidstate.isTagged(FluidTags.WATER))
            {
                double d0 = (double)((float)blockpos1.getY() + fluidstate.getActualHeight(iworldreader, blockpos1));
                DebugRenderer.renderBox((new AxisAlignedBB((double)((float)blockpos1.getX() + 0.01F), (double)((float)blockpos1.getY() + 0.01F), (double)((float)blockpos1.getZ() + 0.01F), (double)((float)blockpos1.getX() + 0.99F), d0, (double)((float)blockpos1.getZ() + 0.99F))).offset(-camX, -camY, -camZ), 1.0F, 1.0F, 1.0F, 0.2F);
            }
        }

        for (BlockPos blockpos2 : BlockPos.getAllInBoxMutable(blockpos.add(-10, -10, -10), blockpos.add(10, 10, 10)))
        {
            FluidState fluidstate1 = iworldreader.getFluidState(blockpos2);

            if (fluidstate1.isTagged(FluidTags.WATER))
            {
                DebugRenderer.renderText(String.valueOf(fluidstate1.getLevel()), (double)blockpos2.getX() + 0.5D, (double)((float)blockpos2.getY() + fluidstate1.getActualHeight(iworldreader, blockpos2)), (double)blockpos2.getZ() + 0.5D, -16777216);
            }
        }

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}
