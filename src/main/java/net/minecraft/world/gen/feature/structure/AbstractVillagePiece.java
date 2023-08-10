package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Random;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.jigsaw.EmptyJigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.JigsawJunction;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.template.TemplateManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AbstractVillagePiece extends StructurePiece
{
    private static final Logger field_237000_d_ = LogManager.getLogger();
    protected final JigsawPiece jigsawPiece;
    protected BlockPos pos;
    private final int groundLevelDelta;
    protected final Rotation rotation;
    private final List<JigsawJunction> junctions = Lists.newArrayList();
    private final TemplateManager templateManager;

    public AbstractVillagePiece(TemplateManager p_i242036_1_, JigsawPiece p_i242036_2_, BlockPos p_i242036_3_, int p_i242036_4_, Rotation p_i242036_5_, MutableBoundingBox p_i242036_6_)
    {
        super(IStructurePieceType.field_242786_ad, 0);
        this.templateManager = p_i242036_1_;
        this.jigsawPiece = p_i242036_2_;
        this.pos = p_i242036_3_;
        this.groundLevelDelta = p_i242036_4_;
        this.rotation = p_i242036_5_;
        this.boundingBox = p_i242036_6_;
    }

    public AbstractVillagePiece(TemplateManager p_i242037_1_, CompoundNBT p_i242037_2_)
    {
        super(IStructurePieceType.field_242786_ad, p_i242037_2_);
        this.templateManager = p_i242037_1_;
        this.pos = new BlockPos(p_i242037_2_.getInt("PosX"), p_i242037_2_.getInt("PosY"), p_i242037_2_.getInt("PosZ"));
        this.groundLevelDelta = p_i242037_2_.getInt("ground_level_delta");
        this.jigsawPiece = JigsawPiece.field_236847_e_.parse(NBTDynamicOps.INSTANCE, p_i242037_2_.getCompound("pool_element")).resultOrPartial(field_237000_d_::error).orElse(EmptyJigsawPiece.INSTANCE);
        this.rotation = Rotation.valueOf(p_i242037_2_.getString("rotation"));
        this.boundingBox = this.jigsawPiece.getBoundingBox(p_i242037_1_, this.pos, this.rotation);
        ListNBT listnbt = p_i242037_2_.getList("junctions", 10);
        this.junctions.clear();
        listnbt.forEach((p_214827_1_) ->
        {
            this.junctions.add(JigsawJunction.func_236819_a_(new Dynamic<>(NBTDynamicOps.INSTANCE, p_214827_1_)));
        });
    }

    /**
     * (abstract) Helper method to read subclass data from NBT
     */
    protected void readAdditional(CompoundNBT tagCompound)
    {
        tagCompound.putInt("PosX", this.pos.getX());
        tagCompound.putInt("PosY", this.pos.getY());
        tagCompound.putInt("PosZ", this.pos.getZ());
        tagCompound.putInt("ground_level_delta", this.groundLevelDelta);
        JigsawPiece.field_236847_e_.encodeStart(NBTDynamicOps.INSTANCE, this.jigsawPiece).resultOrPartial(field_237000_d_::error).ifPresent((p_237002_1_) ->
        {
            tagCompound.put("pool_element", p_237002_1_);
        });
        tagCompound.putString("rotation", this.rotation.name());
        ListNBT listnbt = new ListNBT();

        for (JigsawJunction jigsawjunction : this.junctions)
        {
            listnbt.add(jigsawjunction.func_236820_a_(NBTDynamicOps.INSTANCE).getValue());
        }

        tagCompound.put("junctions", listnbt);
    }

    public boolean func_230383_a_(ISeedReader p_230383_1_, StructureManager p_230383_2_, ChunkGenerator p_230383_3_, Random p_230383_4_, MutableBoundingBox p_230383_5_, ChunkPos p_230383_6_, BlockPos p_230383_7_)
    {
        return this.func_237001_a_(p_230383_1_, p_230383_2_, p_230383_3_, p_230383_4_, p_230383_5_, p_230383_7_, false);
    }

    public boolean func_237001_a_(ISeedReader p_237001_1_, StructureManager p_237001_2_, ChunkGenerator p_237001_3_, Random p_237001_4_, MutableBoundingBox p_237001_5_, BlockPos p_237001_6_, boolean p_237001_7_)
    {
        return this.jigsawPiece.func_230378_a_(this.templateManager, p_237001_1_, p_237001_2_, p_237001_3_, this.pos, p_237001_6_, this.rotation, p_237001_5_, p_237001_4_, p_237001_7_);
    }

    public void offset(int x, int y, int z)
    {
        super.offset(x, y, z);
        this.pos = this.pos.add(x, y, z);
    }

    public Rotation getRotation()
    {
        return this.rotation;
    }

    public String toString()
    {
        return String.format("<%s | %s | %s | %s>", this.getClass().getSimpleName(), this.pos, this.rotation, this.jigsawPiece);
    }

    public JigsawPiece getJigsawPiece()
    {
        return this.jigsawPiece;
    }

    public BlockPos getPos()
    {
        return this.pos;
    }

    public int getGroundLevelDelta()
    {
        return this.groundLevelDelta;
    }

    public void addJunction(JigsawJunction junction)
    {
        this.junctions.add(junction);
    }

    public List<JigsawJunction> getJunctions()
    {
        return this.junctions;
    }
}
