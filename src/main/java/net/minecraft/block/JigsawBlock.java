package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.JigsawTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.jigsaw.JigsawOrientation;
import net.minecraft.world.gen.feature.template.Template;

public class JigsawBlock extends Block implements ITileEntityProvider
{
    public static final EnumProperty<JigsawOrientation> ORIENTATION = BlockStateProperties.ORIENTATION;

    protected JigsawBlock(AbstractBlock.Properties properties)
    {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(ORIENTATION, JigsawOrientation.NORTH_UP));
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(ORIENTATION);
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
     * fine.
     */
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return state.with(ORIENTATION, rot.getOrientation().func_235531_a_(state.get(ORIENTATION)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
     */
    public BlockState mirror(BlockState state, Mirror mirrorIn)
    {
        return state.with(ORIENTATION, mirrorIn.getOrientation().func_235531_a_(state.get(ORIENTATION)));
    }

    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        Direction direction = context.getFace();
        Direction direction1;

        if (direction.getAxis() == Direction.Axis.Y)
        {
            direction1 = context.getPlacementHorizontalFacing().getOpposite();
        }
        else
        {
            direction1 = Direction.UP;
        }

        return this.getDefaultState().with(ORIENTATION, JigsawOrientation.func_239641_a_(direction, direction1));
    }

    @Nullable
    public TileEntity createNewTileEntity(IBlockReader worldIn)
    {
        return new JigsawTileEntity();
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof JigsawTileEntity && player.canUseCommandBlock())
        {
            player.openJigsaw((JigsawTileEntity)tileentity);
            return ActionResultType.func_233537_a_(worldIn.isRemote);
        }
        else
        {
            return ActionResultType.PASS;
        }
    }

    public static boolean hasJigsawMatch(Template.BlockInfo info, Template.BlockInfo info2)
    {
        Direction direction = getConnectingDirection(info.state);
        Direction direction1 = getConnectingDirection(info2.state);
        Direction direction2 = getJigsawAlignmentDirection(info.state);
        Direction direction3 = getJigsawAlignmentDirection(info2.state);
        JigsawTileEntity.OrientationType jigsawtileentity$orientationtype = JigsawTileEntity.OrientationType.func_235673_a_(info.nbt.getString("joint")).orElseGet(() ->
        {
            return direction.getAxis().isHorizontal() ? JigsawTileEntity.OrientationType.ALIGNED : JigsawTileEntity.OrientationType.ROLLABLE;
        });
        boolean flag = jigsawtileentity$orientationtype == JigsawTileEntity.OrientationType.ROLLABLE;
        return direction == direction1.getOpposite() && (flag || direction2 == direction3) && info.nbt.getString("target").equals(info2.nbt.getString("name"));
    }

    /**
     * This represents the face that the puzzle piece is on. To connect: 2 jigsaws must have their puzzle piece face
     * facing each other.
     */
    public static Direction getConnectingDirection(BlockState state)
    {
        return state.get(ORIENTATION).func_239642_b_();
    }

    /**
     * This represents the face that the line connector is on. To connect, if the OrientationType is ALIGNED, the two
     * lines must be in the same direction. (Their textures will form one straight line)
     */
    public static Direction getJigsawAlignmentDirection(BlockState state)
    {
        return state.get(ORIENTATION).func_239644_c_();
    }
}
