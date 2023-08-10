package net.minecraft.world.gen.feature.template;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

public class RuleEntry
{
    public static final Codec<RuleEntry> field_237108_a_ = RecordCodecBuilder.create((p_237111_0_) ->
    {
        return p_237111_0_.group(RuleTest.field_237127_c_.fieldOf("input_predicate").forGetter((p_237116_0_) -> {
            return p_237116_0_.inputPredicate;
        }), RuleTest.field_237127_c_.fieldOf("location_predicate").forGetter((p_237115_0_) -> {
            return p_237115_0_.locationPredicate;
        }), PosRuleTest.field_237102_c_.optionalFieldOf("position_predicate", AlwaysTrueTest.field_237100_b_).forGetter((p_237114_0_) -> {
            return p_237114_0_.field_237109_d_;
        }), BlockState.CODEC.fieldOf("output_state").forGetter((p_237113_0_) -> {
            return p_237113_0_.outputState;
        }), CompoundNBT.CODEC.optionalFieldOf("output_nbt").forGetter((p_237112_0_) -> {
            return Optional.ofNullable(p_237112_0_.outputNbt);
        })).apply(p_237111_0_, RuleEntry::new);
    });
    private final RuleTest inputPredicate;
    private final RuleTest locationPredicate;
    private final PosRuleTest field_237109_d_;
    private final BlockState outputState;
    @Nullable
    private final CompoundNBT outputNbt;

    public RuleEntry(RuleTest inputPredicate, RuleTest locationPredicate, BlockState outputState)
    {
        this(inputPredicate, locationPredicate, AlwaysTrueTest.field_237100_b_, outputState, Optional.empty());
    }

    public RuleEntry(RuleTest p_i232117_1_, RuleTest p_i232117_2_, PosRuleTest p_i232117_3_, BlockState p_i232117_4_)
    {
        this(p_i232117_1_, p_i232117_2_, p_i232117_3_, p_i232117_4_, Optional.empty());
    }

    public RuleEntry(RuleTest p_i232118_1_, RuleTest p_i232118_2_, PosRuleTest p_i232118_3_, BlockState p_i232118_4_, Optional<CompoundNBT> p_i232118_5_)
    {
        this.inputPredicate = p_i232118_1_;
        this.locationPredicate = p_i232118_2_;
        this.field_237109_d_ = p_i232118_3_;
        this.outputState = p_i232118_4_;
        this.outputNbt = p_i232118_5_.orElse((CompoundNBT)null);
    }

    public boolean func_237110_a_(BlockState p_237110_1_, BlockState p_237110_2_, BlockPos p_237110_3_, BlockPos p_237110_4_, BlockPos p_237110_5_, Random p_237110_6_)
    {
        return this.inputPredicate.test(p_237110_1_, p_237110_6_) && this.locationPredicate.test(p_237110_2_, p_237110_6_) && this.field_237109_d_.func_230385_a_(p_237110_3_, p_237110_4_, p_237110_5_, p_237110_6_);
    }

    public BlockState getOutputState()
    {
        return this.outputState;
    }

    @Nullable
    public CompoundNBT getOutputNbt()
    {
        return this.outputNbt;
    }
}
