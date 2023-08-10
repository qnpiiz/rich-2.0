package net.minecraft.test;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.arguments.BlockStateInput;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;
import org.apache.commons.io.IOUtils;

public class StructureHelper
{
    public static String field_229590_a_ = "gameteststructures";

    public static Rotation func_240562_a_(int p_240562_0_)
    {
        switch (p_240562_0_)
        {
            case 0:
                return Rotation.NONE;

            case 1:
                return Rotation.CLOCKWISE_90;

            case 2:
                return Rotation.CLOCKWISE_180;

            case 3:
                return Rotation.COUNTERCLOCKWISE_90;

            default:
                throw new IllegalArgumentException("rotationSteps must be a value from 0-3. Got value " + p_240562_0_);
        }
    }

    public static AxisAlignedBB func_229594_a_(StructureBlockTileEntity p_229594_0_)
    {
        BlockPos blockpos = p_229594_0_.getPos();
        BlockPos blockpos1 = blockpos.add(p_229594_0_.getStructureSize().add(-1, -1, -1));
        BlockPos blockpos2 = Template.getTransformedPos(blockpos1, Mirror.NONE, p_229594_0_.getRotation(), blockpos);
        return new AxisAlignedBB(blockpos, blockpos2);
    }

    public static MutableBoundingBox func_240568_b_(StructureBlockTileEntity p_240568_0_)
    {
        BlockPos blockpos = p_240568_0_.getPos();
        BlockPos blockpos1 = blockpos.add(p_240568_0_.getStructureSize().add(-1, -1, -1));
        BlockPos blockpos2 = Template.getTransformedPos(blockpos1, Mirror.NONE, p_240568_0_.getRotation(), blockpos);
        return new MutableBoundingBox(blockpos, blockpos2);
    }

    public static void func_240564_a_(BlockPos p_240564_0_, BlockPos p_240564_1_, Rotation p_240564_2_, ServerWorld p_240564_3_)
    {
        BlockPos blockpos = Template.getTransformedPos(p_240564_0_.add(p_240564_1_), Mirror.NONE, p_240564_2_, p_240564_0_);
        p_240564_3_.setBlockState(blockpos, Blocks.COMMAND_BLOCK.getDefaultState());
        CommandBlockTileEntity commandblocktileentity = (CommandBlockTileEntity)p_240564_3_.getTileEntity(blockpos);
        commandblocktileentity.getCommandBlockLogic().setCommand("test runthis");
        BlockPos blockpos1 = Template.getTransformedPos(blockpos.add(0, 0, -1), Mirror.NONE, p_240564_2_, blockpos);
        p_240564_3_.setBlockState(blockpos1, Blocks.STONE_BUTTON.getDefaultState().rotate(p_240564_2_));
    }

    public static void func_229603_a_(String p_229603_0_, BlockPos p_229603_1_, BlockPos p_229603_2_, Rotation p_229603_3_, ServerWorld p_229603_4_)
    {
        MutableBoundingBox mutableboundingbox = func_229598_a_(p_229603_1_, p_229603_2_, p_229603_3_);
        func_229595_a_(mutableboundingbox, p_229603_1_.getY(), p_229603_4_);
        p_229603_4_.setBlockState(p_229603_1_, Blocks.STRUCTURE_BLOCK.getDefaultState());
        StructureBlockTileEntity structureblocktileentity = (StructureBlockTileEntity)p_229603_4_.getTileEntity(p_229603_1_);
        structureblocktileentity.setIgnoresEntities(false);
        structureblocktileentity.setName(new ResourceLocation(p_229603_0_));
        structureblocktileentity.setSize(p_229603_2_);
        structureblocktileentity.setMode(StructureMode.SAVE);
        structureblocktileentity.setShowBoundingBox(true);
    }

    public static StructureBlockTileEntity func_240565_a_(String p_240565_0_, BlockPos p_240565_1_, Rotation p_240565_2_, int p_240565_3_, ServerWorld p_240565_4_, boolean p_240565_5_)
    {
        BlockPos blockpos = func_229605_a_(p_240565_0_, p_240565_4_).getSize();
        MutableBoundingBox mutableboundingbox = func_229598_a_(p_240565_1_, blockpos, p_240565_2_);
        BlockPos blockpos1;

        if (p_240565_2_ == Rotation.NONE)
        {
            blockpos1 = p_240565_1_;
        }
        else if (p_240565_2_ == Rotation.CLOCKWISE_90)
        {
            blockpos1 = p_240565_1_.add(blockpos.getZ() - 1, 0, 0);
        }
        else if (p_240565_2_ == Rotation.CLOCKWISE_180)
        {
            blockpos1 = p_240565_1_.add(blockpos.getX() - 1, 0, blockpos.getZ() - 1);
        }
        else
        {
            if (p_240565_2_ != Rotation.COUNTERCLOCKWISE_90)
            {
                throw new IllegalArgumentException("Invalid rotation: " + p_240565_2_);
            }

            blockpos1 = p_240565_1_.add(0, 0, blockpos.getX() - 1);
        }

        func_229608_b_(p_240565_1_, p_240565_4_);
        func_229595_a_(mutableboundingbox, p_240565_1_.getY(), p_240565_4_);
        StructureBlockTileEntity structureblocktileentity = func_240566_a_(p_240565_0_, blockpos1, p_240565_2_, p_240565_4_, p_240565_5_);
        p_240565_4_.getPendingBlockTicks().getPending(mutableboundingbox, true, false);
        p_240565_4_.clearBlockEvents(mutableboundingbox);
        return structureblocktileentity;
    }

    private static void func_229608_b_(BlockPos p_229608_0_, ServerWorld p_229608_1_)
    {
        ChunkPos chunkpos = new ChunkPos(p_229608_0_);

        for (int i = -1; i < 4; ++i)
        {
            for (int j = -1; j < 4; ++j)
            {
                int k = chunkpos.x + i;
                int l = chunkpos.z + j;
                p_229608_1_.forceChunk(k, l, true);
            }
        }
    }

    public static void func_229595_a_(MutableBoundingBox p_229595_0_, int p_229595_1_, ServerWorld p_229595_2_)
    {
        MutableBoundingBox mutableboundingbox = new MutableBoundingBox(p_229595_0_.minX - 2, p_229595_0_.minY - 3, p_229595_0_.minZ - 3, p_229595_0_.maxX + 3, p_229595_0_.maxY + 20, p_229595_0_.maxZ + 3);
        BlockPos.getAllInBox(mutableboundingbox).forEach((p_229592_2_) ->
        {
            func_229591_a_(p_229595_1_, p_229592_2_, p_229595_2_);
        });
        p_229595_2_.getPendingBlockTicks().getPending(mutableboundingbox, true, false);
        p_229595_2_.clearBlockEvents(mutableboundingbox);
        AxisAlignedBB axisalignedbb = new AxisAlignedBB((double)mutableboundingbox.minX, (double)mutableboundingbox.minY, (double)mutableboundingbox.minZ, (double)mutableboundingbox.maxX, (double)mutableboundingbox.maxY, (double)mutableboundingbox.maxZ);
        List<Entity> list = p_229595_2_.getEntitiesWithinAABB(Entity.class, axisalignedbb, (p_229593_0_) ->
        {
            return !(p_229593_0_ instanceof PlayerEntity);
        });
        list.forEach(Entity::remove);
    }

    public static MutableBoundingBox func_229598_a_(BlockPos p_229598_0_, BlockPos p_229598_1_, Rotation p_229598_2_)
    {
        BlockPos blockpos = p_229598_0_.add(p_229598_1_).add(-1, -1, -1);
        BlockPos blockpos1 = Template.getTransformedPos(blockpos, Mirror.NONE, p_229598_2_, p_229598_0_);
        MutableBoundingBox mutableboundingbox = MutableBoundingBox.createProper(p_229598_0_.getX(), p_229598_0_.getY(), p_229598_0_.getZ(), blockpos1.getX(), blockpos1.getY(), blockpos1.getZ());
        int i = Math.min(mutableboundingbox.minX, mutableboundingbox.maxX);
        int j = Math.min(mutableboundingbox.minZ, mutableboundingbox.maxZ);
        BlockPos blockpos2 = new BlockPos(p_229598_0_.getX() - i, 0, p_229598_0_.getZ() - j);
        mutableboundingbox.func_236989_a_(blockpos2);
        return mutableboundingbox;
    }

    public static Optional<BlockPos> func_229596_a_(BlockPos p_229596_0_, int p_229596_1_, ServerWorld p_229596_2_)
    {
        return func_229609_c_(p_229596_0_, p_229596_1_, p_229596_2_).stream().filter((p_229601_2_) ->
        {
            return func_229599_a_(p_229601_2_, p_229596_0_, p_229596_2_);
        }).findFirst();
    }

    @Nullable
    public static BlockPos func_229607_b_(BlockPos p_229607_0_, int p_229607_1_, ServerWorld p_229607_2_)
    {
        Comparator<BlockPos> comparator = Comparator.comparingInt((p_229597_1_) ->
        {
            return p_229597_1_.manhattanDistance(p_229607_0_);
        });
        Collection<BlockPos> collection = func_229609_c_(p_229607_0_, p_229607_1_, p_229607_2_);
        Optional<BlockPos> optional = collection.stream().min(comparator);
        return optional.orElse((BlockPos)null);
    }

    public static Collection<BlockPos> func_229609_c_(BlockPos p_229609_0_, int p_229609_1_, ServerWorld p_229609_2_)
    {
        Collection<BlockPos> collection = Lists.newArrayList();
        AxisAlignedBB axisalignedbb = new AxisAlignedBB(p_229609_0_);
        axisalignedbb = axisalignedbb.grow((double)p_229609_1_);

        for (int i = (int)axisalignedbb.minX; i <= (int)axisalignedbb.maxX; ++i)
        {
            for (int j = (int)axisalignedbb.minY; j <= (int)axisalignedbb.maxY; ++j)
            {
                for (int k = (int)axisalignedbb.minZ; k <= (int)axisalignedbb.maxZ; ++k)
                {
                    BlockPos blockpos = new BlockPos(i, j, k);
                    BlockState blockstate = p_229609_2_.getBlockState(blockpos);

                    if (blockstate.isIn(Blocks.STRUCTURE_BLOCK))
                    {
                        collection.add(blockpos);
                    }
                }
            }
        }

        return collection;
    }

    private static Template func_229605_a_(String p_229605_0_, ServerWorld p_229605_1_)
    {
        TemplateManager templatemanager = p_229605_1_.getStructureTemplateManager();
        Template template = templatemanager.getTemplate(new ResourceLocation(p_229605_0_));

        if (template != null)
        {
            return template;
        }
        else
        {
            String s = p_229605_0_ + ".snbt";
            Path path = Paths.get(field_229590_a_, s);
            CompoundNBT compoundnbt = func_229606_a_(path);

            if (compoundnbt == null)
            {
                throw new RuntimeException("Could not find structure file " + path + ", and the structure is not available in the world structures either.");
            }
            else
            {
                return templatemanager.func_227458_a_(compoundnbt);
            }
        }
    }

    private static StructureBlockTileEntity func_240566_a_(String p_240566_0_, BlockPos p_240566_1_, Rotation p_240566_2_, ServerWorld p_240566_3_, boolean p_240566_4_)
    {
        p_240566_3_.setBlockState(p_240566_1_, Blocks.STRUCTURE_BLOCK.getDefaultState());
        StructureBlockTileEntity structureblocktileentity = (StructureBlockTileEntity)p_240566_3_.getTileEntity(p_240566_1_);
        structureblocktileentity.setMode(StructureMode.LOAD);
        structureblocktileentity.setRotation(p_240566_2_);
        structureblocktileentity.setIgnoresEntities(false);
        structureblocktileentity.setName(new ResourceLocation(p_240566_0_));
        structureblocktileentity.func_242688_a(p_240566_3_, p_240566_4_);

        if (structureblocktileentity.getStructureSize() != BlockPos.ZERO)
        {
            return structureblocktileentity;
        }
        else
        {
            Template template = func_229605_a_(p_240566_0_, p_240566_3_);
            structureblocktileentity.func_242689_a(p_240566_3_, p_240566_4_, template);

            if (structureblocktileentity.getStructureSize() == BlockPos.ZERO)
            {
                throw new RuntimeException("Failed to load structure " + p_240566_0_);
            }
            else
            {
                return structureblocktileentity;
            }
        }
    }

    @Nullable
    private static CompoundNBT func_229606_a_(Path p_229606_0_)
    {
        try
        {
            BufferedReader bufferedreader = Files.newBufferedReader(p_229606_0_);
            String s = IOUtils.toString((Reader)bufferedreader);
            return JsonToNBT.getTagFromJson(s);
        }
        catch (IOException ioexception)
        {
            return null;
        }
        catch (CommandSyntaxException commandsyntaxexception)
        {
            throw new RuntimeException("Error while trying to load structure " + p_229606_0_, commandsyntaxexception);
        }
    }

    private static void func_229591_a_(int p_229591_0_, BlockPos p_229591_1_, ServerWorld p_229591_2_)
    {
        BlockState blockstate = null;
        FlatGenerationSettings flatgenerationsettings = FlatGenerationSettings.func_242869_a(p_229591_2_.func_241828_r().getRegistry(Registry.BIOME_KEY));

        if (flatgenerationsettings instanceof FlatGenerationSettings)
        {
            BlockState[] ablockstate = flatgenerationsettings.getStates();

            if (p_229591_1_.getY() < p_229591_0_ && p_229591_1_.getY() <= ablockstate.length)
            {
                blockstate = ablockstate[p_229591_1_.getY() - 1];
            }
        }
        else if (p_229591_1_.getY() == p_229591_0_ - 1)
        {
            blockstate = p_229591_2_.getBiome(p_229591_1_).getGenerationSettings().getSurfaceBuilderConfig().getTop();
        }
        else if (p_229591_1_.getY() < p_229591_0_ - 1)
        {
            blockstate = p_229591_2_.getBiome(p_229591_1_).getGenerationSettings().getSurfaceBuilderConfig().getUnder();
        }

        if (blockstate == null)
        {
            blockstate = Blocks.AIR.getDefaultState();
        }

        BlockStateInput blockstateinput = new BlockStateInput(blockstate, Collections.emptySet(), (CompoundNBT)null);
        blockstateinput.place(p_229591_2_, p_229591_1_, 2);
        p_229591_2_.func_230547_a_(p_229591_1_, blockstate.getBlock());
    }

    private static boolean func_229599_a_(BlockPos p_229599_0_, BlockPos p_229599_1_, ServerWorld p_229599_2_)
    {
        StructureBlockTileEntity structureblocktileentity = (StructureBlockTileEntity)p_229599_2_.getTileEntity(p_229599_0_);
        AxisAlignedBB axisalignedbb = func_229594_a_(structureblocktileentity).grow(1.0D);
        return axisalignedbb.contains(Vector3d.copyCentered(p_229599_1_));
    }
}
