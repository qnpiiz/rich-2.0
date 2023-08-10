package net.minecraft.nbt;

public class NBTTypes
{
    private static final INBTType<?>[] TYPES = new INBTType[] {EndNBT.TYPE, ByteNBT.TYPE, ShortNBT.TYPE, IntNBT.TYPE, LongNBT.TYPE, FloatNBT.TYPE, DoubleNBT.TYPE, ByteArrayNBT.TYPE, StringNBT.TYPE, ListNBT.TYPE, CompoundNBT.TYPE, IntArrayNBT.TYPE, LongArrayNBT.TYPE};

    public static INBTType<?> getGetTypeByID(int id)
    {
        return id >= 0 && id < TYPES.length ? TYPES[id] : INBTType.getEndNBT(id);
    }
}
