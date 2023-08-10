package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class ItemUseContext
{
    @Nullable
    private final PlayerEntity player;
    private final Hand hand;
    private final BlockRayTraceResult rayTraceResult;
    private final World world;
    private final ItemStack item;

    public ItemUseContext(PlayerEntity player, Hand handIn, BlockRayTraceResult rayTraceResultIn)
    {
        this(player.world, player, handIn, player.getHeldItem(handIn), rayTraceResultIn);
    }

    protected ItemUseContext(World worldIn, @Nullable PlayerEntity player, Hand handIn, ItemStack heldItem, BlockRayTraceResult rayTraceResultIn)
    {
        this.player = player;
        this.hand = handIn;
        this.rayTraceResult = rayTraceResultIn;
        this.item = heldItem;
        this.world = worldIn;
    }

    protected final BlockRayTraceResult func_242401_i()
    {
        return this.rayTraceResult;
    }

    public BlockPos getPos()
    {
        return this.rayTraceResult.getPos();
    }

    public Direction getFace()
    {
        return this.rayTraceResult.getFace();
    }

    public Vector3d getHitVec()
    {
        return this.rayTraceResult.getHitVec();
    }

    public boolean isInside()
    {
        return this.rayTraceResult.isInside();
    }

    public ItemStack getItem()
    {
        return this.item;
    }

    @Nullable
    public PlayerEntity getPlayer()
    {
        return this.player;
    }

    public Hand getHand()
    {
        return this.hand;
    }

    public World getWorld()
    {
        return this.world;
    }

    public Direction getPlacementHorizontalFacing()
    {
        return this.player == null ? Direction.NORTH : this.player.getHorizontalFacing();
    }

    public boolean hasSecondaryUseForPlayer()
    {
        return this.player != null && this.player.isSecondaryUseActive();
    }

    public float getPlacementYaw()
    {
        return this.player == null ? 0.0F : this.player.rotationYaw;
    }
}
