package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.datafix.TypeReferences;

public class ChunkGenStatus extends DataFix
{
    public ChunkGenStatus(Schema outputSchema, boolean changesType)
    {
        super(outputSchema, changesType);
    }

    public TypeRewriteRule makeRule()
    {
        Type<?> type = this.getInputSchema().getType(TypeReferences.CHUNK);
        Type<?> type1 = this.getOutputSchema().getType(TypeReferences.CHUNK);
        Type<?> type2 = type.findFieldType("Level");
        Type<?> type3 = type1.findFieldType("Level");
        Type<?> type4 = type2.findFieldType("TileTicks");
        OpticFinder<?> opticfinder = DSL.fieldFinder("Level", type2);
        OpticFinder<?> opticfinder1 = DSL.fieldFinder("TileTicks", type4);
        return TypeRewriteRule.seq(this.fixTypeEverywhereTyped("ChunkToProtoChunkFix", type, this.getOutputSchema().getType(TypeReferences.CHUNK), (p_209732_3_) ->
        {
            return p_209732_3_.updateTyped(opticfinder, type3, (p_207915_2_) -> {
                Optional <? extends Stream <? extends Dynamic<? >>> optional = p_207915_2_.getOptionalTyped(opticfinder1).flatMap((p_233158_0_) -> {
                    return p_233158_0_.write().result();
                }).flatMap((p_233159_0_) -> {
                    return p_233159_0_.asStreamOpt().result();
                });
                Dynamic<?> dynamic = p_207915_2_.get(DSL.remainderFinder());
                boolean flag = dynamic.get("TerrainPopulated").asBoolean(false) && (!dynamic.get("LightPopulated").asNumber().result().isPresent() || dynamic.get("LightPopulated").asBoolean(false));
                dynamic = dynamic.set("Status", dynamic.createString(flag ? "mobs_spawned" : "empty"));
                dynamic = dynamic.set("hasLegacyStructureData", dynamic.createBoolean(true));
                Dynamic<?> dynamic1;

                if (flag)
                {
                    Optional<ByteBuffer> optional1 = dynamic.get("Biomes").asByteBufferOpt().result();

                    if (optional1.isPresent())
                    {
                        ByteBuffer bytebuffer = optional1.get();
                        int[] aint = new int[256];

                        for (int i = 0; i < aint.length; ++i)
                        {
                            if (i < bytebuffer.capacity())
                            {
                                aint[i] = bytebuffer.get(i) & 255;
                            }
                        }

                        dynamic = dynamic.set("Biomes", dynamic.createIntList(Arrays.stream(aint)));
                    }

                    Dynamic<?> dynamic2 = dynamic;
                    List<ShortList> list = IntStream.range(0, 16).mapToObj((p_211428_0_) ->
                    {
                        return new ShortArrayList();
                    }).collect(Collectors.toList());

                    if (optional.isPresent())
                    {
                        optional.get().forEach((p_233161_1_) ->
                        {
                            int j = p_233161_1_.get("x").asInt(0);
                            int k = p_233161_1_.get("y").asInt(0);
                            int l = p_233161_1_.get("z").asInt(0);
                            short short1 = packOffsetCoordinates(j, k, l);
                            list.get(k >> 4).add(short1);
                        });
                        dynamic = dynamic.set("ToBeTicked", dynamic.createList(list.stream().map((p_233160_1_) ->
                        {
                            return dynamic2.createList(p_233160_1_.stream().map(dynamic2::createShort));
                        })));
                    }

                    dynamic1 = DataFixUtils.orElse(p_207915_2_.set(DSL.remainderFinder(), dynamic).write().result(), dynamic);
                }
                else {
                    dynamic1 = dynamic;
                }

                return type3.readTyped(dynamic1).result().orElseThrow(() -> {
                    return new IllegalStateException("Could not read the new chunk");
                }).getFirst();
            });
        }), this.writeAndRead("Structure biome inject", this.getInputSchema().getType(TypeReferences.STRUCTURE_FEATURE), this.getOutputSchema().getType(TypeReferences.STRUCTURE_FEATURE)));
    }

    private static short packOffsetCoordinates(int p_210975_0_, int p_210975_1_, int p_210975_2_)
    {
        return (short)(p_210975_0_ & 15 | (p_210975_1_ & 15) << 4 | (p_210975_2_ & 15) << 8);
    }
}
