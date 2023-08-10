package net.minecraft.entity.ai.attributes;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.registry.Registry;

public class ModifiableAttributeInstance
{
    /** The Attribute this is an instance of */
    private final Attribute genericAttribute;
    private final Map<AttributeModifier.Operation, Set<AttributeModifier>> mapByOperation = Maps.newEnumMap(AttributeModifier.Operation.class);
    private final Map<UUID, AttributeModifier> instanceMap = new Object2ObjectArrayMap<>();
    private final Set<AttributeModifier> mapByUUID = new ObjectArraySet<>();
    private double base;
    private boolean requiresComputation = true;
    private double modifiedValue;
    private final Consumer<ModifiableAttributeInstance> modifiedValueConsumer;

    public ModifiableAttributeInstance(Attribute attribute, Consumer<ModifiableAttributeInstance> modifiedValueConsumer)
    {
        this.genericAttribute = attribute;
        this.modifiedValueConsumer = modifiedValueConsumer;
        this.base = attribute.getDefaultValue();
    }

    /**
     * Get the Attribute this is an instance of
     */
    public Attribute getAttribute()
    {
        return this.genericAttribute;
    }

    public double getBaseValue()
    {
        return this.base;
    }

    public void setBaseValue(double baseValue)
    {
        if (baseValue != this.base)
        {
            this.base = baseValue;
            this.compute();
        }
    }

    public Set<AttributeModifier> getOrCreateModifiersByOperation(AttributeModifier.Operation operation)
    {
        return this.mapByOperation.computeIfAbsent(operation, (operationIn) ->
        {
            return Sets.newHashSet();
        });
    }

    public Set<AttributeModifier> getModifierListCopy()
    {
        return ImmutableSet.copyOf(this.instanceMap.values());
    }

    @Nullable

    /**
     * Returns attribute modifier, if any, by the given UUID
     */
    public AttributeModifier getModifier(UUID uuid)
    {
        return this.instanceMap.get(uuid);
    }

    public boolean hasModifier(AttributeModifier modifier)
    {
        return this.instanceMap.get(modifier.getID()) != null;
    }

    private void applyModifier(AttributeModifier modifier)
    {
        AttributeModifier attributemodifier = this.instanceMap.putIfAbsent(modifier.getID(), modifier);

        if (attributemodifier != null)
        {
            throw new IllegalArgumentException("Modifier is already applied on this attribute!");
        }
        else
        {
            this.getOrCreateModifiersByOperation(modifier.getOperation()).add(modifier);
            this.compute();
        }
    }

    public void applyNonPersistentModifier(AttributeModifier modifier)
    {
        this.applyModifier(modifier);
    }

    public void applyPersistentModifier(AttributeModifier modifier)
    {
        this.applyModifier(modifier);
        this.mapByUUID.add(modifier);
    }

    protected void compute()
    {
        this.requiresComputation = true;
        this.modifiedValueConsumer.accept(this);
    }

    public void removeModifier(AttributeModifier modifier)
    {
        this.getOrCreateModifiersByOperation(modifier.getOperation()).remove(modifier);
        this.instanceMap.remove(modifier.getID());
        this.mapByUUID.remove(modifier);
        this.compute();
    }

    public void removeModifier(UUID identifier)
    {
        AttributeModifier attributemodifier = this.getModifier(identifier);

        if (attributemodifier != null)
        {
            this.removeModifier(attributemodifier);
        }
    }

    public boolean removePersistentModifier(UUID identifier)
    {
        AttributeModifier attributemodifier = this.getModifier(identifier);

        if (attributemodifier != null && this.mapByUUID.contains(attributemodifier))
        {
            this.removeModifier(attributemodifier);
            return true;
        }
        else
        {
            return false;
        }
    }

    public void removeAllModifiers()
    {
        for (AttributeModifier attributemodifier : this.getModifierListCopy())
        {
            this.removeModifier(attributemodifier);
        }
    }

    public double getValue()
    {
        if (this.requiresComputation)
        {
            this.modifiedValue = this.computeValue();
            this.requiresComputation = false;
        }

        return this.modifiedValue;
    }

    private double computeValue()
    {
        double d0 = this.getBaseValue();

        for (AttributeModifier attributemodifier : this.getModifiersByOperation(AttributeModifier.Operation.ADDITION))
        {
            d0 += attributemodifier.getAmount();
        }

        double d1 = d0;

        for (AttributeModifier attributemodifier1 : this.getModifiersByOperation(AttributeModifier.Operation.MULTIPLY_BASE))
        {
            d1 += d0 * attributemodifier1.getAmount();
        }

        for (AttributeModifier attributemodifier2 : this.getModifiersByOperation(AttributeModifier.Operation.MULTIPLY_TOTAL))
        {
            d1 *= 1.0D + attributemodifier2.getAmount();
        }

        return this.genericAttribute.clampValue(d1);
    }

    private Collection<AttributeModifier> getModifiersByOperation(AttributeModifier.Operation operation)
    {
        return this.mapByOperation.getOrDefault(operation, Collections.emptySet());
    }

    public void copyValuesFromInstance(ModifiableAttributeInstance instance)
    {
        this.base = instance.base;
        this.instanceMap.clear();
        this.instanceMap.putAll(instance.instanceMap);
        this.mapByUUID.clear();
        this.mapByUUID.addAll(instance.mapByUUID);
        this.mapByOperation.clear();
        instance.mapByOperation.forEach((operation, modifierSet) ->
        {
            this.getOrCreateModifiersByOperation(operation).addAll(modifierSet);
        });
        this.compute();
    }

    public CompoundNBT writeInstances()
    {
        CompoundNBT compoundnbt = new CompoundNBT();
        compoundnbt.putString("Name", Registry.ATTRIBUTE.getKey(this.genericAttribute).toString());
        compoundnbt.putDouble("Base", this.base);

        if (!this.mapByUUID.isEmpty())
        {
            ListNBT listnbt = new ListNBT();

            for (AttributeModifier attributemodifier : this.mapByUUID)
            {
                listnbt.add(attributemodifier.write());
            }

            compoundnbt.put("Modifiers", listnbt);
        }

        return compoundnbt;
    }

    public void readInstances(CompoundNBT nbt)
    {
        this.base = nbt.getDouble("Base");

        if (nbt.contains("Modifiers", 9))
        {
            ListNBT listnbt = nbt.getList("Modifiers", 10);

            for (int i = 0; i < listnbt.size(); ++i)
            {
                AttributeModifier attributemodifier = AttributeModifier.read(listnbt.getCompound(i));

                if (attributemodifier != null)
                {
                    this.instanceMap.put(attributemodifier.getID(), attributemodifier);
                    this.getOrCreateModifiersByOperation(attributemodifier.getOperation()).add(attributemodifier);
                    this.mapByUUID.add(attributemodifier);
                }
            }
        }

        this.compute();
    }
}
