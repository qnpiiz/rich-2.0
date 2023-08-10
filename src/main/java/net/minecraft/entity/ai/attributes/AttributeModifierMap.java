package net.minecraft.entity.ai.attributes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.registry.Registry;

public class AttributeModifierMap
{
    private final Map<Attribute, ModifiableAttributeInstance> attributeMap;

    public AttributeModifierMap(Map<Attribute, ModifiableAttributeInstance> attributeMap)
    {
        this.attributeMap = ImmutableMap.copyOf(attributeMap);
    }

    private ModifiableAttributeInstance getModifier(Attribute attribute)
    {
        ModifiableAttributeInstance modifiableattributeinstance = this.attributeMap.get(attribute);

        if (modifiableattributeinstance == null)
        {
            throw new IllegalArgumentException("Can't find attribute " + Registry.ATTRIBUTE.getKey(attribute));
        }
        else
        {
            return modifiableattributeinstance;
        }
    }

    public double getAttributeValue(Attribute attribute)
    {
        return this.getModifier(attribute).getValue();
    }

    public double getAttributeBaseValue(Attribute attribute)
    {
        return this.getModifier(attribute).getBaseValue();
    }

    public double getAttributeModifierValue(Attribute attribute, UUID id)
    {
        AttributeModifier attributemodifier = this.getModifier(attribute).getModifier(id);

        if (attributemodifier == null)
        {
            throw new IllegalArgumentException("Can't find modifier " + id + " on attribute " + Registry.ATTRIBUTE.getKey(attribute));
        }
        else
        {
            return attributemodifier.getAmount();
        }
    }

    @Nullable
    public ModifiableAttributeInstance createImmutableAttributeInstance(Consumer<ModifiableAttributeInstance> onChangedCallback, Attribute attribute)
    {
        ModifiableAttributeInstance modifiableattributeinstance = this.attributeMap.get(attribute);

        if (modifiableattributeinstance == null)
        {
            return null;
        }
        else
        {
            ModifiableAttributeInstance modifiableattributeinstance1 = new ModifiableAttributeInstance(attribute, onChangedCallback);
            modifiableattributeinstance1.copyValuesFromInstance(modifiableattributeinstance);
            return modifiableattributeinstance1;
        }
    }

    public static AttributeModifierMap.MutableAttribute createMutableAttribute()
    {
        return new AttributeModifierMap.MutableAttribute();
    }

    public boolean hasAttribute(Attribute attribute)
    {
        return this.attributeMap.containsKey(attribute);
    }

    public boolean hasModifier(Attribute attribute, UUID id)
    {
        ModifiableAttributeInstance modifiableattributeinstance = this.attributeMap.get(attribute);
        return modifiableattributeinstance != null && modifiableattributeinstance.getModifier(id) != null;
    }

    public static class MutableAttribute
    {
        private final Map<Attribute, ModifiableAttributeInstance> attributeMap = Maps.newHashMap();
        private boolean edited;

        private ModifiableAttributeInstance createAttributeInstance(Attribute attribute)
        {
            ModifiableAttributeInstance modifiableattributeinstance = new ModifiableAttributeInstance(attribute, (modifiableInstance) ->
            {
                if (this.edited)
                {
                    throw new UnsupportedOperationException("Tried to change value for default attribute instance: " + Registry.ATTRIBUTE.getKey(attribute));
                }
            });
            this.attributeMap.put(attribute, modifiableattributeinstance);
            return modifiableattributeinstance;
        }

        public AttributeModifierMap.MutableAttribute createMutableAttribute(Attribute attribute)
        {
            this.createAttributeInstance(attribute);
            return this;
        }

        public AttributeModifierMap.MutableAttribute createMutableAttribute(Attribute attribute, double value)
        {
            ModifiableAttributeInstance modifiableattributeinstance = this.createAttributeInstance(attribute);
            modifiableattributeinstance.setBaseValue(value);
            return this;
        }

        public AttributeModifierMap create()
        {
            this.edited = true;
            return new AttributeModifierMap(this.attributeMap);
        }
    }
}
