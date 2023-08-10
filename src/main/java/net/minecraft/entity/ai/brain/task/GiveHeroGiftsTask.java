package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class GiveHeroGiftsTask extends Task<VillagerEntity>
{
    private static final Map<VillagerProfession, ResourceLocation> GIFTS = Util.make(Maps.newHashMap(), (giftMap) ->
    {
        giftMap.put(VillagerProfession.ARMORER, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_ARMORER_GIFT);
        giftMap.put(VillagerProfession.BUTCHER, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_BUTCHER_GIFT);
        giftMap.put(VillagerProfession.CARTOGRAPHER, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_CARTOGRAPHER_GIFT);
        giftMap.put(VillagerProfession.CLERIC, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_CLERIC_GIFT);
        giftMap.put(VillagerProfession.FARMER, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_FARMER_GIFT);
        giftMap.put(VillagerProfession.FISHERMAN, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_FISHERMAN_GIFT);
        giftMap.put(VillagerProfession.FLETCHER, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_FLETCHER_GIFT);
        giftMap.put(VillagerProfession.LEATHERWORKER, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_LEATHERWORKER_GIFT);
        giftMap.put(VillagerProfession.LIBRARIAN, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_LIBRARIAN_GIFT);
        giftMap.put(VillagerProfession.MASON, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_MASON_GIFT);
        giftMap.put(VillagerProfession.SHEPHERD, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_SHEPHERD_GIFT);
        giftMap.put(VillagerProfession.TOOLSMITH, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_TOOLSMITH_GIFT);
        giftMap.put(VillagerProfession.WEAPONSMITH, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_WEAPONSMITH_GIFT);
    });
    private int cooldown = 600;
    private boolean done;
    private long startTime;

    public GiveHeroGiftsTask(int duration)
    {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.INTERACTION_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleStatus.VALUE_PRESENT), duration);
    }

    protected boolean shouldExecute(ServerWorld worldIn, VillagerEntity owner)
    {
        if (!this.hasNearestPlayer(owner))
        {
            return false;
        }
        else if (this.cooldown > 0)
        {
            --this.cooldown;
            return false;
        }
        else
        {
            return true;
        }
    }

    protected void startExecuting(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn)
    {
        this.done = false;
        this.startTime = gameTimeIn;
        PlayerEntity playerentity = this.getNearestPlayer(entityIn).get();
        entityIn.getBrain().setMemory(MemoryModuleType.INTERACTION_TARGET, playerentity);
        BrainUtil.lookAt(entityIn, playerentity);
    }

    protected boolean shouldContinueExecuting(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn)
    {
        return this.hasNearestPlayer(entityIn) && !this.done;
    }

    protected void updateTask(ServerWorld worldIn, VillagerEntity owner, long gameTime)
    {
        PlayerEntity playerentity = this.getNearestPlayer(owner).get();
        BrainUtil.lookAt(owner, playerentity);

        if (this.isCloseEnough(owner, playerentity))
        {
            if (gameTime - this.startTime > 20L)
            {
                this.giveGifts(owner, playerentity);
                this.done = true;
            }
        }
        else
        {
            BrainUtil.setTargetEntity(owner, playerentity, 0.5F, 5);
        }
    }

    protected void resetTask(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn)
    {
        this.cooldown = getNextCooldown(worldIn);
        entityIn.getBrain().removeMemory(MemoryModuleType.INTERACTION_TARGET);
        entityIn.getBrain().removeMemory(MemoryModuleType.WALK_TARGET);
        entityIn.getBrain().removeMemory(MemoryModuleType.LOOK_TARGET);
    }

    private void giveGifts(VillagerEntity villager, LivingEntity hero)
    {
        for (ItemStack itemstack : this.getGifts(villager))
        {
            BrainUtil.spawnItemNearEntity(villager, itemstack, hero.getPositionVec());
        }
    }

    private List<ItemStack> getGifts(VillagerEntity villager)
    {
        if (villager.isChild())
        {
            return ImmutableList.of(new ItemStack(Items.POPPY));
        }
        else
        {
            VillagerProfession villagerprofession = villager.getVillagerData().getProfession();

            if (GIFTS.containsKey(villagerprofession))
            {
                LootTable loottable = villager.world.getServer().getLootTableManager().getLootTableFromLocation(GIFTS.get(villagerprofession));
                LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerWorld)villager.world)).withParameter(LootParameters.field_237457_g_, villager.getPositionVec()).withParameter(LootParameters.THIS_ENTITY, villager).withRandom(villager.getRNG());
                return loottable.generate(lootcontext$builder.build(LootParameterSets.GIFT));
            }
            else
            {
                return ImmutableList.of(new ItemStack(Items.WHEAT_SEEDS));
            }
        }
    }

    private boolean hasNearestPlayer(VillagerEntity villager)
    {
        return this.getNearestPlayer(villager).isPresent();
    }

    private Optional<PlayerEntity> getNearestPlayer(VillagerEntity villager)
    {
        return villager.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER).filter(this::isHero);
    }

    private boolean isHero(PlayerEntity player)
    {
        return player.isPotionActive(Effects.HERO_OF_THE_VILLAGE);
    }

    private boolean isCloseEnough(VillagerEntity villager, PlayerEntity hero)
    {
        BlockPos blockpos = hero.getPosition();
        BlockPos blockpos1 = villager.getPosition();
        return blockpos1.withinDistance(blockpos, 5.0D);
    }

    private static int getNextCooldown(ServerWorld world)
    {
        return 600 + world.rand.nextInt(6001);
    }
}
