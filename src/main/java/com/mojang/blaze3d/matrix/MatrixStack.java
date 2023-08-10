package com.mojang.blaze3d.matrix;

import com.google.common.collect.Queues;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;

public class MatrixStack
{
    Deque<MatrixStack.Entry> freeEntries = new ArrayDeque<>();
    private final Deque<MatrixStack.Entry> stack = Util.make(Queues.newArrayDeque(), (p_lambda$new$0_0_) ->
    {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.setIdentity();
        Matrix3f matrix3f = new Matrix3f();
        matrix3f.setIdentity();
        p_lambda$new$0_0_.add(new MatrixStack.Entry(matrix4f, matrix3f));
    });

    public void translate(double x, double y, double z)
    {
        MatrixStack.Entry matrixstack$entry = this.stack.getLast();
        matrixstack$entry.matrix.mulTranslate((float)x, (float)y, (float)z);
    }

    public void scale(float x, float y, float z)
    {
        MatrixStack.Entry matrixstack$entry = this.stack.getLast();
        matrixstack$entry.matrix.mulScale(x, y, z);

        if (x == y && y == z)
        {
            if (x > 0.0F)
            {
                return;
            }

            matrixstack$entry.normal.mul(-1.0F);
        }

        float f = 1.0F / x;
        float f1 = 1.0F / y;
        float f2 = 1.0F / z;
        float f3 = MathHelper.fastInvCubeRoot(f * f1 * f2);
        matrixstack$entry.normal.mul(Matrix3f.makeScaleMatrix(f3 * f, f3 * f1, f3 * f2));
    }

    public void rotate(Quaternion quaternion)
    {
        MatrixStack.Entry matrixstack$entry = this.stack.getLast();
        matrixstack$entry.matrix.mul(quaternion);
        matrixstack$entry.normal.mul(quaternion);
    }

    public void push()
    {
        MatrixStack.Entry matrixstack$entry = this.stack.getLast();
        MatrixStack.Entry matrixstack$entry1 = this.freeEntries.pollLast();

        if (matrixstack$entry1 == null)
        {
            matrixstack$entry1 = new MatrixStack.Entry(matrixstack$entry.matrix.copy(), matrixstack$entry.normal.copy());
        }
        else
        {
            matrixstack$entry1.matrix.set(matrixstack$entry.matrix);
            matrixstack$entry1.normal.set(matrixstack$entry.normal);
        }

        this.stack.addLast(matrixstack$entry1);
    }

    public void pop()
    {
        MatrixStack.Entry matrixstack$entry = this.stack.removeLast();

        if (matrixstack$entry != null)
        {
            this.freeEntries.add(matrixstack$entry);
        }
    }

    public MatrixStack.Entry getLast()
    {
        return this.stack.getLast();
    }

    public boolean clear()
    {
        return this.stack.size() == 1;
    }

    public String toString()
    {
        return this.getLast().toString();
    }

    public static final class Entry
    {
        private final Matrix4f matrix;
        private final Matrix3f normal;

        private Entry(Matrix4f matrix, Matrix3f normal)
        {
            this.matrix = matrix;
            this.normal = normal;
        }

        public Matrix4f getMatrix()
        {
            return this.matrix;
        }

        public Matrix3f getNormal()
        {
            return this.normal;
        }

        public String toString()
        {
            return this.matrix.toString() + this.normal.toString();
        }
    }
}
