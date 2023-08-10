package net.minecraft.item.crafting;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Util;

public final class RecipeBookStatus
{
    private static final Map<RecipeBookCategory, Pair<String, String>> field_242147_a = ImmutableMap.of(RecipeBookCategory.CRAFTING, Pair.of("isGuiOpen", "isFilteringCraftable"), RecipeBookCategory.FURNACE, Pair.of("isFurnaceGuiOpen", "isFurnaceFilteringCraftable"), RecipeBookCategory.BLAST_FURNACE, Pair.of("isBlastingFurnaceGuiOpen", "isBlastingFurnaceFilteringCraftable"), RecipeBookCategory.SMOKER, Pair.of("isSmokerGuiOpen", "isSmokerFilteringCraftable"));
    private final Map<RecipeBookCategory, RecipeBookStatus.CategoryStatus> field_242148_b;

    private RecipeBookStatus(Map<RecipeBookCategory, RecipeBookStatus.CategoryStatus> p_i241892_1_)
    {
        this.field_242148_b = p_i241892_1_;
    }

    public RecipeBookStatus()
    {
        this(Util.make(Maps.newEnumMap(RecipeBookCategory.class), (p_242153_0_) ->
        {
            for (RecipeBookCategory recipebookcategory : RecipeBookCategory.values())
            {
                p_242153_0_.put(recipebookcategory, new RecipeBookStatus.CategoryStatus(false, false));
            }
        }));
    }

    public boolean func_242151_a(RecipeBookCategory p_242151_1_)
    {
        return (this.field_242148_b.get(p_242151_1_)).field_242162_a;
    }

    public void func_242152_a(RecipeBookCategory p_242152_1_, boolean p_242152_2_)
    {
        (this.field_242148_b.get(p_242152_1_)).field_242162_a = p_242152_2_;
    }

    public boolean func_242158_b(RecipeBookCategory p_242158_1_)
    {
        return (this.field_242148_b.get(p_242158_1_)).field_242163_b;
    }

    public void func_242159_b(RecipeBookCategory p_242159_1_, boolean p_242159_2_)
    {
        (this.field_242148_b.get(p_242159_1_)).field_242163_b = p_242159_2_;
    }

    public static RecipeBookStatus func_242157_a(PacketBuffer p_242157_0_)
    {
        Map<RecipeBookCategory, RecipeBookStatus.CategoryStatus> map = Maps.newEnumMap(RecipeBookCategory.class);

        for (RecipeBookCategory recipebookcategory : RecipeBookCategory.values())
        {
            boolean flag = p_242157_0_.readBoolean();
            boolean flag1 = p_242157_0_.readBoolean();
            map.put(recipebookcategory, new RecipeBookStatus.CategoryStatus(flag, flag1));
        }

        return new RecipeBookStatus(map);
    }

    public void func_242161_b(PacketBuffer p_242161_1_)
    {
        for (RecipeBookCategory recipebookcategory : RecipeBookCategory.values())
        {
            RecipeBookStatus.CategoryStatus recipebookstatus$categorystatus = this.field_242148_b.get(recipebookcategory);

            if (recipebookstatus$categorystatus == null)
            {
                p_242161_1_.writeBoolean(false);
                p_242161_1_.writeBoolean(false);
            }
            else
            {
                p_242161_1_.writeBoolean(recipebookstatus$categorystatus.field_242162_a);
                p_242161_1_.writeBoolean(recipebookstatus$categorystatus.field_242163_b);
            }
        }
    }

    public static RecipeBookStatus func_242154_a(CompoundNBT p_242154_0_)
    {
        Map<RecipeBookCategory, RecipeBookStatus.CategoryStatus> map = Maps.newEnumMap(RecipeBookCategory.class);
        field_242147_a.forEach((p_242156_2_, p_242156_3_) ->
        {
            boolean flag = p_242154_0_.getBoolean(p_242156_3_.getFirst());
            boolean flag1 = p_242154_0_.getBoolean(p_242156_3_.getSecond());
            map.put(p_242156_2_, new RecipeBookStatus.CategoryStatus(flag, flag1));
        });
        return new RecipeBookStatus(map);
    }

    public void func_242160_b(CompoundNBT p_242160_1_)
    {
        field_242147_a.forEach((p_242155_2_, p_242155_3_) ->
        {
            RecipeBookStatus.CategoryStatus recipebookstatus$categorystatus = this.field_242148_b.get(p_242155_2_);
            p_242160_1_.putBoolean(p_242155_3_.getFirst(), recipebookstatus$categorystatus.field_242162_a);
            p_242160_1_.putBoolean(p_242155_3_.getSecond(), recipebookstatus$categorystatus.field_242163_b);
        });
    }

    public RecipeBookStatus func_242149_a()
    {
        Map<RecipeBookCategory, RecipeBookStatus.CategoryStatus> map = Maps.newEnumMap(RecipeBookCategory.class);

        for (RecipeBookCategory recipebookcategory : RecipeBookCategory.values())
        {
            RecipeBookStatus.CategoryStatus recipebookstatus$categorystatus = this.field_242148_b.get(recipebookcategory);
            map.put(recipebookcategory, recipebookstatus$categorystatus.func_242164_a());
        }

        return new RecipeBookStatus(map);
    }

    public void func_242150_a(RecipeBookStatus p_242150_1_)
    {
        this.field_242148_b.clear();

        for (RecipeBookCategory recipebookcategory : RecipeBookCategory.values())
        {
            RecipeBookStatus.CategoryStatus recipebookstatus$categorystatus = p_242150_1_.field_242148_b.get(recipebookcategory);
            this.field_242148_b.put(recipebookcategory, recipebookstatus$categorystatus.func_242164_a());
        }
    }

    public boolean equals(Object p_equals_1_)
    {
        return this == p_equals_1_ || p_equals_1_ instanceof RecipeBookStatus && this.field_242148_b.equals(((RecipeBookStatus)p_equals_1_).field_242148_b);
    }

    public int hashCode()
    {
        return this.field_242148_b.hashCode();
    }

    static final class CategoryStatus
    {
        private boolean field_242162_a;
        private boolean field_242163_b;

        public CategoryStatus(boolean p_i241893_1_, boolean p_i241893_2_)
        {
            this.field_242162_a = p_i241893_1_;
            this.field_242163_b = p_i241893_2_;
        }

        public RecipeBookStatus.CategoryStatus func_242164_a()
        {
            return new RecipeBookStatus.CategoryStatus(this.field_242162_a, this.field_242163_b);
        }

        public boolean equals(Object p_equals_1_)
        {
            if (this == p_equals_1_)
            {
                return true;
            }
            else if (!(p_equals_1_ instanceof RecipeBookStatus.CategoryStatus))
            {
                return false;
            }
            else
            {
                RecipeBookStatus.CategoryStatus recipebookstatus$categorystatus = (RecipeBookStatus.CategoryStatus)p_equals_1_;
                return this.field_242162_a == recipebookstatus$categorystatus.field_242162_a && this.field_242163_b == recipebookstatus$categorystatus.field_242163_b;
            }
        }

        public int hashCode()
        {
            int i = this.field_242162_a ? 1 : 0;
            return 31 * i + (this.field_242163_b ? 1 : 0);
        }

        public String toString()
        {
            return "[open=" + this.field_242162_a + ", filtering=" + this.field_242163_b + ']';
        }
    }
}
