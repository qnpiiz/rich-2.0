package fun.rich.ui.notification;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import fun.rich.Rich;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import fun.rich.feature.impl.hud.Notifications;
import fun.rich.ui.clickgui.ClickGuiScreen;
import fun.rich.utils.render.RenderUtils;

import java.awt.*;
import java.util.List;

public class NotificationRenderer {

    private static final Minecraft mc = Minecraft.getInstance();
    private static final List<Notification> NOTIFICATIONS = Lists.newCopyOnWriteArrayList();

    public static void queue(String title, String content, int second, NotificationMode type) {
        NOTIFICATIONS.add(new Notification(title, content, type, second * 1000, mc.neverlose500_18));
    }

    public static void publish(MainWindow sr, MatrixStack matrixStack) {
        if (Rich.instance.featureManager.getFeature(Notifications.class).isEnabled() && !(mc.currentScreen instanceof ClickGuiScreen)) {
            if (!NOTIFICATIONS.isEmpty()) {
                int y = sr.getScaledHeight() - 40;
                double better;
                for (Notification notification : NOTIFICATIONS) {
                    better = mc.neverlose500_18.getStringWidth(notification.getTitle() + " " + notification.getContent());

                    if (!notification.getTimer().hasReached(notification.getTime() / 2))
                        notification.notificationTimeBarWidth = 360;
                    else
                        notification.notificationTimeBarWidth = MathHelper.easeOutBack((float) notification.notificationTimeBarWidth, 0, (float) (4 * Rich.deltaTime()));

                    if (!notification.getTimer().hasReached(notification.getTime())) {
                        notification.x = MathHelper.easeOutBack((float) notification.x, (float) (mc.getMainWindow().getScaledWidth() - better), (float) (5 * Rich.deltaTime()));
                        notification.y = MathHelper.easeOutBack((float) notification.y, (float) y, (float) (5 * Rich.deltaTime()));
                    } else {
                        notification.x = MathHelper.easeOutBack((float) notification.x, (float) (mc.getMainWindow().getScaledWidth() + 50), (float) (5 * Rich.deltaTime()));
                        notification.y = MathHelper.easeOutBack((float) notification.y, (float) y, (float) (5 * Rich.deltaTime()));
                        if (notification.x > mc.getMainWindow().getScaledWidth() + 24 && mc.player != null && mc.world != null && !mc.gameSettings.showDebugInfo)
                            NOTIFICATIONS.remove(notification);
                    }

                    RenderUtils.drawSmoothRect(notification.x - 30, notification.y - 13, mc.getMainWindow().getScaledWidth(), notification.y + 12.0f, new Color(20, 20, 20, 180).getRGB(), matrixStack);
                    RenderUtils.drawSmoothRect(notification.x - 30, notification.y - 13, notification.x + 5, notification.y + 12.0f, new Color(155, 155, 155, 50).getRGB(), matrixStack);
                    mc.notification.drawString(notification.getType().getIconString(), (float) (notification.x - 22), (float) (notification.y - 8), -1, matrixStack);

                    mc.rubik_18.drawString(notification.getTitle(), (float) (notification.x + 10), (float) (notification.y - 9), -1, matrixStack);
                    mc.rubik_17.drawString(notification.getContent(), (float) (notification.x + 10), (float) (notification.y + 2), -1, matrixStack);

                    y -= 30;
                }
            }
        }
    }
}
