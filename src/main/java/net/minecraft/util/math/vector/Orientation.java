package net.minecraft.util.math.vector;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.TriplePermutation;
import net.minecraft.util.Util;
import net.minecraft.world.gen.feature.jigsaw.JigsawOrientation;

public enum Orientation implements IStringSerializable
{
    IDENTITY("identity", TriplePermutation.P123, false, false, false),
    ROT_180_FACE_XY("rot_180_face_xy", TriplePermutation.P123, true, true, false),
    ROT_180_FACE_XZ("rot_180_face_xz", TriplePermutation.P123, true, false, true),
    ROT_180_FACE_YZ("rot_180_face_yz", TriplePermutation.P123, false, true, true),
    ROT_120_NNN("rot_120_nnn", TriplePermutation.P231, false, false, false),
    ROT_120_NNP("rot_120_nnp", TriplePermutation.P312, true, false, true),
    ROT_120_NPN("rot_120_npn", TriplePermutation.P312, false, true, true),
    ROT_120_NPP("rot_120_npp", TriplePermutation.P231, true, false, true),
    ROT_120_PNN("rot_120_pnn", TriplePermutation.P312, true, true, false),
    ROT_120_PNP("rot_120_pnp", TriplePermutation.P231, true, true, false),
    ROT_120_PPN("rot_120_ppn", TriplePermutation.P231, false, true, true),
    ROT_120_PPP("rot_120_ppp", TriplePermutation.P312, false, false, false),
    ROT_180_EDGE_XY_NEG("rot_180_edge_xy_neg", TriplePermutation.P213, true, true, true),
    ROT_180_EDGE_XY_POS("rot_180_edge_xy_pos", TriplePermutation.P213, false, false, true),
    ROT_180_EDGE_XZ_NEG("rot_180_edge_xz_neg", TriplePermutation.P321, true, true, true),
    ROT_180_EDGE_XZ_POS("rot_180_edge_xz_pos", TriplePermutation.P321, false, true, false),
    ROT_180_EDGE_YZ_NEG("rot_180_edge_yz_neg", TriplePermutation.P132, true, true, true),
    ROT_180_EDGE_YZ_POS("rot_180_edge_yz_pos", TriplePermutation.P132, true, false, false),
    ROT_90_X_NEG("rot_90_x_neg", TriplePermutation.P132, false, false, true),
    ROT_90_X_POS("rot_90_x_pos", TriplePermutation.P132, false, true, false),
    ROT_90_Y_NEG("rot_90_y_neg", TriplePermutation.P321, true, false, false),
    ROT_90_Y_POS("rot_90_y_pos", TriplePermutation.P321, false, false, true),
    ROT_90_Z_NEG("rot_90_z_neg", TriplePermutation.P213, false, true, false),
    ROT_90_Z_POS("rot_90_z_pos", TriplePermutation.P213, true, false, false),
    INVERSION("inversion", TriplePermutation.P123, true, true, true),
    INVERT_X("invert_x", TriplePermutation.P123, true, false, false),
    INVERT_Y("invert_y", TriplePermutation.P123, false, true, false),
    INVERT_Z("invert_z", TriplePermutation.P123, false, false, true),
    ROT_60_REF_NNN("rot_60_ref_nnn", TriplePermutation.P312, true, true, true),
    ROT_60_REF_NNP("rot_60_ref_nnp", TriplePermutation.P231, true, false, false),
    ROT_60_REF_NPN("rot_60_ref_npn", TriplePermutation.P231, false, false, true),
    ROT_60_REF_NPP("rot_60_ref_npp", TriplePermutation.P312, false, false, true),
    ROT_60_REF_PNN("rot_60_ref_pnn", TriplePermutation.P231, false, true, false),
    ROT_60_REF_PNP("rot_60_ref_pnp", TriplePermutation.P312, true, false, false),
    ROT_60_REF_PPN("rot_60_ref_ppn", TriplePermutation.P312, false, true, false),
    ROT_60_REF_PPP("rot_60_ref_ppp", TriplePermutation.P231, true, true, true),
    SWAP_XY("swap_xy", TriplePermutation.P213, false, false, false),
    SWAP_YZ("swap_yz", TriplePermutation.P132, false, false, false),
    SWAP_XZ("swap_xz", TriplePermutation.P321, false, false, false),
    SWAP_NEG_XY("swap_neg_xy", TriplePermutation.P213, true, true, false),
    SWAP_NEG_YZ("swap_neg_yz", TriplePermutation.P132, false, true, true),
    SWAP_NEG_XZ("swap_neg_xz", TriplePermutation.P321, true, false, true),
    ROT_90_REF_X_NEG("rot_90_ref_x_neg", TriplePermutation.P132, true, false, true),
    ROT_90_REF_X_POS("rot_90_ref_x_pos", TriplePermutation.P132, true, true, false),
    ROT_90_REF_Y_NEG("rot_90_ref_y_neg", TriplePermutation.P321, true, true, false),
    ROT_90_REF_Y_POS("rot_90_ref_y_pos", TriplePermutation.P321, false, true, true),
    ROT_90_REF_Z_NEG("rot_90_ref_z_neg", TriplePermutation.P213, false, true, true),
    ROT_90_REF_Z_POS("rot_90_ref_z_pos", TriplePermutation.P213, true, false, true);

    private final Matrix3f field_235517_W_;
    private final String field_235518_X_;
    @Nullable
    private Map<Direction, Direction> field_235519_Y_;
    private final boolean field_235520_Z_;
    private final boolean field_235521_aa_;
    private final boolean field_235522_ab_;
    private final TriplePermutation field_235523_ac_;
    private static final Orientation[][] field_235524_ad_ = Util.make(new Orientation[values().length][values().length], (p_235532_0_) -> {
        Map<Pair<TriplePermutation, BooleanList>, Orientation> map = Arrays.stream(values()).collect(Collectors.toMap((p_235536_0_) -> {
            return Pair.of(p_235536_0_.field_235523_ac_, p_235536_0_.func_235533_b_());
        }, (p_235535_0_) -> {
            return p_235535_0_;
        }));

        for (Orientation orientation : values())
        {
            for (Orientation orientation1 : values())
            {
                BooleanList booleanlist = orientation.func_235533_b_();
                BooleanList booleanlist1 = orientation1.func_235533_b_();
                TriplePermutation triplepermutation = orientation1.field_235523_ac_.func_239188_a_(orientation.field_235523_ac_);
                BooleanArrayList booleanarraylist = new BooleanArrayList(3);

                for (int i = 0; i < 3; ++i)
                {
                    booleanarraylist.add(booleanlist.getBoolean(i) ^ booleanlist1.getBoolean(orientation.field_235523_ac_.func_239187_a_(i)));
                }

                p_235532_0_[orientation.ordinal()][orientation1.ordinal()] = map.get(Pair.of(triplepermutation, booleanarraylist));
            }
        }
    });
    private static final Orientation[] field_235525_ae_ = Arrays.stream(values()).map((p_235534_0_) -> {
        return Arrays.stream(values()).filter((p_235528_1_) -> {
            return p_235534_0_.func_235527_a_(p_235528_1_) == IDENTITY;
        }).findAny().get();
    }).toArray((p_235526_0_) -> {
        return new Orientation[p_235526_0_];
    });

    private Orientation(String p_i231784_3_, TriplePermutation p_i231784_4_, boolean p_i231784_5_, boolean p_i231784_6_, boolean p_i231784_7_)
    {
        this.field_235518_X_ = p_i231784_3_;
        this.field_235520_Z_ = p_i231784_5_;
        this.field_235521_aa_ = p_i231784_6_;
        this.field_235522_ab_ = p_i231784_7_;
        this.field_235523_ac_ = p_i231784_4_;
        this.field_235517_W_ = new Matrix3f();
        this.field_235517_W_.m00 = p_i231784_5_ ? -1.0F : 1.0F;
        this.field_235517_W_.m11 = p_i231784_6_ ? -1.0F : 1.0F;
        this.field_235517_W_.m22 = p_i231784_7_ ? -1.0F : 1.0F;
        this.field_235517_W_.mul(p_i231784_4_.func_239186_a_());
    }

    private BooleanList func_235533_b_()
    {
        return new BooleanArrayList(new boolean[] {this.field_235520_Z_, this.field_235521_aa_, this.field_235522_ab_});
    }

    public Orientation func_235527_a_(Orientation p_235527_1_)
    {
        return field_235524_ad_[this.ordinal()][p_235527_1_.ordinal()];
    }

    public String toString()
    {
        return this.field_235518_X_;
    }

    public String getString()
    {
        return this.field_235518_X_;
    }

    public Direction func_235530_a_(Direction p_235530_1_)
    {
        if (this.field_235519_Y_ == null)
        {
            this.field_235519_Y_ = Maps.newEnumMap(Direction.class);

            for (Direction direction : Direction.values())
            {
                Direction.Axis direction$axis = direction.getAxis();
                Direction.AxisDirection direction$axisdirection = direction.getAxisDirection();
                Direction.Axis direction$axis1 = Direction.Axis.values()[this.field_235523_ac_.func_239187_a_(direction$axis.ordinal())];
                Direction.AxisDirection direction$axisdirection1 = this.isOnAxis(direction$axis1) ? direction$axisdirection.inverted() : direction$axisdirection;
                Direction direction1 = Direction.getFacingFromAxisDirection(direction$axis1, direction$axisdirection1);
                this.field_235519_Y_.put(direction, direction1);
            }
        }

        return this.field_235519_Y_.get(p_235530_1_);
    }

    public boolean isOnAxis(Direction.Axis axis)
    {
        switch (axis)
        {
            case X:
                return this.field_235520_Z_;

            case Y:
                return this.field_235521_aa_;

            case Z:
            default:
                return this.field_235522_ab_;
        }
    }

    public JigsawOrientation func_235531_a_(JigsawOrientation p_235531_1_)
    {
        return JigsawOrientation.func_239641_a_(this.func_235530_a_(p_235531_1_.func_239642_b_()), this.func_235530_a_(p_235531_1_.func_239644_c_()));
    }
}
