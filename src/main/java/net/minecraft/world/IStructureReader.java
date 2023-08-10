package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;

public interface IStructureReader
{
    @Nullable
    StructureStart<?> func_230342_a_(Structure<?> p_230342_1_);

    void func_230344_a_(Structure<?> p_230344_1_, StructureStart<?> p_230344_2_);

    LongSet func_230346_b_(Structure<?> p_230346_1_);

    void func_230343_a_(Structure<?> p_230343_1_, long p_230343_2_);

    Map < Structure<?>, LongSet > getStructureReferences();

    void setStructureReferences(Map < Structure<?>, LongSet > structureReferences);
}
