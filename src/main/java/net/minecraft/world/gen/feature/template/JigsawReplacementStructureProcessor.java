package net.minecraft.world.gen.feature.template;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class JigsawReplacementStructureProcessor extends StructureProcessor
{
    public static final Codec<JigsawReplacementStructureProcessor> field_237085_a_;
    public static final JigsawReplacementStructureProcessor INSTANCE = new JigsawReplacementStructureProcessor();

    private JigsawReplacementStructureProcessor()
    {
    }

    @Nullable
    public Template.BlockInfo func_230386_a_(IWorldReader p_230386_1_, BlockPos p_230386_2_, BlockPos p_230386_3_, Template.BlockInfo p_230386_4_, Template.BlockInfo p_230386_5_, PlacementSettings p_230386_6_)
    {
        BlockState blockstate = p_230386_5_.state;

        if (blockstate.isIn(Blocks.JIGSAW))
        {
            String s = p_230386_5_.nbt.getString("final_state");
            BlockStateParser blockstateparser = new BlockStateParser(new StringReader(s), false);

            try
            {
                blockstateparser.parse(true);
            }
            catch (CommandSyntaxException commandsyntaxexception)
            {
                throw new RuntimeException(commandsyntaxexception);
            }

            return blockstateparser.getState().isIn(Blocks.STRUCTURE_VOID) ? null : new Template.BlockInfo(p_230386_5_.pos, blockstateparser.getState(), (CompoundNBT)null);
        }
        else
        {
            return p_230386_5_;
        }
    }

    protected IStructureProcessorType<?> getType()
    {
        return IStructureProcessorType.JIGSAW_REPLACEMENT;
    }

    static
    {
        field_237085_a_ = Codec.unit(() ->
        {
            return INSTANCE;
        });
    }
}
