package net.minecraft.client.renderer.model;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Orientation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.math.vector.Vector3f;

public enum ModelRotation implements IModelTransform
{
    X0_Y0(0, 0),
    X0_Y90(0, 90),
    X0_Y180(0, 180),
    X0_Y270(0, 270),
    X90_Y0(90, 0),
    X90_Y90(90, 90),
    X90_Y180(90, 180),
    X90_Y270(90, 270),
    X180_Y0(180, 0),
    X180_Y90(180, 90),
    X180_Y180(180, 180),
    X180_Y270(180, 270),
    X270_Y0(270, 0),
    X270_Y90(270, 90),
    X270_Y180(270, 180),
    X270_Y270(270, 270);

    private static final Map<Integer, ModelRotation> MAP_ROTATIONS = Arrays.stream(values()).collect(Collectors.toMap((rotation) -> {
        return rotation.combinedXY;
    }, (rotation) -> {
        return rotation;
    }));
    private final TransformationMatrix transformation;
    private final Orientation orientation;
    private final int combinedXY;

    private static int combineXY(int x, int y)
    {
        return x * 360 + y;
    }

    private ModelRotation(int x, int y)
    {
        this.combinedXY = combineXY(x, y);
        Quaternion quaternion = new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), (float)(-y), true);
        quaternion.multiply(new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), (float)(-x), true));
        Orientation orientation = Orientation.IDENTITY;

        for (int i = 0; i < y; i += 90)
        {
            orientation = orientation.func_235527_a_(Orientation.ROT_90_Y_NEG);
        }

        for (int j = 0; j < x; j += 90)
        {
            orientation = orientation.func_235527_a_(Orientation.ROT_90_X_NEG);
        }

        this.transformation = new TransformationMatrix((Vector3f)null, quaternion, (Vector3f)null, (Quaternion)null);
        this.orientation = orientation;
    }

    public TransformationMatrix getRotation()
    {
        return this.transformation;
    }

    public static ModelRotation getModelRotation(int x, int y)
    {
        return MAP_ROTATIONS.get(combineXY(MathHelper.normalizeAngle(x, 360), MathHelper.normalizeAngle(y, 360)));
    }
}
