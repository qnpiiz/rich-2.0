package net.optifine.entity.model.anim;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.optifine.expr.IExpressionFloat;

public class ModelVariableFloat implements IExpressionFloat
{
    private String name;
    private ModelRenderer modelRenderer;
    private ModelVariableType enumModelVariable;

    public ModelVariableFloat(String name, ModelRenderer modelRenderer, ModelVariableType enumModelVariable)
    {
        this.name = name;
        this.modelRenderer = modelRenderer;
        this.enumModelVariable = enumModelVariable;
    }

    public float eval()
    {
        return this.getValue();
    }

    public float getValue()
    {
        return this.enumModelVariable.getFloat(this.modelRenderer);
    }

    public void setValue(float value)
    {
        this.enumModelVariable.setFloat(this.modelRenderer, value);
    }

    public String toString()
    {
        return this.name;
    }
}
