package net.minecraft.client.renderer.texture;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.util.math.MathHelper;
import net.optifine.util.MathUtils;

public class Stitcher
{
    private static final Comparator<Stitcher.Holder> COMPARATOR_HOLDER = Comparator.comparing((Stitcher.Holder p_lambda$static$0_0_) ->
    {
        return -p_lambda$static$0_0_.height;
    }).thenComparing((p_lambda$static$1_0_) ->
    {
        return -p_lambda$static$1_0_.width;
    }).thenComparing((p_lambda$static$2_0_) ->
    {
        return p_lambda$static$2_0_.spriteInfo.getSpriteLocation();
    });
    private final int mipmapLevelStitcher;
    private final Set<Stitcher.Holder> setStitchHolders = Sets.newHashSetWithExpectedSize(256);
    private final List<Stitcher.Slot> stitchSlots = Lists.newArrayListWithCapacity(256);
    private int currentWidth;
    private int currentHeight;
    private final int maxWidth;
    private final int maxHeight;

    public Stitcher(int mipmapLevelIn, int maxWidthIn, int maxHeightIn)
    {
        this.mipmapLevelStitcher = maxHeightIn;
        this.maxWidth = mipmapLevelIn;
        this.maxHeight = maxWidthIn;
    }

    public int getCurrentWidth()
    {
        return this.currentWidth;
    }

    public int getCurrentHeight()
    {
        return this.currentHeight;
    }

    public void addSprite(TextureAtlasSprite.Info spriteInfoIn)
    {
        Stitcher.Holder stitcher$holder = new Stitcher.Holder(spriteInfoIn, this.mipmapLevelStitcher);
        this.setStitchHolders.add(stitcher$holder);
    }

    public void doStitch()
    {
        List<Stitcher.Holder> list = Lists.newArrayList(this.setStitchHolders);
        list.sort(COMPARATOR_HOLDER);

        for (Stitcher.Holder stitcher$holder : list)
        {
            if (!this.allocateSlot(stitcher$holder))
            {
                throw new StitcherException(stitcher$holder.spriteInfo, list.stream().map((p_lambda$doStitch$3_0_) ->
                {
                    return p_lambda$doStitch$3_0_.spriteInfo;
                }).collect(ImmutableList.toImmutableList()), this.currentWidth, this.currentHeight, this.maxWidth, this.maxHeight);
            }
        }

        this.currentWidth = MathHelper.smallestEncompassingPowerOfTwo(this.currentWidth);
        this.currentHeight = MathHelper.smallestEncompassingPowerOfTwo(this.currentHeight);
    }

    public void getStitchSlots(Stitcher.ISpriteLoader spriteLoaderIn)
    {
        for (Stitcher.Slot stitcher$slot : this.stitchSlots)
        {
            stitcher$slot.getAllStitchSlots((p_lambda$getStitchSlots$4_2_) ->
            {
                Stitcher.Holder stitcher$holder = p_lambda$getStitchSlots$4_2_.getStitchHolder();
                TextureAtlasSprite.Info textureatlassprite$info = stitcher$holder.spriteInfo;
                spriteLoaderIn.load(textureatlassprite$info, this.currentWidth, this.currentHeight, p_lambda$getStitchSlots$4_2_.getOriginX(), p_lambda$getStitchSlots$4_2_.getOriginY());
            });
        }
    }

    private static int getMipmapDimension(int dimensionIn, int mipmapLevelIn)
    {
        return (dimensionIn >> mipmapLevelIn) + ((dimensionIn & (1 << mipmapLevelIn) - 1) == 0 ? 0 : 1) << mipmapLevelIn;
    }

    /**
     * Attempts to find space for specified tile
     */
    private boolean allocateSlot(Stitcher.Holder holderIn)
    {
        for (Stitcher.Slot stitcher$slot : this.stitchSlots)
        {
            if (stitcher$slot.addSlot(holderIn))
            {
                return true;
            }
        }

        return this.expandAndAllocateSlot(holderIn);
    }

    /**
     * Expand stitched texture in order to make space for specified tile
     */
    private boolean expandAndAllocateSlot(Stitcher.Holder holderIn)
    {
        int i = MathHelper.smallestEncompassingPowerOfTwo(this.currentWidth);
        int j = MathHelper.smallestEncompassingPowerOfTwo(this.currentHeight);
        int k = MathHelper.smallestEncompassingPowerOfTwo(this.currentWidth + holderIn.width);
        int l = MathHelper.smallestEncompassingPowerOfTwo(this.currentHeight + holderIn.height);
        boolean flag = k <= this.maxWidth;
        boolean flag1 = l <= this.maxHeight;

        if (!flag && !flag1)
        {
            return false;
        }
        else
        {
            int i1 = MathUtils.roundDownToPowerOfTwo(this.currentHeight);
            boolean flag2 = flag && k <= 2 * i1;

            if (this.currentWidth == 0 && this.currentHeight == 0)
            {
                flag2 = true;
            }

            Stitcher.Slot stitcher$slot;

            if (flag2)
            {
                if (this.currentHeight == 0)
                {
                    this.currentHeight = holderIn.height;
                }

                stitcher$slot = new Stitcher.Slot(this.currentWidth, 0, holderIn.width, this.currentHeight);
                this.currentWidth += holderIn.width;
            }
            else
            {
                stitcher$slot = new Stitcher.Slot(0, this.currentHeight, this.currentWidth, holderIn.height);
                this.currentHeight += holderIn.height;
            }

            stitcher$slot.addSlot(holderIn);
            this.stitchSlots.add(stitcher$slot);
            return true;
        }
    }

    static class Holder
    {
        public final TextureAtlasSprite.Info spriteInfo;
        public final int width;
        public final int height;

        public Holder(TextureAtlasSprite.Info spriteInfoIn, int mipmapLevelIn)
        {
            this.spriteInfo = spriteInfoIn;
            this.width = Stitcher.getMipmapDimension(spriteInfoIn.getSpriteWidth(), mipmapLevelIn);
            this.height = Stitcher.getMipmapDimension(spriteInfoIn.getSpriteHeight(), mipmapLevelIn);
        }

        public String toString()
        {
            return "Holder{width=" + this.width + ", height=" + this.height + ", name=" + this.spriteInfo.getSpriteLocation() + '}';
        }
    }

    public interface ISpriteLoader
    {
        void load(TextureAtlasSprite.Info p_load_1_, int p_load_2_, int p_load_3_, int p_load_4_, int p_load_5_);
    }

    public static class Slot
    {
        private final int originX;
        private final int originY;
        private final int width;
        private final int height;
        private List<Stitcher.Slot> subSlots;
        private Stitcher.Holder holder;

        public Slot(int originXIn, int originYIn, int widthIn, int heightIn)
        {
            this.originX = originXIn;
            this.originY = originYIn;
            this.width = widthIn;
            this.height = heightIn;
        }

        public Stitcher.Holder getStitchHolder()
        {
            return this.holder;
        }

        public int getOriginX()
        {
            return this.originX;
        }

        public int getOriginY()
        {
            return this.originY;
        }

        public boolean addSlot(Stitcher.Holder holderIn)
        {
            if (this.holder != null)
            {
                return false;
            }
            else
            {
                int i = holderIn.width;
                int j = holderIn.height;

                if (i <= this.width && j <= this.height)
                {
                    if (i == this.width && j == this.height)
                    {
                        this.holder = holderIn;
                        return true;
                    }
                    else
                    {
                        if (this.subSlots == null)
                        {
                            this.subSlots = Lists.newArrayListWithCapacity(1);
                            this.subSlots.add(new Stitcher.Slot(this.originX, this.originY, i, j));
                            int k = this.width - i;
                            int l = this.height - j;

                            if (l > 0 && k > 0)
                            {
                                int i1 = Math.max(this.height, k);
                                int j1 = Math.max(this.width, l);

                                if (i1 >= j1)
                                {
                                    this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + j, i, l));
                                    this.subSlots.add(new Stitcher.Slot(this.originX + i, this.originY, k, this.height));
                                }
                                else
                                {
                                    this.subSlots.add(new Stitcher.Slot(this.originX + i, this.originY, k, j));
                                    this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + j, this.width, l));
                                }
                            }
                            else if (k == 0)
                            {
                                this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + j, i, l));
                            }
                            else if (l == 0)
                            {
                                this.subSlots.add(new Stitcher.Slot(this.originX + i, this.originY, k, j));
                            }
                        }

                        for (Stitcher.Slot stitcher$slot : this.subSlots)
                        {
                            if (stitcher$slot.addSlot(holderIn))
                            {
                                return true;
                            }
                        }

                        return false;
                    }
                }
                else
                {
                    return false;
                }
            }
        }

        public void getAllStitchSlots(Consumer<Stitcher.Slot> slots)
        {
            if (this.holder != null)
            {
                slots.accept(this);
            }
            else if (this.subSlots != null)
            {
                for (Stitcher.Slot stitcher$slot : this.subSlots)
                {
                    stitcher$slot.getAllStitchSlots(slots);
                }
            }
        }

        public String toString()
        {
            return "Slot{originX=" + this.originX + ", originY=" + this.originY + ", width=" + this.width + ", height=" + this.height + ", texture=" + this.holder + ", subSlots=" + this.subSlots + '}';
        }
    }
}
