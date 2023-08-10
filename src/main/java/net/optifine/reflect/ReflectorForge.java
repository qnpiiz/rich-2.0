package net.optifine.reflect;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.optifine.Log;
import net.optifine.util.StrUtils;

public class ReflectorForge
{
    public static Object EVENT_RESULT_ALLOW = Reflector.getFieldValue(Reflector.Event_Result_ALLOW);
    public static Object EVENT_RESULT_DENY = Reflector.getFieldValue(Reflector.Event_Result_DENY);
    public static Object EVENT_RESULT_DEFAULT = Reflector.getFieldValue(Reflector.Event_Result_DEFAULT);
    public static final boolean FORGE_BLOCKSTATE_HAS_TILE_ENTITY = Reflector.IForgeBlockState_hasTileEntity.exists();
    public static final boolean FORGE_ENTITY_CAN_UPDATE = Reflector.IForgeEntity_canUpdate.exists();

    public static void putLaunchBlackboard(String key, Object value)
    {
        Map map = (Map)Reflector.getFieldValue(Reflector.Launch_blackboard);

        if (map != null)
        {
            map.put(key, value);
        }
    }

    public static InputStream getOptiFineResourceStream(String path)
    {
        if (!Reflector.OptiFineResourceLocator.exists())
        {
            return null;
        }
        else
        {
            path = StrUtils.removePrefix(path, "/");
            return (InputStream)Reflector.call(Reflector.OptiFineResourceLocator_getOptiFineResourceStream, path);
        }
    }

    public static ReflectorClass getReflectorClassOptiFineResourceLocator()
    {
        String s = "optifine.OptiFineResourceLocator";
        Object object = System.getProperties().get(s + ".class");

        if (object instanceof Class)
        {
            Class oclass = (Class)object;
            return new ReflectorClass(oclass);
        }
        else
        {
            return new ReflectorClass(s);
        }
    }

    public static boolean blockHasTileEntity(BlockState state)
    {
        return FORGE_BLOCKSTATE_HAS_TILE_ENTITY ? Reflector.callBoolean(state, Reflector.IForgeBlockState_hasTileEntity) : state.getBlock().isTileEntityProvider();
    }

    public static boolean isItemDamaged(ItemStack stack)
    {
        return !Reflector.IForgeItem_showDurabilityBar.exists() ? stack.isDamaged() : Reflector.callBoolean(stack.getItem(), Reflector.IForgeItem_showDurabilityBar, stack);
    }

    public static int getLightValue(BlockState stateIn, IBlockDisplayReader worldIn, BlockPos posIn)
    {
        return Reflector.IForgeBlockState_getLightValue2.exists() ? Reflector.callInt(stateIn, Reflector.IForgeBlockState_getLightValue2, worldIn, posIn) : stateIn.getLightValue();
    }

    public static MapData getMapData(ItemStack stack, World world)
    {
        if (Reflector.ForgeHooksClient.exists())
        {
            FilledMapItem filledmapitem = (FilledMapItem)stack.getItem();
            return FilledMapItem.getMapData(stack, world);
        }
        else
        {
            return FilledMapItem.getMapData(stack, world);
        }
    }

    public static String[] getForgeModIds()
    {
        if (!Reflector.Loader.exists())
        {
            return new String[0];
        }
        else
        {
            Object object = Reflector.call(Reflector.Loader_instance);
            List list = (List)Reflector.call(object, Reflector.Loader_getActiveModList);

            if (list == null)
            {
                return new String[0];
            }
            else
            {
                List<String> list1 = new ArrayList<>();

                for (Object object1 : list)
                {
                    if (Reflector.ModContainer.isInstance(object1))
                    {
                        String s = Reflector.callString(object1, Reflector.ModContainer_getModId);

                        if (s != null)
                        {
                            list1.add(s);
                        }
                    }
                }

                String[] astring = list1.toArray(new String[list1.size()]);
                return astring;
            }
        }
    }

    public static boolean isAir(BlockState blockState, IBlockReader world, BlockPos pos)
    {
        return Reflector.IForgeBlockState_isAir2.exists() ? Reflector.callBoolean(blockState, Reflector.IForgeBlockState_isAir2, world, pos) : blockState.isAir();
    }

    public static boolean canDisableShield(ItemStack itemstack, ItemStack itemstack1, PlayerEntity entityplayer, MobEntity entityLiving)
    {
        return Reflector.IForgeItemStack_canDisableShield.exists() ? Reflector.callBoolean(itemstack, Reflector.IForgeItemStack_canDisableShield, itemstack1, entityplayer, entityLiving) : itemstack.getItem() instanceof AxeItem;
    }

    public static boolean isShield(ItemStack itemstack, PlayerEntity entityplayer)
    {
        if (Reflector.IForgeItemStack_isShield.exists())
        {
            return Reflector.callBoolean(itemstack, Reflector.IForgeItemStack_isShield, entityplayer);
        }
        else
        {
            return itemstack.getItem() == Items.SHIELD;
        }
    }

    public static Button makeButtonMods(MainMenuScreen guiMainMenu, int yIn, int rowHeightIn)
    {
        return !Reflector.ModListScreen_Constructor.exists() ? null : new Button(guiMainMenu.width / 2 - 100, yIn + rowHeightIn * 2, 98, 20, new TranslationTextComponent("fml.menu.mods"), (button) ->
        {
            Screen screen = (Screen)Reflector.ModListScreen_Constructor.newInstance(guiMainMenu);
            Minecraft.getInstance().displayGuiScreen(screen);
        });
    }

    public static void setForgeLightPipelineEnabled(boolean value)
    {
        if (Reflector.ForgeConfig_Client_forgeLightPipelineEnabled.exists())
        {
            setConfigClientBoolean(Reflector.ForgeConfig_Client_forgeLightPipelineEnabled, value);
        }
    }

    public static boolean getForgeUseCombinedDepthStencilAttachment()
    {
        return Reflector.ForgeConfig_Client_useCombinedDepthStencilAttachment.exists() ? getConfigClientBoolean(Reflector.ForgeConfig_Client_useCombinedDepthStencilAttachment, false) : false;
    }

    public static boolean getConfigClientBoolean(ReflectorField configField, boolean def)
    {
        if (!configField.exists())
        {
            return def;
        }
        else
        {
            Object object = Reflector.ForgeConfig_CLIENT.getValue();

            if (object == null)
            {
                return def;
            }
            else
            {
                Object object1 = Reflector.getFieldValue(object, configField);
                return object1 == null ? def : Reflector.callBoolean(object1, Reflector.ForgeConfigSpec_ConfigValue_get);
            }
        }
    }

    private static void setConfigClientBoolean(ReflectorField clientField, final boolean value)
    {
        if (clientField.exists())
        {
            Object object = Reflector.ForgeConfig_CLIENT.getValue();

            if (object != null)
            {
                Object object1 = Reflector.getFieldValue(object, clientField);

                if (object1 != null)
                {
                    Supplier<Boolean> supplier = new Supplier<Boolean>()
                    {
                        public Boolean get()
                        {
                            return value;
                        }
                    };
                    Reflector.setFieldValue(object1, Reflector.ForgeConfigSpec_ConfigValue_defaultSupplier, supplier);
                    Object object2 = Reflector.getFieldValue(object1, Reflector.ForgeConfigSpec_ConfigValue_spec);

                    if (object2 != null)
                    {
                        Reflector.setFieldValue(object2, Reflector.ForgeConfigSpec_childConfig, (Object)null);
                    }

                    Log.dbg("Set ForgeConfig.CLIENT." + clientField.getTargetField().getName() + "=" + value);
                }
            }
        }
    }

    public static boolean canUpdate(Entity entity)
    {
        return FORGE_ENTITY_CAN_UPDATE ? Reflector.callBoolean(entity, Reflector.IForgeEntity_canUpdate) : true;
    }

    public static boolean isDamageable(Item item, ItemStack stack)
    {
        return Reflector.IForgeItem_isDamageable1.exists() ? Reflector.callBoolean(item, Reflector.IForgeItem_isDamageable1, stack) : item.isDamageable();
    }

    public static void fillNormal(int[] faceData, Direction facing)
    {
        Vector3f vector3f = getVertexPos(faceData, 3);
        Vector3f vector3f1 = getVertexPos(faceData, 1);
        Vector3f vector3f2 = getVertexPos(faceData, 2);
        Vector3f vector3f3 = getVertexPos(faceData, 0);
        vector3f.sub(vector3f1);
        vector3f2.sub(vector3f3);
        vector3f2.cross(vector3f);
        vector3f2.normalize();
        int i = (byte)Math.round(vector3f2.getX() * 127.0F) & 255;
        int j = (byte)Math.round(vector3f2.getY() * 127.0F) & 255;
        int k = (byte)Math.round(vector3f2.getZ() * 127.0F) & 255;
        int l = i | j << 8 | k << 16;
        int i1 = faceData.length / 4;

        for (int j1 = 0; j1 < 4; ++j1)
        {
            faceData[j1 * i1 + 7] = l;
        }
    }

    private static Vector3f getVertexPos(int[] data, int vertex)
    {
        int i = data.length / 4;
        int j = vertex * i;
        float f = Float.intBitsToFloat(data[j]);
        float f1 = Float.intBitsToFloat(data[j + 1]);
        float f2 = Float.intBitsToFloat(data[j + 2]);
        return new Vector3f(f, f1, f2);
    }
}
