package net.minecraft.util.datafix.codec;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

public class DatapackCodec
{
    /**
     * This is the default "Vanilla and nothing else" codec. Should have a more distinct name compared to
     * CODEC
     */
    public static final DatapackCodec VANILLA_CODEC = new DatapackCodec(ImmutableList.of("vanilla"), ImmutableList.of());
    public static final Codec<DatapackCodec> CODEC = RecordCodecBuilder.create((builder) ->
    {
        return builder.group(Codec.STRING.listOf().fieldOf("Enabled").forGetter((datapackCodec) -> {
            return datapackCodec.enabled;
        }), Codec.STRING.listOf().fieldOf("Disabled").forGetter((datapackCodec) -> {
            return datapackCodec.disabled;
        })).apply(builder, DatapackCodec::new);
    });
    private final List<String> enabled;
    private final List<String> disabled;

    public DatapackCodec(List<String> enabled, List<String> disabled)
    {
        this.enabled = ImmutableList.copyOf(enabled);
        this.disabled = ImmutableList.copyOf(disabled);
    }

    public List<String> getEnabled()
    {
        return this.enabled;
    }

    public List<String> getDisabled()
    {
        return this.disabled;
    }
}
