package net.minecraft.entity.ai.attributes;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.monster.CaveSpiderEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.ElderGuardianEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.EndermiteEntity;
import net.minecraft.entity.monster.EvokerEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.monster.GiantEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.monster.HoglinEntity;
import net.minecraft.entity.monster.IllusionerEntity;
import net.minecraft.entity.monster.MagmaCubeEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.entity.monster.SilverfishEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.entity.monster.VindicatorEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.monster.ZoglinEntity;
import net.minecraft.entity.monster.ZombieEntity;
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
import net.minecraft.entity.passive.fish.AbstractFishEntity;
import net.minecraft.entity.passive.horse.AbstractChestedHorseEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.entity.passive.horse.SkeletonHorseEntity;
import net.minecraft.entity.passive.horse.ZombieHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GlobalEntityTypeAttributes
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map < EntityType <? extends LivingEntity > , AttributeModifierMap > VANILLA_ATTRIBUTES = ImmutableMap. < EntityType <? extends LivingEntity > , AttributeModifierMap > builder().put(EntityType.ARMOR_STAND, LivingEntity.registerAttributes().create()).put(EntityType.BAT, BatEntity.func_234175_m_().create()).put(EntityType.BEE, BeeEntity.func_234182_eX_().create()).put(EntityType.BLAZE, BlazeEntity.registerAttributes().create()).put(EntityType.CAT, CatEntity.func_234184_eY_().create()).put(EntityType.CAVE_SPIDER, CaveSpiderEntity.registerAttributes().create()).put(EntityType.CHICKEN, ChickenEntity.func_234187_eI_().create()).put(EntityType.COD, AbstractFishEntity.func_234176_m_().create()).put(EntityType.COW, CowEntity.func_234188_eI_().create()).put(EntityType.CREEPER, CreeperEntity.registerAttributes().create()).put(EntityType.DOLPHIN, DolphinEntity.func_234190_eK_().create()).put(EntityType.DONKEY, AbstractChestedHorseEntity.func_234234_eJ_().create()).put(EntityType.DROWNED, ZombieEntity.func_234342_eQ_().create()).put(EntityType.ELDER_GUARDIAN, ElderGuardianEntity.func_234283_m_().create()).put(EntityType.ENDERMAN, EndermanEntity.func_234287_m_().create()).put(EntityType.ENDERMITE, EndermiteEntity.func_234288_m_().create()).put(EntityType.ENDER_DRAGON, EnderDragonEntity.registerAttributes().create()).put(EntityType.EVOKER, EvokerEntity.func_234289_eI_().create()).put(EntityType.FOX, FoxEntity.func_234192_eI_().create()).put(EntityType.GHAST, GhastEntity.func_234290_eH_().create()).put(EntityType.GIANT, GiantEntity.func_234291_m_().create()).put(EntityType.GUARDIAN, GuardianEntity.func_234292_eK_().create()).put(EntityType.HOGLIN, HoglinEntity.func_234362_eI_().create()).put(EntityType.HORSE, AbstractHorseEntity.func_234237_fg_().create()).put(EntityType.HUSK, ZombieEntity.func_234342_eQ_().create()).put(EntityType.ILLUSIONER, IllusionerEntity.func_234293_eI_().create()).put(EntityType.IRON_GOLEM, IronGolemEntity.func_234200_m_().create()).put(EntityType.LLAMA, LlamaEntity.func_234244_fu_().create()).put(EntityType.MAGMA_CUBE, MagmaCubeEntity.func_234294_m_().create()).put(EntityType.MOOSHROOM, CowEntity.func_234188_eI_().create()).put(EntityType.MULE, AbstractChestedHorseEntity.func_234234_eJ_().create()).put(EntityType.OCELOT, OcelotEntity.func_234201_eI_().create()).put(EntityType.PANDA, PandaEntity.func_234204_eW_().create()).put(EntityType.PARROT, ParrotEntity.func_234213_eS_().create()).put(EntityType.PHANTOM, MonsterEntity.func_234295_eP_().create()).put(EntityType.PIG, PigEntity.func_234215_eI_().create()).put(EntityType.PIGLIN, PiglinEntity.func_234420_eI_().create()).put(EntityType.field_242287_aj, PiglinBruteEntity.func_242344_eS().create()).put(EntityType.PILLAGER, PillagerEntity.func_234296_eI_().create()).put(EntityType.PLAYER, PlayerEntity.func_234570_el_().create()).put(EntityType.POLAR_BEAR, PolarBearEntity.func_234219_eI_().create()).put(EntityType.PUFFERFISH, AbstractFishEntity.func_234176_m_().create()).put(EntityType.RABBIT, RabbitEntity.func_234224_eJ_().create()).put(EntityType.RAVAGER, RavagerEntity.func_234297_m_().create()).put(EntityType.SALMON, AbstractFishEntity.func_234176_m_().create()).put(EntityType.SHEEP, SheepEntity.func_234225_eI_().create()).put(EntityType.SHULKER, ShulkerEntity.func_234300_m_().create()).put(EntityType.SILVERFISH, SilverfishEntity.func_234301_m_().create()).put(EntityType.SKELETON, AbstractSkeletonEntity.registerAttributes().create()).put(EntityType.SKELETON_HORSE, SkeletonHorseEntity.func_234250_eJ_().create()).put(EntityType.SLIME, MonsterEntity.func_234295_eP_().create()).put(EntityType.SNOW_GOLEM, SnowGolemEntity.func_234226_m_().create()).put(EntityType.SPIDER, SpiderEntity.func_234305_eI_().create()).put(EntityType.SQUID, SquidEntity.func_234227_m_().create()).put(EntityType.STRAY, AbstractSkeletonEntity.registerAttributes().create()).put(EntityType.STRIDER, StriderEntity.func_234317_eK_().create()).put(EntityType.TRADER_LLAMA, LlamaEntity.func_234244_fu_().create()).put(EntityType.TROPICAL_FISH, AbstractFishEntity.func_234176_m_().create()).put(EntityType.TURTLE, TurtleEntity.func_234228_eK_().create()).put(EntityType.VEX, VexEntity.func_234321_m_().create()).put(EntityType.VILLAGER, VillagerEntity.registerAttributes().create()).put(EntityType.VINDICATOR, VindicatorEntity.func_234322_eI_().create()).put(EntityType.WANDERING_TRADER, MobEntity.func_233666_p_().create()).put(EntityType.WITCH, WitchEntity.func_234323_eI_().create()).put(EntityType.WITHER, WitherEntity.registerAttributes().create()).put(EntityType.WITHER_SKELETON, AbstractSkeletonEntity.registerAttributes().create()).put(EntityType.WOLF, WolfEntity.func_234233_eS_().create()).put(EntityType.ZOGLIN, ZoglinEntity.func_234339_m_().create()).put(EntityType.ZOMBIE, ZombieEntity.func_234342_eQ_().create()).put(EntityType.ZOMBIE_HORSE, ZombieHorseEntity.func_234256_eJ_().create()).put(EntityType.ZOMBIE_VILLAGER, ZombieEntity.func_234342_eQ_().create()).put(EntityType.ZOMBIFIED_PIGLIN, ZombifiedPiglinEntity.func_234352_eU_().create()).build();

    public static AttributeModifierMap getAttributesForEntity(EntityType <? extends LivingEntity > livingEntity)
    {
        return VANILLA_ATTRIBUTES.get(livingEntity);
    }

    public static boolean doesEntityHaveAttributes(EntityType<?> entityType)
    {
        return VANILLA_ATTRIBUTES.containsKey(entityType);
    }

    public static void validateEntityAttributes()
    {
        Registry.ENTITY_TYPE.stream().filter((entityType) ->
        {
            return entityType.getClassification() != EntityClassification.MISC;
        }).filter((entityType) ->
        {
            return !doesEntityHaveAttributes(entityType);
        }).map(Registry.ENTITY_TYPE::getKey).forEach((entityId) ->
        {
            if (SharedConstants.developmentMode)
            {
                throw new IllegalStateException("Entity " + entityId + " has no attributes");
            }
            else {
                LOGGER.error("Entity {} has no attributes", (Object)entityId);
            }
        });
    }
}
