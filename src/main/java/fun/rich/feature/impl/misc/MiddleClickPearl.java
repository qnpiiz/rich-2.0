package fun.rich.feature.impl.misc;

import fun.rich.event.events.impl.input.EventMiddleMouse;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import fun.rich.event.EventTarget;
import fun.rich.feature.Feature;
import fun.rich.feature.impl.FeatureCategory;

public class MiddleClickPearl extends Feature {

    public MiddleClickPearl() {
        super("MiddleClickPearl", FeatureCategory.Misc);
    }

    @EventTarget
    public void onMouseEvent(EventMiddleMouse event) {
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() == Items.ENDER_PEARL) {
                mc.player.connection.sendPacket(new CHeldItemChangePacket(i));
                mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
            }
        }
    }
}
