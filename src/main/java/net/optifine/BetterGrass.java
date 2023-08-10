package net.optifine;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.GrassPathBlock;
import net.minecraft.block.MyceliumBlock;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.optifine.model.BlockModelUtils;
import net.optifine.util.PropertiesOrdered;

public class BetterGrass
{
    private static boolean betterGrass = true;
    private static boolean betterGrassPath = true;
    private static boolean betterMycelium = true;
    private static boolean betterPodzol = true;
    private static boolean betterGrassSnow = true;
    private static boolean betterMyceliumSnow = true;
    private static boolean betterPodzolSnow = true;
    private static boolean grassMultilayer = false;
    private static TextureAtlasSprite spriteGrass = null;
    private static TextureAtlasSprite spriteGrassSide = null;
    private static TextureAtlasSprite spriteGrassPath = null;
    private static TextureAtlasSprite spriteGrassPathSide = null;
    private static TextureAtlasSprite spriteMycelium = null;
    private static TextureAtlasSprite spritePodzol = null;
    private static TextureAtlasSprite spriteSnow = null;
    private static boolean spritesLoaded = false;
    private static IBakedModel modelCubeGrass = null;
    private static IBakedModel modelGrassPath = null;
    private static IBakedModel modelCubeGrassPath = null;
    private static IBakedModel modelCubeMycelium = null;
    private static IBakedModel modelCubePodzol = null;
    private static IBakedModel modelCubeSnow = null;
    private static boolean modelsLoaded = false;
    private static final String TEXTURE_GRASS_DEFAULT = "block/grass_block_top";
    private static final String TEXTURE_GRASS_SIDE_DEFAULT = "block/grass_block_side";
    private static final String TEXTURE_GRASS_PATH_DEFAULT = "block/grass_path_top";
    private static final String TEXTURE_GRASS_PATH_SIDE_DEFAULT = "block/grass_path_side";
    private static final String TEXTURE_MYCELIUM_DEFAULT = "block/mycelium_top";
    private static final String TEXTURE_PODZOL_DEFAULT = "block/podzol_top";
    private static final String TEXTURE_SNOW_DEFAULT = "block/snow";
    private static final Random RANDOM = new Random(0L);

    public static void updateIcons(AtlasTexture textureMap)
    {
        spritesLoaded = false;
        modelsLoaded = false;
        loadProperties(textureMap);
    }

    public static void update()
    {
        if (spritesLoaded)
        {
            modelCubeGrass = BlockModelUtils.makeModelCube(spriteGrass, 0);

            if (grassMultilayer)
            {
                IBakedModel ibakedmodel = BlockModelUtils.makeModelCube(spriteGrassSide, -1);
                modelCubeGrass = BlockModelUtils.joinModelsCube(ibakedmodel, modelCubeGrass);
            }

            modelGrassPath = BlockModelUtils.makeModel("grass_path", spriteGrassPathSide, spriteGrassPath);
            modelCubeGrassPath = BlockModelUtils.makeModelCube(spriteGrassPath, -1);
            modelCubeMycelium = BlockModelUtils.makeModelCube(spriteMycelium, -1);
            modelCubePodzol = BlockModelUtils.makeModelCube(spritePodzol, 0);
            modelCubeSnow = BlockModelUtils.makeModelCube(spriteSnow, -1);
            modelsLoaded = true;
        }
    }

    private static void loadProperties(AtlasTexture textureMap)
    {
        betterGrass = true;
        betterGrassPath = true;
        betterMycelium = true;
        betterPodzol = true;
        betterGrassSnow = true;
        betterMyceliumSnow = true;
        betterPodzolSnow = true;
        spriteGrass = textureMap.registerSprite(new ResourceLocation("block/grass_block_top"));
        spriteGrassSide = textureMap.registerSprite(new ResourceLocation("block/grass_block_side"));
        spriteGrassPath = textureMap.registerSprite(new ResourceLocation("block/grass_path_top"));
        spriteGrassPathSide = textureMap.registerSprite(new ResourceLocation("block/grass_path_side"));
        spriteMycelium = textureMap.registerSprite(new ResourceLocation("block/mycelium_top"));
        spritePodzol = textureMap.registerSprite(new ResourceLocation("block/podzol_top"));
        spriteSnow = textureMap.registerSprite(new ResourceLocation("block/snow"));
        spritesLoaded = true;
        String s = "optifine/bettergrass.properties";

        try
        {
            ResourceLocation resourcelocation = new ResourceLocation(s);

            if (!Config.hasResource(resourcelocation))
            {
                return;
            }

            InputStream inputstream = Config.getResourceStream(resourcelocation);

            if (inputstream == null)
            {
                return;
            }

            boolean flag = Config.isFromDefaultResourcePack(resourcelocation);

            if (flag)
            {
                Config.dbg("BetterGrass: Parsing default configuration " + s);
            }
            else
            {
                Config.dbg("BetterGrass: Parsing configuration " + s);
            }

            Properties properties = new PropertiesOrdered();
            properties.load(inputstream);
            inputstream.close();
            betterGrass = getBoolean(properties, "grass", true);
            betterGrassPath = getBoolean(properties, "grass_path", true);
            betterMycelium = getBoolean(properties, "mycelium", true);
            betterPodzol = getBoolean(properties, "podzol", true);
            betterGrassSnow = getBoolean(properties, "grass.snow", true);
            betterMyceliumSnow = getBoolean(properties, "mycelium.snow", true);
            betterPodzolSnow = getBoolean(properties, "podzol.snow", true);
            grassMultilayer = getBoolean(properties, "grass.multilayer", false);
            spriteGrass = registerSprite(properties, "texture.grass", "block/grass_block_top", textureMap);
            spriteGrassSide = registerSprite(properties, "texture.grass_side", "block/grass_block_side", textureMap);
            spriteGrassPath = registerSprite(properties, "texture.grass_path", "block/grass_path_top", textureMap);
            spriteGrassPathSide = registerSprite(properties, "texture.grass_path_side", "block/grass_path_side", textureMap);
            spriteMycelium = registerSprite(properties, "texture.mycelium", "block/mycelium_top", textureMap);
            spritePodzol = registerSprite(properties, "texture.podzol", "block/podzol_top", textureMap);
            spriteSnow = registerSprite(properties, "texture.snow", "block/snow", textureMap);
        }
        catch (IOException ioexception)
        {
            Config.warn("Error reading: " + s + ", " + ioexception.getClass().getName() + ": " + ioexception.getMessage());
        }
    }

    public static void refreshIcons(AtlasTexture textureMap)
    {
        spriteGrass = getSprite(textureMap, spriteGrass.getName());
        spriteGrassSide = getSprite(textureMap, spriteGrassSide.getName());
        spriteGrassPath = getSprite(textureMap, spriteGrassPath.getName());
        spriteGrassPathSide = getSprite(textureMap, spriteGrassPathSide.getName());
        spriteMycelium = getSprite(textureMap, spriteMycelium.getName());
        spritePodzol = getSprite(textureMap, spritePodzol.getName());
        spriteSnow = getSprite(textureMap, spriteSnow.getName());
    }

    private static TextureAtlasSprite getSprite(AtlasTexture textureMap, ResourceLocation loc)
    {
        TextureAtlasSprite textureatlassprite = textureMap.getSprite(loc);

        if (textureatlassprite == null || textureatlassprite instanceof MissingTextureSprite)
        {
            Config.warn("Missing BetterGrass sprite: " + loc);
        }

        return textureatlassprite;
    }

    private static TextureAtlasSprite registerSprite(Properties props, String key, String textureDefault, AtlasTexture textureMap)
    {
        String s = props.getProperty(key);

        if (s == null)
        {
            s = textureDefault;
        }

        ResourceLocation resourcelocation = new ResourceLocation("textures/" + s + ".png");

        if (!Config.hasResource(resourcelocation))
        {
            Config.warn("BetterGrass texture not found: " + resourcelocation);
            s = textureDefault;
        }

        ResourceLocation resourcelocation1 = new ResourceLocation(s);
        return textureMap.registerSprite(resourcelocation1);
    }

    public static List getFaceQuads(IBlockReader blockAccess, BlockState blockState, BlockPos blockPos, Direction facing, List quads)
    {
        if (facing != Direction.UP && facing != Direction.DOWN)
        {
            if (!modelsLoaded)
            {
                return quads;
            }
            else
            {
                Block block = blockState.getBlock();

                if (block instanceof MyceliumBlock)
                {
                    return getFaceQuadsMycelium(blockAccess, blockState, blockPos, facing, quads);
                }
                else if (block instanceof GrassPathBlock)
                {
                    return getFaceQuadsGrassPath(blockAccess, blockState, blockPos, facing, quads);
                }
                else if (block == Blocks.PODZOL)
                {
                    return getFaceQuadsPodzol(blockAccess, blockState, blockPos, facing, quads);
                }
                else if (block == Blocks.DIRT)
                {
                    return getFaceQuadsDirt(blockAccess, blockState, blockPos, facing, quads);
                }
                else
                {
                    return block instanceof GrassBlock ? getFaceQuadsGrass(blockAccess, blockState, blockPos, facing, quads) : quads;
                }
            }
        }
        else
        {
            return quads;
        }
    }

    private static List getFaceQuadsMycelium(IBlockReader blockAccess, BlockState blockState, BlockPos blockPos, Direction facing, List quads)
    {
        Block block = blockAccess.getBlockState(blockPos.up()).getBlock();
        boolean flag = block == Blocks.SNOW_BLOCK || block == Blocks.SNOW;

        if (Config.isBetterGrassFancy())
        {
            if (flag)
            {
                if (betterMyceliumSnow && getBlockAt(blockPos, facing, blockAccess) == Blocks.SNOW)
                {
                    return modelCubeSnow.getQuads(blockState, facing, RANDOM);
                }
            }
            else if (betterMycelium && getBlockAt(blockPos.down(), facing, blockAccess) == Blocks.MYCELIUM)
            {
                return modelCubeMycelium.getQuads(blockState, facing, RANDOM);
            }
        }
        else if (flag)
        {
            if (betterMyceliumSnow)
            {
                return modelCubeSnow.getQuads(blockState, facing, RANDOM);
            }
        }
        else if (betterMycelium)
        {
            return modelCubeMycelium.getQuads(blockState, facing, RANDOM);
        }

        return quads;
    }

    private static List getFaceQuadsGrassPath(IBlockReader blockAccess, BlockState blockState, BlockPos blockPos, Direction facing, List quads)
    {
        if (!betterGrassPath)
        {
            return quads;
        }
        else if (Config.isBetterGrassFancy())
        {
            return getBlockAt(blockPos.down(), facing, blockAccess) == Blocks.GRASS_PATH ? modelGrassPath.getQuads(blockState, facing, RANDOM) : quads;
        }
        else
        {
            return modelGrassPath.getQuads(blockState, facing, RANDOM);
        }
    }

    private static List getFaceQuadsPodzol(IBlockReader blockAccess, BlockState blockState, BlockPos blockPos, Direction facing, List quads)
    {
        Block block = getBlockAt(blockPos, Direction.UP, blockAccess);
        boolean flag = block == Blocks.SNOW_BLOCK || block == Blocks.SNOW;

        if (Config.isBetterGrassFancy())
        {
            if (flag)
            {
                if (betterPodzolSnow && getBlockAt(blockPos, facing, blockAccess) == Blocks.SNOW)
                {
                    return modelCubeSnow.getQuads(blockState, facing, RANDOM);
                }
            }
            else if (betterPodzol)
            {
                BlockPos blockpos = blockPos.down().offset(facing);
                BlockState blockstate = blockAccess.getBlockState(blockpos);

                if (blockstate.getBlock() == Blocks.PODZOL)
                {
                    return modelCubePodzol.getQuads(blockState, facing, RANDOM);
                }
            }
        }
        else if (flag)
        {
            if (betterPodzolSnow)
            {
                return modelCubeSnow.getQuads(blockState, facing, RANDOM);
            }
        }
        else if (betterPodzol)
        {
            return modelCubePodzol.getQuads(blockState, facing, RANDOM);
        }

        return quads;
    }

    private static List getFaceQuadsDirt(IBlockReader blockAccess, BlockState blockState, BlockPos blockPos, Direction facing, List quads)
    {
        Block block = getBlockAt(blockPos, Direction.UP, blockAccess);
        return block == Blocks.GRASS_PATH && betterGrassPath && getBlockAt(blockPos, facing, blockAccess) == Blocks.GRASS_PATH ? modelCubeGrassPath.getQuads(blockState, facing, RANDOM) : quads;
    }

    private static List getFaceQuadsGrass(IBlockReader blockAccess, BlockState blockState, BlockPos blockPos, Direction facing, List quads)
    {
        Block block = blockAccess.getBlockState(blockPos.up()).getBlock();
        boolean flag = block == Blocks.SNOW_BLOCK || block == Blocks.SNOW;

        if (Config.isBetterGrassFancy())
        {
            if (flag)
            {
                if (betterGrassSnow && getBlockAt(blockPos, facing, blockAccess) == Blocks.SNOW)
                {
                    return modelCubeSnow.getQuads(blockState, facing, RANDOM);
                }
            }
            else if (betterGrass && getBlockAt(blockPos.down(), facing, blockAccess) == Blocks.GRASS_BLOCK)
            {
                return modelCubeGrass.getQuads(blockState, facing, RANDOM);
            }
        }
        else if (flag)
        {
            if (betterGrassSnow)
            {
                return modelCubeSnow.getQuads(blockState, facing, RANDOM);
            }
        }
        else if (betterGrass)
        {
            return modelCubeGrass.getQuads(blockState, facing, RANDOM);
        }

        return quads;
    }

    private static Block getBlockAt(BlockPos blockPos, Direction facing, IBlockReader blockAccess)
    {
        BlockPos blockpos = blockPos.offset(facing);
        return blockAccess.getBlockState(blockpos).getBlock();
    }

    private static boolean getBoolean(Properties props, String key, boolean def)
    {
        String s = props.getProperty(key);
        return s == null ? def : Boolean.parseBoolean(s);
    }
}
