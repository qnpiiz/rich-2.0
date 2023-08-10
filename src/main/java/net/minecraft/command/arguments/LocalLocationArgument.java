package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Objects;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

public class LocalLocationArgument implements ILocationArgument
{
    private final double left;
    private final double up;
    private final double forwards;

    public LocalLocationArgument(double leftIn, double upIn, double forwardsIn)
    {
        this.left = leftIn;
        this.up = upIn;
        this.forwards = forwardsIn;
    }

    public Vector3d getPosition(CommandSource source)
    {
        Vector2f vector2f = source.getRotation();
        Vector3d vector3d = source.getEntityAnchorType().apply(source);
        float f = MathHelper.cos((vector2f.y + 90.0F) * ((float)Math.PI / 180F));
        float f1 = MathHelper.sin((vector2f.y + 90.0F) * ((float)Math.PI / 180F));
        float f2 = MathHelper.cos(-vector2f.x * ((float)Math.PI / 180F));
        float f3 = MathHelper.sin(-vector2f.x * ((float)Math.PI / 180F));
        float f4 = MathHelper.cos((-vector2f.x + 90.0F) * ((float)Math.PI / 180F));
        float f5 = MathHelper.sin((-vector2f.x + 90.0F) * ((float)Math.PI / 180F));
        Vector3d vector3d1 = new Vector3d((double)(f * f2), (double)f3, (double)(f1 * f2));
        Vector3d vector3d2 = new Vector3d((double)(f * f4), (double)f5, (double)(f1 * f4));
        Vector3d vector3d3 = vector3d1.crossProduct(vector3d2).scale(-1.0D);
        double d0 = vector3d1.x * this.forwards + vector3d2.x * this.up + vector3d3.x * this.left;
        double d1 = vector3d1.y * this.forwards + vector3d2.y * this.up + vector3d3.y * this.left;
        double d2 = vector3d1.z * this.forwards + vector3d2.z * this.up + vector3d3.z * this.left;
        return new Vector3d(vector3d.x + d0, vector3d.y + d1, vector3d.z + d2);
    }

    public Vector2f getRotation(CommandSource source)
    {
        return Vector2f.ZERO;
    }

    public boolean isXRelative()
    {
        return true;
    }

    public boolean isYRelative()
    {
        return true;
    }

    public boolean isZRelative()
    {
        return true;
    }

    public static LocalLocationArgument parse(StringReader reader) throws CommandSyntaxException
    {
        int i = reader.getCursor();
        double d0 = parseCoord(reader, i);

        if (reader.canRead() && reader.peek() == ' ')
        {
            reader.skip();
            double d1 = parseCoord(reader, i);

            if (reader.canRead() && reader.peek() == ' ')
            {
                reader.skip();
                double d2 = parseCoord(reader, i);
                return new LocalLocationArgument(d0, d1, d2);
            }
            else
            {
                reader.setCursor(i);
                throw Vec3Argument.POS_INCOMPLETE.createWithContext(reader);
            }
        }
        else
        {
            reader.setCursor(i);
            throw Vec3Argument.POS_INCOMPLETE.createWithContext(reader);
        }
    }

    private static double parseCoord(StringReader reader, int start) throws CommandSyntaxException
    {
        if (!reader.canRead())
        {
            throw LocationPart.EXPECTED_DOUBLE.createWithContext(reader);
        }
        else if (reader.peek() != '^')
        {
            reader.setCursor(start);
            throw Vec3Argument.POS_MIXED_TYPES.createWithContext(reader);
        }
        else
        {
            reader.skip();
            return reader.canRead() && reader.peek() != ' ' ? reader.readDouble() : 0.0D;
        }
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof LocalLocationArgument))
        {
            return false;
        }
        else
        {
            LocalLocationArgument locallocationargument = (LocalLocationArgument)p_equals_1_;
            return this.left == locallocationargument.left && this.up == locallocationargument.up && this.forwards == locallocationargument.forwards;
        }
    }

    public int hashCode()
    {
        return Objects.hash(this.left, this.up, this.forwards);
    }
}
