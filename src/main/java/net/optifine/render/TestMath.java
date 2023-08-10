package net.optifine.render;

import java.util.Random;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;

public class TestMath
{
    static Random random = new Random();

    public static void main(String[] args)
    {
        int i = 1000000;
        dbg("Test math: " + i);

        for (int j = 0; j < 1000000; ++j)
        {
            testMatrix4f_mulTranslate();
            testMatrix4f_mulScale();
            testMatrix4f_mulQuaternion();
            testMatrix3f_mulQuaternion();
            testVector4f_transform();
            testVector3f_transform();
        }

        dbg("Done");
    }

    private static void testMatrix4f_mulTranslate()
    {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.setRandom(random);
        Matrix4f matrix4f1 = matrix4f.copy();
        float f = random.nextFloat();
        float f1 = random.nextFloat();
        float f2 = random.nextFloat();
        matrix4f.mul(Matrix4f.makeTranslate(f, f1, f2));
        matrix4f1.mulTranslate(f, f1, f2);

        if (!matrix4f1.equals(matrix4f))
        {
            dbg("*** DIFFERENT ***");
            dbg(matrix4f.toString());
            dbg(matrix4f1.toString());
        }
    }

    private static void testMatrix4f_mulScale()
    {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.setRandom(random);
        Matrix4f matrix4f1 = matrix4f.copy();
        float f = random.nextFloat();
        float f1 = random.nextFloat();
        float f2 = random.nextFloat();
        matrix4f.mul(Matrix4f.makeScale(f, f1, f2));
        matrix4f1.mulScale(f, f1, f2);

        if (!matrix4f1.equals(matrix4f))
        {
            dbg("*** DIFFERENT ***");
            dbg(matrix4f.toString());
            dbg(matrix4f1.toString());
        }
    }

    private static void testMatrix4f_mulQuaternion()
    {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.setRandom(random);
        Matrix4f matrix4f1 = matrix4f.copy();
        Quaternion quaternion = new Quaternion(random.nextFloat(), random.nextFloat(), random.nextFloat(), random.nextFloat());
        matrix4f.mul(new Matrix4f(quaternion));
        matrix4f1.mul(quaternion);

        if (!matrix4f1.equals(matrix4f))
        {
            dbg("*** DIFFERENT ***");
            dbg(matrix4f.toString());
            dbg(matrix4f1.toString());
        }
    }

    private static void testMatrix3f_mulQuaternion()
    {
        Matrix3f matrix3f = new Matrix3f();
        matrix3f.setRandom(random);
        Matrix3f matrix3f1 = matrix3f.copy();
        Quaternion quaternion = new Quaternion(random.nextFloat(), random.nextFloat(), random.nextFloat(), random.nextFloat());
        matrix3f.mul(new Matrix3f(quaternion));
        matrix3f1.mul(quaternion);

        if (!matrix3f1.equals(matrix3f))
        {
            dbg("*** DIFFERENT ***");
            dbg(matrix3f.toString());
            dbg(matrix3f1.toString());
        }
    }

    private static void testVector3f_transform()
    {
        Vector3f vector3f = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
        Vector3f vector3f1 = vector3f.copy();
        Matrix3f matrix3f = new Matrix3f();
        matrix3f.setRandom(random);
        vector3f.transform(matrix3f);
        float f = matrix3f.getTransformX(vector3f1.getX(), vector3f1.getY(), vector3f1.getZ());
        float f1 = matrix3f.getTransformY(vector3f1.getX(), vector3f1.getY(), vector3f1.getZ());
        float f2 = matrix3f.getTransformZ(vector3f1.getX(), vector3f1.getY(), vector3f1.getZ());
        vector3f1 = new Vector3f(f, f1, f2);

        if (!vector3f1.equals(vector3f))
        {
            dbg("*** DIFFERENT ***");
            dbg(vector3f.toString());
            dbg(vector3f1.toString());
        }
    }

    private static void testVector4f_transform()
    {
        Vector4f vector4f = new Vector4f(random.nextFloat(), random.nextFloat(), random.nextFloat(), random.nextFloat());
        Vector4f vector4f1 = new Vector4f(vector4f.getX(), vector4f.getY(), vector4f.getZ(), vector4f.getW());
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.setRandom(random);
        vector4f.transform(matrix4f);
        float f = matrix4f.getTransformX(vector4f1.getX(), vector4f1.getY(), vector4f1.getZ(), vector4f1.getW());
        float f1 = matrix4f.getTransformY(vector4f1.getX(), vector4f1.getY(), vector4f1.getZ(), vector4f1.getW());
        float f2 = matrix4f.getTransformZ(vector4f1.getX(), vector4f1.getY(), vector4f1.getZ(), vector4f1.getW());
        float f3 = matrix4f.getTransformW(vector4f1.getX(), vector4f1.getY(), vector4f1.getZ(), vector4f1.getW());
        vector4f1 = new Vector4f(f, f1, f2, f3);

        if (!vector4f1.equals(vector4f))
        {
            dbg("*** DIFFERENT ***");
            dbg(vector4f.toString());
            dbg(vector4f1.toString());
        }
    }

    private static void dbg(String str)
    {
        System.out.println(str);
    }
}
