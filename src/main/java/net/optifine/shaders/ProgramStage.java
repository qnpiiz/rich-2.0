package net.optifine.shaders;

public enum ProgramStage
{
    NONE(""),
    SHADOW("shadow"),
    SHADOWCOMP("shadowcomp"),
    PREPARE("prepare"),
    GBUFFERS("gbuffers"),
    DEFERRED("deferred"),
    COMPOSITE("composite");

    private String name;

    private ProgramStage(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public boolean isAnyComposite()
    {
        return this == SHADOWCOMP || this == PREPARE || this == DEFERRED || this == COMPOSITE;
    }

    public boolean isMainComposite()
    {
        return this == PREPARE || this == DEFERRED || this == COMPOSITE;
    }

    public boolean isAnyShadow()
    {
        return this == SHADOW || this == SHADOWCOMP;
    }
}
