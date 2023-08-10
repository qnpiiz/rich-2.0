package net.minecraft.block;

import net.minecraft.potion.Effect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;

public class FlowerBlock extends BushBlock
{
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D);
    private final Effect stewEffect;
    private final int stewEffectDuration;

    public FlowerBlock(Effect effect, int effectDuration, AbstractBlock.Properties properties)
    {
        super(properties);
        this.stewEffect = effect;

        if (effect.isInstant())
        {
            this.stewEffectDuration = effectDuration;
        }
        else
        {
            this.stewEffectDuration = effectDuration * 20;
        }
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        Vector3d vector3d = state.getOffset(worldIn, pos);
        return SHAPE.withOffset(vector3d.x, vector3d.y, vector3d.z);
    }

    /**
     * Get the OffsetType for this Block. Determines if the model is rendered slightly offset.
     */
    public AbstractBlock.OffsetType getOffsetType()
    {
        return AbstractBlock.OffsetType.XZ;
    }

    /**
     * Gets the effect that is applied when making suspicious stew.
     */
    public Effect getStewEffect()
    {
        return this.stewEffect;
    }

    /**
     * The duration of the effect granted by a suspicious stew made with this item.
     */
    public int getStewEffectDuration()
    {
        return this.stewEffectDuration;
    }
}
