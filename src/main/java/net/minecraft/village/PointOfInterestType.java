package net.minecraft.village;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.state.properties.BedPart;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class PointOfInterestType
{
    private static final Supplier<Set<PointOfInterestType>> WORKSTATIONS = Suppliers.memoize(() ->
    {
        return Registry.VILLAGER_PROFESSION.stream().map(VillagerProfession::getPointOfInterest).collect(Collectors.toSet());
    });
    public static final Predicate<PointOfInterestType> ANY_VILLAGER_WORKSTATION = (type) ->
    {
        return WORKSTATIONS.get().contains(type);
    };
    public static final Predicate<PointOfInterestType> MATCH_ANY = (type) ->
    {
        return true;
    };
    private static final Set<BlockState> BED_HEADS = ImmutableList.of(Blocks.RED_BED, Blocks.BLACK_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.CYAN_BED, Blocks.GRAY_BED, Blocks.GREEN_BED, Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_GRAY_BED, Blocks.LIME_BED, Blocks.MAGENTA_BED, Blocks.ORANGE_BED, Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED).stream().flatMap((block) ->
    {
        return block.getStateContainer().getValidStates().stream();
    }).filter((state) ->
    {
        return state.get(BedBlock.PART) == BedPart.HEAD;
    }).collect(ImmutableSet.toImmutableSet());
    private static final Map<BlockState, PointOfInterestType> POIT_BY_BLOCKSTATE = Maps.newHashMap();
    public static final PointOfInterestType UNEMPLOYED = register("unemployed", ImmutableSet.of(), 1, ANY_VILLAGER_WORKSTATION, 1);
    public static final PointOfInterestType ARMORER = register("armorer", getAllStates(Blocks.BLAST_FURNACE), 1, 1);
    public static final PointOfInterestType BUTCHER = register("butcher", getAllStates(Blocks.SMOKER), 1, 1);
    public static final PointOfInterestType CARTOGRAPHER = register("cartographer", getAllStates(Blocks.CARTOGRAPHY_TABLE), 1, 1);
    public static final PointOfInterestType CLERIC = register("cleric", getAllStates(Blocks.BREWING_STAND), 1, 1);
    public static final PointOfInterestType FARMER = register("farmer", getAllStates(Blocks.COMPOSTER), 1, 1);
    public static final PointOfInterestType FISHERMAN = register("fisherman", getAllStates(Blocks.BARREL), 1, 1);
    public static final PointOfInterestType FLETCHER = register("fletcher", getAllStates(Blocks.FLETCHING_TABLE), 1, 1);
    public static final PointOfInterestType LEATHERWORKER = register("leatherworker", getAllStates(Blocks.CAULDRON), 1, 1);
    public static final PointOfInterestType LIBRARIAN = register("librarian", getAllStates(Blocks.LECTERN), 1, 1);
    public static final PointOfInterestType MASON = register("mason", getAllStates(Blocks.STONECUTTER), 1, 1);
    public static final PointOfInterestType NITWIT = register("nitwit", ImmutableSet.of(), 1, 1);
    public static final PointOfInterestType SHEPHERD = register("shepherd", getAllStates(Blocks.LOOM), 1, 1);
    public static final PointOfInterestType TOOLSMITH = register("toolsmith", getAllStates(Blocks.SMITHING_TABLE), 1, 1);
    public static final PointOfInterestType WEAPONSMITH = register("weaponsmith", getAllStates(Blocks.GRINDSTONE), 1, 1);
    public static final PointOfInterestType HOME = register("home", BED_HEADS, 1, 1);
    public static final PointOfInterestType MEETING = register("meeting", getAllStates(Blocks.BELL), 32, 6);
    public static final PointOfInterestType BEEHIVE = register("beehive", getAllStates(Blocks.BEEHIVE), 0, 1);
    public static final PointOfInterestType BEE_NEST = register("bee_nest", getAllStates(Blocks.BEE_NEST), 0, 1);
    public static final PointOfInterestType NETHER_PORTAL = register("nether_portal", getAllStates(Blocks.NETHER_PORTAL), 0, 1);
    public static final PointOfInterestType LODESTONE = register("lodestone", getAllStates(Blocks.LODESTONE), 0, 1);
    protected static final Set<BlockState> BLOCKS_OF_INTEREST = new ObjectOpenHashSet<>(POIT_BY_BLOCKSTATE.keySet());
    private final String name;
    private final Set<BlockState> blockStates;
    private final int maxFreeTickets;
    private final Predicate<PointOfInterestType> predicate;
    private final int validRange;

    private static Set<BlockState> getAllStates(Block blockIn)
    {
        return ImmutableSet.copyOf(blockIn.getStateContainer().getValidStates());
    }

    private PointOfInterestType(String nameIn, Set<BlockState> blockStatesIn, int maxFreeTicketsIn, Predicate<PointOfInterestType> predicate, int validRange)
    {
        this.name = nameIn;
        this.blockStates = ImmutableSet.copyOf(blockStatesIn);
        this.maxFreeTickets = maxFreeTicketsIn;
        this.predicate = predicate;
        this.validRange = validRange;
    }

    private PointOfInterestType(String nameIn, Set<BlockState> blockStatesIn, int maxFreeTicketsIn, int validRange)
    {
        this.name = nameIn;
        this.blockStates = ImmutableSet.copyOf(blockStatesIn);
        this.maxFreeTickets = maxFreeTicketsIn;
        this.predicate = (type) ->
        {
            return type == this;
        };
        this.validRange = validRange;
    }

    public int getMaxFreeTickets()
    {
        return this.maxFreeTickets;
    }

    public Predicate<PointOfInterestType> getPredicate()
    {
        return this.predicate;
    }

    public int getValidRange()
    {
        return this.validRange;
    }

    public String toString()
    {
        return this.name;
    }

    private static PointOfInterestType register(String key, Set<BlockState> blockStates, int maxFreeTickets, int validRange)
    {
        return registerBlockStates(Registry.register(Registry.POINT_OF_INTEREST_TYPE, new ResourceLocation(key), new PointOfInterestType(key, blockStates, maxFreeTickets, validRange)));
    }

    private static PointOfInterestType register(String key, Set<BlockState> blockStates, int maxFreeTickets, Predicate<PointOfInterestType> predicate, int validRange)
    {
        return registerBlockStates(Registry.register(Registry.POINT_OF_INTEREST_TYPE, new ResourceLocation(key), new PointOfInterestType(key, blockStates, maxFreeTickets, predicate, validRange)));
    }

    private static PointOfInterestType registerBlockStates(PointOfInterestType poit)
    {
        poit.blockStates.forEach((state) ->
        {
            PointOfInterestType pointofinteresttype = POIT_BY_BLOCKSTATE.put(state, poit);

            if (pointofinteresttype != null)
            {
                throw(IllegalStateException)Util.pauseDevMode(new IllegalStateException(String.format("%s is defined in too many tags", state)));
            }
        });
        return poit;
    }

    public static Optional<PointOfInterestType> forState(BlockState state)
    {
        return Optional.ofNullable(POIT_BY_BLOCKSTATE.get(state));
    }
}
