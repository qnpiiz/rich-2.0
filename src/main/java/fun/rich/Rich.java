package fun.rich;

import fun.rich.draggable.DraggableHUD;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.Minecraft;
import fun.rich.event.EventManager;
import fun.rich.event.EventTarget;
import fun.rich.event.events.impl.input.EventInputKey;
import fun.rich.feature.Feature;
import fun.rich.feature.FeatureManager;
import fun.rich.friend.FriendManager;
import fun.rich.ui.clickgui.ClickGuiScreen;

@FieldDefaults(level = AccessLevel.PUBLIC)
public class Rich {

    DraggableHUD draggableHUD;
    FriendManager friendManager;
    FeatureManager featureManager;
    ClickGuiScreen clickGui;

    public static final String NAME = "RichClient";
    public static final String VERSION = "0.1.1";
    public static final Rich instance = new Rich();

    public void init() {
        Minecraft.getInstance().getMainWindow().setWindowTitle(NAME.concat(" v").concat(VERSION));
        draggableHUD = new DraggableHUD();
        friendManager = new FriendManager();
        featureManager = new FeatureManager();
        clickGui = new ClickGuiScreen();
        EventManager.register(this);
    }

    public void stop() {
        EventManager.unregister(this);
    }

    @EventTarget
    public void onKey(EventInputKey event) {
        featureManager.features.stream()
                .filter(feature -> feature.getBind() == event.getKey())
                .forEach(Feature::toggle);
        // TODO: Add macros to key listener
    }

    public static double deltaTime() {
        return Minecraft.getDebugFPS() > 0 ? (1.0000 / Minecraft.getDebugFPS()) : 1;
    }
}
