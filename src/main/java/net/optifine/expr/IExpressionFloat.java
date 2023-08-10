package net.optifine.expr;

public interface IExpressionFloat extends IExpression
{
    float eval();

default ExpressionType getExpressionType()
    {
        return ExpressionType.FLOAT;
    }
}
