package net.minecraft.client.renderer.vertex;

import com.google.common.collect.ImmutableList;
import net.optifine.Config;
import net.optifine.shaders.SVertexFormat;

public class DefaultVertexFormats
{
    public static final VertexFormatElement POSITION_3F = new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.POSITION, 3);
    public static final VertexFormatElement COLOR_4UB = new VertexFormatElement(0, VertexFormatElement.Type.UBYTE, VertexFormatElement.Usage.COLOR, 4);
    public static final VertexFormatElement TEX_2F = new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2);

    /** Lightmap texture coords */
    public static final VertexFormatElement TEX_2S = new VertexFormatElement(1, VertexFormatElement.Type.SHORT, VertexFormatElement.Usage.UV, 2);
    public static final VertexFormatElement TEX_2SB = new VertexFormatElement(2, VertexFormatElement.Type.SHORT, VertexFormatElement.Usage.UV, 2);
    public static final VertexFormatElement NORMAL_3B = new VertexFormatElement(0, VertexFormatElement.Type.BYTE, VertexFormatElement.Usage.NORMAL, 3);
    public static final VertexFormatElement PADDING_1B = new VertexFormatElement(0, VertexFormatElement.Type.BYTE, VertexFormatElement.Usage.PADDING, 1);
    public static final VertexFormat BLOCK = new VertexFormat(ImmutableList.<VertexFormatElement>builder().add(POSITION_3F).add(COLOR_4UB).add(TEX_2F).add(TEX_2SB).add(NORMAL_3B).add(PADDING_1B).build());
    public static final VertexFormat ENTITY = new VertexFormat(ImmutableList.<VertexFormatElement>builder().add(POSITION_3F).add(COLOR_4UB).add(TEX_2F).add(TEX_2S).add(TEX_2SB).add(NORMAL_3B).add(PADDING_1B).build());
    public static final VertexFormat BLOCK_VANILLA = BLOCK.duplicate();
    public static final VertexFormat BLOCK_SHADERS = SVertexFormat.makeExtendedFormatBlock(BLOCK_VANILLA);
    public static final int BLOCK_VANILLA_SIZE = BLOCK_VANILLA.getSize();
    public static final int BLOCK_SHADERS_SIZE = BLOCK_SHADERS.getSize();
    public static final VertexFormat ENTITY_VANILLA = ENTITY.duplicate();
    public static final VertexFormat ENTITY_SHADERS = SVertexFormat.makeExtendedFormatEntity(ENTITY_VANILLA);
    public static final int ENTITY_VANILLA_SIZE = ENTITY_VANILLA.getSize();
    public static final int ENTITY_SHADERS_SIZE = ENTITY_SHADERS.getSize();

    @Deprecated
    public static final VertexFormat PARTICLE_POSITION_TEX_COLOR_LMAP = new VertexFormat(ImmutableList.<VertexFormatElement>builder().add(POSITION_3F).add(TEX_2F).add(COLOR_4UB).add(TEX_2SB).build());
    public static final VertexFormat POSITION = new VertexFormat(ImmutableList.<VertexFormatElement>builder().add(POSITION_3F).build());
    public static final VertexFormat POSITION_COLOR = new VertexFormat(ImmutableList.<VertexFormatElement>builder().add(POSITION_3F).add(COLOR_4UB).build());
    public static final VertexFormat POSITION_COLOR_LIGHTMAP = new VertexFormat(ImmutableList.<VertexFormatElement>builder().add(POSITION_3F).add(COLOR_4UB).add(TEX_2SB).build());
    public static final VertexFormat POSITION_TEX = new VertexFormat(ImmutableList.<VertexFormatElement>builder().add(POSITION_3F).add(TEX_2F).build());
    public static final VertexFormat POSITION_COLOR_TEX = new VertexFormat(ImmutableList.<VertexFormatElement>builder().add(POSITION_3F).add(COLOR_4UB).add(TEX_2F).build());

    @Deprecated
    public static final VertexFormat POSITION_TEX_COLOR = new VertexFormat(ImmutableList.<VertexFormatElement>builder().add(POSITION_3F).add(TEX_2F).add(COLOR_4UB).build());
    public static final VertexFormat POSITION_COLOR_TEX_LIGHTMAP = new VertexFormat(ImmutableList.<VertexFormatElement>builder().add(POSITION_3F).add(COLOR_4UB).add(TEX_2F).add(TEX_2SB).build());

    @Deprecated
    public static final VertexFormat POSITION_TEX_LIGHTMAP_COLOR = new VertexFormat(ImmutableList.<VertexFormatElement>builder().add(POSITION_3F).add(TEX_2F).add(TEX_2SB).add(COLOR_4UB).build());

    @Deprecated
    public static final VertexFormat POSITION_TEX_COLOR_NORMAL = new VertexFormat(ImmutableList.<VertexFormatElement>builder().add(POSITION_3F).add(TEX_2F).add(COLOR_4UB).add(NORMAL_3B).add(PADDING_1B).build());

    public static void updateVertexFormats()
    {
        if (Config.isShaders())
        {
            BLOCK.copyFrom(BLOCK_SHADERS);
            ENTITY.copyFrom(ENTITY_SHADERS);
        }
        else
        {
            BLOCK.copyFrom(BLOCK_VANILLA);
            ENTITY.copyFrom(ENTITY_VANILLA);
        }
    }

    static
    {
        POSITION_3F.setName("POSITION_3F");
        COLOR_4UB.setName("COLOR_4UB");
        TEX_2F.setName("TEX_2F");
        TEX_2S.setName("TEX_2S");
        TEX_2SB.setName("TEX_2SB");
        NORMAL_3B.setName("NORMAL_3B");
        PADDING_1B.setName("PADDING_1B");
        BLOCK.setName("BLOCK");
        ENTITY.setName("ENTITY");
        BLOCK_SHADERS.setName("BLOCK_SHADERS");
        ENTITY_SHADERS.setName("ENTITY_SHADERS");
        PARTICLE_POSITION_TEX_COLOR_LMAP.setName("PARTICLE_POSITION_TEX_COLOR_LMAP");
        POSITION.setName("POSITION");
        POSITION_COLOR.setName("POSITION_COLOR");
        POSITION_COLOR_LIGHTMAP.setName("POSITION_COLOR_LIGHTMAP");
        POSITION_TEX.setName("POSITION_TEX");
        POSITION_COLOR_TEX.setName("POSITION_COLOR_TEX");
        POSITION_TEX_COLOR.setName("POSITION_TEX_COLOR");
        POSITION_COLOR_TEX_LIGHTMAP.setName("POSITION_COLOR_TEX_LIGHTMAP");
        POSITION_TEX_LIGHTMAP_COLOR.setName("POSITION_TEX_LIGHTMAP_COLOR");
        POSITION_TEX_COLOR_NORMAL.setName("POSITION_TEX_COLOR_NORMAL");
    }
}
