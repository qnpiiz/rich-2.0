package net.minecraft.world.gen.feature.template;

import java.util.List;

public class StructureProcessorList
{
    private final List<StructureProcessor> field_242918_a;

    public StructureProcessorList(List<StructureProcessor> p_i242038_1_)
    {
        this.field_242918_a = p_i242038_1_;
    }

    public List<StructureProcessor> func_242919_a()
    {
        return this.field_242918_a;
    }

    public String toString()
    {
        return "ProcessorList[" + this.field_242918_a + "]";
    }
}
