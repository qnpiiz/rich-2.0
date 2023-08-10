package net.minecraft.potion;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.optifine.Config;
import net.optifine.CustomColors;

public class PotionUtils
{
    private static final IFormattableTextComponent field_242400_a = (new TranslationTextComponent("effect.none")).mergeStyle(TextFormatting.GRAY);

    public static List<EffectInstance> getEffectsFromStack(ItemStack stack)
    {
        return getEffectsFromTag(stack.getTag());
    }

    public static List<EffectInstance> mergeEffects(Potion potionIn, Collection<EffectInstance> effects)
    {
        List<EffectInstance> list = Lists.newArrayList();
        list.addAll(potionIn.getEffects());
        list.addAll(effects);
        return list;
    }

    public static List<EffectInstance> getEffectsFromTag(@Nullable CompoundNBT tag)
    {
        List<EffectInstance> list = Lists.newArrayList();
        list.addAll(getPotionTypeFromNBT(tag).getEffects());
        addCustomPotionEffectToList(tag, list);
        return list;
    }

    public static List<EffectInstance> getFullEffectsFromItem(ItemStack itemIn)
    {
        return getFullEffectsFromTag(itemIn.getTag());
    }

    public static List<EffectInstance> getFullEffectsFromTag(@Nullable CompoundNBT tag)
    {
        List<EffectInstance> list = Lists.newArrayList();
        addCustomPotionEffectToList(tag, list);
        return list;
    }

    public static void addCustomPotionEffectToList(@Nullable CompoundNBT tag, List<EffectInstance> effectList)
    {
        if (tag != null && tag.contains("CustomPotionEffects", 9))
        {
            ListNBT listnbt = tag.getList("CustomPotionEffects", 10);

            for (int i = 0; i < listnbt.size(); ++i)
            {
                CompoundNBT compoundnbt = listnbt.getCompound(i);
                EffectInstance effectinstance = EffectInstance.read(compoundnbt);

                if (effectinstance != null)
                {
                    effectList.add(effectinstance);
                }
            }
        }
    }

    public static int getColor(ItemStack itemStackIn)
    {
        CompoundNBT compoundnbt = itemStackIn.getTag();

        if (compoundnbt != null && compoundnbt.contains("CustomPotionColor", 99))
        {
            return compoundnbt.getInt("CustomPotionColor");
        }
        else
        {
            return getPotionFromItem(itemStackIn) == Potions.EMPTY ? 16253176 : getPotionColorFromEffectList(getEffectsFromStack(itemStackIn));
        }
    }

    public static int getPotionColor(Potion potionIn)
    {
        return potionIn == Potions.EMPTY ? 16253176 : getPotionColorFromEffectList(potionIn.getEffects());
    }

    public static int getPotionColorFromEffectList(Collection<EffectInstance> effects)
    {
        int i = 3694022;

        if (effects.isEmpty())
        {
            return Config.isCustomColors() ? CustomColors.getPotionColor((Effect)null, i) : 3694022;
        }
        else
        {
            float f = 0.0F;
            float f1 = 0.0F;
            float f2 = 0.0F;
            int j = 0;

            for (EffectInstance effectinstance : effects)
            {
                if (effectinstance.doesShowParticles())
                {
                    int k = effectinstance.getPotion().getLiquidColor();

                    if (Config.isCustomColors())
                    {
                        k = CustomColors.getPotionColor(effectinstance.getPotion(), k);
                    }

                    int l = effectinstance.getAmplifier() + 1;
                    f += (float)(l * (k >> 16 & 255)) / 255.0F;
                    f1 += (float)(l * (k >> 8 & 255)) / 255.0F;
                    f2 += (float)(l * (k >> 0 & 255)) / 255.0F;
                    j += l;
                }
            }

            if (j == 0)
            {
                return 0;
            }
            else
            {
                f = f / (float)j * 255.0F;
                f1 = f1 / (float)j * 255.0F;
                f2 = f2 / (float)j * 255.0F;
                return (int)f << 16 | (int)f1 << 8 | (int)f2;
            }
        }
    }

    public static Potion getPotionFromItem(ItemStack itemIn)
    {
        return getPotionTypeFromNBT(itemIn.getTag());
    }

    /**
     * If no correct potion is found, returns the default one : PotionTypes.water
     */
    public static Potion getPotionTypeFromNBT(@Nullable CompoundNBT tag)
    {
        return tag == null ? Potions.EMPTY : Potion.getPotionTypeForName(tag.getString("Potion"));
    }

    public static ItemStack addPotionToItemStack(ItemStack itemIn, Potion potionIn)
    {
        ResourceLocation resourcelocation = Registry.POTION.getKey(potionIn);

        if (potionIn == Potions.EMPTY)
        {
            itemIn.removeChildTag("Potion");
        }
        else
        {
            itemIn.getOrCreateTag().putString("Potion", resourcelocation.toString());
        }

        return itemIn;
    }

    public static ItemStack appendEffects(ItemStack itemIn, Collection<EffectInstance> effects)
    {
        if (effects.isEmpty())
        {
            return itemIn;
        }
        else
        {
            CompoundNBT compoundnbt = itemIn.getOrCreateTag();
            ListNBT listnbt = compoundnbt.getList("CustomPotionEffects", 9);

            for (EffectInstance effectinstance : effects)
            {
                listnbt.add(effectinstance.write(new CompoundNBT()));
            }

            compoundnbt.put("CustomPotionEffects", listnbt);
            return itemIn;
        }
    }

    public static void addPotionTooltip(ItemStack itemIn, List<ITextComponent> lores, float durationFactor)
    {
        List<EffectInstance> list = getEffectsFromStack(itemIn);
        List<Pair<Attribute, AttributeModifier>> list1 = Lists.newArrayList();

        if (list.isEmpty())
        {
            lores.add(field_242400_a);
        }
        else
        {
            for (EffectInstance effectinstance : list)
            {
                IFormattableTextComponent iformattabletextcomponent = new TranslationTextComponent(effectinstance.getEffectName());
                Effect effect = effectinstance.getPotion();
                Map<Attribute, AttributeModifier> map = effect.getAttributeModifierMap();

                if (!map.isEmpty())
                {
                    for (Entry<Attribute, AttributeModifier> entry : map.entrySet())
                    {
                        AttributeModifier attributemodifier = entry.getValue();
                        AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), effect.getAttributeModifierAmount(effectinstance.getAmplifier(), attributemodifier), attributemodifier.getOperation());
                        list1.add(new Pair<>(entry.getKey(), attributemodifier1));
                    }
                }

                if (effectinstance.getAmplifier() > 0)
                {
                    iformattabletextcomponent = new TranslationTextComponent("potion.withAmplifier", iformattabletextcomponent, new TranslationTextComponent("potion.potency." + effectinstance.getAmplifier()));
                }

                if (effectinstance.getDuration() > 20)
                {
                    iformattabletextcomponent = new TranslationTextComponent("potion.withDuration", iformattabletextcomponent, EffectUtils.getPotionDurationString(effectinstance, durationFactor));
                }

                lores.add(iformattabletextcomponent.mergeStyle(effect.getEffectType().getColor()));
            }
        }

        if (!list1.isEmpty())
        {
            lores.add(StringTextComponent.EMPTY);
            lores.add((new TranslationTextComponent("potion.whenDrank")).mergeStyle(TextFormatting.DARK_PURPLE));

            for (Pair<Attribute, AttributeModifier> pair : list1)
            {
                AttributeModifier attributemodifier2 = pair.getSecond();
                double d0 = attributemodifier2.getAmount();
                double d1;

                if (attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL)
                {
                    d1 = attributemodifier2.getAmount();
                }
                else
                {
                    d1 = attributemodifier2.getAmount() * 100.0D;
                }

                if (d0 > 0.0D)
                {
                    lores.add((new TranslationTextComponent("attribute.modifier.plus." + attributemodifier2.getOperation().getId(), ItemStack.DECIMALFORMAT.format(d1), new TranslationTextComponent(pair.getFirst().getAttributeName()))).mergeStyle(TextFormatting.BLUE));
                }
                else if (d0 < 0.0D)
                {
                    d1 = d1 * -1.0D;
                    lores.add((new TranslationTextComponent("attribute.modifier.take." + attributemodifier2.getOperation().getId(), ItemStack.DECIMALFORMAT.format(d1), new TranslationTextComponent(pair.getFirst().getAttributeName()))).mergeStyle(TextFormatting.RED));
                }
            }
        }
    }
}
