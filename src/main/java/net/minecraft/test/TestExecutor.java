package net.minecraft.test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestExecutor
{
    private static final Logger field_229466_a_ = LogManager.getLogger();
    private final BlockPos field_229467_b_;
    private final ServerWorld field_229468_c_;
    private final TestCollection field_229469_d_;
    private final int field_240536_e_;
    private final List<TestTracker> field_229470_e_ = Lists.newArrayList();
    private final Map<TestTracker, BlockPos> field_240537_g_ = Maps.newHashMap();
    private final List<Pair<TestBatch, Collection<TestTracker>>> field_229471_f_ = Lists.newArrayList();
    private TestResultList field_229472_g_;
    private int field_229473_h_ = 0;
    private BlockPos.Mutable field_229474_i_;

    public TestExecutor(Collection<TestBatch> p_i232555_1_, BlockPos p_i232555_2_, Rotation p_i232555_3_, ServerWorld p_i232555_4_, TestCollection p_i232555_5_, int p_i232555_6_)
    {
        this.field_229474_i_ = p_i232555_2_.toMutable();
        this.field_229467_b_ = p_i232555_2_;
        this.field_229468_c_ = p_i232555_4_;
        this.field_229469_d_ = p_i232555_5_;
        this.field_240536_e_ = p_i232555_6_;
        p_i232555_1_.forEach((p_240539_3_) ->
        {
            Collection<TestTracker> collection = Lists.newArrayList();

            for (TestFunctionInfo testfunctioninfo : p_240539_3_.func_229465_b_())
            {
                TestTracker testtracker = new TestTracker(testfunctioninfo, p_i232555_3_, p_i232555_4_);
                collection.add(testtracker);
                this.field_229470_e_.add(testtracker);
            }

            this.field_229471_f_.add(Pair.of(p_240539_3_, collection));
        });
    }

    public List<TestTracker> func_229476_a_()
    {
        return this.field_229470_e_;
    }

    public void func_229482_b_()
    {
        this.func_229477_a_(0);
    }

    private void func_229477_a_(int p_229477_1_)
    {
        this.field_229473_h_ = p_229477_1_;
        this.field_229472_g_ = new TestResultList();

        if (p_229477_1_ < this.field_229471_f_.size())
        {
            Pair<TestBatch, Collection<TestTracker>> pair = this.field_229471_f_.get(this.field_229473_h_);
            TestBatch testbatch = pair.getFirst();
            Collection<TestTracker> collection = pair.getSecond();
            this.func_229480_a_(collection);
            testbatch.func_229464_a_(this.field_229468_c_);
            String s = testbatch.func_229463_a_();
            field_229466_a_.info("Running test batch '" + s + "' (" + collection.size() + " tests)...");
            collection.forEach((p_229483_1_) ->
            {
                this.field_229472_g_.func_229579_a_(p_229483_1_);
                this.field_229472_g_.func_240558_a_(new ITestCallback()
                {
                    public void func_225644_a_(TestTracker p_225644_1_)
                    {
                    }
                    public void func_225645_c_(TestTracker p_225645_1_)
                    {
                        TestExecutor.this.func_229479_a_(p_225645_1_);
                    }
                });
                BlockPos blockpos = this.field_240537_g_.get(p_229483_1_);
                TestUtils.func_240553_a_(p_229483_1_, blockpos, this.field_229469_d_);
            });
        }
    }

    private void func_229479_a_(TestTracker p_229479_1_)
    {
        if (this.field_229472_g_.func_229588_i_())
        {
            this.func_229477_a_(this.field_229473_h_ + 1);
        }
    }

    private void func_229480_a_(Collection<TestTracker> p_229480_1_)
    {
        int i = 0;
        AxisAlignedBB axisalignedbb = new AxisAlignedBB(this.field_229474_i_);

        for (TestTracker testtracker : p_229480_1_)
        {
            BlockPos blockpos = new BlockPos(this.field_229474_i_);
            StructureBlockTileEntity structureblocktileentity = StructureHelper.func_240565_a_(testtracker.func_229522_s_(), blockpos, testtracker.func_240545_t_(), 2, this.field_229468_c_, true);
            AxisAlignedBB axisalignedbb1 = StructureHelper.func_229594_a_(structureblocktileentity);
            testtracker.func_229503_a_(structureblocktileentity.getPos());
            this.field_240537_g_.put(testtracker, new BlockPos(this.field_229474_i_));
            axisalignedbb = axisalignedbb.union(axisalignedbb1);
            this.field_229474_i_.move((int)axisalignedbb1.getXSize() + 5, 0, 0);

            if (i++ % this.field_240536_e_ == this.field_240536_e_ - 1)
            {
                this.field_229474_i_.move(0, 0, (int)axisalignedbb.getZSize() + 6);
                this.field_229474_i_.setX(this.field_229467_b_.getX());
                axisalignedbb = new AxisAlignedBB(this.field_229474_i_);
            }
        }
    }
}
