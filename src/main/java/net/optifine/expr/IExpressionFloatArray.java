package net.optifine.expr;

public interface IExpressionFloatArray extends IExpression
{
    float[] eval();

default ExpressionType getExpressionType()
    {
        return ExpressionType.FLOAT_ARRAY;
    }
}
