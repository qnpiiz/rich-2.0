package net.minecraft.block;

public abstract class StemGrownBlock extends Block
{
    public StemGrownBlock(AbstractBlock.Properties properties)
    {
        super(properties);
    }

    public abstract StemBlock getStem();

    public abstract AttachedStemBlock getAttachedStem();
}
