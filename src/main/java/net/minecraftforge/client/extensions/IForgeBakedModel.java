package net.minecraftforge.client.extensions;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.IModelData;
import net.optifine.reflect.Reflector;

public interface IForgeBakedModel
{
default IBakedModel getBakedModel()
    {
        return (IBakedModel)this;
    }

default List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData)
    {
        return this.getBakedModel().getQuads(state, side, rand);
    }

default boolean isAmbientOcclusion(BlockState state)
    {
        return this.getBakedModel().isAmbientOcclusion();
    }

default IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat)
    {
        return (IBakedModel)Reflector.ForgeHooksClient_handlePerspective.call(this.getBakedModel(), cameraTransformType, mat);
    }

default IModelData getModelData(IBlockDisplayReader world, BlockPos pos, BlockState state, IModelData tileData)
    {
        return tileData;
    }

default TextureAtlasSprite getParticleTexture(IModelData data)
    {
        return this.getBakedModel().getParticleTexture();
    }

default boolean isLayered()
    {
        return false;
    }
}
