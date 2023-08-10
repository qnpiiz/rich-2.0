package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;

public class FluidPredicate
{
    public static final FluidPredicate ANY = new FluidPredicate((ITag<Fluid>)null, (Fluid)null, StatePropertiesPredicate.EMPTY);
    @Nullable
    private final ITag<Fluid> fluidTag;
    @Nullable
    private final Fluid fluid;
    private final StatePropertiesPredicate stateCondition;

    public FluidPredicate(@Nullable ITag<Fluid> fluidTag, @Nullable Fluid fluid, StatePropertiesPredicate stateCondition)
    {
        this.fluidTag = fluidTag;
        this.fluid = fluid;
        this.stateCondition = stateCondition;
    }

    public boolean test(ServerWorld world, BlockPos pos)
    {
        if (this == ANY)
        {
            return true;
        }
        else if (!world.isBlockPresent(pos))
        {
            return false;
        }
        else
        {
            FluidState fluidstate = world.getFluidState(pos);
            Fluid fluid = fluidstate.getFluid();

            if (this.fluidTag != null && !this.fluidTag.contains(fluid))
            {
                return false;
            }
            else if (this.fluid != null && fluid != this.fluid)
            {
                return false;
            }
            else
            {
                return this.stateCondition.matches(fluidstate);
            }
        }
    }

    public static FluidPredicate deserialize(@Nullable JsonElement element)
    {
        if (element != null && !element.isJsonNull())
        {
            JsonObject jsonobject = JSONUtils.getJsonObject(element, "fluid");
            Fluid fluid = null;

            if (jsonobject.has("fluid"))
            {
                ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(jsonobject, "fluid"));
                fluid = Registry.FLUID.getOrDefault(resourcelocation);
            }

            ITag<Fluid> itag = null;

            if (jsonobject.has("tag"))
            {
                ResourceLocation resourcelocation1 = new ResourceLocation(JSONUtils.getString(jsonobject, "tag"));
                itag = TagCollectionManager.getManager().getFluidTags().get(resourcelocation1);

                if (itag == null)
                {
                    throw new JsonSyntaxException("Unknown fluid tag '" + resourcelocation1 + "'");
                }
            }

            StatePropertiesPredicate statepropertiespredicate = StatePropertiesPredicate.deserializeProperties(jsonobject.get("state"));
            return new FluidPredicate(itag, fluid, statepropertiespredicate);
        }
        else
        {
            return ANY;
        }
    }

    public JsonElement serialize()
    {
        if (this == ANY)
        {
            return JsonNull.INSTANCE;
        }
        else
        {
            JsonObject jsonobject = new JsonObject();

            if (this.fluid != null)
            {
                jsonobject.addProperty("fluid", Registry.FLUID.getKey(this.fluid).toString());
            }

            if (this.fluidTag != null)
            {
                jsonobject.addProperty("tag", TagCollectionManager.getManager().getFluidTags().getValidatedIdFromTag(this.fluidTag).toString());
            }

            jsonobject.add("state", this.stateCondition.toJsonElement());
            return jsonobject;
        }
    }
}
