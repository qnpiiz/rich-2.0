package net.minecraft.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class BedExplosionDamageSource extends DamageSource
{
    protected BedExplosionDamageSource()
    {
        super("badRespawnPoint");
        this.setDifficultyScaled();
        this.setExplosion();
    }

    /**
     * Gets the death message that is displayed when the player dies
     */
    public ITextComponent getDeathMessage(LivingEntity entityLivingBaseIn)
    {
        ITextComponent itextcomponent = TextComponentUtils.wrapWithSquareBrackets(new TranslationTextComponent("death.attack.badRespawnPoint.link")).modifyStyle((p_233545_0_) ->
        {
            return p_233545_0_.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://bugs.mojang.com/browse/MCPE-28723")).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("MCPE-28723")));
        });
        return new TranslationTextComponent("death.attack.badRespawnPoint.message", entityLivingBaseIn.getDisplayName(), itextcomponent);
    }
}
