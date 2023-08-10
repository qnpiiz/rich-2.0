package net.minecraft.world.gen.feature.jigsaw;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public interface IJigsawDeserializer<P extends JigsawPiece>
{
    IJigsawDeserializer<SingleJigsawPiece> SINGLE_POOL_ELEMENT = func_236851_a_("single_pool_element", SingleJigsawPiece.field_236838_b_);
    IJigsawDeserializer<ListJigsawPiece> LIST_POOL_ELEMENT = func_236851_a_("list_pool_element", ListJigsawPiece.field_236834_a_);
    IJigsawDeserializer<FeatureJigsawPiece> FEATURE_POOL_ELEMENT = func_236851_a_("feature_pool_element", FeatureJigsawPiece.field_236816_a_);
    IJigsawDeserializer<EmptyJigsawPiece> EMPTY_POOL_ELEMENT = func_236851_a_("empty_pool_element", EmptyJigsawPiece.field_236814_a_);
    IJigsawDeserializer<LegacySingleJigsawPiece> field_236849_e_ = func_236851_a_("legacy_single_pool_element", LegacySingleJigsawPiece.field_236832_a_);

    Codec<P> codec();

    static <P extends JigsawPiece> IJigsawDeserializer<P> func_236851_a_(String p_236851_0_, Codec<P> p_236851_1_)
    {
        return Registry.register(Registry.STRUCTURE_POOL_ELEMENT, p_236851_0_, () ->
        {
            return p_236851_1_;
        });
    }
}
