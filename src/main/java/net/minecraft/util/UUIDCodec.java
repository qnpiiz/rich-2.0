package net.minecraft.util;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.UUID;

public final class UUIDCodec
{
    public static final Codec<UUID> CODEC = Codec.INT_STREAM.comapFlatMap((p_239778_0_) ->
    {
        return Util.validateIntStreamSize(p_239778_0_, 4).map(UUIDCodec::decodeUUID);
    }, (p_239780_0_) ->
    {
        return Arrays.stream(encodeUUID(p_239780_0_));
    });

    public static UUID decodeUUID(int[] bits)
    {
        return new UUID((long)bits[0] << 32 | (long)bits[1] & 4294967295L, (long)bits[2] << 32 | (long)bits[3] & 4294967295L);
    }

    public static int[] encodeUUID(UUID uuid)
    {
        long i = uuid.getMostSignificantBits();
        long j = uuid.getLeastSignificantBits();
        return encodeBits(i, j);
    }

    private static int[] encodeBits(long most, long least)
    {
        return new int[] {(int)(most >> 32), (int)most, (int)(least >> 32), (int)least};
    }
}
