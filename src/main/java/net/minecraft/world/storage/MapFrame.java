package net.minecraft.world.storage;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;

public class MapFrame
{
    private final BlockPos pos;
    private final int rotation;
    private final int entityId;

    public MapFrame(BlockPos pos, int rotation, int entityId)
    {
        this.pos = pos;
        this.rotation = rotation;
        this.entityId = entityId;
    }

    public static MapFrame read(CompoundNBT nbt)
    {
        BlockPos blockpos = NBTUtil.readBlockPos(nbt.getCompound("Pos"));
        int i = nbt.getInt("Rotation");
        int j = nbt.getInt("EntityId");
        return new MapFrame(blockpos, i, j);
    }

    public CompoundNBT write()
    {
        CompoundNBT compoundnbt = new CompoundNBT();
        compoundnbt.put("Pos", NBTUtil.writeBlockPos(this.pos));
        compoundnbt.putInt("Rotation", this.rotation);
        compoundnbt.putInt("EntityId", this.entityId);
        return compoundnbt;
    }

    public BlockPos getPos()
    {
        return this.pos;
    }

    public int getRotation()
    {
        return this.rotation;
    }

    public int getEntityId()
    {
        return this.entityId;
    }

    public String getFrameName()
    {
        return getFrameNameWithPos(this.pos);
    }

    public static String getFrameNameWithPos(BlockPos pos)
    {
        return "frame-" + pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }
}
