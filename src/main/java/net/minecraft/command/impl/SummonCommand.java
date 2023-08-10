package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntitySummonArgument;
import net.minecraft.command.arguments.NBTCompoundTagArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class SummonCommand
{
    private static final SimpleCommandExceptionType SUMMON_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.summon.failed"));
    private static final SimpleCommandExceptionType field_244378_b = new SimpleCommandExceptionType(new TranslationTextComponent("commands.summon.failed.uuid"));
    private static final SimpleCommandExceptionType field_241075_b_ = new SimpleCommandExceptionType(new TranslationTextComponent("commands.summon.invalidPosition"));

    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(Commands.literal("summon").requires((p_198740_0_) ->
        {
            return p_198740_0_.hasPermissionLevel(2);
        }).then(Commands.argument("entity", EntitySummonArgument.entitySummon()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes((p_198738_0_) ->
        {
            return summonEntity(p_198738_0_.getSource(), EntitySummonArgument.getEntityId(p_198738_0_, "entity"), p_198738_0_.getSource().getPos(), new CompoundNBT(), true);
        }).then(Commands.argument("pos", Vec3Argument.vec3()).executes((p_198735_0_) ->
        {
            return summonEntity(p_198735_0_.getSource(), EntitySummonArgument.getEntityId(p_198735_0_, "entity"), Vec3Argument.getVec3(p_198735_0_, "pos"), new CompoundNBT(), true);
        }).then(Commands.argument("nbt", NBTCompoundTagArgument.nbt()).executes((p_198739_0_) ->
        {
            return summonEntity(p_198739_0_.getSource(), EntitySummonArgument.getEntityId(p_198739_0_, "entity"), Vec3Argument.getVec3(p_198739_0_, "pos"), NBTCompoundTagArgument.getNbt(p_198739_0_, "nbt"), false);
        })))));
    }

    private static int summonEntity(CommandSource source, ResourceLocation type, Vector3d pos, CompoundNBT nbt, boolean randomizeProperties) throws CommandSyntaxException
    {
        BlockPos blockpos = new BlockPos(pos);

        if (!World.isInvalidPosition(blockpos))
        {
            throw field_241075_b_.create();
        }
        else
        {
            CompoundNBT compoundnbt = nbt.copy();
            compoundnbt.putString("id", type.toString());
            ServerWorld serverworld = source.getWorld();
            Entity entity = EntityType.loadEntityAndExecute(compoundnbt, serverworld, (p_218914_1_) ->
            {
                p_218914_1_.setLocationAndAngles(pos.x, pos.y, pos.z, p_218914_1_.rotationYaw, p_218914_1_.rotationPitch);
                return p_218914_1_;
            });

            if (entity == null)
            {
                throw SUMMON_FAILED.create();
            }
            else
            {
                if (randomizeProperties && entity instanceof MobEntity)
                {
                    ((MobEntity)entity).onInitialSpawn(source.getWorld(), source.getWorld().getDifficultyForLocation(entity.getPosition()), SpawnReason.COMMAND, (ILivingEntityData)null, (CompoundNBT)null);
                }

                if (!serverworld.func_242106_g(entity))
                {
                    throw field_244378_b.create();
                }
                else
                {
                    source.sendFeedback(new TranslationTextComponent("commands.summon.success", entity.getDisplayName()), true);
                    return 1;
                }
            }
        }
    }
}
