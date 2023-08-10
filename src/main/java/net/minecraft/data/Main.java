package net.minecraft.data;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class Main
{
    public static void main(String[] p_main_0_) throws IOException
    {
        OptionParser optionparser = new OptionParser();
        OptionSpec<Void> optionspec = optionparser.accepts("help", "Show the help menu").forHelp();
        OptionSpec<Void> optionspec1 = optionparser.accepts("server", "Include server generators");
        OptionSpec<Void> optionspec2 = optionparser.accepts("client", "Include client generators");
        OptionSpec<Void> optionspec3 = optionparser.accepts("dev", "Include development tools");
        OptionSpec<Void> optionspec4 = optionparser.accepts("reports", "Include data reports");
        OptionSpec<Void> optionspec5 = optionparser.accepts("validate", "Validate inputs");
        OptionSpec<Void> optionspec6 = optionparser.accepts("all", "Include all generators");
        OptionSpec<String> optionspec7 = optionparser.accepts("output", "Output folder").withRequiredArg().defaultsTo("generated");
        OptionSpec<String> optionspec8 = optionparser.accepts("input", "Input folder").withRequiredArg();
        OptionSet optionset = optionparser.parse(p_main_0_);

        if (!optionset.has(optionspec) && optionset.hasOptions())
        {
            Path path = Paths.get(optionspec7.value(optionset));
            boolean flag = optionset.has(optionspec6);
            boolean flag1 = flag || optionset.has(optionspec2);
            boolean flag2 = flag || optionset.has(optionspec1);
            boolean flag3 = flag || optionset.has(optionspec3);
            boolean flag4 = flag || optionset.has(optionspec4);
            boolean flag5 = flag || optionset.has(optionspec5);
            DataGenerator datagenerator = makeGenerator(path, optionset.valuesOf(optionspec8).stream().map((p_200263_0_) ->
            {
                return Paths.get(p_200263_0_);
            }).collect(Collectors.toList()), flag1, flag2, flag3, flag4, flag5);
            datagenerator.run();
        }
        else
        {
            optionparser.printHelpOn(System.out);
        }
    }

    /**
     * Creates a data generator based on the given options
     */
    public static DataGenerator makeGenerator(Path output, Collection<Path> inputs, boolean client, boolean server, boolean dev, boolean reports, boolean validate)
    {
        DataGenerator datagenerator = new DataGenerator(output, inputs);

        if (client || server)
        {
            datagenerator.addProvider((new SNBTToNBTConverter(datagenerator)).addTransformer(new StructureUpdater()));
        }

        if (client)
        {
            datagenerator.addProvider(new BlockStateProvider(datagenerator));
        }

        if (server)
        {
            datagenerator.addProvider(new FluidTagsProvider(datagenerator));
            BlockTagsProvider blocktagsprovider = new BlockTagsProvider(datagenerator);
            datagenerator.addProvider(blocktagsprovider);
            datagenerator.addProvider(new ItemTagsProvider(datagenerator, blocktagsprovider));
            datagenerator.addProvider(new EntityTypeTagsProvider(datagenerator));
            datagenerator.addProvider(new RecipeProvider(datagenerator));
            datagenerator.addProvider(new AdvancementProvider(datagenerator));
            datagenerator.addProvider(new LootTableProvider(datagenerator));
        }

        if (dev)
        {
            datagenerator.addProvider(new NBTToSNBTConverter(datagenerator));
        }

        if (reports)
        {
            datagenerator.addProvider(new BlockListReport(datagenerator));
            datagenerator.addProvider(new RegistryDumpReport(datagenerator));
            datagenerator.addProvider(new CommandsReport(datagenerator));
            datagenerator.addProvider(new BiomeProvider(datagenerator));
        }

        return datagenerator;
    }
}
