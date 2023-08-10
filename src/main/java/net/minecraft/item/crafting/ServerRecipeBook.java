package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.play.server.SRecipeBookPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerRecipeBook extends RecipeBook
{
    private static final Logger LOGGER = LogManager.getLogger();

    public int add(Collection < IRecipe<? >> recipes, ServerPlayerEntity player)
    {
        List<ResourceLocation> list = Lists.newArrayList();
        int i = 0;

        for (IRecipe<?> irecipe : recipes)
        {
            ResourceLocation resourcelocation = irecipe.getId();

            if (!this.recipes.contains(resourcelocation) && !irecipe.isDynamic())
            {
                this.unlock(resourcelocation);
                this.markNew(resourcelocation);
                list.add(resourcelocation);
                CriteriaTriggers.RECIPE_UNLOCKED.trigger(player, irecipe);
                ++i;
            }
        }

        this.sendPacket(SRecipeBookPacket.State.ADD, player, list);
        return i;
    }

    public int remove(Collection < IRecipe<? >> recipes, ServerPlayerEntity player)
    {
        List<ResourceLocation> list = Lists.newArrayList();
        int i = 0;

        for (IRecipe<?> irecipe : recipes)
        {
            ResourceLocation resourcelocation = irecipe.getId();

            if (this.recipes.contains(resourcelocation))
            {
                this.lock(resourcelocation);
                list.add(resourcelocation);
                ++i;
            }
        }

        this.sendPacket(SRecipeBookPacket.State.REMOVE, player, list);
        return i;
    }

    private void sendPacket(SRecipeBookPacket.State state, ServerPlayerEntity player, List<ResourceLocation> recipesIn)
    {
        player.connection.sendPacket(new SRecipeBookPacket(state, recipesIn, Collections.emptyList(), this.func_242139_a()));
    }

    public CompoundNBT write()
    {
        CompoundNBT compoundnbt = new CompoundNBT();
        this.func_242139_a().func_242160_b(compoundnbt);
        ListNBT listnbt = new ListNBT();

        for (ResourceLocation resourcelocation : this.recipes)
        {
            listnbt.add(StringNBT.valueOf(resourcelocation.toString()));
        }

        compoundnbt.put("recipes", listnbt);
        ListNBT listnbt1 = new ListNBT();

        for (ResourceLocation resourcelocation1 : this.newRecipes)
        {
            listnbt1.add(StringNBT.valueOf(resourcelocation1.toString()));
        }

        compoundnbt.put("toBeDisplayed", listnbt1);
        return compoundnbt;
    }

    public void read(CompoundNBT tag, RecipeManager recipeManager)
    {
        this.func_242140_a(RecipeBookStatus.func_242154_a(tag));
        ListNBT listnbt = tag.getList("recipes", 8);
        this.deserializeRecipes(listnbt, this::unlock, recipeManager);
        ListNBT listnbt1 = tag.getList("toBeDisplayed", 8);
        this.deserializeRecipes(listnbt1, this::markNew, recipeManager);
    }

    private void deserializeRecipes(ListNBT nbtList, Consumer < IRecipe<? >> recipeConsumer, RecipeManager recipeManager)
    {
        for (int i = 0; i < nbtList.size(); ++i)
        {
            String s = nbtList.getString(i);

            try
            {
                ResourceLocation resourcelocation = new ResourceLocation(s);
                Optional <? extends IRecipe<? >> optional = recipeManager.getRecipe(resourcelocation);

                if (!optional.isPresent())
                {
                    LOGGER.error("Tried to load unrecognized recipe: {} removed now.", (Object)resourcelocation);
                }
                else
                {
                    recipeConsumer.accept(optional.get());
                }
            }
            catch (ResourceLocationException resourcelocationexception)
            {
                LOGGER.error("Tried to load improperly formatted recipe: {} removed now.", (Object)s);
            }
        }
    }

    public void init(ServerPlayerEntity player)
    {
        player.connection.sendPacket(new SRecipeBookPacket(SRecipeBookPacket.State.INIT, this.recipes, this.newRecipes, this.func_242139_a()));
    }
}
