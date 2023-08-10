package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;

public class SSpawnObjectPacket implements IPacket<IClientPlayNetHandler>
{
    private int entityId;
    private UUID uniqueId;
    private double x;
    private double y;
    private double z;
    private int speedX;
    private int speedY;
    private int speedZ;
    private int pitch;
    private int yaw;
    private EntityType<?> type;
    private int data;

    public SSpawnObjectPacket()
    {
    }

    public SSpawnObjectPacket(int entityId, UUID uuid, double xPos, double yPos, double zPos, float pitch, float yaw, EntityType<?> entityType, int entityData, Vector3d speedVector)
    {
        this.entityId = entityId;
        this.uniqueId = uuid;
        this.x = xPos;
        this.y = yPos;
        this.z = zPos;
        this.pitch = MathHelper.floor(pitch * 256.0F / 360.0F);
        this.yaw = MathHelper.floor(yaw * 256.0F / 360.0F);
        this.type = entityType;
        this.data = entityData;
        this.speedX = (int)(MathHelper.clamp(speedVector.x, -3.9D, 3.9D) * 8000.0D);
        this.speedY = (int)(MathHelper.clamp(speedVector.y, -3.9D, 3.9D) * 8000.0D);
        this.speedZ = (int)(MathHelper.clamp(speedVector.z, -3.9D, 3.9D) * 8000.0D);
    }

    public SSpawnObjectPacket(Entity entity)
    {
        this(entity, 0);
    }

    public SSpawnObjectPacket(Entity entityIn, int typeIn)
    {
        this(entityIn.getEntityId(), entityIn.getUniqueID(), entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ(), entityIn.rotationPitch, entityIn.rotationYaw, entityIn.getType(), typeIn, entityIn.getMotion());
    }

    public SSpawnObjectPacket(Entity entity, EntityType<?> entityType, int entityData, BlockPos pos)
    {
        this(entity.getEntityId(), entity.getUniqueID(), (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), entity.rotationPitch, entity.rotationYaw, entityType, entityData, entity.getMotion());
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.entityId = buf.readVarInt();
        this.uniqueId = buf.readUniqueId();
        this.type = Registry.ENTITY_TYPE.getByValue(buf.readVarInt());
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.pitch = buf.readByte();
        this.yaw = buf.readByte();
        this.data = buf.readInt();
        this.speedX = buf.readShort();
        this.speedY = buf.readShort();
        this.speedZ = buf.readShort();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeVarInt(this.entityId);
        buf.writeUniqueId(this.uniqueId);
        buf.writeVarInt(Registry.ENTITY_TYPE.getId(this.type));
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeByte(this.pitch);
        buf.writeByte(this.yaw);
        buf.writeInt(this.data);
        buf.writeShort(this.speedX);
        buf.writeShort(this.speedY);
        buf.writeShort(this.speedZ);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(IClientPlayNetHandler handler)
    {
        handler.handleSpawnObject(this);
    }

    public int getEntityID()
    {
        return this.entityId;
    }

    public UUID getUniqueId()
    {
        return this.uniqueId;
    }

    public double getX()
    {
        return this.x;
    }

    public double getY()
    {
        return this.y;
    }

    public double getZ()
    {
        return this.z;
    }

    public double func_218693_g()
    {
        return (double)this.speedX / 8000.0D;
    }

    public double func_218695_h()
    {
        return (double)this.speedY / 8000.0D;
    }

    public double func_218692_i()
    {
        return (double)this.speedZ / 8000.0D;
    }

    public int getPitch()
    {
        return this.pitch;
    }

    public int getYaw()
    {
        return this.yaw;
    }

    public EntityType<?> getType()
    {
        return this.type;
    }

    public int getData()
    {
        return this.data;
    }
}
