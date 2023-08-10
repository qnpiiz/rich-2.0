package net.minecraft.world.gen.feature.jigsaw;

import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class EmptyJigsawPiece extends JigsawPiece
{
    public static final Codec<EmptyJigsawPiece> field_236814_a_;
    public static final EmptyJigsawPiece INSTANCE = new EmptyJigsawPiece();

    private EmptyJigsawPiece()
    {
        super(JigsawPattern.PlacementBehaviour.TERRAIN_MATCHING);
    }

    public List<Template.BlockInfo> getJigsawBlocks(TemplateManager templateManagerIn, BlockPos pos, Rotation rotationIn, Random rand)
    {
        return Collections.emptyList();
    }

    public MutableBoundingBox getBoundingBox(TemplateManager templateManagerIn, BlockPos pos, Rotation rotationIn)
    {
        return MutableBoundingBox.getNewBoundingBox();
    }

    public boolean func_230378_a_(TemplateManager p_230378_1_, ISeedReader p_230378_2_, StructureManager p_230378_3_, ChunkGenerator p_230378_4_, BlockPos p_230378_5_, BlockPos p_230378_6_, Rotation p_230378_7_, MutableBoundingBox p_230378_8_, Random p_230378_9_, boolean p_230378_10_)
    {
        return true;
    }

    public IJigsawDeserializer<?> getType()
    {
        return IJigsawDeserializer.EMPTY_POOL_ELEMENT;
    }

    public String toString()
    {
        return "Empty";
    }

    static
    {
        field_236814_a_ = Codec.unit(() ->
        {
            return INSTANCE;
        });
    }
}
