package net.minecraft.client.renderer.debug;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Set;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;

public class VillageSectionsDebugRender implements DebugRenderer.IDebugRenderer
{
    private final Set<SectionPos> field_239375_a_ = Sets.newHashSet();

    VillageSectionsDebugRender()
    {
    }

    public void clear()
    {
        this.field_239375_a_.clear();
    }

    public void func_239378_a_(SectionPos p_239378_1_)
    {
        this.field_239375_a_.add(p_239378_1_);
    }

    public void func_239379_b_(SectionPos p_239379_1_)
    {
        this.field_239375_a_.remove(p_239379_1_);
    }

    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, double camX, double camY, double camZ)
    {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        this.func_239376_a_(camX, camY, camZ);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    private void func_239376_a_(double p_239376_1_, double p_239376_3_, double p_239376_5_)
    {
        BlockPos blockpos = new BlockPos(p_239376_1_, p_239376_3_, p_239376_5_);
        this.field_239375_a_.forEach((p_239377_1_) ->
        {
            if (blockpos.withinDistance(p_239377_1_.getCenter(), 60.0D))
            {
                func_239380_c_(p_239377_1_);
            }
        });
    }

    private static void func_239380_c_(SectionPos p_239380_0_)
    {
        float f = 1.0F;
        BlockPos blockpos = p_239380_0_.getCenter();
        BlockPos blockpos1 = blockpos.add(-1.0D, -1.0D, -1.0D);
        BlockPos blockpos2 = blockpos.add(1.0D, 1.0D, 1.0D);
        DebugRenderer.renderBox(blockpos1, blockpos2, 0.2F, 1.0F, 0.2F, 0.15F);
    }
}
