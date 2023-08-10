package net.minecraft.entity;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.item.ExperienceBottleEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.item.PaintingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.item.minecart.ChestMinecartEntity;
import net.minecraft.entity.item.minecart.CommandBlockMinecartEntity;
import net.minecraft.entity.item.minecart.FurnaceMinecartEntity;
import net.minecraft.entity.item.minecart.HopperMinecartEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.item.minecart.SpawnerMinecartEntity;
import net.minecraft.entity.item.minecart.TNTMinecartEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.monster.CaveSpiderEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.DrownedEntity;
import net.minecraft.entity.monster.ElderGuardianEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.EndermiteEntity;
import net.minecraft.entity.monster.EvokerEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.monster.GiantEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.monster.HoglinEntity;
import net.minecraft.entity.monster.HuskEntity;
import net.minecraft.entity.monster.IllusionerEntity;
import net.minecraft.entity.monster.MagmaCubeEntity;
import net.minecraft.entity.monster.PhantomEntity;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.entity.monster.SilverfishEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.monster.StrayEntity;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.entity.monster.VindicatorEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.monster.ZoglinEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.entity.monster.ZombifiedPiglinEntity;
import net.minecraft.entity.monster.piglin.PiglinBruteEntity;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.passive.fish.CodEntity;
import net.minecraft.entity.passive.fish.PufferfishEntity;
import net.minecraft.entity.passive.fish.SalmonEntity;
import net.minecraft.entity.passive.fish.TropicalFishEntity;
import net.minecraft.entity.passive.horse.DonkeyEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.entity.passive.horse.MuleEntity;
import net.minecraft.entity.passive.horse.SkeletonHorseEntity;
import net.minecraft.entity.passive.horse.TraderLlamaEntity;
import net.minecraft.entity.passive.horse.ZombieHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.EggEntity;
import net.minecraft.entity.projectile.EvokerFangsEntity;
import net.minecraft.entity.projectile.EyeOfEnderEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SnowballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityType<T extends Entity>
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final EntityType<AreaEffectCloudEntity> AREA_EFFECT_CLOUD = register("area_effect_cloud", EntityType.Builder.<AreaEffectCloudEntity>create(AreaEffectCloudEntity::new, EntityClassification.MISC).immuneToFire().size(6.0F, 0.5F).trackingRange(10).func_233608_b_(Integer.MAX_VALUE));
    public static final EntityType<ArmorStandEntity> ARMOR_STAND = register("armor_stand", EntityType.Builder.<ArmorStandEntity>create(ArmorStandEntity::new, EntityClassification.MISC).size(0.5F, 1.975F).trackingRange(10));
    public static final EntityType<ArrowEntity> ARROW = register("arrow", EntityType.Builder.<ArrowEntity>create(ArrowEntity::new, EntityClassification.MISC).size(0.5F, 0.5F).trackingRange(4).func_233608_b_(20));
    public static final EntityType<BatEntity> BAT = register("bat", EntityType.Builder.<BatEntity>create(BatEntity::new, EntityClassification.AMBIENT).size(0.5F, 0.9F).trackingRange(5));
    public static final EntityType<BeeEntity> BEE = register("bee", EntityType.Builder.<BeeEntity>create(BeeEntity::new, EntityClassification.CREATURE).size(0.7F, 0.6F).trackingRange(8));
    public static final EntityType<BlazeEntity> BLAZE = register("blaze", EntityType.Builder.<BlazeEntity>create(BlazeEntity::new, EntityClassification.MONSTER).immuneToFire().size(0.6F, 1.8F).trackingRange(8));
    public static final EntityType<BoatEntity> BOAT = register("boat", EntityType.Builder.<BoatEntity>create(BoatEntity::new, EntityClassification.MISC).size(1.375F, 0.5625F).trackingRange(10));
    public static final EntityType<CatEntity> CAT = register("cat", EntityType.Builder.<CatEntity>create(CatEntity::new, EntityClassification.CREATURE).size(0.6F, 0.7F).trackingRange(8));
    public static final EntityType<CaveSpiderEntity> CAVE_SPIDER = register("cave_spider", EntityType.Builder.<CaveSpiderEntity>create(CaveSpiderEntity::new, EntityClassification.MONSTER).size(0.7F, 0.5F).trackingRange(8));
    public static final EntityType<ChickenEntity> CHICKEN = register("chicken", EntityType.Builder.<ChickenEntity>create(ChickenEntity::new, EntityClassification.CREATURE).size(0.4F, 0.7F).trackingRange(10));
    public static final EntityType<CodEntity> COD = register("cod", EntityType.Builder.<CodEntity>create(CodEntity::new, EntityClassification.WATER_AMBIENT).size(0.5F, 0.3F).trackingRange(4));
    public static final EntityType<CowEntity> COW = register("cow", EntityType.Builder.<CowEntity>create(CowEntity::new, EntityClassification.CREATURE).size(0.9F, 1.4F).trackingRange(10));
    public static final EntityType<CreeperEntity> CREEPER = register("creeper", EntityType.Builder.<CreeperEntity>create(CreeperEntity::new, EntityClassification.MONSTER).size(0.6F, 1.7F).trackingRange(8));
    public static final EntityType<DolphinEntity> DOLPHIN = register("dolphin", EntityType.Builder.<DolphinEntity>create(DolphinEntity::new, EntityClassification.WATER_CREATURE).size(0.9F, 0.6F));
    public static final EntityType<DonkeyEntity> DONKEY = register("donkey", EntityType.Builder.<DonkeyEntity>create(DonkeyEntity::new, EntityClassification.CREATURE).size(1.3964844F, 1.5F).trackingRange(10));
    public static final EntityType<DragonFireballEntity> DRAGON_FIREBALL = register("dragon_fireball", EntityType.Builder.<DragonFireballEntity>create(DragonFireballEntity::new, EntityClassification.MISC).size(1.0F, 1.0F).trackingRange(4).func_233608_b_(10));
    public static final EntityType<DrownedEntity> DROWNED = register("drowned", EntityType.Builder.<DrownedEntity>create(DrownedEntity::new, EntityClassification.MONSTER).size(0.6F, 1.95F).trackingRange(8));
    public static final EntityType<ElderGuardianEntity> ELDER_GUARDIAN = register("elder_guardian", EntityType.Builder.<ElderGuardianEntity>create(ElderGuardianEntity::new, EntityClassification.MONSTER).size(1.9975F, 1.9975F).trackingRange(10));
    public static final EntityType<EnderCrystalEntity> END_CRYSTAL = register("end_crystal", EntityType.Builder.<EnderCrystalEntity>create(EnderCrystalEntity::new, EntityClassification.MISC).size(2.0F, 2.0F).trackingRange(16).func_233608_b_(Integer.MAX_VALUE));
    public static final EntityType<EnderDragonEntity> ENDER_DRAGON = register("ender_dragon", EntityType.Builder.<EnderDragonEntity>create(EnderDragonEntity::new, EntityClassification.MONSTER).immuneToFire().size(16.0F, 8.0F).trackingRange(10));
    public static final EntityType<EndermanEntity> ENDERMAN = register("enderman", EntityType.Builder.<EndermanEntity>create(EndermanEntity::new, EntityClassification.MONSTER).size(0.6F, 2.9F).trackingRange(8));
    public static final EntityType<EndermiteEntity> ENDERMITE = register("endermite", EntityType.Builder.<EndermiteEntity>create(EndermiteEntity::new, EntityClassification.MONSTER).size(0.4F, 0.3F).trackingRange(8));
    public static final EntityType<EvokerEntity> EVOKER = register("evoker", EntityType.Builder.<EvokerEntity>create(EvokerEntity::new, EntityClassification.MONSTER).size(0.6F, 1.95F).trackingRange(8));
    public static final EntityType<EvokerFangsEntity> EVOKER_FANGS = register("evoker_fangs", EntityType.Builder.<EvokerFangsEntity>create(EvokerFangsEntity::new, EntityClassification.MISC).size(0.5F, 0.8F).trackingRange(6).func_233608_b_(2));
    public static final EntityType<ExperienceOrbEntity> EXPERIENCE_ORB = register("experience_orb", EntityType.Builder.<ExperienceOrbEntity>create(ExperienceOrbEntity::new, EntityClassification.MISC).size(0.5F, 0.5F).trackingRange(6).func_233608_b_(20));
    public static final EntityType<EyeOfEnderEntity> EYE_OF_ENDER = register("eye_of_ender", EntityType.Builder.<EyeOfEnderEntity>create(EyeOfEnderEntity::new, EntityClassification.MISC).size(0.25F, 0.25F).trackingRange(4).func_233608_b_(4));
    public static final EntityType<FallingBlockEntity> FALLING_BLOCK = register("falling_block", EntityType.Builder.<FallingBlockEntity>create(FallingBlockEntity::new, EntityClassification.MISC).size(0.98F, 0.98F).trackingRange(10).func_233608_b_(20));
    public static final EntityType<FireworkRocketEntity> FIREWORK_ROCKET = register("firework_rocket", EntityType.Builder.<FireworkRocketEntity>create(FireworkRocketEntity::new, EntityClassification.MISC).size(0.25F, 0.25F).trackingRange(4).func_233608_b_(10));
    public static final EntityType<FoxEntity> FOX = register("fox", EntityType.Builder.<FoxEntity>create(FoxEntity::new, EntityClassification.CREATURE).size(0.6F, 0.7F).trackingRange(8).func_233607_a_(Blocks.SWEET_BERRY_BUSH));
    public static final EntityType<GhastEntity> GHAST = register("ghast", EntityType.Builder.<GhastEntity>create(GhastEntity::new, EntityClassification.MONSTER).immuneToFire().size(4.0F, 4.0F).trackingRange(10));
    public static final EntityType<GiantEntity> GIANT = register("giant", EntityType.Builder.<GiantEntity>create(GiantEntity::new, EntityClassification.MONSTER).size(3.6F, 12.0F).trackingRange(10));
    public static final EntityType<GuardianEntity> GUARDIAN = register("guardian", EntityType.Builder.<GuardianEntity>create(GuardianEntity::new, EntityClassification.MONSTER).size(0.85F, 0.85F).trackingRange(8));
    public static final EntityType<HoglinEntity> HOGLIN = register("hoglin", EntityType.Builder.<HoglinEntity>create(HoglinEntity::new, EntityClassification.MONSTER).size(1.3964844F, 1.4F).trackingRange(8));
    public static final EntityType<HorseEntity> HORSE = register("horse", EntityType.Builder.<HorseEntity>create(HorseEntity::new, EntityClassification.CREATURE).size(1.3964844F, 1.6F).trackingRange(10));
    public static final EntityType<HuskEntity> HUSK = register("husk", EntityType.Builder.<HuskEntity>create(HuskEntity::new, EntityClassification.MONSTER).size(0.6F, 1.95F).trackingRange(8));
    public static final EntityType<IllusionerEntity> ILLUSIONER = register("illusioner", EntityType.Builder.<IllusionerEntity>create(IllusionerEntity::new, EntityClassification.MONSTER).size(0.6F, 1.95F).trackingRange(8));
    public static final EntityType<IronGolemEntity> IRON_GOLEM = register("iron_golem", EntityType.Builder.<IronGolemEntity>create(IronGolemEntity::new, EntityClassification.MISC).size(1.4F, 2.7F).trackingRange(10));
    public static final EntityType<ItemEntity> ITEM = register("item", EntityType.Builder.<ItemEntity>create(ItemEntity::new, EntityClassification.MISC).size(0.25F, 0.25F).trackingRange(6).func_233608_b_(20));
    public static final EntityType<ItemFrameEntity> ITEM_FRAME = register("item_frame", EntityType.Builder.<ItemFrameEntity>create(ItemFrameEntity::new, EntityClassification.MISC).size(0.5F, 0.5F).trackingRange(10).func_233608_b_(Integer.MAX_VALUE));
    public static final EntityType<FireballEntity> FIREBALL = register("fireball", EntityType.Builder.<FireballEntity>create(FireballEntity::new, EntityClassification.MISC).size(1.0F, 1.0F).trackingRange(4).func_233608_b_(10));
    public static final EntityType<LeashKnotEntity> LEASH_KNOT = register("leash_knot", EntityType.Builder.<LeashKnotEntity>create(LeashKnotEntity::new, EntityClassification.MISC).disableSerialization().size(0.5F, 0.5F).trackingRange(10).func_233608_b_(Integer.MAX_VALUE));
    public static final EntityType<LightningBoltEntity> LIGHTNING_BOLT = register("lightning_bolt", EntityType.Builder.<LightningBoltEntity>create(LightningBoltEntity::new, EntityClassification.MISC).disableSerialization().size(0.0F, 0.0F).trackingRange(16).func_233608_b_(Integer.MAX_VALUE));
    public static final EntityType<LlamaEntity> LLAMA = register("llama", EntityType.Builder.<LlamaEntity>create(LlamaEntity::new, EntityClassification.CREATURE).size(0.9F, 1.87F).trackingRange(10));
    public static final EntityType<LlamaSpitEntity> LLAMA_SPIT = register("llama_spit", EntityType.Builder.<LlamaSpitEntity>create(LlamaSpitEntity::new, EntityClassification.MISC).size(0.25F, 0.25F).trackingRange(4).func_233608_b_(10));
    public static final EntityType<MagmaCubeEntity> MAGMA_CUBE = register("magma_cube", EntityType.Builder.<MagmaCubeEntity>create(MagmaCubeEntity::new, EntityClassification.MONSTER).immuneToFire().size(2.04F, 2.04F).trackingRange(8));
    public static final EntityType<MinecartEntity> MINECART = register("minecart", EntityType.Builder.<MinecartEntity>create(MinecartEntity::new, EntityClassification.MISC).size(0.98F, 0.7F).trackingRange(8));
    public static final EntityType<ChestMinecartEntity> CHEST_MINECART = register("chest_minecart", EntityType.Builder.<ChestMinecartEntity>create(ChestMinecartEntity::new, EntityClassification.MISC).size(0.98F, 0.7F).trackingRange(8));
    public static final EntityType<CommandBlockMinecartEntity> COMMAND_BLOCK_MINECART = register("command_block_minecart", EntityType.Builder.<CommandBlockMinecartEntity>create(CommandBlockMinecartEntity::new, EntityClassification.MISC).size(0.98F, 0.7F).trackingRange(8));
    public static final EntityType<FurnaceMinecartEntity> FURNACE_MINECART = register("furnace_minecart", EntityType.Builder.<FurnaceMinecartEntity>create(FurnaceMinecartEntity::new, EntityClassification.MISC).size(0.98F, 0.7F).trackingRange(8));
    public static final EntityType<HopperMinecartEntity> HOPPER_MINECART = register("hopper_minecart", EntityType.Builder.<HopperMinecartEntity>create(HopperMinecartEntity::new, EntityClassification.MISC).size(0.98F, 0.7F).trackingRange(8));
    public static final EntityType<SpawnerMinecartEntity> SPAWNER_MINECART = register("spawner_minecart", EntityType.Builder.<SpawnerMinecartEntity>create(SpawnerMinecartEntity::new, EntityClassification.MISC).size(0.98F, 0.7F).trackingRange(8));
    public static final EntityType<TNTMinecartEntity> TNT_MINECART = register("tnt_minecart", EntityType.Builder.<TNTMinecartEntity>create(TNTMinecartEntity::new, EntityClassification.MISC).size(0.98F, 0.7F).trackingRange(8));
    public static final EntityType<MuleEntity> MULE = register("mule", EntityType.Builder.<MuleEntity>create(MuleEntity::new, EntityClassification.CREATURE).size(1.3964844F, 1.6F).trackingRange(8));
    public static final EntityType<MooshroomEntity> MOOSHROOM = register("mooshroom", EntityType.Builder.<MooshroomEntity>create(MooshroomEntity::new, EntityClassification.CREATURE).size(0.9F, 1.4F).trackingRange(10));
    public static final EntityType<OcelotEntity> OCELOT = register("ocelot", EntityType.Builder.<OcelotEntity>create(OcelotEntity::new, EntityClassification.CREATURE).size(0.6F, 0.7F).trackingRange(10));
    public static final EntityType<PaintingEntity> PAINTING = register("painting", EntityType.Builder.<PaintingEntity>create(PaintingEntity::new, EntityClassification.MISC).size(0.5F, 0.5F).trackingRange(10).func_233608_b_(Integer.MAX_VALUE));
    public static final EntityType<PandaEntity> PANDA = register("panda", EntityType.Builder.<PandaEntity>create(PandaEntity::new, EntityClassification.CREATURE).size(1.3F, 1.25F).trackingRange(10));
    public static final EntityType<ParrotEntity> PARROT = register("parrot", EntityType.Builder.<ParrotEntity>create(ParrotEntity::new, EntityClassification.CREATURE).size(0.5F, 0.9F).trackingRange(8));
    public static final EntityType<PhantomEntity> PHANTOM = register("phantom", EntityType.Builder.<PhantomEntity>create(PhantomEntity::new, EntityClassification.MONSTER).size(0.9F, 0.5F).trackingRange(8));
    public static final EntityType<PigEntity> PIG = register("pig", EntityType.Builder.<PigEntity>create(PigEntity::new, EntityClassification.CREATURE).size(0.9F, 0.9F).trackingRange(10));
    public static final EntityType<PiglinEntity> PIGLIN = register("piglin", EntityType.Builder.<PiglinEntity>create(PiglinEntity::new, EntityClassification.MONSTER).size(0.6F, 1.95F).trackingRange(8));
    public static final EntityType<PiglinBruteEntity> field_242287_aj = register("piglin_brute", EntityType.Builder.<PiglinBruteEntity>create(PiglinBruteEntity::new, EntityClassification.MONSTER).size(0.6F, 1.95F).trackingRange(8));
    public static final EntityType<PillagerEntity> PILLAGER = register("pillager", EntityType.Builder.<PillagerEntity>create(PillagerEntity::new, EntityClassification.MONSTER).func_225435_d().size(0.6F, 1.95F).trackingRange(8));
    public static final EntityType<PolarBearEntity> POLAR_BEAR = register("polar_bear", EntityType.Builder.<PolarBearEntity>create(PolarBearEntity::new, EntityClassification.CREATURE).size(1.4F, 1.4F).trackingRange(10));
    public static final EntityType<TNTEntity> TNT = register("tnt", EntityType.Builder.<TNTEntity>create(TNTEntity::new, EntityClassification.MISC).immuneToFire().size(0.98F, 0.98F).trackingRange(10).func_233608_b_(10));
    public static final EntityType<PufferfishEntity> PUFFERFISH = register("pufferfish", EntityType.Builder.<PufferfishEntity>create(PufferfishEntity::new, EntityClassification.WATER_AMBIENT).size(0.7F, 0.7F).trackingRange(4));
    public static final EntityType<RabbitEntity> RABBIT = register("rabbit", EntityType.Builder.<RabbitEntity>create(RabbitEntity::new, EntityClassification.CREATURE).size(0.4F, 0.5F).trackingRange(8));
    public static final EntityType<RavagerEntity> RAVAGER = register("ravager", EntityType.Builder.<RavagerEntity>create(RavagerEntity::new, EntityClassification.MONSTER).size(1.95F, 2.2F).trackingRange(10));
    public static final EntityType<SalmonEntity> SALMON = register("salmon", EntityType.Builder.<SalmonEntity>create(SalmonEntity::new, EntityClassification.WATER_AMBIENT).size(0.7F, 0.4F).trackingRange(4));
    public static final EntityType<SheepEntity> SHEEP = register("sheep", EntityType.Builder.<SheepEntity>create(SheepEntity::new, EntityClassification.CREATURE).size(0.9F, 1.3F).trackingRange(10));
    public static final EntityType<ShulkerEntity> SHULKER = register("shulker", EntityType.Builder.<ShulkerEntity>create(ShulkerEntity::new, EntityClassification.MONSTER).immuneToFire().func_225435_d().size(1.0F, 1.0F).trackingRange(10));
    public static final EntityType<ShulkerBulletEntity> SHULKER_BULLET = register("shulker_bullet", EntityType.Builder.<ShulkerBulletEntity>create(ShulkerBulletEntity::new, EntityClassification.MISC).size(0.3125F, 0.3125F).trackingRange(8));
    public static final EntityType<SilverfishEntity> SILVERFISH = register("silverfish", EntityType.Builder.<SilverfishEntity>create(SilverfishEntity::new, EntityClassification.MONSTER).size(0.4F, 0.3F).trackingRange(8));
    public static final EntityType<SkeletonEntity> SKELETON = register("skeleton", EntityType.Builder.<SkeletonEntity>create(SkeletonEntity::new, EntityClassification.MONSTER).size(0.6F, 1.99F).trackingRange(8));
    public static final EntityType<SkeletonHorseEntity> SKELETON_HORSE = register("skeleton_horse", EntityType.Builder.<SkeletonHorseEntity>create(SkeletonHorseEntity::new, EntityClassification.CREATURE).size(1.3964844F, 1.6F).trackingRange(10));
    public static final EntityType<SlimeEntity> SLIME = register("slime", EntityType.Builder.<SlimeEntity>create(SlimeEntity::new, EntityClassification.MONSTER).size(2.04F, 2.04F).trackingRange(10));
    public static final EntityType<SmallFireballEntity> SMALL_FIREBALL = register("small_fireball", EntityType.Builder.<SmallFireballEntity>create(SmallFireballEntity::new, EntityClassification.MISC).size(0.3125F, 0.3125F).trackingRange(4).func_233608_b_(10));
    public static final EntityType<SnowGolemEntity> SNOW_GOLEM = register("snow_golem", EntityType.Builder.<SnowGolemEntity>create(SnowGolemEntity::new, EntityClassification.MISC).size(0.7F, 1.9F).trackingRange(8));
    public static final EntityType<SnowballEntity> SNOWBALL = register("snowball", EntityType.Builder.<SnowballEntity>create(SnowballEntity::new, EntityClassification.MISC).size(0.25F, 0.25F).trackingRange(4).func_233608_b_(10));
    public static final EntityType<SpectralArrowEntity> SPECTRAL_ARROW = register("spectral_arrow", EntityType.Builder.<SpectralArrowEntity>create(SpectralArrowEntity::new, EntityClassification.MISC).size(0.5F, 0.5F).trackingRange(4).func_233608_b_(20));
    public static final EntityType<SpiderEntity> SPIDER = register("spider", EntityType.Builder.<SpiderEntity>create(SpiderEntity::new, EntityClassification.MONSTER).size(1.4F, 0.9F).trackingRange(8));
    public static final EntityType<SquidEntity> SQUID = register("squid", EntityType.Builder.<SquidEntity>create(SquidEntity::new, EntityClassification.WATER_CREATURE).size(0.8F, 0.8F).trackingRange(8));
    public static final EntityType<StrayEntity> STRAY = register("stray", EntityType.Builder.<StrayEntity>create(StrayEntity::new, EntityClassification.MONSTER).size(0.6F, 1.99F).trackingRange(8));
    public static final EntityType<StriderEntity> STRIDER = register("strider", EntityType.Builder.<StriderEntity>create(StriderEntity::new, EntityClassification.CREATURE).immuneToFire().size(0.9F, 1.7F).trackingRange(10));
    public static final EntityType<EggEntity> EGG = register("egg", EntityType.Builder.<EggEntity>create(EggEntity::new, EntityClassification.MISC).size(0.25F, 0.25F).trackingRange(4).func_233608_b_(10));
    public static final EntityType<EnderPearlEntity> ENDER_PEARL = register("ender_pearl", EntityType.Builder.<EnderPearlEntity>create(EnderPearlEntity::new, EntityClassification.MISC).size(0.25F, 0.25F).trackingRange(4).func_233608_b_(10));
    public static final EntityType<ExperienceBottleEntity> EXPERIENCE_BOTTLE = register("experience_bottle", EntityType.Builder.<ExperienceBottleEntity>create(ExperienceBottleEntity::new, EntityClassification.MISC).size(0.25F, 0.25F).trackingRange(4).func_233608_b_(10));
    public static final EntityType<PotionEntity> POTION = register("potion", EntityType.Builder.<PotionEntity>create(PotionEntity::new, EntityClassification.MISC).size(0.25F, 0.25F).trackingRange(4).func_233608_b_(10));
    public static final EntityType<TridentEntity> TRIDENT = register("trident", EntityType.Builder.<TridentEntity>create(TridentEntity::new, EntityClassification.MISC).size(0.5F, 0.5F).trackingRange(4).func_233608_b_(20));
    public static final EntityType<TraderLlamaEntity> TRADER_LLAMA = register("trader_llama", EntityType.Builder.<TraderLlamaEntity>create(TraderLlamaEntity::new, EntityClassification.CREATURE).size(0.9F, 1.87F).trackingRange(10));
    public static final EntityType<TropicalFishEntity> TROPICAL_FISH = register("tropical_fish", EntityType.Builder.<TropicalFishEntity>create(TropicalFishEntity::new, EntityClassification.WATER_AMBIENT).size(0.5F, 0.4F).trackingRange(4));
    public static final EntityType<TurtleEntity> TURTLE = register("turtle", EntityType.Builder.<TurtleEntity>create(TurtleEntity::new, EntityClassification.CREATURE).size(1.2F, 0.4F).trackingRange(10));
    public static final EntityType<VexEntity> VEX = register("vex", EntityType.Builder.<VexEntity>create(VexEntity::new, EntityClassification.MONSTER).immuneToFire().size(0.4F, 0.8F).trackingRange(8));
    public static final EntityType<VillagerEntity> VILLAGER = register("villager", EntityType.Builder.<VillagerEntity>create(VillagerEntity::new, EntityClassification.MISC).size(0.6F, 1.95F).trackingRange(10));
    public static final EntityType<VindicatorEntity> VINDICATOR = register("vindicator", EntityType.Builder.<VindicatorEntity>create(VindicatorEntity::new, EntityClassification.MONSTER).size(0.6F, 1.95F).trackingRange(8));
    public static final EntityType<WanderingTraderEntity> WANDERING_TRADER = register("wandering_trader", EntityType.Builder.<WanderingTraderEntity>create(WanderingTraderEntity::new, EntityClassification.CREATURE).size(0.6F, 1.95F).trackingRange(10));
    public static final EntityType<WitchEntity> WITCH = register("witch", EntityType.Builder.<WitchEntity>create(WitchEntity::new, EntityClassification.MONSTER).size(0.6F, 1.95F).trackingRange(8));
    public static final EntityType<WitherEntity> WITHER = register("wither", EntityType.Builder.<WitherEntity>create(WitherEntity::new, EntityClassification.MONSTER).immuneToFire().func_233607_a_(Blocks.WITHER_ROSE).size(0.9F, 3.5F).trackingRange(10));
    public static final EntityType<WitherSkeletonEntity> WITHER_SKELETON = register("wither_skeleton", EntityType.Builder.<WitherSkeletonEntity>create(WitherSkeletonEntity::new, EntityClassification.MONSTER).immuneToFire().func_233607_a_(Blocks.WITHER_ROSE).size(0.7F, 2.4F).trackingRange(8));
    public static final EntityType<WitherSkullEntity> WITHER_SKULL = register("wither_skull", EntityType.Builder.<WitherSkullEntity>create(WitherSkullEntity::new, EntityClassification.MISC).size(0.3125F, 0.3125F).trackingRange(4).func_233608_b_(10));
    public static final EntityType<WolfEntity> WOLF = register("wolf", EntityType.Builder.<WolfEntity>create(WolfEntity::new, EntityClassification.CREATURE).size(0.6F, 0.85F).trackingRange(10));
    public static final EntityType<ZoglinEntity> ZOGLIN = register("zoglin", EntityType.Builder.<ZoglinEntity>create(ZoglinEntity::new, EntityClassification.MONSTER).immuneToFire().size(1.3964844F, 1.4F).trackingRange(8));
    public static final EntityType<ZombieEntity> ZOMBIE = register("zombie", EntityType.Builder.<ZombieEntity>create(ZombieEntity::new, EntityClassification.MONSTER).size(0.6F, 1.95F).trackingRange(8));
    public static final EntityType<ZombieHorseEntity> ZOMBIE_HORSE = register("zombie_horse", EntityType.Builder.<ZombieHorseEntity>create(ZombieHorseEntity::new, EntityClassification.CREATURE).size(1.3964844F, 1.6F).trackingRange(10));
    public static final EntityType<ZombieVillagerEntity> ZOMBIE_VILLAGER = register("zombie_villager", EntityType.Builder.<ZombieVillagerEntity>create(ZombieVillagerEntity::new, EntityClassification.MONSTER).size(0.6F, 1.95F).trackingRange(8));
    public static final EntityType<ZombifiedPiglinEntity> ZOMBIFIED_PIGLIN = register("zombified_piglin", EntityType.Builder.<ZombifiedPiglinEntity>create(ZombifiedPiglinEntity::new, EntityClassification.MONSTER).immuneToFire().size(0.6F, 1.95F).trackingRange(8));
    public static final EntityType<PlayerEntity> PLAYER = register("player", EntityType.Builder.<PlayerEntity>create(EntityClassification.MISC).disableSerialization().disableSummoning().size(0.6F, 1.8F).trackingRange(32).func_233608_b_(2));
    public static final EntityType<FishingBobberEntity> FISHING_BOBBER = register("fishing_bobber", EntityType.Builder.<FishingBobberEntity>create(EntityClassification.MISC).disableSerialization().disableSummoning().size(0.25F, 0.25F).trackingRange(4).func_233608_b_(5));
    private final EntityType.IFactory<T> factory;
    private final EntityClassification classification;
    private final ImmutableSet<Block> field_233593_bg_;
    private final boolean serializable;
    private final boolean summonable;
    private final boolean immuneToFire;
    private final boolean field_225438_be;
    private final int defaultTrackingRange;
    private final int defaultUpdateInterval;
    @Nullable
    private String translationKey;
    @Nullable
    private ITextComponent name;
    @Nullable
    private ResourceLocation lootTable;
    private final EntitySize size;

    private static <T extends Entity> EntityType<T> register(String key, EntityType.Builder<T> builder)
    {
        return Registry.register(Registry.ENTITY_TYPE, key, builder.build(key));
    }

    public static ResourceLocation getKey(EntityType<?> entityTypeIn)
    {
        return Registry.ENTITY_TYPE.getKey(entityTypeIn);
    }

    public static Optional < EntityType<? >> byKey(String key)
    {
        return Registry.ENTITY_TYPE.getOptional(ResourceLocation.tryCreate(key));
    }

    public EntityType(EntityType.IFactory<T> p_i231489_1_, EntityClassification p_i231489_2_, boolean p_i231489_3_, boolean p_i231489_4_, boolean p_i231489_5_, boolean p_i231489_6_, ImmutableSet<Block> p_i231489_7_, EntitySize p_i231489_8_, int trackingRange, int updateInterval)
    {
        this.factory = p_i231489_1_;
        this.classification = p_i231489_2_;
        this.field_225438_be = p_i231489_6_;
        this.serializable = p_i231489_3_;
        this.summonable = p_i231489_4_;
        this.immuneToFire = p_i231489_5_;
        this.field_233593_bg_ = p_i231489_7_;
        this.size = p_i231489_8_;
        this.defaultTrackingRange = trackingRange;
        this.defaultUpdateInterval = updateInterval;
    }

    @Nullable
    public Entity spawn(ServerWorld worldIn, @Nullable ItemStack stack, @Nullable PlayerEntity playerIn, BlockPos pos, SpawnReason reason, boolean p_220331_6_, boolean p_220331_7_)
    {
        return this.spawn(worldIn, stack == null ? null : stack.getTag(), stack != null && stack.hasDisplayName() ? stack.getDisplayName() : null, playerIn, pos, reason, p_220331_6_, p_220331_7_);
    }

    @Nullable
    public T spawn(ServerWorld worldIn, @Nullable CompoundNBT compound, @Nullable ITextComponent customName, @Nullable PlayerEntity playerIn, BlockPos pos, SpawnReason reason, boolean p_220342_7_, boolean p_220342_8_)
    {
        T t = this.create(worldIn, compound, customName, playerIn, pos, reason, p_220342_7_, p_220342_8_);

        if (t != null)
        {
            worldIn.func_242417_l(t);
        }

        return t;
    }

    @Nullable
    public T create(ServerWorld worldIn, @Nullable CompoundNBT compound, @Nullable ITextComponent customName, @Nullable PlayerEntity playerIn, BlockPos pos, SpawnReason reason, boolean p_220349_7_, boolean p_220349_8_)
    {
        T t = this.create(worldIn);

        if (t == null)
        {
            return (T)null;
        }
        else
        {
            double d0;

            if (p_220349_7_)
            {
                t.setPosition((double)pos.getX() + 0.5D, (double)(pos.getY() + 1), (double)pos.getZ() + 0.5D);
                d0 = func_208051_a(worldIn, pos, p_220349_8_, t.getBoundingBox());
            }
            else
            {
                d0 = 0.0D;
            }

            t.setLocationAndAngles((double)pos.getX() + 0.5D, (double)pos.getY() + d0, (double)pos.getZ() + 0.5D, MathHelper.wrapDegrees(worldIn.rand.nextFloat() * 360.0F), 0.0F);

            if (t instanceof MobEntity)
            {
                MobEntity mobentity = (MobEntity)t;
                mobentity.rotationYawHead = mobentity.rotationYaw;
                mobentity.renderYawOffset = mobentity.rotationYaw;
                mobentity.onInitialSpawn(worldIn, worldIn.getDifficultyForLocation(mobentity.getPosition()), reason, (ILivingEntityData)null, compound);
                mobentity.playAmbientSound();
            }

            if (customName != null && t instanceof LivingEntity)
            {
                t.setCustomName(customName);
            }

            applyItemNBT(worldIn, playerIn, t, compound);
            return t;
        }
    }

    protected static double func_208051_a(IWorldReader worldReader, BlockPos pos, boolean p_208051_2_, AxisAlignedBB p_208051_3_)
    {
        AxisAlignedBB axisalignedbb = new AxisAlignedBB(pos);

        if (p_208051_2_)
        {
            axisalignedbb = axisalignedbb.expand(0.0D, -1.0D, 0.0D);
        }

        Stream<VoxelShape> stream = worldReader.func_234867_d_((Entity)null, axisalignedbb, (entity) ->
        {
            return true;
        });
        return 1.0D + VoxelShapes.getAllowedOffset(Direction.Axis.Y, p_208051_3_, stream, p_208051_2_ ? -2.0D : -1.0D);
    }

    public static void applyItemNBT(World worldIn, @Nullable PlayerEntity player, @Nullable Entity spawnedEntity, @Nullable CompoundNBT itemNBT)
    {
        if (itemNBT != null && itemNBT.contains("EntityTag", 10))
        {
            MinecraftServer minecraftserver = worldIn.getServer();

            if (minecraftserver != null && spawnedEntity != null)
            {
                if (worldIn.isRemote || !spawnedEntity.ignoreItemEntityData() || player != null && minecraftserver.getPlayerList().canSendCommands(player.getGameProfile()))
                {
                    CompoundNBT compoundnbt = spawnedEntity.writeWithoutTypeId(new CompoundNBT());
                    UUID uuid = spawnedEntity.getUniqueID();
                    compoundnbt.merge(itemNBT.getCompound("EntityTag"));
                    spawnedEntity.setUniqueId(uuid);
                    spawnedEntity.read(compoundnbt);
                }
            }
        }
    }

    public boolean isSerializable()
    {
        return this.serializable;
    }

    public boolean isSummonable()
    {
        return this.summonable;
    }

    public boolean isImmuneToFire()
    {
        return this.immuneToFire;
    }

    public boolean func_225437_d()
    {
        return this.field_225438_be;
    }

    public EntityClassification getClassification()
    {
        return this.classification;
    }

    public String getTranslationKey()
    {
        if (this.translationKey == null)
        {
            this.translationKey = Util.makeTranslationKey("entity", Registry.ENTITY_TYPE.getKey(this));
        }

        return this.translationKey;
    }

    public ITextComponent getName()
    {
        if (this.name == null)
        {
            this.name = new TranslationTextComponent(this.getTranslationKey());
        }

        return this.name;
    }

    public String toString()
    {
        return this.getTranslationKey();
    }

    public ResourceLocation getLootTable()
    {
        if (this.lootTable == null)
        {
            ResourceLocation resourcelocation = Registry.ENTITY_TYPE.getKey(this);
            this.lootTable = new ResourceLocation(resourcelocation.getNamespace(), "entities/" + resourcelocation.getPath());
        }

        return this.lootTable;
    }

    public float getWidth()
    {
        return this.size.width;
    }

    public float getHeight()
    {
        return this.size.height;
    }

    @Nullable
    public T create(World worldIn)
    {
        return this.factory.create(this, worldIn);
    }

    @Nullable
    public static Entity create(int id, World worldIn)
    {
        return create(worldIn, Registry.ENTITY_TYPE.getByValue(id));
    }

    public static Optional<Entity> loadEntityUnchecked(CompoundNBT compound, World worldIn)
    {
        return Util.acceptOrElse(readEntityType(compound).map((entityType) ->
        {
            return entityType.create(worldIn);
        }), (entity) ->
        {
            entity.read(compound);
        }, () ->
        {
            LOGGER.warn("Skipping Entity with id {}", (Object)compound.getString("id"));
        });
    }

    @Nullable
    private static Entity create(World worldIn, @Nullable EntityType<?> type)
    {
        return type == null ? null : type.create(worldIn);
    }

    public AxisAlignedBB getBoundingBoxWithSizeApplied(double p_220328_1_, double p_220328_3_, double p_220328_5_)
    {
        float f = this.getWidth() / 2.0F;
        return new AxisAlignedBB(p_220328_1_ - (double)f, p_220328_3_, p_220328_5_ - (double)f, p_220328_1_ + (double)f, p_220328_3_ + (double)this.getHeight(), p_220328_5_ + (double)f);
    }

    public boolean func_233597_a_(BlockState p_233597_1_)
    {
        if (this.field_233593_bg_.contains(p_233597_1_.getBlock()))
        {
            return false;
        }
        else if (this.immuneToFire || !p_233597_1_.isIn(BlockTags.FIRE) && !p_233597_1_.isIn(Blocks.MAGMA_BLOCK) && !CampfireBlock.isLit(p_233597_1_) && !p_233597_1_.isIn(Blocks.LAVA))
        {
            return p_233597_1_.isIn(Blocks.WITHER_ROSE) || p_233597_1_.isIn(Blocks.SWEET_BERRY_BUSH) || p_233597_1_.isIn(Blocks.CACTUS);
        }
        else
        {
            return true;
        }
    }

    public EntitySize getSize()
    {
        return this.size;
    }

    public static Optional < EntityType<? >> readEntityType(CompoundNBT compound)
    {
        return Registry.ENTITY_TYPE.getOptional(new ResourceLocation(compound.getString("id")));
    }

    @Nullable
    public static Entity loadEntityAndExecute(CompoundNBT compound, World worldIn, Function<Entity, Entity> p_220335_2_)
    {
        return loadEntity(compound, worldIn).map(p_220335_2_).map((p_220346_3_) ->
        {
            if (compound.contains("Passengers", 9))
            {
                ListNBT listnbt = compound.getList("Passengers", 10);

                for (int i = 0; i < listnbt.size(); ++i)
                {
                    Entity entity = loadEntityAndExecute(listnbt.getCompound(i), worldIn, p_220335_2_);

                    if (entity != null)
                    {
                        entity.startRiding(p_220346_3_, true);
                    }
                }
            }

            return p_220346_3_;
        }).orElse((Entity)null);
    }

    private static Optional<Entity> loadEntity(CompoundNBT compound, World worldIn)
    {
        try
        {
            return loadEntityUnchecked(compound, worldIn);
        }
        catch (RuntimeException runtimeexception)
        {
            LOGGER.warn("Exception loading entity: ", (Throwable)runtimeexception);
            return Optional.empty();
        }
    }

    public int func_233602_m_()
    {
        return this.defaultTrackingRange;
    }

    public int getUpdateFrequency()
    {
        return this.defaultUpdateInterval;
    }

    public boolean shouldSendVelocityUpdates()
    {
        return this != PLAYER && this != LLAMA_SPIT && this != WITHER && this != BAT && this != ITEM_FRAME && this != LEASH_KNOT && this != PAINTING && this != END_CRYSTAL && this != EVOKER_FANGS;
    }

    /**
     * Checks if this entity type is contained in the tag
     */
    public boolean isContained(ITag < EntityType<? >> tagIn)
    {
        return tagIn.contains(this);
    }

    public static class Builder<T extends Entity>
    {
        private final EntityType.IFactory<T> factory;
        private final EntityClassification classification;
        private ImmutableSet<Block> field_233603_c_ = ImmutableSet.of();
        private boolean serializable = true;
        private boolean summonable = true;
        private boolean immuneToFire;
        private boolean field_225436_f;
        private int field_233604_h_ = 5;
        private int field_233605_i_ = 3;
        private EntitySize size = EntitySize.flexible(0.6F, 1.8F);

        private Builder(EntityType.IFactory<T> factoryIn, EntityClassification classificationIn)
        {
            this.factory = factoryIn;
            this.classification = classificationIn;
            this.field_225436_f = classificationIn == EntityClassification.CREATURE || classificationIn == EntityClassification.MISC;
        }

        public static <T extends Entity> EntityType.Builder<T> create(EntityType.IFactory<T> factoryIn, EntityClassification classificationIn)
        {
            return new EntityType.Builder<>(factoryIn, classificationIn);
        }

        public static <T extends Entity> EntityType.Builder<T> create(EntityClassification classificationIn)
        {
            return new EntityType.Builder<>((type, world) ->
            {
                return (T)null;
            }, classificationIn);
        }

        public EntityType.Builder<T> size(float width, float height)
        {
            this.size = EntitySize.flexible(width, height);
            return this;
        }

        public EntityType.Builder<T> disableSummoning()
        {
            this.summonable = false;
            return this;
        }

        public EntityType.Builder<T> disableSerialization()
        {
            this.serializable = false;
            return this;
        }

        public EntityType.Builder<T> immuneToFire()
        {
            this.immuneToFire = true;
            return this;
        }

        public EntityType.Builder<T> func_233607_a_(Block... p_233607_1_)
        {
            this.field_233603_c_ = ImmutableSet.copyOf(p_233607_1_);
            return this;
        }

        public EntityType.Builder<T> func_225435_d()
        {
            this.field_225436_f = true;
            return this;
        }

        public EntityType.Builder<T> trackingRange(int p_233606_1_)
        {
            this.field_233604_h_ = p_233606_1_;
            return this;
        }

        public EntityType.Builder<T> func_233608_b_(int p_233608_1_)
        {
            this.field_233605_i_ = p_233608_1_;
            return this;
        }

        public EntityType<T> build(String id)
        {
            if (this.serializable)
            {
                Util.attemptDataFix(TypeReferences.ENTITY_TYPE, id);
            }

            return new EntityType<>(this.factory, this.classification, this.serializable, this.summonable, this.immuneToFire, this.field_225436_f, this.field_233603_c_, this.size, this.field_233604_h_, this.field_233605_i_);
        }
    }

    public interface IFactory<T extends Entity>
    {
        T create(EntityType<T> p_create_1_, World p_create_2_);
    }
}
