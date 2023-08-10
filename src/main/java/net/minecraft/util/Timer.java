package net.minecraft.util;

public class Timer {

    public float renderPartialTicks;
    public float elapsedPartialTicks;
    private long lastSyncSysClock;
    private final float tickLength;

    private float timerSpeed = 1.0F;

    public Timer(float ticks, long lastSyncSysClock) {
        this.tickLength = 1000.0F / ticks;
        this.lastSyncSysClock = lastSyncSysClock;
    }

    public int getPartialTicks(long gameTime) {
        this.elapsedPartialTicks = (gameTime - this.lastSyncSysClock + (timerSpeed - 1.0f)) / this.tickLength;
        //this.elapsedPartialTicks = (float)(gameTime - this.lastSyncSysClock) / this.tickLength;
        this.lastSyncSysClock = gameTime;
        this.renderPartialTicks += this.elapsedPartialTicks;
        int i = (int)this.renderPartialTicks;
        this.renderPartialTicks -= (float)i;
        return i;
    }
}
