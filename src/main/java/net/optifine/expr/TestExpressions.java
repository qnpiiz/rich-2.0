package net.optifine.expr;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TestExpressions
{
    public static void main(String[] args) throws Exception
    {
        ExpressionParser expressionparser = new ExpressionParser((IExpressionResolver)null);

        while (true)
        {
            try
            {
                InputStreamReader inputstreamreader = new InputStreamReader(System.in);
                BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
                String s = bufferedreader.readLine();

                if (s.length() <= 0)
                {
                    return;
                }

                IExpression iexpression = expressionparser.parse(s);

                if (iexpression instanceof IExpressionFloat)
                {
                    IExpressionFloat iexpressionfloat = (IExpressionFloat)iexpression;
                    float f = iexpressionfloat.eval();
                    System.out.println("" + f);
                }

                if (iexpression instanceof IExpressionBool)
                {
                    IExpressionBool iexpressionbool = (IExpressionBool)iexpression;
                    boolean flag = iexpressionbool.eval();
                    System.out.println("" + flag);
                }
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }
    }
}
