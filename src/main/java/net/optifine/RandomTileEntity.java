package net.optifine;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.optifine.util.TileEntityUtils;

public class RandomTileEntity implements IRandomEntity
{
    private TileEntity tileEntity;

    public int getId()
    {
        return Config.getRandom(this.tileEntity.getPos(), 0);
    }

    public BlockPos getSpawnPosition()
    {
        return this.tileEntity.getPos();
    }

    public String getName()
    {
        return TileEntityUtils.getTileEntityName(this.tileEntity);
    }

    public Biome getSpawnBiome()
    {
        return this.tileEntity.getWorld().getBiome(this.tileEntity.getPos());
    }

    public int getHealth()
    {
        return -1;
    }

    public int getMaxHealth()
    {
        return -1;
    }

    public TileEntity getTileEntity()
    {
        return this.tileEntity;
    }

    public void setTileEntity(TileEntity tileEntity)
    {
        this.tileEntity = tileEntity;
    }
}
