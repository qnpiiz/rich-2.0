package net.minecraft.entity.ai.attributes;

import io.netty.util.internal.ThreadLocalRandom;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AttributeModifier
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final double amount;
    private final AttributeModifier.Operation operation;
    private final Supplier<String> name;
    private final UUID id;

    public AttributeModifier(String nameIn, double amountIn, AttributeModifier.Operation operationIn)
    {
        this(MathHelper.getRandomUUID(ThreadLocalRandom.current()), () ->
        {
            return nameIn;
        }, amountIn, operationIn);
    }

    public AttributeModifier(UUID uuid, String nameIn, double amountIn, AttributeModifier.Operation operationIn)
    {
        this(uuid, () ->
        {
            return nameIn;
        }, amountIn, operationIn);
    }

    public AttributeModifier(UUID uuid, Supplier<String> nameIn, double amountIn, AttributeModifier.Operation operationIn)
    {
        this.id = uuid;
        this.name = nameIn;
        this.amount = amountIn;
        this.operation = operationIn;
    }

    public UUID getID()
    {
        return this.id;
    }

    public String getName()
    {
        return this.name.get();
    }

    public AttributeModifier.Operation getOperation()
    {
        return this.operation;
    }

    public double getAmount()
    {
        return this.amount;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass())
        {
            AttributeModifier attributemodifier = (AttributeModifier)p_equals_1_;
            return Objects.equals(this.id, attributemodifier.id);
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return this.id.hashCode();
    }

    public String toString()
    {
        return "AttributeModifier{amount=" + this.amount + ", operation=" + this.operation + ", name='" + (String)this.name.get() + '\'' + ", id=" + this.id + '}';
    }

    public CompoundNBT write()
    {
        CompoundNBT compoundnbt = new CompoundNBT();
        compoundnbt.putString("Name", this.getName());
        compoundnbt.putDouble("Amount", this.amount);
        compoundnbt.putInt("Operation", this.operation.getId());
        compoundnbt.putUniqueId("UUID", this.id);
        return compoundnbt;
    }

    @Nullable
    public static AttributeModifier read(CompoundNBT nbt)
    {
        try
        {
            UUID uuid = nbt.getUniqueId("UUID");
            AttributeModifier.Operation attributemodifier$operation = AttributeModifier.Operation.byId(nbt.getInt("Operation"));
            return new AttributeModifier(uuid, nbt.getString("Name"), nbt.getDouble("Amount"), attributemodifier$operation);
        }
        catch (Exception exception)
        {
            LOGGER.warn("Unable to create attribute: {}", (Object)exception.getMessage());
            return null;
        }
    }

    public static enum Operation
    {
        ADDITION(0),
        MULTIPLY_BASE(1),
        MULTIPLY_TOTAL(2);

        private static final AttributeModifier.Operation[] VALUES = new AttributeModifier.Operation[]{ADDITION, MULTIPLY_BASE, MULTIPLY_TOTAL};
        private final int id;

        private Operation(int id)
        {
            this.id = id;
        }

        public int getId()
        {
            return this.id;
        }

        public static AttributeModifier.Operation byId(int id)
        {
            if (id >= 0 && id < VALUES.length)
            {
                return VALUES[id];
            }
            else
            {
                throw new IllegalArgumentException("No operation with value " + id);
            }
        }
    }
}
