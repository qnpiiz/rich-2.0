package fun.rich.utils.inventory;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CEntityActionPacket;

@UtilityClass
public class InventoryUtils {

    private static final Minecraft mc = Minecraft.getInstance();

    public static boolean doesHotbarHaveAxe() {
        for (int i = 0; i < 9; i++)
            if (mc.player.inventory.getStackInSlot(i).getItem() instanceof AxeItem)
                return true;

        return false;
    }

    public static void disabler(int elytra) {
        mc.playerController.windowClick(0, elytra, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, mc.player);
        mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
        mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
        mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, elytra, 0, ClickType.PICKUP, mc.player);
    }

    public static int getSlotWithElytra() {
        for (int i = 0; i < 45; i++) {
            ItemStack itemStack = mc.player.inventoryContainer.getSlot(i).getStack();
            if (itemStack.getItem() == Items.ELYTRA)
                return i < 9 ? i + 36 : i;
        }

        return -1;
    }

    public static int getSlowWithArmor() {
        for (int i = 0; i < 45; i++) {
            ItemStack itemStack = mc.player.inventoryContainer.getSlot(i).getStack();
            if (itemStack.getItem() == Items.DIAMOND_CHESTPLATE || itemStack.getItem() == Items.GOLDEN_CHESTPLATE || itemStack.getItem() == Items.LEATHER_CHESTPLATE || itemStack.getItem() == Items.CHAINMAIL_CHESTPLATE || itemStack.getItem() == Items.IRON_LEGGINGS)
                return i < 9 ? i + 36 : i;
        }

        return -1;
    }

    public static void swapElytraToChestplate() {
        for (ItemStack stack : mc.player.inventory.armorInventory) {
            if (stack.getItem() == Items.ELYTRA) {
                int slot = getSlowWithArmor() < 9 ? getSlowWithArmor() + 36 : getSlowWithArmor();
                if (getSlowWithArmor() != -1) {
                    mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
                    mc.playerController.windowClick(6, slot, 0, ClickType.PICKUP, mc.player);
                }
            }
        }
    }

    public static boolean isActiveItemStackBlocking(LivingEntity base, int ticks) {
        if (base.isHandActive() && !base.getActiveItemStack().isEmpty()) {
            Item item = base.getActiveItemStack().getItem();

            if (item.getUseAction(base.getActiveItemStack()) == UseAction.BLOCK)
                return item.getUseDuration(base.getActiveItemStack()) - base.getItemInUseCount() >= ticks;
        }

        return false;
    }

    public static int getAxe() {
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() instanceof AxeItem)
                return i;
        }

        return 1;
    }

    public static boolean doesHotbarHaveBlock() {
        for (int i = 0; i < 9; i++)
            if (mc.player.inventory.getStackInSlot(i).getItem() instanceof BlockItem)
                return true;

        return false;
    }

    public static int getTotemAtHotbar() {
        for (int i = 0; i < 45; i++) {
            ItemStack itemStack = mc.player.inventoryContainer.getSlot(i).getStack();
            if (itemStack.getItem() == Items.TOTEM_OF_UNDYING)
                return i < 9 ? i + 36 : i;
        }

        return -1;
    }

    public static int getSwordAtHotbar() {
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() instanceof SwordItem)
                return i;
        }

        return 1;
    }
}
