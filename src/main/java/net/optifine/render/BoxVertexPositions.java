package net.optifine.render;

import net.optifine.util.IntExpiringCache;
import net.optifine.util.RandomUtils;

public class BoxVertexPositions extends IntExpiringCache<VertexPosition[][]>
{
    public BoxVertexPositions()
    {
        super(60000 + RandomUtils.getRandomInt(10000));
    }

    protected VertexPosition[][] make()
    {
        VertexPosition[][] avertexposition = new VertexPosition[6][4];

        for (int i = 0; i < avertexposition.length; ++i)
        {
            VertexPosition[] avertexposition1 = avertexposition[i];

            for (int j = 0; j < avertexposition1.length; ++j)
            {
                avertexposition1[j] = new VertexPosition();
            }
        }

        return avertexposition;
    }
}
