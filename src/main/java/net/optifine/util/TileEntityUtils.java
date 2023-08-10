package net.optifine.util;

import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.tileentity.EnchantingTableTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.INameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.optifine.reflect.Reflector;

public class TileEntityUtils
{
    public static String getTileEntityName(IBlockReader blockAccess, BlockPos blockPos)
    {
        TileEntity tileentity = blockAccess.getTileEntity(blockPos);
        return getTileEntityName(tileentity);
    }

    public static String getTileEntityName(TileEntity te)
    {
        if (!(te instanceof INameable))
        {
            return null;
        }
        else
        {
            INameable inameable = (INameable)te;
            updateTileEntityName(te);
            return !inameable.hasCustomName() ? null : inameable.getCustomName().getUnformattedComponentText();
        }
    }

    public static void updateTileEntityName(TileEntity te)
    {
        BlockPos blockpos = te.getPos();
        ITextComponent itextcomponent = getTileEntityRawName(te);

        if (itextcomponent == null)
        {
            ITextComponent itextcomponent1 = getServerTileEntityRawName(blockpos);

            if (itextcomponent1 == null)
            {
                itextcomponent1 = new StringTextComponent("");
            }

            setTileEntityRawName(te, itextcomponent1);
        }
    }

    public static ITextComponent getServerTileEntityRawName(BlockPos blockPos)
    {
        TileEntity tileentity = IntegratedServerUtils.getTileEntity(blockPos);
        return tileentity == null ? null : getTileEntityRawName(tileentity);
    }

    public static ITextComponent getTileEntityRawName(TileEntity te)
    {
        if (te instanceof INameable)
        {
            return ((INameable)te).getCustomName();
        }
        else
        {
            return te instanceof BeaconTileEntity ? (ITextComponent)Reflector.getFieldValue(te, Reflector.TileEntityBeacon_customName) : null;
        }
    }

    public static boolean setTileEntityRawName(TileEntity te, ITextComponent name)
    {
        if (te instanceof LockableTileEntity)
        {
            ((LockableTileEntity)te).setCustomName(name);
            return true;
        }
        else if (te instanceof BannerTileEntity)
        {
            ((BannerTileEntity)te).setName(name);
            return true;
        }
        else if (te instanceof EnchantingTableTileEntity)
        {
            ((EnchantingTableTileEntity)te).setCustomName(name);
            return true;
        }
        else if (te instanceof BeaconTileEntity)
        {
            ((BeaconTileEntity)te).setCustomName(name);
            return true;
        }
        else
        {
            return false;
        }
    }
}
