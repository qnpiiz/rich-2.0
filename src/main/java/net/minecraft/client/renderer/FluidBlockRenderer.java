package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BreakableBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.properties.SlabType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.IBlockReader;
import net.optifine.Config;
import net.optifine.CustomColors;
import net.optifine.reflect.Reflector;
import net.optifine.render.RenderEnv;
import net.optifine.shaders.SVertexBuilder;
import net.optifine.shaders.Shaders;

public class FluidBlockRenderer
{
    private final TextureAtlasSprite[] atlasSpritesLava = new TextureAtlasSprite[2];
    private final TextureAtlasSprite[] atlasSpritesWater = new TextureAtlasSprite[2];
    private TextureAtlasSprite atlasSpriteWaterOverlay;

    protected void initAtlasSprites()
    {
        this.atlasSpritesLava[0] = Minecraft.getInstance().getModelManager().getBlockModelShapes().getModel(Blocks.LAVA.getDefaultState()).getParticleTexture();
        this.atlasSpritesLava[1] = ModelBakery.LOCATION_LAVA_FLOW.getSprite();
        this.atlasSpritesWater[0] = Minecraft.getInstance().getModelManager().getBlockModelShapes().getModel(Blocks.WATER.getDefaultState()).getParticleTexture();
        this.atlasSpritesWater[1] = ModelBakery.LOCATION_WATER_FLOW.getSprite();
        this.atlasSpriteWaterOverlay = ModelBakery.LOCATION_WATER_OVERLAY.getSprite();
    }

    private static boolean isAdjacentFluidSameAs(IBlockReader worldIn, BlockPos pos, Direction side, FluidState state)
    {
        BlockPos blockpos = pos.offset(side);
        FluidState fluidstate = worldIn.getFluidState(blockpos);
        return fluidstate.getFluid().isEquivalentTo(state.getFluid());
    }

    private static boolean func_239284_a_(IBlockReader p_239284_0_, Direction p_239284_1_, float p_239284_2_, BlockPos p_239284_3_, BlockState p_239284_4_)
    {
        if (p_239284_4_.isSolid())
        {
            VoxelShape voxelshape = VoxelShapes.create(0.0D, 0.0D, 0.0D, 1.0D, (double)p_239284_2_, 1.0D);
            VoxelShape voxelshape1 = p_239284_4_.getRenderShapeTrue(p_239284_0_, p_239284_3_);
            return VoxelShapes.isCubeSideCovered(voxelshape, voxelshape1, p_239284_1_);
        }
        else
        {
            return false;
        }
    }

    private static boolean func_239283_a_(IBlockReader p_239283_0_, BlockPos p_239283_1_, Direction p_239283_2_, float p_239283_3_)
    {
        BlockPos blockpos = p_239283_1_.offset(p_239283_2_);
        BlockState blockstate = p_239283_0_.getBlockState(blockpos);
        return func_239284_a_(p_239283_0_, p_239283_2_, p_239283_3_, blockpos, blockstate);
    }

    private static boolean func_239282_a_(IBlockReader p_239282_0_, BlockPos p_239282_1_, BlockState p_239282_2_, Direction p_239282_3_)
    {
        return func_239284_a_(p_239282_0_, p_239282_3_.getOpposite(), 1.0F, p_239282_1_, p_239282_2_);
    }

    public static boolean func_239281_a_(IBlockDisplayReader p_239281_0_, BlockPos p_239281_1_, FluidState p_239281_2_, BlockState p_239281_3_, Direction p_239281_4_)
    {
        return !func_239282_a_(p_239281_0_, p_239281_1_, p_239281_3_, p_239281_4_) && !isAdjacentFluidSameAs(p_239281_0_, p_239281_1_, p_239281_4_, p_239281_2_);
    }

    public boolean render(IBlockDisplayReader lightReaderIn, BlockPos posIn, IVertexBuilder vertexBuilderIn, FluidState fluidStateIn)
    {
        BlockState blockstate = fluidStateIn.getBlockState();
        boolean flag7;

        try
        {
            if (Config.isShaders())
            {
                SVertexBuilder.pushEntity(blockstate, vertexBuilderIn);
            }

            boolean flag = fluidStateIn.isTagged(FluidTags.LAVA);
            TextureAtlasSprite[] atextureatlassprite = flag ? this.atlasSpritesLava : this.atlasSpritesWater;
            BlockState blockstate1 = lightReaderIn.getBlockState(posIn);

            if (Reflector.ForgeHooksClient_getFluidSprites.exists())
            {
                TextureAtlasSprite[] atextureatlassprite1 = (TextureAtlasSprite[])Reflector.call(Reflector.ForgeHooksClient_getFluidSprites, lightReaderIn, posIn, fluidStateIn);

                if (atextureatlassprite1 != null)
                {
                    atextureatlassprite = atextureatlassprite1;
                }
            }

            RenderEnv renderenv = vertexBuilderIn.getRenderEnv(blockstate, posIn);
            int i = -1;
            float f = 1.0F;

            if (Reflector.IForgeFluid_getAttributes.exists())
            {
                Object object = Reflector.call(fluidStateIn.getFluid(), Reflector.IForgeFluid_getAttributes);

                if (object != null && Reflector.FluidAttributes_getColor.exists())
                {
                    i = Reflector.callInt(object, Reflector.FluidAttributes_getColor, lightReaderIn, posIn);
                    f = (float)(i >> 24 & 255) / 255.0F;
                }
            }

            boolean flag9 = !isAdjacentFluidSameAs(lightReaderIn, posIn, Direction.UP, fluidStateIn);
            boolean flag1 = func_239281_a_(lightReaderIn, posIn, fluidStateIn, blockstate1, Direction.DOWN) && !func_239283_a_(lightReaderIn, posIn, Direction.DOWN, 0.8888889F);
            boolean flag2 = func_239281_a_(lightReaderIn, posIn, fluidStateIn, blockstate1, Direction.NORTH);
            boolean flag3 = func_239281_a_(lightReaderIn, posIn, fluidStateIn, blockstate1, Direction.SOUTH);
            boolean flag4 = func_239281_a_(lightReaderIn, posIn, fluidStateIn, blockstate1, Direction.WEST);
            boolean flag5 = func_239281_a_(lightReaderIn, posIn, fluidStateIn, blockstate1, Direction.EAST);

            if (flag9 || flag1 || flag5 || flag4 || flag2 || flag3)
            {
                if (i < 0)
                {
                    i = CustomColors.getFluidColor(lightReaderIn, blockstate, posIn, renderenv);
                }

                float f28 = (float)(i >> 16 & 255) / 255.0F;
                float f1 = (float)(i >> 8 & 255) / 255.0F;
                float f2 = (float)(i & 255) / 255.0F;
                flag7 = false;
                float f3 = lightReaderIn.func_230487_a_(Direction.DOWN, true);
                float f4 = lightReaderIn.func_230487_a_(Direction.UP, true);
                float f5 = lightReaderIn.func_230487_a_(Direction.NORTH, true);
                float f6 = lightReaderIn.func_230487_a_(Direction.WEST, true);
                float f7 = this.getFluidHeight(lightReaderIn, posIn, fluidStateIn.getFluid());
                float f8 = this.getFluidHeight(lightReaderIn, posIn.south(), fluidStateIn.getFluid());
                float f9 = this.getFluidHeight(lightReaderIn, posIn.east().south(), fluidStateIn.getFluid());
                float f10 = this.getFluidHeight(lightReaderIn, posIn.east(), fluidStateIn.getFluid());
                double d0 = (double)(posIn.getX() & 15);
                double d1 = (double)(posIn.getY() & 15);
                double d2 = (double)(posIn.getZ() & 15);

                if (Config.isRenderRegions())
                {
                    int j = posIn.getX() >> 4 << 4;
                    int k = posIn.getY() >> 4 << 4;
                    int l = posIn.getZ() >> 4 << 4;
                    int i1 = 8;
                    int j1 = j >> i1 << i1;
                    int k1 = l >> i1 << i1;
                    int l1 = j - j1;
                    int i2 = l - k1;
                    d0 += (double)l1;
                    d1 += (double)k;
                    d2 += (double)i2;
                }

                if (Config.isShaders() && Shaders.useMidBlockAttrib)
                {
                    vertexBuilderIn.setMidBlock((float)(d0 + 0.5D), (float)(d1 + 0.5D), (float)(d2 + 0.5D));
                }

                float f29 = 0.001F;
                float f30 = flag1 ? 0.001F : 0.0F;

                if (flag9 && !func_239283_a_(lightReaderIn, posIn, Direction.UP, Math.min(Math.min(f7, f8), Math.min(f9, f10))))
                {
                    flag7 = true;
                    f7 -= 0.001F;
                    f8 -= 0.001F;
                    f9 -= 0.001F;
                    f10 -= 0.001F;
                    Vector3d vector3d = fluidStateIn.getFlow(lightReaderIn, posIn);
                    float f11;
                    float f12;
                    float f13;
                    float f32;
                    float f35;
                    float f38;
                    float f40;
                    float f42;

                    if (vector3d.x == 0.0D && vector3d.z == 0.0D)
                    {
                        TextureAtlasSprite textureatlassprite1 = atextureatlassprite[0];
                        vertexBuilderIn.setSprite(textureatlassprite1);
                        f32 = textureatlassprite1.getInterpolatedU(0.0D);
                        f11 = textureatlassprite1.getInterpolatedV(0.0D);
                        f35 = f32;
                        f42 = textureatlassprite1.getInterpolatedV(16.0D);
                        f38 = textureatlassprite1.getInterpolatedU(16.0D);
                        f12 = f42;
                        f40 = f38;
                        f13 = f11;
                    }
                    else
                    {
                        TextureAtlasSprite textureatlassprite = atextureatlassprite[1];
                        vertexBuilderIn.setSprite(textureatlassprite);
                        float f14 = (float)MathHelper.atan2(vector3d.z, vector3d.x) - ((float)Math.PI / 2F);
                        float f15 = MathHelper.sin(f14) * 0.25F;
                        float f16 = MathHelper.cos(f14) * 0.25F;
                        float f17 = 8.0F;
                        f32 = textureatlassprite.getInterpolatedU((double)(8.0F + (-f16 - f15) * 16.0F));
                        f11 = textureatlassprite.getInterpolatedV((double)(8.0F + (-f16 + f15) * 16.0F));
                        f35 = textureatlassprite.getInterpolatedU((double)(8.0F + (-f16 + f15) * 16.0F));
                        f42 = textureatlassprite.getInterpolatedV((double)(8.0F + (f16 + f15) * 16.0F));
                        f38 = textureatlassprite.getInterpolatedU((double)(8.0F + (f16 + f15) * 16.0F));
                        f12 = textureatlassprite.getInterpolatedV((double)(8.0F + (f16 - f15) * 16.0F));
                        f40 = textureatlassprite.getInterpolatedU((double)(8.0F + (f16 - f15) * 16.0F));
                        f13 = textureatlassprite.getInterpolatedV((double)(8.0F + (-f16 - f15) * 16.0F));
                    }

                    float f46 = (f32 + f35 + f38 + f40) / 4.0F;
                    float f47 = (f11 + f42 + f12 + f13) / 4.0F;
                    float f48 = (float)atextureatlassprite[0].getWidth() / (atextureatlassprite[0].getMaxU() - atextureatlassprite[0].getMinU());
                    float f49 = (float)atextureatlassprite[0].getHeight() / (atextureatlassprite[0].getMaxV() - atextureatlassprite[0].getMinV());
                    float f50 = 4.0F / Math.max(f49, f48);
                    f32 = MathHelper.lerp(f50, f32, f46);
                    f35 = MathHelper.lerp(f50, f35, f46);
                    f38 = MathHelper.lerp(f50, f38, f46);
                    f40 = MathHelper.lerp(f50, f40, f46);
                    f11 = MathHelper.lerp(f50, f11, f47);
                    f42 = MathHelper.lerp(f50, f42, f47);
                    f12 = MathHelper.lerp(f50, f12, f47);
                    f13 = MathHelper.lerp(f50, f13, f47);
                    int j2 = this.getCombinedAverageLight(lightReaderIn, posIn);
                    float f18 = f4 * f28;
                    float f19 = f4 * f1;
                    float f20 = f4 * f2;
                    this.vertexVanilla(vertexBuilderIn, d0 + 0.0D, d1 + (double)f7, d2 + 0.0D, f18, f19, f20, f, f32, f11, j2);
                    this.vertexVanilla(vertexBuilderIn, d0 + 0.0D, d1 + (double)f8, d2 + 1.0D, f18, f19, f20, f, f35, f42, j2);
                    this.vertexVanilla(vertexBuilderIn, d0 + 1.0D, d1 + (double)f9, d2 + 1.0D, f18, f19, f20, f, f38, f12, j2);
                    this.vertexVanilla(vertexBuilderIn, d0 + 1.0D, d1 + (double)f10, d2 + 0.0D, f18, f19, f20, f, f40, f13, j2);

                    if (fluidStateIn.shouldRenderSides(lightReaderIn, posIn.up()))
                    {
                        this.vertexVanilla(vertexBuilderIn, d0 + 0.0D, d1 + (double)f7, d2 + 0.0D, f18, f19, f20, f, f32, f11, j2);
                        this.vertexVanilla(vertexBuilderIn, d0 + 1.0D, d1 + (double)f10, d2 + 0.0D, f18, f19, f20, f, f40, f13, j2);
                        this.vertexVanilla(vertexBuilderIn, d0 + 1.0D, d1 + (double)f9, d2 + 1.0D, f18, f19, f20, f, f38, f12, j2);
                        this.vertexVanilla(vertexBuilderIn, d0 + 0.0D, d1 + (double)f8, d2 + 1.0D, f18, f19, f20, f, f35, f42, j2);
                    }
                }

                if (flag1)
                {
                    vertexBuilderIn.setSprite(atextureatlassprite[0]);
                    float f31 = atextureatlassprite[0].getMinU();
                    float f33 = atextureatlassprite[0].getMaxU();
                    float f36 = atextureatlassprite[0].getMinV();
                    float f39 = atextureatlassprite[0].getMaxV();
                    int i3 = this.getCombinedAverageLight(lightReaderIn, posIn.down());
                    float f41 = lightReaderIn.func_230487_a_(Direction.DOWN, true);
                    float f43 = f41 * f28;
                    float f44 = f41 * f1;
                    float f45 = f41 * f2;
                    this.vertexVanilla(vertexBuilderIn, d0, d1 + (double)f30, d2 + 1.0D, f43, f44, f45, f, f31, f39, i3);
                    this.vertexVanilla(vertexBuilderIn, d0, d1 + (double)f30, d2, f43, f44, f45, f, f31, f36, i3);
                    this.vertexVanilla(vertexBuilderIn, d0 + 1.0D, d1 + (double)f30, d2, f43, f44, f45, f, f33, f36, i3);
                    this.vertexVanilla(vertexBuilderIn, d0 + 1.0D, d1 + (double)f30, d2 + 1.0D, f43, f44, f45, f, f33, f39, i3);
                    flag7 = true;
                }

                for (int l2 = 0; l2 < 4; ++l2)
                {
                    float f34;
                    float f37;
                    double d3;
                    double d4;
                    double d5;
                    double d6;
                    Direction direction;
                    boolean flag10;

                    if (l2 == 0)
                    {
                        f34 = f7;
                        f37 = f10;
                        d3 = d0;
                        d5 = d0 + 1.0D;
                        d4 = d2 + (double)0.001F;
                        d6 = d2 + (double)0.001F;
                        direction = Direction.NORTH;
                        flag10 = flag2;
                    }
                    else if (l2 == 1)
                    {
                        f34 = f9;
                        f37 = f8;
                        d3 = d0 + 1.0D;
                        d5 = d0;
                        d4 = d2 + 1.0D - (double)0.001F;
                        d6 = d2 + 1.0D - (double)0.001F;
                        direction = Direction.SOUTH;
                        flag10 = flag3;
                    }
                    else if (l2 == 2)
                    {
                        f34 = f8;
                        f37 = f7;
                        d3 = d0 + (double)0.001F;
                        d5 = d0 + (double)0.001F;
                        d4 = d2 + 1.0D;
                        d6 = d2;
                        direction = Direction.WEST;
                        flag10 = flag4;
                    }
                    else
                    {
                        f34 = f10;
                        f37 = f9;
                        d3 = d0 + 1.0D - (double)0.001F;
                        d5 = d0 + 1.0D - (double)0.001F;
                        d4 = d2;
                        d6 = d2 + 1.0D;
                        direction = Direction.EAST;
                        flag10 = flag5;
                    }

                    if (flag10 && !func_239283_a_(lightReaderIn, posIn, direction, Math.max(f34, f37)))
                    {
                        flag7 = true;
                        BlockPos blockpos = posIn.offset(direction);
                        TextureAtlasSprite textureatlassprite2 = atextureatlassprite[1];
                        float f51 = 0.0F;
                        float f52 = 0.0F;
                        boolean flag11 = !flag;

                        if (Reflector.IForgeBlockState_shouldDisplayFluidOverlay.exists())
                        {
                            flag11 = atextureatlassprite[2] != null;
                        }

                        if (flag11)
                        {
                            BlockState blockstate2 = lightReaderIn.getBlockState(blockpos);
                            Block block = blockstate2.getBlock();
                            boolean flag8 = false;

                            if (Reflector.IForgeBlockState_shouldDisplayFluidOverlay.exists())
                            {
                                flag8 = Reflector.callBoolean(blockstate2, Reflector.IForgeBlockState_shouldDisplayFluidOverlay, lightReaderIn, blockpos, fluidStateIn);
                            }

                            if (flag8 || block instanceof BreakableBlock || block instanceof LeavesBlock || block == Blocks.BEACON)
                            {
                                textureatlassprite2 = this.atlasSpriteWaterOverlay;
                            }

                            if (block == Blocks.FARMLAND || block == Blocks.GRASS_PATH)
                            {
                                f51 = 0.9375F;
                                f52 = 0.9375F;
                            }

                            if (block instanceof SlabBlock)
                            {
                                SlabBlock slabblock = (SlabBlock)block;

                                if (blockstate2.get(SlabBlock.TYPE) == SlabType.BOTTOM)
                                {
                                    f51 = 0.5F;
                                    f52 = 0.5F;
                                }
                            }
                        }

                        vertexBuilderIn.setSprite(textureatlassprite2);

                        if (!(f34 <= f51) || !(f37 <= f52))
                        {
                            f51 = Math.min(f51, f34);
                            f52 = Math.min(f52, f37);

                            if (f51 > f29)
                            {
                                f51 -= f29;
                            }

                            if (f52 > f29)
                            {
                                f52 -= f29;
                            }

                            float f53 = textureatlassprite2.getInterpolatedV((double)((1.0F - f51) * 16.0F * 0.5F));
                            float f54 = textureatlassprite2.getInterpolatedV((double)((1.0F - f52) * 16.0F * 0.5F));
                            float f55 = textureatlassprite2.getInterpolatedU(0.0D);
                            float f56 = textureatlassprite2.getInterpolatedU(8.0D);
                            float f21 = textureatlassprite2.getInterpolatedV((double)((1.0F - f34) * 16.0F * 0.5F));
                            float f22 = textureatlassprite2.getInterpolatedV((double)((1.0F - f37) * 16.0F * 0.5F));
                            float f23 = textureatlassprite2.getInterpolatedV(8.0D);
                            int k2 = this.getCombinedAverageLight(lightReaderIn, blockpos);
                            float f24 = l2 < 2 ? lightReaderIn.func_230487_a_(Direction.NORTH, true) : lightReaderIn.func_230487_a_(Direction.WEST, true);
                            float f25 = 1.0F * f24 * f28;
                            float f26 = 1.0F * f24 * f1;
                            float f27 = 1.0F * f24 * f2;
                            this.vertexVanilla(vertexBuilderIn, d3, d1 + (double)f34, d4, f25, f26, f27, f, f55, f21, k2);
                            this.vertexVanilla(vertexBuilderIn, d5, d1 + (double)f37, d6, f25, f26, f27, f, f56, f22, k2);
                            this.vertexVanilla(vertexBuilderIn, d5, d1 + (double)f30, d6, f25, f26, f27, f, f56, f54, k2);
                            this.vertexVanilla(vertexBuilderIn, d3, d1 + (double)f30, d4, f25, f26, f27, f, f55, f53, k2);

                            if (textureatlassprite2 != this.atlasSpriteWaterOverlay)
                            {
                                this.vertexVanilla(vertexBuilderIn, d3, d1 + (double)f30, d4, f25, f26, f27, f, f55, f53, k2);
                                this.vertexVanilla(vertexBuilderIn, d5, d1 + (double)f30, d6, f25, f26, f27, f, f56, f54, k2);
                                this.vertexVanilla(vertexBuilderIn, d5, d1 + (double)f37, d6, f25, f26, f27, f, f56, f22, k2);
                                this.vertexVanilla(vertexBuilderIn, d3, d1 + (double)f34, d4, f25, f26, f27, f, f55, f21, k2);
                            }
                        }
                    }
                }

                vertexBuilderIn.setSprite((TextureAtlasSprite)null);
                return flag7;
            }

            flag7 = false;
        }
        finally
        {
            if (Config.isShaders())
            {
                SVertexBuilder.popEntity(vertexBuilderIn);
            }
        }

        return flag7;
    }

    private void vertexVanilla(IVertexBuilder vertexBuilderIn, double x, double y, double z, float red, float green, float blue, float u, float v, int packedLight)
    {
        vertexBuilderIn.pos(x, y, z).color(red, green, blue, 1.0F).tex(u, v).lightmap(packedLight).normal(0.0F, 1.0F, 0.0F).endVertex();
    }

    private void vertexVanilla(IVertexBuilder p_vertexVanilla_1_, double p_vertexVanilla_2_, double p_vertexVanilla_4_, double p_vertexVanilla_6_, float p_vertexVanilla_8_, float p_vertexVanilla_9_, float p_vertexVanilla_10_, float p_vertexVanilla_11_, float p_vertexVanilla_12_, float p_vertexVanilla_13_, int p_vertexVanilla_14_)
    {
        p_vertexVanilla_1_.pos(p_vertexVanilla_2_, p_vertexVanilla_4_, p_vertexVanilla_6_).color(p_vertexVanilla_8_, p_vertexVanilla_9_, p_vertexVanilla_10_, p_vertexVanilla_11_).tex(p_vertexVanilla_12_, p_vertexVanilla_13_).lightmap(p_vertexVanilla_14_).normal(0.0F, 1.0F, 0.0F).endVertex();
    }

    private int getCombinedAverageLight(IBlockDisplayReader lightReaderIn, BlockPos posIn)
    {
        int i = WorldRenderer.getCombinedLight(lightReaderIn, posIn);
        int j = WorldRenderer.getCombinedLight(lightReaderIn, posIn.up());
        int k = i & 255;
        int l = j & 255;
        int i1 = i >> 16 & 255;
        int j1 = j >> 16 & 255;
        return (k > l ? k : l) | (i1 > j1 ? i1 : j1) << 16;
    }

    private float getFluidHeight(IBlockReader reader, BlockPos pos, Fluid fluidIn)
    {
        int i = 0;
        float f = 0.0F;

        for (int j = 0; j < 4; ++j)
        {
            BlockPos blockpos = pos.add(-(j & 1), 0, -(j >> 1 & 1));

            if (reader.getFluidState(blockpos.up()).getFluid().isEquivalentTo(fluidIn))
            {
                return 1.0F;
            }

            FluidState fluidstate = reader.getFluidState(blockpos);

            if (fluidstate.getFluid().isEquivalentTo(fluidIn))
            {
                float f1 = fluidstate.getActualHeight(reader, blockpos);

                if (f1 >= 0.8F)
                {
                    f += f1 * 10.0F;
                    i += 10;
                }
                else
                {
                    f += f1;
                    ++i;
                }
            }
            else if (!reader.getBlockState(blockpos).getMaterial().isSolid())
            {
                ++i;
            }
        }

        return f / (float)i;
    }
}
