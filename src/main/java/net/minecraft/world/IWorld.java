package net.minecraft.world;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.storage.IWorldInfo;

public interface IWorld extends IBiomeReader, IDayTimeReader
{
default long func_241851_ab()
    {
        return this.getWorldInfo().getDayTime();
    }

    ITickList<Block> getPendingBlockTicks();

    ITickList<Fluid> getPendingFluidTicks();

    /**
     * Returns the world's WorldInfo object
     */
    IWorldInfo getWorldInfo();

    DifficultyInstance getDifficultyForLocation(BlockPos pos);

default Difficulty getDifficulty()
    {
        return this.getWorldInfo().getDifficulty();
    }

    /**
     * Gets the world's chunk provider
     */
    AbstractChunkProvider getChunkProvider();

default boolean chunkExists(int chunkX, int chunkZ)
    {
        return this.getChunkProvider().chunkExists(chunkX, chunkZ);
    }

    Random getRandom();

default void func_230547_a_(BlockPos p_230547_1_, Block p_230547_2_)
    {
    }

    /**
     * Plays a sound. On the server, the sound is broadcast to all nearby <em>except</em> the given player. On the
     * client, the sound only plays if the given player is the client player. Thus, this method is intended to be called
     * from code running on both sides. The client plays it locally and the server plays it for everyone else.
     */
    void playSound(@Nullable PlayerEntity player, BlockPos pos, SoundEvent soundIn, SoundCategory category, float volume, float pitch);

    void addParticle(IParticleData particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed);

    void playEvent(@Nullable PlayerEntity player, int type, BlockPos pos, int data);

default int func_234938_ad_()
    {
        return this.getDimensionType().getLogicalHeight();
    }

default void playEvent(int type, BlockPos pos, int data)
    {
        this.playEvent((PlayerEntity)null, type, pos, data);
    }
}
