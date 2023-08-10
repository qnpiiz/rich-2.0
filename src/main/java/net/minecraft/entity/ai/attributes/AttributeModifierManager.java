package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AttributeModifierManager
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<Attribute, ModifiableAttributeInstance> instanceMap = Maps.newHashMap();
    private final Set<ModifiableAttributeInstance> instanceSet = Sets.newHashSet();
    private final AttributeModifierMap attributeMap;

    public AttributeModifierManager(AttributeModifierMap attributeMap)
    {
        this.attributeMap = attributeMap;
    }

    private void addInstance(ModifiableAttributeInstance instance)
    {
        if (instance.getAttribute().getShouldWatch())
        {
            this.instanceSet.add(instance);
        }
    }

    public Set<ModifiableAttributeInstance> getInstances()
    {
        return this.instanceSet;
    }

    public Collection<ModifiableAttributeInstance> getWatchedInstances()
    {
        return this.instanceMap.values().stream().filter((instance) ->
        {
            return instance.getAttribute().getShouldWatch();
        }).collect(Collectors.toList());
    }

    @Nullable
    public ModifiableAttributeInstance createInstanceIfAbsent(Attribute attribute)
    {
        return this.instanceMap.computeIfAbsent(attribute, (attribute2) ->
        {
            return this.attributeMap.createImmutableAttributeInstance(this::addInstance, attribute2);
        });
    }

    public boolean hasAttributeInstance(Attribute attribute)
    {
        return this.instanceMap.get(attribute) != null || this.attributeMap.hasAttribute(attribute);
    }

    public boolean hasModifier(Attribute attribute, UUID uuid)
    {
        ModifiableAttributeInstance modifiableattributeinstance = this.instanceMap.get(attribute);
        return modifiableattributeinstance != null ? modifiableattributeinstance.getModifier(uuid) != null : this.attributeMap.hasModifier(attribute, uuid);
    }

    public double getAttributeValue(Attribute attribute)
    {
        ModifiableAttributeInstance modifiableattributeinstance = this.instanceMap.get(attribute);
        return modifiableattributeinstance != null ? modifiableattributeinstance.getValue() : this.attributeMap.getAttributeValue(attribute);
    }

    public double getAttributeBaseValue(Attribute attribute)
    {
        ModifiableAttributeInstance modifiableattributeinstance = this.instanceMap.get(attribute);
        return modifiableattributeinstance != null ? modifiableattributeinstance.getBaseValue() : this.attributeMap.getAttributeBaseValue(attribute);
    }

    public double getModifierValue(Attribute attribute, UUID uuid)
    {
        ModifiableAttributeInstance modifiableattributeinstance = this.instanceMap.get(attribute);
        return modifiableattributeinstance != null ? modifiableattributeinstance.getModifier(uuid).getAmount() : this.attributeMap.getAttributeModifierValue(attribute, uuid);
    }

    public void removeModifiers(Multimap<Attribute, AttributeModifier> map)
    {
        map.asMap().forEach((attribute, modifiers) ->
        {
            ModifiableAttributeInstance modifiableattributeinstance = this.instanceMap.get(attribute);

            if (modifiableattributeinstance != null)
            {
                modifiers.forEach(modifiableattributeinstance::removeModifier);
            }
        });
    }

    public void reapplyModifiers(Multimap<Attribute, AttributeModifier> map)
    {
        map.forEach((attribute, modifiers) ->
        {
            ModifiableAttributeInstance modifiableattributeinstance = this.createInstanceIfAbsent(attribute);

            if (modifiableattributeinstance != null)
            {
                modifiableattributeinstance.removeModifier(modifiers);
                modifiableattributeinstance.applyNonPersistentModifier(modifiers);
            }
        });
    }

    public void refreshOnRespawn(AttributeModifierManager manager)
    {
        manager.instanceMap.values().forEach((modifiableInstance) ->
        {
            ModifiableAttributeInstance modifiableattributeinstance = this.createInstanceIfAbsent(modifiableInstance.getAttribute());

            if (modifiableattributeinstance != null)
            {
                modifiableattributeinstance.copyValuesFromInstance(modifiableInstance);
            }
        });
    }

    public ListNBT serialize()
    {
        ListNBT listnbt = new ListNBT();

        for (ModifiableAttributeInstance modifiableattributeinstance : this.instanceMap.values())
        {
            listnbt.add(modifiableattributeinstance.writeInstances());
        }

        return listnbt;
    }

    public void deserialize(ListNBT nbt)
    {
        for (int i = 0; i < nbt.size(); ++i)
        {
            CompoundNBT compoundnbt = nbt.getCompound(i);
            String s = compoundnbt.getString("Name");
            Util.acceptOrElse(Registry.ATTRIBUTE.getOptional(ResourceLocation.tryCreate(s)), (attribute) ->
            {
                ModifiableAttributeInstance modifiableattributeinstance = this.createInstanceIfAbsent(attribute);

                if (modifiableattributeinstance != null)
                {
                    modifiableattributeinstance.readInstances(compoundnbt);
                }
            }, () ->
            {
                LOGGER.warn("Ignoring unknown attribute '{}'", (Object)s);
            });
        }
    }
}
