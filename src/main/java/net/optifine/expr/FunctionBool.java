package net.optifine.expr;

public class FunctionBool implements IExpressionBool
{
    private FunctionType type;
    private IExpression[] arguments;

    public FunctionBool(FunctionType type, IExpression[] arguments)
    {
        this.type = type;
        this.arguments = arguments;
    }

    public boolean eval()
    {
        return this.type.evalBool(this.arguments);
    }

    public String toString()
    {
        return "" + this.type + "()";
    }
}
