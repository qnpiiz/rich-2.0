package net.minecraftforge.client.model;

import java.util.Collections;
import java.util.Map;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.IModelData;

public class ModelDataManager
{
    public static Map<BlockPos, IModelData> getModelData(World world, ChunkPos pos)
    {
        return Collections.emptyMap();
    }
}
