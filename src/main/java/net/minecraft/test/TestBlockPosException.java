package net.minecraft.test;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;

public class TestBlockPosException extends TestRuntimeException
{
    private final BlockPos field_229455_a_ = null;
    private final BlockPos field_229456_b_ = null;
    private final long field_229457_c_ = 0L;

    public String getMessage()
    {
        String s = "" + this.field_229455_a_.getX() + "," + this.field_229455_a_.getY() + "," + this.field_229455_a_.getZ() + " (relative: " + this.field_229456_b_.getX() + "," + this.field_229456_b_.getY() + "," + this.field_229456_b_.getZ() + ")";
        return super.getMessage() + " at " + s + " (t=" + this.field_229457_c_ + ")";
    }

    @Nullable
    public String func_229458_a_()
    {
        return super.getMessage() + " here";
    }

    @Nullable
    public BlockPos func_229459_c_()
    {
        return this.field_229455_a_;
    }

    private TestBlockPosException()
    {
        super("Synthetic constructor added by MCP, do not call");
        throw new RuntimeException("Synthetic constructor added by MCP, do not call");
    }
}
