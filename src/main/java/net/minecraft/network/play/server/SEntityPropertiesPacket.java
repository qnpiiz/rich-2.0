package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class SEntityPropertiesPacket implements IPacket<IClientPlayNetHandler>
{
    private int entityId;
    private final List<SEntityPropertiesPacket.Snapshot> snapshots = Lists.newArrayList();

    public SEntityPropertiesPacket()
    {
    }

    public SEntityPropertiesPacket(int entityIdIn, Collection<ModifiableAttributeInstance> instances)
    {
        this.entityId = entityIdIn;

        for (ModifiableAttributeInstance modifiableattributeinstance : instances)
        {
            this.snapshots.add(new SEntityPropertiesPacket.Snapshot(modifiableattributeinstance.getAttribute(), modifiableattributeinstance.getBaseValue(), modifiableattributeinstance.getModifierListCopy()));
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.entityId = buf.readVarInt();
        int i = buf.readInt();

        for (int j = 0; j < i; ++j)
        {
            ResourceLocation resourcelocation = buf.readResourceLocation();
            Attribute attribute = Registry.ATTRIBUTE.getOrDefault(resourcelocation);
            double d0 = buf.readDouble();
            List<AttributeModifier> list = Lists.newArrayList();
            int k = buf.readVarInt();

            for (int l = 0; l < k; ++l)
            {
                UUID uuid = buf.readUniqueId();
                list.add(new AttributeModifier(uuid, "Unknown synced attribute modifier", buf.readDouble(), AttributeModifier.Operation.byId(buf.readByte())));
            }

            this.snapshots.add(new SEntityPropertiesPacket.Snapshot(attribute, d0, list));
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarInt(this.entityId);
        buf.writeInt(this.snapshots.size());

        for (SEntityPropertiesPacket.Snapshot sentitypropertiespacket$snapshot : this.snapshots)
        {
            buf.writeResourceLocation(Registry.ATTRIBUTE.getKey(sentitypropertiespacket$snapshot.func_240834_a_()));
            buf.writeDouble(sentitypropertiespacket$snapshot.getBaseValue());
            buf.writeVarInt(sentitypropertiespacket$snapshot.getModifiers().size());

            for (AttributeModifier attributemodifier : sentitypropertiespacket$snapshot.getModifiers())
            {
                buf.writeUniqueId(attributemodifier.getID());
                buf.writeDouble(attributemodifier.getAmount());
                buf.writeByte(attributemodifier.getOperation().getId());
            }
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(IClientPlayNetHandler handler)
    {
        handler.handleEntityProperties(this);
    }

    public int getEntityId()
    {
        return this.entityId;
    }

    public List<SEntityPropertiesPacket.Snapshot> getSnapshots()
    {
        return this.snapshots;
    }

    public class Snapshot
    {
        private final Attribute field_240833_b_;
        private final double baseValue;
        private final Collection<AttributeModifier> modifiers;

        public Snapshot(Attribute p_i232582_2_, double p_i232582_3_, Collection<AttributeModifier> p_i232582_5_)
        {
            this.field_240833_b_ = p_i232582_2_;
            this.baseValue = p_i232582_3_;
            this.modifiers = p_i232582_5_;
        }

        public Attribute func_240834_a_()
        {
            return this.field_240833_b_;
        }

        public double getBaseValue()
        {
            return this.baseValue;
        }

        public Collection<AttributeModifier> getModifiers()
        {
            return this.modifiers;
        }
    }
}
