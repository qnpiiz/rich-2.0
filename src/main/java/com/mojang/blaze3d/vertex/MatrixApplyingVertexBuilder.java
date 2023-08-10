package com.mojang.blaze3d.vertex;

import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;

public class MatrixApplyingVertexBuilder extends DefaultColorVertexBuilder
{
    private final IVertexBuilder vertexBuilder;
    private final Matrix4f currentTransformMatrixInverted;
    private final Matrix3f normalMatrixInverted;
    private float posX;
    private float posY;
    private float posZ;
    private int u;
    private int v;
    private int light;
    private float normalX;
    private float normalY;
    private float normalZ;

    public MatrixApplyingVertexBuilder(IVertexBuilder vertexBuilder, Matrix4f currentTransformMatrix, Matrix3f normalMatrix)
    {
        this.vertexBuilder = vertexBuilder;
        this.currentTransformMatrixInverted = currentTransformMatrix.copy();
        this.currentTransformMatrixInverted.invert();
        this.normalMatrixInverted = normalMatrix.copy();
        this.normalMatrixInverted.invert();
        this.reset();
    }

    private void reset()
    {
        this.posX = 0.0F;
        this.posY = 0.0F;
        this.posZ = 0.0F;
        this.u = 0;
        this.v = 10;
        this.light = 15728880;
        this.normalX = 0.0F;
        this.normalY = 1.0F;
        this.normalZ = 0.0F;
    }

    public void endVertex()
    {
        Vector3f vector3f = new Vector3f(this.normalX, this.normalY, this.normalZ);
        vector3f.transform(this.normalMatrixInverted);
        Direction direction = Direction.getFacingFromVector(vector3f.getX(), vector3f.getY(), vector3f.getZ());
        Vector4f vector4f = new Vector4f(this.posX, this.posY, this.posZ, 1.0F);
        vector4f.transform(this.currentTransformMatrixInverted);
        vector4f.transform(Vector3f.YP.rotationDegrees(180.0F));
        vector4f.transform(Vector3f.XP.rotationDegrees(-90.0F));
        vector4f.transform(direction.getRotation());
        float f = -vector4f.getX();
        float f1 = -vector4f.getY();
        this.vertexBuilder.pos((double)this.posX, (double)this.posY, (double)this.posZ).color(1.0F, 1.0F, 1.0F, 1.0F).tex(f, f1).overlay(this.u, this.v).lightmap(this.light).normal(this.normalX, this.normalY, this.normalZ).endVertex();
        this.reset();
    }

    public IVertexBuilder pos(double x, double y, double z)
    {
        this.posX = (float)x;
        this.posY = (float)y;
        this.posZ = (float)z;
        return this;
    }

    public IVertexBuilder color(int red, int green, int blue, int alpha)
    {
        return this;
    }

    public IVertexBuilder tex(float u, float v)
    {
        return this;
    }

    public IVertexBuilder overlay(int u, int v)
    {
        this.u = u;
        this.v = v;
        return this;
    }

    public IVertexBuilder lightmap(int u, int v)
    {
        this.light = u | v << 16;
        return this;
    }

    public IVertexBuilder normal(float x, float y, float z)
    {
        this.normalX = x;
        this.normalY = y;
        this.normalZ = z;
        return this;
    }
}
