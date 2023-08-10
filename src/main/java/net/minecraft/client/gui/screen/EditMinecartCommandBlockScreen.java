package net.minecraft.client.gui.screen;

import net.minecraft.entity.item.minecart.CommandBlockMinecartEntity;
import net.minecraft.network.play.client.CUpdateMinecartCommandBlockPacket;
import net.minecraft.tileentity.CommandBlockLogic;

public class EditMinecartCommandBlockScreen extends AbstractCommandBlockScreen
{
    private final CommandBlockLogic commandBlockLogic;

    public EditMinecartCommandBlockScreen(CommandBlockLogic p_i46595_1_)
    {
        this.commandBlockLogic = p_i46595_1_;
    }

    public CommandBlockLogic getLogic()
    {
        return this.commandBlockLogic;
    }

    int func_195236_i()
    {
        return 150;
    }

    protected void init()
    {
        super.init();
        this.trackOutput = this.getLogic().shouldTrackOutput();
        this.updateTrackOutput();
        this.commandTextField.setText(this.getLogic().getCommand());
    }

    protected void func_195235_a(CommandBlockLogic commandBlockLogicIn)
    {
        if (commandBlockLogicIn instanceof CommandBlockMinecartEntity.MinecartCommandLogic)
        {
            CommandBlockMinecartEntity.MinecartCommandLogic commandblockminecartentity$minecartcommandlogic = (CommandBlockMinecartEntity.MinecartCommandLogic)commandBlockLogicIn;
            this.mc.getConnection().sendPacket(new CUpdateMinecartCommandBlockPacket(commandblockminecartentity$minecartcommandlogic.getMinecart().getEntityId(), this.commandTextField.getText(), commandBlockLogicIn.shouldTrackOutput()));
        }
    }
}
