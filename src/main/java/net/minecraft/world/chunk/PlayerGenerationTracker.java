package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.stream.Stream;
import net.minecraft.entity.player.ServerPlayerEntity;

public final class PlayerGenerationTracker
{
    private final Object2BooleanMap<ServerPlayerEntity> generatingPlayers = new Object2BooleanOpenHashMap<>();

    public Stream<ServerPlayerEntity> getGeneratingPlayers(long chunkPosIn)
    {
        return this.generatingPlayers.keySet().stream();
    }

    public void addPlayer(long chunkPosIn, ServerPlayerEntity player, boolean canGenerateChunks)
    {
        this.generatingPlayers.put(player, canGenerateChunks);
    }

    public void removePlayer(long chunkPosIn, ServerPlayerEntity player)
    {
        this.generatingPlayers.removeBoolean(player);
    }

    public void disableGeneration(ServerPlayerEntity player)
    {
        this.generatingPlayers.replace(player, true);
    }

    public void enableGeneration(ServerPlayerEntity player)
    {
        this.generatingPlayers.replace(player, false);
    }

    public boolean cannotGenerateChunks(ServerPlayerEntity player)
    {
        return this.generatingPlayers.getOrDefault(player, true);
    }

    public boolean canGeneratePlayer(ServerPlayerEntity player)
    {
        return this.generatingPlayers.getBoolean(player);
    }

    public void updatePlayerPosition(long oldChunkPos, long newChunkPos, ServerPlayerEntity player)
    {
    }
}
