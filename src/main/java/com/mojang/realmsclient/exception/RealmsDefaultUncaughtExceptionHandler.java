package com.mojang.realmsclient.exception;

import java.lang.Thread.UncaughtExceptionHandler;
import org.apache.logging.log4j.Logger;

public class RealmsDefaultUncaughtExceptionHandler implements UncaughtExceptionHandler
{
    private final Logger field_224980_a;

    public RealmsDefaultUncaughtExceptionHandler(Logger p_i51787_1_)
    {
        this.field_224980_a = p_i51787_1_;
    }

    public void uncaughtException(Thread p_uncaughtException_1_, Throwable p_uncaughtException_2_)
    {
        this.field_224980_a.error("Caught previously unhandled exception :");
        this.field_224980_a.error(p_uncaughtException_2_);
    }
}
