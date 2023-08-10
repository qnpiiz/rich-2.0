package net.optifine.render;

public class GlCullState
{
    private boolean enabled;
    private int mode;

    public GlCullState()
    {
        this(false, 1029);
    }

    public GlCullState(boolean enabled)
    {
        this(enabled, 1029);
    }

    public GlCullState(boolean enabled, int mode)
    {
        this.enabled = enabled;
        this.mode = mode;
    }

    public void setState(boolean enabled, int mode)
    {
        this.enabled = enabled;
        this.mode = mode;
    }

    public void setState(GlCullState state)
    {
        this.enabled = state.enabled;
        this.mode = state.mode;
    }

    public void setMode(int mode)
    {
        this.mode = mode;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public void setEnabled()
    {
        this.enabled = true;
    }

    public void setDisabled()
    {
        this.enabled = false;
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    public int getMode()
    {
        return this.mode;
    }

    public String toString()
    {
        return "enabled: " + this.enabled + ", mode: " + this.mode;
    }
}
