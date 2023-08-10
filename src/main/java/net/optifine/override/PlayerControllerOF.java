package net.optifine.override;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;

public class PlayerControllerOF extends PlayerController
{
    private boolean acting = false;
    private BlockPos lastClickBlockPos = null;
    private Entity lastClickEntity = null;

    public PlayerControllerOF(Minecraft mcIn, ClientPlayNetHandler netHandler)
    {
        super(mcIn, netHandler);
    }

    /**
     * Called when the player is hitting a block with an item.
     */
    public boolean clickBlock(BlockPos loc, Direction face)
    {
        this.acting = true;
        this.lastClickBlockPos = loc;
        boolean flag = super.clickBlock(loc, face);
        this.acting = false;
        return flag;
    }

    public boolean onPlayerDamageBlock(BlockPos posBlock, Direction directionFacing)
    {
        this.acting = true;
        this.lastClickBlockPos = posBlock;
        boolean flag = super.onPlayerDamageBlock(posBlock, directionFacing);
        this.acting = false;
        return flag;
    }

    public ActionResultType processRightClick(PlayerEntity player, World worldIn, Hand hand)
    {
        this.acting = true;
        ActionResultType actionresulttype = super.processRightClick(player, worldIn, hand);
        this.acting = false;
        return actionresulttype;
    }

    public ActionResultType func_217292_a(ClientPlayerEntity player, ClientWorld worldIn, Hand hand, BlockRayTraceResult rayTrace)
    {
        this.acting = true;
        this.lastClickBlockPos = rayTrace.getPos();
        ActionResultType actionresulttype = super.func_217292_a(player, worldIn, hand, rayTrace);
        this.acting = false;
        return actionresulttype;
    }

    /**
     * Handles right clicking an entity, sends a packet to the server.
     */
    public ActionResultType interactWithEntity(PlayerEntity player, Entity target, Hand hand)
    {
        this.lastClickEntity = target;
        return super.interactWithEntity(player, target, hand);
    }

    /**
     * Handles right clicking an entity from the entities side, sends a packet to the server.
     */
    public ActionResultType interactWithEntity(PlayerEntity player, Entity target, EntityRayTraceResult ray, Hand hand)
    {
        this.lastClickEntity = target;
        return super.interactWithEntity(player, target, ray, hand);
    }

    public boolean isActing()
    {
        return this.acting;
    }

    public BlockPos getLastClickBlockPos()
    {
        return this.lastClickBlockPos;
    }

    public Entity getLastClickEntity()
    {
        return this.lastClickEntity;
    }
}
