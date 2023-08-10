package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.template.ProcessorLists;

public class BastionRemnantsPieces
{
    public static final JigsawPattern field_243686_a = JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation("bastion/starts"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.func_242861_b("bastion/units/air_base", ProcessorLists.field_244124_x), 1), Pair.of(JigsawPiece.func_242861_b("bastion/hoglin_stable/air_base", ProcessorLists.field_244124_x), 1), Pair.of(JigsawPiece.func_242861_b("bastion/treasure/big_air_full", ProcessorLists.field_244124_x), 1), Pair.of(JigsawPiece.func_242861_b("bastion/bridge/starting_pieces/entrance_base", ProcessorLists.field_244124_x), 1)), JigsawPattern.PlacementBehaviour.RIGID));

    public static void func_236258_a_()
    {
        BastionRemnantsMainPools.func_236256_a_();
        BastionRemnantsStablesPools.func_236255_a_();
        BastionRemnantsTreasurePools.func_236262_a_();
        BastionRemnantsBridgePools.func_236254_a_();
        BastionRemnantsMobsPools.func_236261_a_();
    }
}
