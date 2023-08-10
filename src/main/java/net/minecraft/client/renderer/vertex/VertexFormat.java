package net.minecraft.client.renderer.vertex;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import java.util.stream.Collectors;

public class VertexFormat
{
    private ImmutableList<VertexFormatElement> elements;
    private IntList offsets = new IntArrayList();

    /** The total size of this vertex format. */
    private int vertexSize;
    private String name;
    private int positionElementOffset = -1;
    private int normalElementOffset = -1;
    private int colorElementOffset = -1;
    private Int2IntMap uvOffsetsById = new Int2IntArrayMap();

    public VertexFormat(ImmutableList<VertexFormatElement> elementsIn)
    {
        this.elements = elementsIn;
        int i = 0;

        for (VertexFormatElement vertexformatelement : elementsIn)
        {
            this.offsets.add(i);
            VertexFormatElement.Usage vertexformatelement$usage = vertexformatelement.getUsage();

            if (vertexformatelement$usage == VertexFormatElement.Usage.POSITION)
            {
                this.positionElementOffset = i;
            }
            else if (vertexformatelement$usage == VertexFormatElement.Usage.NORMAL)
            {
                this.normalElementOffset = i;
            }
            else if (vertexformatelement$usage == VertexFormatElement.Usage.COLOR)
            {
                this.colorElementOffset = i;
            }
            else if (vertexformatelement$usage == VertexFormatElement.Usage.UV)
            {
                this.uvOffsetsById.put(vertexformatelement.getIndex(), i);
            }

            i += vertexformatelement.getSize();
        }

        this.vertexSize = i;
    }

    public String toString()
    {
        return "format: " + this.name + " " + this.elements.size() + " elements: " + (String)this.elements.stream().map(Object::toString).collect(Collectors.joining(" "));
    }

    public int getIntegerSize()
    {
        return this.getSize() / 4;
    }

    public int getSize()
    {
        return this.vertexSize;
    }

    public ImmutableList<VertexFormatElement> getElements()
    {
        return this.elements;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass())
        {
            VertexFormat vertexformat = (VertexFormat)p_equals_1_;
            return this.vertexSize != vertexformat.vertexSize ? false : this.elements.equals(vertexformat.elements);
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return this.elements.hashCode();
    }

    public void setupBufferState(long pointerIn)
    {
        if (!RenderSystem.isOnRenderThread())
        {
            RenderSystem.recordRenderCall(() ->
            {
                this.setupBufferState(pointerIn);
            });
        }
        else
        {
            int i = this.getSize();
            List<VertexFormatElement> list = this.getElements();

            for (int j = 0; j < list.size(); ++j)
            {
                list.get(j).setupBufferState(pointerIn + (long)this.offsets.getInt(j), i);
            }
        }
    }

    public void clearBufferState()
    {
        if (!RenderSystem.isOnRenderThread())
        {
            RenderSystem.recordRenderCall(this::clearBufferState);
        }
        else
        {
            for (VertexFormatElement vertexformatelement : this.getElements())
            {
                vertexformatelement.clearBufferState();
            }
        }
    }

    public int getOffset(int p_getOffset_1_)
    {
        return this.offsets.getInt(p_getOffset_1_);
    }

    public boolean hasPosition()
    {
        return this.positionElementOffset >= 0;
    }

    public int getPositionOffset()
    {
        return this.positionElementOffset;
    }

    public boolean hasNormal()
    {
        return this.normalElementOffset >= 0;
    }

    public int getNormalOffset()
    {
        return this.normalElementOffset;
    }

    public boolean hasColor()
    {
        return this.colorElementOffset >= 0;
    }

    public int getColorOffset()
    {
        return this.colorElementOffset;
    }

    public boolean hasUV(int p_hasUV_1_)
    {
        return this.uvOffsetsById.containsKey(p_hasUV_1_);
    }

    public int getUvOffsetById(int p_getUvOffsetById_1_)
    {
        return this.uvOffsetsById.get(p_getUvOffsetById_1_);
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String p_setName_1_)
    {
        this.name = p_setName_1_;
    }

    public void copyFrom(VertexFormat p_copyFrom_1_)
    {
        this.elements = p_copyFrom_1_.elements;
        this.offsets = p_copyFrom_1_.offsets;
        this.vertexSize = p_copyFrom_1_.vertexSize;
        this.name = p_copyFrom_1_.name;
        this.positionElementOffset = p_copyFrom_1_.positionElementOffset;
        this.normalElementOffset = p_copyFrom_1_.normalElementOffset;
        this.colorElementOffset = p_copyFrom_1_.colorElementOffset;
        this.uvOffsetsById = p_copyFrom_1_.uvOffsetsById;
    }

    public VertexFormat duplicate()
    {
        VertexFormat vertexformat = new VertexFormat(ImmutableList.of());
        vertexformat.copyFrom(this);
        return vertexformat;
    }
}
