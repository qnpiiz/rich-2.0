package net.minecraft.client.audio;

import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import org.lwjgl.openal.AL10;

public class Listener
{
    private float gain = 1.0F;
    private Vector3d clientLocation = Vector3d.ZERO;

    public void setPosition(Vector3d pos)
    {
        this.clientLocation = pos;
        AL10.alListener3f(4100, (float)pos.x, (float)pos.y, (float)pos.z);
    }

    public Vector3d getClientLocation()
    {
        return this.clientLocation;
    }

    public void setOrientation(Vector3f clientViewVector, Vector3f viewVectorRaised)
    {
        AL10.alListenerfv(4111, new float[] {clientViewVector.getX(), clientViewVector.getY(), clientViewVector.getZ(), viewVectorRaised.getX(), viewVectorRaised.getY(), viewVectorRaised.getZ()});
    }

    public void setGain(float gainIn)
    {
        AL10.alListenerf(4106, gainIn);
        this.gain = gainIn;
    }

    public float getGain()
    {
        return this.gain;
    }

    public void init()
    {
        this.setPosition(Vector3d.ZERO);
        this.setOrientation(Vector3f.ZN, Vector3f.YP);
    }
}
