package net.optifine.render;

import it.unimi.dsi.fastutil.longs.Long2ByteLinkedOpenHashMap;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.optifine.BlockPosM;
import net.optifine.Config;
import net.optifine.model.BakedQuadRetextured;
import net.optifine.model.ListQuadsOverlay;

public class RenderEnv
{
    private BlockState blockState;
    private BlockPos blockPos;
    private int blockId = -1;
    private int metadata = -1;
    private int breakingAnimation = -1;
    private int smartLeaves = -1;
    private float[] quadBounds = new float[Direction.VALUES.length * 2];
    private BitSet boundsFlags = new BitSet(3);
    private BlockModelRenderer.AmbientOcclusionFace aoFace = new BlockModelRenderer.AmbientOcclusionFace();
    private BlockPosM colorizerBlockPosM = null;
    private boolean[] borderFlags = null;
    private boolean[] borderFlags2 = null;
    private boolean[] borderFlags3 = null;
    private Direction[] borderDirections = null;
    private List<BakedQuad> listQuadsCustomizer = new ArrayList<>();
    private List<BakedQuad> listQuadsCtmMultipass = new ArrayList<>();
    private BakedQuad[] arrayQuadsCtm1 = new BakedQuad[1];
    private BakedQuad[] arrayQuadsCtm2 = new BakedQuad[2];
    private BakedQuad[] arrayQuadsCtm3 = new BakedQuad[3];
    private BakedQuad[] arrayQuadsCtm4 = new BakedQuad[4];
    private RegionRenderCacheBuilder regionRenderCacheBuilder = null;
    private ListQuadsOverlay[] listsQuadsOverlay = new ListQuadsOverlay[RenderType.CHUNK_RENDER_TYPES.length];
    private boolean overlaysRendered = false;
    private Long2ByteLinkedOpenHashMap renderSideMap = new Long2ByteLinkedOpenHashMap();
    private static final int UNKNOWN = -1;
    private static final int FALSE = 0;
    private static final int TRUE = 1;

    public RenderEnv(BlockState blockState, BlockPos blockPos)
    {
        this.blockState = blockState;
        this.blockPos = blockPos;
    }

    public void reset(BlockState blockStateIn, BlockPos blockPosIn)
    {
        if (this.blockState != blockStateIn || this.blockPos != blockPosIn)
        {
            this.blockState = blockStateIn;
            this.blockPos = blockPosIn;
            this.blockId = -1;
            this.metadata = -1;
            this.breakingAnimation = -1;
            this.smartLeaves = -1;
            this.boundsFlags.clear();
        }
    }

    public int getBlockId()
    {
        if (this.blockId < 0)
        {
            this.blockId = this.blockState.getBlockId();
        }

        return this.blockId;
    }

    public int getMetadata()
    {
        if (this.metadata < 0)
        {
            this.metadata = this.blockState.getMetadata();
        }

        return this.metadata;
    }

    public float[] getQuadBounds()
    {
        return this.quadBounds;
    }

    public BitSet getBoundsFlags()
    {
        return this.boundsFlags;
    }

    public BlockModelRenderer.AmbientOcclusionFace getAoFace()
    {
        return this.aoFace;
    }

    public boolean isBreakingAnimation(List listQuads)
    {
        if (this.breakingAnimation == -1 && listQuads.size() > 0)
        {
            if (listQuads.get(0) instanceof BakedQuadRetextured)
            {
                this.breakingAnimation = 1;
            }
            else
            {
                this.breakingAnimation = 0;
            }
        }

        return this.breakingAnimation == 1;
    }

    public boolean isBreakingAnimation(BakedQuad quad)
    {
        if (this.breakingAnimation < 0)
        {
            if (quad instanceof BakedQuadRetextured)
            {
                this.breakingAnimation = 1;
            }
            else
            {
                this.breakingAnimation = 0;
            }
        }

        return this.breakingAnimation == 1;
    }

    public boolean isBreakingAnimation()
    {
        return this.breakingAnimation == 1;
    }

    public BlockState getBlockState()
    {
        return this.blockState;
    }

    public BlockPosM getColorizerBlockPosM()
    {
        if (this.colorizerBlockPosM == null)
        {
            this.colorizerBlockPosM = new BlockPosM(0, 0, 0);
        }

        return this.colorizerBlockPosM;
    }

    public boolean[] getBorderFlags()
    {
        if (this.borderFlags == null)
        {
            this.borderFlags = new boolean[4];
        }

        return this.borderFlags;
    }

    public boolean[] getBorderFlags2()
    {
        if (this.borderFlags2 == null)
        {
            this.borderFlags2 = new boolean[4];
        }

        return this.borderFlags2;
    }

    public boolean[] getBorderFlags3()
    {
        if (this.borderFlags3 == null)
        {
            this.borderFlags3 = new boolean[4];
        }

        return this.borderFlags3;
    }

    public Direction[] getBorderDirections()
    {
        if (this.borderDirections == null)
        {
            this.borderDirections = new Direction[4];
        }

        return this.borderDirections;
    }

    public Direction[] getBorderDirections(Direction dir0, Direction dir1, Direction dir2, Direction dir3)
    {
        Direction[] adirection = this.getBorderDirections();
        adirection[0] = dir0;
        adirection[1] = dir1;
        adirection[2] = dir2;
        adirection[3] = dir3;
        return adirection;
    }

    public boolean isSmartLeaves()
    {
        if (this.smartLeaves == -1)
        {
            if (Config.isTreesSmart() && this.blockState.getBlock() instanceof LeavesBlock)
            {
                this.smartLeaves = 1;
            }
            else
            {
                this.smartLeaves = 0;
            }
        }

        return this.smartLeaves == 1;
    }

    public List<BakedQuad> getListQuadsCustomizer()
    {
        return this.listQuadsCustomizer;
    }

    public BakedQuad[] getArrayQuadsCtm(BakedQuad quad)
    {
        this.arrayQuadsCtm1[0] = quad;
        return this.arrayQuadsCtm1;
    }

    public BakedQuad[] getArrayQuadsCtm(BakedQuad quad0, BakedQuad quad1)
    {
        this.arrayQuadsCtm2[0] = quad0;
        this.arrayQuadsCtm2[1] = quad1;
        return this.arrayQuadsCtm2;
    }

    public BakedQuad[] getArrayQuadsCtm(BakedQuad quad0, BakedQuad quad1, BakedQuad quad2)
    {
        this.arrayQuadsCtm3[0] = quad0;
        this.arrayQuadsCtm3[1] = quad1;
        this.arrayQuadsCtm3[2] = quad2;
        return this.arrayQuadsCtm3;
    }

    public BakedQuad[] getArrayQuadsCtm(BakedQuad quad0, BakedQuad quad1, BakedQuad quad2, BakedQuad quad3)
    {
        this.arrayQuadsCtm4[0] = quad0;
        this.arrayQuadsCtm4[1] = quad1;
        this.arrayQuadsCtm4[2] = quad2;
        this.arrayQuadsCtm4[3] = quad3;
        return this.arrayQuadsCtm4;
    }

    public List<BakedQuad> getListQuadsCtmMultipass(BakedQuad[] quads)
    {
        this.listQuadsCtmMultipass.clear();

        if (quads != null)
        {
            for (int i = 0; i < quads.length; ++i)
            {
                BakedQuad bakedquad = quads[i];
                this.listQuadsCtmMultipass.add(bakedquad);
            }
        }

        return this.listQuadsCtmMultipass;
    }

    public RegionRenderCacheBuilder getRegionRenderCacheBuilder()
    {
        return this.regionRenderCacheBuilder;
    }

    public void setRegionRenderCacheBuilder(RegionRenderCacheBuilder regionRenderCacheBuilder)
    {
        this.regionRenderCacheBuilder = regionRenderCacheBuilder;
    }

    public ListQuadsOverlay getListQuadsOverlay(RenderType layer)
    {
        ListQuadsOverlay listquadsoverlay = this.listsQuadsOverlay[layer.ordinal()];

        if (listquadsoverlay == null)
        {
            listquadsoverlay = new ListQuadsOverlay();
            this.listsQuadsOverlay[layer.ordinal()] = listquadsoverlay;
        }

        return listquadsoverlay;
    }

    public boolean isOverlaysRendered()
    {
        return this.overlaysRendered;
    }

    public void setOverlaysRendered(boolean overlaysRendered)
    {
        this.overlaysRendered = overlaysRendered;
    }

    public Long2ByteLinkedOpenHashMap getRenderSideMap()
    {
        return this.renderSideMap;
    }
}
