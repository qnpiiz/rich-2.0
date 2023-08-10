package net.minecraft.client.gui.spectator;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.client.CSpectatePacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class PlayerMenuObject implements ISpectatorMenuObject
{
    private final GameProfile profile;
    private final ResourceLocation resourceLocation;
    private final StringTextComponent field_243475_c;

    public PlayerMenuObject(GameProfile profileIn)
    {
        this.profile = profileIn;
        Minecraft minecraft = Minecraft.getInstance();
        Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(profileIn);

        if (map.containsKey(Type.SKIN))
        {
            this.resourceLocation = minecraft.getSkinManager().loadSkin(map.get(Type.SKIN), Type.SKIN);
        }
        else
        {
            this.resourceLocation = DefaultPlayerSkin.getDefaultSkin(PlayerEntity.getUUID(profileIn));
        }

        this.field_243475_c = new StringTextComponent(profileIn.getName());
    }

    public void selectItem(SpectatorMenu menu)
    {
        Minecraft.getInstance().getConnection().sendPacket(new CSpectatePacket(this.profile.getId()));
    }

    public ITextComponent getSpectatorName()
    {
        return this.field_243475_c;
    }

    public void func_230485_a_(MatrixStack p_230485_1_, float p_230485_2_, int p_230485_3_)
    {
        Minecraft.getInstance().getTextureManager().bindTexture(this.resourceLocation);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, (float)p_230485_3_ / 255.0F);
        AbstractGui.blit(p_230485_1_, 2, 2, 12, 12, 8.0F, 8.0F, 8, 8, 64, 64);
        AbstractGui.blit(p_230485_1_, 2, 2, 12, 12, 40.0F, 8.0F, 8, 8, 64, 64);
    }

    public boolean isEnabled()
    {
        return true;
    }
}
