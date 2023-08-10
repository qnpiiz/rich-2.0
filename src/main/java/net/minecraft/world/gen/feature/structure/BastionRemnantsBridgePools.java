package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.template.ProcessorLists;

public class BastionRemnantsBridgePools
{
    public static void func_236254_a_()
    {
    }

    static
    {
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation("bastion/bridge/starting_pieces"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.func_242861_b("bastion/bridge/starting_pieces/entrance", ProcessorLists.field_244126_z), 1), Pair.of(JigsawPiece.func_242861_b("bastion/bridge/starting_pieces/entrance_face", ProcessorLists.field_244124_x), 1)), JigsawPattern.PlacementBehaviour.RIGID));
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation("bastion/bridge/bridge_pieces"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.func_242861_b("bastion/bridge/bridge_pieces/bridge", ProcessorLists.field_244095_A), 1)), JigsawPattern.PlacementBehaviour.RIGID));
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation("bastion/bridge/legs"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.func_242861_b("bastion/bridge/legs/leg_0", ProcessorLists.field_244124_x), 1), Pair.of(JigsawPiece.func_242861_b("bastion/bridge/legs/leg_1", ProcessorLists.field_244124_x), 1)), JigsawPattern.PlacementBehaviour.RIGID));
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation("bastion/bridge/walls"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.func_242861_b("bastion/bridge/walls/wall_base_0", ProcessorLists.field_244125_y), 1), Pair.of(JigsawPiece.func_242861_b("bastion/bridge/walls/wall_base_1", ProcessorLists.field_244125_y), 1)), JigsawPattern.PlacementBehaviour.RIGID));
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation("bastion/bridge/ramparts"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.func_242861_b("bastion/bridge/ramparts/rampart_0", ProcessorLists.field_244125_y), 1), Pair.of(JigsawPiece.func_242861_b("bastion/bridge/ramparts/rampart_1", ProcessorLists.field_244125_y), 1)), JigsawPattern.PlacementBehaviour.RIGID));
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation("bastion/bridge/rampart_plates"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.func_242861_b("bastion/bridge/rampart_plates/plate_0", ProcessorLists.field_244125_y), 1)), JigsawPattern.PlacementBehaviour.RIGID));
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation("bastion/bridge/connectors"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.func_242861_b("bastion/bridge/connectors/back_bridge_top", ProcessorLists.field_244124_x), 1), Pair.of(JigsawPiece.func_242861_b("bastion/bridge/connectors/back_bridge_bottom", ProcessorLists.field_244124_x), 1)), JigsawPattern.PlacementBehaviour.RIGID));
    }
}
