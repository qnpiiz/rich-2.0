package net.minecraft.entity;

public interface IJumpingMount
{
    void setJumpPower(int jumpPowerIn);

    boolean canJump();

    void handleStartJump(int jumpPower);

    void handleStopJump();
}
