package net.optifine.config;

public class GlVersion
{
    private int major;
    private int minor;
    private int release;
    private String suffix;

    public GlVersion(int major, int minor)
    {
        this(major, minor, 0);
    }

    public GlVersion(int major, int minor, int release)
    {
        this(major, minor, release, (String)null);
    }

    public GlVersion(int major, int minor, int release, String suffix)
    {
        this.major = major;
        this.minor = minor;
        this.release = release;
        this.suffix = suffix;
    }

    public int getMajor()
    {
        return this.major;
    }

    public int getMinor()
    {
        return this.minor;
    }

    public int getRelease()
    {
        return this.release;
    }

    public int toInt()
    {
        if (this.minor > 9)
        {
            return this.major * 100 + this.minor;
        }
        else
        {
            return this.release > 9 ? this.major * 100 + this.minor * 10 + 9 : this.major * 100 + this.minor * 10 + this.release;
        }
    }

    public String toString()
    {
        return this.suffix == null ? "" + this.major + "." + this.minor + "." + this.release : "" + this.major + "." + this.minor + "." + this.release + this.suffix;
    }
}
