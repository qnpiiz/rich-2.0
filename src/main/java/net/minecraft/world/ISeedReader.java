package net.minecraft.world;

import java.util.stream.Stream;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;

public interface ISeedReader extends IServerWorld
{
    /**
     * gets the random world seed
     */
    long getSeed();

    Stream <? extends StructureStart<? >> func_241827_a(SectionPos p_241827_1_, Structure<?> p_241827_2_);
}
