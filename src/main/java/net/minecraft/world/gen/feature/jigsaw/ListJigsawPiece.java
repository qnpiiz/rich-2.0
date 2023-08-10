package net.minecraft.world.gen.feature.jigsaw;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class ListJigsawPiece extends JigsawPiece
{
    public static final Codec<ListJigsawPiece> field_236834_a_ = RecordCodecBuilder.create((p_236835_0_) ->
    {
        return p_236835_0_.group(JigsawPiece.field_236847_e_.listOf().fieldOf("elements").forGetter((p_236836_0_) -> {
            return p_236836_0_.elements;
        }), func_236848_d_()).apply(p_236835_0_, ListJigsawPiece::new);
    });
    private final List<JigsawPiece> elements;

    public ListJigsawPiece(List<JigsawPiece> p_i51405_1_, JigsawPattern.PlacementBehaviour p_i51405_2_)
    {
        super(p_i51405_2_);

        if (p_i51405_1_.isEmpty())
        {
            throw new IllegalArgumentException("Elements are empty");
        }
        else
        {
            this.elements = p_i51405_1_;
            this.setProjectionOnEachElement(p_i51405_2_);
        }
    }

    public List<Template.BlockInfo> getJigsawBlocks(TemplateManager templateManagerIn, BlockPos pos, Rotation rotationIn, Random rand)
    {
        return this.elements.get(0).getJigsawBlocks(templateManagerIn, pos, rotationIn, rand);
    }

    public MutableBoundingBox getBoundingBox(TemplateManager templateManagerIn, BlockPos pos, Rotation rotationIn)
    {
        MutableBoundingBox mutableboundingbox = MutableBoundingBox.getNewBoundingBox();

        for (JigsawPiece jigsawpiece : this.elements)
        {
            MutableBoundingBox mutableboundingbox1 = jigsawpiece.getBoundingBox(templateManagerIn, pos, rotationIn);
            mutableboundingbox.expandTo(mutableboundingbox1);
        }

        return mutableboundingbox;
    }

    public boolean func_230378_a_(TemplateManager p_230378_1_, ISeedReader p_230378_2_, StructureManager p_230378_3_, ChunkGenerator p_230378_4_, BlockPos p_230378_5_, BlockPos p_230378_6_, Rotation p_230378_7_, MutableBoundingBox p_230378_8_, Random p_230378_9_, boolean p_230378_10_)
    {
        for (JigsawPiece jigsawpiece : this.elements)
        {
            if (!jigsawpiece.func_230378_a_(p_230378_1_, p_230378_2_, p_230378_3_, p_230378_4_, p_230378_5_, p_230378_6_, p_230378_7_, p_230378_8_, p_230378_9_, p_230378_10_))
            {
                return false;
            }
        }

        return true;
    }

    public IJigsawDeserializer<?> getType()
    {
        return IJigsawDeserializer.LIST_POOL_ELEMENT;
    }

    public JigsawPiece setPlacementBehaviour(JigsawPattern.PlacementBehaviour placementBehaviour)
    {
        super.setPlacementBehaviour(placementBehaviour);
        this.setProjectionOnEachElement(placementBehaviour);
        return this;
    }

    public String toString()
    {
        return "List[" + (String)this.elements.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]";
    }

    private void setProjectionOnEachElement(JigsawPattern.PlacementBehaviour p_214864_1_)
    {
        this.elements.forEach((p_214863_1_) ->
        {
            p_214863_1_.setPlacementBehaviour(p_214864_1_);
        });
    }
}
