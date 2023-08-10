package fun.rich.font;

import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class FontUtil {

    public static Font getFontFromTTF(ResourceLocation loc, float fontSize, int fontType) {
        try {
            Font output = Font.createFont(fontType, FontUtil.class.getResourceAsStream("/assets/minecraft/".concat(loc.getPath())));
            output = output.deriveFont(fontSize);
            return output;
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }
}
