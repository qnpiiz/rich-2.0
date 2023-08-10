package net.optifine.expr;

public class Parameters implements IParameters
{
    private ExpressionType[] parameterTypes;

    public Parameters(ExpressionType[] parameterTypes)
    {
        this.parameterTypes = parameterTypes;
    }

    public ExpressionType[] getParameterTypes(IExpression[] params)
    {
        return this.parameterTypes;
    }
}
