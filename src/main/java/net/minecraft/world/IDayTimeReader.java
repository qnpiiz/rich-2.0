package net.minecraft.world;

public interface IDayTimeReader extends IWorldReader
{
    long func_241851_ab();

default float getMoonFactor()
    {
        return DimensionType.MOON_PHASE_FACTORS[this.getDimensionType().getMoonPhase(this.func_241851_ab())];
    }

default float func_242415_f(float p_242415_1_)
    {
        return this.getDimensionType().getCelestrialAngleByTime(this.func_241851_ab());
    }

default int getMoonPhase()
    {
        return this.getDimensionType().getMoonPhase(this.func_241851_ab());
    }
}
