package net.minecraft.world.gen.feature.jigsaw;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.block.Blocks;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.JigsawReplacementStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.ProcessorLists;
import net.minecraft.world.gen.feature.template.StructureProcessorList;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class SingleJigsawPiece extends JigsawPiece
{
    private static final Codec<Either<ResourceLocation, Template>> field_236837_a_ = Codec.of(SingleJigsawPiece::func_236840_a_, ResourceLocation.CODEC.map(Either::left));
    public static final Codec<SingleJigsawPiece> field_236838_b_ = RecordCodecBuilder.create((p_236841_0_) ->
    {
        return p_236841_0_.group(func_236846_c_(), func_236844_b_(), func_236848_d_()).apply(p_236841_0_, SingleJigsawPiece::new);
    });
    protected final Either<ResourceLocation, Template> field_236839_c_;
    protected final Supplier<StructureProcessorList> processors;

    private static <T> DataResult<T> func_236840_a_(Either<ResourceLocation, Template> p_236840_0_, DynamicOps<T> p_236840_1_, T p_236840_2_)
    {
        Optional<ResourceLocation> optional = p_236840_0_.left();
        return !optional.isPresent() ? DataResult.error("Can not serialize a runtime pool element") : ResourceLocation.CODEC.encode(optional.get(), p_236840_1_, p_236840_2_);
    }

    protected static <E extends SingleJigsawPiece> RecordCodecBuilder<E, Supplier<StructureProcessorList>> func_236844_b_()
    {
        return IStructureProcessorType.field_242922_m.fieldOf("processors").forGetter((p_236845_0_) ->
        {
            return p_236845_0_.processors;
        });
    }

    protected static <E extends SingleJigsawPiece> RecordCodecBuilder<E, Either<ResourceLocation, Template>> func_236846_c_()
    {
        return field_236837_a_.fieldOf("location").forGetter((p_236842_0_) ->
        {
            return p_236842_0_.field_236839_c_;
        });
    }

    protected SingleJigsawPiece(Either<ResourceLocation, Template> p_i242008_1_, Supplier<StructureProcessorList> p_i242008_2_, JigsawPattern.PlacementBehaviour p_i242008_3_)
    {
        super(p_i242008_3_);
        this.field_236839_c_ = p_i242008_1_;
        this.processors = p_i242008_2_;
    }

    public SingleJigsawPiece(Template p_i242009_1_)
    {
        this(Either.right(p_i242009_1_), () ->
        {
            return ProcessorLists.field_244101_a;
        }, JigsawPattern.PlacementBehaviour.RIGID);
    }

    private Template func_236843_a_(TemplateManager p_236843_1_)
    {
        return this.field_236839_c_.map(p_236843_1_::getTemplateDefaulted, Function.identity());
    }

    public List<Template.BlockInfo> getDataMarkers(TemplateManager p_214857_1_, BlockPos p_214857_2_, Rotation p_214857_3_, boolean p_214857_4_)
    {
        Template template = this.func_236843_a_(p_214857_1_);
        List<Template.BlockInfo> list = template.func_215386_a(p_214857_2_, (new PlacementSettings()).setRotation(p_214857_3_), Blocks.STRUCTURE_BLOCK, p_214857_4_);
        List<Template.BlockInfo> list1 = Lists.newArrayList();

        for (Template.BlockInfo template$blockinfo : list)
        {
            if (template$blockinfo.nbt != null)
            {
                StructureMode structuremode = StructureMode.valueOf(template$blockinfo.nbt.getString("mode"));

                if (structuremode == StructureMode.DATA)
                {
                    list1.add(template$blockinfo);
                }
            }
        }

        return list1;
    }

    public List<Template.BlockInfo> getJigsawBlocks(TemplateManager templateManagerIn, BlockPos pos, Rotation rotationIn, Random rand)
    {
        Template template = this.func_236843_a_(templateManagerIn);
        List<Template.BlockInfo> list = template.func_215386_a(pos, (new PlacementSettings()).setRotation(rotationIn), Blocks.JIGSAW, true);
        Collections.shuffle(list, rand);
        return list;
    }

    public MutableBoundingBox getBoundingBox(TemplateManager templateManagerIn, BlockPos pos, Rotation rotationIn)
    {
        Template template = this.func_236843_a_(templateManagerIn);
        return template.getMutableBoundingBox((new PlacementSettings()).setRotation(rotationIn), pos);
    }

    public boolean func_230378_a_(TemplateManager p_230378_1_, ISeedReader p_230378_2_, StructureManager p_230378_3_, ChunkGenerator p_230378_4_, BlockPos p_230378_5_, BlockPos p_230378_6_, Rotation p_230378_7_, MutableBoundingBox p_230378_8_, Random p_230378_9_, boolean p_230378_10_)
    {
        Template template = this.func_236843_a_(p_230378_1_);
        PlacementSettings placementsettings = this.func_230379_a_(p_230378_7_, p_230378_8_, p_230378_10_);

        if (!template.func_237146_a_(p_230378_2_, p_230378_5_, p_230378_6_, placementsettings, p_230378_9_, 18))
        {
            return false;
        }
        else
        {
            for (Template.BlockInfo template$blockinfo : Template.func_237145_a_(p_230378_2_, p_230378_5_, p_230378_6_, placementsettings, this.getDataMarkers(p_230378_1_, p_230378_5_, p_230378_7_, false)))
            {
                this.handleDataMarker(p_230378_2_, template$blockinfo, p_230378_5_, p_230378_7_, p_230378_9_, p_230378_8_);
            }

            return true;
        }
    }

    protected PlacementSettings func_230379_a_(Rotation p_230379_1_, MutableBoundingBox p_230379_2_, boolean p_230379_3_)
    {
        PlacementSettings placementsettings = new PlacementSettings();
        placementsettings.setBoundingBox(p_230379_2_);
        placementsettings.setRotation(p_230379_1_);
        placementsettings.func_215223_c(true);
        placementsettings.setIgnoreEntities(false);
        placementsettings.addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
        placementsettings.func_237133_d_(true);

        if (!p_230379_3_)
        {
            placementsettings.addProcessor(JigsawReplacementStructureProcessor.INSTANCE);
        }

        this.processors.get().func_242919_a().forEach(placementsettings::addProcessor);
        this.getPlacementBehaviour().getStructureProcessors().forEach(placementsettings::addProcessor);
        return placementsettings;
    }

    public IJigsawDeserializer<?> getType()
    {
        return IJigsawDeserializer.SINGLE_POOL_ELEMENT;
    }

    public String toString()
    {
        return "Single[" + this.field_236839_c_ + "]";
    }
}
