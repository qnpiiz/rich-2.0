package net.optifine.shaders.config;

import net.optifine.expr.IExpressionBool;

public class ExpressionShaderOptionSwitch implements IExpressionBool
{
    private ShaderOptionSwitch shaderOption;

    public ExpressionShaderOptionSwitch(ShaderOptionSwitch shaderOption)
    {
        this.shaderOption = shaderOption;
    }

    public boolean eval()
    {
        return ShaderOptionSwitch.isTrue(this.shaderOption.getValue());
    }

    public String toString()
    {
        return "" + this.shaderOption;
    }
}
