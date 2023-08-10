package net.minecraft.server.management;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Dynamic;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.network.play.server.SHeldItemChangePacket;
import net.minecraft.network.play.server.SJoinGamePacket;
import net.minecraft.network.play.server.SPlayEntityEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.network.play.server.SPlayerAbilitiesPacket;
import net.minecraft.network.play.server.SPlayerListItemPacket;
import net.minecraft.network.play.server.SRespawnPacket;
import net.minecraft.network.play.server.SServerDifficultyPacket;
import net.minecraft.network.play.server.SSetExperiencePacket;
import net.minecraft.network.play.server.STagsListPacket;
import net.minecraft.network.play.server.STeamsPacket;
import net.minecraft.network.play.server.SUpdateRecipesPacket;
import net.minecraft.network.play.server.SUpdateTimePacket;
import net.minecraft.network.play.server.SUpdateViewDistancePacket;
import net.minecraft.network.play.server.SWorldBorderPacket;
import net.minecraft.network.play.server.SWorldSpawnChangedPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.ServerStatisticsManager;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.FolderName;
import net.minecraft.world.storage.IWorldInfo;
import net.minecraft.world.storage.PlayerData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PlayerList
{
    public static final File FILE_PLAYERBANS = new File("banned-players.json");
    public static final File FILE_IPBANS = new File("banned-ips.json");
    public static final File FILE_OPS = new File("ops.json");
    public static final File FILE_WHITELIST = new File("whitelist.json");
    private static final Logger LOGGER = LogManager.getLogger();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
    private final MinecraftServer server;
    private final List<ServerPlayerEntity> players = Lists.newArrayList();
    private final Map<UUID, ServerPlayerEntity> uuidToPlayerMap = Maps.newHashMap();
    private final BanList bannedPlayers = new BanList(FILE_PLAYERBANS);
    private final IPBanList bannedIPs = new IPBanList(FILE_IPBANS);
    private final OpList ops = new OpList(FILE_OPS);
    private final WhiteList whiteListedPlayers = new WhiteList(FILE_WHITELIST);
    private final Map<UUID, ServerStatisticsManager> playerStatFiles = Maps.newHashMap();
    private final Map<UUID, PlayerAdvancements> advancements = Maps.newHashMap();
    private final PlayerData playerDataManager;
    private boolean whiteListEnforced;
    private final DynamicRegistries.Impl field_232639_s_;
    protected final int maxPlayers;
    private int viewDistance;
    private GameType gameType;
    private boolean commandsAllowedForAll;
    private int playerPingIndex;

    public PlayerList(MinecraftServer p_i231425_1_, DynamicRegistries.Impl p_i231425_2_, PlayerData p_i231425_3_, int p_i231425_4_)
    {
        this.server = p_i231425_1_;
        this.field_232639_s_ = p_i231425_2_;
        this.maxPlayers = p_i231425_4_;
        this.playerDataManager = p_i231425_3_;
    }

    public void initializeConnectionToPlayer(NetworkManager netManager, ServerPlayerEntity playerIn)
    {
        GameProfile gameprofile = playerIn.getGameProfile();
        PlayerProfileCache playerprofilecache = this.server.getPlayerProfileCache();
        GameProfile gameprofile1 = playerprofilecache.getProfileByUUID(gameprofile.getId());
        String s = gameprofile1 == null ? gameprofile.getName() : gameprofile1.getName();
        playerprofilecache.addEntry(gameprofile);
        CompoundNBT compoundnbt = this.readPlayerDataFromFile(playerIn);
        RegistryKey<World> registrykey = compoundnbt != null ? DimensionType.decodeWorldKey(new Dynamic<>(NBTDynamicOps.INSTANCE, compoundnbt.get("Dimension"))).resultOrPartial(LOGGER::error).orElse(World.OVERWORLD) : World.OVERWORLD;
        ServerWorld serverworld = this.server.getWorld(registrykey);
        ServerWorld serverworld1;

        if (serverworld == null)
        {
            LOGGER.warn("Unknown respawn dimension {}, defaulting to overworld", (Object)registrykey);
            serverworld1 = this.server.func_241755_D_();
        }
        else
        {
            serverworld1 = serverworld;
        }

        playerIn.setWorld(serverworld1);
        playerIn.interactionManager.setWorld((ServerWorld)playerIn.world);
        String s1 = "local";

        if (netManager.getRemoteAddress() != null)
        {
            s1 = netManager.getRemoteAddress().toString();
        }

        LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", playerIn.getName().getString(), s1, playerIn.getEntityId(), playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ());
        IWorldInfo iworldinfo = serverworld1.getWorldInfo();
        this.setPlayerGameTypeBasedOnOther(playerIn, (ServerPlayerEntity)null, serverworld1);
        ServerPlayNetHandler serverplaynethandler = new ServerPlayNetHandler(this.server, netManager, playerIn);
        GameRules gamerules = serverworld1.getGameRules();
        boolean flag = gamerules.getBoolean(GameRules.DO_IMMEDIATE_RESPAWN);
        boolean flag1 = gamerules.getBoolean(GameRules.REDUCED_DEBUG_INFO);
        serverplaynethandler.sendPacket(new SJoinGamePacket(playerIn.getEntityId(), playerIn.interactionManager.getGameType(), playerIn.interactionManager.func_241815_c_(), BiomeManager.getHashedSeed(serverworld1.getSeed()), iworldinfo.isHardcore(), this.server.func_240770_D_(), this.field_232639_s_, serverworld1.getDimensionType(), serverworld1.getDimensionKey(), this.getMaxPlayers(), this.viewDistance, flag1, !flag, serverworld1.isDebug(), serverworld1.func_241109_A_()));
        serverplaynethandler.sendPacket(new SCustomPayloadPlayPacket(SCustomPayloadPlayPacket.BRAND, (new PacketBuffer(Unpooled.buffer())).writeString(this.getServer().getServerModName())));
        serverplaynethandler.sendPacket(new SServerDifficultyPacket(iworldinfo.getDifficulty(), iworldinfo.isDifficultyLocked()));
        serverplaynethandler.sendPacket(new SPlayerAbilitiesPacket(playerIn.abilities));
        serverplaynethandler.sendPacket(new SHeldItemChangePacket(playerIn.inventory.currentItem));
        serverplaynethandler.sendPacket(new SUpdateRecipesPacket(this.server.getRecipeManager().getRecipes()));
        serverplaynethandler.sendPacket(new STagsListPacket(this.server.func_244266_aF()));
        this.updatePermissionLevel(playerIn);
        playerIn.getStats().markAllDirty();
        playerIn.getRecipeBook().init(playerIn);
        this.sendScoreboard(serverworld1.getScoreboard(), playerIn);
        this.server.refreshStatusNextTick();
        IFormattableTextComponent iformattabletextcomponent;

        if (playerIn.getGameProfile().getName().equalsIgnoreCase(s))
        {
            iformattabletextcomponent = new TranslationTextComponent("multiplayer.player.joined", playerIn.getDisplayName());
        }
        else
        {
            iformattabletextcomponent = new TranslationTextComponent("multiplayer.player.joined.renamed", playerIn.getDisplayName(), s);
        }

        this.func_232641_a_(iformattabletextcomponent.mergeStyle(TextFormatting.YELLOW), ChatType.SYSTEM, Util.DUMMY_UUID);
        serverplaynethandler.setPlayerLocation(playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), playerIn.rotationYaw, playerIn.rotationPitch);
        this.players.add(playerIn);
        this.uuidToPlayerMap.put(playerIn.getUniqueID(), playerIn);
        this.sendPacketToAllPlayers(new SPlayerListItemPacket(SPlayerListItemPacket.Action.ADD_PLAYER, playerIn));

        for (int i = 0; i < this.players.size(); ++i)
        {
            playerIn.connection.sendPacket(new SPlayerListItemPacket(SPlayerListItemPacket.Action.ADD_PLAYER, this.players.get(i)));
        }

        serverworld1.addNewPlayer(playerIn);
        this.server.getCustomBossEvents().onPlayerLogin(playerIn);
        this.sendWorldInfo(playerIn, serverworld1);

        if (!this.server.getResourcePackUrl().isEmpty())
        {
            playerIn.loadResourcePack(this.server.getResourcePackUrl(), this.server.getResourcePackHash());
        }

        for (EffectInstance effectinstance : playerIn.getActivePotionEffects())
        {
            serverplaynethandler.sendPacket(new SPlayEntityEffectPacket(playerIn.getEntityId(), effectinstance));
        }

        if (compoundnbt != null && compoundnbt.contains("RootVehicle", 10))
        {
            CompoundNBT compoundnbt1 = compoundnbt.getCompound("RootVehicle");
            Entity entity1 = EntityType.loadEntityAndExecute(compoundnbt1.getCompound("Entity"), serverworld1, (p_217885_1_) ->
            {
                return !serverworld1.summonEntity(p_217885_1_) ? null : p_217885_1_;
            });

            if (entity1 != null)
            {
                UUID uuid;

                if (compoundnbt1.hasUniqueId("Attach"))
                {
                    uuid = compoundnbt1.getUniqueId("Attach");
                }
                else
                {
                    uuid = null;
                }

                if (entity1.getUniqueID().equals(uuid))
                {
                    playerIn.startRiding(entity1, true);
                }
                else
                {
                    for (Entity entity : entity1.getRecursivePassengers())
                    {
                        if (entity.getUniqueID().equals(uuid))
                        {
                            playerIn.startRiding(entity, true);
                            break;
                        }
                    }
                }

                if (!playerIn.isPassenger())
                {
                    LOGGER.warn("Couldn't reattach entity to player");
                    serverworld1.removeEntity(entity1);

                    for (Entity entity2 : entity1.getRecursivePassengers())
                    {
                        serverworld1.removeEntity(entity2);
                    }
                }
            }
        }

        playerIn.addSelfToInternalCraftingInventory();
    }

    protected void sendScoreboard(ServerScoreboard scoreboardIn, ServerPlayerEntity playerIn)
    {
        Set<ScoreObjective> set = Sets.newHashSet();

        for (ScorePlayerTeam scoreplayerteam : scoreboardIn.getTeams())
        {
            playerIn.connection.sendPacket(new STeamsPacket(scoreplayerteam, 0));
        }

        for (int i = 0; i < 19; ++i)
        {
            ScoreObjective scoreobjective = scoreboardIn.getObjectiveInDisplaySlot(i);

            if (scoreobjective != null && !set.contains(scoreobjective))
            {
                for (IPacket<?> ipacket : scoreboardIn.getCreatePackets(scoreobjective))
                {
                    playerIn.connection.sendPacket(ipacket);
                }

                set.add(scoreobjective);
            }
        }
    }

    public void func_212504_a(ServerWorld p_212504_1_)
    {
        p_212504_1_.getWorldBorder().addListener(new IBorderListener()
        {
            public void onSizeChanged(WorldBorder border, double newSize)
            {
                PlayerList.this.sendPacketToAllPlayers(new SWorldBorderPacket(border, SWorldBorderPacket.Action.SET_SIZE));
            }
            public void onTransitionStarted(WorldBorder border, double oldSize, double newSize, long time)
            {
                PlayerList.this.sendPacketToAllPlayers(new SWorldBorderPacket(border, SWorldBorderPacket.Action.LERP_SIZE));
            }
            public void onCenterChanged(WorldBorder border, double x, double z)
            {
                PlayerList.this.sendPacketToAllPlayers(new SWorldBorderPacket(border, SWorldBorderPacket.Action.SET_CENTER));
            }
            public void onWarningTimeChanged(WorldBorder border, int newTime)
            {
                PlayerList.this.sendPacketToAllPlayers(new SWorldBorderPacket(border, SWorldBorderPacket.Action.SET_WARNING_TIME));
            }
            public void onWarningDistanceChanged(WorldBorder border, int newDistance)
            {
                PlayerList.this.sendPacketToAllPlayers(new SWorldBorderPacket(border, SWorldBorderPacket.Action.SET_WARNING_BLOCKS));
            }
            public void onDamageAmountChanged(WorldBorder border, double newAmount)
            {
            }
            public void onDamageBufferChanged(WorldBorder border, double newSize)
            {
            }
        });
    }

    @Nullable

    /**
     * called during player login. reads the player information from disk.
     */
    public CompoundNBT readPlayerDataFromFile(ServerPlayerEntity playerIn)
    {
        CompoundNBT compoundnbt = this.server.func_240793_aU_().getHostPlayerNBT();
        CompoundNBT compoundnbt1;

        if (playerIn.getName().getString().equals(this.server.getServerOwner()) && compoundnbt != null)
        {
            compoundnbt1 = compoundnbt;
            playerIn.read(compoundnbt);
            LOGGER.debug("loading single player");
        }
        else
        {
            compoundnbt1 = this.playerDataManager.loadPlayerData(playerIn);
        }

        return compoundnbt1;
    }

    /**
     * also stores the NBTTags if this is an intergratedPlayerList
     */
    protected void writePlayerData(ServerPlayerEntity playerIn)
    {
        this.playerDataManager.savePlayerData(playerIn);
        ServerStatisticsManager serverstatisticsmanager = this.playerStatFiles.get(playerIn.getUniqueID());

        if (serverstatisticsmanager != null)
        {
            serverstatisticsmanager.saveStatFile();
        }

        PlayerAdvancements playeradvancements = this.advancements.get(playerIn.getUniqueID());

        if (playeradvancements != null)
        {
            playeradvancements.save();
        }
    }

    /**
     * Called when a player disconnects from the game. Writes player data to disk and removes them from the world.
     */
    public void playerLoggedOut(ServerPlayerEntity playerIn)
    {
        ServerWorld serverworld = playerIn.getServerWorld();
        playerIn.addStat(Stats.LEAVE_GAME);
        this.writePlayerData(playerIn);

        if (playerIn.isPassenger())
        {
            Entity entity = playerIn.getLowestRidingEntity();

            if (entity.isOnePlayerRiding())
            {
                LOGGER.debug("Removing player mount");
                playerIn.stopRiding();
                serverworld.removeEntity(entity);
                entity.removed = true;

                for (Entity entity1 : entity.getRecursivePassengers())
                {
                    serverworld.removeEntity(entity1);
                    entity1.removed = true;
                }

                serverworld.getChunk(playerIn.chunkCoordX, playerIn.chunkCoordZ).markDirty();
            }
        }

        playerIn.detach();
        serverworld.removePlayer(playerIn);
        playerIn.getAdvancements().dispose();
        this.players.remove(playerIn);
        this.server.getCustomBossEvents().onPlayerLogout(playerIn);
        UUID uuid = playerIn.getUniqueID();
        ServerPlayerEntity serverplayerentity = this.uuidToPlayerMap.get(uuid);

        if (serverplayerentity == playerIn)
        {
            this.uuidToPlayerMap.remove(uuid);
            this.playerStatFiles.remove(uuid);
            this.advancements.remove(uuid);
        }

        this.sendPacketToAllPlayers(new SPlayerListItemPacket(SPlayerListItemPacket.Action.REMOVE_PLAYER, playerIn));
    }

    @Nullable
    public ITextComponent canPlayerLogin(SocketAddress p_206258_1_, GameProfile p_206258_2_)
    {
        if (this.bannedPlayers.isBanned(p_206258_2_))
        {
            ProfileBanEntry profilebanentry = this.bannedPlayers.getEntry(p_206258_2_);
            IFormattableTextComponent iformattabletextcomponent1 = new TranslationTextComponent("multiplayer.disconnect.banned.reason", profilebanentry.getBanReason());

            if (profilebanentry.getBanEndDate() != null)
            {
                iformattabletextcomponent1.append(new TranslationTextComponent("multiplayer.disconnect.banned.expiration", DATE_FORMAT.format(profilebanentry.getBanEndDate())));
            }

            return iformattabletextcomponent1;
        }
        else if (!this.canJoin(p_206258_2_))
        {
            return new TranslationTextComponent("multiplayer.disconnect.not_whitelisted");
        }
        else if (this.bannedIPs.isBanned(p_206258_1_))
        {
            IPBanEntry ipbanentry = this.bannedIPs.getBanEntry(p_206258_1_);
            IFormattableTextComponent iformattabletextcomponent = new TranslationTextComponent("multiplayer.disconnect.banned_ip.reason", ipbanentry.getBanReason());

            if (ipbanentry.getBanEndDate() != null)
            {
                iformattabletextcomponent.append(new TranslationTextComponent("multiplayer.disconnect.banned_ip.expiration", DATE_FORMAT.format(ipbanentry.getBanEndDate())));
            }

            return iformattabletextcomponent;
        }
        else
        {
            return this.players.size() >= this.maxPlayers && !this.bypassesPlayerLimit(p_206258_2_) ? new TranslationTextComponent("multiplayer.disconnect.server_full") : null;
        }
    }

    /**
     * also checks for multiple logins across servers
     */
    public ServerPlayerEntity createPlayerForUser(GameProfile profile)
    {
        UUID uuid = PlayerEntity.getUUID(profile);
        List<ServerPlayerEntity> list = Lists.newArrayList();

        for (int i = 0; i < this.players.size(); ++i)
        {
            ServerPlayerEntity serverplayerentity = this.players.get(i);

            if (serverplayerentity.getUniqueID().equals(uuid))
            {
                list.add(serverplayerentity);
            }
        }

        ServerPlayerEntity serverplayerentity2 = this.uuidToPlayerMap.get(profile.getId());

        if (serverplayerentity2 != null && !list.contains(serverplayerentity2))
        {
            list.add(serverplayerentity2);
        }

        for (ServerPlayerEntity serverplayerentity1 : list)
        {
            serverplayerentity1.connection.disconnect(new TranslationTextComponent("multiplayer.disconnect.duplicate_login"));
        }

        ServerWorld serverworld = this.server.func_241755_D_();
        PlayerInteractionManager playerinteractionmanager;

        if (this.server.isDemo())
        {
            playerinteractionmanager = new DemoPlayerInteractionManager(serverworld);
        }
        else
        {
            playerinteractionmanager = new PlayerInteractionManager(serverworld);
        }

        return new ServerPlayerEntity(this.server, serverworld, profile, playerinteractionmanager);
    }

    public ServerPlayerEntity func_232644_a_(ServerPlayerEntity p_232644_1_, boolean p_232644_2_)
    {
        this.players.remove(p_232644_1_);
        p_232644_1_.getServerWorld().removePlayer(p_232644_1_);
        BlockPos blockpos = p_232644_1_.func_241140_K_();
        float f = p_232644_1_.func_242109_L();
        boolean flag = p_232644_1_.func_241142_M_();
        ServerWorld serverworld = this.server.getWorld(p_232644_1_.func_241141_L_());
        Optional<Vector3d> optional;

        if (serverworld != null && blockpos != null)
        {
            optional = PlayerEntity.func_242374_a(serverworld, blockpos, f, flag, p_232644_2_);
        }
        else
        {
            optional = Optional.empty();
        }

        ServerWorld serverworld1 = serverworld != null && optional.isPresent() ? serverworld : this.server.func_241755_D_();
        PlayerInteractionManager playerinteractionmanager;

        if (this.server.isDemo())
        {
            playerinteractionmanager = new DemoPlayerInteractionManager(serverworld1);
        }
        else
        {
            playerinteractionmanager = new PlayerInteractionManager(serverworld1);
        }

        ServerPlayerEntity serverplayerentity = new ServerPlayerEntity(this.server, serverworld1, p_232644_1_.getGameProfile(), playerinteractionmanager);
        serverplayerentity.connection = p_232644_1_.connection;
        serverplayerentity.copyFrom(p_232644_1_, p_232644_2_);
        serverplayerentity.setEntityId(p_232644_1_.getEntityId());
        serverplayerentity.setPrimaryHand(p_232644_1_.getPrimaryHand());

        for (String s : p_232644_1_.getTags())
        {
            serverplayerentity.addTag(s);
        }

        this.setPlayerGameTypeBasedOnOther(serverplayerentity, p_232644_1_, serverworld1);
        boolean flag2 = false;

        if (optional.isPresent())
        {
            BlockState blockstate = serverworld1.getBlockState(blockpos);
            boolean flag1 = blockstate.isIn(Blocks.RESPAWN_ANCHOR);
            Vector3d vector3d = optional.get();
            float f1;

            if (!blockstate.isIn(BlockTags.BEDS) && !flag1)
            {
                f1 = f;
            }
            else
            {
                Vector3d vector3d1 = Vector3d.copyCenteredHorizontally(blockpos).subtract(vector3d).normalize();
                f1 = (float)MathHelper.wrapDegrees(MathHelper.atan2(vector3d1.z, vector3d1.x) * (double)(180F / (float)Math.PI) - 90.0D);
            }

            serverplayerentity.setLocationAndAngles(vector3d.x, vector3d.y, vector3d.z, f1, 0.0F);
            serverplayerentity.func_242111_a(serverworld1.getDimensionKey(), blockpos, f, flag, false);
            flag2 = !p_232644_2_ && flag1;
        }
        else if (blockpos != null)
        {
            serverplayerentity.connection.sendPacket(new SChangeGameStatePacket(SChangeGameStatePacket.field_241764_a_, 0.0F));
        }

        while (!serverworld1.hasNoCollisions(serverplayerentity) && serverplayerentity.getPosY() < 256.0D)
        {
            serverplayerentity.setPosition(serverplayerentity.getPosX(), serverplayerentity.getPosY() + 1.0D, serverplayerentity.getPosZ());
        }

        IWorldInfo iworldinfo = serverplayerentity.world.getWorldInfo();
        serverplayerentity.connection.sendPacket(new SRespawnPacket(serverplayerentity.world.getDimensionType(), serverplayerentity.world.getDimensionKey(), BiomeManager.getHashedSeed(serverplayerentity.getServerWorld().getSeed()), serverplayerentity.interactionManager.getGameType(), serverplayerentity.interactionManager.func_241815_c_(), serverplayerentity.getServerWorld().isDebug(), serverplayerentity.getServerWorld().func_241109_A_(), p_232644_2_));
        serverplayerentity.connection.setPlayerLocation(serverplayerentity.getPosX(), serverplayerentity.getPosY(), serverplayerentity.getPosZ(), serverplayerentity.rotationYaw, serverplayerentity.rotationPitch);
        serverplayerentity.connection.sendPacket(new SWorldSpawnChangedPacket(serverworld1.getSpawnPoint(), serverworld1.func_242107_v()));
        serverplayerentity.connection.sendPacket(new SServerDifficultyPacket(iworldinfo.getDifficulty(), iworldinfo.isDifficultyLocked()));
        serverplayerentity.connection.sendPacket(new SSetExperiencePacket(serverplayerentity.experience, serverplayerentity.experienceTotal, serverplayerentity.experienceLevel));
        this.sendWorldInfo(serverplayerentity, serverworld1);
        this.updatePermissionLevel(serverplayerentity);
        serverworld1.addRespawnedPlayer(serverplayerentity);
        this.players.add(serverplayerentity);
        this.uuidToPlayerMap.put(serverplayerentity.getUniqueID(), serverplayerentity);
        serverplayerentity.addSelfToInternalCraftingInventory();
        serverplayerentity.setHealth(serverplayerentity.getHealth());

        if (flag2)
        {
            serverplayerentity.connection.sendPacket(new SPlaySoundEffectPacket(SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE, SoundCategory.BLOCKS, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), 1.0F, 1.0F));
        }

        return serverplayerentity;
    }

    public void updatePermissionLevel(ServerPlayerEntity player)
    {
        GameProfile gameprofile = player.getGameProfile();
        int i = this.server.getPermissionLevel(gameprofile);
        this.sendPlayerPermissionLevel(player, i);
    }

    /**
     * self explanitory
     */
    public void tick()
    {
        if (++this.playerPingIndex > 600)
        {
            this.sendPacketToAllPlayers(new SPlayerListItemPacket(SPlayerListItemPacket.Action.UPDATE_LATENCY, this.players));
            this.playerPingIndex = 0;
        }
    }

    public void sendPacketToAllPlayers(IPacket<?> packetIn)
    {
        for (int i = 0; i < this.players.size(); ++i)
        {
            (this.players.get(i)).connection.sendPacket(packetIn);
        }
    }

    public void func_232642_a_(IPacket<?> p_232642_1_, RegistryKey<World> p_232642_2_)
    {
        for (int i = 0; i < this.players.size(); ++i)
        {
            ServerPlayerEntity serverplayerentity = this.players.get(i);

            if (serverplayerentity.world.getDimensionKey() == p_232642_2_)
            {
                serverplayerentity.connection.sendPacket(p_232642_1_);
            }
        }
    }

    public void sendMessageToAllTeamMembers(PlayerEntity player, ITextComponent message)
    {
        Team team = player.getTeam();

        if (team != null)
        {
            for (String s : team.getMembershipCollection())
            {
                ServerPlayerEntity serverplayerentity = this.getPlayerByUsername(s);

                if (serverplayerentity != null && serverplayerentity != player)
                {
                    serverplayerentity.sendMessage(message, player.getUniqueID());
                }
            }
        }
    }

    public void sendMessageToTeamOrAllPlayers(PlayerEntity player, ITextComponent message)
    {
        Team team = player.getTeam();

        if (team == null)
        {
            this.func_232641_a_(message, ChatType.SYSTEM, player.getUniqueID());
        }
        else
        {
            for (int i = 0; i < this.players.size(); ++i)
            {
                ServerPlayerEntity serverplayerentity = this.players.get(i);

                if (serverplayerentity.getTeam() != team)
                {
                    serverplayerentity.sendMessage(message, player.getUniqueID());
                }
            }
        }
    }

    /**
     * Returns an array of the usernames of all the connected players.
     */
    public String[] getOnlinePlayerNames()
    {
        String[] astring = new String[this.players.size()];

        for (int i = 0; i < this.players.size(); ++i)
        {
            astring[i] = this.players.get(i).getGameProfile().getName();
        }

        return astring;
    }

    public BanList getBannedPlayers()
    {
        return this.bannedPlayers;
    }

    public IPBanList getBannedIPs()
    {
        return this.bannedIPs;
    }

    public void addOp(GameProfile profile)
    {
        this.ops.addEntry(new OpEntry(profile, this.server.getOpPermissionLevel(), this.ops.bypassesPlayerLimit(profile)));
        ServerPlayerEntity serverplayerentity = this.getPlayerByUUID(profile.getId());

        if (serverplayerentity != null)
        {
            this.updatePermissionLevel(serverplayerentity);
        }
    }

    public void removeOp(GameProfile profile)
    {
        this.ops.removeEntry(profile);
        ServerPlayerEntity serverplayerentity = this.getPlayerByUUID(profile.getId());

        if (serverplayerentity != null)
        {
            this.updatePermissionLevel(serverplayerentity);
        }
    }

    private void sendPlayerPermissionLevel(ServerPlayerEntity player, int permLevel)
    {
        if (player.connection != null)
        {
            byte b0;

            if (permLevel <= 0)
            {
                b0 = 24;
            }
            else if (permLevel >= 4)
            {
                b0 = 28;
            }
            else
            {
                b0 = (byte)(24 + permLevel);
            }

            player.connection.sendPacket(new SEntityStatusPacket(player, b0));
        }

        this.server.getCommandManager().send(player);
    }

    public boolean canJoin(GameProfile profile)
    {
        return !this.whiteListEnforced || this.ops.hasEntry(profile) || this.whiteListedPlayers.hasEntry(profile);
    }

    public boolean canSendCommands(GameProfile profile)
    {
        return this.ops.hasEntry(profile) || this.server.isServerOwner(profile) && this.server.func_240793_aU_().areCommandsAllowed() || this.commandsAllowedForAll;
    }

    @Nullable
    public ServerPlayerEntity getPlayerByUsername(String username)
    {
        for (ServerPlayerEntity serverplayerentity : this.players)
        {
            if (serverplayerentity.getGameProfile().getName().equalsIgnoreCase(username))
            {
                return serverplayerentity;
            }
        }

        return null;
    }

    /**
     * params: srcPlayer,x,y,z,r,dimension. The packet is not sent to the srcPlayer, but all other players within the
     * search radius
     */
    public void sendToAllNearExcept(@Nullable PlayerEntity except, double x, double y, double z, double radius, RegistryKey<World> dimension, IPacket<?> packetIn)
    {
        for (int i = 0; i < this.players.size(); ++i)
        {
            ServerPlayerEntity serverplayerentity = this.players.get(i);

            if (serverplayerentity != except && serverplayerentity.world.getDimensionKey() == dimension)
            {
                double d0 = x - serverplayerentity.getPosX();
                double d1 = y - serverplayerentity.getPosY();
                double d2 = z - serverplayerentity.getPosZ();

                if (d0 * d0 + d1 * d1 + d2 * d2 < radius * radius)
                {
                    serverplayerentity.connection.sendPacket(packetIn);
                }
            }
        }
    }

    /**
     * Saves all of the players' current states.
     */
    public void saveAllPlayerData()
    {
        for (int i = 0; i < this.players.size(); ++i)
        {
            this.writePlayerData(this.players.get(i));
        }
    }

    public WhiteList getWhitelistedPlayers()
    {
        return this.whiteListedPlayers;
    }

    public String[] getWhitelistedPlayerNames()
    {
        return this.whiteListedPlayers.getKeys();
    }

    public OpList getOppedPlayers()
    {
        return this.ops;
    }

    public String[] getOppedPlayerNames()
    {
        return this.ops.getKeys();
    }

    public void reloadWhitelist()
    {
    }

    /**
     * Updates the time and weather for the given player to those of the given world
     */
    public void sendWorldInfo(ServerPlayerEntity playerIn, ServerWorld worldIn)
    {
        WorldBorder worldborder = this.server.func_241755_D_().getWorldBorder();
        playerIn.connection.sendPacket(new SWorldBorderPacket(worldborder, SWorldBorderPacket.Action.INITIALIZE));
        playerIn.connection.sendPacket(new SUpdateTimePacket(worldIn.getGameTime(), worldIn.getDayTime(), worldIn.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)));
        playerIn.connection.sendPacket(new SWorldSpawnChangedPacket(worldIn.getSpawnPoint(), worldIn.func_242107_v()));

        if (worldIn.isRaining())
        {
            playerIn.connection.sendPacket(new SChangeGameStatePacket(SChangeGameStatePacket.field_241765_b_, 0.0F));
            playerIn.connection.sendPacket(new SChangeGameStatePacket(SChangeGameStatePacket.field_241771_h_, worldIn.getRainStrength(1.0F)));
            playerIn.connection.sendPacket(new SChangeGameStatePacket(SChangeGameStatePacket.field_241772_i_, worldIn.getThunderStrength(1.0F)));
        }
    }

    /**
     * sends the players inventory to himself
     */
    public void sendInventory(ServerPlayerEntity playerIn)
    {
        playerIn.sendContainerToPlayer(playerIn.inventoryContainer);
        playerIn.setPlayerHealthUpdated();
        playerIn.connection.sendPacket(new SHeldItemChangePacket(playerIn.inventory.currentItem));
    }

    /**
     * Returns the number of players currently on the server.
     */
    public int getCurrentPlayerCount()
    {
        return this.players.size();
    }

    /**
     * Returns the maximum number of players allowed on the server.
     */
    public int getMaxPlayers()
    {
        return this.maxPlayers;
    }

    public boolean isWhiteListEnabled()
    {
        return this.whiteListEnforced;
    }

    public void setWhiteListEnabled(boolean whitelistEnabled)
    {
        this.whiteListEnforced = whitelistEnabled;
    }

    public List<ServerPlayerEntity> getPlayersMatchingAddress(String address)
    {
        List<ServerPlayerEntity> list = Lists.newArrayList();

        for (ServerPlayerEntity serverplayerentity : this.players)
        {
            if (serverplayerentity.getPlayerIP().equals(address))
            {
                list.add(serverplayerentity);
            }
        }

        return list;
    }

    /**
     * Gets the view distance, in chunks.
     */
    public int getViewDistance()
    {
        return this.viewDistance;
    }

    public MinecraftServer getServer()
    {
        return this.server;
    }

    /**
     * On integrated servers, returns the host's player data to be written to level.dat.
     */
    public CompoundNBT getHostPlayerData()
    {
        return null;
    }

    public void setGameType(GameType gameModeIn)
    {
        this.gameType = gameModeIn;
    }

    private void setPlayerGameTypeBasedOnOther(ServerPlayerEntity target, @Nullable ServerPlayerEntity source, ServerWorld worldIn)
    {
        if (source != null)
        {
            target.interactionManager.func_241820_a(source.interactionManager.getGameType(), source.interactionManager.func_241815_c_());
        }
        else if (this.gameType != null)
        {
            target.interactionManager.func_241820_a(this.gameType, GameType.NOT_SET);
        }

        target.interactionManager.initializeGameType(worldIn.getServer().func_240793_aU_().getGameType());
    }

    /**
     * Sets whether all players are allowed to use commands (cheats) on the server.
     */
    public void setCommandsAllowedForAll(boolean p_72387_1_)
    {
        this.commandsAllowedForAll = p_72387_1_;
    }

    /**
     * Kicks everyone with "Server closed" as reason.
     */
    public void removeAllPlayers()
    {
        for (int i = 0; i < this.players.size(); ++i)
        {
            (this.players.get(i)).connection.disconnect(new TranslationTextComponent("multiplayer.disconnect.server_shutdown"));
        }
    }

    public void func_232641_a_(ITextComponent p_232641_1_, ChatType p_232641_2_, UUID p_232641_3_)
    {
        this.server.sendMessage(p_232641_1_, p_232641_3_);
        this.sendPacketToAllPlayers(new SChatPacket(p_232641_1_, p_232641_2_, p_232641_3_));
    }

    public ServerStatisticsManager getPlayerStats(PlayerEntity playerIn)
    {
        UUID uuid = playerIn.getUniqueID();
        ServerStatisticsManager serverstatisticsmanager = uuid == null ? null : this.playerStatFiles.get(uuid);

        if (serverstatisticsmanager == null)
        {
            File file1 = this.server.func_240776_a_(FolderName.STATS).toFile();
            File file2 = new File(file1, uuid + ".json");

            if (!file2.exists())
            {
                File file3 = new File(file1, playerIn.getName().getString() + ".json");

                if (file3.exists() && file3.isFile())
                {
                    file3.renameTo(file2);
                }
            }

            serverstatisticsmanager = new ServerStatisticsManager(this.server, file2);
            this.playerStatFiles.put(uuid, serverstatisticsmanager);
        }

        return serverstatisticsmanager;
    }

    public PlayerAdvancements getPlayerAdvancements(ServerPlayerEntity p_192054_1_)
    {
        UUID uuid = p_192054_1_.getUniqueID();
        PlayerAdvancements playeradvancements = this.advancements.get(uuid);

        if (playeradvancements == null)
        {
            File file1 = this.server.func_240776_a_(FolderName.ADVANCEMENTS).toFile();
            File file2 = new File(file1, uuid + ".json");
            playeradvancements = new PlayerAdvancements(this.server.getDataFixer(), this, this.server.getAdvancementManager(), file2, p_192054_1_);
            this.advancements.put(uuid, playeradvancements);
        }

        playeradvancements.setPlayer(p_192054_1_);
        return playeradvancements;
    }

    public void setViewDistance(int viewDistanceIn)
    {
        this.viewDistance = viewDistanceIn;
        this.sendPacketToAllPlayers(new SUpdateViewDistancePacket(viewDistanceIn));

        for (ServerWorld serverworld : this.server.getWorlds())
        {
            if (serverworld != null)
            {
                serverworld.getChunkProvider().setViewDistance(viewDistanceIn);
            }
        }
    }

    public List<ServerPlayerEntity> getPlayers()
    {
        return this.players;
    }

    @Nullable

    /**
     * Get's the EntityPlayerMP object representing the player with the UUID.
     */
    public ServerPlayerEntity getPlayerByUUID(UUID playerUUID)
    {
        return this.uuidToPlayerMap.get(playerUUID);
    }

    public boolean bypassesPlayerLimit(GameProfile profile)
    {
        return false;
    }

    public void reloadResources()
    {
        for (PlayerAdvancements playeradvancements : this.advancements.values())
        {
            playeradvancements.reset(this.server.getAdvancementManager());
        }

        this.sendPacketToAllPlayers(new STagsListPacket(this.server.func_244266_aF()));
        SUpdateRecipesPacket supdaterecipespacket = new SUpdateRecipesPacket(this.server.getRecipeManager().getRecipes());

        for (ServerPlayerEntity serverplayerentity : this.players)
        {
            serverplayerentity.connection.sendPacket(supdaterecipespacket);
            serverplayerentity.getRecipeBook().init(serverplayerentity);
        }
    }

    public boolean commandsAllowedForAll()
    {
        return this.commandsAllowedForAll;
    }
}
