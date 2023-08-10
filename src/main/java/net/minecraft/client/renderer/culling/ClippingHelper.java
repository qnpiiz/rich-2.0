package net.minecraft.client.renderer.culling;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector4f;
import net.optifine.render.ICamera;

public class ClippingHelper implements ICamera
{
    private final Vector4f[] frustum = new Vector4f[6];
    private double cameraX;
    private double cameraY;
    private double cameraZ;
    public boolean disabled = false;

    public ClippingHelper(Matrix4f matrix4f, Matrix4f projection)
    {
        this.calculateFrustum(matrix4f, projection);
    }

    public void setCameraPosition(double camX, double camY, double camZ)
    {
        this.cameraX = camX;
        this.cameraY = camY;
        this.cameraZ = camZ;
    }

    private void calculateFrustum(Matrix4f projection, Matrix4f frustrumMatrix)
    {
        Matrix4f matrix4f = frustrumMatrix.copy();
        matrix4f.mul(projection);
        matrix4f.transpose();
        this.setFrustumPlane(matrix4f, -1, 0, 0, 0);
        this.setFrustumPlane(matrix4f, 1, 0, 0, 1);
        this.setFrustumPlane(matrix4f, 0, -1, 0, 2);
        this.setFrustumPlane(matrix4f, 0, 1, 0, 3);
        this.setFrustumPlane(matrix4f, 0, 0, -1, 4);
        this.setFrustumPlane(matrix4f, 0, 0, 1, 5);
    }

    private void setFrustumPlane(Matrix4f frustrumMatrix, int x, int y, int z, int id)
    {
        Vector4f vector4f = new Vector4f((float)x, (float)y, (float)z, 1.0F);
        vector4f.transform(frustrumMatrix);
        vector4f.normalize();
        this.frustum[id] = vector4f;
    }

    public boolean isBoundingBoxInFrustum(AxisAlignedBB aabbIn)
    {
        return this.isBoxInFrustum(aabbIn.minX, aabbIn.minY, aabbIn.minZ, aabbIn.maxX, aabbIn.maxY, aabbIn.maxZ);
    }

    private boolean isBoxInFrustum(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
    {
        if (this.disabled)
        {
            return true;
        }
        else
        {
            float f = (float)(minX - this.cameraX);
            float f1 = (float)(minY - this.cameraY);
            float f2 = (float)(minZ - this.cameraZ);
            float f3 = (float)(maxX - this.cameraX);
            float f4 = (float)(maxY - this.cameraY);
            float f5 = (float)(maxZ - this.cameraZ);
            return this.isBoxInFrustumRaw(f, f1, f2, f3, f4, f5);
        }
    }

    private boolean isBoxInFrustumRaw(float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
    {
        for (int i = 0; i < 6; ++i)
        {
            Vector4f vector4f = this.frustum[i];
            float f = vector4f.getX();
            float f1 = vector4f.getY();
            float f2 = vector4f.getZ();
            float f3 = vector4f.getW();

            if (f * minX + f1 * minY + f2 * minZ + f3 <= 0.0F && f * maxX + f1 * minY + f2 * minZ + f3 <= 0.0F && f * minX + f1 * maxY + f2 * minZ + f3 <= 0.0F && f * maxX + f1 * maxY + f2 * minZ + f3 <= 0.0F && f * minX + f1 * minY + f2 * maxZ + f3 <= 0.0F && f * maxX + f1 * minY + f2 * maxZ + f3 <= 0.0F && f * minX + f1 * maxY + f2 * maxZ + f3 <= 0.0F && f * maxX + f1 * maxY + f2 * maxZ + f3 <= 0.0F)
            {
                return false;
            }
        }

        return true;
    }

    public boolean isBoxInFrustumFully(double p_isBoxInFrustumFully_1_, double p_isBoxInFrustumFully_3_, double p_isBoxInFrustumFully_5_, double p_isBoxInFrustumFully_7_, double p_isBoxInFrustumFully_9_, double p_isBoxInFrustumFully_11_)
    {
        if (this.disabled)
        {
            return true;
        }
        else
        {
            float f = (float)p_isBoxInFrustumFully_1_;
            float f1 = (float)p_isBoxInFrustumFully_3_;
            float f2 = (float)p_isBoxInFrustumFully_5_;
            float f3 = (float)p_isBoxInFrustumFully_7_;
            float f4 = (float)p_isBoxInFrustumFully_9_;
            float f5 = (float)p_isBoxInFrustumFully_11_;

            for (int i = 0; i < 6; ++i)
            {
                Vector4f vector4f = this.frustum[i];
                float f6 = vector4f.getX();
                float f7 = vector4f.getY();
                float f8 = vector4f.getZ();
                float f9 = vector4f.getW();

                if (i < 4)
                {
                    if (f6 * f + f7 * f1 + f8 * f2 + f9 <= 0.0F || f6 * f3 + f7 * f1 + f8 * f2 + f9 <= 0.0F || f6 * f + f7 * f4 + f8 * f2 + f9 <= 0.0F || f6 * f3 + f7 * f4 + f8 * f2 + f9 <= 0.0F || f6 * f + f7 * f1 + f8 * f5 + f9 <= 0.0F || f6 * f3 + f7 * f1 + f8 * f5 + f9 <= 0.0F || f6 * f + f7 * f4 + f8 * f5 + f9 <= 0.0F || f6 * f3 + f7 * f4 + f8 * f5 + f9 <= 0.0F)
                    {
                        return false;
                    }
                }
                else if (f6 * f + f7 * f1 + f8 * f2 + f9 <= 0.0F && f6 * f3 + f7 * f1 + f8 * f2 + f9 <= 0.0F && f6 * f + f7 * f4 + f8 * f2 + f9 <= 0.0F && f6 * f3 + f7 * f4 + f8 * f2 + f9 <= 0.0F && f6 * f + f7 * f1 + f8 * f5 + f9 <= 0.0F && f6 * f3 + f7 * f1 + f8 * f5 + f9 <= 0.0F && f6 * f + f7 * f4 + f8 * f5 + f9 <= 0.0F && f6 * f3 + f7 * f4 + f8 * f5 + f9 <= 0.0F)
                {
                    return false;
                }
            }

            return true;
        }
    }

    public Vector4f[] getFrustum()
    {
        return this.frustum;
    }
}
