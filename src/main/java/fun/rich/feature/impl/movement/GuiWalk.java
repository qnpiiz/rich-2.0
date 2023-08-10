package fun.rich.feature.impl.movement;

import fun.rich.event.events.impl.player.EventUpdate;
import fun.rich.feature.impl.FeatureCategory;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import fun.rich.event.EventTarget;
import fun.rich.feature.Feature;

import java.util.Arrays;

public class GuiWalk extends Feature {

    public GuiWalk() {
        super("GuiWalk", FeatureCategory.Movement);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        KeyBinding[] keys = {
                mc.gameSettings.keyBindForward,
                mc.gameSettings.keyBindBack,
                mc.gameSettings.keyBindLeft,
                mc.gameSettings.keyBindRight,
                mc.gameSettings.keyBindJump,
                mc.gameSettings.keyBindSprint
        };

        if (!(mc.currentScreen instanceof ChatScreen)) {
            Arrays.stream(keys).forEach(keyBinding -> keyBinding.setPressed(InputMappings.isKeyDown(mc.getMainWindow().getHandle(), keyBinding.getDefault().getKeyCode())));
        }
    }
}
