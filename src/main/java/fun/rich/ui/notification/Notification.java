package fun.rich.ui.notification;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import fun.rich.font.MCFontRenderer;
import fun.rich.ui.clickgui.ScreenHelper;
import fun.rich.utils.math.TimerHelper;

@Getter
public class Notification {

    private final Minecraft mc = Minecraft.getInstance();

    public static final int HEIGHT = 30;
    private final String title, content;
    private final int time;
    private final NotificationMode type;
    private final TimerHelper timer;
    private final MCFontRenderer fontRenderer;
    public double x = mc.getMainWindow().getScaledWidth();
    public double y = mc.getMainWindow().getScaledHeight();
    public double notificationTimeBarWidth;
    private final ScreenHelper screenHelper;

    public Notification(String title, String content, NotificationMode type, int second, MCFontRenderer fontRenderer) {
        this.title = title;
        this.content = content;
        this.time = second;
        this.type = type;
        this.timer = new TimerHelper();
        this.fontRenderer = fontRenderer;
        this.screenHelper = new ScreenHelper((mc.getMainWindow().getScaledWidth() - getWidth() + getWidth()), (mc.getMainWindow().getScaledHeight() - 60));
    }

    public int getWidth() {
        return Math.max(100, Math.max(this.fontRenderer.getStringWidth(this.title), this.fontRenderer.getStringWidth(this.content)) + 90);
    }

    public ScreenHelper getTranslate() {
        return screenHelper;
    }
}
