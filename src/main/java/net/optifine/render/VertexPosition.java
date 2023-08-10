package net.optifine.render;

import net.optifine.shaders.Shaders;

public class VertexPosition
{
    private int frameId;
    private float posX;
    private float posY;
    private float posZ;
    private float velocityX;
    private float velocityY;
    private float velocityZ;
    private boolean velocityValid;

    public void setPosition(int frameId, float x, float y, float z)
    {
        if (!Shaders.isShadowPass)
        {
            if (frameId != this.frameId)
            {
                if (this.frameId != 0)
                {
                    this.velocityX = x - this.posX;
                    this.velocityY = y - this.posY;
                    this.velocityZ = z - this.posZ;
                    this.velocityValid = frameId - this.frameId <= 3 && !Shaders.pointOfViewChanged;
                }

                this.frameId = frameId;
                this.posX = x;
                this.posY = y;
                this.posZ = z;
            }
        }
    }

    public boolean isVelocityValid()
    {
        return this.velocityValid;
    }

    public float getVelocityX()
    {
        return this.velocityX;
    }

    public float getVelocityY()
    {
        return this.velocityY;
    }

    public float getVelocityZ()
    {
        return this.velocityZ;
    }

    public int getFrameId()
    {
        return this.frameId;
    }
}
