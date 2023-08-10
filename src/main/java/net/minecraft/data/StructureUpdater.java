package net.minecraft.data;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.world.gen.feature.template.Template;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureUpdater implements SNBTToNBTConverter.ITransformer
{
    private static final Logger LOGGER = LogManager.getLogger();

    public CompoundNBT func_225371_a(String p_225371_1_, CompoundNBT p_225371_2_)
    {
        return p_225371_1_.startsWith("data/minecraft/structures/") ? updateSNBT(p_225371_1_, addDataVersion(p_225371_2_)) : p_225371_2_;
    }

    private static CompoundNBT addDataVersion(CompoundNBT nbt)
    {
        if (!nbt.contains("DataVersion", 99))
        {
            nbt.putInt("DataVersion", 500);
        }

        return nbt;
    }

    private static CompoundNBT updateSNBT(String name, CompoundNBT nbt)
    {
        Template template = new Template();
        int i = nbt.getInt("DataVersion");
        int j = 2532;

        if (i < 2532)
        {
            LOGGER.warn("SNBT Too old, do not forget to update: " + i + " < " + 2532 + ": " + name);
        }

        CompoundNBT compoundnbt = NBTUtil.update(DataFixesManager.getDataFixer(), DefaultTypeReferences.STRUCTURE, nbt, i);
        template.read(compoundnbt);
        return template.writeToNBT(new CompoundNBT());
    }
}
