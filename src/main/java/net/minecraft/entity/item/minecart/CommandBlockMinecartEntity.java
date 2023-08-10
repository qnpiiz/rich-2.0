package net.minecraft.entity.item.minecart;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class CommandBlockMinecartEntity extends AbstractMinecartEntity
{
    private static final DataParameter<String> COMMAND = EntityDataManager.createKey(CommandBlockMinecartEntity.class, DataSerializers.STRING);
    private static final DataParameter<ITextComponent> LAST_OUTPUT = EntityDataManager.createKey(CommandBlockMinecartEntity.class, DataSerializers.TEXT_COMPONENT);
    private final CommandBlockLogic commandBlockLogic = new CommandBlockMinecartEntity.MinecartCommandLogic();

    /** Cooldown before command block logic runs again in ticks */
    private int activatorRailCooldown;

    public CommandBlockMinecartEntity(EntityType <? extends CommandBlockMinecartEntity > type, World world)
    {
        super(type, world);
    }

    public CommandBlockMinecartEntity(World worldIn, double x, double y, double z)
    {
        super(EntityType.COMMAND_BLOCK_MINECART, worldIn, x, y, z);
    }

    protected void registerData()
    {
        super.registerData();
        this.getDataManager().register(COMMAND, "");
        this.getDataManager().register(LAST_OUTPUT, StringTextComponent.EMPTY);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readAdditional(CompoundNBT compound)
    {
        super.readAdditional(compound);
        this.commandBlockLogic.read(compound);
        this.getDataManager().set(COMMAND, this.getCommandBlockLogic().getCommand());
        this.getDataManager().set(LAST_OUTPUT, this.getCommandBlockLogic().getLastOutput());
    }

    protected void writeAdditional(CompoundNBT compound)
    {
        super.writeAdditional(compound);
        this.commandBlockLogic.write(compound);
    }

    public AbstractMinecartEntity.Type getMinecartType()
    {
        return AbstractMinecartEntity.Type.COMMAND_BLOCK;
    }

    public BlockState getDefaultDisplayTile()
    {
        return Blocks.COMMAND_BLOCK.getDefaultState();
    }

    public CommandBlockLogic getCommandBlockLogic()
    {
        return this.commandBlockLogic;
    }

    /**
     * Called every tick the minecart is on an activator rail.
     */
    public void onActivatorRailPass(int x, int y, int z, boolean receivingPower)
    {
        if (receivingPower && this.ticksExisted - this.activatorRailCooldown >= 4)
        {
            this.getCommandBlockLogic().trigger(this.world);
            this.activatorRailCooldown = this.ticksExisted;
        }
    }

    public ActionResultType processInitialInteract(PlayerEntity player, Hand hand)
    {
        return this.commandBlockLogic.tryOpenEditCommandBlock(player);
    }

    public void notifyDataManagerChange(DataParameter<?> key)
    {
        super.notifyDataManagerChange(key);

        if (LAST_OUTPUT.equals(key))
        {
            try
            {
                this.commandBlockLogic.setLastOutput(this.getDataManager().get(LAST_OUTPUT));
            }
            catch (Throwable throwable)
            {
            }
        }
        else if (COMMAND.equals(key))
        {
            this.commandBlockLogic.setCommand(this.getDataManager().get(COMMAND));
        }
    }

    /**
     * Checks if players can use this entity to access operator (permission level 2) commands either directly or
     * indirectly, such as give or setblock. A similar method exists for entities at {@link
     * net.minecraft.tileentity.TileEntity#onlyOpsCanSetNbt()}.<p>For example, {@link
     * net.minecraft.entity.item.EntityMinecartCommandBlock#ignoreItemEntityData() command block minecarts} and {@link
     * net.minecraft.entity.item.EntityMinecartMobSpawner#ignoreItemEntityData() mob spawner minecarts} (spawning
     * command block minecarts or drops) are considered accessible.</p>@return true if this entity offers ways for
     * unauthorized players to use restricted commands
     */
    public boolean ignoreItemEntityData()
    {
        return true;
    }

    public class MinecartCommandLogic extends CommandBlockLogic
    {
        public ServerWorld getWorld()
        {
            return (ServerWorld)CommandBlockMinecartEntity.this.world;
        }

        public void updateCommand()
        {
            CommandBlockMinecartEntity.this.getDataManager().set(CommandBlockMinecartEntity.COMMAND, this.getCommand());
            CommandBlockMinecartEntity.this.getDataManager().set(CommandBlockMinecartEntity.LAST_OUTPUT, this.getLastOutput());
        }

        public Vector3d getPositionVector()
        {
            return CommandBlockMinecartEntity.this.getPositionVec();
        }

        public CommandBlockMinecartEntity getMinecart()
        {
            return CommandBlockMinecartEntity.this;
        }

        public CommandSource getCommandSource()
        {
            return new CommandSource(this, CommandBlockMinecartEntity.this.getPositionVec(), CommandBlockMinecartEntity.this.getPitchYaw(), this.getWorld(), 2, this.getName().getString(), CommandBlockMinecartEntity.this.getDisplayName(), this.getWorld().getServer(), CommandBlockMinecartEntity.this);
        }
    }
}
