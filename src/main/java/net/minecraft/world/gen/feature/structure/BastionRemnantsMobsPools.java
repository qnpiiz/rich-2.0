package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;

public class BastionRemnantsMobsPools
{
    public static void func_236261_a_()
    {
    }

    static
    {
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation("bastion/mobs/piglin"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.func_242859_b("bastion/mobs/melee_piglin"), 1), Pair.of(JigsawPiece.func_242859_b("bastion/mobs/sword_piglin"), 4), Pair.of(JigsawPiece.func_242859_b("bastion/mobs/crossbow_piglin"), 4), Pair.of(JigsawPiece.func_242859_b("bastion/mobs/empty"), 1)), JigsawPattern.PlacementBehaviour.RIGID));
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation("bastion/mobs/hoglin"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.func_242859_b("bastion/mobs/hoglin"), 2), Pair.of(JigsawPiece.func_242859_b("bastion/mobs/empty"), 1)), JigsawPattern.PlacementBehaviour.RIGID));
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation("bastion/blocks/gold"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.func_242859_b("bastion/blocks/air"), 3), Pair.of(JigsawPiece.func_242859_b("bastion/blocks/gold"), 1)), JigsawPattern.PlacementBehaviour.RIGID));
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation("bastion/mobs/piglin_melee"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.func_242859_b("bastion/mobs/melee_piglin_always"), 1), Pair.of(JigsawPiece.func_242859_b("bastion/mobs/melee_piglin"), 5), Pair.of(JigsawPiece.func_242859_b("bastion/mobs/sword_piglin"), 1)), JigsawPattern.PlacementBehaviour.RIGID));
    }
}
