package net.minecraft.inventory.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.registry.Registry;

public class ContainerType<T extends Container>
{
    public static final ContainerType<ChestContainer> GENERIC_9X1 = register("generic_9x1", ChestContainer::createGeneric9X1);
    public static final ContainerType<ChestContainer> GENERIC_9X2 = register("generic_9x2", ChestContainer::createGeneric9X2);
    public static final ContainerType<ChestContainer> GENERIC_9X3 = register("generic_9x3", ChestContainer::createGeneric9X3);
    public static final ContainerType<ChestContainer> GENERIC_9X4 = register("generic_9x4", ChestContainer::createGeneric9X4);
    public static final ContainerType<ChestContainer> GENERIC_9X5 = register("generic_9x5", ChestContainer::createGeneric9X5);
    public static final ContainerType<ChestContainer> GENERIC_9X6 = register("generic_9x6", ChestContainer::createGeneric9X6);
    public static final ContainerType<DispenserContainer> GENERIC_3X3 = register("generic_3x3", DispenserContainer::new);
    public static final ContainerType<RepairContainer> ANVIL = register("anvil", RepairContainer::new);
    public static final ContainerType<BeaconContainer> BEACON = register("beacon", BeaconContainer::new);
    public static final ContainerType<BlastFurnaceContainer> BLAST_FURNACE = register("blast_furnace", BlastFurnaceContainer::new);
    public static final ContainerType<BrewingStandContainer> BREWING_STAND = register("brewing_stand", BrewingStandContainer::new);
    public static final ContainerType<WorkbenchContainer> CRAFTING = register("crafting", WorkbenchContainer::new);
    public static final ContainerType<EnchantmentContainer> ENCHANTMENT = register("enchantment", EnchantmentContainer::new);
    public static final ContainerType<FurnaceContainer> FURNACE = register("furnace", FurnaceContainer::new);
    public static final ContainerType<GrindstoneContainer> GRINDSTONE = register("grindstone", GrindstoneContainer::new);
    public static final ContainerType<HopperContainer> HOPPER = register("hopper", HopperContainer::new);
    public static final ContainerType<LecternContainer> LECTERN = register("lectern", (p_221504_0_, p_221504_1_) ->
    {
        return new LecternContainer(p_221504_0_);
    });
    public static final ContainerType<LoomContainer> LOOM = register("loom", LoomContainer::new);
    public static final ContainerType<MerchantContainer> MERCHANT = register("merchant", MerchantContainer::new);
    public static final ContainerType<ShulkerBoxContainer> SHULKER_BOX = register("shulker_box", ShulkerBoxContainer::new);
    public static final ContainerType<SmithingTableContainer> SMITHING = register("smithing", SmithingTableContainer::new);
    public static final ContainerType<SmokerContainer> SMOKER = register("smoker", SmokerContainer::new);
    public static final ContainerType<CartographyContainer> CARTOGRAPHY_TABLE = register("cartography_table", CartographyContainer::new);
    public static final ContainerType<StonecutterContainer> STONECUTTER = register("stonecutter", StonecutterContainer::new);
    private final ContainerType.IFactory<T> factory;

    private static <T extends Container> ContainerType<T> register(String key, ContainerType.IFactory<T> factory)
    {
        return Registry.register(Registry.MENU, key, new ContainerType<>(factory));
    }

    private ContainerType(ContainerType.IFactory<T> factory)
    {
        this.factory = factory;
    }

    public T create(int windowId, PlayerInventory player)
    {
        return this.factory.create(windowId, player);
    }

    interface IFactory<T extends Container>
    {
        T create(int p_create_1_, PlayerInventory p_create_2_);
    }
}
