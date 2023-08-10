package net.optifine.expr;

public interface IExpressionBool extends IExpression
{
    boolean eval();

default ExpressionType getExpressionType()
    {
        return ExpressionType.BOOL;
    }
}
