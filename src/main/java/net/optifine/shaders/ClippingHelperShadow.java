package net.optifine.shaders;

import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;

public class ClippingHelperShadow extends ClippingHelper
{
    private static ClippingHelperShadow instance = new ClippingHelperShadow();
    float[] frustumTest = new float[6];
    float[][] shadowClipPlanes = new float[10][4];
    int shadowClipPlaneCount;
    float[] matInvMP = new float[16];
    float[] vecIntersection = new float[4];
    float[][] frustum;

    public ClippingHelperShadow()
    {
        super((Matrix4f)null, (Matrix4f)null);
    }

    public boolean isBoxInFrustum(double x1, double y1, double z1, double x2, double y2, double z2)
    {
        for (int i = 0; i < this.shadowClipPlaneCount; ++i)
        {
            float[] afloat = this.shadowClipPlanes[i];

            if (this.dot4(afloat, x1, y1, z1) <= 0.0D && this.dot4(afloat, x2, y1, z1) <= 0.0D && this.dot4(afloat, x1, y2, z1) <= 0.0D && this.dot4(afloat, x2, y2, z1) <= 0.0D && this.dot4(afloat, x1, y1, z2) <= 0.0D && this.dot4(afloat, x2, y1, z2) <= 0.0D && this.dot4(afloat, x1, y2, z2) <= 0.0D && this.dot4(afloat, x2, y2, z2) <= 0.0D)
            {
                return false;
            }
        }

        return true;
    }

    private double dot4(float[] plane, double x, double y, double z)
    {
        return (double)plane[0] * x + (double)plane[1] * y + (double)plane[2] * z + (double)plane[3];
    }

    private double dot3(float[] vecA, float[] vecB)
    {
        return (double)vecA[0] * (double)vecB[0] + (double)vecA[1] * (double)vecB[1] + (double)vecA[2] * (double)vecB[2];
    }

    public static ClippingHelper getInstance()
    {
        instance.init();
        return instance;
    }

    private void normalizePlane(float[] plane)
    {
        float f = MathHelper.sqrt(plane[0] * plane[0] + plane[1] * plane[1] + plane[2] * plane[2]);
        plane[0] /= f;
        plane[1] /= f;
        plane[2] /= f;
        plane[3] /= f;
    }

    private void normalize3(float[] plane)
    {
        float f = MathHelper.sqrt(plane[0] * plane[0] + plane[1] * plane[1] + plane[2] * plane[2]);

        if (f == 0.0F)
        {
            f = 1.0F;
        }

        plane[0] /= f;
        plane[1] /= f;
        plane[2] /= f;
    }

    private void assignPlane(float[] plane, float a, float b, float c, float d)
    {
        float f = (float)Math.sqrt((double)(a * a + b * b + c * c));
        plane[0] = a / f;
        plane[1] = b / f;
        plane[2] = c / f;
        plane[3] = d / f;
    }

    private void copyPlane(float[] dst, float[] src)
    {
        dst[0] = src[0];
        dst[1] = src[1];
        dst[2] = src[2];
        dst[3] = src[3];
    }

    private void cross3(float[] out, float[] a, float[] b)
    {
        out[0] = a[1] * b[2] - a[2] * b[1];
        out[1] = a[2] * b[0] - a[0] * b[2];
        out[2] = a[0] * b[1] - a[1] * b[0];
    }

    private void addShadowClipPlane(float[] plane)
    {
        this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], plane);
    }

    private float length(float x, float y, float z)
    {
        return (float)Math.sqrt((double)(x * x + y * y + z * z));
    }

    private float distance(float x1, float y1, float z1, float x2, float y2, float z2)
    {
        return this.length(x1 - x2, y1 - y2, z1 - z2);
    }

    private void makeShadowPlane(float[] shadowPlane, float[] positivePlane, float[] negativePlane, float[] vecSun)
    {
        this.cross3(this.vecIntersection, positivePlane, negativePlane);
        this.cross3(shadowPlane, this.vecIntersection, vecSun);
        this.normalize3(shadowPlane);
        float f = (float)this.dot3(positivePlane, negativePlane);
        float f1 = (float)this.dot3(shadowPlane, negativePlane);
        float f2 = this.distance(shadowPlane[0], shadowPlane[1], shadowPlane[2], negativePlane[0] * f1, negativePlane[1] * f1, negativePlane[2] * f1);
        float f3 = this.distance(positivePlane[0], positivePlane[1], positivePlane[2], negativePlane[0] * f, negativePlane[1] * f, negativePlane[2] * f);
        float f4 = f2 / f3;
        float f5 = (float)this.dot3(shadowPlane, positivePlane);
        float f6 = this.distance(shadowPlane[0], shadowPlane[1], shadowPlane[2], positivePlane[0] * f5, positivePlane[1] * f5, positivePlane[2] * f5);
        float f7 = this.distance(negativePlane[0], negativePlane[1], negativePlane[2], positivePlane[0] * f, positivePlane[1] * f, positivePlane[2] * f);
        float f8 = f6 / f7;
        shadowPlane[3] = positivePlane[3] * f4 + negativePlane[3] * f8;
    }

    public void init()
    {
    }
}
