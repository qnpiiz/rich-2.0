package net.optifine.entity.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.tileentity.TileEntityType;
import net.optifine.Config;
import net.optifine.util.Either;

public class CustomModelRegistry
{
    private static Map<String, ModelAdapter> mapModelAdapters = makeMapModelAdapters();

    private static Map<String, ModelAdapter> makeMapModelAdapters()
    {
        Map<String, ModelAdapter> map = new LinkedHashMap<>();
        addModelAdapter(map, new ModelAdapterArmorStand());
        addModelAdapter(map, new ModelAdapterBat());
        addModelAdapter(map, new ModelAdapterBee());
        addModelAdapter(map, new ModelAdapterBlaze());
        addModelAdapter(map, new ModelAdapterBoat());
        addModelAdapter(map, new ModelAdapterCaveSpider());
        addModelAdapter(map, new ModelAdapterChicken());
        addModelAdapter(map, new ModelAdapterCat());
        addModelAdapter(map, new ModelAdapterCow());
        addModelAdapter(map, new ModelAdapterCod());
        addModelAdapter(map, new ModelAdapterCreeper());
        addModelAdapter(map, new ModelAdapterDragon());
        addModelAdapter(map, new ModelAdapterDonkey());
        addModelAdapter(map, new ModelAdapterDolphin());
        addModelAdapter(map, new ModelAdapterDrowned());
        addModelAdapter(map, new ModelAdapterElderGuardian());
        addModelAdapter(map, new ModelAdapterEnderCrystal());
        addModelAdapter(map, new ModelAdapterEnderman());
        addModelAdapter(map, new ModelAdapterEndermite());
        addModelAdapter(map, new ModelAdapterEvoker());
        addModelAdapter(map, new ModelAdapterEvokerFangs());
        addModelAdapter(map, new ModelAdapterFox());
        addModelAdapter(map, new ModelAdapterGhast());
        addModelAdapter(map, new ModelAdapterGiant());
        addModelAdapter(map, new ModelAdapterGuardian());
        addModelAdapter(map, new ModelAdapterHoglin());
        addModelAdapter(map, new ModelAdapterHorse());
        addModelAdapter(map, new ModelAdapterHusk());
        addModelAdapter(map, new ModelAdapterIllusioner());
        addModelAdapter(map, new ModelAdapterIronGolem());
        addModelAdapter(map, new ModelAdapterLeadKnot());
        addModelAdapter(map, new ModelAdapterLlama());
        addModelAdapter(map, new ModelAdapterLlamaSpit());
        addModelAdapter(map, new ModelAdapterMagmaCube());
        addModelAdapter(map, new ModelAdapterMinecart());
        addModelAdapter(map, new ModelAdapterMinecartChest());
        addModelAdapter(map, new ModelAdapterMinecartCommandBlock());
        addModelAdapter(map, new ModelAdapterMinecartFurnace());
        addModelAdapter(map, new ModelAdapterMinecartHopper());
        addModelAdapter(map, new ModelAdapterMinecartTnt());
        addModelAdapter(map, new ModelAdapterMinecartMobSpawner());
        addModelAdapter(map, new ModelAdapterMooshroom());
        addModelAdapter(map, new ModelAdapterMule());
        addModelAdapter(map, new ModelAdapterOcelot());
        addModelAdapter(map, new ModelAdapterPanda());
        addModelAdapter(map, new ModelAdapterParrot());
        addModelAdapter(map, new ModelAdapterPhantom());
        addModelAdapter(map, new ModelAdapterPig());
        addModelAdapter(map, new ModelAdapterPiglin());
        addModelAdapter(map, new ModelAdapterPiglinBrute());
        addModelAdapter(map, new ModelAdapterPolarBear());
        addModelAdapter(map, new ModelAdapterPillager());
        addModelAdapter(map, new ModelAdapterPufferFishBig());
        addModelAdapter(map, new ModelAdapterPufferFishMedium());
        addModelAdapter(map, new ModelAdapterPufferFishSmall());
        addModelAdapter(map, new ModelAdapterRabbit());
        addModelAdapter(map, new ModelAdapterRavager());
        addModelAdapter(map, new ModelAdapterSalmon());
        addModelAdapter(map, new ModelAdapterSheep());
        addModelAdapter(map, new ModelAdapterShulker());
        addModelAdapter(map, new ModelAdapterShulkerBullet());
        addModelAdapter(map, new ModelAdapterSilverfish());
        addModelAdapter(map, new ModelAdapterSkeleton());
        addModelAdapter(map, new ModelAdapterSkeletonHorse());
        addModelAdapter(map, new ModelAdapterSlime());
        addModelAdapter(map, new ModelAdapterSnowman());
        addModelAdapter(map, new ModelAdapterSpider());
        addModelAdapter(map, new ModelAdapterSquid());
        addModelAdapter(map, new ModelAdapterStray());
        addModelAdapter(map, new ModelAdapterStrider());
        addModelAdapter(map, new ModelAdapterTraderLlama());
        addModelAdapter(map, new ModelAdapterTrident());
        addModelAdapter(map, new ModelAdapterTropicalFishA());
        addModelAdapter(map, new ModelAdapterTropicalFishB());
        addModelAdapter(map, new ModelAdapterTurtle());
        addModelAdapter(map, new ModelAdapterVex());
        addModelAdapter(map, new ModelAdapterVillager());
        addModelAdapter(map, new ModelAdapterVindicator());
        addModelAdapter(map, new ModelAdapterWanderingTrader());
        addModelAdapter(map, new ModelAdapterWitch());
        addModelAdapter(map, new ModelAdapterWither());
        addModelAdapter(map, new ModelAdapterWitherSkeleton());
        addModelAdapter(map, new ModelAdapterWitherSkull());
        addModelAdapter(map, new ModelAdapterWolf());
        addModelAdapter(map, new ModelAdapterZoglin());
        addModelAdapter(map, new ModelAdapterZombie());
        addModelAdapter(map, new ModelAdapterZombieHorse());
        addModelAdapter(map, new ModelAdapterZombieVillager());
        addModelAdapter(map, new ModelAdapterZombifiedPiglin());
        addModelAdapter(map, new ModelAdapterSheepWool());
        addModelAdapter(map, new ModelAdapterLlamaDecor());
        addModelAdapter(map, new ModelAdapterBanner());
        addModelAdapter(map, new ModelAdapterBed());
        addModelAdapter(map, new ModelAdapterBell());
        addModelAdapter(map, new ModelAdapterBook());
        addModelAdapter(map, new ModelAdapterBookLectern());
        addModelAdapter(map, new ModelAdapterChest());
        addModelAdapter(map, new ModelAdapterChestLarge());
        addModelAdapter(map, new ModelAdapterConduit());
        addModelAdapter(map, new ModelAdapterEnderChest());
        addModelAdapter(map, new ModelAdapterHeadCreeper());
        addModelAdapter(map, new ModelAdapterHeadDragon());
        addModelAdapter(map, new ModelAdapterHeadPlayer());
        addModelAdapter(map, new ModelAdapterHeadSkeleton());
        addModelAdapter(map, new ModelAdapterHeadWitherSkeleton());
        addModelAdapter(map, new ModelAdapterHeadZombie());
        addModelAdapter(map, new ModelAdapterShulkerBox());
        addModelAdapter(map, new ModelAdapterSign());
        addModelAdapter(map, new ModelAdapterTrappedChest());
        addModelAdapter(map, new ModelAdapterTrappedChestLarge());
        return map;
    }

    private static void addModelAdapter(Map<String, ModelAdapter> map, ModelAdapter modelAdapter)
    {
        addModelAdapter(map, modelAdapter, modelAdapter.getName());
        String[] astring = modelAdapter.getAliases();

        if (astring != null)
        {
            for (int i = 0; i < astring.length; ++i)
            {
                String s = astring[i];
                addModelAdapter(map, modelAdapter, s);
            }
        }

        Model model = modelAdapter.makeModel();
        String[] astring1 = modelAdapter.getModelRendererNames();

        for (int j = 0; j < astring1.length; ++j)
        {
            String s1 = astring1[j];
            ModelRenderer modelrenderer = modelAdapter.getModelRenderer(model, s1);

            if (modelrenderer == null)
            {
                Config.warn("Model renderer not found, model: " + modelAdapter.getName() + ", name: " + s1);
            }
        }
    }

    private static void addModelAdapter(Map<String, ModelAdapter> map, ModelAdapter modelAdapter, String name)
    {
        if (map.containsKey(name))
        {
            String s = "?";
            Either<EntityType, TileEntityType> either = modelAdapter.getType();

            if (either.getLeft().isPresent())
            {
                s = either.getLeft().get().getTranslationKey();
            }

            if (either.getRight().isPresent())
            {
                s = "" + TileEntityType.getId(either.getRight().get());
            }

            Config.warn("Model adapter already registered for id: " + name + ", type: " + s);
        }

        map.put(name, modelAdapter);
    }

    public static ModelAdapter getModelAdapter(String name)
    {
        return mapModelAdapters.get(name);
    }

    public static String[] getModelNames()
    {
        Set<String> set = mapModelAdapters.keySet();
        return set.toArray(new String[set.size()]);
    }
}
