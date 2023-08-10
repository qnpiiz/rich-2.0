package net.minecraft.world.gen.feature.jigsaw;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKeyCodec;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.GravityStructureProcessor;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.TemplateManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JigsawPattern
{
    private static final Logger field_236853_d_ = LogManager.getLogger();
    public static final Codec<JigsawPattern> field_236852_a_ = RecordCodecBuilder.create((p_236854_0_) ->
    {
        return p_236854_0_.group(ResourceLocation.CODEC.fieldOf("name").forGetter(JigsawPattern::getName), ResourceLocation.CODEC.fieldOf("fallback").forGetter(JigsawPattern::getFallback), Codec.mapPair(JigsawPiece.field_236847_e_.fieldOf("element"), Codec.INT.fieldOf("weight")).codec().listOf().promotePartial(Util.func_240982_a_("Pool element: ", field_236853_d_::error)).fieldOf("elements").forGetter((p_236857_0_) -> {
            return p_236857_0_.rawTemplates;
        })).apply(p_236854_0_, JigsawPattern::new);
    });
    public static final Codec<Supplier<JigsawPattern>> field_244392_b_ = RegistryKeyCodec.create(Registry.JIGSAW_POOL_KEY, field_236852_a_);
    private final ResourceLocation name;
    private final List<Pair<JigsawPiece, Integer>> rawTemplates;
    private final List<JigsawPiece> jigsawPieces;
    private final ResourceLocation fallback;
    private int maxSize = Integer.MIN_VALUE;

    public JigsawPattern(ResourceLocation p_i242010_1_, ResourceLocation p_i242010_2_, List<Pair<JigsawPiece, Integer>> p_i242010_3_)
    {
        this.name = p_i242010_1_;
        this.rawTemplates = p_i242010_3_;
        this.jigsawPieces = Lists.newArrayList();

        for (Pair<JigsawPiece, Integer> pair : p_i242010_3_)
        {
            JigsawPiece jigsawpiece = pair.getFirst();

            for (int i = 0; i < pair.getSecond(); ++i)
            {
                this.jigsawPieces.add(jigsawpiece);
            }
        }

        this.fallback = p_i242010_2_;
    }

    public JigsawPattern(ResourceLocation nameIn, ResourceLocation p_i51397_2_, List < Pair < Function < JigsawPattern.PlacementBehaviour, ? extends JigsawPiece > , Integer >> p_i51397_3_, JigsawPattern.PlacementBehaviour placementBehaviourIn)
    {
        this.name = nameIn;
        this.rawTemplates = Lists.newArrayList();
        this.jigsawPieces = Lists.newArrayList();

        for (Pair < Function < JigsawPattern.PlacementBehaviour, ? extends JigsawPiece > , Integer > pair : p_i51397_3_)
        {
            JigsawPiece jigsawpiece = pair.getFirst().apply(placementBehaviourIn);
            this.rawTemplates.add(Pair.of(jigsawpiece, pair.getSecond()));

            for (int i = 0; i < pair.getSecond(); ++i)
            {
                this.jigsawPieces.add(jigsawpiece);
            }
        }

        this.fallback = p_i51397_2_;
    }

    public int getMaxSize(TemplateManager templateManagerIn)
    {
        if (this.maxSize == Integer.MIN_VALUE)
        {
            this.maxSize = this.jigsawPieces.stream().mapToInt((p_236856_1_) ->
            {
                return p_236856_1_.getBoundingBox(templateManagerIn, BlockPos.ZERO, Rotation.NONE).getYSize();
            }).max().orElse(0);
        }

        return this.maxSize;
    }

    public ResourceLocation getFallback()
    {
        return this.fallback;
    }

    public JigsawPiece getRandomPiece(Random rand)
    {
        return this.jigsawPieces.get(rand.nextInt(this.jigsawPieces.size()));
    }

    public List<JigsawPiece> getShuffledPieces(Random rand)
    {
        return ImmutableList.copyOf(ObjectArrays.shuffle(this.jigsawPieces.toArray(new JigsawPiece[0]), rand));
    }

    public ResourceLocation getName()
    {
        return this.name;
    }

    public int getNumberOfPieces()
    {
        return this.jigsawPieces.size();
    }

    public static enum PlacementBehaviour implements IStringSerializable
    {
        TERRAIN_MATCHING("terrain_matching", ImmutableList.of(new GravityStructureProcessor(Heightmap.Type.WORLD_SURFACE_WG, -1))),
        RIGID("rigid", ImmutableList.of());

        public static final Codec<JigsawPattern.PlacementBehaviour> field_236858_c_ = IStringSerializable.createEnumCodec(JigsawPattern.PlacementBehaviour::values, JigsawPattern.PlacementBehaviour::getBehaviour);
        private static final Map<String, JigsawPattern.PlacementBehaviour> BEHAVIOURS = Arrays.stream(values()).collect(Collectors.toMap(JigsawPattern.PlacementBehaviour::getName, (p_214935_0_) -> {
            return p_214935_0_;
        }));
        private final String name;
        private final ImmutableList<StructureProcessor> structureProcessors;

        private PlacementBehaviour(String nameIn, ImmutableList<StructureProcessor> structureProcessorsIn)
        {
            this.name = nameIn;
            this.structureProcessors = structureProcessorsIn;
        }

        public String getName()
        {
            return this.name;
        }

        public static JigsawPattern.PlacementBehaviour getBehaviour(String nameIn)
        {
            return BEHAVIOURS.get(nameIn);
        }

        public ImmutableList<StructureProcessor> getStructureProcessors()
        {
            return this.structureProcessors;
        }

        public String getString()
        {
            return this.name;
        }
    }
}
